/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.LabResultAutoText;
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
public class AutoCompleteTextTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(AutoCompleteTextTableModel.class.getName());
    private List<LabResultAutoText> listLRAT = new ArrayList();
    private final String[] columnNames = {"Description"};
    private final AbstractDataAccess dao = Global.dao;

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
        LabResultAutoText record = listLRAT.get(row);

        switch (column) {
            case 0: //Description
                return record.getAutoText();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        LabResultAutoText record = listLRAT.get(row);

        switch (column) {
            case 0: //Description
                if (!Util1.getString(value, "-").equals("-")) {
                    record.setAutoText(value.toString());
                    try {
                        dao.save(record);
                        addNewRow();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                ex.toString(), "Auto Text Save Error",
                                JOptionPane.ERROR_MESSAGE);
                        dao.rollBack();
                    } finally {
                        dao.close();
                    }
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid description.", "Auto Text",
                            JOptionPane.ERROR_MESSAGE);
                }
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return listLRAT.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    private void addNewRow() {
        int count = listLRAT.size();

        if (count == 0 || listLRAT.get(count - 1).getId() != null) {
            listLRAT.add(new LabResultAutoText());
        }
    }

    public void delete(int row) {
        LabResultAutoText record = listLRAT.get(row);
        String sql;
        if (NumberUtil.NZeroL(record.getId()) > 0) {
            try {
                dao.open();
                dao.beginTran();
                sql = "delete from opd_lab_result_auto_text where id = " + record.getId().toString();
                dao.deleteSQL(sql);
                listLRAT.remove(row);
                fireTableRowsDeleted(row, row);
                addNewRow();
            } catch (Exception e) {
                dao.rollBack();
                log.error("delete : " + e);
            } finally {
                dao.close();
            }
        }
    }

    public List<LabResultAutoText> getListLRAT() {
        return listLRAT;
    }

    public void setListLRAT(List<LabResultAutoText> listLRAT) {
        this.listLRAT = listLRAT;
        fireTableDataChanged();
    }

    public void getAutoText() {
        try {
            List<LabResultAutoText> list = dao.findAllHSQL(
                    "select o from LabResultAutoText o order by o.autoText");
            setListLRAT(list);
        } catch (Exception ex) {
            log.error("getAutoText : " + ex.getMessage());
        } finally {
            dao.close();
        }
        addNewRow();
    }

    public LabResultAutoText getResultText(int index) {
        return listLRAT.get(index);
    }

    public int getSize() {
        if (listLRAT != null) {
            return listLRAT.size();
        } else {
            return 0;
        }
    }
}
