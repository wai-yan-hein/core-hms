/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.LocationGroup;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class LocationGroupTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(LocationGroupTableModel.class.getName());
    private List<LocationGroup> listLG = new ArrayList();
    private final String[] columnNames = {"Group Name"};

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
        if (listLG == null) {
            return null;
        }

        if (listLG.isEmpty()) {
            return null;
        }

        try {
            LocationGroup item = listLG.get(row);

            switch (column) {
                case 0: //Name
                    return item.getGroupName();
                default:
                    return null;
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
        if (listLG == null) {
            return 0;
        }
        return listLG.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<LocationGroup> getListLG() {
        return listLG;
    }

    public void setListLG(List<LocationGroup> listLG) {
        this.listLG = listLG;
        fireTableDataChanged();
    }
    
    public LocationGroup getLocationGroup(int row){
        LocationGroup lg = null;
        if(listLG != null){
            if(!listLG.isEmpty()){
                if(row >= 0 && row < listLG.size()){
                    lg = listLG.get(row);
                }
            }
        }
        return lg;
    }
}
