/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.OPDCusLabGroup;
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
public class OPDCusLabGroupTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OPDCusLabGroupTableModel.class.getName());
    private List<OPDCusLabGroup> listOCLG = new ArrayList();
    private final String[] columnNames = {"Group Name"};
    private final AbstractDataAccess dao = Global.dao;
    private JTable parent;

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
        OPDCusLabGroup record = listOCLG.get(row);

        switch (column) {
            case 0: //Name
                return record.getGroupName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OPDCusLabGroup record = listOCLG.get(row);

        switch (column) {
            case 0: //Name
                if (value == null) {
                    record.setGroupName(null);
                } else {
                    record.setGroupName(value.toString());
                }
                break;
        }

        try {
            dao.save(record);
            if (!hasEmptyRow()) {
                listOCLG.add(new OPDCusLabGroup());
                fireTableRowsInserted(listOCLG.size() - 1, listOCLG.size() - 1);
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
        return listOCLG.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OPDCusLabGroup> getListOCLG() {
        return listOCLG;
    }

    public void setListOCLG(List<OPDCusLabGroup> listOCLG) {
        this.listOCLG = listOCLG;
        if (this.listOCLG == null) {
            this.listOCLG = new ArrayList();
            this.listOCLG.add(new OPDCusLabGroup());
        } else if (this.listOCLG.isEmpty()) {
            this.listOCLG.add(new OPDCusLabGroup());
        }
        if (!hasEmptyRow()) {
            this.listOCLG.add(new OPDCusLabGroup());
        }
        fireTableDataChanged();
    }

    private boolean hasEmptyRow() {
        if (listOCLG == null) {
            return true;
        }

        if (listOCLG.isEmpty()) {
            return false;
        }

        OPDCusLabGroup tmp = listOCLG.get(listOCLG.size() - 1);
        return tmp.getId() == null;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public OPDCusLabGroup getSelectGroup(int row) {
        if (listOCLG != null) {
            if (!listOCLG.isEmpty()) {
                return listOCLG.get(row);
            }
        }

        return null;
    }

    public void deleteRow(int row) {
        if (listOCLG == null) {
            return;
        }

        if (listOCLG.isEmpty()) {
            return;
        }

        OPDCusLabGroup oclg = listOCLG.get(row);
        if (oclg.getId() != null) {
            listOCLG.remove(row);
            fireTableRowsDeleted(0, listOCLG.size() - 1);
            if (row - 1 >= 0) {
                parent.setRowSelectionInterval(row - 1, row - 1);
            }

            try {
                dao.deleteSQL("delete from opd_cus_lab_group where id = "
                        + oclg.getId());
                dao.deleteSQL("delete from opd_cus_lab_group_detail where cus_grp_id = " 
                        + oclg.getId());
            } catch (Exception ex) {
                log.error("deleteRow : " + ex.getMessage());
            }
        }
    }
}
