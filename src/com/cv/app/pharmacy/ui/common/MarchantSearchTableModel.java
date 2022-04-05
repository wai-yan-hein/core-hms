/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.view.VMarchant;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class MarchantSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(MarchantSearchTableModel.class.getName());
    private List<VMarchant> listVM = new ArrayList();
    private final String[] columnNames = {"Code", "Name", "Type"};

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
            case 0: //Student No.
                return String.class;
            case 1: //Name
                return String.class;
            case 2: //Age
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listVM == null) {
            return null;
        }

        if (listVM.isEmpty()) {
            return null;
        }

        try {
            VMarchant vm = listVM.get(row);

            switch (column) {
                case 0: //Code
                    return vm.getPersonNumber();
                case 1: //Name
                    return vm.getPersonName();
                case 2: //Type
                    return vm.getPersonType();
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
        if(listVM == null){
            return 0;
        }
        return listVM.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VMarchant> getListVM() {
        return listVM;
    }

    public void setListVM(List<VMarchant> listVM) {
        this.listVM = listVM;
        fireTableDataChanged();
    }

    public VMarchant getSelectVM(int row) {
        if(listVM != null){
            if(!listVM.isEmpty()){
                return listVM.get(row);
            }
        }
        return null;
    }
}
