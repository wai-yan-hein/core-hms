/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.OPDGroup;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class OPDGroupTableModel extends AbstractTableModel{
    private List<OPDGroup> listOPDGroup = new ArrayList();
    private String[] columnNames = {"Group"};
    
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
        OPDGroup record = listOPDGroup.get(row);

        switch (column) {
            case 0: //Name
                return record.getGroupName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listOPDGroup.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OPDGroup> getListOPDGroup() {
        return listOPDGroup;
    }

    public void setListOPDGroup(List<OPDGroup> listOPDGroup) {
        this.listOPDGroup = listOPDGroup;
        fireTableDataChanged();
    }
    
    public OPDGroup getOPDGroup(int row){
        return listOPDGroup.get(row);
    }
    
    public void setOPDGroup(int row, OPDGroup oPDGroup){
        listOPDGroup.set(row, oPDGroup);
        fireTableRowsUpdated(row, row);
    }
    
    public void addOPDGroup(OPDGroup oPDGroup){
        listOPDGroup.add(oPDGroup);
        fireTableRowsInserted(listOPDGroup.size()-1, listOPDGroup.size()-1);
    }
    
    public void deleteOPDGroup(int row){
        listOPDGroup.remove(row);
        fireTableRowsDeleted(0, listOPDGroup.size()-1);
    }
}
