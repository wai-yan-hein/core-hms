/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.DamageHis;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
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
public class DamageSearchTableModel extends AbstractTableModel{
    
    static Logger log = Logger.getLogger(DamageSearchTableModel.class.getName());
    private List<VoucherSearch> listDamageHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Location", "Remark", "User"};
    
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
            case 3: //Remark
                return String.class;
            case 4: //User
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(listDamageHis == null){
            return null;
        }
        
        if(listDamageHis.isEmpty()){
            return null;
        }
        
        try{
        VoucherSearch dmgh = listDamageHis.get(row);

        switch (column) {
            case 0: //Date
                return dmgh.getTranDate();
            case 1: //Vou No
                if(Util1.getNullTo(dmgh.getIsDeleted())){
                    return dmgh.getInvNo() + "**";
                }else{
                    return dmgh.getInvNo();
                }
            case 2: //Location
                return dmgh.getLocation();
            case 3: //Remark
                return dmgh.getRefNo();
            case 4: //User
                return dmgh.getUserName();
            default:
                return null;
        }
        } catch(Exception ex){
            log.error("getValueAt : " + ex.getMessage());
        }
        
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if(listDamageHis == null){
            return 0;
        }
        return listDamageHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherSearch> getListDamageHis() {
        return listDamageHis;
    }

    public void setListDamageHis(List<VoucherSearch> listDamageHis) {
        this.listDamageHis = listDamageHis;
        fireTableDataChanged();
    }

    public VoucherSearch getSelectVou(int row){
        if(listDamageHis != null){
            if(!listDamageHis.isEmpty()){
                return listDamageHis.get(row);
            }
        }
        return null;
    }
}
