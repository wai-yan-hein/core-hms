/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class AutoCompleterTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(AutoCompleterTableModel.class.getName());
    private List listData;
    private Object[] columns;

    public AutoCompleterTableModel(List listData, Object[] columns) {
        this.listData = listData;
        this.columns = columns;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column].toString();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Option
            case 1: //Vou No
            case 2: //Med Code
            case 3: //Medicine
            case 4: //T-Code
            case 5: //Trader Name
            case 6: //Outstanding
            case 10: //Balance
                return String.class;
            case 7: //Exp-Date
                return Date.class;
            case 8: //Qty
                return Integer.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listData == null) {
            return null;
        }

        if (!listData.isEmpty()) {
            return null;
        }

        try {
            Object[] tmpObj = (Object[]) listData.get(row);
            return tmpObj[column].toString();
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
        if(listData == null){
            return 0;
        }
        return listData.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }
}
