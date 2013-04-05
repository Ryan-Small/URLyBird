/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.ui;

/**
 * Thrown to indicate an issue occurred while attempting to launch the
 * application.
 * 
 * @author rsmall
 */
public class LaunchException extends Exception {
	private static final long serialVersionUID = -1229951395931791946L;

	/**
	 * Constructs the exception with a {@code null} detailed message.
	 */
	public LaunchException() {
		super();
	}

	/**
	 * Constructs the exception with {@code message} as its detailed message.
	 * The message can be retrieved later through {@code getMessage}.
	 * 
	 * @param message
	 *            Message indicating why the exception was thrown.
	 */
	public LaunchException(final String message) {
		super(message);
	}
}
