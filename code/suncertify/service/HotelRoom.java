/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.service;

import java.io.Serializable;

/**
 * Represents a hotel room that is stored within a database system.
 * 
 * <p>
 * Changes to the database system, specifically the structure of the records,
 * will need to be reflected here.
 * 
 * @author rsmall
 */
public class HotelRoom implements Serializable {
	private static final long serialVersionUID = 5138016789086468127L;

	/** Represents a field associated with the {@code HotelRoom}. */
	public enum Field {

		/*
		 * Changes to the database file format must be reflected here. For
		 * instance, if a new field is added to the hotel rooms within the
		 * database file, that field must also be added here.
		 * 
		 * Each field must be in the order in which they will be read/returned
		 * from the database as the ordinal may be used to identify the element
		 * represented by the field. i.e. String name = fields[NAME.ordinal];
		 */

		/** Name of the hotel. */
		NAME(64),

		/** Name of the city of the hotel is located in. */
		LOCATION(64),

		/** Maximum occupancy of the room. */
		OCCUPANCY(4),

		/** Indicates if the room is smoking or non-smoking. */
		SMOKING(1),

		/** Price per night for the room. */
		COST(8),

		/** Date of availability. */
		DATE(10),

		/** ID of the customer who has booked this room. */
		CUSTOMER(8);

		/** Maximum number of bytes permitted for this field. */
		private final int maxSize;

		/**
		 * Returns the format of the fields for a record within the database
		 * that represents a {@code HotelRoom}.
		 * 
		 * <p>
		 * Array length indicates the number of fields. Each element identifies
		 * the maximum number of bytes permitted for the field corresponding to
		 * the same index.
		 * 
		 * @return Format of the fields for a record within the database.
		 */
		static int[] getFieldFormat() {
			final int[] recordFormat = new int[Field.values().length];
			for (final Field field : Field.values()) {
				recordFormat[field.ordinal()] = field.maxSize;
			}
			return recordFormat;
		}

		/**
		 * Ensures that {@code fields} adheres to the specifications set by
		 * {@code Field.getFieldFormat}.
		 * 
		 * @param fields
		 *            The set of fields to evaluate.
		 * 
		 * @throws IllegalArgumentException
		 *             If {@code fields} does not have the correct number of
		 *             elements or if an element exceeds its size constraint.
		 */
		private static void checkFormat(final String[] fields)
				throws IllegalArgumentException {
			if (fields.length != Field.values().length) {
				throw new IllegalArgumentException("invalid number of fields");
			}

			for (final Field field : Field.values()) {
				checkField(field, fields[field.ordinal()]);
			}
		}

		/**
		 * Ensures that {@code value} does not exceed the size specified by
		 * {@code field.getMaxSize}. If {@code value} exceeds this size, then an
		 * {@code IllegalArgumentException} will be thrown.
		 * 
		 * @param field
		 *            The {@code Field} {@code value} represents.
		 * 
		 * @param value
		 *            String to evaluate.
		 * 
		 * @throws IllegalArgumentException
		 *             If {@code value} exceeds the size specified by
		 *             {@code field.getMaxSize}.
		 */
		private static void checkField(final Field field, final String value)
				throws IllegalArgumentException {
			if (value.getBytes().length > field.maxSize) {
				final String msg = field + "exceeds " + field.maxSize + "bytes";
				throw new IllegalArgumentException(msg);
			}
		}

		/**
		 * Constructs a new {@code Field}.
		 * 
		 * @param size
		 *            Maximum number of bytes permitted for the field.
		 */
		private Field(final int size) {
			maxSize = size;
		}

		/**
		 * Returns the maximum number of bytes permitted for this field.
		 * 
		 * @return Maximum number of bytes permitted for this field.
		 */
		int getMaxSize() {
			return maxSize;
		}
	}

	/**
	 * Record number of the record. This value should be unique from all other
	 * record numbers and should serve as identification for this particular
	 * record within the database.
	 */
	private final int recNo;

	/**
	 * The fields that will be associated with the record. Each element in the
	 * array will identify a unique field associated with the record as
	 * determined by {@code Field} and the database.
	 */
	private final String[] fields;

	/**
	 * Constructs a new {@code HotelRoom} using {@code fields} as the data.
	 * 
	 * <p>
	 * The element within {@code fields} must be accurately represented as
	 * specified by {@code Field}. This means that there must be the same number
	 * of elements within {@code fields} as there are values specified for
	 * {@code Field} and each element must be less than or equal to the
	 * {@link Field#getMaxSize() maxSize}.
	 * 
	 * @param recNo
	 *            Record number of the record. This value should be unique from
	 *            all other record numbers and should serve as identification
	 *            for this particular record within the database.
	 * 
	 * @param fields
	 *            Data that represents the record's fields.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code fields} does not have the correct number of
	 *             elements or if an element exceeds its size constraint.
	 */
	HotelRoom(final int recNo, final String[] fields) {
		Field.checkFormat(fields);

		this.recNo = recNo;
		this.fields = fields;
	}

	/**
	 * Returns the record number. This value is unique from all other record
	 * numbers and should serve as identification for this particular record
	 * within the database.
	 * 
	 * @return The record number.
	 */
	int getRecordNumber() {
		return recNo;
	}

	/**
	 * Returns the String[] representation of this {@code HotelRoom}. The
	 * elements within the array represent the structure specified by
	 * {@code Field}. This means that there will be the same number of elements
	 * within the array as there are values specified for {@code Field}.
	 * 
	 * @return Data that represents the record's fields.
	 */
	String[] getFields() {
		return fields;
	}

	/**
	 * Returns the value of a field.
	 * 
	 * @param field
	 *            Represents the field to retrieve.
	 * 
	 * @return The value of the field.
	 */
	public String getField(final Field field) {
		return fields[field.ordinal()];
	}

	/**
	 * Sets the value of a field.
	 * 
	 * @param field
	 *            Represents the field to set.
	 * 
	 * @param value
	 *            The new value for the field.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code value} exceeds the size constraint.
	 */
	void setField(final Field field, final String value) {
		Field.checkField(field, value);
		fields[field.ordinal()] = value;
	}
}
