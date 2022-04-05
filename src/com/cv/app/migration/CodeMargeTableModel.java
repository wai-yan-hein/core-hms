/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.migration;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class CodeMargeTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(CodeMargeTableModel.class.getName());
    private List<CodeMarge> list;
    private final String[] columnNames = {"Item Code", "Item Name", "Rel-Str",
        "Ora Id", "New Item Code", "Source", "Status"};
    private final AbstractDataAccess dao = Global.dao;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 4 || column == 6;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Item Code
                return String.class;
            case 1: //Item Name
                return String.class;
            case 2: //Rel-Str
                return String.class;
            case 3: //Ora Id
                return String.class;
            case 4: //New Item Code
                return String.class;
            case 5: //Source
                return String.class;
            case 6: //Status
                return Boolean.class;
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
            CodeMarge record = list.get(row);

            switch (column) {
                case 0: //Item Code
                    return record.getItemCode();
                case 1: //Item Name
                    return record.getItemName();
                case 2: //Rel-Str
                    return record.getRelStr();
                case 3: //Ora Id
                    return record.getOraId();
                case 4: //New Item Code
                    return record.getNewItemCode();
                case 5: //Source
                    return record.getSource();
                case 6: //Status
                    return record.getItemStatus();
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
        if (list == null) {
            return;
        }

        if (list.isEmpty()) {
            return;
        }

        try {
            CodeMarge record = list.get(row);

            switch (column) {
                case 4: //New Item Code
                    if (value == null) {
                        record.setNewItemCode(null);
                    } else {
                        record.setNewItemCode(value.toString().trim());
                    }
                    break;
                case 6: //Status
                    record.setItemStatus((Boolean) value);
                    break;
            }
            fireTableCellUpdated(row, column);
            saveRecord(record);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
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

    public void saveRecord(CodeMarge record) {
        try {
            dao.save(record);
        } catch (Exception ex) {
            log.error("saveRecord : " + ex.toString());
        } finally {
            dao.close();
        }
    }
    
    public void loadData(){
        try{
            String strSql = "select o from CodeMarge o order by o.itemCode";
            list = dao.findAllHSQL(strSql);
            fireTableDataChanged();
        }catch(Exception ex){
            log.error("loadData : " + ex.toString());
        }finally{
            dao.close();
        }
    }
}
