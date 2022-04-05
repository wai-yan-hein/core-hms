/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Category;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class CategoryTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(CategoryTableModel.class.getName());
    private List<Category> listCategory = new ArrayList();
    private final String[] columnNames = {"Category"};

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
        if (listCategory == null) {
            return null;
        }

        if (listCategory.isEmpty()) {
            return null;
        }

        try {
            Category category = listCategory.get(row);

            switch (column) {
                case 0: //Name
                    return category.getCatName();
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
        if (listCategory == null) {
            return 0;
        }
        return listCategory.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Category> getListCategory() {
        return listCategory;
    }

    public void setListCategory(List<Category> listCategory) {
        this.listCategory = listCategory;
        fireTableDataChanged();
    }

    public Category getCategory(int row) {
        if (listCategory != null) {
            if (!listCategory.isEmpty()) {
                return listCategory.get(row);
            }
        }
        return null;
    }

    public void setCategory(int row, Category category) {
        if (listCategory != null) {
            if (!listCategory.isEmpty()) {
                listCategory.set(row, category);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addCategory(Category category) {
        if (listCategory != null) {
            listCategory.add(category);
            fireTableRowsInserted(listCategory.size() - 1, listCategory.size() - 1);
        }
    }

    public void deleteCategory(int row) {
        if (listCategory != null) {
            if (!listCategory.isEmpty()) {
                listCategory.remove(row);
                fireTableRowsDeleted(0, listCategory.size());
            }
        }
    }
}
