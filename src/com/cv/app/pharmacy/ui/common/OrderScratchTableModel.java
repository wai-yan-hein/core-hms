/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.OrderScratchData;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OrderScratchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OrderScratchTableModel.class.getName());
    private List<OrderScratchData> list = new ArrayList();
    private final String[] columnNames = {"Item ID", "Item Name", "Active", "Category",
        "System", "Brand Name", "Sup Code", "Supplier Name", "Phone"};

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
        if (column == 2) {
            return Boolean.class;
        } else {
            return String.class;
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
            OrderScratchData record = list.get(row);

            switch (column) {
                case 0: //Item ID
                    return record.getMedId();
                case 1: //Item Name
                    return record.getMedName();
                case 2: //Active
                    return record.getActive();
                case 3: //Category
                    return record.getCatName();
                case 4: //System
                    return record.getSysName();
                case 5: //Brand Name
                    return record.getBrandName();
                case 6: //Sup Code
                    return record.getSupId();
                case 7: //Supplier Name
                    return record.getSupName();
                case 8: //Phone
                    return record.getPhoneNo();
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
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OrderScratchData> getList() {
        return list;
    }

    public void setList(List<OrderScratchData> list) {
        this.list = list;
        fireTableDataChanged();
    }
}
