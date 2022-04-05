/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.OPDLabGroup;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class OPDLabGroupTableModel extends AbstractTableModel{
    private List<OPDLabGroup> listOPDLabGroup = new ArrayList();
    private final String[] columnNames = {"OPD Lab Group"};
    
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
        OPDLabGroup record = listOPDLabGroup.get(row);

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
        return listOPDLabGroup.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OPDLabGroup> getListOPDLabGroup() {
        return listOPDLabGroup;
    }

    public void setListOPDLabGroup(List<OPDLabGroup> listOPDLabGroup) {
        this.listOPDLabGroup = listOPDLabGroup;
        fireTableDataChanged();
    }
    
    public OPDLabGroup getOPDLabGroup(int row){
        return listOPDLabGroup.get(row);
    }
    
    public void setOPDLabGroup(int row, OPDLabGroup olg){
        listOPDLabGroup.set(row, olg);
        fireTableRowsUpdated(row, row);
    }
    
    public void addOPDLabGroup(OPDLabGroup olg){
        listOPDLabGroup.add(olg);
        fireTableRowsInserted(listOPDLabGroup.size()-1, listOPDLabGroup.size()-1);
    }
    
    public void deleteOPDLabGroup(int row){
        listOPDLabGroup.remove(row);
        fireTableRowsDeleted(0, listOPDLabGroup.size()-1);
    }
}
