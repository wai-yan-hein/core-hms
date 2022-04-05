/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ReportChooseTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ReportChooseTableModel.class.getName());
    private final List<String> listR = new ArrayList();
    private final String[] columnNames = {"Report"};

    public ReportChooseTableModel(){
        listR.add("SHIFA");
        listR.add("Alpha");
    }
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
            case 0: //Name
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listR == null) {
            return null;
        }

        if (listR.isEmpty()) {
            return null;
        }

        try {
            String ajh = listR.get(row);

            switch (column) {
                case 0: //Date
                    return ajh;
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
        return listR.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public String getReport(int row){
        return listR.get(row);
    }
}
