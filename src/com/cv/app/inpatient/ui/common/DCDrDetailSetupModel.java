/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.common.Global;
import com.cv.app.ot.database.entity.DrDetailId;
import com.cv.app.ot.database.entity.DrDetailIdKey;
import com.cv.app.inpatient.database.view.VDCDrDetailId;
import com.cv.app.inpatient.database.entity.InpService;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class DCDrDetailSetupModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(DCDrDetailSetupModel.class.getName());
    private List<VDCDrDetailId> list = new ArrayList();
    private final String[] columnNames = {"DC Service Name"};
    private final AbstractDataAccess dao = Global.dao;

    public DCDrDetailSetupModel() {
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

        VDCDrDetailId record = list.get(row);

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

        VDCDrDetailId record = list.get(row);

        switch (column) {
            case 0: //OT Service Name
                if (value == null) {

                } else {
                    InpService service = (InpService)value;
                    if (record.getKey() == null) {
                        DrDetailIdKey key = new DrDetailIdKey();
                        key.setOption("DC");
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

    public List<VDCDrDetailId> getList() {
        return list;
    }

    public void setList(List<VDCDrDetailId> list) {
        this.list = list;
        fireTableDataChanged();
        addNewRow();
    }
    
    private void saveRecord(VDCDrDetailId rec){
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
                VDCDrDetailId record = list.get(row);
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
            list.add(new VDCDrDetailId());
            fireTableRowsInserted(list.size() - 1, list.size() - 1);
        }
    }
}
