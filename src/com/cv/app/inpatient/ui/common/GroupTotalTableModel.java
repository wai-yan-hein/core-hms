/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.pharmacy.database.helper.SessionTtl;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class GroupTotalTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(GroupTotalTableModel.class.getName());
    private List<SessionTtl> listBal;
    private final String[] columnNames = {"Tran Type", "Total"};
    
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
            case 0: //Tran Type
                return String.class;
            case 1: //Amount
                return Double.class;
            default:
                return Object.class;
        }

    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listBal == null) {
            return null;
        }

        if (listBal.isEmpty()) {
            return null;
        }

        try {
            SessionTtl record = listBal.get(row);

            switch (column) {
                case 0: //Tran Type
                    return record.getDesc();
                case 1: //Amount
                    return record.getTtlPaid();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listBal == null) {
            return 0;
        } else {
            return listBal.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<SessionTtl> getListBal() {
        return listBal;
    }

    public void setListBal(List<SessionTtl> listBal) {
        this.listBal = listBal;
        fireTableDataChanged();
    }
    
    public void clear(){
        listBal = new ArrayList();
        fireTableDataChanged();
        System.gc();
    }
}
