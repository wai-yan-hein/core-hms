/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.opd.database.entity.Doctor;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class DoctorAutoCompleteTableModel extends AbstractTableModel{
    
    static Logger log = Logger.getLogger(DoctorAutoCompleteTableModel.class.getName());
    private List<Doctor> listDoctor;
    private final String[] columnNames = {"Code", "Name"};
    
    public DoctorAutoCompleteTableModel(List<Doctor> listDoctor){
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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(listDoctor == null){
            return null;
        }
        
        if(listDoctor.isEmpty()){
            return null;
        }
        
        try{
        Doctor doctor = listDoctor.get(row);

        switch (column) {
            case 0: //Code
                return doctor.getDoctorId();
            case 1: //Name
                return doctor.getDoctorName();
            default:
                return null;
        }
        } catch (Exception ex){
            log.error("getValueAt : " + ex.getMessage());
        }
        
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if(listDoctor == null){
            return 0;
        }
        return listDoctor.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public Doctor getDoctor(int row){
        if(listDoctor == null){
            return null;
        }
        return listDoctor.get(row);
    }
    
    public int getSize(){
        if(listDoctor == null){
            return 0;
        }
        return listDoctor.size();
    }
}
