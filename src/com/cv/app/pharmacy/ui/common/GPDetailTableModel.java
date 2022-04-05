/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.tempentity.StockCostingDetail;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class GPDetailTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(GPDetailTableModel.class.getName());
    private List<StockCostingDetail> listStockCostingDetail = new ArrayList();
    private final String[] columnNames = {"Date", "Option", "Qty", "Cost Price",
        "Unit", "Cost Qty", "Smallest Cost", "Amount"};

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
            case 0: //Tran Date
                return String.class;
            case 1: //Tran Option
                return String.class;
            case 2: //Tran Qty
                return Integer.class;
            case 3: //Cost Price
                return Double.class;
            case 4: //Unit
                return String.class;
            case 5: //Cost Qty
                return Integer.class;
            case 6: //Smallest Cost
                return Double.class;
            case 7: //Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listStockCostingDetail == null) {
            return null;
        }

        if (listStockCostingDetail.isEmpty()) {
            return null;
        }

        try {
            StockCostingDetail stockCostingDetail = listStockCostingDetail.get(row);

            switch (column) {
                case 0: //Tran Date
                    if (stockCostingDetail.getTranDate() != null) {
                        return DateUtil.toDateStr(stockCostingDetail.getTranDate());
                    } else {
                        return null;
                    }
                case 1: //Tran Option
                    return stockCostingDetail.getTranOption();
                case 2: //Tran Qty
                    return stockCostingDetail.getTranQty();
                case 3: //Cost Price
                    return stockCostingDetail.getPackingCost();
                case 4: //Unit
                    return stockCostingDetail.getUnit();
                case 5: //Cost Qty
                    //return stockCostingDetail.getCostQty();
                    return stockCostingDetail.getSaleQty();
                case 6: //Smallest Cost
                    return stockCostingDetail.getSmallestCost();
                case 7: //Amount
                    Double amount = NumberUtil.NZero(stockCostingDetail.getSmallestCost())
                            * NumberUtil.NZeroInt(stockCostingDetail.getSaleQty());
                    return amount;
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
        if (listStockCostingDetail == null) {
            return 0;
        }
        return listStockCostingDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<StockCostingDetail> getListStockCostingDetail() {
        return listStockCostingDetail;
    }

    public void setListStockCostingDetail(List<StockCostingDetail> listStockCostingDetail) {
        this.listStockCostingDetail = listStockCostingDetail;

        fireTableDataChanged();
    }

    public StockCostingDetail getStockCostingDetail(int row) {
        if (listStockCostingDetail != null) {
            if (!listStockCostingDetail.isEmpty()) {
                return listStockCostingDetail.get(row);
            }
        }
        return null;
    }

    public void setStockCostingDetail(int row, StockCostingDetail stockCostingDetail) {
        if (listStockCostingDetail != null) {
            if (!listStockCostingDetail.isEmpty()) {
                listStockCostingDetail.set(row, stockCostingDetail);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void removeAll() {
        if (listStockCostingDetail != null) {
            listStockCostingDetail.removeAll(listStockCostingDetail);
            fireTableDataChanged();
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
