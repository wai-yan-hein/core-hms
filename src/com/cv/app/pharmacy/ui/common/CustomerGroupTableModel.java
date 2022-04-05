/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.CustomerGroup;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class CustomerGroupTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(CustomerGroupTableModel.class.getName());
    private List<CustomerGroup> listCustomerGroup = new ArrayList();
    private final String[] columnNames = {"Code", "Name"};

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
        if (listCustomerGroup == null) {
            return null;
        }

        if (listCustomerGroup.isEmpty()) {
            return null;
        }

        try {
            CustomerGroup customerGroup = listCustomerGroup.get(row);

            switch (column) {
                case 0: //Code
                    return customerGroup.getGroupId();
                case 1: //Name
                    return customerGroup.getGroupName();
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
        if (listCustomerGroup == null) {
            return 0;
        }
        return listCustomerGroup.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<CustomerGroup> getListCustomerGroup() {
        return listCustomerGroup;
    }

    public void setListCustomerGroup(List<CustomerGroup> listCustomerGroup) {
        this.listCustomerGroup = listCustomerGroup;
        fireTableDataChanged();
    }

    public CustomerGroup getCustomerGroup(int row) {
        if (listCustomerGroup != null) {
            if (!listCustomerGroup.isEmpty()) {
                return listCustomerGroup.get(row);
            }
        }
        return null;
    }

    public void setCustomerGroup(int row, CustomerGroup customerGroup) {
        if (listCustomerGroup != null) {
            if (!listCustomerGroup.isEmpty()) {
                listCustomerGroup.set(row, customerGroup);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addCustomerGroup(CustomerGroup customerGroup) {
        if (listCustomerGroup != null) {
            listCustomerGroup.add(customerGroup);
            fireTableRowsInserted(listCustomerGroup.size() - 1, listCustomerGroup.size() - 1);
        }
    }

    public void deleteChargeType(int row) {
        if (listCustomerGroup != null) {
            if (!listCustomerGroup.isEmpty()) {
                listCustomerGroup.remove(row);
                fireTableRowsDeleted(0, listCustomerGroup.size() - 1);
            }
        }
    }
}
