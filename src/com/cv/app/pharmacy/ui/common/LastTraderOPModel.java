/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.TraderBalanceFixList;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class LastTraderOPModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(LastTraderOPModel.class.getName());
    private List<TraderBalanceFixList> listTrader = new ArrayList();
    private final String[] columnNames = {"Code", "Name", "Trader Balance", "Vou Balance", "Difference"};

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
            case 0: //Code
                return String.class;
            case 1: //Name
                return String.class;
            case 2: //Trader Balance
                return Double.class;
            case 3: //Vou Balance
                return Double.class;
            case 4: //Difference
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listTrader == null) {
            return null;
        }

        if (listTrader.isEmpty()) {
            return null;
        }

        try {
            TraderBalanceFixList record = listTrader.get(row);

            switch (column) {
                case 0: //Code
                    return record.getTraderId();
                case 1: //Name
                    return record.getTraderName();
                case 2: //Trader balance
                    return record.getTraderBalance();
                case 3: //Vou Balance
                    return record.getVouBalance();
                case 4: //Difference
                    return record.getDifference();
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
        if (listTrader == null) {
            return 0;
        }
        return listTrader.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<TraderBalanceFixList> getListTrader() {
        return listTrader;
    }

    public void setListTrader(List<TraderBalanceFixList> listTrader) {
        this.listTrader = listTrader;
        fireTableDataChanged();
    }

    public TraderBalanceFixList getTrader(int index){
        if(listTrader == null){
            return null;
        }else if(listTrader.isEmpty()){
            return null;
        }else{
            return listTrader.get(index);
        }
    }
    
    public void clear(){
        listTrader = new ArrayList();
        fireTableDataChanged();
    }
}
