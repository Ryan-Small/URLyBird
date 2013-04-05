/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.db;

/**
 * Thrown to indicate a security violation.
 * 
 * @author rsmall
 */
public class SecurityException extends RuntimeException {
	private static final long serialVersionUID = 8491334144885559308L;

	/**
	 * Constructs the exception with a {@code null} detailed message.
	 */
	public SecurityException() {
		super();
	}

	/**
	 * Constructs the exception with {@code message} as its detailed message.
	 * The message can be retrieved later through {@code getMessage}.
	 * 
	 * @param message
	 *            Message indicating why the exception was thrown.
	 */
	public SecurityException(final String message) {
		super(message);
	}
}
