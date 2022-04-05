/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.view.VMedicine1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class MedListTableModel1 extends AbstractTableModel {

    static Logger log = Logger.getLogger(MedListTableModel1.class.getName());
    private List<VMedicine1> listMedicine = new ArrayList();
    private final String[] columnNames = {"Item-Code", "Item Name", "Item Type", 
        "Brand", "Category", "System"};

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
        if (listMedicine == null) {
            return null;
        }

        if (listMedicine.isEmpty()) {
            return null;
        }

        try {
            VMedicine1 med = listMedicine.get(row);

            switch (column) {
                case 0: //Item-Code
                    return med.getMedId();
                case 1: //Item Name
                    return med.getMedName();
                case 2: //Item Type
                    return med.getMedTypeName();
                case 3: //Brand
                    return med.getBrandName();
                case 4: //Category
                    return med.getCatName();
                case 5: //System
                    return med.getSystem();
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
        if (listMedicine == null) {
            return 0;
        }
        return listMedicine.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VMedicine1> getListMedicine() {
        return listMedicine;
    }

    public void setListMedicine(List<VMedicine1> listMedicine) {
        this.listMedicine = listMedicine;
        fireTableDataChanged();
    }

    public VMedicine1 getMedicine(int row) {
        if (listMedicine != null) {
            if (!listMedicine.isEmpty()) {
                return listMedicine.get(row);
            }
        }
        return null;
    }

    public void setMedicine(int row, VMedicine1 med) {
        if (listMedicine != null) {
            if (!listMedicine.isEmpty()) {
                listMedicine.set(row, med);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addMedicine(VMedicine1 med) {
        if (listMedicine != null) {
            listMedicine.add(med);
            fireTableRowsInserted(listMedicine.size() - 1, listMedicine.size() - 1);
        }
    }

    public void deleteMedicine(int row) {
        if (listMedicine != null) {
            if (!listMedicine.isEmpty()) {
                listMedicine.remove(row);
                fireTableRowsDeleted(0, listMedicine.size());
            }
        }
    }
}
