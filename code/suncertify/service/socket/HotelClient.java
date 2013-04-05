/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.service.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import suncertify.service.*;
import suncertify.ui.LaunchException;

/**
 * This class is responsible for providing functionality for searching, booking
 * and saving HotelRooms. All data is accessed in a thread-safe manner allowing
 * multiple clients to request information simultaneously.
 * 
 * <p>
 * Client based implementation of {@code HotelServices}. Invoking any method
 * here will be handled by the server. The server will send back the results.
 * All communication with the server is handled through sockets. The server must
 * be available at all times.
 * 
 * <p>
 * Note that failure to call {@link #save() save} after the last record has been
 * modified will result in some lost data.
 * 
 * @author rsmall
 */
public class HotelClient implements HotelServices {

	/** Location of the server. */
	private final InetAddress hostname;

	/** Port the server is listening on. */
	private final int port;

	/**
	 * Constructs a new {@code HotelClient} using the {@code hostname} and
	 * {@code port} to connect with the server. It is expected that the server
	 * will be available.
	 * 
	 * @param hostname
	 *            Address of where the server is located.
	 * 
	 * @param port
	 *            Port the server is listening on.Must be between {@code 1024}
	 *            and {@code 65535}.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code hostname} is {@code null} or {@code port} is not
	 *             between {@code 1024} and {@code 65535}.
	 */
	public HotelClient(final InetAddress hostname, final int port)
			throws LaunchException {

		if (hostname == null) {
			throw new IllegalArgumentException("hostname cannot be null");
		}

		if ((port < 1024) || (port > 65535)) {
			throw new LaunchException(
					"Port number must be greater than 1024 and less than 65535.");
		}

		try {
			/* Perform a quick check to ensure that the server is available. */
			final Socket socket = new Socket(hostname, port);
			socket.close();

		} catch (final IOException ex) {
			throw new LaunchException("Server not found.");
		}

		this.hostname = hostname;
		this.port = port;
	}

	/**
	 * Sends the {@code ServiceRequest} to the server to be executed and returns
	 * the result. The result can take any form; including {@code null} and an
	 * exception.
	 * 
	 * @param request
	 *            {@code ServiceRequest} to send to the server to be executed.
	 * 
	 * @return The return value of {@code execute} on {@code request}. This
	 *         value could be an exception.
	 * 
	 * @throws ServicesException
	 *             If there is a problem communicating with the server.
	 */
	private Object execute(final ServiceRequest request)
			throws ServicesException {
		Socket socket = null;
		try {
			socket = new Socket(hostname, port);

			final ObjectOutputStream oos =
					new ObjectOutputStream(socket.getOutputStream());

			final ObjectInputStream ois =
					new ObjectInputStream(socket.getInputStream());

			/* Send the request to the server to be executed. */
			oos.writeObject(request);

			/* Read the results. */
			return ois.readObject();

		} catch (final Exception ex) {
			/*
			 * The most likely reason for the exception to occur would be due to
			 * the server not being available.
			 */
			String err = "Error communicating with the server.\n";
			err += "Please check that the server is still running.";
			throw new ServicesException(err);

		} finally {
			try {
				socket.close();
			} catch (final Exception ignore) {
				/*
				 * There is little we can do here; we have either already ready
				 * the results or an exception has been thrown. In either case
				 * there is no need to supercede either situation so we should
				 * just ignore it.
				 */
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This operation will be executed on the server.
	 * 
	 * @throws ServicesException
	 *             If there is a problem communicating with the server or the
	 *             server cannot satisfy the request.
	 */
	@Override
	public void bookRoom(final HotelRoom hotelRoom, final String id)
			throws HotelRoomNotFoundException, ServicesException,
			IllegalArgumentException {
		final ServiceRequest request =
				ServiceFactory.getBookService(hotelRoom, id);
		final Object results = execute(request);

		if (results instanceof Exception) {
			if (results instanceof HotelRoomNotFoundException) {
				throw (HotelRoomNotFoundException) results;
			}

			if (results instanceof ServicesException) {
				throw (ServicesException) results;
			}

			if (results instanceof IllegalArgumentException) {
				throw (IllegalArgumentException) results;
			}

			throw (RuntimeException) results;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This operation will be executed on the server.
	 * 
	 * @throws ServicesException
	 *             If there is a problem communicating with the server or the
	 *             server cannot satisfy the request.
	 */
	@Override
	public HotelRoom[] getHotelRooms() throws ServicesException {
		return find(new HotelRoomCriteria());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This operation will be executed on the server.
	 * 
	 * @throws ServicesException
	 *             If there is a problem communicating with the server or the
	 *             server cannot satisfy the request.
	 */
	@Override
	public HotelRoom[] find(final HotelRoomCriteria criteria)
			throws ServicesException, IllegalArgumentException {
		final ServiceRequest request = ServiceFactory.getFindRequest(criteria);
		final Object results = execute(request);

		if (results instanceof ServicesException) {
			throw (ServicesException) results;
		}

		if (results instanceof IllegalArgumentException) {
			throw (IllegalArgumentException) results;
		}

		return (HotelRoom[]) results;

	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This operation will be executed on the server.
	 * 
	 * @throws ServicesException
	 *             If there is a problem communicating with the server or the
	 *             server cannot satisfy the request.
	 */
	@Override
	public void save() throws ServicesException {
		final ServiceRequest request = ServiceFactory.getSaveRequest();
		final Object results = execute(request);

		if (results instanceof ServicesException) {
			throw (ServicesException) results;
		}
	}
}
