/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.db;

/**
 * Thrown to indicate that there was a problem reading or writing to the
 * database.
 * 
 * @author rsmall
 */
public class DBIOException extends DBException {
	private static final long serialVersionUID = 2694216758383137856L;

	/**
	 * Constructs the exception with a {@code null} detailed message.
	 */
	public DBIOException() {
		super();
	}

	/**
	 * Constructs the exception with {@code message} as its detailed message.
	 * The message can be retrieved later through {@code getMessage}.
	 * 
	 * @param message
	 *            Message indicating why the exception was thrown.
	 */
	public DBIOException(final String message) {
		super(message);
	}
}
