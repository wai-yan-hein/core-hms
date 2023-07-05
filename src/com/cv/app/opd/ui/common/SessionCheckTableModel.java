/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.view.VSessionClinic;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class SessionCheckTableModel extends AbstractTableModel{
    private List<VSessionClinic> listSVSessionClinic = new ArrayList();
    private List<VSessionClinic> listVSessionClinic = new ArrayList();
    private final String[] columnNames = {"Date", "Inv No", "Patient", "Doctor", 
        "Vou-Total", "Currency", "Discount", "Tax", "Paid", "Vou-Balance", "Tran", 
        "Session", "Bill", "User"};
    
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
            case 0: //TranDate
                return String.class;
            case 1: //Inv No
            case 2: //Patient
            case 3: //Doctor
            case 5: //Currency
                return String.class;
            case 4: //Vou-Total
            case 6: //Discount
            case 7: //Tax
            case 8: //Paid
            case 9: //Vou-Balance
                return Double.class;
            case 10: //Tran Type
                return String.class;
            case 11: //Session
                return String.class;
            case 12: //Pay Type
                return String.class;
            case 13: //User
                return String.class;
            default:
                return null;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        VSessionClinic record = listVSessionClinic.get(row);

        switch (column) {
            case 0: //Tran Date
                return DateUtil.toDateStr(record.getTranDate(), "dd/MM/yyyy hh:mm aa");
            case 1: //Inv-No
                if(record.isDeleted()){
                    return record.getVouId() + "*";
                }else{
                    return record.getVouId();
                }
            case 2: //Patient
                return record.getPtName();
            case 3: //Doctor
                return record.getDrName();
            case 4: //Vou-Total
                return record.getVouTotal();
            case 5: //Currency
                return record.getCurrency();
            case 6: //Discount
                return record.getDiscount();
            case 7: //Tax
                return record.getTax();
            case 8: //Paid
                return record.getPaid();
            case 9: //Vou-Balance
                return record.getVouBalance();
            case 10: //Tran Type
                return record.getTranOption();
            case 11: //Session
                return record.getSessionName();
            case 12: //Pay Type
                return record.getPayTypeDesp();
            case 13: //User
                return record.getUserShort();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listVSessionClinic.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VSessionClinic> getListVSessionClinic() {
        return listSVSessionClinic;
    }

    public void setListVSessionClinic(List<VSessionClinic> listVSessionClinic) {
        this.listSVSessionClinic = listVSessionClinic;
        //fireTableDataChanged();
    }
    
    public VSessionClinic getVSessionClinic(int row){
        return listVSessionClinic.get(row);
    }
    
    public void applyFilter(String strFilter){
        listVSessionClinic = JoSQLUtil.getResult(strFilter, listSVSessionClinic);
        fireTableDataChanged();
        System.gc();
    }
}
