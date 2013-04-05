/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is responsible for providing thread-safe functionality to the data
 * stored in a flat-file database system. Basic functionality for manipulating
 * the data such as retrieval, updating, deleting, searching, creating and
 * saving are provided.
 * 
 * <p>
 * The information from the database file is cached and will be written back to
 * the file when {@link #save() save} is invoked. It is important that the
 * database file is not modified while an instance of this class still has a
 * reference to it; otherwise, the cached data and the data in the file may
 * become out of synch or worse, corrupted.
 * 
 * 
 * @author rsmall
 */
class RecordAccess {

	/** Number of bytes needed to identify the deletion status of a record. */
	private static final int DELETED_LENGTH = 1;

	/** Reference to the physical file on disk containing the records. */
	private final File database;

	/** Byte position in the database file where the data begins. */
	private final long offset;

	/**
	 * Specifies the format of the fields for each record within the database.
	 * Array length indicates the number of fields. Each element identifies the
	 * maximum number of bytes permitted for the field corresponding to the same
	 * index.
	 */
	private final int[] recordFormat;

	/** Maps the record number to the appropriate record. */
	private final Map<Integer, Record> records = new HashMap<Integer, Record>();

	/** Limits access to {@code records}. */
	private final ReadWriteLock recordsLock = new ReentrantReadWriteLock();

	/**
	 * Data structure to represent a record within the {@code DatabaseFile}.
	 */
	private class Record {

		/** Indicates if this record has been deleted. */
		private boolean isDeleted;

		/** Each element identifies a field within the record. */
		private String[] data;

		/**
		 * Constructs a new {@code Record} using the specified {@code fields}.
		 * By default, the record will not be marked as deleted.
		 * 
		 * @param data
		 *            The fields that will be assigned to the record. Each
		 *            element in the array will identify a unique field
		 *            associated with the record as determined by the database.
		 */
		private Record(final String[] data) {
			this(data, false);
		}

		/**
		 * Constructs a new {@code Record} using the specified {@code fields}.
		 * 
		 * @param data
		 *            The fields that will be assigned to the record. Each
		 *            element in the array will identify a unique field
		 *            associated with the record as determined by the database.
		 * 
		 * @param isDeleted
		 *            Deletion status of the record. {@code true} if the record
		 *            is deleted; {@code false} otherwise.
		 */
		private Record(final String[] data, final boolean isDeleted) {
			this.data = data;
			this.isDeleted = isDeleted;
		}
	}

	/**
	 * Creates a new {@code RecordAccess} object using the data stored in the
	 * existing {@code database}. It is expected that {@code database} will not
	 * be accessed while an instance of this class still has a reference to it;
	 * otherwise, the data provided and the data in the file may become out of
	 * sync or worse, corrupted.
	 * 
	 * <p>
	 * The structure of {@code database} is expected to start with a header
	 * followed by the data section. The position that marks the end of the
	 * header and the start of the records is denoted by {@code offset}. The
	 * header portion will be ignored. The data section is expected to consist
	 * of repeating records.
	 * 
	 * <p>
	 * Each record is expected to be preceeded with a single byte indicating if
	 * the record is valid (byte will be a 0) or deleted (byte will be a 1). The
	 * rest of the record is expected to follow the format specified by
	 * {@code recordFormat}. This format is interpreted as follows:
	 * 
	 * <ul>
	 * <li>{@code recordFormat.length} - Number of fields for each record.</li>
	 * <li>{@code recordFormat[n]} - Size, in bytes, allowed for field n.</li>
	 * </ul>
	 * 
	 * @param database
	 *            Reference to the physical file on disk containing the records.
	 * 
	 * @param offset
	 *            Byte position in the database file where the data begins.
	 * 
	 * @param recordFormat
	 *            Specifies the format of the fields for each record within the
	 *            database. Array length indicates the number of fields. Each
	 *            element identifies the maximum number of bytes permitted for
	 *            the field corresponding to the same index.
	 * 
	 * @throws DBIOException
	 *             If the given file does not denote an existing, writable
	 *             regular file or if some other error occurs while opening the
	 *             file or if an I/O error occurs while reading the file.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code database} is {@code null}, {@code recordFormat} is
	 *             {@code null}, or {@code offset} is less than zero.
	 */
	RecordAccess(final File database, final long offset,
			final int[] recordFormat) throws DBIOException {

		if (database == null) {
			throw new IllegalArgumentException("database cannot be null");
		}

		if (recordFormat == null) {
			throw new IllegalArgumentException("recordFormat cannot be null");
		}

		if (offset < 0) {
			throw new IllegalArgumentException("offset cannot be less than 0");
		}

		this.database = database;
		this.offset = offset;
		this.recordFormat = recordFormat;

		loadRecordsFromDatabase();
	}

	/**
	 * Returns the maximum size, in bytes, of a {@code Record}. This value
	 * includes the record's deletion status along with all of the fields.
	 * 
	 * @return Maximum size, in bytes, of a {@code Record}.
	 * 
	 * @throws IllegalStateException
	 *             If the {@code format} has not been set.
	 */
	private int getRecordSize() {
		int total = DELETED_LENGTH;
		for (final int fieldLength : recordFormat) {
			total += fieldLength;
		}
		return total;
	}

	/**
	 * Constructs a new {@code Record} by parsing the specified {@code byte}
	 * array. This constructor is intended to be used when read a record from
	 * the database.
	 * 
	 * @param data
	 *            Raw data extracted from the database file that will be used
	 *            for creating this {@code Record}. This data must include the
	 *            deletion status and all associated fields meaning that its
	 *            size must be equal to the record's {@link #getRecordSize()
	 *            size}.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code data.length} does not equal {@code getRecordSize}.
	 */
	private Record toRecord(final byte[] data) {

		/*
		 * If the provided byte array does not match the size of the record then
		 * we need to throw an exception, otherwise issues will ensue when we
		 * start to parse the data.
		 */
		if (data.length != getRecordSize()) {
			throw new IllegalArgumentException("invalid size");
		}

		/**
		 * Incrementally converts a {@code byte} array into Strings.
		 */
		class ByteReader {

			/** Current position within the {@code byte} array. */
			private int byteOffset = 0;

			/**
			 * Converts a portion of {@code data} into a String and returns it.
			 * Each call extracts the requested number of bytes, specified by
			 * {@code length}, starting from the last byte retrieved from the
			 * previous invocation.
			 * 
			 * @param length
			 *            Number of bytes to extract from {@code data}.
			 * 
			 * @return {@code String} that was extracted from {@code data}.
			 */
			String read(final int length) {
				final String str = new String(data, byteOffset, length);
				byteOffset += length;
				return str.trim();
			}
		}

		final ByteReader reader = new ByteReader();
		final int numberOfFields = recordFormat.length;

		final String[] fields = new String[numberOfFields];

		/*
		 * Read the deletion status independently since it is not considered a
		 * fields but rather a status of the record itself.
		 */
		final boolean isDeleted =
				Boolean.parseBoolean(reader.read(DELETED_LENGTH));

		/* Extracting each field from {@code data}. */
		for (int i = 0; i < numberOfFields; i++) {
			fields[i] = reader.read(recordFormat[i]);
		}

		return new Record(fields, isDeleted);
	}

	/**
	 * Returns a {@code RandomAccessFile} that references the database with
	 * read/write permissions.
	 * 
	 * @return A {@code RandomAccessFile} that references the database.
	 * 
	 * @throws DBIOException
	 *             If the given file does not denote an existing, writable
	 *             regular file or if some other error occurs while opening the
	 *             file.
	 */
	private RandomAccessFile getDatabase() throws DBIOException {

		try {

			/*
			 * We want to avoid creating a new databases since we should be
			 * using an existing one. This means that if the path given to us
			 * doesn't provide us with an existing database we should throw an
			 * exception.
			 */
			if (!database.exists()) {
				throw new DBIOException("file does not exist");
			}

			return new RandomAccessFile(database, "rw");

		} catch (final FileNotFoundException ex) {
			throw new DBIOException(ex.getMessage());
		}
	}

	/**
	 * Extracts the records from the {@code databaseFile} and places them into
	 * {@code records}; mapping the record number to the room.
	 * 
	 * @throws DBIOException
	 *             If the given file does not denote an existing, writable
	 *             regular file or if some other error occurs while opening the
	 *             file or if an I/O error occurs while reading the file.
	 */
	private void loadRecordsFromDatabase() throws DBIOException {
		recordsLock.writeLock().lock();
		try {

			/* Ignore the header and jump straight to the records. */
			final RandomAccessFile databaseFile = getDatabase();
			databaseFile.seek(offset);

			int recordNumber = 0;
			while (databaseFile.getFilePointer() < databaseFile.length()) {

				final byte[] input = new byte[getRecordSize()];
				databaseFile.readFully(input);

				final Record record = toRecord(input);
				records.put(recordNumber, record);

				recordNumber++;
			}

			databaseFile.close();

		} catch (final IOException ex) {
			throw new DBIOException(ex.getMessage());

		} finally {
			recordsLock.writeLock().unlock();
		}
	}

	/**
	 * Converts the record into a {@code byte} array. Each field will be padded
	 * with zeros on the right side to ensure the appropriate length for each
	 * field as specified by {@code recordFormat}.
	 * 
	 * @return The specified record as a {@code byte} array.
	 */
	private byte[] toByteArray(final Record record) {
		final byte[] recordArray = new byte[getRecordSize()];

		/* Write the deletion status. */
		recordArray[0] = (byte) (record.isDeleted ? 1 : 0);
		int position = DELETED_LENGTH;

		/* Copy each field to the recordArray. */
		for (int index = 0; index < record.data.length; index++) {
			final int fieldLength = recordFormat[index];
			final byte[] fieldData = record.data[index].getBytes();
			final byte[] fieldArray = Arrays.copyOf(fieldData, fieldLength);

			System.arraycopy(fieldArray, 0, recordArray, position, fieldLength);
			position += fieldLength;
		}

		return recordArray;
	}

	/**
	 * Returns the fields belonging to the record specified by {@code recNo}.
	 * Each element in the array will identify a unique field associated with
	 * the record as determined by the database.
	 * 
	 * @param recNo
	 *            Record number of the record to retrieve.
	 * 
	 * @return Fields belonging to the record specified by {@code recNo}.
	 * 
	 * @throws RecordNotFoundException
	 *             If the record specified by {@code recNo} does not exist.
	 */
	String[] read(final int recNo) throws RecordNotFoundException {
		recordsLock.readLock().lock();
		try {
			final Record record = records.get(recNo);
			if ((record == null) || record.isDeleted) {
				throw new RecordNotFoundException();
			}
			return record.data;
		} finally {
			recordsLock.readLock().unlock();
		}
	}

	/**
	 * Updates the fields belonging to the record specified by {@code recNo}.
	 * Each element in the array will identify a unique field associated with
	 * the record as determined by the database.
	 * 
	 * @param recNo
	 *            Record number of the record to update.
	 * 
	 * @param data
	 *            The fields that will be assigned to the record. Each element
	 *            in the array will identify a unique field associated with the
	 *            record as determined by the database.
	 */
	void update(final int recNo, final String[] data) {
		verifyFields(data);
		recordsLock.writeLock().lock();
		try {
			final Record record = records.get(recNo);
			record.data = data;
		} finally {
			recordsLock.writeLock().unlock();
		}
	}

	/**
	 * Deletes the record specified by {@code recNo}.
	 * 
	 * @param recNo
	 *            Record number of the record to delete.
	 */
	void delete(final int recNo) {
		recordsLock.writeLock().lock();
		try {
			final Record record = records.get(recNo);
			record.isDeleted = true;
		} finally {
			recordsLock.writeLock().unlock();
		}
	}

	/**
	 * Returns an array of the record numbers that match {@code criteria}.
	 * 
	 * <p>
	 * A record is considered a match if the record's field identified by
	 * {@code n} starts with {@code criteria[n]} for every non-null value within
	 * {@code criteria}.
	 * 
	 * <p>
	 * For example, if {@code criteria[0]} equals {@code John},
	 * {@code criteria[1]} equals {@code Doe} and {@code criteria[2]} is
	 * {@code null}, then a record would be considered a match if field
	 * {@code 0} equals {@code Johnny} and field {@code 1} equals {@code Doe},
	 * but not if field {@code 0} equals {@code Sean} and field {@code 1} equals
	 * {@code Doe}. Since {@code criteria[2]} is {@code null}, it doesn't matter
	 * what field {@code 2} is.
	 * 
	 * <p>
	 * This method is case sensitive.
	 * 
	 * <p>
	 * All records will be returned if every element in {@code criteria} is
	 * {@code null}.
	 * 
	 * @param criteria
	 *            Criteria to use when searching for records.
	 * 
	 * @return An array of the record numbers that match {@code criteria}.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code criteria} is {@code null} or does not have the
	 *             correct number of elements.
	 */
	int[] find(final String[] criteria) {

		if (criteria == null) {
			throw new IllegalArgumentException("criteria cannot be null");
		}

		if (criteria.length != recordFormat.length) {
			throw new IllegalArgumentException("invalid number of elements");
		}

		recordsLock.readLock().lock();
		try {

			final ArrayList<Integer> matchingRecords = new ArrayList<Integer>();
			for (final int recordNumber : records.keySet()) {
				final String[] fields = records.get(recordNumber).data;

				boolean isMatch = true;
				for (int i = 0; i < fields.length; i++) {
					final String field = fields[i];
					final String criterion = criteria[i];

					if (criterion == null) {
						/*
						 * A null value is considered a wildcard. If we have a
						 * wildcard, then we can just move on to the next field.
						 */
						continue;

					} else if (field.startsWith(criterion)) {
						/*
						 * If the fields begins with the associated criteria for
						 * that field, then we can just move on to the next
						 * field.
						 */
						continue;

					} else {
						/*
						 * At this point, the field does not meet the specific
						 * criteria, so we need to mark it as not matching and
						 * leave the loop since it doesn't matter if the rest of
						 * the fields match.
						 */
						isMatch = false;
						break;
					}
				}
				if (isMatch) {
					/* This record meets all of the specified criteria. */
					matchingRecords.add(recordNumber);
				}
			}

			/* Convert the list of matching record numbers to an array. */
			final int[] a = new int[matchingRecords.size()];
			for (int i = 0; i < matchingRecords.size(); i++) {
				a[i] = matchingRecords.get(i);
			}

			return a;

		} finally {
			recordsLock.readLock().unlock();
		}
	}

	/**
	 * Ensures that {@code fields} adheres to the proper format.
	 * 
	 * @param fields
	 *            The array of fields to verify.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code fields} is {@code null}, does not have the correct
	 *             number of fields, contains a {@code null} field or has a
	 *             field that exceeds the size its maximum character limit.
	 */
	private void verifyFields(final String[] fields) {

		if (fields == null) {
			throw new IllegalArgumentException("fields cannot be null");
		}

		if (fields.length != recordFormat.length) {
			throw new IllegalArgumentException("invalid number of fields");
		}

		for (int i = 0; i < fields.length; i++) {
			final String field = fields[i];

			if (field == null) {
				throw new IllegalArgumentException("field cannot be null");
			}

			if (field.length() > recordFormat[i]) {
				final String err = "fields[" + i + "] exceeds character limit";
				throw new IllegalArgumentException(err);
			}
		}
	}

	/**
	 * Creates a new record and sets {@code data} as its fields.
	 * 
	 * @param data
	 *            The fields that will be assigned to the record. Each element
	 *            in the array will identify a unique field associated with the
	 *            record as determined by the database.
	 * 
	 * @return Record number of the new record.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code fields} is {@code null}, does not have the correct
	 *             number of fields, contains a {@code null} field or has a
	 *             field that exceeds the size its maximum character limit.
	 */
	int create(final String[] data) {
		verifyFields(data);
		recordsLock.writeLock().lock();
		try {
			final int recordNumber = records.size() + 1;
			final Record record = new Record(data);
			records.put(recordNumber, record);
			return recordNumber;

		} finally {
			recordsLock.writeLock().unlock();
		}
	}

	/**
	 * Saves the changes made to the records.
	 * 
	 * <p>
	 * This method could be invoked every time a record is modified, after a
	 * specific number of records have been modified, after an amount of time
	 * has elapsed since the last save or through some other strategy. However,
	 * failure to call this method after the last record has been modified will
	 * result in some lost data.
	 * 
	 * @throws DBIOException
	 *             If the given file does not denote an existing, writable
	 *             regular file or if some other error occurs while opening the
	 *             file or if an I/O error occurs while saving the records.
	 */
	void save() throws DBIOException {
		recordsLock.writeLock().lock();
		try {

			/* Ignore the header and jump straight to the records. */
			final RandomAccessFile databaseFile = getDatabase();
			databaseFile.seek(offset);

			for (int index = 0; index < records.size(); index++) {
				final Record record = records.get(index);
				final byte[] data = toByteArray(record);
				databaseFile.write(data);
			}

			databaseFile.close();

		} catch (final IOException ex) {
			throw new DBIOException(ex.getMessage());

		} finally {
			recordsLock.writeLock().unlock();
		}
	}
}