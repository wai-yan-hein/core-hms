/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.VoucherSearch;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class StockOpSearchTableModel extends AbstractTableModel{
    
    static Logger log = Logger.getLogger(StockOpSearchTableModel.class.getName());
    private List<VoucherSearch> listStockOpeningHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Location", "User"};
    
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
            case 1: //Vou No
                return String.class;
            case 2: //Location
                return String.class;
            case 3: //User
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(listStockOpeningHis == null){
            return null;
        }
        
        if(listStockOpeningHis.isEmpty()){
            return null;
        }
        
        try{
        VoucherSearch soh = listStockOpeningHis.get(row);

        switch (column) {
            case 0: //Date
                return soh.getTranDate();
            case 1: //Vou No
                return soh.getInvNo();
            case 2: //Location
                return soh.getLocation();
            case 3: //User
                return soh.getUserName();
            default:
                return null;
        }
        }catch(Exception ex){
            log.error("getValueAt : " + ex.getMessage());
        }
        
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if(listStockOpeningHis == null){
            return 0;
        }
        return listStockOpeningHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherSearch> getListStockOpeningHis() {
        return listStockOpeningHis;
    }

    public void setListStockOpeningHis(List<VoucherSearch> listStockOpeningHis) {
        this.listStockOpeningHis = listStockOpeningHis;
        fireTableDataChanged();
    }

    public VoucherSearch getSelectVou(int row){
        if(listStockOpeningHis != null){
            if(!listStockOpeningHis.isEmpty()){
                return listStockOpeningHis.get(row);
            }
        }
        return null;
    }
}
