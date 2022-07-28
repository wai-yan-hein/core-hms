/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.view.VTraderPayment;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class PaymentHisTableModel extends AbstractTableModel {

    private List<VTraderPayment> listTraderPayHis = new ArrayList();
    private final String[] columnNames = {"Date", "Code", "Trader", "P-Amount"};
    private final String prifxStatus = Util1.getPropValue("system.sale.emitted.prifix");

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
                return String.class;
            case 1: //Code
                return String.class;
            case 2: //Trader
                return String.class;
            case 3: //P-Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        VTraderPayment ph = listTraderPayHis.get(row);

        switch (column) {
            case 0: //Date
                return DateUtil.toDateStr(ph.getPayDate());
            case 1: //Code
                if (prifxStatus.equals("Y")) {
                    return ph.getStuNo();
                } else {
                    return ph.getTraderId();
                }
            case 2: //Trader Name
                return ph.getTraderName();
            case 3: //P-Amount
                return ph.getPaidAmtP();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listTraderPayHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VTraderPayment> getTraderPayHis() {
        return listTraderPayHis;
    }

    public void setListTraderPayHis(List<VTraderPayment> listTraderPayHis) {
        this.listTraderPayHis = listTraderPayHis;
        fireTableDataChanged();
    }

    public VTraderPayment getSelectVou(int row) {
        return listTraderPayHis.get(row);
    }

    public Double getTotal() {
        if (listTraderPayHis == null) {
            return 0.0;
        } else if (listTraderPayHis.isEmpty()) {
            return 0.0;
        } else {
            Double total = 0.0;
            for (VTraderPayment vtp : listTraderPayHis) {
                total += NumberUtil.NZero(vtp.getPaidAmtP());
            }
            return total;
        }
    }

    public void removeAll() {
        if (listTraderPayHis != null) {
            listTraderPayHis.clear();
            fireTableDataChanged();
        }
    }

    public void remove(int row) {
        if (listTraderPayHis != null) {
            listTraderPayHis.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }
}
