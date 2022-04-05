/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.view.VMedicine1;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class ItemTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(AdjSearchTableModel.class.getName());
    private List<VMedicine1> listMedicine = new ArrayList();
    private String[] columnNames = {"Code", "Description", "Active", "Barcode"};
    private final String codeUsage = Util1.getPropValue("system.item.code.field");

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
        if (listMedicine == null) {
            return null;
        }

        if (listMedicine.isEmpty()) {
            return null;
        }

        try {
            VMedicine1 med = listMedicine.get(row);

            switch (column) {
                case 0: //Code
                    if (codeUsage.equals("SHORTNAME")) {
                        return med.getShortName();
                    } else {
                        return med.getMedId();
                    }
                case 1: //Name
                    return med.getMedName();
                case 2: //Active
                    return med.getActive();
                case 3:
                    return med.getBarcode();
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

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
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

    public void deleteItem(int row) {
        if (listMedicine != null) {
            if (!listMedicine.isEmpty()) {
                listMedicine.remove(row);
                fireTableDataChanged();
            }
        }
    }
}
