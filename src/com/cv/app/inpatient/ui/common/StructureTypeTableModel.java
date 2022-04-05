/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.inpatient.database.entity.BuildingStructurType;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class StructureTypeTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(StructureTypeTableModel.class.getName());
    private List<BuildingStructurType> listBST = new ArrayList();
    private final String[] columnNames = {"Type Name"};

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
            case 0: //Type Name
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listBST == null) {
            return null;
        }

        if (listBST.isEmpty()) {
            return null;
        }

        try {
            BuildingStructurType record = listBST.get(row);

            switch (column) {
                case 0: //Type Name
                    return record.getTypeDesp();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        if(listBST == null){
            return 0;
        }
        return listBST.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<BuildingStructurType> getListBST() {
        return listBST;
    }

    public void setListBST(List<BuildingStructurType> listBST) {
        this.listBST = listBST;
        fireTableDataChanged();
    }

    public void deleteRow(int index) {
        if (listBST != null) {
            if (!listBST.isEmpty()) {
                listBST.remove(index);
                fireTableDataChanged();
            }
        }
    }

    public void add(BuildingStructurType bst) {
        if (listBST != null) {
            listBST.add(bst);
            fireTableDataChanged();
        }
    }
}
