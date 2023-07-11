/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.BillOpeningHis;
import com.cv.app.util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class OpenBillPatientTableModel extends AbstractTableModel{
    private List<BillOpeningHis> list = new ArrayList();
    private final String[] columnNames = {"OP Date", "Bill Id", "Reg No.", "Adm No.", "Name"};
    
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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(list == null){
            return null;
        }
        
        if(list.isEmpty()){
            return null;
        }
        
        BillOpeningHis record = list.get(row);

        switch (column) {
            case 0: //OP Date
                return DateUtil.toDateStr(record.getBillOPDate(), "dd/MM/yyyy hh:mm aa");
            case 1: //Bill Id
                return record.getBillId();
            case 2: //Reg No.
                return record.getRegNo();
            case 3: //Adm No.
                return record.getAdmNo();
            case 4: //Name
                return record.getPtName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<BillOpeningHis> getList() {
        return list;
    }

    public void setList(List<BillOpeningHis> list) {
        this.list = list;
        fireTableDataChanged();
    }
    
    public BillOpeningHis getData(int index){
        if(list == null){
            return null;
        }
        
        if(list.isEmpty()){
            return null;
        }
        
        return list.get(index);
    }
}
