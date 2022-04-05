/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.SessionTtl;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class SessionTotalTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(SessionTotalTableModel.class.getName());
    private List<SessionTtl> listSessionTtl = new ArrayList();
    private final String[] columnNames = {"Discription", "Currency", "Amount"};

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
        if (column == 2) {
            return Double.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listSessionTtl == null) {
            return null;
        }

        if (listSessionTtl.isEmpty()) {
            return null;
        }

        try {
            SessionTtl sessionTtl = listSessionTtl.get(row);

            switch (column) {
                case 0: //Discription
                    return sessionTtl.getDesc();
                case 1: //Currency
                    return sessionTtl.getCurrency();
                case 2: //Amount
                    return sessionTtl.getTtlPaid();
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
        if (listSessionTtl == null) {
            return 0;
        }
        return listSessionTtl.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<SessionTtl> getListSessionTtl() {
        return listSessionTtl;
    }

    public void setListSessionTtl(List<SessionTtl> listSessionTtl) {
        if (listSessionTtl != null) {
            this.listSessionTtl.removeAll(listSessionTtl);
        }
        this.listSessionTtl = listSessionTtl;

        fireTableDataChanged();
    }

    public void removeAll() {
        if (listSessionTtl != null) {
            listSessionTtl.removeAll(listSessionTtl);
        }
        fireTableDataChanged();
    }
}
