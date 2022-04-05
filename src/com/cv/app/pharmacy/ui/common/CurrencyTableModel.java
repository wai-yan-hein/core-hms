/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Currency;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class CurrencyTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(CurrencyTableModel.class.getName());
    private List<Currency> listCurrency = new ArrayList();
    private final String[] columnNames = {"ID", "Name", "Symbol"};

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
        if (listCurrency == null) {
            return null;
        }

        if (listCurrency.isEmpty()) {
            return null;
        }

        try {
            Currency currency = listCurrency.get(row);

            switch (column) {
                case 0: //ID
                    return currency.getCurrencyCode();
                case 1: //Name
                    return currency.getCurrencyName();
                case 2: //Symbol
                    return currency.getCurrencySymbol();
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
        if (listCurrency == null) {
            return 0;
        }
        return listCurrency.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Currency> getListCurrency() {
        return listCurrency;
    }

    public void setListCurrency(List<Currency> listCurrency) {
        this.listCurrency = listCurrency;
        fireTableDataChanged();
    }

    public Currency getCurrency(int row) {
        if (listCurrency != null) {
            if (!listCurrency.isEmpty()) {
                return listCurrency.get(row);
            }
        }
        return null;
    }

    public void setCurrency(int row, Currency currency) {
        if (listCurrency != null) {
            if (!listCurrency.isEmpty()) {
                listCurrency.set(row, currency);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addCurrency(Currency currency) {
        if (listCurrency != null) {
            listCurrency.add(currency);
            fireTableRowsInserted(listCurrency.size() - 1, listCurrency.size() - 1);
        }
    }

    public void deleteCurrency(int row) {
        if (listCurrency != null) {
            if (!listCurrency.isEmpty()) {
                listCurrency.remove(row);
                fireTableRowsDeleted(0, listCurrency.size() - 1);
            }
        }
    }
}
