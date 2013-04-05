/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.db;

/**
 * Provides access to a database system. The user of this interface has precise
 * control over the source of the data and how it is accessed.
 * 
 * <p>
 * The implementing class must be thread-safe as multiple clients may attempt to
 * modify the data simultaneously.
 * 
 * <p>
 * The {@code DB} interface requires that a client must invoke
 * {@link #lock(int) lock} prior to calling {@link #update(int, String[], long)
 * update}, {@link #delete(int, long) delete} or {@link #unlock(int, long)
 * unlock} on a record. By locking the record, it ensures that two or more
 * clients cannot modify the record at the same time. When the client has
 * finished modifying the record, it must invoke {@code unlock} on the record.
 * 
 * <p>
 * For security reasons, a cookie is provided when a record is locked. This same
 * cookie must be supplied to the {@code update}, {@code delete} and
 * {@code unlock} methods in order to validate that the client does in fact have
 * the record locked.
 * 
 * <p>
 * <b>Example:</b> Below is an example of a method that will delete a record
 * using the {@code DB} interface, but the process is the same for updating.
 * 
 * <pre>
 * void deleteRecord(final int recNo, final DB db) {
 * 
 * 	final long cookie = db.lock(recNo);
 * 
 * 	try {
 * 		db.delete(recNo, cookie);
 * 
 * 	} catch (final RecordNotFoundException ex) {
 * 		// exception handling elided
 * 
 * 	} finally {
 * 		db.unlock(recNo, cookie);
 * 	}
 * }
 * </pre>
 * 
 * <p>
 * Failure to unlock the record could potentially result in a deadlock. It is
 * recommended that the {@code unlock} method be invoked from a {@code finally}
 * clause to ensure that it is unlocked.
 * 
 * @author rsmall
 */
public interface DB {

	/**
	 * Returns the fields belonging to the record specified by {@code recNo}.
	 * Each element in the array will identify a unique field associated with
	 * the record as determined by the database.
	 * 
	 * @param recNo
	 *            Record number of the record to retrieve.
	 * 
	 * @return Fields belonging to the record specified by {@code recNo}.
	 * 
	 * @throws RecordNotFoundException
	 *             If the record specified by {@code recNo} does not exist.
	 */
	public String[] read(final int recNo) throws RecordNotFoundException;

	/**
	 * Updates the fields belonging to the record specified by {@code recNo}.
	 * Each element in the array will identify a unique field associated with
	 * the record as determined by the database.
	 * 
	 * <p>
	 * In order to update the record, the client will need to {@link #lock(int)
	 * lock} the record, {@link #update(int, String[], long) update} the record
	 * and then {@link #unlock(int, long) unlock} the record. This will ensure
	 * that another client doesn't modify the record while it is being updated.
	 * 
	 * <p>
	 * Note that failure to {@link #unlock(int, long) unlock} the record could
	 * result in a deadlock.
	 * 
	 * <p>
	 * <b>Example:</b> Below is an example of a method that will update a record
	 * using the {@code DB} interface.
	 * 
	 * <pre>
	 * void updateRecord(final int recNo, final String[] fields, final DB db) {
	 * 
	 * 	final long cookie = db.lock(recNo);
	 * 
	 * 	try {
	 * 		db.update(recNo, fields, cookie);
	 * 
	 * 	} catch (final RecordNotFoundException ex) {
	 * 		// exception handling elided
	 * 
	 * 	} finally {
	 * 		db.unlock(recNo, cookie);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param recNo
	 *            Record number of the record to update.
	 * 
	 * @param data
	 *            The fields that will be assigned to the record. Each element
	 *            in the array will identify a unique field associated with the
	 *            record as determined by the database.
	 * 
	 * @param lockCookie
	 *            Cookie that was provided when the record, specified by
	 *            {@code recNo}, was locked.
	 * 
	 * @throws RecordNotFoundException
	 *             If the record, specified by {@code recNo}, could not be
	 *             found.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code fields} is {@code null}, does not have the correct
	 *             number of fields, contains a {@code null} field or has a
	 *             field that exceeds the size its maximum character limit.
	 * 
	 * @throws IllegalStateException
	 *             If the record, specified by {@code recNo}, was never locked.
	 * 
	 * @throws SecurityException
	 *             If the record, specified by {@code recNo}, is locked with a
	 *             cookie other than {@code lockCookie}.
	 */
	public void update(final int recNo, final String[] data,
			final long lockCookie) throws RecordNotFoundException,
			SecurityException;

	/**
	 * Deletes the record specified by {@code recNo}.
	 * 
	 * <p>
	 * In order to delete the record, the client will need to {@link #lock(int)
	 * lock} the record, {@link #delete(int, long) delete} the record and then
	 * {@link #unlock(int, long) unlock} the record. By locking the record, it
	 * ensures that another client cannot modify the record while it is being
	 * deleted.
	 * 
	 * <p>
	 * Note that failure to {@link #unlock(int, long) unlock} the record could
	 * result in a deadlock.
	 * 
	 * <p>
	 * <b>Example:</b> Below is an example of a method that will delete a record
	 * using the {@code DB} interface.
	 * 
	 * <pre>
	 * void updateRecord(final int recNo, final String[] fields, final DB db) {
	 * 
	 * 	final long cookie = db.lock(recNo);
	 * 
	 * 	try {
	 * 		db.delete(recNo, cookie);
	 * 
	 * 	} catch (final RecordNotFoundException ex) {
	 * 		// exception handling elided
	 * 
	 * 	} finally {
	 * 		db.unlock(recNo, cookie);
	 * 	}
	 * }
	 * </pre>
	 * 
	 * @param recNo
	 *            Record number of the record to delete.
	 * 
	 * @param lockCookie
	 *            Cookie that was provided when the record, specified by
	 *            {@code recNo}, was locked.
	 * 
	 * @throws RecordNotFoundException
	 *             If the record, specified by {@code recNo}, could not be
	 *             found.
	 * 
	 * @throws IllegalStateException
	 *             If the record, specified by {@code recNo}, was never locked.
	 * 
	 * @throws SecurityException
	 *             If the record, specified by {@code recNo}, is locked with a
	 *             cookie other than {@code lockCookie}.
	 */
	public void delete(final int recNo, final long lockCookie)
			throws RecordNotFoundException, SecurityException;

	/**
	 * Returns an array of the record numbers that match {@code criteria}.
	 * 
	 * <p>
	 * A record is considered a match if the record's field identified by
	 * {@code n} starts with {@code criteria[n]} for every non-null value within
	 * {@code criteria}.
	 * 
	 * <p>
	 * For example, if {@code criteria[0]} equals {@code John},
	 * {@code criteria[1]} equals {@code Doe} and {@code criteria[2]} is
	 * {@code null}, then a record would be considered a match if field
	 * {@code 0} equals {@code Johnny} and field {@code 1} equals {@code Doe},
	 * but not if field {@code 0} equals {@code Sean} and field {@code 1} equals
	 * {@code Doe}. Since {@code criteria[2]} is {@code null}, it doesn't matter
	 * what field {@code 2} is for either record.
	 * 
	 * <p>
	 * This method is case sensitive.
	 * 
	 * <p>
	 * All records will be returned if every element in {@code criteria} is
	 * {@code null}.
	 * 
	 * @param criteria
	 *            Criteria to use when searching for records.
	 * 
	 * @return An array of the record numbers that match {@code criteria}.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code criteria} is {@code null} or does not have the same
	 *             number of elements as a record has fields.
	 */
	public int[] find(final String[] criteria);

	/**
	 * Creates a new record and sets {@code data} as its fields.
	 * 
	 * @param data
	 *            The fields that will be assigned to the record. Each element
	 *            in the array will identify a unique field associated with the
	 *            record as determined by the database.
	 * 
	 * @return Record number of the new record.
	 * 
	 * @throws DuplicateKeyException
	 *             If an identical key already exists.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code fields} is {@code null}, does not have the correct
	 *             number of fields, contains a {@code null} field or has a
	 *             field that exceeds the size its maximum character limit.
	 */
	public int create(final String[] data) throws DuplicateKeyException;

	/**
	 * Locks the record, specified by {@code recNo}, thereby preventing another
	 * client from locking the record. If the record is already locked by a
	 * different client, then the method will block until the record is
	 * unlocked.
	 * 
	 * <p>
	 * For security reasons, a cookie is provided when a record is locked. This
	 * cookie is used to identify the client when unlocking the record.
	 * 
	 * <p>
	 * Note that failure to {@link #unlock(int, long) unlock} the record could
	 * result in a deadlock.
	 * 
	 * @param recNo
	 *            Record number of the record to lock.
	 * 
	 * @return Cookie that was used to lock the record.
	 * 
	 * @throws RecordNotFoundException
	 *             If the record specified by {@code recNo} could not be found.
	 */
	public long lock(final int recNo) throws RecordNotFoundException;

	/**
	 * Releases the lock on the record, specified by {@code recNo}, allowing it
	 * to be locked by another client. This method must be invoked with the same
	 * cookie that was provided when the record was locked. Failure to unlock a
	 * record could result in a deadlock, thus it is recommended to call the
	 * method from a {@code finally} clause to ensure that the record will in
	 * fact be unlocked.
	 * 
	 * @param recNo
	 *            Record number of the record to unlock.
	 * 
	 * @param lockCookie
	 *            Cookie that was provided when the record, specified by
	 *            {@code recNo}, was locked.
	 * 
	 * @throws RecordNotFoundException
	 *             If the record, specified by {@code recNo}, could not be
	 *             found.
	 * 
	 * @throws IllegalStateException
	 *             If the record, specified by {@code recNo}, was never locked.
	 * 
	 * @throws SecurityException
	 *             If the record, specified by {@code recNo}, is locked with a
	 *             cookie other than {@code lockCookie}.
	 */
	public void unlock(final int recNo, final long lockCookie)
			throws RecordNotFoundException, SecurityException;
}