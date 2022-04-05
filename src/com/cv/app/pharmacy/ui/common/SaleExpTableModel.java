/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.ExpenseType;
import com.cv.app.pharmacy.database.entity.SaleExpense;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class SaleExpTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(SaleExpTableModel.class.getName());
    private List<SaleExpense> listExpense;
    private final String[] columnNames = {"Date", "Expense Type", "Amt-In", "Amt-Out"};
    private String deletedList;

    public SaleExpTableModel(List<SaleExpense> listExpense) {
        this.listExpense = listExpense;

        if (listExpense != null) {
            addEmptyRow();
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return String.class;
            case 2: //Amt-In
                return Double.class;
            case 3: //Amt-Out
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listExpense == null) {
            return null;
        }

        if (listExpense.isEmpty()) {
            return null;
        }

        try {
            SaleExpense record = listExpense.get(row);

            switch (column) {
                case 0: //Date
                    return DateUtil.toDateStr(record.getExpenseDate());
                case 1: //Expense type
                    return record.getExpType();
                case 2: //Amt-In
                    return record.getExpenseIn();
                case 3: //Amt-Out
                    return record.getExpAmount();
                default:
                    return new Object();
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listExpense == null) {
            return;
        }

        if (listExpense.isEmpty()) {
            return;
        }

        try {
            SaleExpense record = listExpense.get(row);

            switch (column) {
                case 0: //Date
                    if (DateUtil.isValidDate(value)) {
                        record.setExpenseDate(DateUtil.toDate(value));
                    }
                    break;
                case 1: //Expense type
                    record.setExpType((ExpenseType) value);
                    if (!hasEmptyRow()) {
                        addEmptyRow();
                    }
                    break;
                case 2: //Amt-In
                    record.setExpenseIn(Double.parseDouble(value.toString()));
                    break;
                case 3: //Amt-Out
                    record.setExpAmount(Double.parseDouble(value.toString()));
                    break;
                default:
                    System.out.println("invalid index");
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        if (listExpense == null) {
            return 0;
        }
        return listExpense.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public boolean hasEmptyRow() {
        if (listExpense == null) {
            return false;
        }
        if (listExpense.isEmpty()) {
            return false;
        }

        SaleExpense record = listExpense.get(listExpense.size() - 1);
        return record.getExpType() == null;
    }

    public void addEmptyRow() {
        if (listExpense != null) {
            SaleExpense record = new SaleExpense();
            record.setExpenseDate(DateUtil.toDate(DateUtil.getTodayDateStr()));
            listExpense.add(record);
            fireTableRowsInserted(listExpense.size() - 1, listExpense.size() - 1);
        }
    }

    public void delete(int row) {
        if (listExpense == null) {
            return;
        }
        if (listExpense.isEmpty()) {
            return;
        }

        SaleExpense record = listExpense.get(row);

        if (record.getSaleExpenseId() != null) {
            if (deletedList == null) {
                deletedList = "'" + record.getSaleExpenseId() + "'";
            } else {
                deletedList = deletedList + ",'" + record.getSaleExpenseId() + "'";
            }
        }

        listExpense.remove(row);

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableRowsDeleted(row, row);
    }

    public boolean isValidEntry() {
        boolean status = true;
        int row = 0;

        if (listExpense != null) {
            for (SaleExpense record : listExpense) {
                if (record.getExpType() != null) {
                    if (NumberUtil.NZero(record.getExpAmount()) <= 0 && NumberUtil.NZero(record.getExpenseIn()) <= 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Invalid expense value.",
                                "Expense amount.", JOptionPane.ERROR_MESSAGE);
                        status = false;
                    } else {
                        record.setUniqueId(row + 1);
                    }

                    row++;
                }
            }
        }

        return status;
    }

    public void setListDetail(List<SaleExpense> listExpense) {
        this.listExpense = listExpense;

        if (!hasEmptyRow()) {
            addEmptyRow();
        }

        fireTableDataChanged();
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from sale_expense where sale_expense_id in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public List<SaleExpense> getListExpense() {
        String strSql = "SELECT * FROM com.cv.app.pharmacy.database.entity.SaleExpense"
                + " WHERE expType.expenseId IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listExpense);
    }
}
