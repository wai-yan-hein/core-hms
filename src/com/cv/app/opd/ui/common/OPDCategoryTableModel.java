/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.OPDCategory;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class OPDCategoryTableModel extends AbstractTableModel {

    private List<OPDCategory> listOPDCategory = new ArrayList();
    private final String[] columnNames = {"Code", "Description"};
    private final AbstractDataAccess dao;
    private int groupId;

    public OPDCategoryTableModel(AbstractDataAccess dao) {
        this.dao = dao;
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
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        OPDCategory record = listOPDCategory.get(row);

        switch (column) {
            case 0: //Code
                return record.getUserCode();
            case 1: //Description
                return record.getCatName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OPDCategory record = listOPDCategory.get(row);

        switch (column) {
            case 0: //Code
                if(value != null){
                    record.setUserCode(value.toString());
                }
                break;
            case 1: //Description
                if (!Util1.getString(value, "-").equals("-")) {
                    record.setCatName(value.toString());
                    record.setGroupId(groupId);
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid description.", "OPD Category",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
        }

        save(record);

        try {
            fireTableCellUpdated(row, column);
        } catch (Exception ex) {

        }
    }

    private void save(OPDCategory record) {
        if (!Util1.getString(record.getCatName(), "-").equals("-")) {
            try {
                dao.save(record);
                addNewRow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Util1.getParent(),
                        ex.toString(), "OPD Category Save Error",
                        JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public int getRowCount() {
        return listOPDCategory.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OPDCategory> getListOPDCategory() {
        return listOPDCategory;
    }

    public void setListOPDCategory(List<OPDCategory> listOPDCategory) {
        this.listOPDCategory = listOPDCategory;
        fireTableDataChanged();
    }

    public OPDCategory getOPDCategory(int row) {
        return listOPDCategory.get(row);
    }

    public void setOPDCategory(int row, OPDCategory oPDCategory) {
        listOPDCategory.set(row, oPDCategory);
        fireTableRowsUpdated(row, row);
    }

    public void addOPDCategory(OPDCategory oPDCategory) {
        listOPDCategory.add(oPDCategory);
        fireTableRowsInserted(listOPDCategory.size() - 1, listOPDCategory.size() - 1);
    }

    public void deleteOPDCategory(int row) {
        listOPDCategory.remove(row);
        fireTableRowsDeleted(0, listOPDCategory.size() - 1);
    }

    public void setGroupId(int id) {
        groupId = id;
        getCategory();
    }

    private void getCategory() {
        //listOPDCategory = dao.findAll("OPDCategory", "groupId = " + groupId);
        listOPDCategory = dao.findAllHSQL(
                "select o from OPDCategory o where o.groupId = " + groupId + " order by o.userCode");
        addNewRow();
        fireTableDataChanged();
    }

    private void addNewRow() {
        int count = listOPDCategory.size();

        if (count == 0 || listOPDCategory.get(count - 1).getCatId() != null) {
            listOPDCategory.add(new OPDCategory());
        }
    }

    public void delete(int row) {
        OPDCategory record = listOPDCategory.get(row);
        String sql;
        if (NumberUtil.NZeroL(record.getCatId()) > 0) {
            try {
                dao.open();
                dao.beginTran();
                sql = "delete from opd_category where cat_id = '" + record.getCatId() + "'";
                dao.deleteSQL(sql);
            } catch (Exception e) {
                dao.rollBack();
            } finally {
                dao.close();
            }
        }

        listOPDCategory.remove(row);

        fireTableRowsDeleted(row, row);

        addNewRow();
    }
}
