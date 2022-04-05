/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.BusinessType;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author MyoGyi
 */
public class BusinessTypeTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(CustomerGroupTableModel.class.getName());
    private List<BusinessType> listBusinessType = new ArrayList();
    private final String[] columnNames = {"Business Type"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public int getRowCount() {
        if (listBusinessType == null) {
            return 0;
        }
        return listBusinessType.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listBusinessType == null) {
            return null;
        }

        if (listBusinessType.isEmpty()) {
            return null;
        }

        try {
            BusinessType businessType = listBusinessType.get(row);

            switch (column) {

                case 0: //Name
                    return businessType.getDescription();
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

    public List<BusinessType> getListBusinessType() {
        return listBusinessType;
    }

    public void setListBusinessType(List<BusinessType> listBusinessType) {
        this.listBusinessType = listBusinessType;
        fireTableDataChanged();
    }

    public BusinessType getBusinessType(int row) {
        if (listBusinessType != null) {
            if (!listBusinessType.isEmpty()) {
                return listBusinessType.get(row);
            }
        }
        return null;
    }

    public void setBusinessType(int row, BusinessType businessType) {
        if (listBusinessType != null) {
            if (!listBusinessType.isEmpty()) {
                listBusinessType.set(row, businessType);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addBusinessType(BusinessType businessType) {
        if (listBusinessType != null) {
            listBusinessType.add(businessType);
            fireTableRowsInserted(listBusinessType.size() - 1, listBusinessType.size() - 1);
        }
    }

    public void deleteChargeType(int row) {
        if (listBusinessType != null) {
            if (!listBusinessType.isEmpty()) {
                listBusinessType.remove(row);
                fireTableRowsDeleted(0, listBusinessType.size() - 1);
            }
        }
    }

}
