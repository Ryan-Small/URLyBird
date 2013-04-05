/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.service.socket;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import suncertify.service.DefaultHotelServices;
import suncertify.service.HotelServices;
import suncertify.service.ServicesException;
import suncertify.service.ServicesInitializationException;

/**
 * This class is responsible for satisfying {@code ServiceRequest} made by any
 * number of {@code HotelClient} instances. Each time a {@code HotelClient}
 * sends a {@code ServiceRequest}, the {@code HotelServer} will
 * {@link ServiceRequest#execute(HotelServices) execute} it. The result will be
 * sent back to the {@code HotelClient} that sent the {@code ServiceRequest}.
 * 
 * @author rsmall
 */
public class HotelServer implements Server {

	/** Used as a lock to control {@code start} and {@code shutdown} operations. */
	private final Object lock = new Object();

	/** Port number to use when listening for client connections. */
	private final int port;

	/** Handles data access. */
	private final HotelServices services;

	/** Responsible for handling socket connections. */
	private Thread serverThread;

	/**
	 * Constructs a new {@code HotelServer} that will be responsible for
	 * monitoring the {@code port} for client connections. Request made from
	 * these client connections will be transacted against the data supplied by
	 * the data stored in {@code database}. {@code database} should reference an
	 * existing file used for storing the data.
	 * 
	 * @param database
	 *            Path to the physical file on disk that contains the records.
	 *            Cannot be {@code null}.
	 * 
	 * @param port
	 *            Port number to use when listening for client connections. Must
	 *            be between {@code 1024} and {@code 65535}.
	 * 
	 * @throws ServicesInitializationException
	 *             If the given file does not denote an existing, writable
	 *             regular file or if some other error occurs while opening the
	 *             file or if an I/O error occurs while reading the file.
	 * 
	 * @throws IllegalArgumentException
	 *             If {@code database} is {@code null} or {@code port} is not
	 *             between {@code 1024} and {@code 65535}.
	 */
	public HotelServer(final File database, final int port)
			throws ServicesInitializationException {

		if (database == null) {
			throw new IllegalArgumentException("database cannot be null");
		}

		if ((port < 1024) || (port > 65535)) {
			throw new IllegalArgumentException(
					"Port must be between 1024 and 65535.");
		}

		services = new DefaultHotelServices(database);
		this.port = port;
	}

	/**
	 * Starts the process of listening for incoming connections from clients.
	 * 
	 * @throws IllegalStateException
	 *             If the server is currently running.
	 */
	@Override
	public void start() {
		/*
		 * We want to avoid starting the server if it is already in the process
		 * of starting or stopping; thus, we synchronize on the lock to force
		 * any subsequent calls being made to wait until the server is started.
		 */
		synchronized (lock) {
			if ((serverThread == null) || !serverThread.isAlive()) {
				final Runnable socketListener = new SocketListener();
				serverThread = new Thread(socketListener);
				serverThread.start();

			} else {
				throw new IllegalStateException("server already running");
			}
		}
	}

	/**
	 * Stops the process of listening for incoming connections from clients.
	 * This has no effect on connections that have already been established, as
	 * they will be allowed to finish. This method will block until all
	 * remaining clients have been satisfied and the server has been shutdown.
	 * Once shutdown, the changes will be saved.
	 * 
	 * @throws ServicesException
	 *             If the changes could not be saved.
	 * 
	 * @throws IllegalStateException
	 *             If the server is not currently running.
	 */
	@Override
	public void stop() throws ServicesException {
		/*
		 * We want to avoid stopping the server if it is already in the process
		 * of starting or stopping; thus, we synchronize on the lock to force
		 * any subsequent calls being made to wait until the server has stopped.
		 */
		synchronized (lock) {
			if (isRunning()) {

				/*
				 * Interrupt the thread the server is running on and politely
				 * wait until the server finishes handling any requests in
				 * progress before continuing on.
				 */
				serverThread.interrupt();
				while (isRunning()) {
					try {
						serverThread.join();

					} catch (final InterruptedException ignore) {
						/* Join until the thread has finished. */
					}
				}

				/*
				 * Save the changes made during this session in case a client
				 * failed to request it themself.
				 */
				services.save();

			} else {
				throw new IllegalStateException("server is not running");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRunning() {
		return (serverThread != null) && serverThread.isAlive();
	}

	/**
	 * Responsible for handling the connection requests made by clients.
	 */
	private class SocketListener implements Runnable {

		/**
		 * Number of milliseconds to elapse before checking if the server needs
		 * to shutdown. Decrease the value in order to increase response time
		 * when shutting down.
		 */
		private static final int TIMEOUT = 1500;

		/**
		 * Starts the process of listening for incoming connections from
		 * clients. Once a {@code ServiceRequest} has been received by the
		 * {@code socket} it will be executed and the returned value (or
		 * exception) will be sent back to the client using the same
		 * {@code socket} the request was received on as a {@code Results}
		 * object.
		 */
		@Override
		public void run() {
			try {
				final ServerSocket serverSocket = new ServerSocket(port);
				serverSocket.setSoTimeout(TIMEOUT);

				/*
				 * If the thread is interrupted, then we will want to stop
				 * listening for connections and preform any cleanup.
				 */
				while (!Thread.interrupted()) {
					try {
						final Socket socket = serverSocket.accept();
						final Thread socketHandler =
								new Thread(new ServiceRequestHandler(socket));

						socketHandler.start();

					} catch (final SocketTimeoutException ignore) {
						/*
						 * We want to periodically check to see if the thread
						 * has been interrupted, indicating that the server is
						 * attempting to shutdown. If the thread has been
						 * interrupted, then we clean up resources.
						 */
					}
				}
				serverSocket.close();

			} catch (final Exception ignore) {
				/*
				 * For some reason the server has failed to start or maybe stop.
				 * There is nothing we can really do at this point to recover,
				 * so just ignore it.
				 */
			}
		}
	}

	/**
	 * Responsible for handling the {@code ServiceRequest} sent by the client.
	 */
	private class ServiceRequestHandler implements Runnable {

		/** Connection to the client. */
		private final Socket socket;

		/**
		 * Constructs a new {@code ServiceRequestHandler} using the specified
		 * {@code socket} to communicate with the client.
		 * 
		 * @param socket
		 *            {@code Socket} that the {@code ServiceRequest} will be
		 *            sent on. This is the same {@code Socket} that the
		 *            {@code Results} will be returned on.
		 */
		public ServiceRequestHandler(final Socket socket) {
			this.socket = socket;
		}

		/**
		 * Once a {@code ServiceRequest} has been received by the {@code socket}
		 * it will be executed and the returned value (or exception) will be
		 * sent back to the client using the same {@code socket} the request was
		 * received on as a {@code Results} object.
		 */
		@Override
		public void run() {
			try {
				final ObjectInputStream ois =
						new ObjectInputStream(socket.getInputStream());

				final ObjectOutputStream oos =
						new ObjectOutputStream(socket.getOutputStream());

				final ServiceRequest request =
						(ServiceRequest) ois.readObject();

				final Object returnValue = request.execute(services);
				oos.writeObject(returnValue);
				oos.flush();

				/*
				 * The client should only be sending a single request per
				 * connection. At this point in time, we have satisfied that
				 * request and are now safe to close the socket.
				 */
				socket.close();

			} catch (final Exception ignore) {

				/*
				 * If we reach this point, then it means that either a
				 * SocketException occurred because a client disconnected, an
				 * I/O error occurred with the streams or the ServiceRequest
				 * class wasn't found. Since the server and client are bundled
				 * as one, the latter is unlikely; and if the client
				 * disconnected or there is a problem with the socket streams,
				 * then we won't be able to notify the client of the issue
				 * anyways. So, at this point there is little we can do.
				 */
			}
		}
	}
}
