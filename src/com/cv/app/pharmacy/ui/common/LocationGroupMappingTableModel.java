/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.LocationGroupMapping;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class LocationGroupMappingTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(LocationGroupMappingTableModel.class.getName());
    private List<LocationGroupMapping> listLGM = new ArrayList();
    private final String[] columnNames = {"Location"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listLGM == null) {
            return null;
        }

        if (listLGM.isEmpty()) {
            return null;
        }

        try {
            LocationGroupMapping item = listLGM.get(row);

            switch (column) {
                case 0: //Name
                    if(item.getKey() != null){
                        if(item.getKey().getLocation() != null){
                            return item.getKey().getLocation().getLocationName();
                        }
                    }
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
        if (listLGM == null) {
            return;
        }

        if (listLGM.isEmpty()) {
            return;
        }
        
        try{
            LocationGroupMapping item = listLGM.get(row);
            switch(column){
                case 0: //Location
                    if(value != null){
                        item.getKey().setLocation((Location)value);
                    } else {
                        item.getKey().setLocation(null);
                    }
                    break;
            }
        }catch(Exception ex){
            log.error("setValueAt : " + ex.getMessage());
        }
        
        fireTableCellUpdated(row, column);
        addNewRow();
    }

    @Override
    public int getRowCount() {
        if (listLGM == null) {
            return 0;
        }
        return listLGM.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<LocationGroupMapping> getListLGM() {
        return listLGM;
    }

    public void addNewRow(){
        if(listLGM != null && !listLGM.isEmpty()){
            LocationGroupMapping lgm = listLGM.get(listLGM.size()-1);
            if(lgm.getKey() != null){
                if(lgm.getKey().getLocation() != null){
                    listLGM.add(new LocationGroupMapping());
                    fireTableDataChanged();
                }
            }
        }else{
            listLGM = new ArrayList();
            listLGM.add(new LocationGroupMapping());
        }
    }
    
    public void setListLGM(List<LocationGroupMapping> listLGM) {
        this.listLGM = listLGM;
        addNewRow();
        fireTableDataChanged();
    }
    
    public LocationGroupMapping getLocationGroupMapping(int row){
        LocationGroupMapping lgm = null;
        if(listLGM != null){
            if(!listLGM.isEmpty()){
                if(row >= 0 && row < listLGM.size()){
                    lgm = listLGM.get(row);
                }
            }
        }
        return lgm;
    }
}
