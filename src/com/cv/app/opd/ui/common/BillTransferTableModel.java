/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.TotalEvent;
import com.cv.app.opd.database.helper.BillTransferDetail;
import com.cv.app.util.NumberUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class BillTransferTableModel extends AbstractTableModel {

    private List<BillTransferDetail> listBTD = new ArrayList();
    private final String[] columnNames = {"Date", "Reg No.", "Admission No.",
        "Name", "Bill Type", "Amount", "Discount", "Paid", "Balance", "P"};
    private TotalEvent event;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 6 || column == 7 || column == 9;
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
            case 9: //P
                return Boolean.class;
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
            case 9: //P
                return record.ispStatus();
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
                record.setPaid(NumberUtil.NZero(record.getAmount())
                        - NumberUtil.NZero(record.getDiscount()));
                if(NumberUtil.NZero(record.getDiscount()) == 0 && NumberUtil.NZero(record.getPaid()) == 0){
                    record.setpStatus(false);
                }else{
                    record.setpStatus(true);
                }
                break;
            case 7: //Paid
                double tmpValue = NumberUtil.NZero(value);
                if (tmpValue == 0 && NumberUtil.NZero(record.getDiscount()) == 0) {
                    record.setPaid(null);
                    record.setpStatus(false);
                } else {
                    record.setPaid(tmpValue);
                    record.setpStatus(true);
                }
                break;
            case 9: //P
                boolean status = (Boolean) value;
                record.setpStatus(status);
                if (status) {
                    if (NumberUtil.NZero(record.getPaid()) == 0) {
                        record.setPaid(NumberUtil.NZero(record.getAmount()) - NumberUtil.NZero(record.getDiscount()));
                    }
                } else {
                    record.setPaid(null);
                    record.setDiscount(null);
                }
                break;
        }

        double balance = NumberUtil.NZero(record.getAmount())
                - (NumberUtil.NZero(record.getDiscount()) + NumberUtil.NZero(record.getPaid()));
        record.setBalance(balance);
        if (event != null) {
            event.assignTotal();
        }
        fireTableCellUpdated(row, 6);
        fireTableCellUpdated(row, 7);
        fireTableCellUpdated(row, 9);
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

    public void setEvent(TotalEvent event) {
        this.event = event;
    }
    
    public List<BillTransferDetail> getSaveData(){
        return listBTD.stream().filter(o -> o.ispStatus())
                .collect(Collectors.toList());
    }
}
