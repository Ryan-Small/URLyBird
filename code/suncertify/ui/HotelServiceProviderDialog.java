/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.ui;

import javax.swing.JDialog;

import suncertify.service.HotelServices;

/**
 * Represents a graphical user interface that will allow a user to modify the
 * configurations necessary for providing an implementation of
 * {@code HotelServices}.
 * 
 * <p>
 * Closing the graphical user interface will dispose of it.
 * 
 * @author rsmall
 */
abstract class HotelServiceProviderDialog extends JDialog {
	private static final long serialVersionUID = -5975077666822481726L;

	/**
	 * Constructs a new {@code HotelServiceProviderDialog}.
	 */
	public HotelServiceProviderDialog() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * Returns an implementation of the {@code HotelServices}.
	 * 
	 * @return Implementation of {@code HotelServices}.
	 * 
	 * @throws LaunchException
	 *             If there was a problem constructing the implementation of
	 *             {@code HotelServices}.
	 */
	protected abstract HotelServices getServices() throws LaunchException;

	/**
	 * Saves the configurations used to construct the implementation of
	 * {@code HotelServices} when {@code getServices} is invoked, possibly
	 * allowing the implementing class to retrieve the values during
	 * construction.
	 */
	protected abstract void saveConfigurations();
}
