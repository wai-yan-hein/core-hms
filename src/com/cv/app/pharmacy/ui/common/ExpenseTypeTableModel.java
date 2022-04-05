/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.ExpenseType;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ExpenseTypeTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ExpenseTypeTableModel.class.getName());
    private List<ExpenseType> listExpenseType = new ArrayList();
    private final String[] columnNames = {"Expense Name"};

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
        if (listExpenseType == null) {
            return null;
        }

        if (listExpenseType.isEmpty()) {
            return null;
        }

        try {
            ExpenseType expenseType = listExpenseType.get(row);

            switch (column) {
                case 0: //Name
                    return expenseType.getExpenseName();
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
        if (listExpenseType == null) {
            return 0;
        }
        return listExpenseType.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<ExpenseType> getListExpenseType() {
        return listExpenseType;
    }

    public void setListExpenseType(List<ExpenseType> listExpenseType) {
        this.listExpenseType = listExpenseType;
        fireTableDataChanged();
    }

    public ExpenseType getExpenseType(int row) {
        if (listExpenseType != null) {
            if (!listExpenseType.isEmpty()) {
                return listExpenseType.get(row);
            }
        }
        return null;
    }

    public void setExpenseType(int row, ExpenseType expenseType) {
        if (listExpenseType != null) {
            if (!listExpenseType.isEmpty()) {
                listExpenseType.set(row, expenseType);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addExpenseType(ExpenseType expenseType) {
        if (listExpenseType != null) {
            listExpenseType.add(expenseType);
            fireTableRowsInserted(listExpenseType.size() - 1, listExpenseType.size() - 1);
        }
    }

    public void deleteExpenseType(int row) {
        if (listExpenseType != null) {
            if (!listExpenseType.isEmpty()) {
                listExpenseType.remove(row);
                fireTableRowsDeleted(0, listExpenseType.size() - 1);
            }
        }
    }
}
