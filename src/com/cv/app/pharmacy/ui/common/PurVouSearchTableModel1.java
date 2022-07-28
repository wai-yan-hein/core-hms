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
public class PurVouSearchTableModel1 extends AbstractTableModel {

    static Logger log = Logger.getLogger(PurVouSearchTableModel1.class.getName());
    private List<VoucherSearch> listPurHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Ref. Vou.",
        "Supplier", "User", "V-Total", "Location"};
    private double ttlDisc;
    private double ttlPaid;
    private double ttlBalance;

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
            case 2: //Remark
                return String.class;
            case 3: //Customer
                return String.class;
            case 4: //User
                return String.class;
            case 5: //V-Total
                return Double.class;
            case 6: //Location
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listPurHis == null) {
            return null;
        }
        if (listPurHis.isEmpty()) {
            return null;
        }

        try {
            VoucherSearch ph = listPurHis.get(row);

            switch (column) {
                case 0: //Date
                    return ph.getTranDate();
                case 1: //Vou No
                    if (Util1.getNullTo(ph.getIsDeleted())) {
                        return ph.getInvNo() + "**";
                    } else {
                        return ph.getInvNo();
                    }
                case 2: //Remark
                    return ph.getRefNo();
                case 3: //Customer
                    if(ph.getCusNo() == null){
                        return ph.getCusName();
                    }else{
                        return ph.getCusNo() + " - " + ph.getCusName();
                    }
                case 4: //User
                    return ph.getUserName();
                case 5: //V-Total
                    return NumberUtil.roundTo(NumberUtil.NZero(ph.getVouTotal()), 0);
                case 6: //Location
                    return " " + ph.getLocation();
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
        if (listPurHis == null) {
            return 0;
        }
        return listPurHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherSearch> getListPurHis() {
        return listPurHis;
    }

    public void setListPurHis(List<VoucherSearch> listPurHis) {
        this.listPurHis = listPurHis;
        fireTableDataChanged();
    }

    public VoucherSearch getSelectVou(int row) {
        if (listPurHis != null) {
            if (!listPurHis.isEmpty()) {
                return listPurHis.get(row);
            }
        }
        return null;
    }

    public Double getTotal() {
        Double total = 0.0;
        ttlDisc = 0.0;
        ttlPaid = 0.0;
        ttlBalance = 0.0;

        if (listPurHis != null) {
            if (!listPurHis.isEmpty()) {
                for (VoucherSearch ph : listPurHis) {
                    total += ph.getVouTotal();
                    ttlDisc += NumberUtil.NZero(ph.getDiscount());
                    ttlPaid += NumberUtil.NZero(ph.getPaid());
                    ttlBalance += NumberUtil.NZero(ph.getBalance());
                }
            }
        }

        return total;
    }

    public double getTtlDisc() {
        return ttlDisc;
    }

    public double getTtlPaid() {
        return ttlPaid;
    }

    public double getTtlBalance() {
        return ttlBalance;
    }
}
