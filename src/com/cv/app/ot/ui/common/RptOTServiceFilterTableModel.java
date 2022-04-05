/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.ot.database.entity.OTProcedure;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class RptOTServiceFilterTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(RptOTServiceFilterTableModel.class.getName());
    private final List<OTProcedure> listService = new ArrayList();
    private final String[] columnNames = {"Code", "Description"};
    private JTable parent;
    
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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        OTProcedure record = listService.get(row);

        switch (column) {
            case 0: //Code
                return record.getServiceCode();
            case 1: //Description
                return record.getServiceName();
            default:
                return new Object();
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        switch (column) {
            case 0: //Code
                if (value != null) {
                    setService((OTProcedure) value, row);
                }
                break;
            default:
                log.info("setValueAt : invalid index");
        }
        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return listService.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public boolean hasEmptyRow() {
        if (listService.isEmpty()) {
            return false;
        }
        OTProcedure record = listService.get(listService.size() - 1);
        return record.getServiceId() == null;
    }

    public void addEmptyRow() {
        OTProcedure record = new OTProcedure();
        listService.add(record);
        fireTableRowsInserted(listService.size() - 1, listService.size() - 1);
        parent.setRowSelectionInterval(listService.size() - 1, listService.size() - 1);
        parent.setColumnSelectionInterval(0, 0);
    }

    public void delete(int row) {
        OTProcedure record = listService.get(row);

        if (record.getServiceId() != null) {
            listService.remove(row);
            fireTableRowsDeleted(row, row);
            if (!hasEmptyRow()) {
                addEmptyRow();
            }
        }
    }

    public void setSrvFilter(OTProcedure record, int pos) {
        listService.set(pos, record);
        fireTableDataChanged();

        if (!hasEmptyRow()) {
            addEmptyRow();
        }
    }

    public void setService(OTProcedure service, int row) {
        final String TABLE = "com.cv.app.ot.database.entity.OTProcedure";
        String strSQL = "SELECT * FROM " + TABLE
                + " WHERE serviceId = " + service.getServiceId();

        try {
            if (!JoSQLUtil.isAlreadyHave(strSQL, listService)) {
                setSrvFilter(service, row);
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate item code.",
                        "Duplicate", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("setService : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    public String getFilterCodeStr() {
        String strTmp = null;

        for (OTProcedure service : listService) {
            if (service.getServiceId() != null) {
                if (strTmp == null) {
                    strTmp = service.getServiceId().toString();
                } else {
                    strTmp = strTmp + "," + service.getServiceId().toString();
                }
            }
        }

        return strTmp;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }
}
