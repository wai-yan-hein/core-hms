/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.view.VOpd;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ReaderEntryTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ReaderEntryTableModel.class.getName());
    private List<VOpd> listService = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Reg No", "Admission No", "Pt-Name", "Vou Total", "Service Name",
        "Doctor", "Refer Dr", "Read Dr", "Technician", "S", "P"};
    private final AbstractDataAccess dao = Global.dao;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        VOpd rec = listService.get(row);
        Date vouSaleDate = rec.getOpdDate();
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            return false;
        }
        return column == 8 || column == 9 || column == 10 || column == 11 || column == 12;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 5:
                return Double.class;
            case 11:
            case 12:
                return Boolean.class;
            default:
                return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        VOpd record = listService.get(row);

        switch (column) {
            case 0: //Date
                return DateUtil.toDateStr(record.getOpdDate());
            case 1: //Vou No
                if (record.getKey() != null) {
                    return record.getKey().getVouNo();
                } else {
                    return null;
                }
            case 2: //Reg No
                return record.getPatientId();
            case 3: //Admission No
                return record.getAdmissionNo();
            case 4: //Pt-Name
                return record.getPatientName();
            case 5: //Vou Total
                return record.getVouTotal();
            case 6: //Service Name
                return record.getServiceName();
            case 7: //Dr
                return record.getDoctorName();
            case 8: //Refer Dr
                return record.getReferDrName();
            case 9: //Read Dr
                return record.getReaderDrName();
            case 10: //Technician
                return record.getTechName();
            case 11: //Status
                return record.isCompleteStatus();
            case 12: //Print
                return record.getPrint();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        VOpd record = listService.get(row);

        switch (column) {
            case 8: //Refer Dr
                if (value != null) {
                    //String vouNo = record.getKey().getVouNo();
                    Doctor dr = (Doctor) value;

                    //record.setDoctorId(dr.getDoctorId());
                    //record.setDoctorName(dr.getDoctorName());
                    record.setReferDrId(dr.getDoctorId());
                    record.setReferDrName(dr.getDoctorName());

                    try {
                        /*String strSql = "update opd_his set doctor_id = '" + dr.getDoctorId() 
                                + "' where opd_inv_id = '" + vouNo + "'";
                        
                        dao.execSql(strSql);*/
                        String strSql = "update opd_details_his set refer_doctor_id = '" + dr.getDoctorId()
                                + "' where opd_detail_id = '" + record.getKey().getOpdDetailId() + "'";
                        dao.execSql(strSql);
                    } catch (Exception ex) {
                        log.error("Refer Dr save : " + ex);
                    } finally {
                        dao.close();
                    }
                } else {
                    //String vouNo = record.getKey().getVouNo();

                    //record.setDoctorId(null);
                    //record.setDoctorName(null);
                    record.setReferDrId(null);
                    record.setReferDrName(null);
                    try {
                        /*String strSql = "update opd_his set doctor_id = null where opd_inv_id = '" + vouNo + "'";
                        dao.execSql(strSql);*/
                        String strSql = "update opd_details_his set refer_doctor_id = null where opd_detail_id = '"
                                + record.getKey().getOpdDetailId() + "'";
                        dao.execSql(strSql);
                    } catch (Exception ex) {
                        log.error("Refer Dr save1 : " + ex);
                    } finally {
                        dao.close();
                    }
                }
                break;
            case 9: //Read Dr
                if (value != null) {
                    Doctor dr = (Doctor) value;

                    record.setReaderDrId(dr.getDoctorId());
                    record.setReaderDrName(dr.getDoctorName());

                    try {
                        String strSql = "update opd_details_his set reader_doctor_id = '" + dr.getDoctorId()
                                + "' where opd_detail_id = '" + record.getKey().getOpdDetailId() + "'";
                        dao.execSql(strSql);
                    } catch (Exception ex) {
                        log.error("Read Dr save : " + ex);
                    } finally {
                        dao.close();
                    }
                } else {
                    record.setReaderDrId(null);
                    record.setReaderDrName(null);

                    try {
                        String strSql = "update opd_details_his set reader_doctor_id = null where opd_detail_id = '"
                                + record.getKey().getOpdDetailId() + "'";
                        dao.execSql(strSql);
                    } catch (Exception ex) {
                        log.error("Read Dr save1 : " + ex);
                    } finally {
                        dao.close();
                    }
                }
                break;
            case 10: //Technician
                if (value != null) {
                    Doctor tech = (Doctor) value;

                    record.setTechId(tech.getDoctorId());
                    record.setTechName(tech.getDoctorName());
                    try {
                        String strSql = "update opd_details_his set tech_id = '" + tech.getDoctorId()
                                + "' where opd_detail_id = '" + record.getKey().getOpdDetailId() + "'";
                        dao.execSql(strSql);
                    } catch (Exception ex) {
                        log.error("Technician save : " + ex);
                    } finally {
                        dao.close();
                    }
                } else {
                    record.setTechId(null);
                    record.setTechName(null);

                    try {
                        String strSql = "update opd_details_his set tech_id = null "
                                + "where opd_detail_id = '" + record.getKey().getOpdDetailId() + "'";
                        dao.execSql(strSql);
                    } catch (Exception ex) {
                        log.error("Technician save1 : " + ex);
                    } finally {
                        dao.close();
                    }
                }
                break;
            case 11: //Status
                Boolean status = (Boolean) value;
                record.setCompleteStatus(status);
                try {
                    String strSql = "update opd_details_his set complete_status = " + status.toString()
                            + " where opd_detail_id = '" + record.getKey().getOpdDetailId() + "'";
                    dao.execSql(strSql);

                    //For X-Ray print
                    /*if (status) {
                        String strCatId = Util1.getPropValue("system.opd.xray.group");
                        if (strCatId != null) {
                            int catId = Integer.parseInt(strCatId);
                            if (catId == record.getCatId()) {
                                printXRayForm(record);
                            }
                        }
                    }*/
                } catch (Exception ex) {
                    log.error("status : " + ex.getMessage());
                }
                break;
            case 12: //Print
                try {
                String userId = Global.machineId;
                String detailId = record.getKey().getOpdDetailId();
                Boolean print = (Boolean) value;
                record.setPrint(print);
                String strSql;
                if (print) {
                    strSql = "insert into tmp_xray_print(user_id, opd_detail_id) "
                            + "values('" + userId + "','" + detailId + "')";
                } else {
                    strSql = "delete from tmp_xray_print where "
                            + "user_id = '" + userId + "' and opd_detail_id = '" + detailId + "'";
                }
                dao.execSql(strSql);
            } catch (Exception ex) {
                log.error("print : " + ex.getMessage());
            }
            break;
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return listService.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void search(String from, String to, String serviceId, String regNo,
            String ptName, String groupId, String complete) {
        String strSql = "select o from VOpd o where date(o.opdDate) between '"
                + DateUtil.toDateStrMYSQL(from) + "' and '"
                + DateUtil.toDateStrMYSQL(to) + "' and o.deleted = false";

        if (!serviceId.equals("-")) {
            strSql = strSql + " and o.key.serviceId = " + serviceId;
        }

        if (regNo.isEmpty()) {
            regNo = "-";
        }

        if (ptName.isEmpty()) {
            ptName = "-";
        }

        if (!regNo.equals("-")) {
            strSql = strSql + " and o.patientId = '" + regNo + "'";
        } else if (!ptName.equals("-")) {
            strSql = strSql + " and o.patientName like '" + ptName + "%'";
        }

        if (!groupId.equals("-")) {
            strSql = strSql + " and o.catId in (select a.key.opdCatId "
                    + "from OPDCusLabGroupDetail a where a.key.cusGrpId = " + groupId + ")";
        }

        if (!complete.equals("-")) {
            if (complete.equals("Complete")) {
                strSql = strSql + " and o.completeStatus = true";
            } else if (complete.equals("In-Complete")) {
                strSql = strSql + " and o.completeStatus = false";
            }
        }

        strSql = strSql + " order by o.opdDate, o.key.vouNo";

        try {
            listService = dao.findAllHSQL(strSql);
            System.gc();
            fireTableDataChanged();
        } catch (Exception ex) {
            log.error("search : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void printXRayForm(VOpd record) {
        String reportName = Util1.getPropValue("report.xray.form");
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + "clinic\\"
                + reportName;
        Map<String, Object> params = new HashMap();
        params.put("p_detail_id", record.getKey().getOpdDetailId());
        String compName = Util1.getPropValue("report.company.name");
        String phoneNo = Util1.getPropValue("report.phone");
        String address = Util1.getPropValue("report.address");
        params.put("compName", compName);
        params.put("phoneNo", phoneNo);
        params.put("comAddress", address);
        params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));
        try {
            dao.close();
            ReportUtil.viewReport(reportPath, params, dao.getConnection());
            dao.commit();
        } catch (Exception ex) {
            log.error("printXRayForm : " + ex.getMessage());
        }
    }
}
