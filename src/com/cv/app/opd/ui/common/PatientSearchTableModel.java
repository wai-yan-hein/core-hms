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
public class PatientSearchTableModel extends AbstractTableModel{
    private List<Patient> listPatient = new ArrayList();
    private String[] columnNames = {"Reg-No", "Name", "Adm-No","Father Name", "City"};
    
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
        Patient record = listPatient.get(row);

        switch (column) {
            case 0: //Reg-No
                return record.getRegNo();
            case 1: //Name
                return record.getPatientName();
            case 2: //Adm-No
                return record.getAdmissionNo();
            case 3: //Father Name
                return record.getFatherName();
            case 4: //City
                if(record.getCity() != null){
                    return record.getCity().getCityName();
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
    
    public Patient getPatient(int row){
        return listPatient.get(row);
    }
}
