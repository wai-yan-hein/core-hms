/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.Gender;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class GenderTableModel extends AbstractTableModel{
    private List<Gender> listGender = new ArrayList();
    private String[] columnNames = {"Gender"};
    
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
        Gender record = listGender.get(row);

        switch (column) {
            case 0: //Name
                return record.getDescription();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listGender.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Gender> getListGender() {
        return listGender;
    }

    public void setListGender(List<Gender> listGender) {
        this.listGender = listGender;
        fireTableDataChanged();
    }
    
    public Gender getGender(int row){
        return listGender.get(row);
    }
    
    public void setGender(int row, Gender gender){
        listGender.set(row, gender);
        fireTableRowsUpdated(row, row);
    }
    
    public void addGender(Gender gender){
        listGender.add(gender);
        fireTableRowsInserted(listGender.size()-1, listGender.size()-1);
    }
    
    public void deleteGender(int row){
        listGender.remove(row);
        fireTableRowsDeleted(0, listGender.size()-1);
    }
}
