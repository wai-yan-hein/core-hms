/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Menu;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ReportListTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ReportListTableModel.class.getName());
    private List<Menu> listReport = new ArrayList();
    private final String[] columnNames = {"Report Name"};

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
        if (listReport == null) {
            return null;
        }

        if (listReport.isEmpty()) {
            return null;
        }

        try {
            Menu menu = listReport.get(row);

            switch (column) {
                case 0: //Report Name
                    return menu.getMenuName();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getVaueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if(listReport == null){
            return 0;
        }
        return listReport.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Menu> getListReport() {
        return listReport;
    }

    public void setListReport(List<Menu> listReport) {
        this.listReport = listReport;
        fireTableDataChanged();
    }

    public Menu getSelectedReport(int row) {
        if(listReport == null){
            return null;
        }
        
        if(listReport.isEmpty()){
            return null;
        }
        
        if (row >= 0) {
            return listReport.get(row);
        } else {
            return null;
        }
    }
}
