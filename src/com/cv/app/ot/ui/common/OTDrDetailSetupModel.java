/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.common.Global;
import com.cv.app.ot.database.entity.DrDetailId;
import com.cv.app.ot.database.entity.DrDetailIdKey;
import com.cv.app.ot.database.entity.OTProcedure;
import com.cv.app.ot.database.view.VOTDrDetailId;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OTDrDetailSetupModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OTDrDetailSetupModel.class.getName());
    private List<VOTDrDetailId> list = new ArrayList();
    private final String[] columnNames = {"OT Service Name"};
    private final AbstractDataAccess dao = Global.dao;

    public OTDrDetailSetupModel() {
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
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
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return null;
        }

        VOTDrDetailId record = list.get(row);

        switch (column) {
            case 0: //OT Service Name
                return record.getServiceName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (list == null) {
            return;
        }

        if (list.isEmpty()) {
            return;
        }

        VOTDrDetailId record = list.get(row);

        switch (column) {
            case 0: //OT Service Name
                if (value == null) {

                } else {
                    OTProcedure service = (OTProcedure)value;
                    if (record.getKey() == null) {
                        DrDetailIdKey key = new DrDetailIdKey();
                        key.setOption("OT");
                        record.setKey(key);
                    }
                    
                    record.getKey().setServiceId(service.getServiceId());
                    record.setServiceName(service.getServiceName());
                }
                break;
        }
        
        saveRecord(record);
        
        try{
            fireTableCellUpdated(row, column);
        }catch(Exception ex){
            
        }
        
        addNewRow();
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VOTDrDetailId> getList() {
        return list;
    }

    public void setList(List<VOTDrDetailId> list) {
        this.list = list;
        fireTableDataChanged();
        addNewRow();
    }
    
    private void saveRecord(VOTDrDetailId rec){
        try{
            DrDetailId detailId = (DrDetailId)dao.find(DrDetailId.class, rec.getKey());
            if(detailId == null){
                detailId = new DrDetailId();
            }
            detailId.setKey(rec.getKey());
            dao.save(detailId);
        }catch(Exception ex){
            log.error("saveRecord : " + ex.toString());
        }finally{
            dao.close();
        }
    }
    
    public void delete(int row){
        if(list != null){
            if(!list.isEmpty()){
                VOTDrDetailId record = list.get(row);
                try{
                    String option = record.getKey().getOption();
                    Integer serviceId = record.getKey().getServiceId();
                    String strSql = "delete from dr_detail_id where option = '" 
                            + option + "' and service_id = " + serviceId;
                    dao.execSql(strSql);
                    list.remove(row);
                    fireTableRowsDeleted(row, row);
                }catch(Exception ex){
                    log.error("delete : " + ex.toString());
                }finally{
                    dao.close();
                }
            }
        }
    }
    
    public void addNewRow() {
        int count = list.size();
        if (count == 0 || list.get(count - 1).getKey() != null) {
            list.add(new VOTDrDetailId());
            fireTableRowsInserted(list.size() - 1, list.size() - 1);
        }
    }
}
