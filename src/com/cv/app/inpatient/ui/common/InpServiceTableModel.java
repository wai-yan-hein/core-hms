/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.common.Global;
import com.cv.app.inpatient.database.entity.InpService;
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
public class InpServiceTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(InpServiceTableModel.class.getName());
    private List<InpService> listInpService = new ArrayList();
    private final String[] columnNames = {"Code", "Description", "Fees", "Hospital", 
        "Nurse", "Tech", "MO", "Active", "CFS"};
    private final AbstractDataAccess dao;
    private int catId;

    public InpServiceTableModel(AbstractDataAccess dao) {
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
            case 4: //Nurse
                return Double.class;
            case 5: //Tech
                return Double.class;
            case 6: //MO
                return Double.class;
            case 7: //Active
                return Boolean.class;
            case 8: //CFS
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listInpService == null) {
            return null;
        }

        if (listInpService.isEmpty()) {
            return null;
        }

        try {
            InpService record = listInpService.get(row);

            switch (column) {
                case 0: //Code
                    return record.getServiceCode();
                case 1: //Description
                    return record.getServiceName();
                case 2: //Fees
                    return record.getFees();
                case 3: //Hospital
                    return record.getFees1();
                case 4: //Nurse
                    return record.getFees2();
                case 5: //Tech
                    return record.getFees3();
                case 6: //MO
                    return record.getFees4();
                case 7: //Active
                    return record.isStatus();
                case 8: //CFS
                    return record.isCfs();
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
        if (listInpService == null) {
            return;
        }

        if (listInpService.isEmpty()) {
            return;
        }

        try {
            InpService record = listInpService.get(row);

            switch (column) {
                case 0: //Code
                    if (value != null) {
                        record.setServiceCode(value.toString());
                    } else {
                        record.setServiceCode(null);
                    }
                    break;
                case 1: //Description
                    if (!Util1.getString(value, "-").equals("-")) {
                        record.setServiceName(value.toString());
                        record.setCatId(catId);
                    } else {
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Invalid description.", "Inpatient Service",
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
                case 3: //To Doctor
                    if (value != null) {
                        record.setFees1(NumberUtil.NZero(value));
                    } else {
                        record.setFees1(null);
                    }
                    break;
                case 4: //To Nurse
                    if (value != null) {
                        record.setFees2(NumberUtil.NZero(value));
                    } else {
                        record.setFees2(null);
                    }
                    break;
                case 5: //To Tech
                    if (value != null) {
                        record.setFees3(NumberUtil.NZero(value));
                    } else {
                        record.setFees3(null);
                    }
                    break;
                case 6: //MO
                    if (value != null) {
                        record.setFees4(NumberUtil.NZero(value));
                    } else {
                        record.setFees4(null);
                    }
                    break;
                case 7: //Status
                    if (value == null) {
                        record.setStatus(false);
                    } else {
                        Boolean status = (Boolean) value;
                        record.setStatus(status);
                    }
                    break;
                case 8: //CFS
                    if (value == null) {
                        record.setCfs(false);
                    } else {
                        Boolean status = (Boolean) value;
                        record.setCfs(status);
                    }
                    break;
            }
            if (Util1.getPropValue("system.dc.setup.autocalculate").equals("Y")) {
                record.setFees(NumberUtil.NZero(record.getFees1()) + NumberUtil.NZero(record.getFees2())
                        + NumberUtil.NZero(record.getFees3()) + NumberUtil.NZero(record.getFees4()));
            }
            saveRecord(record);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
        
        try{
            fireTableCellUpdated(row, column);
        }catch(Exception ex){
            
        }
    }

    @Override
    public int getRowCount() {
        if (listInpService == null) {
            return 0;
        }
        return listInpService.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<InpService> getListInpService() {
        return listInpService;
    }

    public void setListInpService(List<InpService> listInpService) {
        this.listInpService = listInpService;
        fireTableDataChanged();
    }

    public InpService getInpService(int row) {
        if (listInpService != null) {
            if (!listInpService.isEmpty()) {
                return listInpService.get(row);
            }
        }
        return null;
    }

    public void setInpService(int row, InpService service) {
        if (listInpService != null) {
            if (!listInpService.isEmpty()) {
                listInpService.set(row, service);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addInpService(InpService service) {
        if (listInpService != null) {
            if (!listInpService.isEmpty()) {
                listInpService.add(service);
                fireTableRowsInserted(listInpService.size() - 1, listInpService.size() - 1);
            }
        }
    }

    public void deleteInpService(int row) {
        if (listInpService != null) {
            if (!listInpService.isEmpty()) {
                listInpService.remove(row);
                fireTableRowsDeleted(0, listInpService.size() - 1);
            }
        }
    }

    public void setCatId(int id) {
        catId = id;
        getInpService();
    }

    private void getInpService() {
        //listInpService = dao.findAll("InpService", "catId = " + catId);
        listInpService = dao.findAllHSQL(
                "select o from InpService o where o.catId = " + catId + " order by o.serviceName");
        addNewRow();
        fireTableDataChanged();
    }

    private void addNewRow() {
        if (listInpService != null) {
            int count = listInpService.size();

            if ((count == 0 || listInpService.get(count - 1).getCatId() != null)
                    && catId != -1) {
                listInpService.add(new InpService());
            } else if (catId == -1) {
                listInpService.removeAll(listInpService);
                fireTableDataChanged();
            }
        }
    }

    private boolean isValidEntry(InpService record) {
        boolean status = false;

        if (!Util1.getString(record.getServiceName(), "-").equals("-")) {
            status = true;
        } else {
            record.setCatId(catId);
        }

        return status;
    }

    private void saveRecord(InpService record) {
        if (isValidEntry(record)) {
            try {
                execOtBackup(record);
                dao.save(record);
                addNewRow();
            } catch (Exception ex) {
                log.error("saveRecord : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                JOptionPane.showMessageDialog(Util1.getParent(),
                        ex.toString(), "Inpatient Service Save Error",
                        JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    public void updatePriceVersion(int row) {
        InpService record = getInpService(row);

        if (record != null) {
            record.setPriceVersionId(NumberUtil.NZeroInt(record.getPriceVersionId()) + 1);
            saveRecord(record);
        }
    }

    public void updateMedUVersion(int row) {
        InpService record = getInpService(row);

        if (record != null) {
            record.setMeduVersionId(NumberUtil.NZeroInt(record.getMeduVersionId()) + 1);
            saveRecord(record);
        }
    }

    public void delete(int row) {
        if (listInpService != null) {
            if (!listInpService.isEmpty()) {
                try {
                    InpService record = listInpService.get(row);
                    String sql;
                    if (NumberUtil.NZeroL(record.getServiceId()) > 0) {
                        dao.open();
                        dao.beginTran();
                        sql = "delete from inp_service where service_id = '" + record.getServiceId()
                                + "' and cat_id='" + record.getCatId() + "'";
                        dao.deleteSQL(sql);
                    }

                    listInpService.remove(row);
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

    private void execOtBackup(InpService record) {
        if (record.getServiceId() != null) {
            dao.execProc("inp_setbackup", record.getServiceId().toString(),
                    DateUtil.toDateTimeStrMYSQL(new Date()),
                    Global.loginUser.getUserId());
        }
    }
}
