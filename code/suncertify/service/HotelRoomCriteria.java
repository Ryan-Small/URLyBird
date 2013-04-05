/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.service;

import java.io.Serializable;

/**
 * Specifies the search criteria for finding a {@code HotelRoom} when invoking
 * {@link HotelServices#find(HotelRoomCriteria) find}.
 * 
 * @author rsmall
 */
public class HotelRoomCriteria implements Serializable {
	private static final long serialVersionUID = -2163164495968636063L;

	/** Name of the hotel. */
	public String name;

	/** Name of the city the hotel is located in. */
	public String location;
}
