/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.TTranDetail;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class TTranDetailTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(TTranDetailTableModel.class.getName());
    private List<TTranDetail> listTTranDetail = new ArrayList();
    private final String[] columnNames = {"Particular", "Qty", "Price", "Amount"};

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
            case 0: //Particular
            case 1: //Qty
                return String.class;
            case 2: //Price
            case 3: //Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listTTranDetail == null) {
            return null;
        }

        if (listTTranDetail.isEmpty()) {
            return null;
        }

        try {
            TTranDetail ttd = listTTranDetail.get(row);

            switch (column) {
                case 0: //Particular
                    return ttd.getMedName();
                case 1: //Qty
                    return ttd.getQtyStr();
                case 2: //Price
                    return ttd.getPrice();
                case 3: //Amount
                    return ttd.getAmount();
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
        if (listTTranDetail == null) {
            return 0;
        }
        return listTTranDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<TTranDetail> getListTTranDetail() {
        return listTTranDetail;
    }

    public void setListTTranDetail(List<TTranDetail> listTTranDetail) {
        this.listTTranDetail = listTTranDetail;
        fireTableDataChanged();
    }

    public void clear() {
        listTTranDetail.removeAll(listTTranDetail);
        fireTableDataChanged();
    }
}
