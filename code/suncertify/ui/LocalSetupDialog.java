/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.ui;

import static suncertify.urlybird.Configurations.Configuration.DATABASE_PATH;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import suncertify.service.DefaultHotelServices;
import suncertify.service.HotelServices;
import suncertify.service.ServicesInitializationException;
import suncertify.urlybird.Configurations;
import suncertify.urlybird.Configurations.Configuration;

/**
 * Allows a user to customize the database path by providing a graphical user
 * interface. An instance of {@code MainWindow} will be created and displayed
 * upon successful setup. If setup fails, a message will be presented to the
 * user to indicate the problem and the user will be allowed to modify the
 * configurations.
 * 
 * @author rsmall
 */
public class LocalSetupDialog extends HotelServiceProviderDialog {
	private static final long serialVersionUID = -6460187322465338561L;

	/** Path to the database file. */
	private final JTextField jTextFieldDatabasePath = new JTextField();

	/** Provides the default values for configuring the service. */
	private final Configurations configurations;

	/**
	 * Constructs a new {@code LocalSetupDialog} using the values supplied by
	 * {@code configurations} as the default values for the database path.
	 * 
	 * @param configurations
	 *            Provides the default values for configuring the service.
	 */
	public LocalSetupDialog(final Configurations configurations) {
		this.configurations = configurations;
		initComponents();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected HotelServices getServices() throws LaunchException {
		try {
			final String databasePath = jTextFieldDatabasePath.getText();
			final File database = new File(databasePath);

			return new DefaultHotelServices(database);

		} catch (final ServicesInitializationException ex) {
			throw new LaunchException(ex.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The path to the database will be saved.
	 */
	@Override
	protected void saveConfigurations() {
		try {
			final String databasePath = jTextFieldDatabasePath.getText();

			configurations.set(Configuration.DATABASE_PATH, databasePath);
			configurations.save();

		} catch (final IOException ex) {
			JOptionPane.showMessageDialog(this,
					"Error saving properties to file.");
		}
	}

	/**
	 * Initializes the components and adds them to the window.
	 * 
	 * <p>
	 * The end result is essentially a text field with a label to the left and a
	 * button to the right. The field is for the path to the database and the
	 * button will prompt the user to search for the database file. Upon finding
	 * the file, the field will be populated with the path. The field will
	 * automatically be populated with the database path supplied by the
	 * {@code ApplicationProperties} provided during construction of this class.
	 * Underneath the field is a button that will launch the {@code MainWindow}.
	 * This button is also the default button, meaning that the enter key will
	 * activate it.
	 */
	private void initComponents() {

		setTitle("Local Configurations");
		setLayout(new BorderLayout());
		setResizable(false);
		setModal(true);

		GridBagConstraints gridBagConstraints;

		final JPanel jPanelConfigurations = new JPanel();
		jPanelConfigurations.setLayout(new GridBagLayout());
		add(jPanelConfigurations, BorderLayout.CENTER);

		final JLabel jLabelDatabaseSource = new JLabel("Database Source:");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(15, 15, 15, 0);
		jPanelConfigurations.add(jLabelDatabaseSource, gridBagConstraints);

		final String databaseLocation = configurations.get(DATABASE_PATH);
		jTextFieldDatabasePath.setText(databaseLocation);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.ipadx = 254;
		gridBagConstraints.insets = new Insets(15, 5, 15, 0);
		jPanelConfigurations.add(jTextFieldDatabasePath, gridBagConstraints);

		final JButton jButtonBrowse = new JButton("Browse...");
		jButtonBrowse.addActionListener(new DatabaseBrowseListener(
				jTextFieldDatabasePath));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 3;
		gridBagConstraints.insets = new Insets(10, 5, 15, 15);
		jPanelConfigurations.add(jButtonBrowse, gridBagConstraints);

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
