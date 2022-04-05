/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.common;

import com.cv.app.pharmacy.database.entity.Appuser;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class AppuserTableModel extends AbstractTableModel {

    private List<Appuser> listAppuser = new ArrayList();
    private String[] columnNames = {"Name", "S-Name", "Active"};

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
        if(column == 2){
            return Boolean.class;
        }else{
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Appuser appuser = listAppuser.get(row);

        switch (column) {
            case 0: //Name
                return appuser.getUserName();
            case 1: //Short Name
                return appuser.getUserShortName();
            case 2: //Active
                return appuser.getActive();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listAppuser.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public List<Appuser> getListAppuser() {
        return listAppuser;
    }

    public void setListAppuser(List<Appuser> listAppuser) {
        this.listAppuser = listAppuser;
        fireTableDataChanged();
    }
    
    public Appuser getAppuser(int row){
        return listAppuser.get(row);
    }
    
    public void setAppuser(int row, Appuser appuser){
        listAppuser.set(row, appuser);
        fireTableRowsUpdated(row, row);
    }
    
    public void addAppuser(Appuser appuser){
        listAppuser.add(appuser);
        fireTableRowsInserted(listAppuser.size()-1, listAppuser.size()-1);
    }
}
