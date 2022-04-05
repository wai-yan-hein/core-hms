/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.view.VGrossProfit;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class GPCostTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(GPCostTableModel.class.getName());
    private List<VGrossProfit> listGrossProfit = new ArrayList();
    private final String[] columnNames = {"Code", "Description", "Packing-Size", "Adj-Value",
        "Ret-In-Value", "Dmg-Value", "Cost of Sale", "Total Sale",
        "Gross Profit"};
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
                return String.class;
            case 3: //OP-Value
                return Double.class;
            case 4: //TTL-Pur
                return Double.class;
            case 5: //CL-Value
                return Double.class;
            case 6: //COGS
                return Double.class;
            case 7: //TTL-Sale
                return Double.class;
            case 8: //Gross Profit
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listGrossProfit == null) {
            return null;
        }

        if (listGrossProfit.isEmpty()) {
            return null;
        }

        try {
            VGrossProfit grossProfit = listGrossProfit.get(row);

            switch (column) {
                case 0: //Code
                    return grossProfit.getKey().getItemId();
                case 1: //Description
                    return grossProfit.getItemName();
                case 2: //Packing-Size
                    return grossProfit.getRelStr();
                case 3: //OP-Value
                    return grossProfit.getOpValue();
                case 4: //TTL-Pur
                    return grossProfit.getTtlPur();
                case 5: //CL-Value
                    return grossProfit.getClValue();
                case 6: //COGS
                    return grossProfit.getCogsValue();
                case 7: //TTL-Sale
                    return grossProfit.getTtlSale();
                case 8: //Gross Profit
                    return grossProfit.getGpValue();
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
        if(listGrossProfit == null){
            return 0;
        }
        return listGrossProfit.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VGrossProfit> getListGrossProfit() {
        return listGrossProfit;
    }

    public void setListGrossProfit(List<VGrossProfit> listGrossProfit) {
        this.listGrossProfit.removeAll(this.listGrossProfit);
        fireTableDataChanged();

        this.listGrossProfit = listGrossProfit;

        fireTableDataChanged();
    }

    public VGrossProfit getGrossProfit(int row) {
        if(listGrossProfit != null){
            if(!listGrossProfit.isEmpty()){
                return listGrossProfit.get(row);
            }
        }
        return null;
    }

    public void setGrossProfit(int row, VGrossProfit grossProfit) {
        listGrossProfit.set(row, grossProfit);
        fireTableRowsUpdated(row, row);
    }
    /*
     * public void addItemBrand(ItemBrand itemBrand){
     * listItemBrand.add(itemBrand);
     *
     * fireTableRowsInserted(listItemBrand.size()-1, listItemBrand.size()-1); }
     *
     * public void deleteItemBrand(int row){ listItemBrand.remove(row);
     * fireTableRowsDeleted(0, listItemBrand.size());
     }
     */
}
