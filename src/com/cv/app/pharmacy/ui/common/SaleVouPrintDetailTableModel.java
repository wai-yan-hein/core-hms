/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.SaleDetailHis;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class SaleVouPrintDetailTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(SaleVouPrintDetailTableModel.class.getName());
    private List<SaleDetailHis> list = new ArrayList();
    private final String[] columnNames = {"Code", "Description", "Relation-Str",
        "Exp-Date", "Qty", "Cost", "Sale Price", "Discount", "FOC",
        "Amount"};

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
                return String.class;
            case 1: //Description
                return String.class;
            case 2: //Relation-Str
                return String.class;
            case 3: //Exp-Date
                return Date.class;
            case 4: //Qty
                return String.class;
            case 5: //Cost
                return Double.class;
            case 6: //Sale Price
                return Double.class;
            case 7: //Discount
                return Double.class;
            case 8: //FOC
                return String.class;
            case 9: //Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return null;
        }

        try {
            SaleDetailHis record = list.get(row);

            switch (column) {
                case 0: //Code
                    return record.getMedId();
                case 1: //Description
                    return record.getMedName();
                case 2: //Relation-Str
                    return record.getRelStr();
                case 3: //Exp-Date
                    return record.getExpDate();
                case 4: //Qty
                    return record.getSaleQty();
                case 5: //Cost
                    return record.getCostPrice();
                case 6: //Sale Price
                    return record.getSalePrice();
                case 7: //Discount
                    return record.getItemDiscount();
                case 8: //FOC
                    return record.getFocQty();
                case 9: //Amount
                    return record.getSaleAmount();
                default:
                    return Object.class;
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
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<SaleDetailHis> getList() {
        return list;
    }

    public void setList(List<SaleDetailHis> list) {
        this.list = list;
        fireTableDataChanged();
    }
    
    public void clear(){
        list = new ArrayList();
        fireTableDataChanged();
        System.gc();
    }
}
