/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.PurchaseVoucher;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PurVouListTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PurVouListTableModel.class.getName());
    private List<PurchaseVoucher> listPH = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No.", "Supplier", "Vou Total", "S"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 4;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return Date.class;
            case 1: //Vou No.
                return String.class;
            case 2: //Supplier
                return String.class;
            case 3: //Vou Total
                return Double.class;
            case 4: //Select
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listPH == null) {
            return null;
        }

        if (listPH.isEmpty()) {
            return null;
        }

        try {
            PurchaseVoucher record = listPH.get(row);

            switch (column) {
                case 0: //Date
                    return record.getPurDate();
                case 1: //Vou No.
                    return record.getVouoNo();
                case 2: //Supplier
                    if (record.getSupId() != null) {
                        return record.getSupId() + " - " + record.getSupName();
                    } else {
                        return null;
                    }
                case 3: //Vou Total
                    return record.getVouTotal();
                case 4: //Select
                    return record.isCheck();
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
        if (listPH == null) {
            return;
        }

        if (listPH.isEmpty()) {
            return;
        }

        try {
            PurchaseVoucher record = listPH.get(row);

            switch (column) {
                case 4: //Select
                    record.setCheck((Boolean) value);
                    break;
            }

            fireTableCellUpdated(row, column);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    @Override
    public int getRowCount() {
        if (listPH == null) {
            return 0;
        }
        return listPH.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PurchaseVoucher> getListPH() {
        return listPH;
    }

    public void setListPH(List<PurchaseVoucher> listPH) {
        this.listPH = listPH;
        fireTableDataChanged();
    }

    public PurchaseVoucher getSelected(int row) {
        if (listPH != null) {
            if (!listPH.isEmpty()) {
                return listPH.get(row);
            }
        }
        return null;
    }

    public String getUpdateVoucher() {
        String strVouList = "'-'";
        if (listPH != null) {
            strVouList = "";
            for (PurchaseVoucher pv : listPH) {
                if (pv.isCheck()) {
                    if (strVouList.isEmpty()) {
                        strVouList = "'" + pv.getVouoNo() + "'";
                    } else {
                        strVouList = strVouList + ",'" + pv.getVouoNo() + "'";
                    }
                }
            }
        }
        return strVouList;
    }
}
