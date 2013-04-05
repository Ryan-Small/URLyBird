/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.ui;

import static suncertify.urlybird.Configurations.Configuration.DATABASE_PATH;
import static suncertify.urlybird.Configurations.Configuration.SERVER_PORT;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import suncertify.service.ServicesException;
import suncertify.service.socket.HotelServer;
import suncertify.service.socket.Server;
import suncertify.urlybird.Configurations;

/**
 * Allows a user to customize the database path and port number the server is
 * listening on by providing a graphical user interface. The server will be
 * started upon successful setup. If setup fails, a message will be presented to
 * the user to indicate the problem and the user will be allowed to modify the
 * configurations.
 * 
 * @author rsmall
 */
public class ServerSetupWindow extends JFrame {
	private static final long serialVersionUID = 8772846139259590350L;

	/** Path to the database file. */
	private final JTextField jTextFieldDatabasePath = new JTextField();

	/** Port number that the client can use to connect to the server. */
	private final JTextField jTextFieldServerPort = new JTextField();

	/** Allows the user to browse for the database file. */
	private final JButton jButtonBrowse = new JButton("Browse...");

	/** Starts the server. */
	private final JButton jButtonStartServer = new JButton("Start Server");

	/** Stops the server. */
	private final JButton jButtonStopServer = new JButton("Stop Server");

	/** Provides the default values for configuring the service. */
	private final Configurations configurations;

	private Server server;

	/**
	 * Constructs a new {@code ServerSetupWindow} using the values supplied by
	 * {@code properties} as the default values for the database path and server
	 * port. {@code properties} will be updated to reflect the final values
	 * accepted by the user.
	 * 
	 * @param properties
	 *            Provides the default values for configuring the service.
	 */
	public ServerSetupWindow(final Configurations properties) {
		configurations = properties;
		initComponents();
	}

	/**
	 * Enables or disables the user manipulated controls on the window.
	 * 
	 * @param enabled
	 *            {@code true} if the user manipulated controls should be
	 *            enabled; {@code false} otherwise.
	 */
	private void setRunning(final boolean running) {
		jTextFieldDatabasePath.setEnabled(!running);
		jTextFieldServerPort.setEnabled(!running);
		jButtonBrowse.setEnabled(!running);
		jButtonStartServer.setEnabled(!running);
		jButtonStopServer.setEnabled(running);
	}

	/**
	 * Updates the {@code properties} provided during construction with the
	 * values that were used to configure the server.
	 * 
	 * <p>
	 * This will save the path to the database and port number used back to the
	 * {@code ApplicationProperties} that were provided during construction of
	 * this class.
	 */
	private void saveConfigurations() {
		try {
			final String databasePath = jTextFieldDatabasePath.getText();
			final String serverPort = jTextFieldServerPort.getText();

			configurations.set(DATABASE_PATH, databasePath);
			configurations.set(SERVER_PORT, serverPort);
			configurations.save();

		} catch (final IOException ex) {
			displayMessage("Error saving properties to file.");
		}
	}

	/**
	 * Prompts the user with a message.
	 * 
	 * @param message
	 *            Message to be displayed.
	 */
	private void displayMessage(final String message) {
		JOptionPane.showMessageDialog(ServerSetupWindow.this, message);
	}

	/**
	 * Initializes the components and adds them to the window.
	 * 
	 * <p>
	 * The end result is essentially two text fields on top of one another with
	 * a label to their left. The top field is for the path to the database and
	 * the bottom field is for the port number. To the right of the database
	 * field is a button that will prompt the user to search for the database
	 * file. Upon finding the file, the field will be populated with the path.
	 * The two fields will automatically be populated with the IP address and
	 * port number supplied by the {@code applicationProperties} provided during
	 * construction of this class. Underneath these fields is a button that will
	 * launch the {@code MainWindow}. This button is also the default button,
	 * meaning that the enter key will activate it.
	 */
	private void initComponents() {

		setTitle("Server Configurations");
		setLayout(new BorderLayout());
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		/*
		 * Stop the server. This type of server will automatically save the
		 * changes when it is stopped.
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				if (server.isRunning()) {
					try {
						server.stop();
					} catch (final ServicesException ex) {
						displayMessage("Changes could not be saved.");
					}
				}
			}
		});

		GridBagConstraints gridBagConstraints;

		final JPanel jPanelConfigurations = new JPanel();
		jPanelConfigurations.setLayout(new GridBagLayout());
		add(jPanelConfigurations, BorderLayout.CENTER);

		final JLabel jLabelSourceLocation = new JLabel("Database Location:");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(15, 15, 0, 0);
		jPanelConfigurations.add(jLabelSourceLocation, gridBagConstraints);

		final String databaseLocation = configurations.get(DATABASE_PATH);
		jTextFieldDatabasePath.setText(databaseLocation);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.ipadx = 254;
		gridBagConstraints.insets = new Insets(15, 5, 0, 0);
		jPanelConfigurations.add(jTextFieldDatabasePath, gridBagConstraints);

		jButtonBrowse.addActionListener(new DatabaseBrowseListener(
				jTextFieldDatabasePath));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 3;
		gridBagConstraints.ipadx = 12;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(10, 5, 0, 15);
		jPanelConfigurations.add(jButtonBrowse, gridBagConstraints);

		final JLabel jLabelServerPort = new JLabel("Server Port:");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new Insets(5, 53, 15, 0);
		jPanelConfigurations.add(jLabelServerPort, gridBagConstraints);

		final String serverPort = configurations.get(SERVER_PORT);
		jTextFieldServerPort.setText(serverPort);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.ipadx = 53;
		gridBagConstraints.insets = new Insets(5, 5, 15, 15);
		jPanelConfigurations.add(jTextFieldServerPort, gridBagConstraints);

		final JPanel jPanelControls = new JPanel();
		jPanelControls.setLayout(new FlowLayout());
		add(jPanelControls, BorderLayout.SOUTH);

		/* Button for starting the server. */
		jButtonStartServer.addActionListener(new ActionListener() {

			/**
			 * Starts the server and updates the values used for configuring the
			 * server in the {@code properties} provided during construction.
			 * 
			 * @param e
			 *            Event that triggered this method invocation.
			 */
			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					setRunning(true);

					final String dbString = jTextFieldDatabasePath.getText();
					final File database = new File(dbString);

					final String portString = jTextFieldServerPort.getText();
					final int port = Integer.parseInt(portString);

					server = new HotelServer(database, port);
					server.start();

					/*
					 * Saving the configurations after the server has been
					 * started will ensure that the configurations are valid.
					 */
					saveConfigurations();

				} catch (final NumberFormatException ex) {
					displayMessage("Port number is invalid.");
					setRunning(false);

				} catch (final Exception ex) {
					displayMessage(ex.getMessage());
					setRunning(false);
				}
			}
		});
		rootPane.setDefaultButton(jButtonStartServer);
		jPanelControls.add(jButtonStartServer);

		/* Button for stopping the server. */
		jButtonStopServer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					server.stop();
				} catch (final ServicesException ex) {
					displayMessage("Changes could not be saved.");
				}
				setRunning(false);
			}
		});
		jButtonStopServer.setEnabled(false);
		jPanelControls.add(jButtonStopServer);

		pack();
	}
}
