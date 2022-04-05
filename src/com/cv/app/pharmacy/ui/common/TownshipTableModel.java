/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Township;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class TownshipTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(TownshipTableModel.class.getName());
    private List<Township> listTownship = new ArrayList();
    private final String[] columnNames = {"Township"};

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
        if (listTownship == null) {
            return null;
        }

        if (listTownship.isEmpty()) {
            return null;
        }

        try {
            Township township = listTownship.get(row);

            switch (column) {
                case 0: //Name
                    return township.getTownshipName();
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
        if (listTownship == null) {
            return 0;
        }
        return listTownship.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Township> getListTownship() {
        return listTownship;
    }

    public void setListTownship(List<Township> listTownship) {
        this.listTownship = listTownship;
        fireTableDataChanged();
    }

    public Township getTownship(int row) {
        if (listTownship != null) {
            if (!listTownship.isEmpty()) {
                return listTownship.get(row);
            }
        }
        return null;
    }

    public void setTownship(int row, Township township) {
        if (listTownship != null) {
            listTownship.set(row, township);
            fireTableRowsUpdated(row, row);
        }
    }

    public void addTownship(Township township) {
        if (listTownship != null) {
            listTownship.add(township);
            fireTableRowsInserted(listTownship.size() - 1, listTownship.size() - 1);
        }
    }

    public void deleteTownship(int row) {
        if (listTownship != null) {
            if (!listTownship.isEmpty()) {
                listTownship.remove(row);
                fireTableRowsDeleted(0, listTownship.size() - 1);
            }
        }
    }
}
