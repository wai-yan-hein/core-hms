/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.view.VOpd;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class LabStickerTestTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(LabStickerTestTableModel.class.getName());
    private List<VOpd> list = new ArrayList();
    private final String[] columnNames = {"Lab Test", "Qty", "Price", "Amount"};

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
            case 0: //Lab Test
                return String.class;
            case 1: //Qty
                return Integer.class;
            case 2: //Price
                return Double.class;
            case 3: //Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        VOpd record = list.get(row);

        switch (column) {
            case 0: //Lab Test
                return record.getServiceName();
            case 1: //Qty
                return record.getQty();
            case 2: //Price
                return record.getPrice();
            case 3: //Amount
                return record.getAmount();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VOpd> getList() {
        return list;
    }

    public void setList(List<VOpd> list) {
        this.list = list;
        fireTableDataChanged();
    }
    
    public VOpd getSelectedRecord(int row){
        return list.get(row);
    }
}
