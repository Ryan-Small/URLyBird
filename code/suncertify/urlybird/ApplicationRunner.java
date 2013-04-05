/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.urlybird;

import java.awt.*;

import javax.swing.UIManager;

import suncertify.ui.ClientSetupDialog;
import suncertify.ui.LocalSetupDialog;
import suncertify.ui.ServerSetupWindow;

/**
 * Entry point for the URLyBird application. This class is responsible for
 * launching the application in the mode specified by the command-line
 * arguments.
 * 
 * <p>
 * To launch the application, invoke the {@code main} method using the
 * appropriate command-line arguments to specify the mode.
 * 
 * @author rsmall
 */
public class ApplicationRunner {

	/**
	 * User's saved configuration parameters that should be used as the default
	 * values as applicable.
	 */
	private static final Configurations PROPERTIES = new Configurations();

	/**
	 * Launches the application in the mode specified by the command-line
	 * arguments. Only a single mode may be specified at a time and the
	 * following identifies the only acceptable values:
	 * 
	 * <ul>
	 * <li>{@code [no arguments]} - Networked client; this mode will allow the
	 * client to connect to a server application. This mode should be used in
	 * environments where multiple clients will need to access the same data
	 * source. An instance of the server application is required to be
	 * accessible when starting this mode.</li>
	 * 
	 * <li>{@code server} - Server application; this mode will allow multiple
	 * networked clients to access a single data source. This mode should be
	 * used on a machine that all networked clients can access. An instance of
	 * this mode is required to be running prior to starting a networked client.
	 * </li>
	 * 
	 * <li>{@code alone} - Non-networked client; this mode will access the data
	 * source directly. This mode should be used when only a single client will
	 * be accessing the data source or if the data needs to be accessed locally.
	 * </li>
	 * </ul>
	 * 
	 * <p>
	 * Regardless of the mode, a GUI will be displayed in the center of the
	 * screen that will allow the user to configure the appropriate mode.
	 * 
	 * @param args
	 *            Command line arguments.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				setSystemLookAndFeel();
				launchApplication(args);
			}
		});
	}

	/**
	 * Sets the system look-and-feel for the application.
	 */
	private static void setSystemLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		} catch (final Exception ignore) {
			/*
			 * Customizing the look-and-feel is not essential and should not
			 * impact the functionality of the application. A normal user is
			 * unlikely to even know what a look-and-feel is. So, there is no
			 * reason to burden the user with a message indicating the problem.
			 */
		}
	}

	/**
	 * Launches the application in the mode specified by {@code args[0]}. The
	 * following identifies the only acceptable values:
	 * 
	 * <ul>
	 * <li>{@code null} - Networked client; this mode will allow the client to
	 * connect to a server application. This mode should be used in environments
	 * where multiple clients will need to access the same data source. An
	 * instance of the server application is required to be accessible when
	 * starting this mode.</li>
	 * 
	 * <li>{@code server} - Server application; this mode will allow multiple
	 * networked clients to access a single data source. This mode should be
	 * used on a machine that all networked clients can access. An instance of
	 * this mode is required to be running prior to starting a networked client.
	 * </li>
	 * 
	 * <li>{@code alone} - Non-networked client; this mode will access the data
	 * source directly. This mode should be used when only a single client will
	 * be accessing the data source or if the data needs to be accessed locally.
	 * </li>
	 * </ul>
	 * 
	 * <p>
	 * Regardless of the mode, a GUI will be displayed in the center of the
	 * screen that will allow the user to configure the appropriate mode.
	 * 
	 * @param args
	 *            Command line arguments.
	 */
	private static void launchApplication(final String[] args) {
		final String mode = (args.length == 0 ? null : args[0]);

		if (mode == null) {
			final Window gui = new ClientSetupDialog(PROPERTIES);
			display(gui);

		} else if (mode.equalsIgnoreCase("server")) {
			final Window gui = new ServerSetupWindow(PROPERTIES);
			display(gui);

		} else if (mode.equalsIgnoreCase("alone")) {
			final Window gui = new LocalSetupDialog(PROPERTIES);
			display(gui);

		} else {
			System.err.println("Command line options may be one of:");
			System.err.println("[no arguments] - starts networked client");
			System.err.println("\"server\"     - starts server application");
			System.err.println("\"alone\"      - starts non-networked client");
		}
	}

	/**
	 * Displays {@code window} in the center of the screen. {@code window} will
	 * be moved to the center of the screen, both vertically and horizontally,
	 * and then made visible.
	 * 
	 * @param window
	 *            {@code Window} to be displayed.
	 */
	private static void display(final Window window) {
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		final Point centerScreen = new Point();

		centerScreen.x = (int) ((d.getWidth() - window.getWidth()) / 2);
		centerScreen.y = (int) ((d.getHeight() - window.getHeight()) / 2);

		window.setLocation(centerScreen);
		window.setVisible(true);
	}
}