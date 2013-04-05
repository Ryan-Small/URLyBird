/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.db;

import java.io.File;

/**
 * Provides access to a flat-file database system.
 * 
 * <p>
 * This class provides access to the database in a thread-safe manner; Multiple
 * clients may invoke the methods of this class without worrying about data
 * corruption.
 * 
 * <p>
 * The {@link #lock(int) lock} method must be invoked prior to calling
 * {@link #update(int, String[], long) update}, {@link #delete(int, long)
 * delete} or {@link #unlock(int, long) unlock} on a record. By locking the
 * record, it ensures that two or more clients cannot modify the record at the
 * same time. When the client has finished modifying the record, it must invoke
 * {@code unlock} on the record.
 * 
 * <p>
 * For security reasons, a cookie is provided when a record is locked. This same
 * cookie must be supplied to the {@code update}, {@code delete} and
 * {@code unlock} methods in order to validate that the client does in fact have
 * the record locked.
 * 
 * <p>
 * <b>Example:</b> Below is an example of a method that will delete a record
 * using the {@code DB} interface, but the process is the same for updating.
 * 
 * <pre>
 * void deleteRecord(final int recNo, final DB db) {
 * 
 * 	final long cookie = db.lock(recNo);
 * 
 * 	try {
 * 		db.delete(recNo, cookie);
 * 
 * 	} catch (final RecordNotFoundException ex) {
 * 		// exception handling elided
 * 
 * 	} finally {
 * 		db.unlock(recNo, cookie);
 * 	}
 * }
 * </pre>
 * 
 * <p>
 * Failure to unlock the record could potentially result in a deadlock. It is
 * recommended that the {@code unlock} method be invoked from a {@code finally}
 * clause to ensure that it is unlocked.
 * 
 * @author rsmall
 */
public class Data implements DBAdapter {

	/** Handles the accessing of the records. */
	private final RecordAccess recordAccess;

	/** Handles the locking of the records. */
	private final RecordLocker recordLocker;

	/**
	 * Creates a new {@code Data} object using the data stored in the existing
	 * {@code database}. It is expected that {@code database} will not be
	 * accessed while an instance of this class still has a reference to it;
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
	 *            Path to the physical file on disk that contains the records.`
	 * 
	 * @param offset
	 *            Identifies where the first byte of the first record begins
	 *            within the {@code database} file.
	 * 
	 * @param recordFormat
	 *            Defines the exact format of a record's fields and their
	 *            lengths within the {@code database} file.
	 * 
	 * @throws DBIOException
	 *             If {@code database} does not denote an existing, writable
	 *             regular file or if an I/O error occurs.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code database} is {@code null}, {@code recordFormat} is
	 *             {@code null}, or {@code offset} is less than zero.
	 */
	public Data(final File database, final long offset, final int[] recordFormat)
			throws DBIOException, IllegalArgumentException {
		recordAccess = new RecordAccess(database, offset, recordFormat);
		recordLocker = new RecordLocker();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] read(final int recNo) throws RecordNotFoundException {
		return recordAccess.read(recNo);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(final int recNo, final String[] data,
			final long lockCookie) throws IllegalArgumentException,
			IllegalStateException, SecurityException {
		recordLocker.validateCookie(recNo, lockCookie);
		recordAccess.update(recNo, data);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(final int recNo, final long lockCookie)
			throws IllegalStateException, SecurityException {
		recordLocker.validateCookie(recNo, lockCookie);
		recordAccess.delete(recNo);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] find(final String[] criteria) throws IllegalArgumentException {
		return recordAccess.find(criteria);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int create(final String[] data) throws IllegalArgumentException {
		return recordAccess.create(data);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The data will be saved back to the {@code database}. The data will be
	 * written starting at the position denoted by {@code offset} using the
	 * format specified by {@code recordFormat}. Each of these parameters are
	 * specified during construction.
	 */
	@Override
	public void save() throws DBIOException {
		recordAccess.save();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long lock(final int recNo) throws RecordNotFoundException {

		/*
		 * We need to blindly lock the record before ensuring that it exists. If
		 * we performed the check prior to locking the record, then we run the
		 * risk of having another client delete the record after the check, but
		 * before we can lock it here.
		 */
		final long cookie = recordLocker.lock(recNo);
		try {
			read(recNo);
			return cookie;

		} catch (final RecordNotFoundException ex) {

			/*
			 * Unfortunately, the record doesn't exist so we have now have to
			 * unlock the record.
			 */
			recordLocker.unlock(recNo, cookie);
			throw ex;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unlock(final int recNo, final long lockCookie)
			throws IllegalStateException, SecurityException {
		recordLocker.unlock(recNo, lockCookie);
	}
}
