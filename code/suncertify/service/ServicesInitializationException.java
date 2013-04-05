/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.service;

/**
 * Thrown when an instance of {@code HotelService} could not be initialized.
 * 
 * @author rsmall
 */
public class ServicesInitializationException extends ServicesException {
	private static final long serialVersionUID = -7387345722887114871L;

	/**
	 * Constructs the exception with a {@code null} detailed message.
	 */
	public ServicesInitializationException() {
		super();
	}

	/**
	 * Constructs the exception with {@code message} as its detailed message.
	 * The message can be retrieved later through {@code getMessage}.
	 * 
	 * @param message
	 *            Message indicating why the exception was thrown.
	 */
	public ServicesInitializationException(final String message) {
		super(message);
	}
}
