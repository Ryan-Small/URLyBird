/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;

/**
 * Action listener that, when invoked, will set a text component's text property
 * to the absolute path of the user's selected database file. A custom filter
 * will be used to ensure that the user is only able to select a valid database
 * file, one that ends with the 'db' extension.
 * 
 * @author rsmall
 */
class DatabaseBrowseListener implements ActionListener {

	/** Field to update when a user selects a valid database file. */
	private final JTextComponent jTextComponent;

	/**
	 * Constructs the listener with the text component that will have its text
	 * property set to the path of the user's selected database file when
	 * {@code actionPerformed} is invoked.
	 * 
	 * @param jTextComponent
	 *            Component whose text property will be set to the user's
	 *            selected database file.
	 */
	public DatabaseBrowseListener(final JTextComponent jTextComponent) {
		this.jTextComponent = jTextComponent;
	}

	/**
	 * Prompts the user to select a database file. If the user selects a valid
	 * database file, the {@code JTextComponent} provided during construction
	 * will have its text property set to the absolute path of the selected
	 * file. If the user cancels or closes the prompt, no further action will
	 * transpire.
	 * 
	 * @param e
	 *            Event that triggered this method invocation.
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		final String directory = System.getProperty("user.dir");
		final JFileChooser chooser = new JFileChooser(directory);
		chooser.setFileFilter(new DatabaseFilter());

		final Component parent = jTextComponent;
		final int returnResult = chooser.showOpenDialog(parent);
		if (returnResult == JFileChooser.APPROVE_OPTION) {
			final String source = chooser.getSelectedFile().getAbsolutePath();
			jTextComponent.setText(source);
		}
	}

	/**
	 * Filter for displaying/accepting only database files.
	 */
	private class DatabaseFilter extends FileFilter {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean accept(final File f) {
			if (f.isFile()) {
				return f.getName().endsWith(".db");
			} else {
				return true;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getDescription() {
			return "Database file (*.db)";
		}
	}
}
