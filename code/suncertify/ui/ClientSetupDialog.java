/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.ui;

import static suncertify.urlybird.Configurations.Configuration.SERVER_IP_ADDRESS;
import static suncertify.urlybird.Configurations.Configuration.SERVER_PORT;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;

import suncertify.service.HotelServices;
import suncertify.service.socket.HotelClient;
import suncertify.urlybird.Configurations;

/**
 * Allows a user to customize the IP address of the server and the port number
 * the server is listening on by providing a graphical user interface. An
 * instance of {@code MainWindow} will be created and displayed upon successful
 * setup. If setup fails, a message will be presented to the user to indicate
 * the problem and the user will be allowed to modify the configurations.
 * 
 * @author rsmall
 */
public class ClientSetupDialog extends HotelServiceProviderDialog {
	private static final long serialVersionUID = -4539372706331207658L;

	/** IP address that the client can use to connect to the server. */
	private final JTextField jTextFieldServerIPAddress = new JTextField();

	/** Port number that the client can use to connect to the server. */
	private final JTextField jTextFieldServerPort = new JTextField();

	/** Provides the default values for configuring the service. */
	private final Configurations configurations;

	/**
	 * Constructs a new {@code ClientSetupDialog} using the values supplied by
	 * {@code configurations} as the default values for the server IP address
	 * and server port.
	 * 
	 * @param configurations
	 *            Provides the default values for configuring the service.
	 */
	public ClientSetupDialog(final Configurations configurations) {
		this.configurations = configurations;
		initComponents();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected HotelServices getServices() throws LaunchException {
		try {
			final String textAddress = jTextFieldServerIPAddress.getText();
			final InetAddress address = InetAddress.getByName(textAddress);

			final String textPort = jTextFieldServerPort.getText();
			final int port = Integer.parseInt(textPort);

			return new HotelClient(address, port);

		} catch (final UnknownHostException ex) {
			throw new LaunchException("Unable to determine IP address.");

		} catch (final SecurityException ex) {
			throw new LaunchException("Invalid IP address.");

		} catch (final NumberFormatException ex) {
			throw new LaunchException("Invalid port number.");

		} catch (final Exception ex) {
			throw new LaunchException(ex.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The IP address and port number used to connect to the server will be
	 * saved.
	 */
	@Override
	protected void saveConfigurations() {
		try {
			final String serverIPAddress = jTextFieldServerIPAddress.getText();
			final String serverPort = jTextFieldServerPort.getText();

			configurations.set(SERVER_IP_ADDRESS, serverIPAddress);
			configurations.set(SERVER_PORT, serverPort);
			configurations.save();

		} catch (final IOException ex) {
			final String message = "Error saving configurations to file.";
			JOptionPane.showMessageDialog(this, message);
		}
	}

	/**
	 * Initializes the components and adds them to the window.
	 * 
	 * <p>
	 * The end result is essentially two text fields on top of one another with
	 * a label to their left. The top field is for the server IP address and the
	 * bottom field is for the port number. These two fields will automatically
	 * be populated with the IP address and port number supplied by the
	 * {@code ApplicationProperties} provided during construction of this class.
	 * Underneath these fields is a button that will launch the
	 * {@code MainWindow}. This button is also the default button, meaning that
	 * the enter key will activate it.
	 */
	private void initComponents() {

		setTitle("Client Configurations");
		setLayout(new BorderLayout());
		setResizable(false);
		setModal(true);

		GridBagConstraints gridBagConstraints;

		final JPanel jPanelConfigurations = new JPanel();
		jPanelConfigurations.setLayout(new GridBagLayout());
		add(jPanelConfigurations, BorderLayout.CENTER);

		final JLabel jLabelServerAddress = new JLabel("Server IP Address:");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(15, 15, 0, 0);
		jPanelConfigurations.add(jLabelServerAddress, gridBagConstraints);

		final String serverAddress = configurations.get(SERVER_IP_ADDRESS);
		jTextFieldServerIPAddress.setText(serverAddress);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.ipadx = 254;
		gridBagConstraints.insets = new Insets(15, 5, 0, 15);
		jPanelConfigurations.add(jTextFieldServerIPAddress, gridBagConstraints);

		final JLabel jLabelServerPort = new JLabel("Server Port:");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridheight = 3;
		gridBagConstraints.insets = new Insets(5, 53, 15, 0);
		jPanelConfigurations.add(jLabelServerPort, gridBagConstraints);

		final String serverPort = configurations.get(SERVER_PORT);
		jTextFieldServerPort.setText(serverPort);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.ipadx = 55;
		gridBagConstraints.insets = new Insets(5, 5, 15, 0);
		jPanelConfigurations.add(jTextFieldServerPort, gridBagConstraints);

		final JPanel jPanelControls = new JPanel();
		jPanelControls.setLayout(new FlowLayout());
		add(jPanelControls, BorderLayout.SOUTH);

		final JButton jButtonOk = new JButton("Ok");
		jButtonOk.addActionListener(new OkButtonListener(this));
		rootPane.setDefaultButton(jButtonOk);
		jPanelControls.add(jButtonOk);

		pack();
	}
}
