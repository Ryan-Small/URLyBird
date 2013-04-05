/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.db;

/**
 * Adapts the DB interface to be more consistent with regards to the locking
 * process and adds some new functionality.
 * 
 * <p>
 * The method signatures of {@code update}, {@code delete} and {@code unlock}
 * have been modified to no longer throw {@code RecordNotFoundException}. The
 * exception was removed from the signatures since the {@code lock} method must
 * be invoked prior to any of the methods and it will ensure that the record
 * exists when it locks it.
 * 
 * <p>
 * The new functionality allows a client to determine when the data should be
 * {@link #save() saved}.
 * 
 * @author rsmall
 */
public interface DBAdapter extends DB {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(final int recNo, final String[] data,
			final long lockCookie) throws IllegalArgumentException,
			IllegalStateException, SecurityException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(final int recNo, final long lockCookie)
			throws IllegalStateException, SecurityException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unlock(final int recNo, final long lockCookie)
			throws IllegalStateException, SecurityException;

	/**
	 * Saves the changes made to the database.
	 * 
	 * <p>
	 * Failure to invoke this method after the last modification will result in
	 * some lost data.
	 * 
	 * <p>
	 * This method could be invoked every time a record is modified, after a
	 * specific number of records have been modified, after an amount of time
	 * has elapsed since the last save or through some other strategy.
	 * 
	 * @throws DBIOException
	 *             If an I/O error occurs.
	 */
	public void save() throws DBIOException;
}
