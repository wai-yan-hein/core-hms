/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.PatientBillPayment;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class BillPaymentTableModel extends AbstractTableModel{
    private List<PatientBillPayment> listPBP = new ArrayList();
    private String[] columnNames = {"Bill Type", "Amount", "Payment", "Discount", "Balance", "Remark"};
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 2 || column == 3 || column == 5;
    }

    @Override
    public Class getColumnClass(int column) {
        switch(column){
            case 0: //Bill Type
            case 5: //Remark
                return String.class;
            case 1: //Amount
            case 2: //Payment
            case 3: //Discount
            case 4: //Balance
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        PatientBillPayment record = listPBP.get(row);

        switch (column) {
            case 0: //Bill Type
                return record.getBillTypeDesp();
            case 1: //Amount
                return record.getAmount();
            case 2: //Payment
                return record.getPayAmt();
            case 3: //Discount
                return record.getDiscount();
            case 4: //Balance
                return record.getBalance();
            case 5: //Remark;
                return record.getRemark();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        PatientBillPayment record = listPBP.get(row);
        
        switch (column) {
            case 2: //Paymenu
                if(value != null){
                    record.setPayAmt(NumberUtil.NZero(value));
                }else{
                    record.setPayAmt(null);
                }
                
                record.setBalance(NumberUtil.NZero(record.getAmount())-(NumberUtil.NZero(record.getPayAmt()+NumberUtil.NZero(record.getDiscount()))));
                break;
            case 3: //Discount
                if(value != null){
                    record.setDiscount(NumberUtil.NZero(value));
                }else{
                    record.setDiscount(null);
                }
                record.setBalance(NumberUtil.NZero(record.getAmount())-(NumberUtil.NZero(record.getPayAmt())+NumberUtil.NZero(record.getDiscount())));
                break;
            case 5: //Remark
                if(value != null){
                    record.setRemark(value.toString());
                }else{
                    record.setRemark(null);
                }
                break;
        }
        
        fireTableCellUpdated(row, column);
        fireTableCellUpdated(row, 3);
        fireTableCellUpdated(row, 4);
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
    
    public List<PatientBillPayment> getSavePBP(){
        List<PatientBillPayment> listSavePBP = new ArrayList();
        
        for(PatientBillPayment pbp : listPBP){
            if(NumberUtil.NZero(pbp.getPayAmt()) != 0 || NumberUtil.NZero(pbp.getDiscount()) != 0){
                pbp.setCreatedDate(DateUtil.getTodayDateTime());
                listSavePBP.add(pbp);
            }
        }
        
        return listSavePBP;
    }
}
