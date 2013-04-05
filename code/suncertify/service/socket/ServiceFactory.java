/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.service.socket;

import suncertify.service.HotelRoom;
import suncertify.service.HotelRoomCriteria;
import suncertify.service.HotelServices;

/**
 * A factory where each method will produce a different type of
 * {@code ServiceRequest}. Each {@code ServiceRequest} addresses a different
 * goal. These goals are met by manipulating the supplied {@code HotelServices}.
 * 
 * @author rsmall
 */
class ServiceFactory {

	/**
	 * Creates a {@code ServiceRequest} that will invoke the {@code bookRoom}
	 * method on the supplied {@code HotelServices}. The method will be invoked
	 * using {@code room} and {@code id} as the parameters.
	 * 
	 * <p>
	 * This {@code ServiceRequest} will be either the return value of the
	 * {@link HotelServices#bookRoom(HotelRoom, String)} method or an exception
	 * thrown by the method.
	 * 
	 * @param hotelRoom
	 *            {@code HotelRoom} to be booked.
	 * 
	 * @param id
	 *            Identification code of the customer booking {@code hotelRoom}.
	 * 
	 * @return A {@code ServiceRequest} for booking the {@code room} for the
	 *         customer represented by {@code id}.
	 */
	public static ServiceRequest getBookService(final HotelRoom hotelRoom,
			final String id) {

		return new ServiceRequest() {
			private static final long serialVersionUID = 8935465399972628127L;

			@Override
			public Object execute(final HotelServices services) {

				try {
					services.bookRoom(hotelRoom, id);
					return null;

				} catch (final Exception ex) {
					return ex;
				}
			}
		};
	}

	/**
	 * Creates a {@code ServiceRequest} that will invoke the {@code find} method
	 * of the supplied {@code HotelServices}. The method will be invoked using
	 * {@code criteria} as the parameter.
	 * 
	 * <p>
	 * This {@code ServiceRequest} will be either the return value of the
	 * {@link HotelServices#find(HotelRoomCriteria)} method or an exception
	 * thrown by the method.
	 * 
	 * @param criteria
	 *            Criteria to use when invoking {@code find}.
	 * 
	 * @return A {@code ServiceRequest} for finding the HotelRooms that match
	 *         the specified {@code criteria}.
	 */
	public static ServiceRequest getFindRequest(final HotelRoomCriteria criteria) {

		return new ServiceRequest() {
			private static final long serialVersionUID = -9217779195429800606L;

			@Override
			public Object execute(final HotelServices services) {

				try {
					final HotelRoom[] matchingRooms = services.find(criteria);
					return matchingRooms;

				} catch (final Exception ex) {
					return ex;
				}
			}
		};
	}

	/**
	 * Creates a {@code ServiceRequest} that will invoke the {@code save} method
	 * of the supplied {@code HotelServices}.
	 * 
	 * <p>
	 * This {@code ServiceRequest} will be either the return value of the
	 * {@link HotelServices#save()} method or an exception thrown by the method.
	 * 
	 * @return A {@code ServiceRequest} for saving the data.
	 */
	public static ServiceRequest getSaveRequest() {

		return new ServiceRequest() {
			private static final long serialVersionUID = 733696056519556555L;

			@Override
			public Object execute(final HotelServices services) {

				try {
					services.save();
					return null;

				} catch (final Exception ex) {
					return ex;
				}
			}
		};
	}
}
