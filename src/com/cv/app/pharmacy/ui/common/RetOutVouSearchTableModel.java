/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.util.Util1;
import com.cv.app.util.NumberUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class RetOutVouSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(RetOutVouSearchTableModel.class.getName());
    private List<VoucherSearch> listRetOutHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Customer", "User", "V-Total"};

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
            case 2: //Customer
                return String.class;
            case 3: //User
                return String.class;
            case 4: //V-Total
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listRetOutHis == null) {
            return null;
        }
        if (listRetOutHis.isEmpty()) {
            return null;
        }

        try {
            VoucherSearch roh = listRetOutHis.get(row);

            switch (column) {
                case 0: //Date
                    return roh.getTranDate();
                case 1: //Vou No
                    if (Util1.getNullTo(roh.getIsDeleted())) {
                        return roh.getInvNo() + "**";
                    } else {
                        return roh.getInvNo();
                    }
                case 2: //Customer
                    return roh.getCusNo() + " - " + roh.getCusName();
                case 3: //User
                    return roh.getUserName();
                case 4: //V-Total
                    return NumberUtil.roundTo(roh.getVouTotal(),0);
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
        if(listRetOutHis == null){
            return 0;
        }
        return listRetOutHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherSearch> getListRetOutHis() {
        return listRetOutHis;
    }

    public void setListRetOutHis(List<VoucherSearch> listRetOutHis) {
        this.listRetOutHis = listRetOutHis;
        fireTableDataChanged();
    }

    public VoucherSearch getSelectVou(int row) {
        if(listRetOutHis != null){
            if(!listRetOutHis.isEmpty()){
                return listRetOutHis.get(row);
            }
        }
        return null;
    }
}
