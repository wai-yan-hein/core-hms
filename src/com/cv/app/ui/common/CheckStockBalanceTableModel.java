/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.common;

import com.cv.app.pharmacy.database.helper.CheckStockBalanceEntity;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class CheckStockBalanceTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(CheckStockBalanceTableModel.class.getName());
    private List<CheckStockBalanceEntity> list = new ArrayList();
    private final String[] columnNames = {"Code", "Name", "Relstr", "System Balance",
        "System Amount", "Smallest Cost", "User Balance", "User Amount", "Diff-Qty", "Diff-Amt"};

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
            case 0: //Code
                return String.class;
            case 1: //Name
                return String.class;
            case 2: //Relstr
                return String.class;
            case 3: //Sys-Balance
                return String.class;
            case 4: //Sys-Ttl-Amt
                return Double.class;
            case 5: //Smallest Cost
                return Double.class;
            case 6: //Usr-Balance
                return String.class;
            case 7: //Usr-Ttl-Amt
                return Double.class;
            case 8: //Diff-Qty
                return String.class;
            case 9: //Diff-Amt
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
            CheckStockBalanceEntity record = list.get(row);

            switch (column) {
                case 0: //Code
                    return record.getMedId();
                case 1: //Name
                    return record.getMedName();
                case 2: //Relstr
                    return record.getRelStr();
                case 3: //Sys-Balance
                    return record.getBalQtyStr();
                case 4: //Sys-Ttl-Amt
                    return record.getTtlCost();
                case 5: //Smallest Cost
                    return record.getSmallestCost();
                case 6: //Usr-Balance
                    return record.getUsrBalStr();
                case 7: //Usr-Ttl-Amt
                    return record.getUsrTtlCost();
                case 8: //Diff-Qty
                    return record.getDiffQtyStr();
                case 9: //Diff-Amt
                    return record.getDiffCost();
                default:
                    return Object.class;
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

    public List<CheckStockBalanceEntity> getList() {
        return list;
    }

    public void setList(List<CheckStockBalanceEntity> list) {
        this.list = list;
        fireTableDataChanged();
    }
}
