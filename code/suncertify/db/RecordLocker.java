/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.db;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class is responsible for providing thread-safe functionality for locking
 * records stored in a database system. Locks are placed on record numbers, thus
 * each record needs to have a unique number. Once a record has been locked, any
 * subsequent requests to lock the record will be blocked until the record
 * number becomes available again.
 * 
 * @author rsmall
 */
class RecordLocker {

	/** Maps the record number to the cookie that holds a lock on it. */
	private final Map<Integer, Long> lockedRecords =
			new HashMap<Integer, Long>();

	/** Limits access to {@code lockedRecords}. */
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	/** Signals threads waiting for a record that it is now available. */
	private final Condition writeLockReleased = lock.writeLock().newCondition();

	/**
	 * Sole constructor. Default access to prevent it from being instantiated
	 * outside of the package.
	 */
	RecordLocker() {
	}

	/**
	 * Ensures that the record, specified by {@code recNo}, has been locked by
	 * {@code lockCookie}. If the {@code lockCookie} does not match the expected
	 * cookie, then an exception is thrown; otherwise the method just returns.
	 * 
	 * @param recNo
	 *            Record number of the record whose lock will be validated.
	 * 
	 * @param lockCookie
	 *            Cookie that was provided when the record was locked.
	 * 
	 * @throws IllegalStateException
	 *             If the record, specified by {@code recNo}, was never locked.
	 * 
	 * @throws SecurityException
	 *             If the record, specified by {@code recNo}, is locked with a
	 *             cookie other than {@code lockCookie}.
	 */
	public void validateCookie(final int recNo, final long lockCookie) {
		lock.readLock().lock();
		try {
			final Long expectedLockCookie = lockedRecords.get(recNo);

			if (expectedLockCookie == null) {
				throw new IllegalStateException("lock has not been obtained");

			} else if (lockCookie != expectedLockCookie) {
				throw new SecurityException("invalid lockCookie");
			}
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * Locks the record, specified by {@code recNo}, thereby preventing another
	 * client from locking the record. If the record is already locked by a
	 * different client, then the method will block until the record is
	 * unlocked.
	 * 
	 * <p>
	 * For security reasons, a cookie is provided when a record is locked. This
	 * cookie is used to identify the client when unlocking the record. A client
	 * can use this cookie by {@link #validateCookie(int, long) validating} it
	 * prior to modifying the record.
	 * 
	 * <p>
	 * Note that failure to {@link #unlock(int, long) unlock} the record could
	 * result in a deadlock.
	 * 
	 * @param recNo
	 *            Record number of the record to lock.
	 * 
	 * @return Cookie that was used to lock the record.
	 */
	public long lock(final int recNo) {
		lock.writeLock().lock();
		try {

			final long lockCookie = Thread.currentThread().getId();

			/*
			 * We need to check to see if recNo has been locked. If it is
			 * locked, then we will need to wait until it becomes available.
			 */
			while (lockedRecords.containsKey(recNo)) {

				/*
				 * Oddly, the client is requesting a lock on a record that it
				 * already owns. Return the existing cookie, just to be
				 * consistent.
				 */
				if (lockedRecords.get(recNo) == lockCookie) {
					return lockedRecords.get(recNo);
				}

				try {
					writeLockReleased.await();

				} catch (final InterruptedException ex) {
					/*
					 * Just check to see if there is still a lock on the record
					 * and continue to wait if needed.
					 */
				}
			}

			lockedRecords.put(recNo, lockCookie);
			return lockCookie;

		} finally {
			lock.writeLock().unlock();
		}
	}

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
	 * @throws IllegalStateException
	 *             If the record, specified by {@code recNo}, was never locked.
	 * 
	 * @throws SecurityException
	 *             If the record, specified by {@code recNo}, is locked with a
	 *             cookie other than {@code lockCookie}.
	 */
	public void unlock(final int recNo, final long lockCookie) {
		lock.writeLock().lock();
		try {
			validateCookie(recNo, lockCookie);
			lockedRecords.remove(recNo);
			writeLockReleased.signalAll();

		} finally {
			lock.writeLock().unlock();
		}
	}
}
