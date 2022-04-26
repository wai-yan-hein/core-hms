/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.CurrencyTtl;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class CurrencyTotalTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(CurrencyTotalTableModel.class.getName());
    private List<CurrencyTtl> list = new ArrayList();
    private final String[] columnNames = {"Currency", "Amount"};

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
        switch(column){
            case 0: //Currency
                return String.class;
            case 1: //Amount
                return Double.class;
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

        try {
            CurrencyTtl record = list.get(row);

            switch (column) {
                case 0: //Currency
                    return record.getCurrency();
                case 1: //Amount
                    return record.getTtlPaid();
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

    public List<CurrencyTtl> getList() {
        return list;
    }

    public void setList(List<CurrencyTtl> list) {
        this.list = list;
        fireTableDataChanged();
    }
}
