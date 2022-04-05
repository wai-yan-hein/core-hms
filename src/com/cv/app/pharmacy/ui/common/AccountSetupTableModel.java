/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.AccSetting;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class AccountSetupTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(AccountSetupTableModel.class.getName());
    private List<AccSetting> listAS = new ArrayList();
    private final String[] columnNames = {"Type"};

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
        if (listAS == null) {
            return null;
        }

        if (listAS.isEmpty()) {
            return null;
        }

        try {
            AccSetting record = listAS.get(row);

            switch (column) {
                case 0: //Type
                    return record.getAccType();
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
        if (listAS == null) {
            return 0;
        }
        return listAS.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<AccSetting> getListAS() {
        return listAS;
    }

    public void setListAS(List<AccSetting> listAS) {
        this.listAS = listAS;
        fireTableDataChanged();
    }

    public AccSetting getSelected(int row) {
        if (listAS != null) {
            if (!listAS.isEmpty()) {
                return listAS.get(row);
            }
        }
        return null;
    }

    public void delete(int row) {
        if (listAS != null) {
            if (!listAS.isEmpty()) {
                listAS.remove(row);
                fireTableRowsDeleted(0, listAS.size());
            }
        }
    }
}
