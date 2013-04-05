/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.service.socket;

import java.io.Serializable;

import suncertify.service.HotelServices;

/**
 * Encapsulates a {@code HotelServices} request as an object. This allows the
 * request to be sent from the {@code HotelClient} to the {@code HotelServer}
 * where it will be executed.
 * 
 * <p>
 * Note that this interface does extend {@code Serializable}, allowing a
 * {@code ServiceRequest} to be serialized.
 * 
 * @author rsmall
 */
interface ServiceRequest extends Serializable {

	/**
	 * Executes the request.
	 * 
	 * @param services
	 *            The {@code HotelServices} for which the operation may use.
	 * 
	 * @return The result of executing the operation, {@code null} if no value
	 *         is to be returned or an exception if one occurred while
	 *         attempting to execute the request.
	 */
	public Object execute(HotelServices services);
}
