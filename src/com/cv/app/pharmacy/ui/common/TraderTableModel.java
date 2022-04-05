/*
 * To change this template, choose Tools | Templates
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
public class TraderTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(TraderTableModel.class.getName());
    private List<Trader> listTrader = new ArrayList();
    private String[] columnNames = {"Code", "Name", "Active"};

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
        if (listTrader == null) {
            return null;
        }

        if (listTrader.isEmpty()) {
            return null;
        }

        try {
            Trader trader = listTrader.get(row);

            switch (column) {
                case 0: //Code
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        return trader.getTraderId().replace("CUS", "").replace("SUP", "");
                    } else {
                        return trader.getTraderId();
                    }
                case 1: //Name
                    return trader.getTraderName();
                case 2: //Active
                    return trader.getActive();
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
        if (listTrader == null) {
            return 0;
        }
        return listTrader.size();
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

    public List<Trader> getListTrader() {
        return listTrader;
    }

    public void setListTrader(List<Trader> listTrader) {
        this.listTrader = listTrader;
        fireTableDataChanged();
    }

    public Trader getTrader(int row) {
        return listTrader.get(row);
    }

    public void deleteTrader(int row) {
        if (listTrader != null) {
            if (!listTrader.isEmpty()) {
                listTrader.remove(row);
                fireTableRowsDeleted(0, listTrader.size());
            }
        }
    }

    public void addTrader(Trader trader) {
        if (listTrader != null) {
            listTrader.add(trader);
            fireTableRowsInserted(listTrader.size() - 1, listTrader.size() - 1);
            fireTableDataChanged();
        }
    }

    public void setTrader(int row, Trader trader) {
        if (listTrader != null) {
            if (!listTrader.isEmpty()) {
                listTrader.set(row, trader);
                fireTableRowsUpdated(row, row);
            }
        }
    }
}
