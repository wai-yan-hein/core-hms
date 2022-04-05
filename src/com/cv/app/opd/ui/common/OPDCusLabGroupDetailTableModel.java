/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.OPDCategory;
import com.cv.app.opd.database.entity.OPDCusLabGroupDetail;
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
public class OPDCusLabGroupDetailTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OPDCusLabGroupDetailTableModel.class.getName());
    private List<OPDCusLabGroupDetail> listOCLGD = new ArrayList();
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
        OPDCusLabGroupDetail record = listOCLGD.get(row);

        switch (column) {
            case 0: //Name
                if (record.getKey() != null) {
                    if (record.getKey().getOpdCatId() != null) {
                        return record.getKey().getOpdCatId().getCatName();
                    }
                }
                return null;
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OPDCusLabGroupDetail record = listOCLGD.get(row);

        switch (column) {
            case 0: //Name
                if (value != null) {
                    record.getKey().setOpdCatId((OPDCategory) value);
                } else {
                    record.getKey().setOpdCatId(null);
                }
                break;
        }

        try {
            record.getKey().setCusGrpId(groupId);
            dao.save(record);
            if (!hasEmptyRow()) {
                listOCLGD.add(new OPDCusLabGroupDetail());
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

    public List<OPDCusLabGroupDetail> getListOCLGD() {
        return listOCLGD;
    }

    public void setListOCLG(List<OPDCusLabGroupDetail> listOCLGD) {
        this.listOCLGD = listOCLGD;
        if (!hasEmptyRow()) {
            if (groupId != -1) {
                listOCLGD.add(new OPDCusLabGroupDetail());
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

        OPDCusLabGroupDetail tmp = listOCLGD.get(listOCLGD.size() - 1);
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
                List<OPDCusLabGroupDetail> tmpList = dao.findAllHSQL(
                        "select o from OPDCusLabGroupDetail o where o.key.cusGrpId = "
                        + groupId);
                setListOCLG(tmpList);
            } catch (Exception ex) {
                log.error("setGroupId : " + ex.getMessage());
            }
        }
    }

    public OPDCusLabGroupDetail getSelectData(int index) {
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
            dao.deleteSQL("delete from opd_cus_lab_group_detail where opd_cat_id = "
                    + catId + " and cus_grp_id = " + groupId);
        } catch (Exception ex) {
            log.error("deleteItem : " + ex.getMessage());
        }
    }
}
