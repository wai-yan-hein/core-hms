/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.common;

import com.cv.app.pharmacy.database.entity.UserRole;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class UserRoleTableModel extends AbstractTableModel {

    private List<UserRole> listUserRole = new ArrayList();
    private String[] columnNames = {"Role Name"};

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
        UserRole userRole = listUserRole.get(row);

        switch (column) {
            case 0: //Role Name
                return userRole.getRoleName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listUserRole.size();
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

    public List<UserRole> getListUserRole() {
        return listUserRole;
    }

    public void setListUserRole(List<UserRole> listUserRole) {
        this.listUserRole = listUserRole;
        fireTableDataChanged();
    }
    
    public UserRole getUserRole(int row){
        return listUserRole.get(row);
    }
    
    public void setUserRole(int row, UserRole userRole){
        listUserRole.set(row, userRole);
        fireTableRowsUpdated(row, row);
    }
    
    public void addUserRole(UserRole userRole){
        listUserRole.add(userRole);
        fireTableRowsInserted(listUserRole.size()-1, listUserRole.size()-1);
    }
}
