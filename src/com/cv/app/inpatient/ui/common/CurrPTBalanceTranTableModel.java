/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.inpatient.database.healper.CurrPTBalanceTran;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class CurrPTBalanceTranTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(CurrPTBalanceTranTableModel.class.getName());
    private List<CurrPTBalanceTran> list;
    private final String[] columnNames = {"Tran Type", "Tran Date", "Vou No.", "Item Name",
        "Qty", "Price", "Amount"};
    //private final AbstractDataAccess dao = Global.dao;
    //private Double total = 0.0;

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
            case 1: //Tran Date
                return Date.class;
            case 2: //Vou No
                return String.class;
            case 3: //Item Name
                return String.class;
            case 4: //Qty
                return String.class;
            case 5: //Price
                return Double.class;
            case 6: //Amount
                return Double.class;
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
            CurrPTBalanceTran record = list.get(row);

            switch (column) {
                case 0: //Tran Type
                    return record.getTranOption();
                case 1: //Tran Date
                    return record.getTranDate();
                case 2: //Vou No
                    return record.getVouNo();
                case 3: //Item Name
                    return record.getItemName();
                case 4: //Qty
                    return record.getQty();
                case 5: //Price
                    return record.getPrice();
                case 6: //Amount
                    return record.getAmount();
                default:
                    return Object.class;
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
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<CurrPTBalanceTran> getList() {
        return list;
    }

    public void setList(List<CurrPTBalanceTran> list) {
        this.list = list;
        fireTableDataChanged();
    }
    
    public void clear(){
        list = new ArrayList();
        fireTableDataChanged();
        System.gc();
    }
}
