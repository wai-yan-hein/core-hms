/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.TraderPayAccount;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class TraderPayAccountTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(TraderPayAccountTableModel.class.getName());
    private List<TraderPayAccount> list = new ArrayList();
    private final String[] columnNames = {"Description"};

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
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return null;
        }

        try {
            TraderPayAccount record = list.get(row);

            switch (column) {
                case 0: //Description
                    return record.getDesp();
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
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<TraderPayAccount> getList() {
        return list;
    }

    public void setList(List<TraderPayAccount> list) {
        if (list != null) {
            this.list = list;
            fireTableDataChanged();
        }
    }

    public TraderPayAccount getAccount(int row) {
        if (list != null) {
            if (!list.isEmpty()) {
                return list.get(row);
            }
        }

        return null;
    }

    public void setAccount(int row, TraderPayAccount account) {
        if (list != null) {
            if (!list.isEmpty()) {
                list.set(row, account);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addAccount(TraderPayAccount account) {
        if (list != null) {
            list.add(account);
            fireTableRowsInserted(list.size() - 1, list.size() - 1);
        }
    }

    public void deleteAccount(int row) {
        if (list != null) {
            if (!list.isEmpty()) {
                list.remove(row);
                fireTableRowsDeleted(0, list.size());
            }
        }
    }
}
