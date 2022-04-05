/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Medicine;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class MedListTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(MedListTableModel.class.getName());
    private List<Medicine> listMedicine = new ArrayList();
    private final String[] columnNames = {"Item-Code", "Item Name", "Item Type", "Brand", "Category"};

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
            Medicine med = listMedicine.get(row);

            switch (column) {
                case 0: //Item-Code
                    return med.getMedId();
                case 1: //Item Name
                    return med.getMedName();
                case 2: //Item Type
                    if (med.getMedTypeId() != null) {
                        return med.getMedTypeId().getItemTypeName();
                    } else {
                        return null;
                    }
                case 3: //Brand
                    if (med.getBrand() != null) {
                        return med.getBrand().getBrandName();
                    } else {
                        return null;
                    }
                case 4: //Category
                    if (med.getCatId() != null) {
                        return med.getCatId().getCatName();
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
        if (listMedicine == null) {
            return 0;
        }
        return listMedicine.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Medicine> getListMedicine() {
        return listMedicine;
    }

    public void setListMedicine(List<Medicine> listMedicine) {
        this.listMedicine = listMedicine;
        fireTableDataChanged();
    }

    public Medicine getMedicine(int row) {
        if (listMedicine != null) {
            if (!listMedicine.isEmpty()) {
                return listMedicine.get(row);
            }
        }
        return null;
    }

    public void setMedicine(int row, Medicine med) {
        if (listMedicine != null) {
            if (!listMedicine.isEmpty()) {
                listMedicine.set(row, med);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addMedicine(Medicine med) {
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
