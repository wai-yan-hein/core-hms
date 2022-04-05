/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.emr.ui.common;

import com.cv.app.emr.database.entity.AgeRange;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class AgeRangeTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(AgeRangeTableModel.class.getName());
    private List<AgeRange> listAR = new ArrayList();
    private final String[] columnNames = {"Description", "Sort Order"};

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
        if (column == 1) {
            return Integer.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listAR != null) {
            if (!listAR.isEmpty()) {
                try {
                    AgeRange record = listAR.get(row);

                    switch (column) {
                        case 0: //Description
                            return record.getAgerDesp();
                        case 1: //Sort Order
                            return record.getSortOrder();
                        default:
                            return null;
                    }
                } catch (Exception ex) {
                    log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
                }
            }
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listAR == null) {
            return 0;
        } else {
            return listAR.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<AgeRange> getListAR() {
        return listAR;
    }

    public void setListAR(List<AgeRange> listAR) {
        this.listAR = listAR;
        if (this.listAR != null) {
            fireTableDataChanged();
        }
    }

    public AgeRange getAgeRange(int row) {
        if (listAR != null) {
            if (!listAR.isEmpty()) {
                return listAR.get(row);
            }
        }
        return null;
    }

    public void setAgeRange(int row, AgeRange ar) {
        if (listAR != null) {
            if (!listAR.isEmpty()) {
                listAR.set(row, ar);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addAgeRange(AgeRange ar) {
        if (listAR != null) {
            if (!listAR.isEmpty()) {
                listAR.add(ar);
                fireTableRowsInserted(listAR.size() - 1, listAR.size() - 1);
            }
        }
    }

    public void deleteAgeRange(int row) {
        if (listAR != null) {
            if (!listAR.isEmpty()) {
                listAR.remove(row);
                fireTableRowsDeleted(0, listAR.size() - 1);
            }
        }
    }
}
