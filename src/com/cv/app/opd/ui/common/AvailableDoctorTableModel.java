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
public class AvailableDoctorTableModel extends AbstractTableModel{
    private List<Doctor> listDoctor = new ArrayList();
    private final String[] columnNames = {"ID", "Name", "Speciality"};
    
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
        Doctor record = listDoctor.get(row);

        switch (column) {
            case 0: //ID
                return record.getDoctorId();
            case 1: //Name
                if(record.getInitialID() != null){
                    /*return record.getInitialID().getInitialName() +
                        record.getDoctorName();*/
                    return record.getDoctorName();
                }
                else{
                    return record.getDoctorName();
                }
            case 2: //Speciality
              if(record.getSpeciality() != null){
                return record.getSpeciality().getDesp();
              }else{
                return null;
              }
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
