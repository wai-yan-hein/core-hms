/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.Service;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class OPDAutoCompleteTableModel extends AbstractTableModel{
    private List<Service> listService;
    private String[] columnNames = {"Code", "Description", "Fees"};
    
    public OPDAutoCompleteTableModel(List<Service> listService){
        this.listService = listService;
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
        switch(column){
            case 0: //Code
                return String.class;
            case 1: //Description
                return String.class;
            case 2: //Fees
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Service record = listService.get(row);

        switch (column) {
            case 0: //Code
                return record.getServiceCode();
            case 1: //Description
                return record.getServiceName();
            case 2: //Fees
                return record.getFees();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listService.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public Service getService(int row){
        return listService.get(row);
    }
    
    public int getSize(){
        return listService.size();
    }
}
