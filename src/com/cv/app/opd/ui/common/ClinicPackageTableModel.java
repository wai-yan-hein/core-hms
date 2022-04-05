/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.ClinicPackage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class ClinicPackageTableModel extends AbstractTableModel{
    private List<ClinicPackage> listCP = new ArrayList();
    private final String[] columnNames = {"Package Name", "Price", "Status"};
    
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
        switch(column){
            case 0: //Package Name
                return String.class;
            case 1: //Price
                return Double.class;
            case 2: //Status
                return Boolean.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(listCP == null){
            return null;
        }
        
        if(listCP.isEmpty()){
            return null;
        }
        ClinicPackage record = listCP.get(row);

        switch (column) {
            case 0: //Package Name
                return record.getPackageName();
            case 1: //Price
                return record.getPrice();
            case 2: //Status
                return record.getStatus();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listCP.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<ClinicPackage> getListCP() {
        return listCP;
    }

    public void setListCP(List<ClinicPackage> listCP) {
        this.listCP = listCP;
        fireTableDataChanged();
    }
    
    public ClinicPackage getSelectedPackage(int index){
        ClinicPackage cp = null;
        if(listCP != null){
            if(!listCP.isEmpty()){
                cp = listCP.get(index);
            }
        }
        return cp;
    }
}
