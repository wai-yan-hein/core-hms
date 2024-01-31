/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.tempentity.LabUsage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class LabStickerMedUseTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(LabStickerMedUseTableModel.class.getName());
    private List<LabUsage> list = new ArrayList();
    private final String[] columnNames = {"Med Id", "Description", "Qty", "Unit", "Location"};

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
            case 0: //Med Id
                return String.class;
            case 1: //Description
                return String.class;
            case 2: //Qty
                return Float.class;
            case 3: //Unit
                return String.class;
            case 4: //Location
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        LabUsage record = list.get(row);

        switch (column) {
            case 0: //Med Id
                return record.getMedId();
            case 1: //Description
                return record.getDesc();
            case 2: //Qty
                return record.getUnitQty();
            case 3: //Unit
                return record.getUnitId();
            case 4: //Location
                return record.getLocationName();
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

    public List<LabUsage> getList() {
        return list;
    }

    public void setList(List<LabUsage> list) {
        this.list = list;
        fireTableDataChanged();
    }
    
    public void clear(){
        list = new ArrayList();
        fireTableDataChanged();
    }
}
