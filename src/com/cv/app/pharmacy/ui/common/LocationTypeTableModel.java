/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.LocationType;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class LocationTypeTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(LocationTypeTableModel.class.getName());
    private List<LocationType> listLT = new ArrayList();
    private final String[] columnNames = {"Location Type"};

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
        if (listLT == null) {
            return null;
        }

        if (listLT.isEmpty()) {
            return null;
        }

        try {
            LocationType record = listLT.get(row);

            switch (column) {
                case 0: //Name
                    return record.getDescription();
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
        if (listLT == null) {
            return 0;
        }
        return listLT.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<LocationType> getListLT() {
        return listLT;
    }

    public void setListLT(List<LocationType> listLT) {
        this.listLT = listLT;
        fireTableDataChanged();
    }

    public LocationType getLocationType(int row){
        if(listLT != null){
            if(!listLT.isEmpty()){
                return listLT.get(row);
            }
        }
        
        return null;
    }

    public void setLocationType(int row, LocationType lt){
        if(listLT != null){
            if(!listLT.isEmpty()){
                listLT.set(row, lt);
                fireTableRowsUpdated(row, row);
            }
        }
    }
    
    public void addLocationType(LocationType lt){
        if(listLT != null){
            listLT.add(lt);
            fireTableRowsInserted(listLT.size() - 1, listLT.size() - 1);
        }
    }

    public void deleteLocationType(int row) {
        if (listLT != null) {
            if (!listLT.isEmpty()) {
                listLT.remove(row);
                fireTableRowsDeleted(0, listLT.size());
            }
        }
    }
}
