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
public class PurVouSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PurVouSearchTableModel.class.getName());
    private List<VoucherSearch> listVS = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Ref. Vou.", 
        "Supplier", "User", "V-Total", "V-Balance","Location"};

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
            case 6: //V-Balance
                return Double.class;
            case 7: //Location
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listVS == null) {
            return null;
        }
        if (listVS.isEmpty()) {
            return null;
        }

        try {
            VoucherSearch ph = listVS.get(row);

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
                    return ph.getCusNo() + " - " + ph.getCusName();
                case 4: //User
                    return ph.getUserName();
                case 5: //V-Total
                    return NumberUtil.roundTo(ph.getVouTotal(),0);
                case 6: //V-Balance
                    return NumberUtil.roundTo(ph.getBalance(), 0);
                case 7: //Location
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
        if (listVS == null) {
            return 0;
        }
        return listVS.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherSearch> getListPurHis() {
        return listVS;
    }

    public void setListPurHis(List<VoucherSearch> listPurHis) {
        this.listVS = listPurHis;
        fireTableDataChanged();
    }

    public VoucherSearch getSelectVou(int row) {
        if (listVS != null) {
            if (!listVS.isEmpty()) {
                return listVS.get(row);
            }
        }
        return null;
    }

    public Double getTotal() {
        Double total = 0.0;
        if (listVS != null) {
            if (!listVS.isEmpty()) {
                for (VoucherSearch ph : listVS) {
                    if (!ph.getIsDeleted()) {
                        total += ph.getVouTotal();
                    }
                }
            }
        }

        return total;
    }
}
