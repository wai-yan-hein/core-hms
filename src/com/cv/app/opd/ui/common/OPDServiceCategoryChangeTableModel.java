/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.helper.OPDServiceView;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OPDServiceCategoryChangeTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OPDServiceCategoryChangeTableModel.class.getName());

    private List<OPDServiceView> list = new ArrayList();
    private final String[] columnNames = {"Group", "Category", "Service"};
    private final AbstractDataAccess dao = Global.dao;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 1;
    }

    @Override
    public Class getColumnClass(int column) {

        switch (column) {
            case 0: //Group
                return String.class;
            case 1: //Category
                return String.class;
            case 2: // Service
                return String.class;
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

        OPDServiceView record = list.get(row);
        switch (column) {
            case 0: //Group Name
                return record.getGroupName();
            case 1: //Category
                return record.getCatName();
            case 2: //Service
                return record.getServiceName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (list == null) {
            return;
        }

        if (list.isEmpty()) {
            return;
        }

        OPDServiceView record = list.get(row);
        switch (column) {
            case 1: //Category
                
            break;
        }
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void setList(List<OPDServiceView> list) {
        this.list = list;
        fireTableDataChanged();
    }
    
    public void clear(){
        this.list = new ArrayList();
        fireTableDataChanged();
    }

}
