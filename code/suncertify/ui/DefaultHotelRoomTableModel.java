/* 
 * Java Developer Assignment 1Z0-855 
 * URLyBird 1.4.0_01
 */
package suncertify.ui;

import suncertify.service.HotelRoom;
import suncertify.service.HotelRoom.Field;

/**
 * Implementation of {@code AbstractHotelRoomTableModel} that will display a
 * {@code HotelRoom} in each row. Each column will identify one of the fields
 * associated with the {@code HotelRoom}.
 * 
 * @author rsmall
 */
class DefaultHotelRoomTableModel extends AbstractHotelRoomTableModel {
	private static final long serialVersionUID = -4110953337828719711L;

	/** Data that will be displayed in this table model. */
	private HotelRoom[] rooms = new HotelRoom[0];

	/**
	 * Constructs the table model with no data which will result in a table of
	 * zero columns and zero rows.
	 */
	public DefaultHotelRoomTableModel() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HotelRoom getHotelRoom(final int index) {
		return rooms[index];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHotelRooms(final HotelRoom[] rooms) {
		this.rooms = rooms;
		fireTableDataChanged();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRowCount() {
		return rooms.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getColumnCount() {
		return Field.values().length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnName(final int columnIndex) {
		for (final Field field : Field.values()) {
			if (field.ordinal() == columnIndex) {
				return field.toString();
			}
		}
		return "Unknown";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		final HotelRoom room = rooms[rowIndex];
		for (final Field field : Field.values()) {
			if (columnIndex == field.ordinal()) {
				return room.getField(field);
			}
		}
		return "Unknown";
	}

	/**
	 * This method is not yet supported. Data can be modified by using
	 * {@link #setHotelRooms(HotelRoom[])}.
	 * 
	 * @param value
	 *            The new value.
	 * 
	 * @param rowIndex
	 *            The row whose value is to be changed.
	 * 
	 * @param columnIndex
	 *            The column whose value is to be changed.
	 */
	@Override
	public void setValueAt(final Object value, final int rowIndex,
			final int columnIndex) {
		throw new UnsupportedOperationException();
	}
}
