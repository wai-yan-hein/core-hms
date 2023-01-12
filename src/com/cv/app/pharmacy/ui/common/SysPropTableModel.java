/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.SysProperty;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class SysPropTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(SysPropTableModel.class.getName());
    private List<SysProperty> listSysProperty = new ArrayList();
    private final String[] columnNames = {"Description", "Vaue", "Remark"};
    private AbstractDataAccess dao;

    public SysPropTableModel(AbstractDataAccess dao) {
        this.dao = dao;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        /*SysProperty prop = listSysProperty.get(row);

    if (prop.getPropDesp().isEmpty()) {
      return true;
    } else {
      return false;
    }*/

        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(listSysProperty == null){
            return null;
        }
        
        if(listSysProperty.isEmpty()){
            return null;
        }
        
        try{
        SysProperty prop = listSysProperty.get(row);

        switch (column) {
            case 0: //Description
                return prop.getPropDesp();
            case 1: //Value
                return prop.getPropValue();
            case 2: //Remark
                return prop.getPropRemark();
            default:
                return null;
        }
        }catch(Exception ex){
            log.error("getValueAt : " + ex.getMessage());
        }
        
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if(listSysProperty == null){
            return;
        }
        
        if(listSysProperty.isEmpty()){
            return;
        }
        
        boolean status = false;
        try {
            SysProperty prop = listSysProperty.get(row);
            switch (column) {
                case 0: //Description
                    if (value != null) {
                        String strValue = value.toString().trim();
                        if (!strValue.isEmpty()) {
                            if (prop.getPropDesp() != null) {
                                dao.delete(prop);
                            }
                            prop.setPropDesp(strValue);
                            status = true;
                        }
                    }
                    break;
                case 1: //Value
                    if (value != null) {
                        String strValue = value.toString().trim();
                        if (!strValue.isEmpty()) {
                            prop.setPropValue(strValue);
                            status = true;
                        }
                    }
                    break;
                case 2: //Remark
                    if (value != null) {
                        String strValue = value.toString().trim();
                        if (!strValue.isEmpty()) {
                            prop.setPropRemark(strValue);
                            status = true;
                        }
                    }
                default:
                    System.out.println("Invalid index");
            }

            if (status) {
                dao.save(prop);
                Global.systemProperties.put(prop.getPropDesp(), prop.getPropValue());
                addNewRow();
            }
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        if(listSysProperty == null){
            return 0;
        }
        return listSysProperty.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<SysProperty> getListSysProperty() {
        return listSysProperty;
    }

    public void setListSysProperty(List<SysProperty> listSysProperty) {
        this.listSysProperty = listSysProperty;
        fireTableDataChanged();
    }

    public SysProperty getSysProperty(int row) {
        if(listSysProperty != null){
            if(!listSysProperty.isEmpty()){
                return listSysProperty.get(row);
            }
        }
        return null;
    }

    public void setSysProperty(int row, SysProperty prop) {
        if(listSysProperty != null){
            if(!listSysProperty.isEmpty()){
                listSysProperty.set(row, prop);
        fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addSysProperty(SysProperty prop) {
        if(listSysProperty != null){
        listSysProperty.add(prop);
        fireTableRowsInserted(listSysProperty.size() - 1, listSysProperty.size() - 1);
        }
    }

    public void deleteSysProperty(int row) {
        try {
            SysProperty prop = listSysProperty.get(row);

            dao.delete(prop);
            listSysProperty.remove(row);
            Global.systemProperties.remove(prop.getPropDesp());
            fireTableRowsDeleted(0, listSysProperty.size());
        } catch (Exception ex) {
            log.error("deleteSysProperty : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    public void addNewRow() {
        if (!hasNewRow()) {
            if(listSysProperty != null){
            listSysProperty.add(new SysProperty());
            int index = listSysProperty.size();
            fireTableRowsInserted(index - 1, index - 1);
            }
        }
    }

    private boolean hasNewRow() {
        boolean status = true;

        if(listSysProperty == null){
            return false;
        }
        
        if (listSysProperty.isEmpty()) {
            status = false;
        } else {
            SysProperty lastProp = listSysProperty.get(listSysProperty.size() - 1);

            if (lastProp.getPropDesp() != null) {
                status = false;
            }
        }

        return status;
    }
}
