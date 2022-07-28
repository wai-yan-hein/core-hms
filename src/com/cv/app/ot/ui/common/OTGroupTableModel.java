/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.ot.database.entity.OTProcedureGroup;
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
public class OTGroupTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OTGroupTableModel.class.getName());
    private List<OTProcedureGroup> listOTG = new ArrayList();
    private final String[] columnNames = {"Code", "Description"};
    private final AbstractDataAccess dao;

    public OTGroupTableModel(AbstractDataAccess dao) {
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
        OTProcedureGroup record = listOTG.get(row);

        switch (column) {
            case 0: //Code
                return record.getUserCode();
            case 1: //Description
                return record.getGroupName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OTProcedureGroup record = listOTG.get(row);

        switch (column) {
            case 0: //Code
                if (value != null) {
                    record.setUserCode(value.toString());
                } else {
                    record.setUserCode(null);
                }
                break;
            case 1: //Description
                if (!Util1.getString(value, "-").equals("-")) {
                    record.setGroupName(value.toString());

                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid description.", "OT Group",
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

    private void save(OTProcedureGroup record) {
        if (!Util1.getString(record.getGroupName(), "-").equals("-")) {
            try {
                dao.save(record);
                addNewRow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Util1.getParent(),
                        ex.toString(), "OT Group Save Error",
                        JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public int getRowCount() {
        return listOTG.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OTProcedureGroup> getListOTG() {
        return listOTG;
    }

    public void setListOTG(List<OTProcedureGroup> listOTG) {
        this.listOTG = listOTG;
        fireTableDataChanged();
    }

    public OTProcedureGroup getOTProcedureGroup(int row) {
        if (listOTG != null) {
            if (!listOTG.isEmpty()) {
                if (listOTG.size() > row) {
                    return listOTG.get(row);
                }
            }
        }

        return null;
    }

    public void getOTGroup() {
        try {
            listOTG = dao.findAllHSQL("select o from OTProcedureGroup o order by o.userCode");
            if (listOTG == null) {
                listOTG = new ArrayList();
            }
            addNewRow();
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("getOTGroup : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void addNewRow() {
        int count = listOTG.size();

        if (count == 0 || listOTG.get(count - 1).getGroupId() != null) {
            listOTG.add(new OTProcedureGroup());
        }
    }

    public void delete(int row) {
        OTProcedureGroup record = listOTG.get(row);
        String sql;
        if (NumberUtil.NZeroL(record.getGroupId()) > 0) {
            try {
                dao.open();
                dao.beginTran();
                sql = "delete from ot_group where group_id = " + record.getGroupId().toString();
                dao.deleteSQL(sql);
                listOTG.remove(row);
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
}
