/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.ChargeType;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ChargeTypeTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ChargeTypeTableModel.class.getName());
    private List<ChargeType> listChargeType = new ArrayList();
    private final String[] columnNames = {"Charge Type"};

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
        if (listChargeType == null) {
            return null;
        }

        if (listChargeType.isEmpty()) {
            return null;
        }

        try {
            ChargeType chargeType = listChargeType.get(row);

            switch (column) {
                case 0: //Charge Type
                    return chargeType.getChargeTypeDesc();
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
        if (listChargeType == null) {
            return 0;
        }
        return listChargeType.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<ChargeType> getListChargeType() {
        return listChargeType;
    }

    public void setListChargeType(List<ChargeType> listChargeType) {
        this.listChargeType = listChargeType;
        fireTableDataChanged();
    }

    public ChargeType getChargeType(int row) {
        if (listChargeType != null) {
            if (!listChargeType.isEmpty()) {
                return listChargeType.get(row);
            }
        }
        return null;
    }

    public void setChargeType(int row, ChargeType chargeType) {
        if (listChargeType != null) {
            if (!listChargeType.isEmpty()) {
                listChargeType.set(row, chargeType);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addChargeType(ChargeType chargeType) {
        if (listChargeType != null) {
            listChargeType.add(chargeType);
            fireTableRowsInserted(listChargeType.size() - 1, listChargeType.size() - 1);
        }
    }

    public void deleteChargeType(int row) {
        if (listChargeType != null) {
            if (!listChargeType.isEmpty()) {
                listChargeType.remove(row);
                fireTableRowsDeleted(0, listChargeType.size() - 1);
            }
        }
    }
}
