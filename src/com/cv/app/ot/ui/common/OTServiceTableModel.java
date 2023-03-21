/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.common.Global;
import com.cv.app.ot.database.entity.OTProcedure;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class OTServiceTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(OTServiceTableModel.class.getName());
    private List<OTProcedure> listOTP = new ArrayList();
    private final String[] columnNames = {"Code", "Description", "Fees",
        "Hospital", "Staff", "Nurse", "MO", "Active", "CFS"};
    private final AbstractDataAccess dao;
    private int groupId;

    public OTServiceTableModel(AbstractDataAccess dao) {
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
            case 0: //Code
                return String.class;
            case 1: //Description
                return String.class;
            case 2: //Fees
                return Double.class;
            case 3: //Hospital
                return Double.class;
            case 4: //Staff
                return Double.class;
            case 5: //Nurse
                return Double.class;
            case 6: //MO
                return Double.class;
            case 7: //Active
                return Boolean.class;
            case 8: //Cfs
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        OTProcedure record = listOTP.get(row);

        switch (column) {
            case 0: //Code
                return record.getServiceCode();
            case 1: //Description
                return record.getServiceName();
            case 2: //Fees
                return record.getSrvFees();
            case 3: //Hospital
                return record.getSrvFees1();
            case 4: //Staff
                return record.getSrvFees2();
            case 5: //Nurse
                return record.getSrvFees3();
            case 6: //MO
                return record.getSrvFees4();
            case 7: //Active
                return record.isStatus();
            case 8: //Cfs
                return record.isCfs();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        OTProcedure record = listOTP.get(row);

        switch (column) {
            case 0: //Code
                if (value == null) {
                    record.setServiceCode(null);
                } else {
                    record.setServiceCode(value.toString());
                }
                break;
            case 1: //Description
                if (!Util1.getString(value, "-").equals("-")) {
                    record.setServiceName(value.toString());
                    record.setGroupId(groupId);
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid description.", "OT Service",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 2: //Fees
                if (value == null) {
                    record.setSrvFees(NumberUtil.NZero(value));
                } else if (NumberUtil.isNumber(value.toString())) {
                    record.setSrvFees(NumberUtil.NZero(value));
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid number.", "Invalid fees.",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 3: //Hospital
                if (value == null) {
                    record.setSrvFees1(NumberUtil.NZero(value));
                } else if (NumberUtil.isNumber(value.toString())) {
                    record.setSrvFees1(NumberUtil.NZero(value));
                }
                break;
            case 4: //Staff
                if (value == null) {
                    record.setSrvFees2(NumberUtil.NZero(value));
                } else if (NumberUtil.isNumber(value.toString())) {
                    record.setSrvFees2(NumberUtil.NZero(value));
                }
                break;
            case 5: //Nurse
                if (value == null) {
                    record.setSrvFees3(NumberUtil.NZero(value));
                } else if (NumberUtil.isNumber(value.toString())) {
                    record.setSrvFees3(NumberUtil.NZero(value));
                }
                break;
            case 6: //MO
                if (value == null) {
                    record.setSrvFees4(NumberUtil.NZero(value));
                } else if (NumberUtil.isNumber(value.toString())) {
                    record.setSrvFees4(NumberUtil.NZero(value));
                }
                break;
            case 7: //Active
                if (value != null) {
                    Boolean status = (Boolean) value;
                    record.setStatus(status);
                }
                break;
            case 8: //cfs
                if (value != null) {
                    Boolean cfs = (Boolean) value;
                    record.setCfs(cfs);
                }
                break;
        }
        if (Util1.getPropValue("system.ot.setup.autocalculate").equals("Y")) {
            record.setSrvFees(NumberUtil.NZero(record.getSrvFees1()) + NumberUtil.NZero(record.getSrvFees2())
                    + NumberUtil.NZero(record.getSrvFees3()) + NumberUtil.NZero(record.getSrvFees4()));
        }
        saveRecord(record);
        try {
            fireTableCellUpdated(row, column);
        } catch (Exception ex) {

        }
    }

    @Override
    public int getRowCount() {
        return listOTP.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OTProcedure> getListOTP() {
        return listOTP;
    }

    public void setListOTP(List<OTProcedure> listOTP) {
        this.listOTP = listOTP;
        fireTableDataChanged();
    }

    public OTProcedure getService(int row) {
        return listOTP.get(row);
    }

    public void setGroupId(int id) {
        groupId = id;
        getService();
    }

    private void getService() {
        try {
            listOTP = dao.findAllHSQL("select o from OTProcedure o where o.groupId = " + groupId + " order by o.serviceName");
            addNewRow();
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("getService : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void addNewRow() {
        int count = listOTP.size();

        if ((count == 0 || listOTP.get(count - 1).getGroupId() != null)
                && groupId != -1) {
            listOTP.add(new OTProcedure());
        } else if (groupId == -1) {
            listOTP.removeAll(listOTP);
            fireTableDataChanged();
        }
    }

    private boolean isValidEntry(OTProcedure record) {
        boolean status = false;

        if (!Util1.getString(record.getServiceName(), "-").equals("-")) {
            status = true;
        } else {
            record.setGroupId(groupId);
        }

        return status;
    }

    private void saveRecord(OTProcedure record) {
        if (isValidEntry(record)) {
            try {
                execOtBackup(record);
                dao.save(record);
                addNewRow();
            } catch (Exception ex) {
                log.error("saveRecord : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
                JOptionPane.showMessageDialog(Util1.getParent(),
                        ex.toString(), "OT Service Save Error",
                        JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    public void updateMedUVersion(int row) {
        OTProcedure record = getService(row);
        //record.setMeduVersionId(NumberUtil.NZeroInt(record.getMeduVersionId()) + 1);
        saveRecord(record);
    }

    public void delete(int row) {
        OTProcedure record = listOTP.get(row);
        String sql;
        if (NumberUtil.NZeroL(record.getServiceId()) > 0) {
            try {
                dao.open();
                dao.beginTran();
                sql = "delete from ot_service where service_id = '"
                        + record.getServiceId() + "' and group_id= " + record.getGroupId();
                dao.deleteSQL(sql);
                listOTP.remove(row);
                fireTableRowsDeleted(row, row);
                addNewRow();
            } catch (Exception e) {
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    private void execOtBackup(OTProcedure record) {
        if (record.getServiceId() != null) {
            try {
                dao.execProc("ot_setbackup", record.getServiceId().toString(),
                        DateUtil.toDateTimeStrMYSQL(new Date()),
                        Global.loginUser.getUserId());
            } catch (Exception ex) {
                log.error("execOtBackup : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }
}
