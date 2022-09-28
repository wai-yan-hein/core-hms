/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.view.VPatientBillPayment;
import com.cv.app.util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class BillPaymentSearchTableModel extends AbstractTableModel {

    private List<VPatientBillPayment> listVPBP = new ArrayList();
    private String[] columnNames = {"Reg No.", "Date", "Patient Name", "Bill Type", "Remark", "Pay Amount"};

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
            case 0: //Reg No.
            case 1://Date
            case 2: //Patient Name
            case 3: //Bill Type
            case 4: //Remark
                return String.class;
            case 5: //Pay Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        VPatientBillPayment record = listVPBP.get(row);

        switch (column) {
            case 0: //Reg No.
                return record.getRegNo();
            case 1://date
                if (record.getCreatedDate() != null) {
                    return DateUtil.toDateStr(record.getCreatedDate());
                } else {
                    return record.getCreatedDate();
                }

            case 2: //Patient Name
                return record.getPatientName();
            case 3: //Bill Type
                return record.getBillName();
            case 4: //Remark
                return record.getRemark();
            case 5: //Pay Amount;
                return record.getPayAmt();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    @Override
    public int getRowCount() {
        return listVPBP.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VPatientBillPayment> getListVPBP() {
        return listVPBP;
    }

    public void setListVPBP(List<VPatientBillPayment> listVPBP) {
        this.listVPBP = listVPBP;
        fireTableDataChanged();
    }

    public Integer getBillId(int row) {
        return listVPBP.get(row).getId();
    }

    public void remove(int row) {
        listVPBP.remove(row);
        fireTableRowsDeleted(row, row);
    }
}
