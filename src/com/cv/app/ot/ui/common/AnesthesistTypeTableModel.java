/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.ot.database.entity.AnesthesistType;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class AnesthesistTypeTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(AnesthesistTypeTableModel.class.getName());
    private List<AnesthesistType> listAneType = new ArrayList();
    private String[] columnNames = {"Description"};
    private AbstractDataAccess dao;

    public AnesthesistTypeTableModel(AbstractDataAccess dao) {
        this.dao = dao;
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
        switch (column) {
            case 0: //Description
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        AnesthesistType record = listAneType.get(row);

        switch (column) {
            case 0: //Description
                return record.getTypeName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listAneType.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public void addType(AnesthesistType aType){
        listAneType.add(aType);
        fireTableRowsInserted(listAneType.size()-1, listAneType.size()-1);
    }
    
    public void setType(int row, AnesthesistType aType){
        listAneType.set(row, aType);
        fireTableRowsUpdated(row, row);
    }
    
    public AnesthesistType getType(int row){
        return listAneType.get(row);
    }
    
    public void deleteType(int row){
        listAneType.remove(row);
        fireTableRowsDeleted(0, listAneType.size()-1);
    }

    public List<AnesthesistType> getListAneType() {
        return listAneType;
    }

    public void setListAneType(List<AnesthesistType> listAneType) {
        this.listAneType = listAneType;
        fireTableDataChanged();
    }
}
