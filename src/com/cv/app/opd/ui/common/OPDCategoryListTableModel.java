/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.OPDCategory;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OPDCategoryListTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OPDCategoryListTableModel.class.getName());
    private List<OPDCategory> listOPDCategory = new ArrayList();
    private final String[] columnNames = {"Code", "Description"};

    public OPDCategoryListTableModel(List<OPDCategory> list){
        this.listOPDCategory = list;
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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        OPDCategory record = listOPDCategory.get(row);

        switch (column) {
            case 0: //Code
                return record.getUserCode();
            case 1: //Description
                return record.getCatName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        
    }

    @Override
    public int getRowCount() {
        return listOPDCategory.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OPDCategory> getListOPDCategory() {
        return listOPDCategory;
    }

    public void setListOPDCategory(List<OPDCategory> listOPDCategory) {
        this.listOPDCategory = listOPDCategory;
        fireTableDataChanged();
    }

    public OPDCategory getOPDCategory(int row) {
        return listOPDCategory.get(row);
    }
    
    public int getSize(){
        return listOPDCategory.size();
    }
}
