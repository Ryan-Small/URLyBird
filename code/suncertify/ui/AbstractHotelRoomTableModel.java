/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.ui;

import javax.swing.table.AbstractTableModel;

import suncertify.service.HotelRoom;

/**
 * Allows the underlying data associated with a {@code TableModelEvent} to be
 * accessed and updated more directly; as opposed to casting to and from
 * {@code Object}.
 * 
 * @author rsmall
 */
abstract class AbstractHotelRoomTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -3559746061629945389L;

	/**
	 * Returns the {@code HotelRoom} identified by {@code index}. {@code index}
	 * corresponds to the row that the {@code HotelRoom} is displayed on within
	 * the table.
	 * 
	 * @param index
	 *            Index of the {@code HotelRoom} to retrieve.
	 * 
	 * @return {@code HotelRoom} identified by {@code index}.
	 */
	public abstract HotelRoom getHotelRoom(final int index);

	/**
	 * Sets {@code hotelRooms} to be displayed by the table model. Any listeners
	 * will be notified that the cell values in the table's rows may have been
	 * changed along with the number of rows.
	 * 
	 * @param hotelRooms
	 *            New data to be used by the model.
	 */
	public abstract void setHotelRooms(final HotelRoom[] hotelRooms);
}
