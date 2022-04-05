/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.ItemType;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ItemTypeTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ItemTypeTableModel.class.getName());
    private List<ItemType> listItemType = new ArrayList();
    private final String[] columnNames = {"Code", "Type Name"};

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
        if (listItemType == null) {
            return null;
        }

        if (listItemType.isEmpty()) {
            return null;
        }

        try {
            ItemType itemType = listItemType.get(row);

            switch (column) {
                case 0: //ID
                    return itemType.getItemTypeCode();
                case 1: //Name
                    return itemType.getItemTypeName();
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
        if (listItemType == null) {
            return 0;
        }
        return listItemType.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<ItemType> getListItemType() {
        return listItemType;
    }

    public void setListItemType(List<ItemType> listItemType) {
        this.listItemType = listItemType;
        fireTableDataChanged();
    }

    public ItemType getItemType(int row) {
        if (listItemType != null) {
            if (!listItemType.isEmpty()) {
                return listItemType.get(row);
            }
        }
        return null;
    }

    public void setItemType(int row, ItemType itemType) {
        if (listItemType != null) {
            if (!listItemType.isEmpty()) {
                listItemType.set(row, itemType);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addItemType(ItemType itemType) {
        if (listItemType != null) {
            listItemType.add(itemType);
            fireTableRowsInserted(listItemType.size() - 1, listItemType.size() - 1);
        }
    }

    public void deleteItemType(int row) {
        if (listItemType != null) {
            if (!listItemType.isEmpty()) {
                listItemType.remove(row);
                fireTableRowsDeleted(0, listItemType.size() - 1);
            }
        }
    }
}
