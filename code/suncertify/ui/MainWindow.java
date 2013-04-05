/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import suncertify.service.*;
import suncertify.service.HotelRoom.Field;

/**
 * Allows the user to search and book hotel rooms by providing a graphical user
 * interface. This GUI features a table in which to display the hotel rooms.
 * These rooms can be searched by specifying criteria to eliminate undesired
 * rooms. When a desired room is found it can be booked.
 * 
 * @author rsmall
 */
class MainWindow extends JFrame {
	private static final long serialVersionUID = 5339255402188211422L;

	/** Table model used to display the data in the window's table. */
	private final AbstractHotelRoomTableModel model =
			new DefaultHotelRoomTableModel();

	/** Name of the hotel when performing a search. */
	private final JTextField jTextFieldName = new JTextField();

	/** Location of the hotel when performing a search. */
	private final JTextField jTextFieldLocation = new JTextField();

	/** Implementation to use when performing database manipulations. */
	private final HotelServices services;

	/**
	 * Constructs a new {@code MainWindow} using the {@code HotelServices}
	 * provided to search and book HotelRooms.
	 * 
	 * @param services
	 *            Implementation to use when performing database manipulations.
	 */
	public MainWindow(final HotelServices services) {
		this.services = services;
		initComponents();
	}

	/**
	 * Prompts the user with a message.
	 * 
	 * @param message
	 *            Message to display to the user.
	 */
	private void displayMessage(final String message) {
		JOptionPane.showMessageDialog(MainWindow.this, message);
	}

	/**
	 * Returns every {@code HotelRoom} available. A {@code HotelRoom} that has
	 * been deleted is not considered available.
	 * 
	 * <p>
	 * If a {@code ServicesException} is thrown while attempting to retrieve the
	 * information, a message will be displayed to the user indicating the issue
	 * and a zero-length array will be returned.
	 * 
	 * @return Every {@code HotelRoom} available.
	 */
	private HotelRoom[] getAllHotelRooms() {
		try {
			return services.getHotelRooms();

		} catch (final ServicesException ex) {
			displayMessage(ex.getMessage());
			return new HotelRoom[0];
		}
	}

	/**
	 * Preforms a search using the values provided by
	 * {@code jTextFieldName.getText} and {@code jTextFieldLocation.getText} as
	 * the name and location, respectively. The results of the search will be
	 * displayed in the table.
	 * 
	 * <p>
	 * If a {@code ServicesException} is thrown while attempting to retrieve the
	 * information, a message will be displayed to the user indicating the
	 * issue.
	 */
	private void doSearch() {
		try {
			final HotelRoomCriteria criteria = new HotelRoomCriteria();

			final String name = jTextFieldName.getText();
			criteria.name = name.equals("") ? null : name;

			final String location = jTextFieldLocation.getText();
			criteria.location = location.equals("") ? null : location;

			final HotelRoom[] rooms = services.find(criteria);
			model.setHotelRooms(rooms);

		} catch (final ServicesException ex) {
			displayMessage(ex.getMessage());
		}
	}

	/**
	 * Listens for an ENTER key to be released, which will result in a search
	 * being preformed, updating the table with the records that match the
	 * search criteria.
	 */
	private class SearchOnEnter extends KeyAdapter {

		/**
		 * If the ENTER key is released, then a search will be preformed,
		 * updating the table with the records that match the search criteria.
		 * 
		 * @param e
		 *            Event that triggered this method invocation.
		 */
		@Override
		public void keyReleased(final KeyEvent e) {
			super.keyTyped(e);
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				doSearch();
			}
		}
	}

	/**
	 * Initializes the components and adds them to the window.
	 * 
	 * <p>
	 * The end result is essentially a panel with two text fields, each with
	 * their own label. The left field is for the hotel name and the right field
	 * is for the hotel location. The user can invoke a search based on the
	 * criteria provided in the fields in one of three ways; the user can use
	 * the button located to the right of the fields, push enter while one of
	 * the fields has focus or use the mnemonic key 's'. Below the panel
	 * containing the search components is a table that displays all of the
	 * HotelRooms. Below the table is a button that will allow the user to book
	 * the record selected in the table. This button can also be activated using
	 * the mnemonic key 'b'.
	 */
	private void initComponents() {

		setTitle("URLyBird");
		setLayout(new GridBagLayout());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		/*
		 * If the window is being closed, then invoke {@code services.save}. If
		 * a {@code ServicesException} is thrown then display the message to the
		 * user and continue closing; this may result in lost data if this
		 * happens.
		 */
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				try {
					services.save();
				} catch (final ServicesException ex) {
					displayMessage(ex.getMessage());
				}
				super.windowClosing(e);
			}
		});

		GridBagConstraints gridBagConstraints;

		final JPanel jPanelSearch = new JPanel();
		jPanelSearch.setLayout(new GridBagLayout());
		jPanelSearch.setBorder(BorderFactory.createTitledBorder("Search"));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.ipadx = 500;
		gridBagConstraints.insets = new Insets(15, 15, 0, 15);
		getContentPane().add(jPanelSearch, gridBagConstraints);

		final JLabel jLabelName = new JLabel("Name:");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.insets = new Insets(15, 15, 15, 0);
		jPanelSearch.add(jLabelName, gridBagConstraints);

		jTextFieldName.addKeyListener(new SearchOnEnter());
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(15, 5, 15, 0);
		jPanelSearch.add(jTextFieldName, gridBagConstraints);

		final JLabel jLabelLocation = new JLabel("Location:");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.insets = new Insets(15, 15, 15, 0);
		jPanelSearch.add(jLabelLocation, gridBagConstraints);

		jTextFieldLocation.addKeyListener(new SearchOnEnter());
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.insets = new Insets(15, 5, 15, 0);
		jPanelSearch.add(jTextFieldLocation, gridBagConstraints);

		final JButton jButtonClear = new JButton("Search");
		jButtonClear.setMnemonic(KeyEvent.VK_R);
		jButtonClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				doSearch();
			}
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 4;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.insets = new Insets(15, 15, 15, 15);
		jPanelSearch.add(jButtonClear, gridBagConstraints);

		final JTable jTableHotelRooms = new JTable();
		jTableHotelRooms.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jTableHotelRooms.setModel(model);
		model.setHotelRooms(getAllHotelRooms());

		final JScrollPane jScrollPaneHotelRooms = new JScrollPane();
		jScrollPaneHotelRooms.setViewportView(jTableHotelRooms);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.ipadx = 500;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.insets = new Insets(15, 15, 0, 15);
		getContentPane().add(jScrollPaneHotelRooms, gridBagConstraints);

		final JButton jButtonBook = new JButton("Book Selected Room");
		jButtonBook.setMnemonic(KeyEvent.VK_B);
		jButtonBook.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					final int index = jTableHotelRooms.getSelectedRow();

					/* If no HotelRoom is selected, tell the user and return. */
					if (index == -1) {
						displayMessage("No room is selected.");
						return;
					}
					final HotelRoom room = model.getHotelRoom(index);

					/*
					 * If the room is already booked, tell the user and return.
					 * A check will also be made on the server side, but this
					 * catch the issue a bit sooner and prevent the user from
					 * having to provide an ID.
					 */
					if (!room.getField(Field.CUSTOMER).equals("")) {
						displayMessage("Room is already booked.");
						return;
					}

					/* Request the ID of the customer booking the room. */
					final String msg = "Customer ID:";
					final String customerId = JOptionPane.showInputDialog(msg);

					/* If an ID was provided, book the room. */
					if (customerId != null) {
						services.bookRoom(room, customerId);
					}

				} catch (final Exception ex) {
					displayMessage(ex.getMessage());
				}

				/*
				 * This search will update the table, potentially displaying the
				 * customer ID regardless if it was booked by this client or
				 * another client. Without this, the user may see a message
				 * indicating that the room is booked, but the table won't
				 * reflect this.
				 */
				doSearch();
			}
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.insets = new Insets(15, 15, 15, 15);
		getContentPane().add(jButtonBook, gridBagConstraints);

		pack();
		setMinimumSize(new Dimension(600, 400));

		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		final Point centerScreen = new Point();
		centerScreen.x = (int) ((d.getWidth() - getWidth()) / 2);
		centerScreen.y = (int) ((d.getHeight() - getHeight()) / 2);
		setLocation(centerScreen);
	}
}
