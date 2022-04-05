/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.auditlog;

import com.cv.app.util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class SaleAuditTableModel extends AbstractTableModel{
    private List<SaleAuditLog> listSAL = new ArrayList();
    private final String[] columnNames = { "Vou No", "Bak-Date", "Vou-Date", "Vou Total", "User"};
    
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
            case 0: //Vou No
                return String.class;
            case 1: //Bak-Date
                return String.class;
            case 2: //Vou-Date
                return String.class;
            case 3: //Total
                return Double.class;
            case 4: //User
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(listSAL == null){
            return null;
        }
        
        if(listSAL.isEmpty()){
            return null;
        }
        
        SaleAuditLog record = listSAL.get(row);
        switch (column) {
            case 0: //Vou No
                return record.getVouNo();
            case 1: //Bak-Date
                return DateUtil.toDateStr(record.getTranDate(),"dd/MM/yyyy HH:mm:ss");
            case 2: //Vou-Date
                return DateUtil.toDateStr(record.getVouDate());
            case 3: //Total
                return record.getVouTotal();
            case 4: //User
                return record.getTranUser();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listSAL.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<SaleAuditLog> getListSAL() {
        return listSAL;
    }

    public void setListSAL(List<SaleAuditLog> listSAL) {
        this.listSAL = listSAL;
        fireTableDataChanged();
    }
    
    public SaleAuditLog getSaleAuditLog(int rowIndex){
        SaleAuditLog sal = null;
        if(rowIndex >= 0 && rowIndex < listSAL.size()){
            sal = listSAL.get(rowIndex);
        }
        return sal;
    }
}
