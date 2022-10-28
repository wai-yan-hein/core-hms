/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.helper.BillTransferDetail;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class BillTransferTableModel extends AbstractTableModel{
    private List<BillTransferDetail> listBTD = new ArrayList();
    private final String[] columnNames = {"Date", "Reg No.", "Admission No.", "Name", "Bill Type", "Amount"};
    
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
            case 1: //Reg No.
                return String.class;
            case 2: //Admission No.
                return String.class;
            case 3: //Name
                return String.class;
            case 4: //Age
                return String.class;
            case 5: //Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        BillTransferDetail record = listBTD.get(row);

        switch (column) {
            case 0: //Date
                return record.getTranDate();
            case 1: //Reg No.
                return record.getRegNo();
            case 2: //Admission No.
                return record.getAdmissionNo();
            case 3: //Name
                return record.getName();
            case 4: //Age
                return record.getStrAge();
            case 5: //Amount
                return record.getAmount();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        
    }

    @Override
    public int getRowCount() {
        return listBTD.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<BillTransferDetail> getListBTD() {
        return listBTD;
    }

    public void setListBTD(List<BillTransferDetail> listBTD) {
        this.listBTD = listBTD;
        fireTableDataChanged();
    }
    
    public void clear(){
        listBTD = new ArrayList();
        fireTableDataChanged();
        System.gc();
    }
    
    public BillTransferDetail getSelectedData(int index){
        if(listBTD == null){
            return null;
        }else if(listBTD.isEmpty()){
            return null;
        }else{
            return listBTD.get(index);
        }
    }
}
