/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.opd.database.helper.OPDDrPayment;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class OTDrPaymentTableModel extends AbstractTableModel {

    private List<OPDDrPayment> listDP = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Reg No", "Patient Name",
        "Admission No", "Service Name", "Qty", "Price", "Charge Type", "Amount", 
        "OT Fee", "Staff", "Nurse", "MO"};

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
                return Date.class;
            case 1: //Vou No
                return String.class;
            case 2: //Reg No
                return String.class;
            case 3: //Patient Name
                return String.class;
            case 4: //Admission No
                return String.class;
            case 5: //Service Name
                return String.class;
            case 6: //Qty
                return Integer.class;
            case 7: //Price
                return Double.class;
            case 8: //Charge Type
                return String.class;
            case 9: //Amount
                return Double.class;
            case 10: //Surgeon Fee
                return Double.class;
            case 11: //Nurse
                return Double.class;
            case 12: //Tech
                return Double.class;
            case 13: //MO
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDP == null) {
            return null;
        }

        if (listDP.isEmpty()) {
            return null;
        }

        OPDDrPayment record = listDP.get(row);
        switch (column) {
            case 0: //Date
                return record.getTranDate();
            case 1: //Vou No
                return record.getVouNo();
            case 2: //Reg No
                return record.getRegNo();
            case 3: //Patient Name
                return record.getPtName();
            case 4: //Admission No
                return record.getAdmissionNo();
            case 5: //Service Name
                return record.getServiceName();
            case 6: //Qty
                return record.getQty();
            case 7: //Price
                return record.getPrice();
            case 8: //Charge Type
                return record.getChargeType();
            case 9: //Amount
                return record.getAmount();
            case 10: //Surgeon
                return record.getReaderFee();
            case 11: //Staff
                return record.getStaffFee();
            case 12: //Nurse
                return record.getTechFee();
            case 13: //MO
                return record.getMoFee();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listDP == null) {
            return 0;
        } else {
            return listDP.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OPDDrPayment> getListDP() {
        return listDP;
    }

    public void setListDP(List<OPDDrPayment> listDP) {
        this.listDP = listDP;
        System.gc();
        fireTableDataChanged();
    }
    
    public void clear(){
        listDP = new ArrayList();
        System.gc();
        fireTableDataChanged();
    }
}
