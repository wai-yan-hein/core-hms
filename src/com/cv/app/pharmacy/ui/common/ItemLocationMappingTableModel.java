/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.LocationItemMapping;
import com.cv.app.pharmacy.database.entity.LocationItemMappingKey;
import com.cv.app.pharmacy.database.view.VItemLocationMapping;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author MgKyawThuraAung
 */
public class ItemLocationMappingTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ItemLocationMappingTableModel.class.getName());
    private List<VItemLocationMapping> listVILM = new ArrayList();
    private final String[] columnNames = {"Item Type", "Category", "Brand", "Item Code", " Item Name", "Active"};
    private final AbstractDataAccess dao = Global.dao;

    @Override
    public int getRowCount() {
        if (listVILM == null) {
            return 0;
        }
        return listVILM.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 5;
    }

    @Override
    public Class getColumnClass(int column) {
        if (column == 5) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listVILM == null) {
            return null;
        }

        if (listVILM.isEmpty()) {
            return null;
        }
        try {
            VItemLocationMapping record = listVILM.get(row);
            switch (column) {
                case 0:
                    return record.getItemTypeName();
                case 1:
                    return record.getCategoryName();
                case 2:
                    return record.getBrandName();
                case 3:
                    return record.getShortName();
                case 4:
                    return record.getItemName();
                case 5:
                    return record.getMapStatus();
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
        if (listVILM == null) {
            return;
        }

        if (listVILM.isEmpty()) {
            return;
        }
        VItemLocationMapping record = listVILM.get(row);
        switch (column) {
            case 0://Item Type
                break;
            case 1://Category
                break;
            case 2://Code
                if (value != null) {
                    record.getKey().setItemId((String) value);
                }
                break;
            case 3://Name
                break;
            case 5:
                Boolean tmpValue = (Boolean) value;
                record.setMapStatus(tmpValue);
                if (tmpValue) {
                    saveMapping(record);
                } else {
                    deleteMapping(record.getKey().getItemId(),
                            record.getKey().getLocationId());
                }
                break;
        }
    }

    public void setlistVILM(List<VItemLocationMapping> listVILM) {
        this.listVILM = listVILM;
        fireTableDataChanged();
    }

    private void saveMapping(VItemLocationMapping vMapping) {
        LocationItemMappingKey key = vMapping.getKey();
        LocationItemMapping lim = new LocationItemMapping();
        lim.setKey(key);

        try {
            dao.save(lim);
        } catch (Exception ex) {
            log.error("saveMapping : " + ex.getMessage());
        } finally {
            dao.close();
        }

    }

    private void deleteMapping(String itemId, int LocationId) {
        String strSql = "delete from location_item_mapping where item_id ='" + itemId + "'"
                + "and location_id =" + LocationId + " ";
        try {
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("deleteMapping : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public void selectAll(){
        if(listVILM != null){
            if(!listVILM.isEmpty()){
                int index = 0;
                for(VItemLocationMapping ilm : listVILM){
                    if(!ilm.getMapStatus()){
                        ilm.setMapStatus(Boolean.TRUE);
                        saveMapping(ilm);
                        fireTableCellUpdated(index, 5);
                        index++;
                    }
                }
            }
        }
    }
    
    public void unSelectAll(){
        if(listVILM != null){
            if(!listVILM.isEmpty()){
                int index = 0;
                for(VItemLocationMapping ilm : listVILM){
                    if(ilm.getMapStatus()){
                        ilm.setMapStatus(Boolean.FALSE);
                        deleteMapping(ilm.getKey().getItemId(),ilm.getKey().getLocationId());
                        fireTableCellUpdated(index, 5);
                        index++;
                    }
                }
            }
        }
    }
}
