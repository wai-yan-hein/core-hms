/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.LabMachine;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class LabMachineTableModel extends AbstractTableModel{
    private List<LabMachine> listlMachine = new ArrayList();
    private String[] columnNames = {"Lab Machine"};
    
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
        LabMachine record = listlMachine.get(row);

        switch (column) {
            case 0: //Name
                return record.getlMachineName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listlMachine.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<LabMachine> getListLabMachine() {
        return listlMachine;
    }

    public void setListCity(List<LabMachine> listlMachine) {
        this.listlMachine = listlMachine;
        fireTableDataChanged();
    }
    
    public LabMachine getLabMachine(int row){
        return listlMachine.get(row);
    }
    
    public void setLabMachine(int row, LabMachine labmachine){
        listlMachine.set(row, labmachine);
        fireTableRowsUpdated(row, row);
    }
    
    public void addLabMachine(LabMachine labmachine){
        listlMachine.add(labmachine);
        fireTableRowsInserted(listlMachine.size()-1, listlMachine.size()-1);
    }
    
    public void deleteLabMachine(int row){
        listlMachine.remove(row);
        fireTableRowsDeleted(0, listlMachine.size()-1);
    }
}
