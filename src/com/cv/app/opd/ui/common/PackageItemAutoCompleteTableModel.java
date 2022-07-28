/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.view.VUnionItem;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PackageItemAutoCompleteTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PackageItemAutoCompleteTableModel.class.getName());
    private List<VUnionItem> listPI = null;
    private final String[] columnNames = {"Code", "Item Name", "Item Type"};
    private final AbstractDataAccess dao = Global.dao;

    public PackageItemAutoCompleteTableModel() {
        try {
            listPI = dao.findAllHSQL("select o from VUnionItem o");
        } catch (Exception ex) {
            log.error("PackageItemAutoCompleteTableModel : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

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
        if (listPI == null) {
            return null;
        } else if (listPI.isEmpty()) {
            return null;
        }

        VUnionItem record = listPI.get(row);

        switch (column) {
            case 0: //Code
                return record.getItemCode();
            case 1: //Item Name
                return record.getItemName();
            case 2: //Item Type
                return record.getItemType();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listPI.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public VUnionItem getItem(int index) {
        VUnionItem vui = null;
        if (listPI != null) {
            if (!listPI.isEmpty()) {
                vui = listPI.get(index);
            }
        }
        return vui;
    }

    public int getSize() {
        if (listPI == null) {
            return 0;
        } else if (listPI.isEmpty()) {
            return 0;
        } else {
            return listPI.size();
        }
    }
}
