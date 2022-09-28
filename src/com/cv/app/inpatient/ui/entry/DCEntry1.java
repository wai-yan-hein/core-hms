/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.entry;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.RegNo;
import com.cv.app.common.SelectionObserver;
import com.cv.app.emr.database.entity.AgeRange;
import com.cv.app.inpatient.database.entity.AdmissionKey;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.inpatient.database.entity.DCDetailHis;
import com.cv.app.inpatient.database.entity.DCDoctorFee;
import com.cv.app.inpatient.database.entity.DCHis;
import com.cv.app.inpatient.database.entity.DCStatus;
import com.cv.app.inpatient.database.entity.Diagnosis;
import com.cv.app.inpatient.database.healper.PackageDetailEdit;
import com.cv.app.inpatient.ui.common.DCTableCellEditor;
import com.cv.app.inpatient.ui.common.DCTableModel;
import com.cv.app.inpatient.ui.util.AdmissionSearch;
import com.cv.app.inpatient.ui.util.DCDoctorFeeDialog;
import com.cv.app.inpatient.ui.util.DCVouSearchDialog;
import com.cv.app.inpatient.ui.util.PackageEditDialog;
import com.cv.app.inpatient.ui.util.PackageSearchDialog;
import com.cv.app.opd.database.entity.ClinicPackage;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.entity.PatientBillPayment;
import com.cv.app.opd.database.tempentity.TempAmountLink;
import com.cv.app.opd.ui.common.AmountLinkTableModel;
import com.cv.app.opd.ui.util.DoctorSearchDialog;
import com.cv.app.ot.database.entity.DrDetailId;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.AccSetting;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.PatientBillTableModel;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jms.MapMessage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;
import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;
import org.josql.QueryResults;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author admin
 */
public class DCEntry1 extends javax.swing.JPanel implements FormAction, KeyPropagate,
        SelectionObserver, KeyListener {

    static Logger log = Logger.getLogger(DCEntry1.class.getName());
    private AbstractDataAccess dao = Global.dao;
    private DCHis currVou = new DCHis();
    private DCTableModel tableModel = new DCTableModel(dao);
    private boolean cboBindStatus = false;
    private GenVouNoImpl vouEngine = null;
    private String focusCtrlName = "-";
    private PatientBillTableModel tblPatientBillTableModel = new PatientBillTableModel();
    private PaymentType ptCash;
    private PaymentType ptCredit;
    private boolean canEdit = true;
    private final AmountLinkTableModel tblAmountLinkTableModel = new AmountLinkTableModel();

    /**
     * Creates new form DCEntry1
     */
    public DCEntry1() {
        initComponents();

        try {
            initCombo();
            txtVouNo.setFormatterFactory(new VouFormatFactory());
            if (Util1.getPropValue("system.login.default.value").equals("Y")) {
                if (Global.loginDate == null) {
                    Global.loginDate = DateUtil.getTodayDateStr();
                }
                txtDate.setText(Global.loginDate);
            } else {
                txtDate.setText(DateUtil.getTodayDateStr());
            }
            vouEngine = new GenVouNoImpl(dao, "DC",
                    DateUtil.getPeriod(txtDate.getText()));
            ptCash = (PaymentType) dao.find(PaymentType.class,
                    NumberUtil.NZeroInt(Util1.getPropValue("system.paymenttype.cash")));
            ptCredit = (PaymentType) dao.find(PaymentType.class,
                    NumberUtil.NZeroInt(Util1.getPropValue("system.paymenttype.credit")));
            initTable();
            tableModel.setParent(tblService);
            tableModel.addNewRow();
            tableModel.setVouStatus("NEW");
            actionMapping();
            initTextBoxAlign();
            assignDefaultValue();
            initForFocus();
            timerFocus();
            if (Util1.getPropValue("system.dc.link.amt").equals("Y")) {
                jPanel8.setVisible(true);
            } else {
                jPanel8.setVisible(false);
            }
        } catch (Exception ex) {
            log.error("OPD : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }

        butAdmit.setEnabled(false);
        butAdmit.setVisible(false);
        butPkgEdit.setEnabled(false);
        deleteLinkTemp();
    }

    public void timerFocus() {
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtPatientNo.requestFocus();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    //FormAction
    @Override
    public void save() {
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot save edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isValidEntry()) {
            Date vouSaleDate = DateUtil.toDate(txtDate.getText());
            Date lockDate = PharmacyUtil.getLockDate(dao);
            if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                        + DateUtil.toDateStr(lockDate) + ".",
                        "Locked Data", JOptionPane.ERROR_MESSAGE);
                return;
            }

            log.error("dc voucher save start : " + currVou.getOpdInvId());
            try {
                Date d = new Date();
                String strVouTotal = NumberUtil.NZero(currVou.getVouTotal()).toString();
                String strDiscA = NumberUtil.NZero(currVou.getDiscountA()).toString();
                String strPaid = NumberUtil.NZero(currVou.getPaid()).toString();
                String strBalance = NumberUtil.NZero(currVou.getVouBalance()).toString();
                dao.execProc("bk_dc",
                        currVou.getOpdInvId(),
                        DateUtil.toDateTimeStrMYSQL(d),
                        Global.loginUser.getUserId(),
                        Global.machineId,
                        strVouTotal,
                        strDiscA,
                        strPaid,
                        strBalance);
            } catch (Exception ex) {
                log.error("bk_dc : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
            } finally {
                dao.close();
            }
            log.error("dc voucher save after bk_dc : " + currVou.getOpdInvId());

            try {
                if (currVou.getDcStatus() != null) {
                    AdmissionKey key = new AdmissionKey();
                    key.setRegister(currVou.getPatient());
                    key.setAmsNo(currVou.getAdmissionNo());
                    Ams ams = (Ams) dao.find(Ams.class, key);
                    if (ams.getBuildingStructure() != null) {
                        String strSql = " update building_structure set reg_no = null "
                                + " where id =" + ams.getBuildingStructure().getId() + "";
                        dao.execSql(strSql);
                    }
                }

                if (lblStatus.getText().equals("NEW")) {
                    currVou.setCreatedBy(Global.loginUser);
                    currVou.setCreatedDate(new Date());
                } else {
                    currVou.setUpdatedBy(Global.loginUser);
                    currVou.setUpdatedDate(new Date());
                }

                deleteDetail();

                //dao.open();
                //dao.beginTran();
                String vouNo = currVou.getOpdInvId();
                List<DCDetailHis> listDetail = currVou.getListOPDDetailHis();
                for (DCDetailHis odh : listDetail) {
                    odh.setVouNo(vouNo);
                    if (odh.getOpdDetailId() == null) {
                        odh.setOpdDetailId(vouNo + "-" + odh.getUniqueId().toString());
                    }
                    if (odh.getListDCDF() != null) {
                        if (!odh.getListDCDF().isEmpty()) {
                            List<DCDoctorFee> listDF = odh.getListDCDF();
                            Integer maxUniqueId = NumberUtil.NZeroInt(listDF.get(listDF.size() - 1).getUniqueId());
                            for (DCDoctorFee odf : listDF) {
                                if (NumberUtil.NZeroInt(odf.getUniqueId()) == 0) {
                                    odf.setUniqueId(maxUniqueId++);
                                }
                                odf.setDcDetailId(odh.getOpdDetailId());
                                if (odf.getDrFeeId() == null) {
                                    odf.setDrFeeId(odh.getOpdDetailId() + "-" + odf.getUniqueId().toString());
                                }
                                dao.save(odf);
                            }
                        }
                    }

                    dao.save(odh);
                }
                dao.save(currVou);
                log.error("dc voucher save after save : " + currVou.getOpdInvId());
                //dao.commit();
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }

                updateVouTotal(currVou.getOpdInvId());

                String desp = "-";
                if (currVou.getPatient() != null) {
                    desp = currVou.getPatient().getRegNo() + "-" + currVou.getPatient().getPatientName();
                }
                uploadToAccount(currVou.getOpdInvId(), currVou.isDeleted(),
                        currVou.getVouBalance(), currVou.getDiscountA(),
                        currVou.getPaid(), currVou.getTaxA(), desp);

                if (currVou.getDcStatus() != null) {
                    log.error("dc voucher save status change : " + currVou.getOpdInvId() + " : " + currVou.getDcStatus());
                    Patient pt = currVou.getPatient();
                    log.error("DC Trace : " + pt.getRegNo() + ";" + pt.getAdmissionNo());
                    String admissionNo = pt.getAdmissionNo();
                    AdmissionKey key = new AdmissionKey(pt, currVou.getAdmissionNo());

                    Ams ams = (Ams) dao.find(Ams.class, key);
                    if (ams != null) {
                        ams.setDcStatus(currVou.getDcStatus());
                        Diagnosis dg = (Diagnosis) cboDiagnosis.getSelectedItem();
                        if (dg != null) {
                            ams.setDiagnosis(dg.getId());
                        }
                        AgeRange ar = (AgeRange) cboAgeRange.getSelectedItem();
                        if (ar != null) {
                            ams.setAgeRangeId(ar.getId());
                        }

                        ams.setDcDateTime(currVou.getCreatedDate());
                        dao.save(ams);
                    }
                    log.error("dc voucher save after admission status change : " + currVou.getOpdInvId());
                    if (pt.getAdmissionNo() != null) {
                        if (pt.getAdmissionNo().equals(currVou.getAdmissionNo())) {
                            pt.setAdmissionNo(null);
                        }
                    }
                    dao.save(pt);
                    log.error("dc voucher save after admission no set to null : " + currVou.getOpdInvId() + " : " + currVou.getAdmissionNo());
                    if (admissionNo != null) {
                        if (!admissionNo.isEmpty()) {
                            List listPT = dao.findAllHSQL("select o from Patient o where o.admissionNo = '" + admissionNo + "'");
                            if (listPT != null) {
                                if (!listPT.isEmpty()) {
                                    for (int i = 0; i < listPT.size(); i++) {
                                        Patient tmp = (Patient) listPT.get(i);
                                        tmp.setAdmissionNo(null);
                                        dao.save(tmp);
                                    }
                                }
                            }
                        }
                    }
                }
                newForm();
            } catch (Exception ex) {
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " : " + currVou.getOpdInvId() + " - " + ex.toString());
                JOptionPane.showMessageDialog(Util1.getParent(), "DC voucher save Error.\n" + ex.getMessage(),
                        "Save DC Voucher", JOptionPane.ERROR_MESSAGE);
            } finally {
                dao.close();
            }
            log.error("dc voucher save end ================== ");
        }
        deleteLinkTemp();
    }

    @Override
    public void newForm() {
        currVou = new DCHis();
        canEdit = true;
        txtDoctorName.setText(null);
        txtDoctorNo.setText(null);
        txtPatientName.setText(null);
        txtPatientNo.setText(null);
        txtRemark.setText(null);
        txtVouNo.setText(null);
        txtPkgName.setText(null);
        txtPkgPrice.setValue(null);
        tableModel.clear();
        tableModel.setVouStatus("NEW");
        txtPatientNo.setEditable(true);
        txtPatientName.setEditable(true);
        lblStatus.setText("NEW");
        txtPatientNo.requestFocusInWindow();
        txtAdmissionNo.setText(null);
        txtBedNo.setText(null);
        tableModel.setCanEdit(canEdit);
        assignDefaultValue();
        txtBillTotal.setText(null);
        tblPatientBillTableModel.setListPBP(new ArrayList());
        tblAmountLinkTableModel.clear();
        txtPatientNo.requestFocus();
        butAdmit.setEnabled(false);
        butPkgRemove.setEnabled(false);
        butPkgEdit.setEnabled(false);
        butCheckBill.setEnabled(false);
        txtLinkTotal.setValue(null);
        applySecurityPolicy();
    }

    @Override
    public void history() {
        DCVouSearchDialog dialog = new DCVouSearchDialog(this, "ENTRY");
    }

    @Override
    public void delete() {
        Date vouSaleDate = DateUtil.toDate(txtDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (lblStatus.getText().equals("EDIT")) {
            if (!Util1.hashPrivilege("DCVoucherDelete")) {
                JOptionPane.showMessageDialog(Util1.getParent(), "You have no permission to delete voucher.",
                        "DC voucher delete", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        /*if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot delete edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }*/
        if (tableModel.isPayAlready()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Doctor payment already made. You cannot delete this voucher.",
                    "Doctor payment", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (Util1.getNullTo(currVou.isDeleted())) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Voucher already deleted.",
                    "DC voucher delete", JOptionPane.ERROR_MESSAGE);
        } else {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "DC voucher delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                log.error("dc delete start : " + currVou.getOpdInvId());
                try {
                    Date d = new Date();
                    String strVouTotal = NumberUtil.NZero(currVou.getVouTotal()).toString();
                    String strDiscA = NumberUtil.NZero(currVou.getDiscountA()).toString();
                    String strPaid = NumberUtil.NZero(currVou.getPaid()).toString();
                    String strBalance = NumberUtil.NZero(currVou.getVouBalance()).toString();
                    dao.execProc("bk_dc",
                            currVou.getOpdInvId(),
                            DateUtil.toDateTimeStrMYSQL(d),
                            Global.loginUser.getUserId(),
                            Global.machineId,
                            strVouTotal,
                            strDiscA,
                            strPaid,
                            strBalance);
                } catch (Exception ex) {
                    log.error("bk_dc : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
                } finally {
                    dao.close();
                }
                log.error("dc delete after bk_dc.");
                try {
                    currVou.setDeleted(true);
                    currVou.setIntgUpdStatus(null);
                    currVou.setListOPDDetailHis(tableModel.getListOPDDetailHis());

                    //dao.open();
                    //dao.beginTran();
                    if (currVou.getDcStatus() != null) {
                        String regNo = currVou.getPatient().getRegNo();
                        String admNo = currVou.getAdmissionNo();
                        String sql = "update admission set dc_status = null where ams_no = '" + admNo + "'";
                        String sql1 = "update patient_detail set admission_no='" + admNo + "' where reg_no='" + regNo + "'";
                        dao.execSql(sql, sql1);
                    }
                    //dao.save(currVou);
                    String vouNo = currVou.getOpdInvId();
                    dao.execSql("update dc_his set deleted = true, intg_upd_status = null where dc_inv_id = '" + vouNo + "'");
                    log.error("dc delete after voucher save.");
                    //dao.commit();
                    uploadToAccount(currVou.getOpdInvId(), currVou.isDeleted(),
                            currVou.getVouBalance(), currVou.getDiscountA(),
                            currVou.getPaid(), currVou.getTaxA(), "");
                    newForm();
                } catch (Exception ex) {
                    log.error("delete : " + ex.getStackTrace()[0].getLineNumber()
                            + " - " + currVou.getOpdInvId() + " - " + ex.toString());
                    JOptionPane.showMessageDialog(Util1.getParent(), "DC Voucher Delete Error.(" + ex.getMessage() + "')",
                            "Voucher Delete", JOptionPane.ERROR_MESSAGE);
                    dao.rollBack();
                } finally {
                    dao.close();
                }
                log.error("dc delete end. " + currVou.getOpdInvId() + " ==================");
            }
        }
    }

    @Override
    public void deleteCopy() {
        Date vouSaleDate = DateUtil.toDate(txtDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (lblStatus.getText().equals("EDIT")) {
            if (!Util1.hashPrivilege("DCVoucherDelete")) {
                JOptionPane.showMessageDialog(Util1.getParent(), "You have no permission to delete voucher.",
                        "DC voucher delete", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        /*if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot delete and copy edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }*/
        if (tableModel.isPayAlready()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Doctor payment already made. You cannot delete this voucher.",
                    "Doctor payment", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete and copy?",
                "OPD voucher delete", JOptionPane.YES_NO_OPTION);

        if (yes_no == 0) {
            currVou.setDeleted(true);

            try {
                Date d = new Date();
                String strVouTotal = NumberUtil.NZero(currVou.getVouTotal()).toString();
                String strDiscA = NumberUtil.NZero(currVou.getDiscountA()).toString();
                String strPaid = NumberUtil.NZero(currVou.getPaid()).toString();
                String strBalance = NumberUtil.NZero(currVou.getVouBalance()).toString();
                dao.execProc("bk_dc",
                        currVou.getOpdInvId(),
                        DateUtil.toDateTimeStrMYSQL(d),
                        Global.loginUser.getUserId(),
                        Global.machineId,
                        strVouTotal,
                        strDiscA,
                        strPaid,
                        strBalance);
            } catch (Exception ex) {
                log.error("bk_dc : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
            } finally {
                dao.close();
            }

            try {
                currVou.setListOPDDetailHis(tableModel.getListOPDDetailHis());
                dao.save(currVou);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
                uploadToAccount(currVou.getOpdInvId(), currVou.isDeleted(),
                        currVou.getVouBalance(), currVou.getDiscountA(),
                        currVou.getPaid(), currVou.getTaxA(), "");
                copyVoucher(currVou.getOpdInvId());
                genVouNo();
                applySecurityPolicy();
                tableModel.setVouStatus("NEW");
            } catch (Exception ex) {
                log.error("deleteCopy : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            } finally {
                dao.close();
            }
        }
    }

    private void copyVoucher(String invNo) {
        try {
            DCHis tmpVou = (DCHis) dao.find(DCHis.class,
                    invNo);

            currVou = new DCHis();
            BeanUtils.copyProperties(tmpVou, currVou);
            List<DCDetailHis> listDetail = new ArrayList();
            List<DCDetailHis> listDetailTmp = tmpVou.getListOPDDetailHis();

            for (DCDetailHis detail : listDetailTmp) {
                DCDetailHis tmpDetail = new DCDetailHis();
                BeanUtils.copyProperties(detail, tmpDetail);
                tmpDetail.setOpdDetailId(null);
                listDetail.add(tmpDetail);
            }
            currVou.setListOPDDetailHis(listDetail);
            currVou.setDeleted(false);

            if (tmpVou.getListOPDDetailHis().size() > 0) {
                //tableModel.clear();
                tableModel.setListOPDDetailHis(listDetail);
            }

            //txtVouNo.setText(tmpVou.getOpdInvId());
            lblStatus.setText("NEW");
            txtDate.setText(DateUtil.toDateStr(tmpVou.getInvDate()));
            cboCurrency.setSelectedItem(tmpVou.getCurrency());
            cboPaymentType.setSelectedItem(tmpVou.getPaymentType());
            txtRemark.setText(tmpVou.getRemark());
            cboDCStatus.setSelectedItem(tmpVou.getDcStatus());

            if (tmpVou.getPatient() != null) {
                txtPatientNo.setText(tmpVou.getPatient().getRegNo());
                txtPatientName.setText(tmpVou.getPatient().getPatientName());
            } else {
                txtPatientName.setText(tmpVou.getPatientName());
                txtPatientName.setEditable(true);
                txtPatientNo.setEditable(false);
            }

            if (tmpVou.getDoctor() != null) {
                txtDoctorNo.setText(tmpVou.getDoctor().getDoctorId());
                txtDoctorName.setText(tmpVou.getDoctor().getDoctorName());
            }

            txtVouTotal.setValue(tmpVou.getVouTotal());
            txtDiscP.setValue(tmpVou.getDiscountP());
            txtDiscA.setValue(tmpVou.getDiscountA());
            txtTaxP.setValue(tmpVou.getTaxP());
            txtTaxA.setValue(tmpVou.getTaxA());
            txtPaid.setValue(tmpVou.getPaid());
            txtVouBalance.setValue(tmpVou.getVouBalance());
        } catch (Exception ex) {
            log.error("copyVoucher : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    @Override
    public void print() {
        double linkTotal = 0;
        if (isValidEntry()) {
            log.error("dc vou print start : " + currVou.getOpdInvId());
            try {
                Date d = new Date();
                String strVouTotal = NumberUtil.NZero(currVou.getVouTotal()).toString();
                String strDiscA = NumberUtil.NZero(currVou.getDiscountA()).toString();
                String strPaid = NumberUtil.NZero(currVou.getPaid()).toString();
                String strBalance = NumberUtil.NZero(currVou.getVouBalance()).toString();
                dao.execProc("bk_dc",
                        currVou.getOpdInvId(),
                        DateUtil.toDateTimeStrMYSQL(d),
                        Global.loginUser.getUserId(),
                        Global.machineId,
                        strVouTotal,
                        strDiscA,
                        strPaid,
                        strBalance);
            } catch (Exception ex) {
                log.error("bk_dc : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
            } finally {
                dao.close();
            }
            log.error("dc vou print after bk_dc : " + currVou.getOpdInvId());
            try {
                if (lblStatus.getText().equals("NEW")) {
                    currVou.setCreatedBy(Global.loginUser);
                    currVou.setCreatedDate(new Date());
                } else {
                    currVou.setUpdatedBy(Global.loginUser);
                    currVou.setUpdatedDate(new Date());
                }

                Date vouSaleDate = DateUtil.toDate(txtDate.getText());
                Date lockDate = PharmacyUtil.getLockDate(dao);
                boolean isDataLock = false;
                if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                    isDataLock = true;
                }

                if (canEdit) {
                    if (!isDataLock) {
                        dao.open();
                        dao.beginTran();
                        String vouNo = currVou.getOpdInvId();
                        List<DCDetailHis> listDetail = currVou.getListOPDDetailHis();
                        for (DCDetailHis odh : listDetail) {
                            odh.setVouNo(vouNo);
                            if (odh.getOpdDetailId() == null) {
                                odh.setOpdDetailId(vouNo + "-" + odh.getUniqueId().toString());
                            }
                            if (odh.getListDCDF() != null) {
                                if (!odh.getListDCDF().isEmpty()) {
                                    List<DCDoctorFee> listDF = odh.getListDCDF();
                                    Integer maxUniqueId = NumberUtil.NZeroInt(listDF.get(listDF.size() - 1).getUniqueId());
                                    for (DCDoctorFee odf : listDF) {
                                        if (NumberUtil.NZeroInt(odf.getUniqueId()) == 0) {
                                            odf.setUniqueId(maxUniqueId++);
                                        }
                                        odf.setDcDetailId(odh.getOpdDetailId());
                                        odf.setDrFeeId(odh.getOpdDetailId() + "-" + odf.getUniqueId().toString());

                                        if (odf.getDrFeeId() == null) {
                                            odf.setDrFeeId(odh.getOpdDetailId() + "-" + odf.getUniqueId().toString());
                                        }
                                        dao.save1(odf);
                                    }
                                }
                            }

                            dao.save1(odh);
                        }
                        dao.save1(currVou);
                        dao.commit();

                        linkTotal = tblAmountLinkTableModel.getTotalAmount() + currVou.getVouTotal() - currVou.getDiscountA();

                        if (lblStatus.getText().equals("NEW")) {
                            vouEngine.updateVouNo();
                        }
                        log.error("dc vou print after new vou generate : " + currVou.getOpdInvId());
                        deleteDetail();
                        updateVouTotal(currVou.getOpdInvId());

                        String desp = "-";
                        if (currVou.getPatient() != null) {
                            desp = currVou.getPatient().getRegNo() + "-" + currVou.getPatient().getPatientName();
                        }
                        uploadToAccount(currVou.getOpdInvId(), currVou.isDeleted(),
                                currVou.getVouBalance(), currVou.getDiscountA(),
                                currVou.getPaid(), currVou.getTaxA(), desp);
                        log.error("dc vou print after uploadToAccount : " + currVou.getOpdInvId());
                    }

                }
                lblStatus.setText("EDIT");

                if (!isDataLock) {
                    if (currVou.getDcStatus() != null) {
                        Patient pt = currVou.getPatient();
                        AdmissionKey key = new AdmissionKey(pt, pt.getAdmissionNo());
                        try {
                            Ams ams = (Ams) dao.find(Ams.class,
                                    key);
                            if (ams != null) {
                                ams.setDcStatus(currVou.getDcStatus());
                                Diagnosis dg = (Diagnosis) cboDiagnosis.getSelectedItem();
                                if (dg != null) {
                                    ams.setDiagnosis(dg.getId());
                                }
                                AgeRange ar = (AgeRange) cboAgeRange.getSelectedItem();
                                if (ar != null) {
                                    ams.setAgeRangeId(ar.getId());
                                }
                                ams.setDcDateTime(currVou.getCreatedDate());
                                dao.save(ams);
                            }
                        } catch (Exception ex) {
                            log.error("Admission update print : " + currVou.getOpdInvId() + " : " + ex.toString());
                        } finally {
                            dao.close();
                        }

                        try {
                            if (pt.getAdmissionNo() != null) {
                                if (pt.getAdmissionNo().equals(currVou.getAdmissionNo())) {
                                    pt.setAdmissionNo(null);
                                }
                            }
                            dao.save(pt);
                        } catch (Exception ex) {
                            log.error("Admission remove error : " + currVou.getOpdInvId() + " : " + ex.toString());
                        } finally {
                            dao.close();
                        }

                        String strSql = " update building_structure bs inner join admission a on bs.id = "
                                + " a.building_structure_id set bs.reg_no = null "
                                + " where a.ams_no ='" + currVou.getAdmissionNo() + "'";
                        dao.execSql(strSql);
                    }
                }
            } catch (Exception ex) {
                dao.rollBack();
                log.error("print : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " : " + ex);
                JOptionPane.showMessageDialog(Util1.getParent(), "Error cannot print.",
                        "DC print", JOptionPane.ERROR_MESSAGE);
                return;
            } finally {
                dao.close();
            }
        }

        backupPackage(currVou.getOpdInvId(), "DC-Print", "DC-PRINT");
        log.error("dc vou print after backupPackage : " + currVou.getOpdInvId());

        if (currVou.getDcStatus() != null) {
            if (Util1.getPropValue("system.dc.pt.balancecheck").equals("Y")) {
                double ptBalance = getPatientBalance(txtPatientNo.getText().trim());
                if (Math.abs(ptBalance) != 0) {
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Patient balance is not zero (" + ptBalance + ").\nPlease Check.",
                            "Patient Balance Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        log.error("dc vou print finished database update : " + currVou.getOpdInvId());

        //Properties prop = ReportUtil.loadReportPathProperties();
        String reportName = Util1.getPropValue("report.file.dc");
        if (chkDetails.isSelected()) {
            reportName = Util1.getPropValue("system.dc.report.detail");
            reportName = "clinic/" + reportName;
        } else if (chkA5.isSelected()) {
            reportName = "clinic/DCDetailA5";
        } else if (chkSummary.isSelected()) {
            reportName = "clinic/" + Util1.getPropValue("system.dc.report.summary");
        }

        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + reportName;

        //For package report
        Long pkgId = currVou.getPkgId();
        if (pkgId != null) {
            reportName = Util1.getPropValue("system.dc.report.package");
            reportPath = Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path")
                    + "clinic/" + reportName;
        }
        //================================

        String imagePath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path") + "/img/logo.jpg";
        String printerName = Util1.getPropValue("report.vou.printer");
        Map<String, Object> params = new HashMap();
        String printMode = Util1.getPropValue("report.vou.printer.mode");

        //params.put("imagePath", imagePath);
        String compName = Util1.getPropValue("report.company.name");
        String phoneNo = Util1.getPropValue("report.phone");
        String address = Util1.getPropValue("report.address");
        params.put("compName", compName);
        params.put("phoneNo", phoneNo);
        params.put("comAddress", address);
        params.put("category", Util1.getPropValue("report.company.cat"));
        params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));
        params.put("user_id", Global.machineId);
        params.put("dc_user_id", "DC-" + Global.machineId);
        params.put("IMAGE_PATH", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));
        params.put("REPORT_CONNECTION", dao.getConnection());
        params.put("link_amt_status", Util1.getPropValue("system.dc.link.amt"));
        params.put("link_amt", linkTotal);

        Diagnosis digs = (Diagnosis) cboDiagnosis.getSelectedItem();
        if (digs == null) {
            params.put("diagnosis", null);
        } else {
            params.put("diagnosis", digs.getIntName());
        }

        if (!chkDetails.isSelected() && !chkA5.isSelected() && !chkSummary.isSelected()) {
            params.put("invoiceNo", currVou.getOpdInvId());
            if (currVou.getPatient() != null) {
                params.put("customerName", currVou.getPatient().getPatientName());
                Date regDate = currVou.getPatient().getRegDate();
                Date trgDate = DateUtil.toDate("08/10/2018", "dd/MM/yyyy");
                if (regDate.before(trgDate)) {
                    Integer year = regDate.getYear();
                    params.put("reg_no", currVou.getPatient().getRegNo() + "/" + year);
                } else {
                    params.put("reg_no", currVou.getPatient().getRegNo());
                }
                Ams ams;
                AdmissionKey key;
                if (currVou.getPatient().getAdmissionNo() != null) {
                    key = new AdmissionKey(currVou.getPatient(), currVou.getPatient().getAdmissionNo());
                } else {
                    key = new AdmissionKey(currVou.getPatient(), txtAdmissionNo.getText().trim());
                }
                try {
                    ams = (Ams) dao.find(Ams.class,
                            key);
                    if (ams.getBuildingStructure() != null) {
                        params.put("bed_no", ams.getBuildingStructure().getDescription());
                    } else {
                        params.put("bed_no", "-");
                    }
                } catch (Exception ex) {
                    log.error(regDate);
                } finally {
                    dao.close();
                }

            } else {
                if (currVou.getPatientName() != null) {
                    params.put("customerName", currVou.getPatientName());
                } else {
                    params.put("customerName", "");
                }
                params.put("reg_no", "");
            }
            if (currVou.getDoctor() != null) {
                params.put("doctor", currVou.getDoctor().getDoctorName());
            } else {
                params.put("doctor", "");
            }
            params.put("saleDate", currVou.getInvDate());
            params.put("grandTotal", currVou.getVouTotal());
            params.put("paid", currVou.getPaid());
            params.put("discount", currVou.getDiscountA());
            params.put("tax", currVou.getTaxA());
            params.put("balance", currVou.getVouBalance());
            params.put("user", Global.loginUser.getUserShortName());

            params.put("remark", txtRemark.getText());

            if (lblStatus.getText().equals("NEW")) {
                params.put("vou_status", " ");
            } else {
                params.put("vou_status", lblStatus.getText());
            }

            if (printMode.equals("View")) {
                //ReportUtil.viewReport(reportPath, params, tableModel.getListOPDDetailHis());
                ReportUtil.viewReport(reportPath, params, dao.getConnection());
            } else {
                JasperPrint jp = ReportUtil.getReport(reportPath, params, dao.getConnection());
                ReportUtil.printJasper(jp, printerName);
            }

            newForm();
        } else {
            Patient pt = currVou.getPatient();
            params.put("reg_no", pt.getRegNo());

            if (pt != null && !txtAdmissionNo.getText().trim().isEmpty()) {
                try {
                    Ams ams;
                    AdmissionKey key;
                    if (!txtAdmissionNo.getText().trim().isEmpty()) {
                        key = new AdmissionKey(pt, txtAdmissionNo.getText().trim());
                    } else {
                        key = new AdmissionKey(pt, pt.getAdmissionNo());
                    }
                    ams = (Ams) dao.find(Ams.class,
                            key);
                    if (ams.getBuildingStructure() != null) {
                        params.put("bed_no", ams.getBuildingStructure().getDescription());
                    } else {
                        params.put("bed_no", "-");
                    }

                    if (txtAdmissionNo.getText().isEmpty()) {
                        params.put("adm_no", "-");
                    } else {
                        params.put("adm_no", txtAdmissionNo.getText());
                    }
                    //params.put("adm_no", key.getAmsNo());
                    if (currVou.getInvDate() == null) {
                        params.put("tran_date", DateUtil.toDateTimeStrMYSQL(DateUtil.toDateTime(txtDate.getText())));
                    } else {
                        params.put("tran_date", DateUtil.toDateTimeStrMYSQL(currVou.getInvDate()));
                    }
                    params.put("adm_date", DateUtil.toDateTimeStrMYSQL(ams.getAmsDate()));
                    params.put("pt_name", pt.getPatientName());
                    if (ams.getDoctor() == null) {
                        params.put("dr_name", "");
                    } else {
                        params.put("dr_name", ams.getDoctor().getDoctorName());
                    }
                    String toDate = DateUtil.toDateTimeStrMYSQL(currVou.getInvDate());
                    if (toDate == null) {
                        toDate = DateUtil.toDateTimeStrMYSQL(DateUtil.toDateTime(txtDate.getText()));
                    }
                    String period = DateUtil.toDateStr(ams.getAmsDate(), "dd/MM/yyyy hh:mm aa") + " To "
                            + DateUtil.toDateStr(DateUtil.toDateTime(txtDate.getText()), "dd/MM/yyyy hh:mm aa");
                    params.put("period", period);
                    if (ams.getBuildingStructure() != null) {
                        params.put("bed_no", ams.getBuildingStructure().getDescription());
                    } else {
                        params.put("bed_no", "-");
                    }

                    if (ams.getTownship() != null) {
                        params.put("address", ams.getTownship().getTownshipName());
                    } else {
                        params.put("address", "-");
                    }

                    if (currVou.getDcStatus() == null) {
                        params.put("dc_status", "");
                    } else {
                        params.put("dc_status", currVou.getDcStatus().getStatusDesp());
                    }
                    /*if (ams.getDob() != null) {
                    params.put("age", DateUtil.getAge(DateUtil.toDateStr(ams.getDob())));*/

 /*if (ams.getAge() != null) {
                    params.put("age", ams.getAge() + "Years");
                } else {
                    params.put("age", pt.getAge() + "Years");
                }*/
                    if (pt.getDob() != null) {
                        String dob = DateUtil.toDateStr(pt.getDob(), "dd/MM/yyyy");
                        String strAge = DateUtil.getAge(dob);
                        params.put("age", strAge);
                    } else {
                        params.put("age", "Years");
                    }

                    if (ams.getSex() == null) {
                        params.put("sex", "");
                    } else {
                        params.put("sex", ams.getSex().getDescription());
                    }

                    assignExtraParam(params, txtAdmissionNo.getText(), pt.getRegNo(),
                            DateUtil.toDateStr(ams.getAmsDate(), "yyyy-MM-dd"),
                            DateUtil.toDateStr(txtDate.getText(), "yyyy-MM-dd"));

                    dao.close();
                    ReportUtil.viewReport(reportPath, params, dao.getConnection());
                    dao.commit();
                } catch (Exception ex) {
                    log.error("print : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }
        }
        //deleteLinkTemp();
    }

    private void assignExtraParam(Map<String, Object> params, String admNo,
            String regNo, String admDate, String tranDate) {
        try {
            double ttlPaid = 0.0;
            double ttlRefund = 0.0;
            double ttlDiscount = 0.0;
            double ttlDeposite = 0.0;
            String paidSql = "#paid\n"
                    + "select sum(paid) ttl_amt\n"
                    + "from (\n"
                    + "select sum(ifnull(paid_amount,0)) paid\n"
                    + "from sale_his\n"
                    + "where deleted = false and admission_no = '" + admNo + "'\n"
                    + "and date(sale_date) between '" + admDate + "' and '" + tranDate + "'\n"
                    + "	union all\n"
                    + "select sum(ifnull(paid,0)) amount\n"
                    + "from opd_his\n"
                    + "where deleted = false and admission_no = '" + admNo + "'\n"
                    + "and date(opd_date) between '" + admDate + "' and '" + tranDate + "'\n"
                    + "	union all\n"
                    + "select sum(ifnull(amount,0))\n"
                    + "from v_ot\n"
                    + "where service_id in (select sys_prop_value from sys_prop\n"
                    + "where sys_prop_desp in ('system.ot.paid.id')) and deleted = false and admission_no = '" + admNo + "'\n"
                    + "and date(ot_date) between '" + admDate + "' and '" + tranDate + "'\n"
                    + "	union all\n"
                    + "select sum(ifnull(amount,0))\n"
                    + "from v_dc\n"
                    + "where service_id in (select sys_prop_value from sys_prop\n"
                    + "where sys_prop_desp in ('system.dc.paid.id')) and deleted = false  and admission_no = '" + admNo + "'\n"
                    + "and date(dc_date) between '" + admDate + "' and '" + tranDate + "'\n"
                    + ")a";
            String depositSql = "#deposite\n"
                    + "select sum(deposite_amt) ttl_amt\n"
                    + "from(\n"
                    + "select sum(amount) deposite_amt\n"
                    + "FROM v_dc\n"
                    + "where service_id in (select sys_prop_value from sys_prop\n"
                    + "where sys_prop_desp in ('system.dc.deposite.id')) and deleted = false  and admission_no = '" + admNo + "'\n"
                    + "and date(dc_date) between '" + admDate + "' and '" + tranDate + "'\n"
                    + "	union all\n"
                    + "select sum(amount) deposite_amt\n"
                    + "FROM v_ot\n"
                    + "where service_id in (select sys_prop_value from sys_prop\n"
                    + "where sys_prop_desp in ('system.ot.deposite.id')) and deleted = false  and admission_no = '" + admNo + "'\n"
                    + "and date(ot_date) between '" + admDate + "' and '" + tranDate + "'\n"
                    + ")a";
            String disSql = "#discount\n"
                    + "select sum(discount) ttl_amt\n"
                    + "from (\n"
                    + "select sum(ifnull(discount,0)) discount\n"
                    + "FROM sale_his\n"
                    + "where deleted = false and admission_no = '" + admNo + "'\n"
                    + "and date(sale_date) between '" + admDate + "' and '" + tranDate + "'\n"
                    + "	union all\n"
                    + "select sum(ifnull(disc_a,0)) amount\n"
                    + "FROM opd_his\n"
                    + "where deleted = false and admission_no = '" + admNo + "'\n"
                    + "and date(opd_date) between '" + admDate + "' and '" + tranDate + "'\n"
                    + "	union all\n"
                    + "select sum(ifnull(disc_a,0))\n"
                    + "FROM v_ot\n"
                    + "where deleted = false  and admission_no = '" + admNo + "'\n"
                    + "and date(ot_date) between '" + admDate + "' and '" + tranDate + "'\n"
                    + "	union all\n"
                    + "select sum(ifnull(disc_a,0))\n"
                    + "from dc_his\n"
                    + "where deleted = false  and admission_no = '" + admNo + "'\n"
                    + "and date(dc_date) between '" + admDate + "' and '" + tranDate + "'\n"
                    + ")a";
            String refundSql = "#refund\n"
                    + "select sum(refund_amt)*-1 ttl_amt\n"
                    + "from(\n"
                    + "select sum(amount) refund_amt\n"
                    + "FROM v_dc\n"
                    + "where service_id in (select sys_prop_value from sys_prop\n"
                    + "where sys_prop_desp in ('system.dc.refund.id')) and deleted = false  and admission_no = '" + admNo + "'\n"
                    + "and date(dc_date) between '" + admDate + "' and '" + tranDate + "'\n"
                    + "	union all\n"
                    + "select sum(amount) refund_amt\n"
                    + "FROM v_ot\n"
                    + "where service_id in (select sys_prop_value from sys_prop\n"
                    + "where sys_prop_desp in ('system.ot.refund.id')) and deleted = false  and admission_no = '" + admNo + "'\n"
                    + "and date(ot_date) between '" + admDate + "' and '" + tranDate + "'\n"
                    + ")a";
            ResultSet rs = dao.execSQL(paidSql);
            if (rs.next()) {
                ttlPaid = rs.getDouble("ttl_amt");
            }
            rs = dao.execSQL(depositSql);
            if (rs.next()) {
                ttlDeposite = rs.getDouble("ttl_amt");
            }
            rs = dao.execSQL(disSql);
            if (rs.next()) {
                ttlDiscount = rs.getDouble("ttl_amt");
            }
            rs = dao.execSQL(refundSql);
            if (rs.next()) {
                ttlRefund = rs.getDouble("ttl_amt");
            }
            params.put("total_discount", ttlDiscount * -1);
            params.put("total_paid", ttlPaid);
            params.put("total_deposite", ttlDeposite);
            params.put("total_refund", ttlRefund);

        } catch (SQLException ex) {
            log.error("assignExtraParam : " + ex.getMessage());
        }
    }

    //KeyPropagate
    @Override
    public void keyEvent(KeyEvent e) {

    }

    //SelectionObserver
    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "DoctorSearch":
                try {
                Doctor doctor = (Doctor) selectObj;
                doctor = (Doctor) dao.find(Doctor.class,
                        doctor.getDoctorId());
                currVou.setDoctor(doctor);
                txtDoctorNo.setText(doctor.getDoctorId());
                txtDoctorName.setText((doctor.getDoctorName()));
                txtVouTotal.setValue(tableModel.getTotal());
                calcBalance();
                tblService.requestFocus();
            } catch (Exception ex) {
                log.error("selected DoctorSearch : " + ex.getMessage());
            } finally {
                dao.close();
            }
            break;
            case "PatientSearch":
                try {
                Patient patient = (Patient) selectObj;
                currVou.setPatient(patient);
                currVou.setAdmissionNo(patient.getAdmissionNo());
                txtPatientNo.setText(patient.getRegNo());
                txtPatientName.setText(patient.getPatientName());
                txtPatientName.setEditable(false);
                if (patient.getDoctor() != null) {
                    selected("DoctorSearch", patient.getDoctor());
                }
                txtDoctorNo.requestFocus();
                txtAdmissionNo.setText(patient.getAdmissionNo());
                if (Util1.getNullTo(patient.getAdmissionNo(), "").trim().isEmpty()) {
                    butAdmit.setEnabled(true);
                    cboPaymentType.setSelectedItem(ptCash);
                } else {
                    butAdmit.setEnabled(false);
                    AdmissionKey key = new AdmissionKey();
                    key.setRegister(patient);
                    key.setAmsNo(patient.getAdmissionNo());
                    Ams ams = (Ams) dao.find(Ams.class,
                            key);
                    if (ams != null) {
                        lblAdmissionDate.setText(DateUtil.toDateTimeStr(ams.getAmsDate(),
                                "dd/MM/yyyy HH:mm:ss"));
                        if (ams.getBuildingStructure() != null) {
                            txtBedNo.setText(ams.getBuildingStructure().getDescription());
                            if (ams.getBuildingStructure().getFacilityType() != null) {
                                tableModel.setRoomFee(ams.getBuildingStructure().getFacilityType().getPrice());
                            } else {
                                tableModel.setRoomFee(null);
                            }
                        }
                    }
                    if (Util1.getPropValue("system.admission.paytype").equals("CREDIT")) {
                        cboPaymentType.setSelectedItem(ptCredit);
                    } else {
                        cboPaymentType.setSelectedItem(ptCash);
                    }
                }
                getPatientBill(patient.getRegNo());
                if (Util1.getPropValue("system.dc.link.amt").equals("Y")) {
                    linkAmount();
                }
            } catch (Exception ex) {
                log.error("selected : PatientSearch : " + ex.getMessage());
            }
            break;
            case "DCVouList":
                try {
                newForm();
                VoucherSearch vs = (VoucherSearch) selectObj;
                String vouId = vs.getInvNo();
                try {
                    currVou = (DCHis) dao.find(DCHis.class,
                            vouId);
                    List<DCDetailHis> listDetail = dao.findAllHSQL(
                            "select o from DCDetailHis o where o.vouNo = '" + vouId + "' order by o.uniqueId");
                    currVou.setListOPDDetailHis(listDetail);
                } catch (Exception ex) {
                    log.error("DCVouList : " + ex.toString());
                } finally {
                    dao.close();
                }
                tableModel.clear();
                tableModel.setListOPDDetailHis(currVou.getListOPDDetailHis());
                txtVouNo.setText(currVou.getOpdInvId());
                txtDate.setText(DateUtil.toDateStr(currVou.getInvDate()));
                cboCurrency.setSelectedItem(currVou.getCurrency());
                cboPaymentType.setSelectedItem(currVou.getPaymentType());
                txtRemark.setText(currVou.getRemark());
                cboDCStatus.setSelectedItem(currVou.getDcStatus());
                cboDiagnosis.setSelectedItem(currVou.getDiagnosis());
                txtAdmissionNo.setText(currVou.getAdmissionNo());
                if (txtAdmissionNo.getText() != null) {
                    if (!txtAdmissionNo.getText().trim().isEmpty()) {
                        AdmissionKey key = new AdmissionKey();
                        key.setRegister(currVou.getPatient());
                        key.setAmsNo(txtAdmissionNo.getText().trim());
                        Ams ams = (Ams) dao.find(Ams.class,
                                key);
                        if (ams != null) {
                            lblAdmissionDate.setText(DateUtil.toDateTimeStr(ams.getAmsDate(),
                                    "dd/MM/yyyy HH:mm:ss"));
                            if (ams.getBuildingStructure() != null) {
                                txtBedNo.setText(ams.getBuildingStructure().getDescription());
                            }
                        }
                    }
                }
                cboAgeRange.setSelectedItem(currVou.getAgeRange());
                txtPkgName.setText(currVou.getPkgName());
                txtPkgPrice.setValue(currVou.getPkgPrice());

                if (currVou.isDeleted()) {
                    lblStatus.setText("DELETED");
                } else {
                    lblStatus.setText("EDIT");
                }

                if (currVou.getPatient() != null) {
                    txtPatientNo.setText(currVou.getPatient().getRegNo());
                    txtPatientName.setText(currVou.getPatient().getPatientName());
                } else {
                    txtPatientName.setText(currVou.getPatientName());
                    txtPatientName.setEditable(true);
                    txtPatientNo.setEditable(false);
                }

                if (currVou.getDoctor() != null) {
                    txtDoctorNo.setText(currVou.getDoctor().getDoctorId());
                    txtDoctorName.setText(currVou.getDoctor().getDoctorName());
                }

                txtVouTotal.setValue(currVou.getVouTotal());
                txtDiscP.setValue(currVou.getDiscountP());
                txtDiscA.setValue(currVou.getDiscountA());
                txtTaxP.setValue(currVou.getTaxP());
                txtTaxA.setValue(currVou.getTaxA());
                txtPaid.setValue(currVou.getPaid());
                txtVouBalance.setValue(currVou.getVouBalance());
                setEditStatus(currVou.getOpdInvId());
                applySecurityPolicy();
                tableModel.setCanEdit(canEdit);
                tableModel.setVouStatus("EDIT");
                tableModel.setVouDate(txtDate.getText());
                //For Package
                if (currVou.getPkgId() != null) {
                    calcPackageExtraFees(currVou);
                    calcPackageGainLost("EDIT");
                    butPkgRemove.setEnabled(true);
                    butPkgEdit.setEnabled(true);
                    butCheckBill.setEnabled(true);
                }
                if (Util1.getPropValue("system.dc.link.amt").equals("Y")) {
                    linkAmount();
                }
            } catch (Exception ex) {
                log.error("DCVouList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
            break;
            case "AdmissionSearch":
                Patient ams = ((Ams) selectObj).getKey().getRegister();
                if (ams.getAdmissionNo() != null) {
                    selected("PatientSearch", ams);
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(), "This patient is no longer admitted.",
                            "Not Admitted Patient", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case "PackageSelect":
                if (selectObj != null) {
                    ClinicPackage cp = (ClinicPackage) selectObj;
                    currVou.setPkgId(cp.getId());
                    currVou.setPkgName(cp.getPackageName());
                    currVou.setPkgPrice(cp.getPrice());
                    txtPkgName.setText(currVou.getPkgName());
                    txtPkgPrice.setValue(currVou.getPkgPrice());
                    butPkgRemove.setEnabled(true);
                    //Save package his
                    savePackage(txtVouNo.getText().trim(), currVou.getPkgId());
                    calcPackageExtraFees(currVou);
                    calcPackageGainLost("-");
                    butPkgEdit.setEnabled(true);
                    butCheckBill.setEnabled(true);
                } else {
                    butPkgEdit.setEnabled(false);
                    butCheckBill.setEnabled(false);
                }
                break;
        }

        tblService.requestFocusInWindow();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
            if (!focusCtrlName.equals("-")) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtPatientNo")) {
                    txtDoctorNo.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtDoctorNo")) {
                    cboDCStatus.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("cboDCStatus")) {
                    if (!cboDCStatus.isPopupVisible()) {
                        txtRemark.requestFocus();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtRemark")) {
                    tblService.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtCusId")) {
                    txtRemark.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtRemark")) {
                    cboDCStatus.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("cboDCStatus")) {
                    if (!cboDCStatus.isPopupVisible()) {
                        txtDoctorNo.requestFocus();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtDoctorNo")) {
                    txtPatientNo.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("tblService")) {
                    int selRow = tblService.getSelectedRow();
                    if (selRow == -1 || selRow == 0) {
                        cboDCStatus.requestFocus();
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void initForFocus() {
        txtPatientNo.addKeyListener(this);
        txtDoctorNo.addKeyListener(this);
        cboDCStatus.addKeyListener(this);
        cboDCStatus.getEditor().getEditorComponent().addKeyListener(this);
        cboDCStatus.getEditor().getEditorComponent().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cboDCStatus.requestFocus();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        tblService.addKeyListener(this);
        txtRemark.addKeyListener(this);
    }

    private void genVouNo() {
        String vouNo = vouEngine.getVouNo();
        txtVouNo.setText(vouNo);

        try {
            List<DCHis> listOPDH = dao.findAllHSQL(
                    "select o from DCHis o where o.opdInvId = '" + vouNo + "'");
            if (listOPDH != null) {
                if (!listOPDH.isEmpty()) {
                    vouEngine.updateVouNo();
                    vouNo = vouEngine.getVouNo();
                    txtVouNo.setText(vouNo);
                    listOPDH = null;
                    listOPDH = dao.findAllHSQL(
                            "select o from DCHis o where o.opdInvId = '" + vouNo + "'");
                    if (listOPDH != null) {
                        if (!listOPDH.isEmpty()) {
                            log.error("Duplicate DC voucher error : " + txtVouNo.getText() + " @ "
                                    + txtDate.getText());
                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Duplicate DC vou no. Exit the program and try again.",
                                    "DC Vou No", JOptionPane.ERROR_MESSAGE);
                            System.exit(-1);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("genVouNo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void getDoctor() {
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (txtDoctorNo.getText() != null && !txtDoctorNo.getText().isEmpty()) {
            try {

                Doctor dr = null;
                dao.open();
                //dr = (Doctor) dao.find(Doctor.class, txtDoctorNo.getText());
                List<Doctor> listDr = dao.findAllHSQL("select o from Doctor o where o.doctorId = '"
                        + txtDoctorNo.getText().trim() + "' and o.active = true");
                if (listDr != null) {
                    if (!listDr.isEmpty()) {
                        dr = listDr.get(0);
                    }
                }
                dao.close();

                if (dr == null) {
                    txtDoctorNo.setText(null);
                    txtDoctorName.setText(null);
                    currVou.setDoctor(null);
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid doctor code.",
                            "Doctor Code", JOptionPane.ERROR_MESSAGE);

                } else {
                    selected("DoctorSearch", dr);
                }
            } catch (Exception ex) {
                log.error("getDoctor : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtDoctorNo.setText(null);
            txtDoctorName.setText(null);
            currVou.setDoctor(null);
        }
    }

    private void calcBalance() {
        if (cboPaymentType.getSelectedItem() != null) {
            PaymentType pt = (PaymentType) cboPaymentType.getSelectedItem();
            double discount = NumberUtil.NZero(txtDiscA.getValue());
            double tax = NumberUtil.NZero(txtTaxA.getValue());
            double vouTotal = NumberUtil.NZero(txtVouTotal.getValue());

            if (pt.getPaymentTypeId() == 1) {
                txtPaid.setValue((vouTotal + tax) - discount);
            }
            double paid = NumberUtil.NZero(txtPaid.getValue());
            txtVouBalance.setValue((vouTotal + tax) - (discount + paid));
        } else {
            //Payment type is not selected
            double discount = NumberUtil.NZero(txtDiscA.getValue());
            double tax = NumberUtil.NZero(txtTaxA.getValue());
            double vouTotal = NumberUtil.NZero(txtVouTotal.getValue());
            txtPaid.setValue((vouTotal + tax) - discount);
            double paid = NumberUtil.NZero(txtPaid.getValue());
            txtVouBalance.setValue((vouTotal + tax) - (discount + paid));
        }

        /*if (cboPaymentType.getSelectedItem() != null) {
            PaymentType pt = (PaymentType) cboPaymentType.getSelectedItem();
            double discount = NumberUtil.NZero(txtDiscA.getValue());
            double tax = NumberUtil.NZero(txtTaxA.getValue());
            double vouTotal = NumberUtil.NZero(txtVouTotal.getValue());

            
            double paid = NumberUtil.NZero(txtPaid.getValue());
            txtVouBalance.setValue((vouTotal + tax) - (discount + paid));
        } else {
            //Payment type is not selected
        }*/
    }

    private void getPatient() {
        deleteLinkTemp();
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (txtPatientNo.getText() != null && !txtPatientNo.getText().isEmpty()) {
            try {
                Patient pt;

                dao.open();
                pt = (Patient) dao.find(Patient.class,
                        txtPatientNo.getText());
                dao.close();

                if (pt == null) {
                    txtPatientNo.setText(null);
                    txtPatientName.setText(null);
                    currVou.setPatient(null);

                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid patient code.",
                            "Patient Code", JOptionPane.ERROR_MESSAGE);
                } else {
                    selected("PatientSearch", pt);
                    tblService.requestFocusInWindow();
                }
            } catch (Exception ex) {
                log.error("getPatient : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtPatientNo.setText(null);
            txtPatientName.setText(null);
            currVou.setPatient(null);
            txtPatientName.setEditable(true);
        }
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboPaymentType, dao.findAll("PaymentType"));
            BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));
            BindingUtil.BindCombo(cboDCStatus, dao.findAll("DCStatus"));
            BindingUtil.BindCombo(cboDiagnosis, dao.findAll("Diagnosis"));
            BindingUtil.BindCombo(cboAgeRange, dao.findAll("AgeRange"));

            new ComBoBoxAutoComplete(cboPaymentType, this);
            new ComBoBoxAutoComplete(cboCurrency, this);
            new ComBoBoxAutoComplete(cboDCStatus, this);
            new ComBoBoxAutoComplete(cboDiagnosis, this);
            new ComBoBoxAutoComplete(cboAgeRange, this);

            cboDCStatus.setSelectedItem(null);
            cboDiagnosis.setSelectedItem(null);
            cboAgeRange.setSelectedItem(null);

            cboBindStatus = true;
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        try {
            if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
                tblService.setCellSelectionEnabled(true);
            }
            tblService.setCellSelectionEnabled(true);
            tblService.getTableHeader().setFont(Global.lableFont);
            //Adjust column width
            tblService.getColumnModel().getColumn(0).setPreferredWidth(40);//Code
            tblService.getColumnModel().getColumn(1).setPreferredWidth(300);//Description
            tblService.getColumnModel().getColumn(2).setPreferredWidth(20);//Qty
            tblService.getColumnModel().getColumn(3).setPreferredWidth(60);//Fees
            tblService.getColumnModel().getColumn(4).setPreferredWidth(50);//Charge Type
            tblService.getColumnModel().getColumn(5).setPreferredWidth(80);//Amount

            //Change JTable cell editor
            tblService.getColumnModel().getColumn(0).setCellEditor(
                    new DCTableCellEditor(dao));
            tblService.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor());
            tblService.getColumnModel().getColumn(3).setCellEditor(new BestTableCellEditor());
            tblService.getTableHeader().setFont(Global.lableFont);
            JComboBox cboChargeType = new JComboBox();
            BindingUtil.BindCombo(cboChargeType, dao.findAll("ChargeType"));
            tblService.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cboChargeType));

            tblService.getModel().addTableModelListener((TableModelEvent e) -> {
                //txtVouTotal.setValue(tableModel.getTotal());
                String depositeId = Util1.getPropValue("system.dc.deposite.id");
                String discountId = Util1.getPropValue("system.dc.disc.id");
                String paidId = Util1.getPropValue("system.dc.paid.id");
                String refundId = Util1.getPropValue("system.dc.refund.id");
                List<DCDetailHis> listDCDH = tableModel.getListOPDDetailHis();
                QueryResults qr;
                Query q = new Query();
                String strSql = "SELECT * FROM com.cv.app.inpatient.database.entity.DCDetailHis"
                        + " WHERE service IS NOT NULL "
                        + "EXECUTE ON ALL sum(amount) AS total";
                String strFilter = "SELECT * FROM com.cv.app.inpatient.database.entity.DCDetailHis WHERE "
                        + "service.serviceId not in (" + depositeId + ","
                        + discountId + "," + paidId + "," + refundId + ")";
                try {
                    q.parse(strSql);
                    qr = q.execute(JoSQLUtil.getResult(strFilter, listDCDH));
                    double vTotal = Double.parseDouble(qr.getSaveValue("total").toString());
                    txtVouTotal.setValue(vTotal);

                    strFilter = "SELECT * FROM com.cv.app.inpatient.database.entity.DCDetailHis WHERE "
                            + "service.serviceId in (" + depositeId + "," + paidId + ")";
                    qr = q.execute(JoSQLUtil.getResult(strFilter, listDCDH));
                    double vTotalPaid = Double.parseDouble(qr.getSaveValue("total").toString());
                    txtPaid.setValue(vTotalPaid);

                    strFilter = "SELECT * FROM com.cv.app.inpatient.database.entity.DCDetailHis WHERE "
                            + "service.serviceId in (" + refundId + ")";
                    qr = q.execute(JoSQLUtil.getResult(strFilter, listDCDH));
                    double vTotalRefund = Double.parseDouble(qr.getSaveValue("total").toString());
                    txtPaid.setValue(vTotalPaid - vTotalRefund);

                    strFilter = "SELECT * FROM com.cv.app.inpatient.database.entity.DCDetailHis WHERE "
                            + "service.serviceId in (" + discountId + ")";
                    qr = q.execute(JoSQLUtil.getResult(strFilter, listDCDH));
                    double vTotalDiscount = Double.parseDouble(qr.getSaveValue("total").toString());
                    txtDiscA.setValue(vTotalDiscount);
                } catch (QueryParseException qpe) {
                    log.error("JoSQLUtil.isAlreadyHave qpe: " + qpe.toString());
                } catch (QueryExecutionException | NumberFormatException ex) {
                    log.error("JoSQLUtil.isAlreadyHave : " + ex.toString());
                }
                txtTotalItem.setText(Integer.toString((tableModel.getTotalRecord() - 1)));
                calcBalance();
            });

            tblService.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblService.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
                txtRecNo.setText(Integer.toString(tblService.getSelectedRow() + 1));
            });

            tblPatientBill.getColumnModel().getColumn(0).setPreferredWidth(180);//Bill Name
            tblPatientBill.getColumnModel().getColumn(1).setPreferredWidth(70);//Amount
            tblPatientBill.getTableHeader().setFont(Global.lableFont);
            tblAmountLink.getColumnModel().getColumn(0).setPreferredWidth(40);//Option
            tblAmountLink.getColumnModel().getColumn(1).setPreferredWidth(100);//Vou No
            tblAmountLink.getColumnModel().getColumn(2).setPreferredWidth(60);//Amount
            tblAmountLink.getColumnModel().getColumn(3).setPreferredWidth(20);//Status
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void actionMapping() {
        //F8 event on tblService
        tblService.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblService.getActionMap().put("F8-Action", actionItemDelete);

        //Enter event on tblService
        tblService.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblService.getActionMap().put("ENTER-Action", actionTblServiceEnterKey);

        formActionKeyMapping(txtVouNo);
        formActionKeyMapping(txtRemark);
        formActionKeyMapping(txtVouTotal);
        formActionKeyMapping(txtDiscP);
        formActionKeyMapping(txtTaxP);
        formActionKeyMapping(txtVouBalance);
        formActionKeyMapping(tblService);
        formActionKeyMapping(txtPatientNo);
        formActionKeyMapping(txtPatientName);
        formActionKeyMapping(txtDoctorNo);
        formActionKeyMapping(txtDoctorName);
        formActionKeyMapping(cboCurrency);
        formActionKeyMapping(cboPaymentType);
        formActionKeyMapping(cboDCStatus);
    }

    private void formActionKeyMapping(JComponent jc) {
        //Save
        jc.getInputMap().put(KeyStroke.getKeyStroke("F5"), "F5-Action");
        jc.getActionMap().put("F5-Action", actionSave);

        //Print
        jc.getInputMap().put(KeyStroke.getKeyStroke("F7"), "F7-Action");
        jc.getActionMap().put("F7-Action", actionPrint);

        //History
        jc.getInputMap().put(KeyStroke.getKeyStroke("F9"), "F9-Action");
        jc.getActionMap().put("F9-Action", actionHistory);

        //Clear
        jc.getInputMap().put(KeyStroke.getKeyStroke("F10"), "F10-Action");
        jc.getActionMap().put("F10-Action", actionNewForm);

        //Delete
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.CTRL_DOWN_MASK);
        jc.getInputMap().put(keyStroke, "Ctrl-F8-Action");
        jc.getActionMap().put("Ctrl-F8-Action", actionDelete);

        //Delete Copy
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.SHIFT_DOWN_MASK);
        jc.getInputMap().put(keyStroke, "Shift-F8-Action");
        jc.getActionMap().put("Shift-F8-Action", actionDeleteCopy);
    }
    private Action actionSave = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            save();
        }
    };
    private Action actionPrint = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            print();
        }
    };
    private Action actionHistory = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            history();
        }
    };
    private Action actionNewForm = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            newForm();
        }
    };
    private Action actionDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            delete();
        }
    };
    private Action actionDeleteCopy = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteCopy();
        }
    };
    private Action actionItemDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!canEdit) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot delete.",
                        "Check Point", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (lblStatus.getText().equals("EDIT")) {
                if (!Util1.hashPrivilege("DCVoucherEditChange")) {
                    if (!canEdit) {
                        return;
                    }
                }
            }
            int row = tblService.getSelectedRow();

            if (row >= 0) {
                DCDetailHis opdh = tableModel.getOPDDetailHis(row);

                if (opdh.getService() != null) {
                    try {
                        if (tblService.getCellEditor() != null) {
                            tblService.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {
                        //log.error(ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                    }

                    /*int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                        "Are you sure to delete?",
                        "Delete", JOptionPane.YES_NO_OPTION);*/
                    //if (yes_no == 0) {
                    tableModel.deleteOPDDetailHis(row);
                    //}
                }
            }
        }
    };

    private void initTextBoxAlign() {
        txtVouTotal.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtPaid.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtVouBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtDiscA.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtDiscP.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTaxA.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTaxP.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtPkgPrice.setHorizontalAlignment(JFormattedTextField.RIGHT);

        txtVouTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtDiscA.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtDiscP.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTaxA.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTaxP.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPkgPrice.setFormatterFactory(NumberUtil.getDecimalFormat());
    }

    private void assignDefaultValue() {
        if (Util1.getPropValue("system.login.default.value").equals("Y")) {
            if (Global.loginDate == null) {
                Global.loginDate = DateUtil.getTodayDateStr();
            }
            txtDate.setText(Global.loginDate);
        } else {
            txtDate.setText(DateUtil.getTodayDateStr());
        }
        txtVouTotal.setValue(0);
        txtTaxA.setValue(0);
        txtTaxP.setValue(0);
        txtPaid.setValue(0);
        txtDiscA.setValue(0);
        txtDiscP.setValue(0);
        txtVouBalance.setValue(0);
        //cboDCStatus.setSelectedIndex(0);
        if (cboDiagnosis.getItemCount() > 0) {
            cboDiagnosis.setSelectedIndex(0);
        }
        cboPaymentType.setSelectedIndex(0);
        vouEngine.setPeriod(DateUtil.getPeriod(txtDate.getText()));
        cboPaymentType.setSelectedItem(ptCash);
        cboDCStatus.setSelectedItem(null);
        cboDiagnosis.setSelectedItem(null);
        genVouNo();
    }

    private Action actionTblServiceEnterKey = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblService.getCellEditor() != null) {
                    tblService.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
                log.error(ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            }

            int row = tblService.getSelectedRow();
            int col = tblService.getSelectedColumn();

            DCDetailHis record = tableModel.getOPDDetailHis(row);
            if (col == 0 && record.getService() != null) {
                tblService.setColumnSelectionInterval(1, 1); //to Qty
            } else if (col == 1 && record.getService() != null) {
                tblService.setColumnSelectionInterval(3, 3); //to Charge Type
            } else if (col == 2 && record.getService() != null) {
                tblService.setColumnSelectionInterval(3, 3); //to Charge Type
            } else if (col == 3 && record.getService() != null) {
                if ((row + 1) <= tableModel.getListOPDDetailHis().size()) {
                    tblService.setRowSelectionInterval(row + 1, row + 1);
                }
                tblService.setColumnSelectionInterval(0, 0); //to Description
            }
        }
    };

    private boolean isValidEntry() {
        if (!Util1.hashPrivilege("CanEditDCCheckPoint")) {
            if (lblStatus.getText().equals("NEW")) {
                try {
                    long cnt = dao.getRowCount("select count(*) from c_bk_dc_his where dc_date >= '"
                            + DateUtil.toDateStrMYSQL(txtDate.getText()) + "'");
                    if (cnt > 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot entry voucher in back date.",
                                "Check Point", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                } catch (Exception ex) {
                    log.error("isValidEntry : " + ex.toString());
                }
            }
        }

        if (tblService.getCellEditor() != null) {
            tblService.getCellEditor().stopCellEditing();
        }
        boolean status = true;
        double modelTotal = tableModel.getTotal();
        txtVouTotal.setValue(modelTotal);
        calcBalance();

        if (!DateUtil.isValidDate(txtDate.getText())) {
            log.error("DC date error : " + txtVouNo.getText());
            status = false;
        } else if (txtVouNo.getText().trim().length() < 15) {
            log.error("DC vou error : " + txtVouNo.getText() + " - " + txtDate.getText());
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid vou no.",
                    "Vou No.", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (!tableModel.isValidEntry()) {
            status = false;
        } else if (txtAdmissionNo.getText().trim().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid admitted patient.",
                    "Admitted Patient", JOptionPane.ERROR_MESSAGE);
        } else {
            currVou.setOpdInvId(txtVouNo.getText());
            if (lblStatus.getText().equals("NEW")) {
                currVou.setInvDate(DateUtil.toDateTime(txtDate.getText()));
            } else {
                Date tmpDate = DateUtil.toDate(txtDate.getText());
                if (!DateUtil.isSameDate(tmpDate, currVou.getInvDate())) {
                    currVou.setInvDate(DateUtil.toDateTime(txtDate.getText()));
                }
            }
            currVou.setCurrency((Currency) cboCurrency.getSelectedItem());
            currVou.setPaymentType((PaymentType) cboPaymentType.getSelectedItem());
            currVou.setRemark(txtRemark.getText().trim());
            currVou.setVouTotal(NumberUtil.NZero(txtVouTotal.getValue()));
            currVou.setDiscountP(NumberUtil.NZero(txtDiscP.getValue()));
            currVou.setDiscountA(NumberUtil.NZero(txtDiscA.getValue()));
            currVou.setTaxP(NumberUtil.NZero(txtTaxP.getValue()));
            currVou.setTaxA(NumberUtil.NZero(txtTaxA.getValue()));
            currVou.setPaid(NumberUtil.NZero(txtPaid.getValue()));
            currVou.setVouBalance(NumberUtil.NZero(txtVouBalance.getValue()));
            currVou.setListOPDDetailHis(tableModel.getListOPDDetailHis());

            if (cboAgeRange.getSelectedItem() == null) {
                currVou.setAgeRange(null);
            } else {
                currVou.setAgeRange((AgeRange) cboAgeRange.getSelectedItem());
            }

            //currVou.setDonorName(txtDonorName.getText());
            if (cboDCStatus.getSelectedItem() == null) {
                currVou.setDcStatus(null);
            } else {
                currVou.setDcStatus((DCStatus) cboDCStatus.getSelectedItem());
            }
            currVou.setPatientName(txtPatientName.getText());
            currVou.setAdmissionNo(txtAdmissionNo.getText());
            currVou.setDiagnosis((Diagnosis) cboDiagnosis.getSelectedItem());
            currVou.setIntgUpdStatus(null);

            if (lblStatus.getText().equals("EDIT") || lblStatus.getText().equals("DELETED")) {
                currVou.setUpdatedBy(Global.loginUser);
                currVou.setUpdatedDate(new Date());
                Date tmpDate = DateUtil.toDate(txtDate.getText());
                if (!DateUtil.isSameDate(tmpDate, currVou.getInvDate())) {
                    currVou.setInvDate(DateUtil.toDateTime(txtDate.getText()));
                }
            } else if (lblStatus.getText().equals("NEW")) {
                currVou.setCreatedBy(Global.loginUser);
                currVou.setDeleted(false);
                currVou.setSession(Global.sessionId);
                currVou.setCreatedDate(new Date());
            }
        }

        return status;
    }

    public void getPatientBill(String regNo) {
        try {
            List<PatientBillPayment> listPBP = new ArrayList();
            Double totalBalance = 0.0;
            String currency = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
            String date = DateUtil.toDateStrMYSQL(txtDate.getText());
            try ( //dao.open();
                     ResultSet resultSet = dao.getPro("patient_bill_payment",
                            regNo, DateUtil.toDateStrMYSQL(txtDate.getText()),
                            currency, Global.machineId)) {
                while (resultSet.next()) {
                    double bal = resultSet.getDouble("balance");
                    if (bal != 0) {
                        PatientBillPayment pbp = new PatientBillPayment();

                        pbp.setBillTypeDesp(resultSet.getString("payment_type_name"));
                        pbp.setBillTypeId(resultSet.getInt("bill_type"));
                        pbp.setCreatedBy(Global.loginUser.getUserId());
                        pbp.setCurrency(resultSet.getString("currency"));
                        pbp.setPayDate(DateUtil.toDate(txtDate.getText()));
                        pbp.setRegNo(resultSet.getString("reg_no"));
                        pbp.setAmount(resultSet.getDouble("balance"));
                        pbp.setBalance(resultSet.getDouble("balance"));

                        totalBalance += pbp.getAmount();
                        listPBP.add(pbp);
                    }
                }
            }

            tblPatientBillTableModel.setListPBP(listPBP);
            txtBillTotal.setValue(totalBalance);
        } catch (Exception ex) {
            log.error("PatientSearch : Patient_bill_Payment :" + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private boolean isNeedDetail(int serviceId) {
        boolean status = false;
        try {
            List<DrDetailId> listDDI = dao.findAllHSQL(
                    "select o from DrDetailId o where o.key.option = 'DC' and o.key.serviceId = " + serviceId);
            if (listDDI != null) {
                if (!listDDI.isEmpty()) {
                    status = true;
                }
            }
        } catch (Exception ex) {
            log.error("isNeedDetail : " + ex.getMessage());
        } finally {
            dao.close();
        }
        return status;
    }

    private void doctorFeePopup(DCDetailHis record) {
        if (record != null) {
            if (record.getService() != null) {
                if (record.getService().getServiceId() != null) {
                    List<DCDoctorFee> listDrFee = record.getListDCDF();
                    if (listDrFee == null) {
                        listDrFee = new ArrayList();
                    }
                    DCDoctorFeeDialog dialog = new DCDoctorFeeDialog(listDrFee, record.getService().getServiceId());
                    dialog.setVisible(true);
                    record.setListDCDF(dialog.getEntryDrFee());
                    record.setPrice(dialog.getTotal());
                }
            }
        }
    }

    private void applySecurityPolicy() {
        if (lblStatus.getText().equals("EDIT")) {
            if (!Util1.hashPrivilege("DCVoucherEditChange") || !canEdit) {
                if (!canEdit) {
                    enableDisableControl(false);
                } else {
                    enableDisableControl(true);
                }
            } else {
                enableDisableControl(true);
            }
        } else {
            enableDisableControl(true);
        }
    }

    private void enableDisableControl(boolean status) {
        txtPatientNo.setEnabled(status);
        txtPatientName.setEnabled(status);
        txtDoctorNo.setEnabled(status);
        cboCurrency.setEnabled(status);
        cboPaymentType.setEnabled(status);
        cboDCStatus.setEnabled(status);
        txtRemark.setEnabled(status);
        cboAgeRange.setEnabled(status);
        cboDiagnosis.setEnabled(status);
        //butPkgRemove.setEnabled(status);
        //butCheckBill.setEnabled(status);
    }

    private void savePackage(String vouNo, Long pkgId) {
        if (pkgId != null) {
            String strSqlDelete = "delete from clinic_package_detail_his where dc_inv_no = '" + vouNo + "' and pkg_opt = 'DC'";
            String strSql = "insert into clinic_package_detail_his(dc_inv_no, pkg_id, item_key, "
                    + "unit_qty,item_unit,qty_smallest, sys_price, usr_price, pk_detail_id, "
                    + "sys_amt, usr_amt, pkg_opt) \n"
                    + "select '" + vouNo + "', pkg_id, item_key, unit_qty, item_unit, qty_smallest, sys_price, "
                    + "usr_price, id, sys_amt, usr_amt, 'DC' \n"
                    + "from clinic_package_detail where pkg_id = " + pkgId.toString();
            try {
                dao.execSql(strSqlDelete, strSql);
            } catch (Exception ex) {
                log.error("savePackage : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    private void calcPackageExtraFees(DCHis dcHis) {
        try {
            if (dcHis != null) {
                if (dcHis.getAdmissionNo() != null && dcHis.getPkgId() != null) {
                    String vouNo = txtVouNo.getText().trim();
                    String regNo = dcHis.getPatient().getRegNo();
                    String strSqlDelete = "delete from tmp_clinic_package where user_id = '" + Global.machineId + "'";
                    String strSqlNotIncludeItem = "insert into tmp_clinic_package (vou_no, reg_no, ams_no, item_id,\n"
                            + "  item_key, qty, price, amount, user_id, tran_option, tran_type, pkg_status, sort_order)\n"
                            + "select '" + vouNo + "', reg_no, admission_no, item_id, b.item_key, "
                            + "ttl_use_qty, item_price, ttl_amt, '" + Global.machineId + "', \n"
                            + "tran_option, tran_type, 'OUTSIDE', 2 "
                            + "from (select reg_no, admission_no, item_id, item_key, sum(ttl_use_qty) ttl_use_qty,\n"
                            + "sum(ttl_amt) ttl_amt, if(sum(ttl_use_qty)>0,(sum(ttl_amt)/sum(ttl_use_qty)),0) item_price, 'Pharmacy' tran_option, 'EXPENSE' tran_type\n"
                            + "from (select reg_no, admission_no, med_id item_id, concat('PHAR-',med_id) item_key, sum(sale_smallest_qty) ttl_use_qty,\n"
                            + "sum(sale_amount) ttl_amt\n"
                            + "from (select sh.deleted, sh.admission_no, sh.reg_no, sdh.med_id, sdh.sale_smallest_qty, sdh.sale_amount\n"
                            + "from sale_his sh, sale_detail_his sdh\n"
                            + "where sh.sale_inv_id = sdh.vou_no) a \n"
                            + "where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + dcHis.getAdmissionNo() + "'\n"
                            + " and reg_no = '" + regNo + "' \n"
                            + "group by reg_no, admission_no, med_id\n"
                            + "union all\n"
                            + "select reg_no, admission_no, med_id item_id, concat('PHAR-',med_id) item_key, sum(ret_in_smallest_qty)*-1 ttl_use_qty,\n"
                            + "sum(ret_in_amount)*-1 ttl_amt\n"
                            + "from v_return_in\n"
                            + "where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + dcHis.getAdmissionNo() + "'\n"
                            + " and reg_no = '" + regNo + "' \n"
                            + "group by reg_no, admission_no, med_id) a \n"
                            + "where ifNull(admission_no,'')<>'' and admission_no ='" + dcHis.getAdmissionNo() + "' \n"
                            + " and reg_no = '" + regNo + "' \n"
                            + "group by reg_no, admission_no, item_id\n"
                            + "union all\n"
                            + "select patient_id reg_no, admission_no, service_id item_id, concat('OPD-',service_id) item_key, sum(qty) ttl_use_qty,\n"
                            + "sum(amount) ttl_amt, if(sum(qty)>0,(sum(amount)/sum(qty)),0) item_price, 'OPD' tran_option, 'EXPENSE' tran_type\n"
                            + "from v_opd\n"
                            + "where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + dcHis.getAdmissionNo() + "' \n"
                            + " and patient_id = '" + regNo + "' \n"
                            + "group by reg_no, admission_no, service_id\n"
                            + "union all\n"
                            + "select patient_id reg_no, admission_no, service_id item_id, concat('OT-',service_id) item_key, sum(qty) ttl_use_qty,\n"
                            + "sum(amount) ttl_amt, if(sum(qty)>0,(sum(amount)/sum(qty)),0) item_price, 'OT' tran_option, 'EXPENSE' tran_type\n"
                            + "from v_ot\n"
                            + "where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + dcHis.getAdmissionNo() + "' \n"
                            + " and patient_id = '" + regNo + "' \n"
                            + "and service_id not in (select sys_prop_value from sys_prop \n"
                            + "where sys_prop_desp in ('system.ot.deposite.id','system.ot.disc.id','system.ot.paid.id','system.ot.refund.id')) \n"
                            + "group by patient_id, admission_no, service_id\n"
                            + "union all\n"
                            + "select patient_id reg_no, admission_no, service_id item_id, concat('DC-',service_id) item_key, sum(qty) ttl_use_qty,\n"
                            + "sum(amount) ttl_amt, if(sum(qty)>0, (sum(amount)/sum(qty)),0) item_price, 'DC' tran_option, 'EXPENSE' tran_type\n"
                            + "from v_dc\n"
                            + "where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + dcHis.getAdmissionNo() + "' "
                            + " and patient_id = '" + regNo + "' \n"
                            + "and dc_inv_id <> '" + vouNo + "' "
                            + "and service_id not in (select sys_prop_value from sys_prop \n"
                            + "where sys_prop_desp in ('system.dc.deposite.id','system.dc.disc.id','system.dc.paid.id','system.dc.refund.id')) \n"
                            + "group by patient_id, admission_no, service_id) b left join \n"
                            + "(select item_key from clinic_package_detail_his "
                            + "where dc_inv_no = '" + vouNo
                            + "' and pkg_id = " + dcHis.getPkgId().toString() + " and pkg_opt = 'DC') c on b.item_key = c.item_key \n"
                            + "where c.item_key is null "
                            + "and admission_no = '" + dcHis.getAdmissionNo() + "' \n";
                    String strSqlOverQty = "insert into tmp_clinic_package (vou_no, reg_no, ams_no, item_id,\n"
                            + "  item_key, qty, price, amount, user_id, tran_option, tran_type, pkg_status, sort_order)\n"
                            + "select '" + vouNo + "', vpiu.reg_no, vpiu.admission_no, vpiu.item_id, vpiu.item_key, "
                            + "(vpiu.ttl_use_qty-cpdh.qty_smallest) qty,\n"
                            + "vpiu.item_price, ((vpiu.ttl_use_qty-cpdh.qty_smallest)*vpiu.item_price) amount, '"
                            + Global.machineId + "',\n"
                            + "tran_option, tran_type, 'EXTRA-USE', 2 "
                            + "from (select * from clinic_package_detail_his where dc_inv_no = '"
                            + vouNo + "' and pkg_id = " + dcHis.getPkgId().toString() + " and pkg_opt = 'DC') cpdh, \n"
                            + "(select * from (select reg_no, admission_no, item_id, item_key, sum(ttl_use_qty) ttl_use_qty,\n"
                            + "sum(ttl_amt) ttl_amt, if(sum(ttl_use_qty)>0,(sum(ttl_amt)/sum(ttl_use_qty)),0) item_price, 'Pharmacy' tran_option, 'EXPENSE' tran_type\n"
                            + "from (select reg_no, admission_no, med_id item_id, concat('PHAR-',med_id) item_key, sum(sale_smallest_qty) ttl_use_qty,\n"
                            + "sum(sale_amount) ttl_amt\n"
                            + "from (select sh.deleted, sh.admission_no, sh.reg_no, sdh.med_id, sdh.sale_smallest_qty, sdh.sale_amount\n"
                            + "from sale_his sh, sale_detail_his sdh\n"
                            + "where sh.sale_inv_id = sdh.vou_no) a \n"
                            + "where deleted = false and ifNull(admission_no,'')<>''  and admission_no = '" + dcHis.getAdmissionNo() + "' \n"
                            + " and reg_no = '" + regNo + "' \n"
                            + "group by reg_no, admission_no, med_id\n"
                            + "union all\n"
                            + "select reg_no, admission_no, med_id item_id, concat('PHAR-',med_id) item_key, sum(ret_in_smallest_qty)*-1 ttl_use_qty,\n"
                            + "sum(ret_in_amount)*-1 ttl_amt\n"
                            + "from v_return_in\n"
                            + "where deleted = false and ifNull(admission_no,'')<>''  and admission_no = '" + dcHis.getAdmissionNo() + "' \n"
                            + " and reg_no = '" + regNo + "' \n"
                            + "group by reg_no, admission_no, med_id) a \n"
                            + "where ifNull(admission_no,'')<>''  and admission_no = '" + dcHis.getAdmissionNo() + "' \n"
                            + " and reg_no = '" + regNo + "' \n"
                            + "group by reg_no, admission_no, item_id\n"
                            + "union all\n"
                            + "select patient_id reg_no, admission_no, service_id item_id, concat('OPD-',service_id) item_key, sum(qty) ttl_use_qty,\n"
                            + "sum(amount) ttl_amt, if(sum(qty)>0, (sum(amount)/sum(qty)),0) item_price, 'OPD' tran_option, 'EXPENSE' tran_type\n"
                            + "from v_opd\n"
                            + "where deleted = false and ifNull(admission_no,'')<>''  and admission_no = '" + dcHis.getAdmissionNo() + "' \n"
                            + " and patient_id = '" + regNo + "' \n"
                            + "group by reg_no, admission_no, service_id\n"
                            + "union all\n"
                            + "select patient_id reg_no, admission_no, service_id item_id, concat('OT-',service_id) item_key, sum(qty) ttl_use_qty,\n"
                            + "sum(amount) ttl_amt, if(sum(qty)>0,(sum(amount)/sum(qty)),0) item_price, 'OT' tran_option, 'EXPENSE' tran_type\n"
                            + "from v_ot\n"
                            + "where deleted = false and ifNull(admission_no,'')<>''  and admission_no = '" + dcHis.getAdmissionNo() + "' \n"
                            + " and patient_id = '" + regNo + "' \n"
                            + "and service_id not in (select sys_prop_value from sys_prop \n"
                            + "where sys_prop_desp in ('system.ot.deposite.id','system.ot.disc.id','system.ot.paid.id','system.ot.refund.id')) \n"
                            + "group by patient_id, admission_no, service_id\n"
                            + "union all\n"
                            + "select patient_id reg_no, admission_no, service_id item_id, concat('DC-',service_id) item_key, sum(qty) ttl_use_qty,\n"
                            + "sum(amount) ttl_amt, if(sum(qty), (sum(amount)/sum(qty)),0) item_price, 'DC' tran_option, 'EXPENSE' tran_type\n"
                            + "from v_dc\n"
                            + "where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + dcHis.getAdmissionNo() + "' \n"
                            + " and patient_id = '" + regNo + "' \n"
                            + "and dc_inv_id <> '" + vouNo + "' "
                            + "and service_id not in (select sys_prop_value from sys_prop \n"
                            + "where sys_prop_desp in ('system.dc.deposite.id','system.dc.disc.id','system.dc.paid.id','system.dc.refund.id')) \n"
                            + "group by patient_id, admission_no, service_id) b where admission_no = '" + dcHis.getAdmissionNo() + "') vpiu\n"
                            + "where cpdh.item_key = vpiu.item_key and  cpdh.qty_smallest < vpiu.ttl_use_qty";
                    dao.execSql(strSqlDelete, strSqlNotIncludeItem, strSqlOverQty);
                }
            }
        } catch (Exception ex) {
            log.error("calcPackageExtraFees : " + ex.toString());
        }
    }

    private void calcPackageGainLost(String status) {
        try {
            Double opValue = 0.0;
            Double ttlExpense = 0.0;
            Double ttlPaid = 0.0;
            Double ttlDiscount = 0.0;
            Double ttlOTDeposite = 0.0;
            Double ttlDCDeposite = 0.0;
            Double currVouTotal = NumberUtil.NZero(txtVouTotal.getValue());
            Double currDiscount = NumberUtil.NZero(txtDiscA.getValue());
            Double currTax = NumberUtil.NZero(txtTaxA.getValue());
            String currency = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
            String regNo = txtPatientNo.getText().trim();
            String amsNo = txtAdmissionNo.getText().trim();
            String vouNo = txtVouNo.getText().trim();

            ResultSet resultSet = dao.getPro("get_patient_exp_payment",
                    regNo, DateUtil.toDateStrMYSQL(txtDate.getText()),
                    currency, Global.machineId, vouNo, amsNo);

            if (resultSet.next()) {
                opValue = resultSet.getDouble("v_op");
                ttlExpense = resultSet.getDouble("v_ttl_expense");
                ttlPaid = resultSet.getDouble("v_ttl_payment");
                ttlDiscount = resultSet.getDouble("v_ttl_discount");
                ttlOTDeposite = resultSet.getDouble("v_ttl_ot_deposite");
                ttlDCDeposite = resultSet.getDouble("v_ttl_dc_deposite");
            }
            resultSet.close();

            String strSqlExtraCharges = "select sum(ifnull(amount,0)) ttl_extra_use\n"
                    + "from tmp_clinic_package\n"
                    + "where user_id = '" + Global.machineId + "' and tran_type ='EXPENSE'";
            ResultSet rsExt = dao.execSQL(strSqlExtraCharges);
            Double extraCharges = 0.0;
            if (rsExt.next()) {
                extraCharges = rsExt.getDouble("ttl_extra_use");
            }

            Double pkgAmount = NumberUtil.NZero(txtPkgPrice.getValue());
            ttlExpense = ttlExpense + currVouTotal + currTax;
            Double pkgUseAmt = getPkgUseAmt();
            //Double needToPaid = ttlExpense - ttlPaid;
            Double needToPaid = (pkgAmount + extraCharges) - ttlPaid;

            needToPaid -= (currDiscount + ttlDiscount);
            Double pkgGainAmt = pkgAmount - pkgUseAmt;
            if (status.equals("-")) {
                tableModel.addPaidGain(needToPaid, pkgGainAmt);
            }

            //Package
            String strSql = "insert into tmp_clinic_package (vou_no, reg_no, ams_no, item_id,"
                    + "item_key, qty, price, amount, user_id, tran_option, tran_type, description, sort_order) "
                    + "values('" + vouNo + "','" + regNo + "','" + amsNo + "', '-','-',"
                    + "1," + pkgAmount + "," + pkgAmount + ",'" + Global.machineId
                    + "','Package','PACKAGE','" + txtPkgName.getText() + "',1)";
            dao.execSql(strSql);

            //Total Expense
            if ((pkgAmount + extraCharges) != 0) {
                strSql = "insert into tmp_clinic_package (vou_no, reg_no, ams_no, item_id,"
                        + "item_key, qty, price, amount, user_id, tran_option, tran_type, description, sort_order) "
                        + "values('" + vouNo + "','" + regNo + "','" + amsNo + "', '-','-',"
                        + "1," + (pkgAmount + extraCharges) + "," + (pkgAmount + extraCharges) + ",'" + Global.machineId
                        + "','Expense','TTL-EXPENSE','" + "Total Bill Amount" + "',3)";
                dao.execSql(strSql);
            }

            //Discount
            if ((currDiscount + ttlDiscount) != 0) {
                strSql = "insert into tmp_clinic_package (vou_no, reg_no, ams_no, item_id,"
                        + "item_key, qty, price, amount, user_id, tran_option, tran_type, description, sort_order) "
                        + "values('" + vouNo + "','" + regNo + "','" + amsNo + "', '-','-',"
                        + "1," + (currDiscount + ttlDiscount) + "," + (currDiscount + ttlDiscount) + ",'" + Global.machineId
                        + "','Payment','PAYMENT','" + "Discount" + "',3)";
                dao.execSql(strSql);
            }

            //OT Deposite
            if (ttlOTDeposite != 0) {
                strSql = "insert into tmp_clinic_package (vou_no, reg_no, ams_no, item_id,"
                        + "item_key, qty, price, amount, user_id, tran_option, tran_type, description, sort_order) "
                        + "values('" + vouNo + "','" + regNo + "','" + amsNo + "', '-','-',"
                        + "1," + ttlOTDeposite + "," + ttlOTDeposite + ",'" + Global.machineId
                        + "','Payment','PAYMENT','" + "OT Deposite" + "',3)";
                dao.execSql(strSql);
            }

            //DC Deposite
            if (ttlDCDeposite != 0) {
                strSql = "insert into tmp_clinic_package (vou_no, reg_no, ams_no, item_id,"
                        + "item_key, qty, price, amount, user_id, tran_option, tran_type, description, sort_order) "
                        + "values('" + vouNo + "','" + regNo + "','" + amsNo + "', '-','-',"
                        + "1," + ttlDCDeposite + "," + ttlDCDeposite + ",'" + Global.machineId
                        + "','Payment','PAYMENT','" + "DC Deposite" + "',3)";
                dao.execSql(strSql);
            }

            //Previous Paid
            ttlPaid = ttlPaid - (ttlOTDeposite + ttlDCDeposite);
            if (ttlPaid != 0) {
                strSql = "insert into tmp_clinic_package (vou_no, reg_no, ams_no, item_id,"
                        + "item_key, qty, price, amount, user_id, tran_option, tran_type, description, sort_order) "
                        + "values('" + vouNo + "','" + regNo + "','" + amsNo + "', '-','-',"
                        + "1," + ttlPaid + "," + ttlPaid + ",'" + Global.machineId
                        + "','Payment','PAYMENT','" + "Previous Paid" + "',3)";
                dao.execSql(strSql);
            }

            //Refund or Need to be paid
            if (needToPaid > 0) {
                strSql = "insert into tmp_clinic_package (vou_no, reg_no, ams_no, item_id,"
                        + "item_key, qty, price, amount, user_id, tran_option, tran_type, description, sort_order) "
                        + "values('" + vouNo + "','" + regNo + "','" + amsNo + "', '-','-',"
                        + "1," + needToPaid + "," + needToPaid + ",'" + Global.machineId
                        + "','Payment','PAYMENT','" + "Need to be paid" + "',3)";
                dao.execSql(strSql);
            } else if (needToPaid < 0) {
                strSql = "insert into tmp_clinic_package (vou_no, reg_no, ams_no, item_id,"
                        + "item_key, qty, price, amount, user_id, tran_option, tran_type, description, sort_order) "
                        + "values('" + vouNo + "','" + regNo + "','" + amsNo + "', '-','-',"
                        + "1," + needToPaid + "," + needToPaid + ",'" + Global.machineId
                        + "','Payment','PAYMENT','" + "Refund" + "',3)";
                dao.execSql(strSql);
            }

        } catch (SQLException ex) {
            log.error("calcPackageGainLost dc_patient_balance : " + ex.getStackTrace()[0].getLineNumber() + " - "
                    + ex.toString());
        } catch (Exception ex) {
            log.error("calcPackageGainLost : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void setEditStatus(String invId) {
        //canEdit
        /*List<SessionCheckCheckpoint> list = dao.findAllHSQL(
                "select o from SessionCheckCheckpoint o where o.tranOption = 'DC' "
                + " and o.tranInvId = '" + invId + "'");*/
        canEdit = Util1.hashPrivilege("DCVoucherEditChange");
        boolean isAllowEdit = Util1.hashPrivilege("DCCreditVoucherEdit");
        double vouPaid = NumberUtil.NZero(currVou.getPaid());

        if (!canEdit) {
            if (!isAllowEdit) {
                return;
            }
        }

        if (!Util1.hashPrivilege("CanEditDCCheckPoint")) {
            if (currVou != null) {
                if (currVou.getAdmissionNo() != null) {
                    if (!currVou.getAdmissionNo().trim().isEmpty()) {
                        AdmissionKey key = new AdmissionKey();
                        key.setRegister(currVou.getPatient());
                        key.setAmsNo(currVou.getAdmissionNo());
                        try {
                            Ams admPt = (Ams) dao.find(Ams.class,
                                    key);
                            if (admPt != null) {
                                canEdit = admPt.getDcStatus() == null;
                            }
                        } catch (Exception ex) {
                            log.error("setEditStatus Get Admission : " + invId + " : " + ex.toString());
                        } finally {
                            dao.close();
                        }
                    }
                }
            }

            if (isAllowEdit && vouPaid == 0) {
                return;
            }

            String oneDayEdit = Util1.getPropValue("system.one.day.edit");
            if (oneDayEdit.equals("Y")) {
                if (canEdit) {
                    try {
                        List list = dao.findAllSQLQuery(
                                "select * from c_bk_dc_his where dc_inv_id = '" + invId + "'");
                        if (list != null) {
                            canEdit = list.isEmpty();
                        } else {
                            canEdit = true;
                        }
                    } catch (Exception ex) {
                        log.error("setEditStatus Check BK data : " + invId + " : " + ex.toString());
                    }
                }
            }
        } else {
            canEdit = true;
        }
    }

    private Double getPkgUseAmt() {
        String regNo = txtPatientNo.getText().trim();
        String amsNo = txtAdmissionNo.getText().trim();
        String vouNo = txtVouNo.getText().trim();
        Long pkgId = currVou.getPkgId();
        String strSql = "select os.reg_no, os.admission_no, vui.item_option, vui.med_id, os.item_key, vui.med_name, \n"
                + "	   sum(os.ttl_use_qty) ttl_use_qty, sum(os.ttl_amt) ttl_amt, sum(os.ttl_allow_qty) ttl_allow_qty, \n"
                + "       if(sum(os.ttl_use_qty) - sum(os.ttl_allow_qty)>0, sum(os.ttl_use_qty) - sum(os.ttl_allow_qty), 0) over_usage\n"
                + "  from (\n"
                + "		select a.reg_no, a.admission_no, a.item_key, sum(a.ttl_use_qty) ttl_use_qty,\n"
                + "			   sum(a.ttl_amt) ttl_amt, (sum(a.ttl_amt)/sum(a.ttl_use_qty)) item_price,\n"
                + "			   0 ttl_allow_qty\n"
                + "		  from (select reg_no, admission_no, med_id item_id, concat('PHAR-',med_id) item_key, sum(sale_smallest_qty) ttl_use_qty,\n"
                + "					   sum(sale_amount) ttl_amt\n"
                + "				  from (select sh.deleted, sh.admission_no, sh.reg_no, sdh.med_id, sdh.sale_smallest_qty, sdh.sale_amount\n"
                + "						  from sale_his sh, sale_detail_his sdh\n"
                + "						 where sh.sale_inv_id = sdh.vou_no) a \n"
                + "				 where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + amsNo + "'\n"
                + "				 group by reg_no, admission_no, med_id\n"
                + "				 union all\n"
                + "				select reg_no, admission_no, med_id item_id, concat('PHAR-',med_id) item_key, sum(ret_in_smallest_qty)*-1 ttl_use_qty,\n"
                + "					   sum(ret_in_amount)*-1 ttl_amt\n"
                + "				  from v_return_in\n"
                + "				 where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + amsNo + "'\n"
                + "				 group by reg_no, admission_no, med_id\n"
                + "				) a \n"
                + "		 where ifNull(a.admission_no,'')<>'' and a.admission_no ='" + amsNo + "' group by a.reg_no, a.admission_no, a.item_id\n"
                + "		 union all\n"
                + "		select patient_id reg_no, admission_no, concat('OPD-',service_id) item_key, sum(qty) ttl_use_qty,\n"
                + "			   sum(amount) ttl_amt, (sum(amount)/sum(qty)) item_price,\n"
                + "			   0 ttl_allow_qty\n"
                + "		  from v_opd\n"
                + "		 where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + amsNo + "'\n"
                + "         group by reg_no, admission_no, service_id\n"
                + "		 union all\n"
                + "		select patient_id reg_no, admission_no, concat('OT-',service_id) item_key, sum(qty) ttl_use_qty,\n"
                + "			   sum(amount) ttl_amt, (sum(amount)/sum(qty)) item_price,\n"
                + "			   0 ttl_allow_qty\n"
                + "		  from v_ot\n"
                + "		 where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + amsNo + "' \n"
                + "		   and service_id not in (select sys_prop_value from sys_prop \n"
                + "								   where sys_prop_desp in ('system.ot.deposite.id','system.ot.disc.id','system.ot.paid.id',\n"
                + "										 'system.ot.refund.id')) \n"
                + "		 group by patient_id, admission_no, service_id\n"
                + "		 union all\n"
                + "		select patient_id reg_no, admission_no, concat('DC-',service_id) item_key, sum(qty) ttl_use_qty,\n"
                + "			   sum(amount) ttl_amt, (sum(amount)/sum(qty)) item_price,\n"
                + "			   0 ttl_allow_qty\n"
                + "		  from v_dc\n"
                + "		 where deleted = false and ifNull(admission_no,'')<>'' and admission_no = '" + amsNo + "' \n"
                + "		   and dc_inv_id <> '" + vouNo + "' \n"
                + "		   and service_id not in (select sys_prop_value from sys_prop \n"
                + "								   where sys_prop_desp in ('system.dc.deposite.id','system.dc.disc.id','system.dc.paid.id',\n"
                + "										 'system.dc.refund.id')) \n"
                + "		 group by patient_id, admission_no, service_id\n"
                + "		 union all\n"
                + "		select '" + regNo + "' reg_no, '" + amsNo + "' admission_no, item_key, 0 ttl_use_qty, 0 ttl_amt, 0 item_price, \n"
                + "			   sum(ifnull(qty_smallest,0)) ttl_allow_qty\n"
                + "		  from clinic_package_detail_his\n"
                + "		 where dc_inv_no = '" + vouNo + "' and pkg_id = " + pkgId.toString() + " and pkg_opt = 'DC' \n"
                + "		 group by item_key) os, v_union_item vui\n"
                + "where os.item_key = vui.item_key \n"
                + "group by os.reg_no, os.admission_no, vui.item_option, os.item_key, vui.med_name\n"
                + "having sum(os.ttl_use_qty) > 0";

        double billTotal = 0;
        float overUsageTotal = 0f;
        float extraUsageTotal = 0f;
        double packageUsageTotal = 0;

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                while (rs.next()) {
                    PackageDetailEdit item = new PackageDetailEdit(
                            rs.getString("item_option"),
                            rs.getString("med_id"),
                            rs.getString("med_name"),
                            rs.getFloat("ttl_use_qty"),
                            rs.getDouble("ttl_amt"),
                            rs.getFloat("ttl_allow_qty"),
                            rs.getFloat("over_usage"),
                            rs.getString("item_key")
                    );

                    billTotal += item.getTtlAmount();
                    if (item.getStrStatus().equals("Over Use")) {
                        if (item.getTtlUseQty() != 0) {
                            overUsageTotal += (item.getTtlAmount() / item.getTtlUseQty())
                                    * item.getOverUseQty();
                        }
                    }
                    if (item.getStrStatus().equals("Extra Usage")) {
                        if (item.getTtlUseQty() != 0) {
                            extraUsageTotal += (item.getTtlAmount() / item.getTtlUseQty())
                                    * item.getOverUseQty();
                        }
                    }
                }

                packageUsageTotal = billTotal - (overUsageTotal + extraUsageTotal);
            }
        } catch (SQLException ex) {
            log.error("getPkgUseAmt : " + ex.toString());
        } finally {
            dao.close();
        }

        return packageUsageTotal;
    }

    private void backupPackage(String vouNo, String from, String desp) {
        String strSql = "insert into clinic_bk_clinic_package_detail_his (\n"
                + "  dc_inv_no, pkg_id, item_key, unit_qty, item_unit,\n"
                + "  qty_smallest, sys_price, usr_price, pk_detail_id,\n"
                + "  sys_amt, usr_amt, id, edit_status, prv_smallest_qty,\n"
                + "  updated_date, updated_by, bk_date, bk_by, bk_desp\n"
                + ")\n"
                + "select dc_inv_no, pkg_id, item_key, unit_qty, item_unit,\n"
                + "  qty_smallest, sys_price, usr_price, pk_detail_id,\n"
                + "  sys_amt, usr_amt, id, edit_status, prv_smallest_qty,\n"
                + "  updated_date, updated_by, now(), '" + Global.machineId
                + "', '" + desp + "' \n"
                + "from clinic_package_detail_his where dc_inv_no = '"
                + vouNo + "' and pkg_opt = 'DC'";
        try {
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("backupPackage : " + from + " : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void uploadToAccount(String vouNo, boolean isDeleted,
            Double balance, Double disc, Double paid, Double tax, String desp) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            if (!Global.mqConnection.isStatus()) {
                String mqUrl = Util1.getPropValue("system.mqserver.url");
                Global.mqConnection = new ActiveMQConnection(mqUrl);
            }
            if (Global.mqConnection != null) {
                if (Global.mqConnection.isStatus()) {
                    try {
                        AccSetting as = (AccSetting) dao.find(AccSetting.class,
                                "DC");

                        ActiveMQConnection mq = Global.mqConnection;
                        MapMessage msg = mq.getMapMessageTemplate();
                        msg.setString("entity", "DC");
                        msg.setString("VOUCHER-NO", vouNo);
                        msg.setBoolean("DELETED", isDeleted);
                        msg.setDouble("BALANCE", balance);
                        msg.setDouble("DISCOUNT", disc);
                        msg.setDouble("PAYMENT", paid);
                        msg.setDouble("TAX", tax);
                        msg.setString("DESCRIPTION", desp);

                        msg.setString("SOURCE-ACC", as.getSourceAcc());
                        msg.setString("DIS-ACC", as.getDiscAcc());
                        msg.setString("PAY-ACC", as.getPayAcc());
                        msg.setString("VOU-ACC", as.getBalanceAcc());

                        mq.sendMessage(Global.queueName, msg);
                    } catch (Exception ex) {
                        log.error("uploadToAccount : " + ex.getStackTrace()[0].getLineNumber()
                                + " - " + vouNo + " - " + ex);
                    }
                } else {
                    log.error("Connection status error : " + vouNo);
                }
            } else {
                log.error("Connection error : " + vouNo);
            }
        }
    }

    private void deleteDetail() {
        String deleteSQL;

        try {
            //All detail section need to explicity delete
            //because of save function only delete to join table
            deleteSQL = "delete from dc_doctor_fee where dc_detail_id in ("
                    + tableModel.getDeletedList() + ")";

            if (deleteSQL != null) {
                log.trace("deleteDetail doctor : " + deleteSQL);
                dao.execSql(deleteSQL);
            }

            deleteSQL = tableModel.getDeleteSql();
            if (deleteSQL != null) {
                log.trace("deleteDetail detail : " + deleteSQL);
                dao.execSql(deleteSQL);
            }

            String drFeeIds = tableModel.getDelDrFee();
            if (!drFeeIds.isEmpty()) {
                log.trace("deleteDetail detail : " + drFeeIds);
                dao.execSql("delete from dc_doctor_fee where dc_detail_id in ("
                        + drFeeIds + ")");
            }
        } catch (Exception ex) {
            log.error("deleteDetail : " + ex.toString());
        } finally {
            dao.close();
        }
        //delete section end
    }

    private void updateVouTotal(String vouNo) {
        String strSql = "update dc_his ph\n"
                + "join (select vou_no, sum(ifnull(amount,0)) as ttl_amt \n"
                + "from dc_details_his where vou_no = '" + vouNo + "' \n"
                + " and service_id not in \n"
                + "(select sys_prop_value from sys_prop where sys_prop_desp in \n"
                + "('system.dc.deposite.id','system.dc.disc.id','system.dc.paid.id','system.dc.refund.id'))"
                + " group by vou_no) pd\n"
                + "on ph.dc_inv_id = pd.vou_no set ph.vou_total = pd.ttl_amt\n"
                + "where ph.dc_inv_id = '" + vouNo + "'";
        try {
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("updateVouTotal : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    public double getPatientBalance(String regNo) {
        double totalBalance = 0.0;
        try {
            String currency = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();

            try ( //dao.open();
                     ResultSet resultSet = dao.getPro("patient_bill_payment",
                            regNo, DateUtil.toDateStrMYSQL(txtDate.getText()),
                            currency, Global.machineId)) {
                while (resultSet.next()) {
                    double bal = NumberUtil.NZero(resultSet.getDouble("balance"));
                    totalBalance += bal;
                }
            }
        } catch (Exception ex) {
            totalBalance = -1;
            log.error("getPatientBalance : Patient_bill_Payment :"
                    + ex.getStackTrace()[0].getLineNumber() + " - "
                    + ex.toString() + " : Vou No : " + txtVouNo.getText());
        } finally {
            dao.close();
        }

        return totalBalance;
    }

    private void printBillDetailPackage() {
        String reportName = Util1.getPropValue("report.file.dc");
        if (chkDetails.isSelected()) {
            reportName = Util1.getPropValue("system.dc.report.detail");
            reportName = "clinic/" + reportName;
        } else if (chkA5.isSelected()) {
            reportName = "clinic/DCDetailA5";
        }

        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + reportName;

        //For package report
        /*Long pkgId = currVou.getPkgId();
        if (pkgId != null) {
            reportName = Util1.getPropValue("system.dc.report.package");
            reportPath = Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path")
                    + "clinic/" + reportName;
        }*/
        //================================
        String imagePath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path");
        String printerName = Util1.getPropValue("report.vou.printer");
        Map<String, Object> params = new HashMap();
        String printMode = Util1.getPropValue("report.vou.printer.mode");

        //params.put("imagePath", imagePath);
        String compName = Util1.getPropValue("report.company.name");
        String phoneNo = Util1.getPropValue("report.phone");
        String address = Util1.getPropValue("report.address");
        params.put("compName", compName);
        params.put("phoneNo", phoneNo);
        params.put("comAddress", address);
        params.put("IMAGE_PATH", imagePath);
        params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));
        params.put("user_id", Global.machineId);

        if (chkDetails.isSelected()) {
            Patient pt = currVou.getPatient();
            params.put("reg_no", pt.getRegNo());

            if (pt != null && !txtAdmissionNo.getText().trim().isEmpty()) {
                try {
                    Ams ams;
                    AdmissionKey key;
                    if (!txtAdmissionNo.getText().trim().isEmpty()) {
                        key = new AdmissionKey(pt, txtAdmissionNo.getText().trim());
                    } else {
                        key = new AdmissionKey(pt, pt.getAdmissionNo());
                    }
                    ams = (Ams) dao.find(Ams.class,
                            key);
                    if (ams.getBuildingStructure() != null) {
                        params.put("bed_no", ams.getBuildingStructure().getDescription());
                    } else {
                        params.put("bed_no", "-");
                    }

                    if (txtAdmissionNo.getText().isEmpty()) {
                        params.put("adm_no", "-");
                    } else {
                        params.put("adm_no", txtAdmissionNo.getText());
                    }
                    //params.put("adm_no", key.getAmsNo());
                    if (currVou.getInvDate() == null) {
                        params.put("tran_date", DateUtil.toDateTimeStrMYSQL(DateUtil.toDateTime(txtDate.getText())));
                    } else {
                        params.put("tran_date", DateUtil.toDateTimeStrMYSQL(currVou.getInvDate()));
                    }
                    params.put("adm_date", DateUtil.toDateTimeStrMYSQL(ams.getAmsDate()));
                    params.put("pt_name", pt.getPatientName());
                    if (ams.getDoctor() == null) {
                        params.put("dr_name", "");
                    } else {
                        params.put("dr_name", ams.getDoctor().getDoctorName());
                    }
                    /*String toDate = DateUtil.toDateTimeStrMYSQL(currVou.getInvDate());
                if (toDate == null) {
                    toDate = DateUtil.toDateTimeStrMYSQL(DateUtil.toDateTime(txtDate.getText()));
                }*/
                    String period = DateUtil.toDateStr(ams.getAmsDate(), "dd/MM/yyyy HH:mm:ss") + " To "
                            + DateUtil.toDateTime(txtDate.getText());
                    params.put("period", period);
                    if (ams.getBuildingStructure() != null) {
                        params.put("bed_no", ams.getBuildingStructure().getDescription());
                    } else {
                        params.put("bed_no", "-");
                    }

                    if (ams.getTownship() != null) {
                        params.put("address", ams.getTownship().getTownshipName());
                    } else {
                        params.put("address", "-");
                    }

                    if (currVou.getDcStatus() == null) {
                        params.put("dc_status", "");
                    } else {
                        params.put("dc_status", currVou.getDcStatus().getStatusDesp());
                    }

                    /*if (ams.getAge() != null) {
                    params.put("age", ams.getAge() + "Years");
                } else {
                    params.put("age", pt.getAge() + "Years");
                }*/
                    if (pt.getDob() != null) {
                        String dob = DateUtil.toDateStr(pt.getDob(), "dd/MM/yyyy");
                        String strAge = DateUtil.getAge(dob);
                        params.put("age", strAge);
                    } else {
                        params.put("age", "Years");
                    }

                    if (ams.getSex() == null) {
                        params.put("sex", "");
                    } else {
                        params.put("sex", ams.getSex().getDescription());
                    }
                    dao.close();
                    ReportUtil.viewReport(reportPath, params, dao.getConnection());
                    dao.commit();
                } catch (Exception ex) {
                    log.error("package : " + ex.getMessage());
                }
            }
        } else {

        }
    }

    private void linkAmount() {
        try {
            String delSql = "delete from tmp_amount_link where user_id = 'DC-"
                    + Global.machineId + "'";
            String strSql = "INSERT INTO tmp_amount_link(user_id,tran_option,inv_id,amount,print_status)\n"
                    + "  select 'DC-" + Global.machineId + "', tran_source, inv_id, balance, true\n"
                    + "    from (select date(sale_date) tran_date, reg_no cus_id, currency_id currency, "
                    + "                 'Pharmacy' as tran_source, sale_inv_id inv_id,\n"
                    + "                 ifnull(balance,0) balance\n"
                    + "            from sale_his\n"
                    + "   where deleted = false and reg_no = '"
                    + currVou.getPatient().getRegNo() + "' and admission_no = '" + currVou.getAdmissionNo() + "' \n"
                    + "and date(sale_date) = '" + DateUtil.toDateStr(currVou.getInvDate(), "yyyy-MM-dd") + "'\n"
                    + "		   union all\n"
                    + "		  select date(tran_date) tran_date, patient_id cus_id, currency_id currency, \n"
                    + "				 tran_option tran_source, opd_inv_id inv_id, sum(ifnull(vou_balance,0)) balance\n"
                    + "			from v_session_clinic\n"
                    + "		   where tran_option in ('OPD','OT','DC') and deleted = false \n"
                    + "              and patient_id = '" + currVou.getPatient().getRegNo() + "' and admission_no = '" + currVou.getAdmissionNo() + "' \n"
                    + "              and key_id <> 'DC-" + currVou.getOpdInvId() + "' \n"
                    + "		   group by date(tran_date), patient_id, currency_id, tran_option, opd_inv_id) a\n"
                    + "   where tran_date = '" + DateUtil.toDateStr(currVou.getInvDate(), "yyyy-MM-dd")
                    + "'    and cus_id = '" + currVou.getPatient().getRegNo() + "'";

            dao.execSql(delSql, strSql);

            List<TempAmountLink> listTAL = dao.findAllHSQL(
                    "select o from TempAmountLink o where o.key.userId = 'DC-" + Global.machineId + "'");
            tblAmountLinkTableModel.setListTAL(listTAL);
            txtLinkTotal.setValue(tblAmountLinkTableModel.getTotalAmount());
        } catch (Exception ex) {
            log.error("print link amount : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
    }

    private void deleteLinkTemp() {
        try {
            String delSql = "delete from tmp_amount_link where user_id = 'DC-"
                    + Global.machineId + "'";
            dao.execSql(delSql);
        } catch (Exception ex) {
            log.error("deleteLinkTemp : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPatientNo = new javax.swing.JTextField();
        txtPatientName = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDoctorNo = new javax.swing.JTextField();
        txtDoctorName = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        cboDCStatus = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        cboPaymentType = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        txtAdmissionNo = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        cboDiagnosis = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cboAgeRange = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        txtBedNo = new javax.swing.JTextField();
        butAdmit = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblService = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtRecNo = new javax.swing.JTextField();
        txtTotalItem = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblPatientBill = new javax.swing.JTable();
        txtBillTotal = new javax.swing.JFormattedTextField();
        lblTotal = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        txtPkgName = new javax.swing.JTextField();
        butPkgRemove = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        txtPkgPrice = new javax.swing.JFormattedTextField();
        lblAdmissionDate = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        butCheckBill = new javax.swing.JButton();
        butPkgEdit = new javax.swing.JButton();
        butBillDetail = new javax.swing.JButton();
        chkDetails = new javax.swing.JCheckBox();
        chkA5 = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        txtTaxP = new javax.swing.JFormattedTextField();
        txtDiscP = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtVouBalance = new javax.swing.JFormattedTextField();
        txtTaxA = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        txtDiscA = new javax.swing.JFormattedTextField();
        txtVouTotal = new javax.swing.JFormattedTextField();
        txtPaid = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        chkSummary = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAmountLink = new javax.swing.JTable();
        txtLinkTotal = new javax.swing.JFormattedTextField();
        jLabel21 = new javax.swing.JLabel();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No ");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Date ");

        txtDate.setEditable(false);
        txtDate.setFont(Global.textFont);
        txtDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDateMouseClicked(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Patient");

        txtPatientNo.setFont(Global.textFont);
        txtPatientNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPatientNoFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPatientNoFocusLost(evt);
            }
        });
        txtPatientNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPatientNoActionPerformed(evt);
            }
        });

        txtPatientName.setFont(Global.textFont);
        txtPatientName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPatientNameMouseClicked(evt);
            }
        });
        txtPatientName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPatientNameActionPerformed(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Doctor");

        txtDoctorNo.setFont(Global.textFont);
        txtDoctorNo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDoctorNoFocusGained(evt);
            }
        });
        txtDoctorNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDoctorNoActionPerformed(evt);
            }
        });

        txtDoctorName.setEditable(false);
        txtDoctorName.setFont(Global.textFont);
        txtDoctorName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDoctorNameMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(txtPatientNo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPatientName, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDate))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDoctorNo, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDoctorName, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel4});

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDoctorNo, txtPatientNo});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtPatientNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPatientName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtDoctorNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDoctorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Currency");

        cboCurrency.setFont(Global.textFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("DC Status");

        cboDCStatus.setFont(Global.textFont);
        cboDCStatus.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboDCStatusFocusGained(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Payment");

        cboPaymentType.setFont(Global.textFont);
        cboPaymentType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPaymentTypeActionPerformed(evt);
            }
        });

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Adm No.");

        txtAdmissionNo.setEditable(false);
        txtAdmissionNo.setFont(Global.lableFont);

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Diagnosis");

        cboDiagnosis.setFont(Global.textFont);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboPaymentType, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboDCStatus, 0, 126, Short.MAX_VALUE)
                            .addComponent(txtAdmissionNo)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboDiagnosis, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel17, jLabel6, jLabel7});

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(cboDCStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(txtAdmissionNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboPaymentType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(cboDiagnosis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark ");

        txtRemark.setFont(Global.textFont);
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Age");

        cboAgeRange.setFont(Global.textFont);

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Bed No");

        txtBedNo.setEditable(false);

        butAdmit.setText("Admit");
        butAdmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butAdmitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRemark)
                    .addComponent(cboAgeRange, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(txtBedNo, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butAdmit)
                        .addContainerGap())))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel13, jLabel5, jLabel8});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(cboAgeRange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtBedNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butAdmit))
                .addContainerGap())
        );

        tblService.setFont(Global.textFont);
        tblService.setModel(tableModel);
        tblService.setRowHeight(23);
        tblService.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblServiceFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblServiceFocusLost(evt);
            }
        });
        tblService.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblServiceMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblService);

        lblStatus.setFont(new java.awt.Font("Arial", 0, 36)); // NOI18N
        lblStatus.setText("NEW");

        jLabel23.setFont(Global.lableFont);
        jLabel23.setForeground(new java.awt.Color(182, 175, 175));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Rec No : ");

        jLabel22.setFont(Global.lableFont);
        jLabel22.setForeground(new java.awt.Color(182, 175, 175));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Total Item : ");

        txtRecNo.setEditable(false);
        txtRecNo.setForeground(new java.awt.Color(181, 175, 175));
        txtRecNo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecNo.setBorder(null);

        txtTotalItem.setEditable(false);
        txtTotalItem.setForeground(new java.awt.Color(181, 175, 175));
        txtTotalItem.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalItem.setBorder(null);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalItem, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel23)
                .addGap(26, 26, 26)
                .addComponent(txtRecNo, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(txtRecNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtTotalItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        tblPatientBill.setFont(Global.textFont);
        tblPatientBill.setModel(tblPatientBillTableModel);
        tblPatientBill.setRowHeight(23);
        jScrollPane3.setViewportView(tblPatientBill);

        txtBillTotal.setEditable(false);
        txtBillTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBillTotal.setFont(Global.lableFont);

        lblTotal.setFont(Global.lableFont);
        lblTotal.setText("Total Balance : ");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBillTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBillTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal))
                .addContainerGap())
        );

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Package : ");

        txtPkgName.setEditable(false);
        txtPkgName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPkgNameMouseClicked(evt);
            }
        });

        butPkgRemove.setText("-");
        butPkgRemove.setEnabled(false);
        butPkgRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPkgRemoveActionPerformed(evt);
            }
        });

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Price : ");

        txtPkgPrice.setEditable(false);

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Admission Date : ");

        butCheckBill.setText("Check Bill");
        butCheckBill.setEnabled(false);
        butCheckBill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCheckBillActionPerformed(evt);
            }
        });

        butPkgEdit.setFont(Global.lableFont);
        butPkgEdit.setText("Pkg Edit");
        butPkgEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPkgEditActionPerformed(evt);
            }
        });

        butBillDetail.setFont(Global.lableFont);
        butBillDetail.setText("Bill Detail");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAdmissionDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(txtPkgPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(butPkgRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtPkgName)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(butBillDetail)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(butCheckBill)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(butPkgEdit)))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel19, jLabel20});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(lblAdmissionDate, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtPkgName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(butPkgRemove)
                    .addComponent(txtPkgPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butCheckBill)
                    .addComponent(butPkgEdit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butBillDetail)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chkDetails.setFont(Global.lableFont);
        chkDetails.setText("Details");

        chkA5.setFont(Global.lableFont);
        chkA5.setText("A5");
        chkA5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkA5ActionPerformed(evt);
            }
        });

        txtTaxP.setEditable(false);
        txtTaxP.setFont(Global.textFont);

        txtDiscP.setEditable(false);
        txtDiscP.setFont(Global.textFont);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Discount :");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Vou Total :");

        txtVouBalance.setEditable(false);
        txtVouBalance.setFont(Global.textFont);

        txtTaxA.setEditable(false);
        txtTaxA.setFont(Global.textFont);

        jLabel18.setFont(Global.lableFont);
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Paid :");

        txtDiscA.setEditable(false);
        txtDiscA.setFont(Global.textFont);

        txtVouTotal.setEditable(false);
        txtVouTotal.setFont(Global.textFont);

        txtPaid.setEditable(false);
        txtPaid.setFont(Global.textFont);

        jLabel16.setFont(Global.lableFont);
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Vou Balance :");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Tax :");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel15)
                        .addComponent(jLabel18))
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtPaid, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVouTotal, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtTaxP)
                            .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDiscA)
                            .addComponent(txtTaxA, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        jPanel7Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel15, jLabel16, jLabel18, jLabel9});

        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(12, 12, 12)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDiscA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTaxA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addGap(10, 10, 10)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addGap(10, 10, 10)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chkSummary.setFont(Global.lableFont);
        chkSummary.setText("Summary");

        tblAmountLink.setFont(Global.textFont);
        tblAmountLink.setModel(tblAmountLinkTableModel);
        tblAmountLink.setRowHeight(23);
        jScrollPane2.setViewportView(tblAmountLink);

        txtLinkTotal.setEditable(false);
        txtLinkTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Total : ");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtLinkTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLinkTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chkDetails)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(chkA5))
                            .addComponent(chkSummary))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkDetails)
                            .addComponent(chkA5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkSummary))
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDateMouseClicked
        if (evt.getClickCount() == 2) {
            if (!canEdit) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                        "Check Point", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (lblStatus.getText().equals("EDIT") && !txtDate.getText().equals(DateUtil.getTodayDateStr())) {
                if (!Util1.hashPrivilege("DCVoucherEditChange")) {
                    return;
                }
            }

            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtDate.setText(strDate);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.setPeriod(DateUtil.getPeriod(txtDate.getText()));
                    genVouNo();
                }
            }

        }
    }//GEN-LAST:event_txtDateMouseClicked

    private void txtPatientNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPatientNoFocusGained
        focusCtrlName = "txtPatientNo";
        txtPatientNo.selectAll();
    }//GEN-LAST:event_txtPatientNoFocusGained

    private void txtPatientNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPatientNoFocusLost
        //getPatient();
    }//GEN-LAST:event_txtPatientNoFocusLost

    private void txtPatientNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPatientNoActionPerformed
        getPatient();
    }//GEN-LAST:event_txtPatientNoActionPerformed

    private void txtPatientNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPatientNameMouseClicked
        if (evt.getClickCount() == 2) {
            if (!canEdit) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                        "Check Point", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (lblStatus.getText().equals("EDIT") && !txtDate.getText().equals(DateUtil.getTodayDateStr())) {
                if (!Util1.hashPrivilege("DCVoucherEditChange")) {
                    return;
                }
            }
            //PatientSearch dialog = new PatientSearch(dao, this);
            AdmissionSearch dialog = new AdmissionSearch(dao, this);
        }
    }//GEN-LAST:event_txtPatientNameMouseClicked

    private void txtPatientNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPatientNameActionPerformed
        if (!txtPatientName.getText().isEmpty()) {
            if (txtPatientNo.getText().isEmpty()) {
                txtPatientNo.setText(null);
                currVou.setPatient(null);
                txtPatientNo.setEditable(false);
            }
        } else {
            txtPatientNo.setEditable(true);
        }
    }//GEN-LAST:event_txtPatientNameActionPerformed

    private void txtDoctorNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDoctorNoFocusGained
        focusCtrlName = "txtDoctorNo";
        txtDoctorNo.selectAll();
    }//GEN-LAST:event_txtDoctorNoFocusGained

    private void txtDoctorNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDoctorNoActionPerformed
        getDoctor();
    }//GEN-LAST:event_txtDoctorNoActionPerformed

    private void txtDoctorNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDoctorNameMouseClicked
        if (evt.getClickCount() == 2) {
            if (!canEdit) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                        "Check Point", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (lblStatus.getText().equals("EDIT") && !txtDate.getText().equals(DateUtil.getTodayDateStr())) {
                if (!Util1.hashPrivilege("DCVoucherEditChange")) {
                    return;
                }
            }
            DoctorSearchDialog dialog = new DoctorSearchDialog(dao, this);
        }
    }//GEN-LAST:event_txtDoctorNameMouseClicked

    private void cboDCStatusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboDCStatusFocusGained
        focusCtrlName = "cboDCStatus";
    }//GEN-LAST:event_cboDCStatusFocusGained

    private void cboPaymentTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPaymentTypeActionPerformed
        if (cboBindStatus) {
            if (!Util1.getPropValue("system.payment.cash").equals("Y")) {
                PaymentType pt = (PaymentType) cboPaymentType.getSelectedItem();
                double discount = NumberUtil.NZero(txtDiscA.getValue());
                double tax = NumberUtil.NZero(txtTaxA.getValue());
                double vouTotal = NumberUtil.NZero(txtVouTotal.getValue());

                if (pt.getPaymentTypeId() == 1) {
                    txtPaid.setValue((vouTotal + tax) - discount);
                } else {
                    txtPaid.setValue(0);
                }
            }
            calcBalance();
        }
    }//GEN-LAST:event_cboPaymentTypeActionPerformed

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained
        focusCtrlName = "txtRemark";
        txtRemark.selectAll();
    }//GEN-LAST:event_txtRemarkFocusGained

    private void butAdmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butAdmitActionPerformed
        Patient pt = currVou.getPatient();
        if (txtAdmissionNo.getText().isEmpty() && pt != null) {
            try {
                RegNo regNo = new RegNo(dao, "amsNo");
                Ams ams = new Ams();

                ams.getKey().setRegister(pt);
                ams.getKey().setAmsNo(regNo.getRegNo());
                ams.setAmsDate(DateUtil.toDateTime(txtDate.getText()));
                ams.setAddress(pt.getAddress());
                ams.setCity(pt.getCity());
                ams.setContactNo(pt.getContactNo());
                ams.setDob(pt.getDob());
                ams.setDoctor(pt.getDoctor());
                ams.setFatherName(pt.getFatherName());
                ams.setNationality(pt.getNationality());
                ams.setNirc(pt.getNirc());
                ams.setPatientName(pt.getPatientName());
                ams.setReligion(pt.getReligion());
                ams.setSex(pt.getSex());

                dao.save(ams);
                regNo.updateRegNo();
                txtAdmissionNo.setText(ams.getKey().getAmsNo());
                if (Util1.getPropValue("system.admission.paytype").equals("CREDIT")) {
                    cboPaymentType.setSelectedItem(ptCredit);
                } else {
                    cboPaymentType.setSelectedItem(ptCash);
                }
            } catch (Exception ex) {
                log.error("admit : " + ex.getStackTrace()[0].getLineNumber() + " - " + pt.getRegNo() + " - " + ex);
            } finally {
                dao.close();
            }
        }
    }//GEN-LAST:event_butAdmitActionPerformed

    private void tblServiceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblServiceFocusGained
        focusCtrlName = "tblService";
        int selRow = tblService.getSelectedRow();

        if (selRow == -1) {
            //tblService.editCellAt(0, 0);
            tblService.changeSelection(0, 0, false, false);
        }
    }//GEN-LAST:event_tblServiceFocusGained

    private void chkA5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkA5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkA5ActionPerformed

    private void tblServiceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblServiceMouseClicked
        if (evt.getClickCount() == 2) {
            int index = tblService.getSelectedRow();
            index = tblService.convertRowIndexToModel(index);
            tableModel.showDoctorDetail(index);
            /*DCDetailHis dcdHis = tableModel.getOPDDetailHis(index);
            if (dcdHis != null) {
                List<DCDoctorFee> listDrFee = dcdHis.getListDCDF();
                if (listDrFee == null) {
                    listDrFee = dao.findAllHSQL(
                            "select o from DCDoctorFee o where o.dcDetailId = '"
                            + dcdHis.getOpdDetailId() + "' order by o.uniqueId");
                }else if(listDrFee.isEmpty()){
                    listDrFee = dao.findAllHSQL(
                            "select o from DCDoctorFee o where o.dcDetailId = '"
                            + dcdHis.getOpdDetailId() + "' order by o.uniqueId");
                }
                if (listDrFee != null) {
                    if (!listDrFee.isEmpty()) {
                        dcdHis.setListDCDF(listDrFee);
                        InpService service = dcdHis.getService();

                        if (service != null) {
                            if (isNeedDetail(service.getServiceId())) {
                                doctorFeePopup(dcdHis);
                                tableModel.calculateAmount(index);
                                tableModel.fireTableCellUpdated(index, 3);
                                tableModel.fireTableCellUpdated(index, 5);
                            }
                        }
                    }
                }
            }*/
        }
    }//GEN-LAST:event_tblServiceMouseClicked

    private void tblServiceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblServiceFocusLost
        /*try {
            if (tblService.getCellEditor() != null) {
                tblService.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }*/
    }//GEN-LAST:event_tblServiceFocusLost

    private void txtPkgNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPkgNameMouseClicked
        if (evt.getClickCount() == 2) {
            if (txtAdmissionNo.getText() == null) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid admission no.",
                        "Admission No", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (txtAdmissionNo.getText().isEmpty()) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid admission no.",
                        "Admission No", JOptionPane.ERROR_MESSAGE);
                return;
            } else if (cboDCStatus.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid dc status.",
                        "DC Status", JOptionPane.ERROR_MESSAGE);
                return;
            }
            PackageSearchDialog dialog = new PackageSearchDialog(this, "DC");
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_txtPkgNameMouseClicked

    private void butPkgRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPkgRemoveActionPerformed
        if (currVou.getPkgId() != null) {
            currVou.setPkgId(null);
            currVou.setPkgName(null);
            currVou.setPkgPrice(null);
            txtPkgName.setText(null);
            txtPkgPrice.setValue(null);
            try {
                String vouNo = txtVouNo.getText();
                backupPackage(vouNo, "Package-Remove", "PACKAGE-REMOVE");
                String strSqlDelete = "delete from clinic_package_detail_his where dc_inv_no = '" + vouNo + "' and pkg_opt = 'DC'";
                dao.execSql(strSqlDelete);
            } catch (Exception ex) {
                log.error("butPkgRemoveActionPerformed : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }//GEN-LAST:event_butPkgRemoveActionPerformed

    private void butCheckBillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCheckBillActionPerformed
        String reportName = Util1.getPropValue("report.file.dc");
        if (chkDetails.isSelected()) {
            reportName = Util1.getPropValue("system.dc.report.detail");
            reportName = "clinic/" + reportName;
        } else if (chkA5.isSelected()) {
            reportName = "clinic/DCDetailA5";
        }

        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + reportName;

        //For package report
        Long pkgId = currVou.getPkgId();
        if (pkgId != null) {
            reportName = Util1.getPropValue("system.dc.report.package");
            reportPath = Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path")
                    + "clinic/" + reportName;
        }
        //================================

        String imagePath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path");
        String printerName = Util1.getPropValue("report.vou.printer");
        Map<String, Object> params = new HashMap();
        String printMode = Util1.getPropValue("report.vou.printer.mode");

        //params.put("imagePath", imagePath);
        String compName = Util1.getPropValue("report.company.name");
        String phoneNo = Util1.getPropValue("report.phone");
        String address = Util1.getPropValue("report.address");
        params.put("compName", compName);
        params.put("phoneNo", phoneNo);
        params.put("comAddress", address);
        params.put("IMAGE_PATH", imagePath);
        params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));
        params.put("user_id", Global.machineId);

        if (chkDetails.isSelected()) {
            Patient pt = currVou.getPatient();
            params.put("reg_no", pt.getRegNo());

            if (pt != null && !txtAdmissionNo.getText().trim().isEmpty()) {
                try {
                    Ams ams;
                    AdmissionKey key;
                    if (!txtAdmissionNo.getText().trim().isEmpty()) {
                        key = new AdmissionKey(pt, txtAdmissionNo.getText().trim());
                    } else {
                        key = new AdmissionKey(pt, pt.getAdmissionNo());
                    }
                    ams = (Ams) dao.find(Ams.class,
                            key);
                    if (ams.getBuildingStructure() != null) {
                        params.put("bed_no", ams.getBuildingStructure().getDescription());
                    } else {
                        params.put("bed_no", "-");
                    }

                    if (txtAdmissionNo.getText().isEmpty()) {
                        params.put("adm_no", "-");
                    } else {
                        params.put("adm_no", txtAdmissionNo.getText());
                    }
                    //params.put("adm_no", key.getAmsNo());
                    if (currVou.getInvDate() == null) {
                        params.put("tran_date", DateUtil.toDateTimeStrMYSQL(DateUtil.toDateTime(txtDate.getText())));
                    } else {
                        params.put("tran_date", DateUtil.toDateTimeStrMYSQL(currVou.getInvDate()));
                    }
                    params.put("adm_date", DateUtil.toDateTimeStrMYSQL(ams.getAmsDate()));
                    params.put("pt_name", pt.getPatientName());
                    if (ams.getDoctor() == null) {
                        params.put("dr_name", "");
                    } else {
                        params.put("dr_name", ams.getDoctor().getDoctorName());
                    }
                    /*String toDate = DateUtil.toDateTimeStrMYSQL(currVou.getInvDate());
                if (toDate == null) {
                    toDate = DateUtil.toDateTimeStrMYSQL(DateUtil.toDateTime(txtDate.getText()));
                }*/
                    String period = DateUtil.toDateStr(ams.getAmsDate()) + " To "
                            + DateUtil.toDateStr(DateUtil.toDateTime(txtDate.getText()));
                    params.put("period", period);
                    if (ams.getBuildingStructure() != null) {
                        params.put("bed_no", ams.getBuildingStructure().getDescription());
                    } else {
                        params.put("bed_no", "-");
                    }

                    if (ams.getTownship() != null) {
                        params.put("address", ams.getTownship().getTownshipName());
                    } else {
                        params.put("address", "-");
                    }

                    if (currVou.getDcStatus() == null) {
                        params.put("dc_status", "");
                    } else {
                        params.put("dc_status", currVou.getDcStatus().getStatusDesp());
                    }

                    /*if (ams.getAge() != null) {
                    params.put("age", ams.getAge() + "Years");
                } else {
                    params.put("age", pt.getAge() + "Years");
                }*/
                    if (pt.getDob() != null) {
                        String dob = DateUtil.toDateStr(pt.getDob(), "dd/MM/yyyy");
                        String strAge = DateUtil.getAge(dob);
                        params.put("age", strAge);
                    } else {
                        params.put("age", "Years");
                    }

                    if (ams.getSex() == null) {
                        params.put("sex", "");
                    } else {
                        params.put("sex", ams.getSex().getDescription());
                    }
                    dao.close();
                    ReportUtil.viewReport(reportPath, params, dao.getConnection());
                    dao.commit();
                } catch (Exception ex) {
                    log.error("print : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }
        } else {

        }
    }//GEN-LAST:event_butCheckBillActionPerformed

    private void butPkgEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPkgEditActionPerformed
        PackageEditDialog dialog = new PackageEditDialog(
                txtVouNo.getText(),
                txtPatientNo.getText().trim(),
                txtAdmissionNo.getText(),
                currVou.getPkgId(),
                txtPatientName.getText(),
                currVou.getPkgName(),
                currVou.getPkgPrice(),
                "DC"
        );
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        if (dialog.getStatus()) {
            tableModel.removePayItem();
            calcPackageExtraFees(currVou);
            calcPackageGainLost("-");
        }
    }//GEN-LAST:event_butPkgEditActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butAdmit;
    private javax.swing.JButton butBillDetail;
    private javax.swing.JButton butCheckBill;
    private javax.swing.JButton butPkgEdit;
    private javax.swing.JButton butPkgRemove;
    private javax.swing.JComboBox<String> cboAgeRange;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboDCStatus;
    private javax.swing.JComboBox<String> cboDiagnosis;
    private javax.swing.JComboBox cboPaymentType;
    private javax.swing.JCheckBox chkA5;
    private javax.swing.JCheckBox chkDetails;
    private javax.swing.JCheckBox chkSummary;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblAdmissionDate;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblAmountLink;
    private javax.swing.JTable tblPatientBill;
    private javax.swing.JTable tblService;
    private javax.swing.JTextField txtAdmissionNo;
    private javax.swing.JTextField txtBedNo;
    private javax.swing.JFormattedTextField txtBillTotal;
    private javax.swing.JFormattedTextField txtDate;
    private javax.swing.JFormattedTextField txtDiscA;
    private javax.swing.JFormattedTextField txtDiscP;
    private javax.swing.JTextField txtDoctorName;
    private javax.swing.JTextField txtDoctorNo;
    private javax.swing.JFormattedTextField txtLinkTotal;
    private javax.swing.JFormattedTextField txtPaid;
    private javax.swing.JTextField txtPatientName;
    private javax.swing.JTextField txtPatientNo;
    private javax.swing.JTextField txtPkgName;
    private javax.swing.JFormattedTextField txtPkgPrice;
    private javax.swing.JTextField txtRecNo;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtTaxA;
    private javax.swing.JFormattedTextField txtTaxP;
    private javax.swing.JTextField txtTotalItem;
    private javax.swing.JFormattedTextField txtVouBalance;
    private javax.swing.JFormattedTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables
}
