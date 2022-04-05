/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.PriceChangeHis1;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PCSearchTableModel extends AbstractTableModel{
    
    static Logger log = Logger.getLogger(PCSearchTableModel.class.getName());
    private List<PriceChangeHis1> listPriceChangeHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Remark", "User"};
    
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
            case 2: //Remark
                return String.class;
            case 3: //User
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(listPriceChangeHis == null){
            return null;
        }
        
        if(listPriceChangeHis.isEmpty()){
            return null;
        }
        
        try{
        PriceChangeHis1 pch = listPriceChangeHis.get(row);

        switch (column) {
            case 0: //Date
                return pch.getPriceChangeDate();
            case 1: //Vou No
                return pch.getPriceChangeVouId();
            case 2: //Remark
                return pch.getRemark();
            case 3: //User
              if(pch.getCreatedBy() != null){
                return pch.getCreatedBy().getUserShortName();
              }else{
                return null;
              }
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
        if(listPriceChangeHis == null){
            return 0;
        }
        return listPriceChangeHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PriceChangeHis1> getListPriceChangeHis() {
        return listPriceChangeHis;
    }

    public void setListPriceChangeHis(List<PriceChangeHis1> listPriceChangeHis) {
        this.listPriceChangeHis = listPriceChangeHis;
        fireTableDataChanged();
    }

    public PriceChangeHis1 getSelectVou(int row){
        if(listPriceChangeHis != null){
            if(!listPriceChangeHis.isEmpty()){
                return listPriceChangeHis.get(row);
            }
        }
        return null;
    }
}
