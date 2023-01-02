/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.helper.BillTransferDetail;
import com.cv.app.util.NumberUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class BillTransferTableModel extends AbstractTableModel {

    private List<BillTransferDetail> listBTD = new ArrayList();
    private final String[] columnNames = {"Date", "Reg No.", "Admission No.",
        "Name", "Bill Type", "Amount", "Discount", "Paid", "Balance"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 6 || column == 7;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return Date.class;
            case 1: //Reg No.
                return String.class;
            case 2: //Admission No.
                return String.class;
            case 3: //Name
                return String.class;
            case 4: //Age
                return String.class;
            case 5: //Amount
                return Double.class;
            case 6: //Discount
                return Double.class;
            case 7: //Paid
                return Double.class;
            case 8: //Balance
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        BillTransferDetail record = listBTD.get(row);

        switch (column) {
            case 0: //Date
                return record.getTranDate();
            case 1: //Reg No.
                return record.getRegNo();
            case 2: //Admission No.
                return record.getAdmissionNo();
            case 3: //Name
                return record.getName();
            case 4: //Age
                return record.getStrAge();
            case 5: //Amount
                return record.getAmount();
            case 6: //Discount
                return record.getDiscount();
            case 7: //Paid
                return record.getPaid();
            case 8: //Balance
                return record.getBalance();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        BillTransferDetail record = listBTD.get(row);

        switch (column) {
            case 6: //Discount
                record.setDiscount(NumberUtil.NZero(value));
                record.setPaid(NumberUtil.NZero(record.getAmount())-
                        NumberUtil.NZero(record.getDiscount()));
                break;
            case 7: //Paid
                record.setPaid(NumberUtil.NZero(value));
                break;
        }

        double balance = NumberUtil.NZero(record.getAmount())
                - (NumberUtil.NZero(record.getDiscount()) + NumberUtil.NZero(record.getPaid()));
        record.setBalance(balance);
    }

    @Override
    public int getRowCount() {
        return listBTD.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<BillTransferDetail> getListBTD() {
        return listBTD;
    }

    public void setListBTD(List<BillTransferDetail> listBTD) {
        this.listBTD = listBTD;
        fireTableDataChanged();
    }

    public void clear() {
        listBTD = new ArrayList();
        fireTableDataChanged();
        System.gc();
    }

    public BillTransferDetail getSelectedData(int index) {
        if (listBTD == null) {
            return null;
        } else if (listBTD.isEmpty()) {
            return null;
        } else {
            return listBTD.get(index);
        }
    }
}
