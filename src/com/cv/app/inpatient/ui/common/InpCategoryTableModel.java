/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.inpatient.database.entity.InpCategory;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class InpCategoryTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(InpCategoryTableModel.class.getName());
    private List<InpCategory> listInpCategory = new ArrayList();
    private final String[] columnNames = {"Code", "Description"};
    private final AbstractDataAccess dao;

    public InpCategoryTableModel(AbstractDataAccess dao) {
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
        if (listInpCategory == null) {
            return null;
        }

        if (listInpCategory.isEmpty()) {
            return null;
        }

        try {
            InpCategory record = listInpCategory.get(row);

            switch (column) {
                case 0://Code
                    return record.getUserCode();
                case 1: //Description
                    return record.getCatName();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listInpCategory == null) {
            return;
        }

        if (listInpCategory.isEmpty()) {
            return;
        }

        try {
            InpCategory record = listInpCategory.get(row);

            switch (column) {
                case 0: //Code
                    if (value == null) {
                        record.setUserCode(null);
                    } else {
                        record.setUserCode(value.toString());
                    }
                    break;
                case 1: //Description
                    if (!Util1.getString(value, "-").equals("-")) {
                        record.setCatName(value.toString());

                    } else {
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Invalid description.", "Inpatient Category",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    break;
            }

            save(record);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        try {
            fireTableCellUpdated(row, column);
        } catch (Exception ex) {

        }
    }

    private void save(InpCategory record) {
        if (!Util1.getString(record.getCatName(), "-").equals("-")) {
            try {
                dao.save(record);
                addNewRow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Util1.getParent(),
                        ex.toString(), "Inpatient Category Save Error",
                        JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public int getRowCount() {
        if (listInpCategory == null) {
            return 0;
        } else {
            return listInpCategory.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<InpCategory> getListInpCategory() {
        return listInpCategory;
    }

    public void setListInpCategory(List<InpCategory> listInpCategory) {
        this.listInpCategory = listInpCategory;
        fireTableDataChanged();
    }

    public InpCategory getInpCategory(int row) {
        if (listInpCategory != null) {
            if (!listInpCategory.isEmpty()) {
                return listInpCategory.get(row);
            }
        }

        return null;
    }

    public void setInpCategory(int row, InpCategory oPDCategory) {
        if (listInpCategory != null) {
            if (!listInpCategory.isEmpty()) {
                listInpCategory.set(row, oPDCategory);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addInpCategory(InpCategory oPDCategory) {
        if (listInpCategory != null) {
            listInpCategory.add(oPDCategory);
            fireTableRowsInserted(listInpCategory.size() - 1, listInpCategory.size() - 1);
        }
    }

    public void deleteInpCategory(int row) {
        if (listInpCategory != null) {
            listInpCategory.remove(row);
            fireTableRowsDeleted(0, listInpCategory.size() - 1);
        }
    }

    public void getCategory() {
        listInpCategory = dao.findAllHSQL("select o from InpCategory o order by o.userCode");
        if (listInpCategory == null) {
            listInpCategory = new ArrayList();
        }

        addNewRow();
        fireTableDataChanged();
    }

    private void addNewRow() {
        if (listInpCategory != null) {
            int count = listInpCategory.size();

            if (count == 0 || listInpCategory.get(count - 1).getCatId() != null) {
                listInpCategory.add(new InpCategory());
            }
        }
    }

    public void delete(int row) {
        if (listInpCategory != null) {
            if (listInpCategory.isEmpty()) {
                return;
            }

            try {
                InpCategory record = listInpCategory.get(row);
                String sql;
                if (NumberUtil.NZeroL(record.getCatId()) > 0) {
                    dao.open();
                    dao.beginTran();
                    sql = "delete from inp_category where cat_id = '" + record.getCatId() + "'";
                    dao.deleteSQL(sql);
                }

                listInpCategory.remove(row);
                fireTableRowsDeleted(row, row);
                addNewRow();
            } catch (Exception e) {
                log.error("delete : " + e.toString());
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }
}
