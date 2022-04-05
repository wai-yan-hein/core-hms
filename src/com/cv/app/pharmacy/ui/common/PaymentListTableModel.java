/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.TraderPayHis;
import com.cv.app.util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PaymentListTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PaymentListTableModel.class.getName());
    private List<TraderPayHis> listPay = new ArrayList();
    private final String[] columnNames = {"Date", "Description", "Amount"};

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
        if (column == 2) {
            return Double.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listPay == null) {
            return null;
        }

        if (listPay.isEmpty()) {
            return null;
        }

        try {
            TraderPayHis record = listPay.get(row);

            switch (column) {
                case 0: //Date
                    return DateUtil.toDateStr(record.getPayDate());
                case 1: //Description
                    return record.getRemark();
                case 2: //Amount
                    return record.getPaidAmtP();
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
        if(listPay == null){
            return 0;
        }
        return listPay.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<TraderPayHis> getListTraderPayHis() {
        return listPay;
    }

    public void setListTraderPayHis(List<TraderPayHis> listTraderPayHis) {
        this.listPay = listTraderPayHis;
        fireTableDataChanged();
    }

    public TraderPayHis getPayment(int row) {
        if(listPay != null){
            if(!listPay.isEmpty()){
                return listPay.get(row);
            }
        }
        return null;
    }
}
