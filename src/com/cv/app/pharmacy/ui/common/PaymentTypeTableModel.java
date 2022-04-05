/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.PaymentType;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PaymentTypeTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PaymentTypeTableModel.class.getName());
    private List<PaymentType> listPaymentType = new ArrayList();
    private final String[] columnNames = {"Payment Type"};

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
        if (listPaymentType == null) {
            return null;
        }

        if (listPaymentType.isEmpty()) {
            return null;
        }

        try {
            PaymentType paymentType = listPaymentType.get(row);

            switch (column) {
                case 0: //Payment Type
                    return paymentType.getPaymentTypeName();
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
        if (listPaymentType == null) {
            return 0;
        }
        return listPaymentType.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PaymentType> getListPaymentType() {
        return listPaymentType;
    }

    public void setListPaymentType(List<PaymentType> listPaymentType) {
        this.listPaymentType = listPaymentType;
        fireTableDataChanged();
    }

    public PaymentType getPaymentType(int row) {
        return listPaymentType.get(row);
    }

    public void setPaymentType(int row, PaymentType paymentType) {
        if (listPaymentType != null) {
            if (!listPaymentType.isEmpty()) {
                listPaymentType.set(row, paymentType);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addPaymentType(PaymentType paymentType) {
        if (listPaymentType != null) {
            listPaymentType.add(paymentType);
            fireTableRowsInserted(listPaymentType.size() - 1, listPaymentType.size() - 1);
        }
    }

    public void deletePaymentType(int row) {
        if (listPaymentType != null) {
            if (!listPaymentType.isEmpty()) {
                listPaymentType.remove(row);
                fireTableRowsDeleted(0, listPaymentType.size() - 1);
            }
        }
    }
}
