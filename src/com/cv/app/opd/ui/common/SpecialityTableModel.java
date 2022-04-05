/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.Speciality;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class SpecialityTableModel extends AbstractTableModel{
    private List<Speciality> listSpeciality = new ArrayList();
    private String[] columnNames = {"Description"};
    
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
        Speciality record = listSpeciality.get(row);

        switch (column) {
            case 0: //Description
                return record.getDesp();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listSpeciality.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Speciality> getListSpeciality() {
        return listSpeciality;
    }

    public void setListSpeciality(List<Speciality> listSpeciality) {
        this.listSpeciality = listSpeciality;
        fireTableDataChanged();
    }
    
    public Speciality getSpeciality(int row){
        return listSpeciality.get(row);
    }
    
    public void setSpeciality(int row, Speciality speciality){
        listSpeciality.set(row, speciality);
        fireTableRowsUpdated(row, row);
    }
    
    public void addSpeciality(Speciality speciality){
        listSpeciality.add(speciality);
        fireTableRowsInserted(listSpeciality.size()-1, listSpeciality.size()-1);
    }
    
    public void deleteSpeciality(int row){
        listSpeciality.remove(row);
        fireTableRowsDeleted(0, listSpeciality.size()-1);
    }
}
