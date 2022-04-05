/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.view.VClinicPackageTotal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class PackageTotalTableModel extends AbstractTableModel {

    private List<VClinicPackageTotal> listTTL = new ArrayList();
    private final String[] columnNames = {"Item Option", "Group Name", "Sale Total", "User Total"};

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
        if (column == 2 || column == 3) {
            return Double.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        VClinicPackageTotal record = listTTL.get(row);

        switch (column) {
            case 0: //Item Option
                return record.getItemOption();
            case 1: //Group Name
                return record.getTypeName();
            case 2: //Sale Total
                return record.getSysTotal();
            case 3: //User Total
                return record.getUsrTotal();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listTTL.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VClinicPackageTotal> getListTTL() {
        return listTTL;
    }

    public void setListTTL(List<VClinicPackageTotal> listTTL) {
        this.listTTL = listTTL;
        fireTableDataChanged();
        System.gc();
    }
    
    public void clear(){
        listTTL = new ArrayList();
        fireTableDataChanged();
        System.gc();
    }
}
