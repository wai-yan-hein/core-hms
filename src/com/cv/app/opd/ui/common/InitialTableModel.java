/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.Initial;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class InitialTableModel extends AbstractTableModel{
    private List<Initial> listInitial = new ArrayList();
    private String[] columnNames = {"Initial"};
    
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
        Initial record = listInitial.get(row);

        switch (column) {
            case 0: //Name
                return record.getInitialName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listInitial.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Initial> getListInitial() {
        return listInitial;
    }

    public void setListInitial(List<Initial> listInitial) {
        this.listInitial = listInitial;
        fireTableDataChanged();
    }
    
    public Initial getInitial(int row){
        return listInitial.get(row);
    }
    
    public void setInitial(int row, Initial initial){
        listInitial.set(row, initial);
        fireTableRowsUpdated(row, row);
    }
    
    public void addInitial(Initial initial){
        listInitial.add(initial);
        fireTableRowsInserted(listInitial.size()-1, listInitial.size()-1);
    }
    
    public void deleteCity(int row){
        listInitial.remove(row);
        fireTableRowsDeleted(0, listInitial.size()-1);
    }
}
