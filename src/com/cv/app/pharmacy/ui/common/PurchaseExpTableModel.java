/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.ExpenseType;
import com.cv.app.pharmacy.database.entity.PurchaseExpense;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.HeadlessException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PurchaseExpTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PurchaseExpTableModel.class.getName());
    private List<PurchaseExpense> listExpense;
    private final String[] columnNames = {"Date", "Expense Type", "Amount"};
    private String deletedList;
    private int maxUniqueId = 0;

    public PurchaseExpTableModel(List<PurchaseExpense> listExpense) {
        this.listExpense = listExpense;

        if (listExpense != null) {
            if (listExpense.isEmpty()) {
                addEmptyRow();
            }
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (listExpense == null) {
            return false;
        }
        if (column == 2) {
            return listExpense.get(row).getExpType() != null;
        } else {
            return true;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return String.class;
            case 2: //Amount
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
            PurchaseExpense record = listExpense.get(row);

            switch (column) {
                case 0: //Expense Date
                    return DateUtil.toDateStr(record.getExpDate());
                case 1: //Type
                    return record.getExpType();
                case 2: //Amount
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
            PurchaseExpense record = listExpense.get(row);

            switch (column) {
                case 0: //Expense date
                    if (DateUtil.isValidDate(value)) {
                        record.setExpDate(DateUtil.toDate(value));
                    }
                    break;
                case 1: //Expense Type
                    record.setExpType((ExpenseType) value);
                    if (!hasEmptyRow()) {
                        addEmptyRow();
                    }
                    break;
                case 2: //Amount
                    if (NumberUtil.isNumber(value.toString())) {
                        record.setExpAmount(Double.parseDouble(value.toString()));
                    } else {
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Invalid number type.",
                                "Number Type", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                default:
                    System.out.println("invalid index");
            }
        } catch (NumberFormatException | HeadlessException ex) {
            log.error("setValueAt : " + ex.getMessage());
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

        PurchaseExpense record = listExpense.get(listExpense.size() - 1);

        return record.getExpType() == null;
    }

    public void addEmptyRow() {
        if (listExpense != null) {
            PurchaseExpense record = new PurchaseExpense();
            record.setExpDate(DateUtil.toDate(DateUtil.getTodayDateStr()));
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
        PurchaseExpense record = listExpense.get(row);

        if (NumberUtil.NZeroL(record.getPurchaseExpId()) > 0) {
            if (deletedList == null) {
                deletedList = "'" + record.getPurchaseExpId() + "'";
            } else {
                deletedList = deletedList + ",'" + record.getPurchaseExpId() + "'";
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

        if (listExpense == null) {
            for (PurchaseExpense record : listExpense) {
                if (record.getExpType() != null) {
                    if (NumberUtil.NZero(record.getExpAmount()) <= 0) {
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

    public void setListDetail(List<PurchaseExpense> listExpense) {
        this.listExpense = listExpense;
        if (this.listExpense != null) {
            if (!listExpense.isEmpty()) {
                PurchaseExpense tmpD = listExpense.get(listExpense.size() - 1);
                maxUniqueId = tmpD.getUniqueId();
            }
            if (!hasEmptyRow()) {
                addEmptyRow();
            }
            fireTableDataChanged();
        }
    }

    public String getDeleteSql() {
        String strSQL = null;

        if (deletedList != null) {
            strSQL = "delete from purchase_expense where purchase_expense_id in ("
                    + deletedList + ")";
            deletedList = null;
        }

        return strSQL;
    }

    public List<PurchaseExpense> getListDetail() {
        String strSql = "SELECT * FROM com.cv.app.pharmacy.database.entity.PurchaseExpense"
                + " WHERE expType.expenseId IS NOT NULL and expAmount IS NOT NULL";
        return JoSQLUtil.getResult(strSql, listExpense);
    }
}
