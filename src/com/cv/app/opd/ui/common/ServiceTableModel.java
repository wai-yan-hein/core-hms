/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Service;
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
public class ServiceTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ServiceTableModel.class.getName());
    private List<Service> listService = new ArrayList();
    private final String[] columnNames = {"Code", "Description", "Fees", "Srv Fee", "MO Fee",
        "Staff Fee", "Tech Fee", "Refer Fee", "Read Fee", "%", "CFS", "Active", "Doctor"};
    private final AbstractDataAccess dao;
    private int catId;
    private int labGroupId;

    public ServiceTableModel(AbstractDataAccess dao) {
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
            case 3: //Srv Fee
                return Double.class;
            case 4: //MO Fee
                return Double.class;
            case 5: //Staff Fee
                return Double.class;
            case 6: //Tech Fee
                return Double.class;
            case 7: //Refer Fee
                return Double.class;
            case 8: //Read Fee
                return Double.class;
            case 9: //Percent
                return Boolean.class;
            case 10: //CFS
                return Boolean.class;
            case 11: //Active
                return Boolean.class;
            case 12: //Doctor
                return Doctor.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        Service record = listService.get(row);

        switch (column) {
            case 0: //Code
                return record.getServiceCode();
            case 1: //Description
                return record.getServiceName();
            case 2: //Fees
                return record.getFees();
            case 3: //Srv Fee
                return record.getFees1();
            case 4: //MO Fee
                return record.getFees2();
            case 5: //Staff Fee
                return record.getFees3();
            case 6: //Tech Fee
                return record.getFees4();
            case 7: //Refer Fee
                return record.getFees5();
            case 8: //Read Fee
                return record.getFees6();
            case 9: //Percent
                return record.isPercent();
            case 10: //CFS
                return record.isCfs();
            case 11: //Active
                return record.isStatus();
            case 12: //Doctor
                if (record.getDoctor() != null) {
                    return record.getDoctor().getDoctorName();
                } else {
                    return null;
                }
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        Service record = listService.get(row);

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
                    record.setCatId(catId);
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid description.", "OPD Service",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 2: //Fees
                if (value == null) {
                    record.setFees(NumberUtil.NZero(value));
                } else if (NumberUtil.isNumber(value.toString())) {
                    record.setFees(NumberUtil.NZero(value));
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid number.", "Invalid fees.",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 3: //Srv Fee
                if (value == null) {
                    record.setFees1(NumberUtil.NZero(value));
                } else if (NumberUtil.isNumber(value.toString())) {
                    record.setFees1(NumberUtil.NZero(value));
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid number.", "Invalid fees.",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 4: //MO Fee
                if (value == null) {
                    record.setFees2(NumberUtil.NZero(value));
                } else if (NumberUtil.isNumber(value.toString())) {
                    record.setFees2(NumberUtil.NZero(value));
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid number.", "Invalid fees.",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 5: //Staff Fee
                if (value == null) {
                    record.setFees3(NumberUtil.NZero(value));
                } else if (NumberUtil.isNumber(value.toString())) {
                    record.setFees3(NumberUtil.NZero(value));
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid number.", "Invalid fees.",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 6: //Tech Fee
                if (value == null) {
                    record.setFees4(NumberUtil.NZero(value));
                } else if (NumberUtil.isNumber(value.toString())) {
                    record.setFees4(NumberUtil.NZero(value));
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid number.", "Invalid fees.",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 7: //Refer Fee
                if (value == null) {
                    record.setFees5(NumberUtil.NZero(value));
                } else if (NumberUtil.isNumber(value.toString())) {
                    record.setFees5(NumberUtil.NZero(value));
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid number.", "Invalid fees.",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 8: //Read Fee
                if (value == null) {
                    record.setFees6(NumberUtil.NZero(value));
                } else if (NumberUtil.isNumber(value.toString())) {
                    record.setFees6(NumberUtil.NZero(value));
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid number.", "Invalid fees.",
                            JOptionPane.ERROR_MESSAGE);
                }
                break;
            case 9: //Percent
                Boolean percent = (Boolean) value;
                record.setPercent(percent);
                break;
            case 10: //CFS
                Boolean cfs = (Boolean) value;
                record.setCfs(cfs);
                break;
            case 11: //Active
                Boolean active = (Boolean) value;
                record.setStatus(active);
                break;
            case 12: //Doctor
                if (value == null) {
                    record.setDoctor(null);
                } else {
                    record.setDoctor((Doctor) value);
                }
                break;
        }

        if (Util1.getPropValue("system.opd.setup.autocalculate").equals("Y")) {
            record.setFees(NumberUtil.NZero(record.getFees1()) + NumberUtil.NZero(record.getFees2())
                    + NumberUtil.NZero(record.getFees3()) + NumberUtil.NZero(record.getFees4()) + NumberUtil.NZero(record.getFees5())
                    + NumberUtil.NZero(record.getFees6()));
        }

        if (labGroupId != -1) {
            record.setLabGroupId(labGroupId);
        } else {
            String prpValue = Util1.getPropValue("syste.opd.default.cus.grp.id");
            if (!prpValue.isEmpty()) {
                record.setLabGroupId(Integer.parseInt(prpValue));
            }
        }
        saveRecord(record);

        try {
            fireTableCellUpdated(row, column);
        } catch (Exception e) {

        }
    }

    @Override
    public int getRowCount() {
        return listService.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Service> getListService() {
        return listService;
    }

    public void setListService(List<Service> listService) {
        this.listService = listService;
        fireTableDataChanged();
    }

    public Service getService(int row) {
        return listService.get(row);
    }

    public void setService(int row, Service service) {
        listService.set(row, service);
        fireTableRowsUpdated(row, row);
    }

    public void addService(Service service) {
        listService.add(service);
        fireTableRowsInserted(listService.size() - 1, listService.size() - 1);
    }

    public void deleteService(int row) {
        listService.remove(row);
        fireTableRowsDeleted(0, listService.size() - 1);
    }

    public void setCatId(int id) {
        catId = id;
        getService();
    }

    private void getService() {
        String strSql = "select o from Service o where o.catId = " + catId;

        if (labGroupId != -1) {
            strSql = strSql + " and o.labGroupId = " + labGroupId;
        }

        try {
            listService = dao.findAllHSQL(strSql);
            addNewRow();
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("getService : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void addNewRow() {
        int count = listService.size();

        if ((count == 0 || listService.get(count - 1).getCatId() != null)
                && catId != -1) {
            listService.add(new Service());
        } else if (catId == -1) {
            listService.removeAll(listService);
            fireTableDataChanged();
        }
    }

    private boolean isValidEntry(Service record) {
        boolean status = false;

        if (record.getServiceName() != null) {
            if (!Util1.getString(record.getServiceName().trim(), "-").equals("-")) {
                status = true;
            } else {
                record.setCatId(catId);
            }
        }

        return status;
    }

    private void execOpdBackup(Service record) {
        if (record.getServiceId() != null) {
            try {
                dao.execProc("opd_backup", record.getServiceId().toString(),
                        DateUtil.toDateTimeStrMYSQL(new Date()),
                        Global.loginUser.getUserId());
            } catch (Exception ex) {
                log.error("execOpdBackup : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    private void saveRecord(Service record) {
        if (isValidEntry(record)) {
            try {
                execOpdBackup(record);
                dao.save(record);

                addNewRow();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Util1.getParent(),
                        ex.toString(), "OPD Service Save Error",
                        JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    public void updatePriceVersion(int row) {
        Service record = getService(row);

        record.setPriceVersionId(NumberUtil.NZeroInt(record.getPriceVersionId()) + 1);
        saveRecord(record);
    }

    public void updateMedUVersion(int row) {
        Service record = getService(row);

        record.setMeduVersionId(NumberUtil.NZeroInt(record.getMeduVersionId()) + 1);
        saveRecord(record);
    }

    public void delete(int row) {
        Service record = listService.get(row);
        String sql;
        if (NumberUtil.NZeroL(record.getServiceId()) > 0) {
            try {
                dao.open();
                dao.beginTran();
                sql = "delete from opd_service where service_id = '" + record.getServiceId() + "' and cat_id='" + record.getCatId() + "'";
                dao.deleteSQL(sql);
            } catch (Exception e) {
                dao.rollBack();
            } finally {
                dao.close();
            }
        }

        listService.remove(row);

        fireTableRowsDeleted(row, row);

        addNewRow();
    }

    public int getLabGroupId() {
        return labGroupId;
    }

    public void setLabGroupId(int labGroupId) {
        this.labGroupId = labGroupId;
        getService();
    }
}
