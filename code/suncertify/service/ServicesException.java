/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.service;

/**
 * Thrown when there is a problem providing a service. This class is the general
 * class of exceptions produced by a failed {@code HotelService} operation.
 * 
 * @author rsmall
 */
public class ServicesException extends Exception {
	private static final long serialVersionUID = -2647185025196632503L;

	/**
	 * Constructs the exception with a {@code null} detailed message.
	 */
	public ServicesException() {
		super();
	}

	/**
	 * Constructs the exception with {@code message} as its detailed message.
	 * The message can be retrieved later through {@code getMessage}.
	 * 
	 * @param message
	 *            Message indicating why the exception was thrown.
	 */
	public ServicesException(final String message) {
		super(message);
	}
}
