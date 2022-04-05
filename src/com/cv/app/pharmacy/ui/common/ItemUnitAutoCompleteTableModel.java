/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.ItemUnit;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ItemUnitAutoCompleteTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ItemUnitAutoCompleteTableModel.class.getName());
    private List<ItemUnit> listItemUnit;
    private final String[] columnNames = {"Unit", "Description"};

    public ItemUnitAutoCompleteTableModel(List<ItemUnit> listItemUnit) {
        this.listItemUnit = listItemUnit;
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
        if (listItemUnit == null) {
            return null;
        }

        if (listItemUnit.isEmpty()) {
            return null;
        }

        try {
            ItemUnit unit = listItemUnit.get(row);
            switch (column) {
                case 0: //Unit
                    return unit.getItemUnitCode();
                case 1: //Description
                    return unit.getItemUnitName();
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
        if(listItemUnit == null){
            return 0;
        }
        return listItemUnit.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public ItemUnit getItemUnit(int row) {
        if(listItemUnit != null){
            if(!listItemUnit.isEmpty()){
                return listItemUnit.get(row);
            }
        }
        return null;
    }

    public int getSize() {
        if(listItemUnit == null){
            return 0;
        }
        return listItemUnit.size();
    }
}
