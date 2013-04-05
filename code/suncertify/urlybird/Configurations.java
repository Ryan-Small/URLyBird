/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.urlybird;

import java.io.*;
import java.util.Properties;

/**
 * Provides access to the user's saved configurations for the URLyBird
 * application. This class ensures that these configurations will persist
 * between sessions.
 * 
 * <p>
 * If no saved configurations are available default values will be provided. The
 * default values will be used until specific values are explicitly set.
 * 
 * @author rsmall
 */
public class Configurations {

	/** Path to the file on disk containing the application properties. */
	private static final String PROPERTIES_PATH = "./suncertify.properties";

	/** File to use when loading and storing the {@code properties}. */
	private static final File PROPERTIES_FILE = new File(PROPERTIES_PATH);

	/** Description to identify the properties file. */
	private static final String DESCRIPTION = "URLyBird Properties";

	/** Data structure for storing the properties. */
	private final Properties properties = new Properties();

	/**
	 * Identifies the available configurations for the application.
	 */
	public enum Configuration {

		/*
		 * Adding a new property is as simple as creating a new enum here. The
		 * new property will automatically be added to the file when it is
		 * created.
		 */

		/** Path to the physical file on disk containing the records. */
		DATABASE_PATH(""),

		/** IP address that the server is running on. */
		SERVER_IP_ADDRESS("localhost"),

		/** Port number that the server is listening on for client connections */
		SERVER_PORT("1142");

		/** Default value for the property. */
		private final String defaultValue;

		/**
		 * Constructs a new {@code Configuration} that will be accessible to the
		 * application.
		 * 
		 * @param defaultValue
		 *            Default value to use until a new value is specified.
		 */
		private Configuration(final String defaultValue) {
			// this.key = key;
			this.defaultValue = defaultValue;
		}
	}

	/**
	 * Loads the application specific configurations. If the configurations are
	 * not available a default set of values will be provided.
	 */
	public Configurations() {
		load();
	}

	/**
	 * Loads {@code properties} with the values stored in the
	 * {@code PROPERTIES_FILE}.
	 * 
	 * <p>
	 * The default values will be used if the {@code PROPERTIES_FILE} cannot be
	 * loaded.
	 */
	private void load() {

		/* Synchronize to prevent multiple access points to the file. */
		synchronized (PROPERTIES_FILE) {
			try {
				final FileInputStream fis =
						new FileInputStream(PROPERTIES_FILE);

				properties.load(fis);
				fis.close();

			} catch (final IOException ex) {
				/*
				 * File most likely doesn't exist, but the file may be corrupt
				 * or not formatted correctly due to someone manually editing
				 * the file. In either case, the default value will be provided
				 * if the property isn't loaded properly. So there isn't much we
				 * need to worry about here.
				 */
			}
		}
	}

	/**
	 * Saves the application's configurations. Failure to invoke this method
	 * after setting a configuration will cause the new value not to be saved.
	 * This means that the original value will be loaded in the next session.
	 * 
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public void save() throws IOException {

		/* Synchronize to prevent multiple access points to the file. */
		synchronized (PROPERTIES_FILE) {
			final OutputStream os = new FileOutputStream(PROPERTIES_FILE);
			properties.store(os, DESCRIPTION);
			os.close();
		}
	}

	/**
	 * Returns the value of the {@code Configuration}. If for some reason a
	 * value cannot be found, a default value will be supplied. This means that
	 * {@code null} will never be returned, unless the default value is
	 * explicitly specified as {@code null}.
	 * 
	 * @param property
	 *            Identifies which {@code Property} to get.
	 * 
	 * @return Value of {@code property}.
	 */
	public String get(final Configuration property) {
		return properties.getProperty(property.toString(),
				property.defaultValue);
	}

	/**
	 * Sets the value of {@code property} to {@code value}. {@code null} is a
	 * valid value.
	 * 
	 * @param property
	 *            Identifies which {@code Property} to set.
	 * 
	 * @param value
	 *            New value to be associated with {@code property}.
	 */
	public void set(final Configuration property, final String value) {
		properties.setProperty(property.toString(), value);
	}
}
