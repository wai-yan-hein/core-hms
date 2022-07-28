/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.LocationTraderMapping;
import com.cv.app.pharmacy.database.entity.LocationTraderMappingKey;
import com.cv.app.pharmacy.database.view.VLocationTraderMapping;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author MgKyawThuraAung
 */
public class LocationTraderMappingTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(LocationTraderMappingTableModel.class.getName());
    private List<VLocationTraderMapping> listVILM = new ArrayList();
    private final String[] columnNames = {"Group Id", "Group Name", "Code", "Name", "Township", "Active"};
    private final AbstractDataAccess dao = Global.dao;
    private String prefix = Util1.getPropValue("system.sale.emitted.prifix");
    
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
            VLocationTraderMapping record = listVILM.get(row);
            switch (column) {
                case 0: //Group Id
                    return record.getGroupId();
                case 1: //Group Name
                    return record.getGroupName();
                case 2: //Code
                    if (prefix.equals("Y")) {
                        return record.getStuNo();
                    } else {
                        return record.getKey().getTraderId();
                    }
                case 3: //Name
                    return record.getTraderName();
                case 4: //Township
                    return record.getTownshipName();
                case 5: //Map Status
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
        VLocationTraderMapping record = listVILM.get(row);
        switch (column) {
            case 0://Item Type
                break;
            case 1://Category
                break;
            case 2://Code
                break;
            case 3://Name
                break;
            case 5:
                Boolean tmpValue = (Boolean) value;
                record.setMapStatus(tmpValue);
                if (tmpValue) {
                    saveMapping(record);
                } else {
                    deleteMapping(record.getKey().getTraderId(),
                            record.getKey().getLocationId());
                }
                break;
        }
    }

    public void setlistVILM(List<VLocationTraderMapping> listVILM) {
        this.listVILM = listVILM;
        fireTableDataChanged();
    }

    private void saveMapping(VLocationTraderMapping vMapping) {
        LocationTraderMappingKey key = vMapping.getKey();
        LocationTraderMapping lim = new LocationTraderMapping();
        lim.setKey(key);

        try {
            dao.save(lim);
        } catch (Exception ex) {
            log.error("saveMapping : " + ex.getMessage());
        } finally {
            dao.close();
        }

    }

    private void deleteMapping(String traderId, int LocationId) {
        String strSql = "delete from location_trader_mapping where trader_id ='" + traderId + "'"
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
                for(VLocationTraderMapping ilm : listVILM){
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
                for(VLocationTraderMapping ilm : listVILM){
                    if(ilm.getMapStatus()){
                        ilm.setMapStatus(Boolean.FALSE);
                        deleteMapping(ilm.getKey().getTraderId(),ilm.getKey().getLocationId());
                        fireTableCellUpdated(index, 5);
                        index++;
                    }
                }
            }
        }
    }
}
