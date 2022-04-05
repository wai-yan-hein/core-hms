/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.view.VTraderPayment;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class PaymentHisListTableModel extends AbstractTableModel{
    private List<VTraderPayment> listTraderPayHis = new ArrayList();
    private String[] columnNames = {"Date", "Trader", "C-Amount", "Currency", "P-Amount"};
    
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
        switch(column){
            case 0: //Date
                return Date.class;
            case 1: //Trader
                return String.class;
            case 2: //C-Amount
                return Double.class;
            case 3: //Currency
                return String.class;
            case 4: //P-Amount
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
                return ph.getPayDate();
            case 1: //Trader
                return ph.getTraderName();
            case 2: //C-Amount
                return ph.getPaidAmtC();
            case 3: //Currency
                return ph.getCurrency();
            case 4: //P-Amount
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
    
    public VTraderPayment getSelectVou(int row){
        return listTraderPayHis.get(row);
    }
}
