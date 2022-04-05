/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.view.VStockCosting;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class StockCostingTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(StockCostingTableModel.class.getName());
    private List<VStockCosting> listStockCosting = new ArrayList();
    private final String[] columnNames = {"Stock Code", "Description", "Packing-Size", "Balance",
        "Smallest Qty", "Total Cost", "Location Qty", "Location Total Cost"};
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
        switch (column) {
            case 0: //Code
            case 1: //Description
            case 2: //Packing-Size
            case 3: //Balance
                return String.class;
            case 4: //Smallest Qty
                return Integer.class;
            case 5: //Total Cost
                return Double.class;
            case 6: //Loc Qty
                return Float.class;
            case 7: //Loc Ttl Cost
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listStockCosting == null) {
            return null;
        }

        if (listStockCosting.isEmpty()) {
            return null;
        }

        try {
            VStockCosting stockCosting = listStockCosting.get(row);

            switch (column) {
                case 0: //Code
                    if (codeUsage.equals("SHORTNAME")) {
                        return stockCosting.getShortName();
                    } else {
                        return stockCosting.getMedId();
                    }
                case 1: //Description
                    return stockCosting.getMedName();
                case 2: //Packing-Size
                    return stockCosting.getRelStr();
                case 3: //Balance
                    return stockCosting.getBalQtyStr();
                case 4: //Smallest Qty
                    return stockCosting.getBalQty();
                case 5: //Total Cost
                    return stockCosting.getTtlCost();
                case 6: //Loc Qty
                    return stockCosting.getLocTtlSmallestQty();
                case 7: //Loc Ttl Cost
                    return stockCosting.getLocTtlCost();
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
        if (listStockCosting == null) {
            return 0;
        }
        return listStockCosting.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VStockCosting> getListStockCosting() {
        return listStockCosting;
    }

    public void setListStockCosting(List<VStockCosting> listStockCosting) {
        this.listStockCosting = listStockCosting;

        fireTableDataChanged();
    }

    public VStockCosting getStockCosting(int row) {
        if (listStockCosting != null) {
            if (!listStockCosting.isEmpty()) {
                return listStockCosting.get(row);
            }
        }
        return null;
    }

    public void setStockCosting(int row, VStockCosting stockCosting) {
        if (listStockCosting != null) {
            if (!listStockCosting.isEmpty()) {
                listStockCosting.set(row, stockCosting);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    /*public void addItemBrand(ItemBrand itemBrand){
     listItemBrand.add(itemBrand);
        
     fireTableRowsInserted(listItemBrand.size()-1, listItemBrand.size()-1);
     }
    
     public void deleteItemBrand(int row){
     listItemBrand.remove(row);
     fireTableRowsDeleted(0, listItemBrand.size());
     }*/
}
