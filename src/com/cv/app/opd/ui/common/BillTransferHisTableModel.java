/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.BillTransferHis;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class BillTransferHisTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(BillTransferHisTableModel.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private List<BillTransferHis> listTBL = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No.", "Bill", "Customer", 
        "Ttl-Disc", "Ttl-Paid"};

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
            case 1: //Vou No.
                return String.class;
            case 2: //Bill
                return String.class;
            case 3: //Customer
                return String.class;
            case 4: //Ttl-Disc
                return Double.class;
            case 5: //Ttl-Paid
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        BillTransferHis record = listTBL.get(row);

        switch (column) {
            case 0: //Date
                return record.getTranDate();
            case 1: //Vou No.
                if(record.getDelated()){
                    return record.getBthId() + "*";
                }else{
                    return record.getBthId();
                }
            case 2: //Bill
                return record.getTranOption();
            case 3: //Customer
                return record.getTraderId();
            case 4: //Ttl-Disc
                return record.getDiscount();
            case 5: //Ttl-Paid
                return record.getTotalAmt();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        
    }

    @Override
    public int getRowCount() {
        return listTBL.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void clear() {
        listTBL = new ArrayList();
        fireTableDataChanged();
        System.gc();
    }

    public List<BillTransferHis> getListTBL() {
        return listTBL;
    }

    public void setListTBL(List<BillTransferHis> listTBL) {
        this.listTBL = listTBL;
        fireTableDataChanged();
    }
    
    public BillTransferHis getData(int row){
        if(listTBL == null){
            return null;
        }else if(listTBL.isEmpty()){
            return null;
        }else{
            return listTBL.get(row);
        }
    }
    
    public void deleteRow(int row){
        if(listTBL == null){
        }else if(listTBL.isEmpty()){
        }else{
            listTBL.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }
}
