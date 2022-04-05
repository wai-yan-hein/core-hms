/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.ItemUnit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ItemUnitTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ItemUnitTableModel.class.getName());
    private List<ItemUnit> listItemUnit = new ArrayList();
    private final String[] columnNames = {"Unit-S", "Unit Name"};

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
        if (listItemUnit == null) {
            return null;
        }

        if (listItemUnit.isEmpty()) {
            return null;
        }

        try {
            ItemUnit itemUnit = listItemUnit.get(row);

            switch (column) {
                case 0: //Unit Short
                    return itemUnit.getItemUnitCode();
                case 1: //Unit Name
                    return itemUnit.getItemUnitName();
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
        if (listItemUnit == null) {
            return 0;
        }
        return listItemUnit.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<ItemUnit> getListItemUnit() {
        return listItemUnit;
    }

    public void setListItemUnit(List<ItemUnit> listItemUnit) {
        this.listItemUnit = listItemUnit;
        fireTableDataChanged();
    }

    public ItemUnit getItemUnit(int row) {
        if (listItemUnit != null) {
            if (!listItemUnit.isEmpty()) {
                return listItemUnit.get(row);
            }
        }
        return null;
    }

    public void setItemUnit(int row, ItemUnit itemUnit) {
        if (listItemUnit != null) {
            if (!listItemUnit.isEmpty()) {
                listItemUnit.set(row, itemUnit);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addItemUnit(ItemUnit itemUnit) {
        if (listItemUnit != null) {
            listItemUnit.add(itemUnit);
            fireTableRowsInserted(listItemUnit.size() - 1, listItemUnit.size() - 1);
        }
    }

    public void deleteItemUnit(int row) {
        if (listItemUnit != null) {
            if (!listItemUnit.isEmpty()) {
                listItemUnit.remove(row);
                fireTableRowsDeleted(0, listItemUnit.size());
            }
        }
    }
}
