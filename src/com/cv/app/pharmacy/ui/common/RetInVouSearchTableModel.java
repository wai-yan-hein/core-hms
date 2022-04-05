/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class RetInVouSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(RetInVouSearchTableModel.class.getName());
    private List<VoucherSearch> listRetInHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Customer", "User", 
        "V-Total", "Location"};

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
            case 5: //Location
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listRetInHis == null) {
            return null;
        }

        if (listRetInHis.isEmpty()) {
            return null;
        }

        try {
            VoucherSearch rih = listRetInHis.get(row);

            switch (column) {
                case 0: //Date
                    return rih.getTranDate();
                case 1: //Vou No
                    if (Util1.getNullTo(rih.getIsDeleted())) {
                        return rih.getInvNo() + "**";
                    } else {
                        return rih.getInvNo();
                    }
                case 2: //Customer
                    return rih.getCusNo() + " - " + rih.getCusName();
                case 3: //User
                    return rih.getUserName();
                case 4: //V-Total
                    return NumberUtil.roundTo(rih.getVouTotal(), 0);
                case 5: //Location
                    return " " + rih.getLocation();
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
        if(listRetInHis == null){
            return 0;
        }
        return listRetInHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherSearch> getListRetInHis() {
        return listRetInHis;
    }

    public void setListRetInHis(List<VoucherSearch> listRetInHis) {
        this.listRetInHis = listRetInHis;
        fireTableDataChanged();
    }

    public VoucherSearch getSelectVou(int row) {
        if(listRetInHis != null){
            if(!listRetInHis.isEmpty()){
                return listRetInHis.get(row);
            }
        }
        return null;
    }
    
    public Double getTotal() {
        Double ttlAmt = 0.0;
        if (listRetInHis != null) {
            if (!listRetInHis.isEmpty()) {
                for (VoucherSearch rih : listRetInHis) {
                    if (!rih.getIsDeleted()) {
                        ttlAmt += NumberUtil.NZero(rih.getVouTotal());
                    }
                }
            }
        }
        return ttlAmt;
    }
}
