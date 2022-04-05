/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.Doctor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class DoctorTableModel extends AbstractTableModel{
    private List<Doctor> listDoctor = new ArrayList();
    private String[] columnNames = {"ID", "Initial", "Name", "Active"};
    
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
        if(column == 3){
            return Boolean.class;
        }else{
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Doctor record = listDoctor.get(row);

        switch (column) {
            case 0: //ID
                return record.getDoctorId();
            case 1: //Initial
                if (record.getInitialID() != null) {
                    return record.getInitialID().getInitialName();
                }
            case 2: //Name
                String strTmp = record.getDoctorName();
                return strTmp;
            case 3: //Active
                return record.isActive();
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

    public List<Doctor> getListDoctor() {
        return listDoctor;
    }

    public void setListDoctor(List<Doctor> listDoctor) {
        this.listDoctor = listDoctor;
        fireTableDataChanged();
    }
    
    public Doctor getDoctor(int row){
        return listDoctor.get(row);
    }
    
    public void setDoctor(int row, Doctor doctor){
        listDoctor.set(row, doctor);
        fireTableRowsUpdated(row, row);
    }
    
    public void addDoctor(Doctor doctor){
        listDoctor.add(doctor);
        fireTableRowsInserted(listDoctor.size()-1, listDoctor.size()-1);
    }
    
    public void deleteDoctor(int row){
        listDoctor.remove(row);
        fireTableRowsDeleted(0, listDoctor.size()-1);
    }
}
