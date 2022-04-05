/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.VoucherSearch;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class StockIssueSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(StockIssueSearchTableModel.class.getName());
    private List<VoucherSearch> listStockIssueHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Location", "User"};

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
        switch (column) {
            case 0: //Date
                return Date.class;
            case 1: //Vou No
                return String.class;
            case 2: //Location
                return String.class;
            case 3: //User
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listStockIssueHis == null) {
            return null;
        }

        if (listStockIssueHis.isEmpty()) {
            return null;
        }

        try {
            VoucherSearch sih = listStockIssueHis.get(row);

            switch (column) {
                case 0: //Date
                    return sih.getTranDate();
                case 1: //Vou No
                    if (sih.getIsDeleted()) {
                        return sih.getInvNo() + "**";
                    } else {
                        return sih.getInvNo();
                    }
                case 2: //Location
                    return sih.getLocation();
                case 3: //User
                    return sih.getUserName();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listStockIssueHis == null) {
            return 0;
        }
        return listStockIssueHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherSearch> getListStockIssueHis() {
        return listStockIssueHis;
    }

    public void setListStockIssueHis(List<VoucherSearch> listStockIssueHis) {
        this.listStockIssueHis = listStockIssueHis;
        fireTableDataChanged();
    }

    public VoucherSearch getSelectVou(int row) {
        if (listStockIssueHis != null) {
            if (!listStockIssueHis.isEmpty()) {
                return listStockIssueHis.get(row);
            }
        }
        return null;
    }
}
