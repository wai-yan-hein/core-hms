/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.common.Global;
import com.cv.app.ot.database.entity.OTCusGroupDetail;
import com.cv.app.ot.database.entity.OTProcedureGroup;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OTCusGroupDetailTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OTCusGroupDetailTableModel.class.getName());
    private List<OTCusGroupDetail> listOCLGD = new ArrayList();
    private final String[] columnNames = {"Category Name"};
    private final AbstractDataAccess dao = Global.dao;
    private JTable parent;
    private int groupId = -1;

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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if(listOCLGD == null){
            return null;
        }
        
        if(listOCLGD.isEmpty()){
            return null;
        }
        
        OTCusGroupDetail record = listOCLGD.get(row);
        switch (column) {
            case 0: //Name
                if (record.getKey() != null) {
                    if (record.getKey().getOpdCatId() != null) {
                        return record.getKey().getOpdCatId().getGroupName();
                    }
                }
                return null;
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if(listOCLGD == null){
            return;
        }
        
        if(listOCLGD.isEmpty()){
            return;
        }
        
        OTCusGroupDetail record = listOCLGD.get(row);
        switch (column) {
            case 0: //Name
                if (value != null) {
                    record.getKey().setOpdCatId((OTProcedureGroup) value);
                } else {
                    record.getKey().setOpdCatId(null);
                }
                break;
        }

        try {
            record.getKey().setCusGrpId(groupId);
            dao.save(record);
            if (!hasEmptyRow()) {
                listOCLGD.add(new OTCusGroupDetail());
                fireTableRowsInserted(listOCLGD.size() - 1, listOCLGD.size() - 1);
                if (parent != null) {
                    parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
                }
            }
            fireTableCellUpdated(row, column);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getMessage());
        }
    }

    @Override
    public int getRowCount() {
        if (listOCLGD == null) {
            return 0;
        }
        return listOCLGD.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OTCusGroupDetail> getListOCLGD() {
        return listOCLGD;
    }

    public void setListOCLG(List<OTCusGroupDetail> listOCLGD) {
        this.listOCLGD = listOCLGD;
        if (!hasEmptyRow()) {
            if (groupId != -1) {
                listOCLGD.add(new OTCusGroupDetail());
            }
        }
        fireTableDataChanged();
    }

    private boolean hasEmptyRow() {
        if (listOCLGD == null) {
            return true;
        }

        if (listOCLGD.isEmpty()) {
            return false;
        }

        OTCusGroupDetail tmp = listOCLGD.get(listOCLGD.size() - 1);
        return tmp.getKey().getCusGrpId() == null;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
        if (groupId == -1) {
            setListOCLG(new ArrayList());
        } else {
            try {
                List<OTCusGroupDetail> tmpList = dao.findAllHSQL(
                        "select o from OTCusGroupDetail o where o.key.cusGrpId = "
                        + groupId);
                setListOCLG(tmpList);
            } catch (Exception ex) {
                log.error("setGroupId : " + ex.getMessage());
            }
        }
    }

    public OTCusGroupDetail getSelectData(int index) {
        if (listOCLGD == null) {
            return null;
        }

        if (listOCLGD.isEmpty()) {
            return null;
        }

        return listOCLGD.get(index);
    }

    public void deleteItem(int index, int catId) {
        listOCLGD.remove(index);
        fireTableRowsDeleted(0, listOCLGD.size() - 1);
        if (index - 1 >= 0) {
            parent.setRowSelectionInterval(index - 1, index - 1);
        }

        try {
            dao.deleteSQL("delete from ot_cus_group_detail where ot_cat_id = "
                    + catId + " and cus_grp_id = " + groupId);
        } catch (Exception ex) {
            log.error("deleteItem : " + ex.getMessage());
        }
    }
}
