/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.inpatient.database.entity.RBooking;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class RoomBookingSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(RoomBookingSearchTableModel.class.getName());
    private List<RBooking> listRBooking = new ArrayList();
    private final String[] columnNames = {"Date", "Name", "Remark", "Contact No.", "Room"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listRBooking == null) {
            return null;
        }

        if (listRBooking.isEmpty()) {
            return null;
        }

        try {
            RBooking record = listRBooking.get(row);

            switch (column) {
                case 0: //Date
                    return record.getBookingDate();
                case 1: //Name
                    return record.getBookingName();
                case 2: //Remark
                    return record.getBookingRemark();
                case 3: //ContactNo
                    return record.getBookingContact();
                case 4: //Room
                    if (record.getBuildingStructure() != null) {
                        return record.getBuildingStructure().getDescription();
                    } else {
                        return null;
                    }
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listRBooking != null) {
            return listRBooking.size();
        } else {
            return 0;
        }

    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<RBooking> getListRoomBooking() {
        return listRBooking;
    }

    public RBooking getRoomBooking(int row) {
        if(listRBooking != null){
            if(!listRBooking.isEmpty()){
                return listRBooking.get(row);
            }
        }
        return null;
    }

    public void setListBooking(List<RBooking> listBookings) {
        this.listRBooking = listBookings;
        fireTableDataChanged();
    }
}
