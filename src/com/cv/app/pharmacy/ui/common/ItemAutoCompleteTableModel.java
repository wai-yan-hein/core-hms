/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.view.VMedicine1;
import com.cv.app.util.Util1;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ItemAutoCompleteTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ItemAutoCompleteTableModel.class.getName());
    private List<VMedicine1> listItem;
    private final String[] columnNames = {"Code", "Description", "Relation-Str", "Barcode", "Category"};
    private final String codeUsage = Util1.getPropValue("system.item.code.field");

    public ItemAutoCompleteTableModel(List<VMedicine1> listItem) {
        this.listItem = listItem;
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
        if (listItem == null) {
            return null;
        }

        if (listItem.isEmpty()) {
            return null;
        }

        try {
            VMedicine1 med = listItem.get(row);

            switch (column) {
                case 0: //Code
                    if (codeUsage.equals("SHORTNAME")) {
                        return med.getShortName();
                    } else {
                        return med.getMedId();
                    }
                case 1: //Description
                    return med.getMedName();
                case 2: //Relation-Str
                    return med.getRelStr();
                case 3: //Barcode
                    return med.getBarcode();
                case 4: //Category
                    if (med.getCatId() != null) {
                        //return med.getCatId().getCatName();
                        return med.getCatName();
                    } else {
                        return null;
                    }
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
        if (listItem == null) {
            return 0;
        } else {
            return listItem.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public VMedicine1 getItem(int row) {
        if (listItem == null) {
            return null;
        } else if (listItem.isEmpty()) {
            return null;
        } else {
            return listItem.get(row);
        }
    }

    public int getSize() {
        if (listItem == null) {
            return 0;
        } else {
            return listItem.size();
        }
    }
}
