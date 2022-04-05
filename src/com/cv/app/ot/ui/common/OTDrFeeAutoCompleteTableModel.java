/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.opd.database.entity.Doctor;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class OTDrFeeAutoCompleteTableModel extends AbstractTableModel{
    private final List<Doctor> listDoctor;
    private final String[] columnNames = {"Code", "Doctor Name"};
    
    public OTDrFeeAutoCompleteTableModel(List<Doctor> listDoctor){
        this.listDoctor = listDoctor;
    }
    
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
        switch(column){
            case 0: //Code
                return String.class;
            case 1: //Doctor Name
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Doctor record = listDoctor.get(row);

        switch (column) {
            case 0: //Code
                return record.getDoctorId();
            case 1: //Doctor Name
                return record.getDoctorName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listDoctor.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public Doctor getDoctor(int row){
        return listDoctor.get(row);
    }
    
    public int getSize(){
        return listDoctor.size();
    }
}
