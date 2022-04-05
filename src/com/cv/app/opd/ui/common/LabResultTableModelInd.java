/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.Gender;
import com.cv.app.opd.database.entity.OPDLabResultInd;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
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
public class LabResultTableModelInd extends AbstractTableModel {

    static Logger log = Logger.getLogger(LabResultTableModelInd.class.getName());
    private List<OPDLabResultInd> listResultInd = new ArrayList();
    private final String[] columnNames = {"Low Value", "High Value", "Sex"};
    private final AbstractDataAccess dao;
    private Integer serviceId = -1;

    public LabResultTableModelInd(AbstractDataAccess dao) {
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
        switch (column) {
            case 0: //Low Value
                return String.class;
            case 1: //High Value
                return String.class;
            case 2: //Sex
                return Gender.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        OPDLabResultInd result = listResultInd.get(row);

        switch (column) {
            case 0: //Low Value
                return result.getLowValue();
            case 1: //High Value
                return result.getHighValue();
            case 2: //Sex
                return result.getSex();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OPDLabResultInd result = listResultInd.get(row);

        switch (column) {
            case 0: //Low Value
                if (value != null) {
                    result.setLowValue(value.toString());
                } else {
                    result.setLowValue(null);
                }
                break;
            case 1: //High Value
                if (value != null) {
                    result.setHighValue(value.toString());
                } else {
                    result.setHighValue(null);
                }
                break;
            case 2: //Sex
                if (value != null) {
                    Gender sex = (Gender) value;
                    result.setSex(sex);
                } else {
                    result.setSex(null);
                }
                break;
        }

        saveRecord(result);
        addNewRow();
    }

    private void saveRecord(OPDLabResultInd result) {
        if ((result.getHighValue() != null || result.getLowValue() != null
                || result.getSex() != null) && serviceId != -1) {
            if (result.getResultId() == null) {
                result.setResultId(serviceId);
            }

            try {
                dao.save(result);
            } catch (Exception ex) {
                log.error("saveRecord : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            }
        }
    }

    @Override
    public int getRowCount() {
        return listResultInd.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OPDLabResultInd> getListResult() {
        return listResultInd;
    }

    public void setListResult(List<OPDLabResultInd> listResultInd) {
        this.listResultInd = listResultInd;
        fireTableDataChanged();
    }

    public void setSelectResultId(Integer serviceId) {
        this.serviceId = serviceId;
        String strSql = "select o from OPDLabResultInd o where o.resultId = " + serviceId;
        List<OPDLabResultInd> tmpListResultInd = dao.findAllHSQL(strSql);

        if (tmpListResultInd == null) {
            tmpListResultInd = new ArrayList();
        }

        setListResult(tmpListResultInd);
        addNewRow();
    }

    public void addNewRow() {
        int count = listResultInd.size();

        if (count == 0 || listResultInd.get(count - 1).getResultId() != null) {
            listResultInd.add(new OPDLabResultInd());
            fireTableRowsInserted(listResultInd.size() - 1, listResultInd.size() - 1);
        }
    }

    public void delete(int index) {
        if (index > listResultInd.size() - 1) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid Data.",
                    "Invalid index", JOptionPane.ERROR_MESSAGE);
        } else {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "Result delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                try {
                    OPDLabResultInd tmpInd = listResultInd.get(index);
                    dao.open();
                    dao.beginTran();
                    String sql = "delete from opd_lab_result_ind where ind_id = "
                            + tmpInd.getIndId().toString();
                    dao.deleteSQL(sql);
                    listResultInd.remove(index);
                    fireTableRowsDeleted(index, index);
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
}
