/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import suncertify.service.HotelServices;

/**
 * Action listener that, when invoked, will launch the {@code MainWindow} using
 * the {@code HotelServices} provided by a {@code HotelServiceProviderDialog}.
 * 
 * @author rsmall
 */
class OkButtonListener implements ActionListener {

	/**
	 * Provides the {@code HotelServices} that will be used to launch the
	 * {@code MainWindow}.
	 */
	private final HotelServiceProviderDialog serviceProvider;

	/**
	 * Constructs the listener with the {@code HotelServiceProviderDialog} that
	 * will supply the {@code HotelServices} used for constructing the
	 * {@code MainWindow} when {@code actionPerformed} is invoked.
	 * 
	 * @param serviceProvider
	 *            Provides the {@code HotelServices} that will be used to launch
	 *            the {@code MainWindow}.
	 */
	public OkButtonListener(final HotelServiceProviderDialog serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	/**
	 * Displays a new instance of {@code MainWindow} using the
	 * {@code HotelServices} provided by the {@code HotelServiceProviderDialog}
	 * supplied during construction. The same {@code HotelServiceProviderDialog}
	 * will have its {@code saveProperties} invoked and will be disposed of
	 * prior to displaying the {@code MainWindow}.
	 * 
	 * @param event
	 *            Event that triggered this method invocation.
	 */
	@Override
	public void actionPerformed(final ActionEvent event) {
		try {
			final HotelServices services = serviceProvider.getServices();
			final MainWindow mainWindow = new MainWindow(services);

			serviceProvider.saveConfigurations();
			serviceProvider.dispose();

			mainWindow.setVisible(true);

		} catch (final LaunchException ex) {
			JOptionPane.showMessageDialog(serviceProvider, ex.getMessage());
		}
	}
}
