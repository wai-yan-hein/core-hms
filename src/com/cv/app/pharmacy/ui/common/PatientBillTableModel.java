/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.opd.database.entity.PatientBillPayment;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class PatientBillTableModel extends AbstractTableModel{
    private List<PatientBillPayment> listPBP = new ArrayList();
    private final String[] columnNames = {"Bill Name", "Amount"};
    
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
            case 0:
                return String.class;
            case 1:
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        PatientBillPayment record = listPBP.get(row);

        switch (column) {
            case 0: //Bill Name
                return record.getBillTypeDesp();
            case 1: //Amount
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
        return listPBP.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PatientBillPayment> getListPBP() {
        return listPBP;
    }

    public void setListPBP(List<PatientBillPayment> listPBP) {
        this.listPBP = listPBP;
        fireTableDataChanged();
    }
    
    public Double getBillAmount(int billTypeId){
        if(listPBP == null){
            return 0.0;
        }else if(listPBP.isEmpty()){
            return 0.0;
        }
        for(PatientBillPayment pbp : listPBP){
            if(pbp.getBillTypeId() == billTypeId){
                return pbp.getAmount();
            }
        }
        
        return 0.0;
    }
    
    public void clear(){
        listPBP = new ArrayList();
        fireTableDataChanged();
    }
}
