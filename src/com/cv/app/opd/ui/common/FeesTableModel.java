/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.FeesTemplate;
import com.cv.app.opd.database.entity.ServiceFees;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.JoSQLUtil;
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
public class FeesTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(FeesTableModel.class.getName());
    private List<ServiceFees> listServiceFees = new ArrayList();
    private String[] columnNames = {"Description", "Fees"};
    private AbstractDataAccess dao;
    private int srvId;
    private int groupId;
    private SelectionObserver observer;
    private boolean versionUpdate = false;

    public FeesTableModel(AbstractDataAccess dao, SelectionObserver observer) {
        this.dao = dao;
        this.observer = observer;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Description
                return String.class;
            case 1: //Fees
                return Double.class;
            default:
                return Object.class;
        }

    }

    @Override
    public Object getValueAt(int row, int column) {
        ServiceFees record = listServiceFees.get(row);

        switch (column) {
            case 0: //Description
                return record.getFeesDesp();
            case 1: //Fees
                return record.getFee();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        ServiceFees record = listServiceFees.get(row);

        switch (column) {
            case 0: //Description

                break;
            case 1: //Fees
                if (NumberUtil.isNumber(value)) {
                    record.setFee(NumberUtil.getDouble(value));
                    try {
                        dao.save(record);
                        if (!versionUpdate) {
                            observer.selected("FeesChange", null);
                            versionUpdate = true;
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Save Error.", "Data cannot save.",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid number.", "Invalid fees.",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
        }

        saveRecord(record);
        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return listServiceFees.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<ServiceFees> getListServiceFees() {
        return listServiceFees;
    }

    public void setListService(List<ServiceFees> listServiceFees) {
        this.listServiceFees = listServiceFees;
        fireTableDataChanged();
    }

    public ServiceFees getServiceFees(int row) {
        return listServiceFees.get(row);
    }

    public void setServiceFees(int row, ServiceFees serviceFees) {
        listServiceFees.set(row, serviceFees);
        fireTableRowsUpdated(row, row);
    }

    public void setSrvId(int id) {
        versionUpdate = false;
        srvId = id;
        getServiceFees();
    }

    private void getServiceFees() {
        listServiceFees = dao.findAll("ServiceFees", "serviceId = " + srvId);
        List<FeesTemplate> listFeesTemplate = dao.findAll("FeesTemplate",
                "groupId = " + groupId);

        if (listServiceFees.isEmpty() && srvId != -1) {
            for (FeesTemplate ft : listFeesTemplate) {
                ServiceFees sf = new ServiceFees();
                sf.setFeesDesp(ft.getTemplateName());
                sf.setServiceId(srvId);
                listServiceFees.add(sf);
            }
        }

        if (listServiceFees.size() != listFeesTemplate.size() && srvId != -1) {
            String strFilter = null;

            for (ServiceFees sf : listServiceFees) {
                if (strFilter == null) {
                    strFilter = "'" + sf.getFeesDesp() + "'";
                } else {
                    strFilter = strFilter + ",'" + sf.getFeesDesp() + "'";
                }
            }

            String strSQL = "SELECT * FROM com.cv.app.opd.database.entity.FeesTemplate"
                    + " WHERE templateName NOT IN (" + strFilter + ")";
            List<FeesTemplate> list = JoSQLUtil.getResult(strSQL, listFeesTemplate);

            for (FeesTemplate ft : list) {
                ServiceFees sf = new ServiceFees();
                sf.setFeesDesp(ft.getTemplateName());
                sf.setServiceId(srvId);
                listServiceFees.add(sf);
            }
        }

        fireTableDataChanged();
    }

    private void saveRecord(ServiceFees record) {
        try {
            dao.save(record);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(Util1.getParent(),
                    ex.toString(), "Fee Save Error",
                    JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } finally {
            dao.close();
        }
    }

    public void setGroupId(int id) {
        groupId = id;
    }
}
