/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.util.Util1;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class TraderAutoCompleteTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(TraderAutoCompleteTableModel.class.getName());
    private List<Trader> listTrader;
    private final String[] columnNames = {"Code", "Name"};

    public TraderAutoCompleteTableModel(List<Trader> listTrader) {
        this.listTrader = listTrader;
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
        return String.class;
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
                        return trader.getStuCode();
                    } else {
                        return trader.getTraderId();
                    }

                case 1: //Name
                    return trader.getTraderName();
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

    public Trader getTrader(int row) {
        if (listTrader == null) {
            if (listTrader.isEmpty()) {
                return null;
            }
        }
        return listTrader.get(row);
    }

    public int getSize() {
        if (listTrader == null) {
            return 0;
        }
        return listTrader.size();
    }
}
