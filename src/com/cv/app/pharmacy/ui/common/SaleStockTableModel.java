/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.Stock;
import com.cv.app.util.DateUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class SaleStockTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(SaleStockTableModel.class.getName());
    private List<Stock> listStock = new ArrayList();
    private final String[] columnNames = {"Location", "Exp-Date", "Balance"};

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
        if (listStock == null) {
            return null;
        }

        if (listStock.isEmpty()) {
            return null;
        }

        try {
            Stock record = listStock.get(row);

            switch (column) {
                case 0: //Location
                    return record.getLocationName();
                case 1: //Exp-Date
                    return DateUtil.toDateStr(record.getExpDate());
                case 2: //Balance
                    return record.getQtyStr();
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
        if (listStock == null) {
            return 0;
        }
        return listStock.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Stock> getListStock() {
        return listStock;
    }

    public void setListStock(List<Stock> listStock) {
        this.listStock = listStock;
        fireTableDataChanged();
    }

    public Date getExpireDate(Integer locId) {
        if (!listStock.isEmpty()) {
            for (int i = listStock.size(); i <= listStock.size(); i--) {
                if (i > 0) {
                    Stock s = listStock.get(i - 1);
                    if (s.getLocationId().equals(locId)) {
                        return s.getExpDate();
                    }
                }
            }
        }
        return null;
    }
}
