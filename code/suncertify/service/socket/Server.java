/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.service.socket;

import suncertify.service.ServicesException;

/**
 * Represents a server that clients can connect to it and send requests.
 * 
 * @author rsmall
 */
public interface Server {

	/**
	 * Starts the server allowing clients to connect and send requests to it.
	 * 
	 * @throws ServicesException
	 *             If there is a problem while attempting to start the server.
	 */
	public abstract void start() throws ServicesException;

	/**
	 * Stops the server preventing clients from connecting and send requests to
	 * it.
	 * 
	 * @throws ServicesException
	 *             If there is a problem while attempting to stop the server.
	 */
	public abstract void stop() throws ServicesException;

	/**
	 * Returns {@code true} if the server is running; {@code false} otherwise.
	 * 
	 * @return {@code true} if the server is running; {@code false} otherwise.
	 */
	public abstract boolean isRunning();

}