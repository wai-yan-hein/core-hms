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
 * @author WSwe
 */
public class TraderListTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(TraderListTableModel.class.getName());
    private List<Trader> listTrader = new ArrayList();
    private final String[] columnNames = {"ID", "Name", "Type", "Township"};
    private final String prifxStatus = Util1.getPropValue("system.sale.emitted.prifix");
    
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
                case 0: //ID
                    if (prifxStatus.equals("Y")) {
                        return trader.getTraderId().replace("CUS", "").replace("SUP", "");
                    } else {
                        return trader.getTraderId();
                    }
                case 1: //Name
                    return trader.getTraderName();
                case 2: //Type
                    String type;
                    if (trader.getTraderId().contains("CUS")) {
                        type = "Customer";
                    } else {
                        type = "Supplier";
                    }
                    return type;
                case 3: //Township
                    if (trader.getTownship() != null) {
                        return trader.getTownship().getTownshipName();
                    } else {
                        return null;
                    }
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
        }
        return listTrader.get(row);
    }
}
