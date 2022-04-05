/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.Technician;
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
public class TechnicianSetupTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(TechnicianSetupTableModel.class.getName());
    private List<Technician> listTech = new ArrayList();
    private final String[] columnNames = {"Technician Name", "Active"};
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
        if (column == 1) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Technician record = listTech.get(row);

        switch (column) {
            case 0: //Technician Name
                return record.getTechName();
            case 1: //Active
                return record.getActive();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        Technician record = listTech.get(row);

        switch (column) {
            case 0: //Description
                if (!Util1.getString(value, "-").equals("-")) {
                    record.setTechName(value.toString());
                } else {
                    record.setTechName(null);
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid technician name.", "Name",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 1: //Active
                if (value != null) {
                    Boolean active = (Boolean) value;
                    record.setActive(active);
                } else {
                    record.setActive(Boolean.FALSE);
                }
                break;
        }

        fireTableCellUpdated(row, column);

        if (!Util1.getString(value, "-").equals("-")) {
            try {
                if(record.getTechName() != null){
                    dao.save(record);
                }
                addNewRow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Util1.getParent(),
                        ex.toString(), "Technician Save Error",
                        JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public int getRowCount() {
        return listTech.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    private void addNewRow() {
        int count = listTech.size();

        if (count == 0 || listTech.get(count - 1).getTechId() != null) {
            listTech.add(new Technician());
        }
    }

    public void delete(int row) {
        Technician record = listTech.get(row);
        String sql;
        if (NumberUtil.NZeroL(record.getTechId()) > 0) {
            try {
                dao.open();
                dao.beginTran();
                sql = "delete from technician where tech_id = " + record.getTechId().toString();
                dao.deleteSQL(sql);
                listTech.remove(row);
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

    public List<Technician> getListTech() {
        return listTech;
    }

    public void setListTech(List<Technician> listTech) {
        this.listTech = listTech;
        fireTableDataChanged();
    }

    public void getTechnician() {
        List<Technician> list = dao.findAllHSQL(
                "select o from Technician o order by o.techName");
        setListTech(list);
        addNewRow();
    }

    public int getSize() {
        if (listTech != null) {
            return listTech.size();
        } else {
            return 0;
        }
    }
}
