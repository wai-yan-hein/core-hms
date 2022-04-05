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
public class StockReceiveSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(StockReceiveSearchTableModel.class.getName());
    private List<VoucherSearch> listStockReceiveHis = new ArrayList();
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
        switch (column) {
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
        if(listStockReceiveHis == null){
            return null;
        }
        
        if(listStockReceiveHis.isEmpty()){
            return null;
        }
        
        try{
        VoucherSearch srh = listStockReceiveHis.get(row);

        switch (column) {
            case 0: //Date
                return srh.getTranDate();
            case 1: //Vou No
                if(srh.getIsDeleted()){
                    return srh.getInvNo() + "*";
                }else{
                    return srh.getInvNo();
                }
            case 2: //Location
                return srh.getLocation();
            case 3: //User
                return srh.getUserName();
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
        if(listStockReceiveHis == null){
            return 0;
        }
        return listStockReceiveHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherSearch> getListStockReceiveHis() {
        return listStockReceiveHis;
    }

    public void setListStockReceiveHis(List<VoucherSearch> listStockReceiveHis) {
        this.listStockReceiveHis = listStockReceiveHis;
        fireTableDataChanged();
    }

    public VoucherSearch getSelectVou(int row) {
        if(listStockReceiveHis != null){
            if(!listStockReceiveHis.isEmpty()){
                return listStockReceiveHis.get(row);
            }
        }
        return null;
    }
}
