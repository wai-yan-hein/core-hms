/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.tempentity.TmpVouAmtFix;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class PayVouFixedModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PayVouFixedModel.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private List<TmpVouAmtFix> list = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No.", "Vou Total", "Paid Amount", "Balance"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 3;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return String.class;
            case 1: //Vou No.
                return String.class;
            case 2: //Vou Total
                return Double.class;
            case 3: //Paid Amount
                return Double.class;
            case 4: //Balance
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return null;
        }

        try {
            TmpVouAmtFix record = list.get(row);

            switch (column) {
                case 0: //Date
                    return DateUtil.toDateStr(record.getKey().getSaleDate());
                case 1: //Vou no.
                    return record.getKey().getVouNo();
                case 2: //Vou Total
                    return record.getVouTotal();
                case 3: //Paid Amount
                    return record.getPaidAmount();
                case 4: //Balance
                    return record.getBalance();
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
        if (list == null) {
            return;
        }

        if (list.isEmpty()) {
            return;
        }

        try {
            TmpVouAmtFix record = list.get(row);

            switch (column) {
                case 3: //Paid Amount
                    if (record.getPrvBalance() == null) {
                        record.setPrvBalance(record.getBalance());
                    }
                    if (NumberUtil.isNumber(value)) {
                        if (NumberUtil.NZero(record.getBalance()) - NumberUtil.NZero(record.getPaidAmount()) < 0) {

                        } else {
                            record.setPaidAmount(NumberUtil.NZero(value));
                            record.setBalance(NumberUtil.NZero(record.getBalance()) - NumberUtil.NZero(record.getPaidAmount()));
                        }
                    } else {
                        
                    }
                    dao.save(record);
                    break;
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.toString());
        }
    }

    @Override
    public int getRowCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<TmpVouAmtFix> getList() {
        return list;
    }

    public void setList(List<TmpVouAmtFix> list) {
        this.list = list;
        fireTableDataChanged();
    }
    
    public void clear(){
        list = new ArrayList();
        fireTableDataChanged();
    }
}
