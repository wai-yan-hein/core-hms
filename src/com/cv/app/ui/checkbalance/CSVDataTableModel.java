/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.checkbalance;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author wswe
 */
public class CSVDataTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(CSVDataTableModel.class.getName());
    private final List<PatientBalance> listDetail = new ArrayList();
    private final String[] columnNames = {"Reg No.", "Name", "Rpt Balance"};

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
            case 0: //Reg No
            case 1: //Name
                return String.class;
            case 2: //Rpt Balance
                return Double.class;
            default:
                return Object.class;
        }

    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail == null) {
            return null;
        }

        if (listDetail.isEmpty()) {
            return null;
        }

        try {
            PatientBalance record = listDetail.get(row);

            switch (column) {
                case 0: //Reg No
                    return record.getRegNo();
                case 1: //Name
                    return record.getPtName();
                case 2: //Rpt Balance
                    return record.getRptBalance();
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
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public void addData(PatientBalance data){
        listDetail.add(data);
        fireTableDataChanged();
    }
}
