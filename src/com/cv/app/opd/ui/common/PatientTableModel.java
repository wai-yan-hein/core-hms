/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.ot.database.entity.OTDoctorFee;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PatientTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PatientTableModel.class.getName());
    private List<Patient> listPatient = new ArrayList();
    private String[] columnNames = {"Reg No.", "Name", "City"};
    
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
        switch (column) {
            case 0: //Reg No.
                return String.class;
            case 1: //Name
                return String.class;
            case 3: //City
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Patient record = listPatient.get(row);

        switch (column) {
            case 0: //Reg No.
                return record.getRegNo();
            case 1: //Name
                return record.getPatientName();
            case 2: //City
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
}
