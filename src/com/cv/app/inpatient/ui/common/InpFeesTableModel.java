/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.InpFeesTemplate;
import com.cv.app.inpatient.database.entity.InpServiceFees;
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
public class InpFeesTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(InpFeesTableModel.class.getName());
    private List<InpServiceFees> listServiceFees = new ArrayList();
    private final String[] columnNames = {"Description", "Fees"};
    private AbstractDataAccess dao;
    private int srvId;
    private int groupId;
    private SelectionObserver observer;
    private boolean versionUpdate = false;

    public InpFeesTableModel(AbstractDataAccess dao, SelectionObserver observer) {
        this.dao = dao;
        this.observer = observer;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0;
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
        if (listServiceFees == null) {
            return null;
        }

        if (listServiceFees.isEmpty()) {
            return null;
        }

        try {
            InpServiceFees record = listServiceFees.get(row);

            switch (column) {
                case 0: //Description
                    return record.getFeesDesp();
                case 1: //Fees
                    return record.getFee();
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
        if (listServiceFees == null) {
            return;
        }

        if (listServiceFees.isEmpty()) {
            return;
        }

        try {
            InpServiceFees record = listServiceFees.get(row);

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
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    @Override
    public int getRowCount() {
        if (listServiceFees == null) {
            return 0;
        }
        return listServiceFees.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<InpServiceFees> getListServiceFees() {
        return listServiceFees;
    }

    public void setListService(List<InpServiceFees> listServiceFees) {
        this.listServiceFees = listServiceFees;
        fireTableDataChanged();
    }

    public InpServiceFees getServiceFees(int row) {
        if (listServiceFees == null) {
            return null;
        }
        return listServiceFees.get(row);
    }

    public void setServiceFees(int row, InpServiceFees serviceFees) {
        if (listServiceFees != null) {
            if (!listServiceFees.isEmpty()) {
                listServiceFees.set(row, serviceFees);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void setSrvId(int id) {
        versionUpdate = false;
        srvId = id;
        getServiceFees();
    }

    private void getServiceFees() {
        listServiceFees = dao.findAll("ServiceFees", "serviceId = " + srvId);
        List<InpFeesTemplate> listFeesTemplate = dao.findAll("InpFeesTemplate",
                "groupId = " + groupId);

        if (listServiceFees != null) {
            if (listServiceFees.isEmpty() && srvId != -1) {
                for (InpFeesTemplate ft : listFeesTemplate) {
                    InpServiceFees sf = new InpServiceFees();
                    sf.setFeesDesp(ft.getTemplateName());
                    sf.setServiceId(srvId);
                    listServiceFees.add(sf);
                }
            }

            if (listServiceFees.size() != listFeesTemplate.size() && srvId != -1) {
                String strFilter = null;

                for (InpServiceFees sf : listServiceFees) {
                    if (strFilter == null) {
                        strFilter = "'" + sf.getFeesDesp() + "'";
                    } else {
                        strFilter = strFilter + ",'" + sf.getFeesDesp() + "'";
                    }
                }

                String strSQL = "SELECT * FROM com.cv.app.opd.database.entity.InpFeesTemplate"
                        + " WHERE templateName NOT IN (" + strFilter + ")";
                List<InpFeesTemplate> list = JoSQLUtil.getResult(strSQL, listFeesTemplate);

                for (InpFeesTemplate ft : list) {
                    InpServiceFees sf = new InpServiceFees();
                    sf.setFeesDesp(ft.getTemplateName());
                    sf.setServiceId(srvId);
                    listServiceFees.add(sf);
                }
            }

            fireTableDataChanged();
        }
    }

    private void saveRecord(InpServiceFees record) {
        try {
            dao.save(record);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(Util1.getParent(),
                    ex.toString(), "Inpatient Fee Save Error",
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
