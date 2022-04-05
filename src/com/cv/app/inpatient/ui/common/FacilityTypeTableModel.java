/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.inpatient.database.entity.FacilityType;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class FacilityTypeTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(FacilityTypeTableModel.class.getName());
    private List<FacilityType> list = new ArrayList();
    private final String[] columnNames = {"Type Name", "Price"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Type Name
                return String.class;
            case 1: //Price
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
            FacilityType record = list.get(row);

            switch (column) {
                case 0: //Type Name
                    return record.getTypeDesp();
                case 1: //Price
                    return record.getPrice();
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
        fireTableCellUpdated(row, column);
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

    public void deleteRow(int index) {
        if (list != null) {
            if (!list.isEmpty()) {
                list.remove(index);
                fireTableDataChanged();
            }
        }
    }

    public List<FacilityType> getList() {
        return list;
    }

    public void setList(List<FacilityType> list) {
        this.list = list;
        fireTableDataChanged();
    }

    public void add(FacilityType ft) {
        if (list != null) {
            list.add(ft);
            fireTableDataChanged();
        }
    }
}
