/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.LocationItemMapping;
import com.cv.app.pharmacy.database.entity.LocationItemMappingKey;
import com.cv.app.pharmacy.database.view.VLocationItemMapping;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ItemSetupLocationItemMappingTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ItemSetupLocationItemMappingTableModel.class.getName());
    private List<VLocationItemMapping> listVLIM = new ArrayList();
    private final String[] columnNames = {"Location", "Status"};
    private final AbstractDataAccess dao = Global.dao;
    private String medId;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 1;
    }

    @Override
    public Class getColumnClass(int column) {
        if (column == 1) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listVLIM == null) {
            return null;
        }

        if (listVLIM.isEmpty()) {
            return null;
        }

        try {
            VLocationItemMapping record = listVLIM.get(row);

            switch (column) {
                case 0: //Name
                    return record.getLocationName();
                case 1: //Status
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
        if (listVLIM == null) {
            return;
        }

        if (listVLIM.isEmpty()) {
            return;
        }

        VLocationItemMapping record = listVLIM.get(row);

        switch (column) {
            case 0: //Name
                break;
            case 1: //Status
                Boolean tmpValue = (Boolean) value;
                record.setMapStatus(tmpValue);
                if (tmpValue) {
                    record.getKey().setItemId(medId);
                    saveMapping(record);
                } else {
                    deleteMapping(record.getKey().getLocationId().toString(),
                            record.getKey().getItemId());
                }
                break;
        }
    }

    @Override
    public int getRowCount() {
        if (listVLIM == null) {
            return 0;
        }
        return listVLIM.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void setListVLIM(List<VLocationItemMapping> listVLIM) {
        this.listVLIM = listVLIM;
        fireTableDataChanged();
    }

    public void setMedId(String medId) {
        this.medId = medId;
        try {
            if (!medId.equals("-")) {
                String strSql = "select o from VLocationItemMapping o where (o.key.itemId = '"
                        + medId + "' or o.key.itemId = '-')";
                List<VLocationItemMapping> tmpListVLIM = dao.findAllHSQL(strSql);
                setListVLIM(tmpListVLIM);
            } else {
                setListVLIM(new ArrayList());
            }
        } catch (Exception ex) {
            log.error("setMedId : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void saveMapping(VLocationItemMapping vMapping) {
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

    private void deleteMapping(String locationId, String itemId) {
        String strSql = "delete from location_item_mapping where location_id = "
                + locationId + " and item_id = '" + itemId + "'";

        try {
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("deleteMapping : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }
}
