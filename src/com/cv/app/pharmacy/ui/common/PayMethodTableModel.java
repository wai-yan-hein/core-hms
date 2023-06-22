/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.PayMethod;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PayMethodTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PayMethodTableModel.class.getName());
    private List<PayMethod> list = new ArrayList();
    private final String[] columnNames = {"Description", "Code", "Factor"};

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
            case 0: //Description
                return String.class;
            case 1: //Code
                return String.class;
            case 2: //Factor
                return Integer.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return null;
        }

        try {
            PayMethod record = list.get(row);

            switch (column) {
                case 0: //Description
                    return record.getMethodDesp();
                case 1: //Code
                    return record.getGroupCode();
                case 2: //Factor
                    return record.getFactor();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PayMethod> getList() {
        return list;
    }

    public void setList(List<PayMethod> list) {
        this.list = list;
        fireTableDataChanged();
    }
    

    public PayMethod getPayMethod(int row){
        if(list != null){
            if(!list.isEmpty()){
                return list.get(row);
            }
        }
        return null;
    }

    public void setPayMethod(int row, PayMethod pb){
        if(list != null){
            if(!list.isEmpty()){
                list.set(row, pb);
                fireTableRowsUpdated(row, row);
            }
        }
    }
    
    public void addPayMethod(PayMethod pb){
        if(list != null){
            list.add(pb);
            fireTableRowsInserted(list.size() - 1, list.size() - 1);
        }
    }

    public void delete(int row) {
        if (list != null) {
            if (!list.isEmpty()) {
                list.remove(row);
                fireTableRowsDeleted(0, list.size());
            }
        }
    }
}
