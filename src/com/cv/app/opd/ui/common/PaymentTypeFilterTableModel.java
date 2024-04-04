/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.helper.PaymentTypeFilter;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PaymentTypeFilterTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PaymentTypeFilterTableModel.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private List<PaymentTypeFilter> list = new ArrayList();
    private final String[] columnNames = {"Type", "Filter"};
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 1;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Type
                return String.class;
            case 1: //Filter
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        PaymentTypeFilter record = list.get(row);

        switch (column) {
            case 0: //Tran Option
                return record.getTypeName();
            case 1: //Filter
                return record.getStatus();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        PaymentTypeFilter record = list.get(row);
        switch(column){
            case 1: //Filter
                record.setStatus((Boolean)value);
        }
        
        fireTableCellUpdated(row, column);
        updateRecord(record);
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PaymentTypeFilter> getList() {
        return list;
    }

    public void setList(List<PaymentTypeFilter> list) {
        this.list = list;
        fireTableDataChanged();
    }
    
    private void updateRecord(PaymentTypeFilter ptf){
        try{
            if(ptf.getStatus()){
                String strSql = "insert tmp_pay_type(mac_id, pay_type_id)\n" +
                        "values('" + Global.machineId + "', " + ptf.getTypeId() + ")";
                dao.execSql(strSql);
            } else {
                String strSql = "delete from tmp_pay_type where mac_id = '" + Global.machineId
                        + "' and pay_type_id = " + ptf.getTypeId();
                dao.execSql(strSql);
            }
        }catch(Exception ex){
            log.error("updateRecord : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }
}
