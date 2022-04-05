/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.ot.database.view.VOTGroupService;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OTGroupServiceTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OTGroupServiceTableModel.class.getName());
    private List<VOTGroupService> listGS = new ArrayList();
    private final String[] columnNames = {"Group", "Service", "Status"};

    public OTGroupServiceTableModel() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0;
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
        if (listGS == null) {
            return null;
        }

        if (listGS.isEmpty()) {
            return null;
        }

        try {
            VOTGroupService record = listGS.get(row);

            switch (column) {
                case 0: //Group
                    return record.getGroupName();
                case 1: //Service
                    return record.getSrvName();
                case 2: //Status
                    return record.getActStatus();
                default:
                    return new Object();
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listGS == null) {
            return 0;
        }
        return listGS.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VOTGroupService> getListGS() {
        return listGS;
    }

    public void setListGS(List<VOTGroupService> listGS) {
        this.listGS = listGS;
        fireTableDataChanged();
    }
    
    public VOTGroupService getSelected(int row){
        if(listGS == null){
            return null;
        }
        
        if(listGS.isEmpty()){
            return null;
        }
        
        return listGS.get(row);
    }
}
