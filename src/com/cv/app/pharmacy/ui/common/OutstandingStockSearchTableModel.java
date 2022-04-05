/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.StockOutstanding;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OutstandingStockSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OutstandingStockSearchTableModel.class.getName());
    private List<StockOutstanding> listStockOutstanding = new ArrayList();
    private final String[] columnNames = {"Option", "Date", "Vou No", "Description", "Balance", "User"};

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
            case 0: //Option
                return String.class;
            case 1: //Date
                return Date.class;
            case 2: //Vou No
                return String.class;
            case 3: //Medicine
                return String.class;
            case 4: //Balance
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listStockOutstanding == null) {
            return null;
        }

        if (listStockOutstanding.isEmpty()) {
            return null;
        }

        try {
            StockOutstanding sod = listStockOutstanding.get(row);

            switch (column) {
                case 0: //Option
                    return sod.getTranOption();
                case 1: //Date
                    return sod.getTranDate();
                case 2: //Vou No
                    return sod.getInvId();
                case 3: //Medicine
                    return sod.getMed().getMedName();
                case 4: //Balance
                    return sod.getQtyStr();
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
        if (listStockOutstanding == null) {
            return 0;
        }
        return listStockOutstanding.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<StockOutstanding> getListStockOutstanding() {
        return listStockOutstanding;
    }

    public void setListStockOutstanding(List<StockOutstanding> listStockOutstanding) {
        this.listStockOutstanding = listStockOutstanding;
        fireTableDataChanged();
    }

    public StockOutstanding getSelectStock(int row) {
        if (listStockOutstanding != null) {
            if (!listStockOutstanding.isEmpty()) {
                return listStockOutstanding.get(row);
            }
        }
        return null;
    }

    public void add(StockOutstanding so) {
        if (listStockOutstanding != null) {
            listStockOutstanding.add(so);
            fireTableRowsInserted(listStockOutstanding.size() - 1, listStockOutstanding.size() - 1);
        }
    }

    public void removeAll() {
        if (listStockOutstanding != null) {
            if (!listStockOutstanding.isEmpty()) {
                listStockOutstanding.removeAll(listStockOutstanding);
                fireTableDataChanged();
            }
        }
    }
}
