/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.service;

import static suncertify.service.HotelRoom.Field.CUSTOMER;
import static suncertify.service.HotelRoom.Field.LOCATION;
import static suncertify.service.HotelRoom.Field.NAME;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import suncertify.db.*;

/**
 * Default implementation of {@code HotelServices}.
 * 
 * <p>
 * Changes to the database system, specifically the position of where the data
 * begins, will need to be reflected here.
 * 
 * @author rsmall
 */
public class DefaultHotelServices implements HotelServices {

	/** Position in the database file where the actual data begins. */
	private static final long OFFSET = 74;

	/** Provides access to the database system. */
	private DBAdapter data;

	/**
	 * Constructs a new {@code DefaultHotelServices} using the specified
	 * {@code databaseFile}. {@code databaseFile} should reference an existing
	 * file used for storing the records.
	 * 
	 * @param database
	 *            Path to the physical file on disk that contains the records.
	 * 
	 * @throws ServicesInitializationException
	 *             If {@code database} does not denote an existing, writable
	 *             regular file or if an I/O error occurs.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code database} is {@code null}.
	 */
	public DefaultHotelServices(final File database)
			throws ServicesInitializationException {

		if (database == null) {
			throw new IllegalArgumentException("database cannot be null");
		}

		if (!database.exists()) {
			final String message = "Database file does not exist.";
			throw new ServicesInitializationException(message);
		}

		try {
			data = new Data(database, OFFSET, HotelRoom.Field.getFieldFormat());

		} catch (final DBException ex) {
			final String message = "Cannot load database. " + ex.getMessage();
			throw new ServicesInitializationException(message);
		}
	}

	/**
	 * Determines if {@code string} is an eight digit String.
	 * 
	 * @param string
	 *            String to check.
	 * 
	 * @return {@code true} if {@code id} is an eight digit String;
	 *         {@code false} otherwise.
	 */
	private boolean isEightDigitString(final String string) {
		if (string == null) {
			return false;
		}

		final Pattern pattern = Pattern.compile("\\d{8}");
		final Matcher matcher = pattern.matcher(string);
		return matcher.matches();
	}

	/**
	 * Determines if {@code hotelRoom} is booked.
	 * 
	 * @param hotelRoom
	 *            {@code HotelRoom} to evaluate.
	 * 
	 * @return {@code true} if {@code hotelRoom} is booked; {@code false}
	 *         otherwise.
	 * 
	 * @throws RecordNotFoundException
	 *             If the {@code hotelRoom} does not exist.
	 */
	private boolean isBooked(final HotelRoom hotelRoom)
			throws RecordNotFoundException {

		final String[] fields = data.read(hotelRoom.getRecordNumber());
		return !fields[CUSTOMER.ordinal()].equals("");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bookRoom(final HotelRoom hotelRoom, final String id)
			throws HotelRoomNotFoundException, ServicesException,
			IllegalArgumentException {

		if (hotelRoom == null) {
			throw new IllegalArgumentException("hotelRoom cannot be null.");
		}

		if (!isEightDigitString(id)) {
			final String message = "Customer ID must be an eight digit value.";
			throw new IllegalArgumentException(message);
		}

		try {
			final int recNo = hotelRoom.getRecordNumber();

			if (isBooked(hotelRoom)) {
				throw new IllegalArgumentException("Room is already booked.");
			}

			final long cookie = data.lock(recNo);
			hotelRoom.setField(CUSTOMER, id);

			data.update(recNo, hotelRoom.getFields(), cookie);
			data.unlock(recNo, cookie);

		} catch (final RecordNotFoundException ex) {
			throw new HotelRoomNotFoundException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HotelRoom[] getHotelRooms() throws ServicesException {
		return find(new HotelRoomCriteria());
	}

	/**
	 * Checks to see if {@code hotelRoom} is a valid match. {@code hotelRoom} is
	 * considered a match if {@code criteria.name} is equal to
	 * {@code hotelRoom.name} or {@code null} and if {@code criteria.location}
	 * is equal to {@code hotelRoom.location} or is {@code null}.
	 * 
	 * @param hotelRoom
	 *            The {@code HotelRoom} to evaluate.
	 * 
	 * @return {@code true} if the {@code hotelRoom} meets the criteria;
	 *         {@code false} otherwise.
	 */
	private boolean isValidMatch(final HotelRoomCriteria criteria,
			final HotelRoom hotelRoom) {

		boolean nameMatches = false;
		final String roomName = hotelRoom.getField(NAME);
		if (roomName.equals(criteria.name) || (criteria.name == null)) {
			nameMatches = true;
		}

		boolean locationMatches = false;
		final String roomLocation = hotelRoom.getField(LOCATION);
		if (roomLocation.equals(criteria.location)
				|| (criteria.location == null)) {
			locationMatches = true;
		}

		return nameMatches && locationMatches;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HotelRoom[] find(final HotelRoomCriteria criteria)
			throws ServicesException, IllegalArgumentException {

		if (criteria == null) {
			throw new IllegalArgumentException("criteria cannot be null.");
		}

		/* Get the record numbers that meet the specified criteria. */
		final int numberOfFields = HotelRoom.Field.values().length;
		final String[] fieldCriteria = new String[numberOfFields];

		fieldCriteria[NAME.ordinal()] = criteria.name;
		fieldCriteria[LOCATION.ordinal()] = criteria.location;

		final int[] matchingRecords = data.find(fieldCriteria);

		/* Fetch the HotelRooms themselves. */
		final ArrayList<HotelRoom> hotelRooms = new ArrayList<HotelRoom>();
		for (final int recNo : matchingRecords) {
			try {
				final String[] fields = data.read(recNo);
				final HotelRoom hotelRoom = new HotelRoom(recNo, fields);

				if (isValidMatch(criteria, hotelRoom)) {
					hotelRooms.add(hotelRoom);
				}

			} catch (final RecordNotFoundException ex) {
				/*
				 * This means that a record that initially matched our criteria
				 * has been deleted and is no longer available. At this point,
				 * the best we can do is ignore the record and not add it to the
				 * final results.
				 */
			}
		}

		/* Convert the List to an array and return it. */
		final HotelRoom[] rooms = new HotelRoom[hotelRooms.size()];
		return hotelRooms.toArray(rooms);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save() throws ServicesException {
		try {
			data.save();
		} catch (final DBIOException ex) {
			throw new ServicesException("Changes could not be saved.");
		}
	}
}
