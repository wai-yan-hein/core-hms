/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.view.ReturnInItemList;
import com.cv.app.util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class RetInItemSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(RetInItemSearchTableModel.class.getName());
    private List<ReturnInItemList> listRetInItems = new ArrayList();
    private final String[] columnNames = {"Sale Date", "Vou No", "Ref. Vou.", "Exp-Date",
        "Item Code", "Item Name", "Qty", "Price"};

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
            case 0: //Date
                return String.class;
            case 1: //Vou No
                return String.class;
            case 2: //Ref. Vou.
                return String.class;
            case 3: //Exp-Date
                return String.class;
            case 4: //Item Code
                return String.class;
            case 5: //Item Name
                return String.class;
            case 6: //Qty
                return String.class;
            case 7: //Price
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listRetInItems == null) {
            return null;
        }

        if (listRetInItems.isEmpty()) {
            return null;
        }

        try {
            ReturnInItemList riil = listRetInItems.get(row);

            switch (column) {
                case 0: //Date
                    return DateUtil.toDateStr(riil.getSaleDate());
                case 1: //Vou No
                    return riil.getKey().getSaleInvId();
                case 2: //Ref. Vou.
                    return riil.getRemark();
                case 3: //Exp-Date
                    return DateUtil.toDateStr(riil.getExpDate());
                case 4: //Item Code
                    return riil.getKey().getItem().getMedId();
                case 5: //Item Name
                    return riil.getKey().getItem().getMedName();
                case 6: //Qty
                    return riil.getSaleQty() + riil.getSaleUnit().getItemUnitCode();
                case 7: //Price
                    return riil.getSalePrice();
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
        if(listRetInItems == null){
            return 0;
        }
        return listRetInItems.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<ReturnInItemList> getListRetInItems() {
        return listRetInItems;
    }

    public void setListRetInItems(List<ReturnInItemList> listRetInItems) {
        this.listRetInItems = listRetInItems;
        fireTableDataChanged();
    }

    public ReturnInItemList getSelectedRetInItem(int index) {
        if(listRetInItems != null){
            if(!listRetInItems.isEmpty()){
                return listRetInItems.get(index);
            }
        }
        return null;
    }
}
