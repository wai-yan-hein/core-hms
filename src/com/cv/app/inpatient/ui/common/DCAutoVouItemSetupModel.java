/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.common.Global;
import com.cv.app.inpatient.database.entity.DCAutoVouItem;
import com.cv.app.inpatient.database.entity.InpService;
import com.cv.app.inpatient.database.view.VDCAutoVouItem;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class DCAutoVouItemSetupModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(DCAutoVouItemSetupModel.class.getName());
    private List<VDCAutoVouItem> list = new ArrayList();
    private final String[] columnNames = {"DC Service Name", "Qty"};
    private final AbstractDataAccess dao = Global.dao;

    public DCAutoVouItemSetupModel() {
    }

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
        switch (column) {
            case 0: //Description
                return String.class;
            case 1: //Qty
                return Integer.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return null;
        }

        VDCAutoVouItem record = list.get(row);

        switch (column) {
            case 0: //OT Service Name
                return record.getServiceName();
            case 1: //Qty
                return record.getQty();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (list == null) {
            return;
        }

        if (list.isEmpty()) {
            return;
        }

        VDCAutoVouItem record = list.get(row);

        switch (column) {
            case 0: //OT Service Name
                if (value == null) {

                } else {
                    InpService service = (InpService) value;
                    record.setServiceId(service.getServiceId());
                    record.setQty(1);
                    record.setServiceName(service.getServiceName());
                }
                break;
        }

        saveRecord(record);

        try {
            fireTableCellUpdated(row, column);
            fireTableCellUpdated(row, 1);
        } catch (Exception ex) {

        }

        addNewRow();
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VDCAutoVouItem> getList() {
        return list;
    }

    public void setList(List<VDCAutoVouItem> list) {
        this.list = list;
        fireTableDataChanged();
        addNewRow();
    }

    private void saveRecord(VDCAutoVouItem rec) {
        try {
            DCAutoVouItem item;
            if (rec.getId() == null) {
                item = new DCAutoVouItem();
            } else {
                item = (DCAutoVouItem) dao.find(DCAutoVouItem.class, rec.getId());
            }
            item.setQty(rec.getQty());
            item.setServiceId(rec.getServiceId());
            dao.save(item);
        } catch (Exception ex) {
            log.error("saveRecord : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    public void delete(int row) {
        if (list != null) {
            if (!list.isEmpty()) {
                VDCAutoVouItem record = list.get(row);
                try {
                    Long id = record.getId();
                    String strSql = "delete from dc_auto_vou_item where id = " + id.toString();
                    dao.execSql(strSql);
                    list.remove(row);
                    fireTableRowsDeleted(row, row);
                } catch (Exception ex) {
                    log.error("delete : " + ex.toString());
                } finally {
                    dao.close();
                }
            }
        }
    }

    public void addNewRow() {
        int count = list.size();
        if (count == 0 || list.get(count - 1).getId() != null) {
            list.add(new VDCAutoVouItem());
            fireTableRowsInserted(list.size() - 1, list.size() - 1);
        }
    }
}
