/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.ItemBrand;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class BrandTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(BrandTableModel.class.getName());
    private List<ItemBrand> listItemBrand = new ArrayList();
    private final String[] columnNames = {"Brand Name"};

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
        if (listItemBrand == null) {
            return null;
        }

        if (listItemBrand.isEmpty()) {
            return null;
        }

        try {
            ItemBrand brand = listItemBrand.get(row);

            switch (column) {
                case 0: //Name
                    return brand.getBrandName();
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
        if (listItemBrand == null) {
            return 0;
        }
        return listItemBrand.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<ItemBrand> getListItemBrand() {
        return listItemBrand;
    }

    public void setListItemBrand(List<ItemBrand> listItemBrand) {
        this.listItemBrand = listItemBrand;

        fireTableDataChanged();
    }

    public ItemBrand getItemBrand(int row) {
        if (listItemBrand != null) {
            if (!listItemBrand.isEmpty()) {
                return listItemBrand.get(row);
            }
        }
        return null;
    }

    public void setItemBrand(int row, ItemBrand itemBrand) {
        listItemBrand.set(row, itemBrand);
        fireTableRowsUpdated(row, row);
    }

    public void addItemBrand(ItemBrand itemBrand) {
        if (listItemBrand != null) {
            listItemBrand.add(itemBrand);
            fireTableRowsInserted(listItemBrand.size() - 1, listItemBrand.size() - 1);
        }
    }

    public void deleteItemBrand(int row) {
        if (listItemBrand != null) {
            if (!listItemBrand.isEmpty()) {
                listItemBrand.remove(row);
                fireTableRowsDeleted(0, listItemBrand.size());
            }
        }
    }
}
