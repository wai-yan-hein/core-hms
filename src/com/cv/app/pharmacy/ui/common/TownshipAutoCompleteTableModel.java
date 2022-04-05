/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.Township;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class TownshipAutoCompleteTableModel extends AbstractTableModel{
    
    static Logger log = Logger.getLogger(TownshipAutoCompleteTableModel.class.getName());
    private final List<Township> list;
    private final String[] columnNames = {"Township"};
    
    public TownshipAutoCompleteTableModel(List<Township> list){
        this.list = list;
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
        if(list == null){
            return null;
        }
        
        if(list.isEmpty()){
            return null;
        }
        
        try{
        Township record = list.get(row);

        switch (column) {
            case 0: //Township
                return record.getTownshipName();
            default:
                return null;
        }
        } catch (Exception ex){
            log.error("getValueAt : " + ex.getMessage());
        }
        
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if(list == null){
            return 0;
        }
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    public Township getTownship(int row){
        if(list == null){
            return null;
        }
        return list.get(row);
    }
    
    public int getSize(){
        if(list == null){
            return 0;
        }else{
            return list.size();
        }
    }
}
