/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.Booking;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class AppoimentDrTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(AppoimentDrTableModel.class.getName());

    private List<Booking> listBk = new ArrayList();
    private final String[] columnNames = {"Doctor Name"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 2 || column == 7;
    }

    @Override
    public Class getColumnClass(int column) {

        switch (column) {
            case 0: //Date
                return String.class;
            default:
                return Object.class;

        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listBk == null) {
            return null;
        }

        if (listBk.isEmpty()) {
            return null;
        }

        Booking booking = listBk.get(row);
        switch (column) {
            case 0:
                return booking.getDoctor().getDoctorName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    @Override
    public int getRowCount() {
        return listBk.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Booking> getListBooking() {
        return listBk;
    }

    public void setListBooking(List<Booking> listBooking) {
        this.listBk = listBooking;
        fireTableDataChanged();
    }

    public Booking getBooking(int row) {
        return listBk.get(row);
    }

}
