/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.PurDetailHis;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PurchaseIMEITableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PurchaseIMEITableModel.class.getName());
    private List<PurDetailHis> listDetail;
    private final String[] columnNames = {"Code", "Description", "Relation-Str",
        "Qty", "Unit"};

    public PurchaseIMEITableModel() {

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
        switch (column) {
            case 0: //Code
            case 1: //Medicine Name
            case 2: //Relation-Str
                return String.class;
            case 3: //Qty
                return Float.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail == null) {
            return null;
        }

        if (listDetail.isEmpty()) {
            return null;
        }

        try {
            PurDetailHis record = listDetail.get(row);

            switch (column) {
                case 0: //Code
                    if (record.getMedId() == null) {
                        return null;
                    } else {
                        return record.getMedId().getMedId();
                    }
                case 1: //Medicine Name
                    if (record.getMedId() == null) {
                        return null;
                    } else {
                        return record.getMedId().getMedName();
                    }
                case 2: //Relation-Str
                    if (record.getMedId() == null) {
                        return null;
                    } else {
                        return record.getMedId().getRelStr();
                    }
                case 3: //Qty
                    return record.getQuantity();
                case 4: //Unit
                    return record.getUnitId();
                default:
                    return new Object();
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
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void setListDetail(List<PurDetailHis> listDetail) {

        this.listDetail = listDetail;
        Collections.copy(this.listDetail, listDetail);
        fireTableDataChanged();

    }

    public PurDetailHis getPurDetailHis(int index) {
        if (listDetail == null) {
            return null;
        }
        if (listDetail.isEmpty()) {
            return null;
        } else {
            return listDetail.get(index);
        }
    }

    public void clear() {
        if (listDetail != null) {
            listDetail.clear();
            fireTableDataChanged();
        }

    }
}
