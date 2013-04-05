/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.db;

/**
 * Thrown to indicate that that a record of with the same key already exists in
 * the database.
 * 
 * @author rsmall
 */
public class DuplicateKeyException extends DBException {
	private static final long serialVersionUID = -6689165809485807888L;

	/**
	 * Constructs the exception with a {@code null} detailed message.
	 */
	public DuplicateKeyException() {
		super();
	}

	/**
	 * Constructs the exception with {@code message} as its detailed message.
	 * The message can be retrieved later through {@code getMessage}.
	 * 
	 * @param message
	 *            Message indicating why the exception was thrown.
	 */
	public DuplicateKeyException(final String message) {
		super(message);
	}
}
