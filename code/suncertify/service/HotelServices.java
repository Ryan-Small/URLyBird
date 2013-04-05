/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.service;

/**
 * Provides services for manipulating a database system for hotel rooms.
 * Specifically, booking and searching for hotel rooms.
 * 
 * <p>
 * The implementing class must be thread-safe as many clients may attempt to
 * modify the data simultaneously through the same implementation.
 * 
 * <p>
 * Note that failure to call {@link #save() save} after the last record has been
 * modified will result in some lost data.
 * 
 * @author rsmall
 */
public interface HotelServices {

	/**
	 * Books the {@code hotelRoom} for the customer represented by {@code id}.
	 * 
	 * <p>
	 * Failure to invoke {@link #save() save} after invoking this method will
	 * result in lost data, specifically it will be as though the
	 * {@code hotelRoom} was never booked with {@code id}.
	 * 
	 * @param hotelRoom
	 *            {@code HotelRoom} to be booked.
	 * 
	 * @param id
	 *            Identification code of the customer booking {@code hotelRoom}.
	 * 
	 * @throws HotelRoomNotFoundException
	 *             If the {@code hotelRoom} does not exist.
	 * 
	 * @throws ServicesException
	 *             If an error occurred while booking {@code room}.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code hotelRoom} is {@code null}, {@code id} is not an
	 *             eight digit String or {@code hotelRoom} is already booked.
	 */
	public void bookRoom(final HotelRoom hotelRoom, final String id)
			throws HotelRoomNotFoundException, ServicesException,
			IllegalArgumentException;

	/**
	 * Returns all of the HotelRooms that are available (HotelRooms that have
	 * been deleted are not considered available).
	 * 
	 * @return An array of all the HotelRooms.
	 * 
	 * @throws ServicesException
	 *             If there is a problem reading the database.
	 */
	public HotelRoom[] getHotelRooms() throws ServicesException;

	/**
	 * Returns an array of the HotelRooms that match {@code criteria}. A
	 * {@code HotelRoom} is considered a match if every attribute equals the
	 * non-null corresponding field within {@code criteria}.
	 * 
	 * <p>
	 * <b>Example:</b> If {@code criteria.name} equals {@code Palace} and
	 * {@code criteria.location} equals {@code Whoville}, then a
	 * {@code HotelRoom} would be considered a match if the HotelRoom's
	 * {@code name} equals {@code Palace} and {@code location} equals
	 * {@code Whoville}, but not if the {@code name} equals {@code Pal} and the
	 * {@code location} equals {@code Whoville}. Note that it doesn't matter
	 * what the other attributes of the {@code HotelRoom} are.
	 * 
	 * <p>
	 * <b>Example:</b> If {@code criteria.name} equals {@code null} and
	 * {@code criteria.location} equals {@code Whoville}, then a
	 * {@code HotelRoom} would be considered a match if the HotelRoom's
	 * {@code location} equals {@code Whoville}, regardless of the HotelRoom's
	 * {@code name}.
	 * 
	 * <p>
	 * This method is case sensitive.
	 * 
	 * <p>
	 * All available records will be returned if every field in {@code criteria}
	 * is {@code null}.
	 * 
	 * @param criteria
	 *            Criteria to use when searching for records.
	 * 
	 * @return An array of the HotelRooms that match {@code criteria}.
	 * 
	 * @throws ServicesException
	 *             If there is a problem reading the database.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code criteria} is {@code null}.
	 */
	public HotelRoom[] find(final HotelRoomCriteria criteria)
			throws ServicesException, IllegalArgumentException;

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
	 * @throws ServicesException
	 *             If there is a problem saving the data.
	 */
	public void save() throws ServicesException;
}
