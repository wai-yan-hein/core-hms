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
    private final String[] columnNames = {"Date", "Reg No.", "Adm No.", 
        "Patient Name", "Bill Type", "Remark", "Discount", "Pay Amount"};

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
            case 0: //Date
            case 1: //Reg No.
            case 2: //Adm No.
            case 3: //Patient Name
            case 4: //Bill Type
            case 5: //Remark
                return String.class;
            case 6: //Discount
                return Double.class;
            case 7: //Pay Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        VPatientBillPayment record = listVPBP.get(row);

        switch (column) {
            case 0: //Date
                if (record.getPayDate() != null) {
                    return DateUtil.toDateStr(record.getPayDate());
                } else {
                    return record.getPayDate();
                }
            case 1: //Reg No.
                return record.getRegNo();
            case 2: //Adm No.
                return record.getAdmissionNo();
            case 3: //Patient Name
                return record.getPatientName();
            case 4: //Bill Type
                return record.getBillName();
            case 5: //Remark
                return record.getRemark();
            case 6: //Discount
                return record.getDiscount();
            case 7: //Pay Amount;
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

    public boolean isCanDelete(int row) {
        boolean status = true;
        VPatientBillPayment vpbp = listVPBP.get(row);
        if (vpbp.getRemark() != null) {
            if (vpbp.getRemark().contains("Bill Transfer")) {
                status = false;
            }
        }
        return status;
    }
}
