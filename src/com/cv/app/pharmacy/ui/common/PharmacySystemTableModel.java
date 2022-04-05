/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.PharmacySystem;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PharmacySystemTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PharmacySystemTableModel.class.getName());
    private List<PharmacySystem> listPS = new ArrayList();
    private final String[] columnNames = {"System Desp"};

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
            case 0: //System Desp
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listPS == null) {
            return null;
        }

        if (listPS.isEmpty()) {
            return null;
        }

        try {
            PharmacySystem record = listPS.get(row);

            switch (column) {
                case 0: //System Desp
                    return record.getSystemDesp();
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
        if(listPS == null){
            return 0;
        }else{
            return listPS.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PharmacySystem> getListPS() {
        return listPS;
    }

    public void setListPS(List<PharmacySystem> listPS) {
        this.listPS = listPS;
        fireTableDataChanged();
    }

    public PharmacySystem getSelected(int row) {
        if(listPS != null){
            if(!listPS.isEmpty()){
                return listPS.get(row);
            }
        }
        return null;
    }
    
    public void delete(int row){
        if (listPS != null) {
            if (!listPS.isEmpty()) {
                listPS.remove(row);
                fireTableRowsDeleted(0, listPS.size());
            }
        }
    }
}
