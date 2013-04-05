/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.service;

/**
 * Thrown when a {@code HotelRoom} can not be found.
 * 
 * @author rsmall
 */
public class HotelRoomNotFoundException extends ServicesException {
	private static final long serialVersionUID = 9172845648588845215L;

	/**
	 * Constructs the exception with a {@code null} detailed message.
	 */
	public HotelRoomNotFoundException() {
		super();
	}

	/**
	 * Constructs the exception with {@code message} as its detailed message.
	 * The message can be retrieved later through {@code getMessage}.
	 * 
	 * @param message
	 *            Message indicating why the exception was thrown.
	 */
	public HotelRoomNotFoundException(final String message) {
		super(message);
	}
}
