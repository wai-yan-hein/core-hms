/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.Technician;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class TechnicianAutoCompleteTableModel extends AbstractTableModel{
    private List<Technician> listTech;
    private String[] columnNames = {"Name"};
    
    public TechnicianAutoCompleteTableModel(List<Technician> listTech){
        this.listTech = listTech;
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
            case 0: //Name
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Technician record = listTech.get(row);

        switch (column) {
            case 0: //Name
                return record.getTechName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listTech.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public Technician getTechnician(int row){
        return listTech.get(row);
    }
    
    public int getSize(){
        return listTech.size();
    }
}
