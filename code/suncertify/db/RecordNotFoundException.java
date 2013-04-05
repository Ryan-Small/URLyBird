/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.db;

/**
 * Thrown to indicate that a record was not found in the database.
 * 
 * @author rsmall
 */
public class RecordNotFoundException extends DBException {
	private static final long serialVersionUID = 9172845648588845215L;

	/**
	 * Constructs the exception with a {@code null} detailed message.
	 */
	public RecordNotFoundException() {
		super();
	}

	/**
	 * Constructs the exception with {@code message} as its detailed message.
	 * The message can be retrieved later through {@code getMessage}.
	 * 
	 * @param message
	 *            Message indicating why the exception was thrown.
	 */
	public RecordNotFoundException(final String message) {
		super(message);
	}
}
