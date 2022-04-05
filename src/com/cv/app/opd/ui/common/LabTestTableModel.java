/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.view.VService;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class LabTestTableModel extends AbstractTableModel{
    private List<VService> listService = new ArrayList();
    private String[] columnNames = {"Code","Test Name"};
    private AbstractDataAccess dao;
    private int groupId;
    
    public LabTestTableModel(AbstractDataAccess dao){
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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        VService record = listService.get(row);

        switch (column) {
            case 0: //code
                return record.getServiceCode();
            case 1: //Lab test name
                return record.getServiceName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        
    }

    @Override
    public int getRowCount() {
        return listService.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VService> getListService() {
        return listService;
    }

    public void setListService(List<VService> listService) {
        this.listService = listService;
        fireTableDataChanged();
    }
    
    public void setGroupId(int id){
        groupId = id;
        getCategory();
    }
    
     private void getCategory(){
        //listOPDCategory = dao.findAll("OPDCategory", "groupId = " + groupId);
        listService = dao.findAllHSQL(
            "select o from VService o where o.catId = " + groupId + " order by o.catName");
        addNewRow();
        fireTableDataChanged();
    }
     
     private void addNewRow(){
        int count = listService.size();
        
        if(count == 0 || listService.get(count-1).getCatId()!= null){
            listService.add(new VService());
        }
    }
}
