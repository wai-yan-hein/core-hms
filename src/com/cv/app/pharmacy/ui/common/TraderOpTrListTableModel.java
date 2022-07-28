/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class TraderOpTrListTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(TraderOpTrListTableModel.class.getName());
    private List<Trader> listTrader = new ArrayList();
    private final String[] columnNames = {"Code", "Name", "Township", "Active"};

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
        if (column == 3) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listTrader == null) {
            return null;
        }

        if (listTrader.isEmpty()) {
            return null;
        }

        try {
            Trader tr = listTrader.get(row);

            switch (column) {
                case 0: //Code
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        return tr.getStuCode();
                    } else {
                        return tr.getTraderId();
                    }
                case 1: //Name
                    return tr.getTraderName();
                case 2: //Township
                    if (tr.getTownship() == null) {
                        return null;
                    } else {
                        return tr.getTownship().getTownshipName();
                    }
                case 3: //Active
                    return tr.getActive();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listTrader == null) {
            return 0;
        }
        return listTrader.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Trader> getListTrader() {
        return listTrader;
    }

    public void setListTrader(List<Trader> listTrader) {
        this.listTrader = listTrader;
        fireTableDataChanged();
    }

    public Trader getTrader(int row) {
        if (listTrader == null) {
            return null;
        } else if (listTrader.isEmpty()) {
            return null;
        } else {
            return listTrader.get(row);
        }
    }
}
