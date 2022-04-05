/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.Patient;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class PatientListTableModel extends AbstractTableModel{
    private List<Patient> listPatient = new ArrayList();
    private String[] columnNames = {"ID", "Name", "City"};
    
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
        Patient patient = listPatient.get(row);

        switch (column) {
            case 0: //ID
                return patient.getRegNo();
            case 1: //Name
                return patient.getPatientName();
            case 2: //Township
              if(patient.getCity() != null){
                return patient.getCity().getCityName();
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
        return listPatient.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Patient> getListPatient() {
        return listPatient;
    }

    public void setListPatient(List<Patient> listPatient) {
        this.listPatient = listPatient;
        fireTableDataChanged();
    }
    
    public Patient getPatinet(int row){
        return listPatient.get(row);
    }
}
