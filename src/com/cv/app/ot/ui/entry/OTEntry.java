/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.entry;

import com.cv.app.common.CalculateObserver;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.RegNo;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.AdmissionKey;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.opd.database.entity.BillOpeningHis;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.entity.PatientBillPayment;
import com.cv.app.opd.database.tempentity.TempAmountLink;
import com.cv.app.opd.ui.entry.OPD;
import com.cv.app.opd.ui.util.AmountLinkDialog;
import com.cv.app.opd.ui.util.DoctorSearchDialog;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.ot.database.entity.OTDetailHis;
import com.cv.app.ot.database.entity.OTDoctorFee;
import com.cv.app.ot.database.entity.OTEntryTranLog;
import com.cv.app.ot.database.entity.OTHis;
import com.cv.app.ot.ui.common.OTTableCellEditor;
import com.cv.app.ot.ui.common.OTTableModel;
import com.cv.app.ot.ui.util.OTVouSearchDialog;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
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
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.swing.event.TableModelListener;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.Timeout;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author winswe
 */
public class OTEntry extends javax.swing.JPanel implements FormAction, KeyPropagate,
        SelectionObserver, KeyListener, CalculateObserver {

    static Logger log = Logger.getLogger(OPD.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private OTHis currVou = new OTHis();
    private OTTableModel tableModel = new OTTableModel(dao);
    private boolean cboBindStatus = false;
    private GenVouNoImpl vouEngine = null;
    private String focusCtrlName = "-";
    private PatientBillTableModel tblPatientBillTableModel = new PatientBillTableModel();
    private PaymentType ptCash;
    private PaymentType ptCredit;
    private boolean isPaid = false;
    private boolean canEdit = true;

    @Override
    public void calculate() {
        String depositeId = Util1.getPropValue("system.ot.deposite.id");
        String discountId = Util1.getPropValue("system.ot.disc.id");
        String paidId = Util1.getPropValue("system.ot.paid.id");
        String refundId = Util1.getPropValue("system.ot.refund.id");
        List<OTDetailHis> listDCDH = tableModel.getListOPDDetailHis();

        double vouTotal = listDCDH.stream().filter(o -> o.getService() != null)
                .filter(o -> o.getService().getServiceId() != null)
                .filter(o -> !o.getService().getServiceId().toString().equals(depositeId)
                && !o.getService().getServiceId().toString().equals(discountId)
                && !o.getService().getServiceId().toString().equals(paidId)
                && !o.getService().getServiceId().toString().equals(refundId))
                .mapToDouble(this::calculateAmount).sum();
        log.info("Vou Total : " + vouTotal);
        txtVouTotal.setValue(vouTotal);

        double paidTotal = listDCDH.stream().filter(o -> o.getService() != null)
                .filter(o -> o.getService().getServiceId() != null)
                .filter(o -> o.getService().getServiceId().toString().equals(depositeId)
                || o.getService().getServiceId().toString().equals(paidId))
                .mapToDouble(this::calculateAmount).sum();
        log.info("Paid : " + paidTotal);
        txtPaid.setValue(paidTotal);

        double refundTotal = listDCDH.stream().filter(o -> o.getService() != null)
                .filter(o -> o.getService().getServiceId() != null)
                .filter(o -> o.getService().getServiceId().toString().equals(refundId))
                .mapToDouble(this::calculateAmount).sum();
        log.info("Refund : " + refundTotal);
        txtPaid.setValue(paidTotal - refundTotal);

        double discTotal = listDCDH.stream().filter(o -> o.getService() != null)
                .filter(o -> o.getService().getServiceId() != null)
                .filter(o -> o.getService().getServiceId().toString().equals(discountId))
                .mapToDouble(this::calculateAmount).sum();
        log.info("Discount : " + discTotal);
        txtDiscA.setValue(discTotal);

        txtTotalItem.setText(Integer.toString((tableModel.getTotalRecord() - 1)));

        calcBalance();
    }

    private double calculateAmount(OTDetailHis record) {
        Double amount = 0d;

        if (record.getChargeType() != null) {
            if (record.getChargeType().getChargeTypeId() == 1) {
                amount = NumberUtil.NZeroInt(record.getQuantity())
                        * NumberUtil.NZero(record.getPrice());
            }
        }

        record.setAmount(amount);
        return amount;
    }

    /**
     * Creates new form OTEntry
     */
    public OTEntry() {
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
            vouEngine = new GenVouNoImpl(dao, "OT",
                    DateUtil.getPeriod(txtDate.getText()));
            ptCash = (PaymentType) dao.find(PaymentType.class,
                    NumberUtil.NZeroInt(Util1.getPropValue("system.paymenttype.cash")));
            ptCredit = (PaymentType) dao.find(PaymentType.class,
                    NumberUtil.NZeroInt(Util1.getPropValue("system.paymenttype.credit")));
            initTable();
            tableModel.setParent(tblService);
            tableModel.setVouStatus("NEW");
            tableModel.addNewRow();
            actionMapping();
            initTextBoxAlign();
            assignDefaultValue();
            initForFocus();
            timerFocus();
        } catch (Exception ex) {
            log.error("OT : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }

        butAdmit.setEnabled(false);
        butAdmit.setVisible(false);
        butOTID.setEnabled(false);
        butOTID.setEnabled(false);
        txtBill.setText(null);
        tableModel.setOtInvId(txtVouNo.getText());
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
        //OTConfirDialog dialog = new OTConfirDialog(currVou);
        //if (dialog.isStatus()) {

        if (isValidEntry()) {
            Date vouSaleDate = DateUtil.toDate(txtDate.getText());
            Date lockDate = PharmacyUtil.getLockDate(dao);
            if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                        + DateUtil.toDateStr(lockDate) + ".",
                        "Locked Data", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Date d = new Date();
                String strVouTotal = NumberUtil.NZero(currVou.getVouTotal()).toString();
                String strDiscA = NumberUtil.NZero(currVou.getDiscountA()).toString();
                String strPaid = NumberUtil.NZero(currVou.getPaid()).toString();
                String strBalance = NumberUtil.NZero(currVou.getVouBalance()).toString();
                dao.execProc("bk_ot",
                        currVou.getOpdInvId(),
                        DateUtil.toDateTimeStrMYSQL(d),
                        Global.loginUser.getUserId(),
                        Global.machineId,
                        strVouTotal,
                        strDiscA,
                        strPaid,
                        strBalance);
            } catch (Exception ex) {
                log.error("bk_ot : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
            } finally {
                dao.close();
            }

            try {
                if (lblStatus.getText().equals("NEW")) {
                    currVou.setCreatedBy(Global.loginUser);
                    currVou.setCreatedDate(new Date());
                } else {
                    currVou.setUpdatedBy(Global.loginUser);
                    currVou.setUpdatedDate(new Date());
                }

                dao.open();
                dao.beginTran();
                String vouNo = currVou.getOpdInvId();
                List<OTDetailHis> listDetail = getVerifiedUniqueId(vouNo, currVou.getListOPDDetailHis());
                for (OTDetailHis odh : listDetail) {
                    odh.setVouNo(vouNo);
                    if (odh.getOpdDetailId() == null) {
                        odh.setOpdDetailId(vouNo + "-" + odh.getUniqueId().toString());
                    }
                    if (odh.getListOTDF() != null) {
                        if (!odh.getListOTDF().isEmpty()) {
                            List<OTDoctorFee> listDF = odh.getListOTDF();
                            Integer maxUniqueId = NumberUtil.NZeroInt(listDF.get(listDF.size() - 1).getUniqueId());
                            for (OTDoctorFee odf : listDF) {
                                if (NumberUtil.NZeroInt(odf.getUniqueId()) == 0) {
                                    odf.setUniqueId(maxUniqueId++);
                                }
                                odf.setOtDetailId(odh.getOpdDetailId());
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

                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }

                deleteDetail();
                //updateVouTotal(currVou.getOpdInvId());

                String desp = "-";
                if (currVou.getPatient() != null) {
                    desp = currVou.getPatient().getRegNo() + "-" + currVou.getPatient().getPatientName();
                }
                /*uploadToAccount(currVou.getOpdInvId(), currVou.isDeleted(),
                        currVou.getVouBalance(), currVou.getDiscountA(),
                        currVou.getPaid(), currVou.getTaxA(), desp);*/
                uploadToAccount(currVou.getOpdInvId());
                //Paid check
                try {
                    //double vouBalance = NumberUtil.NZero(currVou.getVouBalance());
                    //double ttlBill = Double.parseDouble(txtBillTotal.getText());
                    if (chkCloseBill.isSelected()) {
                        Patient pt = currVou.getPatient();
                        BillOpeningHis boh = null;
                        if (pt != null) {
                            if (pt.getOtId() != null) {
                                log.error("Bill Close Save ==> OT Vou No : " + currVou.getOpdInvId()
                                        + " Reg No : " + pt.getRegNo() + " User Id : " + Global.loginUser.getUserId()
                                        + " Bill Id : " + pt.getOtId());
                                boh = (BillOpeningHis) dao.find(BillOpeningHis.class, pt.getOtId());
                                boh.setStatus(false);
                                boh.setBillCLDate(new Date());
                                boh.setCloseBy(Global.loginUser.getUserId());
                            }
                            pt.setOtId(null);
                            dao.save(pt);
                            if (boh != null) {
                                dao.save(boh);
                            }
                        }
                    }
                } catch (Exception ex) {
                    log.error("Paid check : " + ex.getMessage());
                }

                saveTranLog(currVou.getOpdInvId(), "Voucher Save.");
                newForm();
            } catch (Exception ex) {
                dao.rollBack();
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                JOptionPane.showMessageDialog(Util1.getParent(), "Error : " + ex.toString(),
                        "OT Save", JOptionPane.ERROR_MESSAGE);
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public void newForm() {
        isPaid = false;
        currVou = new OTHis();
        canEdit = true;
        chkCloseBill.setSelected(false);
        txtDoctorName.setText(null);
        txtDoctorNo.setText(null);
        txtPatientName.setText(null);
        txtPatientNo.setText(null);
        txtRemark.setText(null);
        txtVouNo.setText(null);
        tableModel.clear();
        tableModel.setVouStatus("NEW");
        txtPatientNo.setEditable(true);
        txtPatientName.setEditable(true);
        txtPatientNo.requestFocusInWindow();
        lblStatus.setText("NEW");
        assignDefaultValue();
        txtBillTotal.setText(null);
        tblPatientBillTableModel.setListPBP(new ArrayList());
        butAdmit.setEnabled(false);
        //txtPatientNo.requestFocus();
        txtBill.setText(null);
        tableModel.setCanEdit(canEdit);
        tableModel.setOtInvId(txtVouNo.getText());
        applySecurityPolicy();
    }

    @Override
    public void history() {
        OTVouSearchDialog dialog = new OTVouSearchDialog(this, "ENTRY");
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
        /*if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot delete edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }*/
        if (lblStatus.getText().equals("EDIT")) {
            if (!Util1.hashPrivilege("OTVoucherDelete")) {
                JOptionPane.showMessageDialog(Util1.getParent(), "You have no permission to delete voucher.",
                        "OT voucher delete", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        if (tableModel.isPayAlready()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Doctor payment already made. You cannot delete this voucher.",
                    "Doctor payment", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (Util1.getNullTo(currVou.isDeleted())) {
            JOptionPane.showConfirmDialog(Util1.getParent(), "Voucher already deleted.",
                    "OT voucher delete", JOptionPane.ERROR);
        } else {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "OT voucher delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                try {
                    Date d = new Date();
                    String strVouTotal = NumberUtil.NZero(currVou.getVouTotal()).toString();
                    String strDiscA = NumberUtil.NZero(currVou.getDiscountA()).toString();
                    String strPaid = NumberUtil.NZero(currVou.getPaid()).toString();
                    String strBalance = NumberUtil.NZero(currVou.getVouBalance()).toString();
                    dao.execProc("bk_ot",
                            currVou.getOpdInvId(),
                            DateUtil.toDateTimeStrMYSQL(d),
                            Global.loginUser.getUserId(),
                            Global.machineId,
                            strVouTotal,
                            strDiscA,
                            strPaid,
                            strBalance);
                } catch (Exception ex) {
                    log.error("bk_ot : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
                } finally {
                    dao.close();
                }

                try {
                    currVou.setDeleted(true);
                    //currVou.setListOPDDetailHis(tableModel.getListOPDDetailHis());
                    //dao.save(currVou);
                    String vouNo = currVou.getOpdInvId();
                    dao.execSql("update ot_his set deleted = true, intg_upd_status = null where ot_inv_id = '" + vouNo + "'");
                    /*uploadToAccount(currVou.getOpdInvId(), currVou.isDeleted(),
                            currVou.getVouBalance(), currVou.getDiscountA(),
                            currVou.getPaid(), currVou.getTaxA(), "");*/
                    uploadToAccount(currVou.getOpdInvId());
                    newForm();
                } catch (Exception ex) {
                    log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                } finally {
                    dao.close();
                }
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
        /*if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot delete and copy edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }*/
        if (lblStatus.getText().equals("EDIT")) {
            if (!Util1.hashPrivilege("OTVoucherDelete")) {
                JOptionPane.showMessageDialog(Util1.getParent(), "You have no permission to delete voucher.",
                        "OT voucher delete", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        if (tableModel.isPayAlready()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Doctor payment already made. You cannot delete this voucher.",
                    "Doctor payment", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete and copy?",
                "OT voucher delete", JOptionPane.YES_NO_OPTION);

        if (yes_no == 0) {
            try {
                Date d = new Date();
                String strVouTotal = NumberUtil.NZero(currVou.getVouTotal()).toString();
                String strDiscA = NumberUtil.NZero(currVou.getDiscountA()).toString();
                String strPaid = NumberUtil.NZero(currVou.getPaid()).toString();
                String strBalance = NumberUtil.NZero(currVou.getVouBalance()).toString();
                dao.execProc("bk_ot",
                        currVou.getOpdInvId(),
                        DateUtil.toDateTimeStrMYSQL(d),
                        Global.loginUser.getUserId(),
                        Global.machineId,
                        strVouTotal,
                        strDiscA,
                        strPaid,
                        strBalance);
            } catch (Exception ex) {
                log.error("bk_ot : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
            } finally {
                dao.close();
            }

            try {
                currVou.setDeleted(true);
                currVou.setListOPDDetailHis(tableModel.getListOPDDetailHis());
                dao.save(currVou);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
                /*uploadToAccount(currVou.getOpdInvId(), currVou.isDeleted(),
                        currVou.getVouBalance(), currVou.getDiscountA(),
                        currVou.getPaid(), currVou.getTaxA(), "");*/
                uploadToAccount(currVou.getOpdInvId());
                copyVoucher(currVou.getOpdInvId());
                genVouNo();
                applySecurityPolicy();
                tableModel.setVouStatus("NEW");
            } catch (Exception ex) {
                log.error("deleteCopy : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    private void copyVoucher(String invNo) {
        try {
            OTHis tmpVou = (OTHis) dao.find(OTHis.class, invNo);

            currVou = new OTHis();
            BeanUtils.copyProperties(tmpVou, currVou);
            List<OTDetailHis> listDetail = new ArrayList();
            List<OTDetailHis> listDetailTmp = tmpVou.getListOPDDetailHis();

            for (OTDetailHis detail : listDetailTmp) {
                OTDetailHis tmpDetail = new OTDetailHis();
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
        if (isValidEntry()) {
            try {
                Date d = new Date();
                String strVouTotal = NumberUtil.NZero(currVou.getVouTotal()).toString();
                String strDiscA = NumberUtil.NZero(currVou.getDiscountA()).toString();
                String strPaid = NumberUtil.NZero(currVou.getPaid()).toString();
                String strBalance = NumberUtil.NZero(currVou.getVouBalance()).toString();
                dao.execProc("bk_ot",
                        currVou.getOpdInvId(),
                        DateUtil.toDateTimeStrMYSQL(d),
                        Global.loginUser.getUserId(),
                        Global.machineId,
                        strVouTotal,
                        strDiscA,
                        strPaid,
                        strBalance);
            } catch (Exception ex) {
                log.error("bk_ot : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
            } finally {
                dao.close();
            }

            Date vouSaleDate = DateUtil.toDate(txtDate.getText());
            Date lockDate = PharmacyUtil.getLockDate(dao);
            boolean isDataLock = false;
            if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                isDataLock = true;
            }

            try {
                if (!isDataLock) {
                    if (lblStatus.getText().equals("NEW")) {
                        currVou.setCreatedBy(Global.loginUser);
                        currVou.setCreatedDate(new Date());
                    } else {
                        currVou.setUpdatedBy(Global.loginUser);
                        currVou.setUpdatedDate(new Date());
                    }
                    if (canEdit) {

                        dao.open();
                        //dao.beginTran();
                        String vouNo = currVou.getOpdInvId();
                        List<OTDetailHis> listDetail = getVerifiedUniqueId(vouNo, currVou.getListOPDDetailHis());
                        for (OTDetailHis odh : listDetail) {
                            odh.setVouNo(vouNo);
                            if (odh.getOpdDetailId() == null) {
                                odh.setOpdDetailId(vouNo + "-" + odh.getUniqueId().toString());
                            }
                            if (odh.getListOTDF() != null) {
                                if (!odh.getListOTDF().isEmpty()) {
                                    List<OTDoctorFee> listDF = odh.getListOTDF();
                                    Integer maxUniqueId = NumberUtil.NZeroInt(listDF.get(listDF.size() - 1).getUniqueId());
                                    for (OTDoctorFee odf : listDF) {
                                        if (NumberUtil.NZeroInt(odf.getUniqueId()) == 0) {
                                            odf.setUniqueId(maxUniqueId++);
                                        }
                                        odf.setOtDetailId(odh.getOpdDetailId());
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
                        //dao.commit();

                        if (lblStatus.getText().equals("NEW")) {
                            vouEngine.updateVouNo();
                        }

                        deleteDetail();
                        //updateVouTotal(currVou.getOpdInvId());

                        String desp = "-";
                        if (currVou.getPatient() != null) {
                            desp = currVou.getPatient().getRegNo() + "-" + currVou.getPatient().getPatientName();
                        }
                        uploadToAccount(currVou.getOpdInvId());

                    }

                    //Paid check
                    try {
                        //double vouBalance = NumberUtil.NZero(currVou.getVouBalance());
                        //double ttlBill = Double.parseDouble(txtBillTotal.getValue().toString());
                        if (chkCloseBill.isSelected()) {
                            Patient pt = currVou.getPatient();
                            if (pt != null) {
                                if (pt.getOtId() != null) {
                                    log.error("Bill Close Print ==> OT Vou No : " + currVou.getOpdInvId()
                                            + " Reg No : " + pt.getRegNo() + " User Id : " + Global.loginUser.getUserId()
                                            + " Bill Id : " + pt.getOtId());
                                }
                                pt.setOtId(null);
                                dao.save(pt);
                            }
                        }
                    } catch (Exception ex) {
                        log.error("Paid check : " + ex.getMessage());
                    }
                }
            } catch (Exception ex) {
                log.error("print : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                JOptionPane.showMessageDialog(Util1.getParent(), "Error cannot print.",
                        "OT print", JOptionPane.ERROR_MESSAGE);
                return;
            } finally {
                dao.close();
            }
        }

        String appCurr = Util1.getPropValue("system.app.currency");
        Map<String, Object> params = new HashMap();
        String ptOpDate = getPatientOpDate(txtPatientNo.getText(),
                DateUtil.toDateStrMYSQL(txtDate.getText()), appCurr);
        params.put("pt_op_date", ptOpDate);
        params.put("tran_date", DateUtil.toDateStrMYSQL(txtDate.getText()));
        //params.put("saleDate", txtDate.getText());
        String reportName = Util1.getPropValue("report.file.ot");
        if (currVou.getOtId() != null) {
            if (!currVou.getOtId().isEmpty()) {
                reportName = Util1.getPropValue("report.file.otbillid");
            }
        }
        /*if (txtBill.getText() != null) {
            reportName = "OTDetailMLZ";
            params.put("bill_id", txtBill.getText().trim());
        }*/
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + "clinic/" + reportName;
        String printerName = Util1.getPropValue("report.vou.printer");

        if (chkVouPrinter.isSelected()) {
            reportName = Util1.getPropValue("report.file.ot.vouprinter");
            reportPath = Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path")
                    + "/" + reportName;
            params.put("link_amt_status", "N");
            params.put("link_amt", 0);
            if (Util1.getPropValue("system.link.amount").equals("OT")
                    && currVou.getPatient() != null) {
                try {
                    String delSql = "delete from tmp_amount_link where user_id = '"
                            + Global.machineId + "'";
                    String strSql = "INSERT INTO tmp_amount_link(user_id,tran_option,inv_id,amount,print_status)\n"
                            + "  select '" + Global.machineId + "', tran_source, inv_id, balance, true\n"
                            + "    from (select date(tran_date) tran_date, cus_id, currency, 'Pharmacy' as tran_source, inv_id, \n"
                            + "                 sum(ifnull(paid,0)) balance\n"
                            + "			from v_session\n"
                            + "		   where ifnull(paid,0) <> 0 and source = 'Sale' and deleted = false\n"
                            + "		   group by date(tran_date), cus_id, currency, source, inv_id\n"
                            + "		   union all\n"
                            + "		  select date(tran_date) tran_date, patient_id cus_id, currency_id currency, \n"
                            + "				 tran_option tran_source, opd_inv_id inv_id, sum(ifnull(paid,0)) balance\n"
                            + "			from v_session_clinic\n"
                            + "		   where ifnull(paid,0) <> 0 and tran_option in ('OPD') and deleted = false\n"
                            + "		   group by date(tran_date), patient_id, currency_id, tran_option, opd_inv_id) a\n"
                            + "   where tran_source <> '" + Util1.getPropValue("system.link.amount")
                            + "'    and tran_date = '" + DateUtil.toDateStr(currVou.getInvDate(), "yyyy-MM-dd")
                            + "'    and cus_id = '" + currVou.getPatient().getRegNo() + "'";

                    dao.execSql(delSql, strSql);

                    List<TempAmountLink> listTAL = dao.findAllHSQL(
                            "select o from TempAmountLink o where o.key.userId = '" + Global.machineId + "'");
                    if (listTAL != null) {
                        if (!listTAL.isEmpty()) {
                            AmountLinkDialog dialog = new AmountLinkDialog(listTAL);
                            dialog.setVisible(true);
                            double ttlLinkAmt = dialog.getTtlAmt();
                            if (ttlLinkAmt != 0) {
                                params.put("link_amt_status", "Y");
                                params.put("link_amt", ttlLinkAmt + currVou.getPaid());
                            }
                        }
                    }
                } catch (Exception ex) {
                    log.error("print link amount : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                } finally {
                    dao.close();
                }

            }
        } else {
            reportPath = Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path") + "clinic/"
                    + reportName;

            if (currVou.getPatient() != null) {
                Patient pt = currVou.getPatient();
                if (pt.getAge() != null) {
                    params.put("age", pt.getAge().toString());
                } else {
                    params.put("age", "");
                }
                if (pt.getSex() != null) {
                    params.put("sex", pt.getSex().getDescription());
                } else {
                    params.put("sex", "");
                }
                params.put("address", pt.getAddress());
            } else {
                params.put("age", "");
                params.put("sex", "");
                params.put("address", "");
            }
        }
        String compName = Util1.getPropValue("report.company.name");
        String printMode = Util1.getPropValue("report.vou.printer.mode");
        String phoneNo = Util1.getPropValue("report.phone");
        params.put("invoiceNo", currVou.getOpdInvId());
        if (currVou.getPatient() != null) {
            params.put("customerName", currVou.getPatient().getPatientName());
            Date regDate = currVou.getPatient().getRegDate();
            Date trgDate = DateUtil.toDate("08/10/2018", "dd/MM/yyyy");

            if (currVou.getPatient().getAge() != null) {
                params.put("age", currVou.getPatient().getAge().toString());
            } else {
                params.put("age", "");
            }
            params.put("pt_name", currVou.getPatient().getPatientName());
            params.put("dr_name", txtDoctorName.getText().trim());
            params.put("address", currVou.getPatient().getAddress());
            if (currVou.getPatient().getSex() != null) {
                params.put("sex", currVou.getPatient().getSex().getDescription());
            } else {
                params.put("sex", "");
            }
            if (regDate.before(trgDate)) {
                Integer year = regDate.getYear();
                params.put("reg_no", currVou.getPatient().getRegNo() + "/" + year);
            } else {
                params.put("reg_no", currVou.getPatient().getRegNo());
            }
        } else {
            if (currVou.getPatientName() != null) {
                params.put("customerName", currVou.getPatientName());
            } else {
                params.put("customerName", "");
            }
            params.put("reg_no", "");

            params.put("age", "");
            params.put("pt_name", "");
            params.put("dr_name", "");
            params.put("address", "");
            params.put("sex", "");
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
        params.put("compName", compName);
        params.put("compAddress", Util1.getPropValue("report.address"));
        params.put("phoneNo", phoneNo);
        params.put("remark", txtRemark.getText());
        params.put("user_id", Global.machineId);
        params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));
        params.put("category", Util1.getPropValue("report.company.cat"));
        if (txtBill.getText() == null) {
            params.put("bill_id", "-");
        } else if (txtBill.getText().isEmpty()) {
            params.put("bill_id", "-");
        } else {
            params.put("bill_id", txtBill.getText());
        }

        if (lblStatus.getText().equals("NEW")) {
            params.put("vou_status", " ");
        } else {
            params.put("vou_status", lblStatus.getText());
        }
        params.put("IMAGE_PATH", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));

        if (chkVouPrinter.isSelected()) {
            if (printMode.equals("View")) {
                ReportUtil.viewReport(reportPath, params, dao.getConnection());
            } else {
                JasperPrint jp = ReportUtil.getReport(reportPath, params, dao.getConnection());
                int count = Util1.getIntegerOne(Util1.getPropValue("system.ot.print.count"));
                for (int i = 0; i < count; i++) {
                    ReportUtil.printJasper(jp, printerName);
                }
            }
        } else {
            ReportUtil.viewReport(reportPath, params, dao.getConnection());
        }

        saveTranLog(currVou.getOpdInvId(), "Voucher Print.");
        newForm();
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
                doctor = (Doctor) dao.find(Doctor.class, doctor.getDoctorId());
                currVou.setDoctor(doctor);
                txtDoctorNo.setText(doctor.getDoctorId());
                txtDoctorName.setText((doctor.getDoctorName()));
                txtVouTotal.setValue(tableModel.getTotal());
                calcBalance();
                tblService.requestFocus();
            } catch (Exception ex) {
                log.error("selected : " + ex.getMessage());
            } finally {
                dao.close();
            }
            break;
            case "PatientSearch":
                txtAdmissionNo.setText(null);
                txtBill.setText(null);
                butAdmit.setEnabled(true);
                Patient patient = (Patient) selectObj;
                currVou.setPatient(patient);
                currVou.setAdmissionNo(patient.getAdmissionNo());
                txtAdmissionNo.setText(patient.getAdmissionNo());
                txtPatientNo.setText(patient.getRegNo());
                txtPatientName.setText(patient.getPatientName());
                txtPatientName.setEditable(false);
                if (!Util1.isNullOrEmpty(patient.getAdmissionNo())) {
                    cboPaymentType.setSelectedItem(ptCredit);
                    butAdmit.setEnabled(false);
                    if (Util1.getPropValue("system.admission.paytype").equals("CASH")) {
                        cboPaymentType.setSelectedItem(ptCash);
                    }
                } else if (!Util1.isNullOrEmpty(patient.getOtId())) {
                    cboPaymentType.setSelectedItem(ptCredit);
                    txtBill.setText(patient.getOtId());
                } else {
                    cboPaymentType.setSelectedItem(ptCash);
                }
                if (patient.getOtId() != null) {
                    butOTID.setEnabled(false);
                    txtBill.setText(patient.getOtId());
                } else {
                    butOTID.setEnabled(true);
                    txtBill.setText(null);
                }
                if (patient.getDoctor() != null) {
                    selected("DoctorSearch", patient.getDoctor());
                }
                txtDoctorNo.requestFocus();
                getPatientBill(patient.getRegNo());
                break;
            case "OTVouList":
                try {
                newForm();
                VoucherSearch vs = (VoucherSearch) selectObj;
                String vouId = vs.getInvNo();
                try {
                    currVou = (OTHis) dao.find(OTHis.class, vouId);
                    List<OTDetailHis> listDetail = dao.findAllHSQL(
                            "select o from OTDetailHis o where o.vouNo = '" + vouId + "' order by o.uniqueId");
                    currVou.setListOPDDetailHis(listDetail);
                } catch (Exception ex) {
                    log.error("OTVouList : " + ex.toString());
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
                txtAdmissionNo.setText(currVou.getAdmissionNo());

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
                if (currVou.getPaid() != 0) {
                    isPaid = true;
                }
                txtVouBalance.setValue(currVou.getVouBalance());
                setEditStatus(currVou.getOpdInvId());
                applySecurityPolicy();
                tableModel.setCanEdit(canEdit);
                tableModel.setVouStatus("EDIT");
                txtBill.setText(currVou.getOtId());
                if (txtBill.getText() == null) {
                    butOTID.setEnabled(true);
                } else if (!txtBill.getText().isEmpty()) {
                    butOTID.setEnabled(false);
                } else {
                    butOTID.setEnabled(true);
                }
                tableModel.setOtInvId(txtVouNo.getText());
            } catch (Exception ex) {
                log.error("OTVouList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
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
                    cboPaymentType.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("cboPaymentType")) {
                    if (!cboPaymentType.isPopupVisible()) {
                        txtRemark.requestFocus();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtRemark")) {
                    tblService.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtDoctorNo")) {
                    txtPatientNo.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtRemark")) {
                    cboPaymentType.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("cboPaymentType")) {
                    if (!cboPaymentType.isPopupVisible()) {
                        txtDoctorNo.requestFocus();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("tblService")) {
                    int selRow = tblService.getSelectedRow();
                    if (selRow == -1 || selRow == 0) {
                        txtRemark.requestFocus();
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
        tblService.addKeyListener(this);
        txtRemark.addKeyListener(this);

        cboPaymentType.getEditor().getEditorComponent().addKeyListener(this);
        cboPaymentType.getEditor().getEditorComponent().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cboPaymentType.requestFocus();
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
    }

    private void genVouNo() {
        String vouNo = vouEngine.getVouNo();
        txtVouNo.setText(vouNo);

        try {
            List<OTHis> listOTH = dao.findAllHSQL(
                    "select o from OTHis o where o.opdInvId = '" + vouNo + "'");
            if (listOTH != null) {
                if (!listOTH.isEmpty()) {
                    vouEngine.updateVouNo();
                    vouNo = vouEngine.getVouNo();
                    txtVouNo.setText(vouNo);
                    listOTH = null;
                    listOTH = dao.findAllHSQL(
                            "select o from OTHis o where o.opdInvId = '" + vouNo + "'");
                    if (listOTH != null) {
                        if (!listOTH.isEmpty()) {
                            log.error("Duplicate ot vour error : " + txtVouNo.getText() + " @ "
                                    + txtDate.getText());
                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Duplicate OT vou no. Exit the program and try again.",
                                    "OT Vou No", JOptionPane.ERROR_MESSAGE);
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
                        + txtDoctorNo.getText().trim() + "'  and o.active = true");
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
        double discount = NumberUtil.NZero(txtDiscA.getValue());
        double tax = NumberUtil.NZero(txtTaxA.getValue());
        double vouTotal = NumberUtil.NZero(txtVouTotal.getValue());
        if (Util1.getPropValue("system.app.allCash").equals("Y")) {
            PaymentType pt = (PaymentType) cboPaymentType.getSelectedItem();
            if (pt.getPaymentTypeId() == 1) {
                txtPaid.setValue((vouTotal + tax) - discount);
            } else {
                txtPaid.setValue(0);
            }
        }
        double paid = NumberUtil.NZero(txtPaid.getValue());
        txtVouBalance.setValue((vouTotal + tax) - (discount + paid));
    }

    private void getPatient() {
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (txtPatientNo.getText() != null && !txtPatientNo.getText().isEmpty()) {
            try {
                Patient pt;

                dao.open();
                pt = (Patient) dao.find(Patient.class, txtPatientNo.getText());
                dao.close();

                if (pt == null) {
                    txtPatientNo.setText(null);
                    txtPatientName.setText(null);
                    currVou.setPatient(null);
                    butOTID.setEnabled(true);
                    txtBill.setText(null);

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
            butOTID.setEnabled(true);
            txtBill.setText(null);
        }
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboPaymentType, dao.findAll("PaymentType"));
            BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));

            new ComBoBoxAutoComplete(cboPaymentType, this);
            new ComBoBoxAutoComplete(cboCurrency, this);

            cboBindStatus = true;
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        try {
            tableModel.setCalObserver(this);
            if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
                tblService.setCellSelectionEnabled(true);
            }
            tblService.getTableHeader().setFont(Global.lableFont);
            tblService.setFont(Global.textFont);
            tblService.setRowHeight(Global.rowHeight);
            //Adjust column width
            tblService.getColumnModel().getColumn(0).setPreferredWidth(40);//Code
            tblService.getColumnModel().getColumn(1).setPreferredWidth(300);//Description
            tblService.getColumnModel().getColumn(2).setPreferredWidth(20);//Qty
            tblService.getColumnModel().getColumn(3).setPreferredWidth(60);//Fees
            tblService.getColumnModel().getColumn(4).setPreferredWidth(50);//Charge Type
            tblService.getColumnModel().getColumn(5).setPreferredWidth(80);//Amount

            //Change JTable cell editor
            tblService.getColumnModel().getColumn(0).setCellEditor(
                    new OTTableCellEditor(dao));
            tblService.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor());
            tblService.getColumnModel().getColumn(3).setCellEditor(new BestTableCellEditor());

            JComboBox cboChargeType = new JComboBox();
            BindingUtil.BindCombo(cboChargeType, dao.findAll("ChargeType"));
            tblService.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cboChargeType));

            tblService.getModel().addTableModelListener((TableModelEvent e) -> {
                /*String depositeId = Util1.getPropValue("system.ot.deposite.id");
                String discountId = Util1.getPropValue("system.ot.disc.id");
                String paidId = Util1.getPropValue("system.ot.paid.id");
                String refundId = Util1.getPropValue("system.ot.refund.id");
                List<OTDetailHis> listDCDH = tableModel.getListOPDDetailHis();
                QueryResults qr;
                Query q = new Query();
                String strSql = "SELECT * FROM com.cv.app.ot.database.entity.OTDetailHis"
                + " WHERE service IS NOT NULL "
                + "EXECUTE ON ALL sum(amount) AS total";
                String strFilter = "SELECT * FROM com.cv.app.ot.database.entity.OTDetailHis WHERE "
                + "service.serviceId not in (" + depositeId + ","
                + discountId + "," + paidId + "," + refundId + ")";
                try {
                q.parse(strSql);
                qr = q.execute(JoSQLUtil.getResult(strFilter, listDCDH));
                double vTotal = Double.parseDouble(qr.getSaveValue("total").toString());
                txtVouTotal.setValue(vTotal);
                strFilter = "SELECT * FROM com.cv.app.ot.database.entity.OTDetailHis WHERE "
                + "service.serviceId in (" + depositeId + "," + paidId + ")";
                qr = q.execute(JoSQLUtil.getResult(strFilter, listDCDH));
                double vTotalPaid = Double.parseDouble(qr.getSaveValue("total").toString());
                txtPaid.setValue(vTotalPaid);
                isPaid = vTotalPaid != 0;
                strFilter = "SELECT * FROM com.cv.app.ot.database.entity.OTDetailHis WHERE "
                + "service.serviceId in (" + refundId + ")";
                qr = q.execute(JoSQLUtil.getResult(strFilter, listDCDH));
                double vTotalRefund = Double.parseDouble(qr.getSaveValue("total").toString());
                txtPaid.setValue(vTotalPaid - vTotalRefund);
                strFilter = "SELECT * FROM com.cv.app.ot.database.entity.OTDetailHis WHERE "
                + "service.serviceId in (" + discountId + ")";
                qr = q.execute(JoSQLUtil.getResult(strFilter, listDCDH));
                double vTotalDiscount = Double.parseDouble(qr.getSaveValue("total").toString());
                txtDiscA.setValue(vTotalDiscount);
                } catch (QueryParseException qpe) {
                log.info("JoSQLUtil.isAlreadyHave qpe: " + qpe.toString());
                } catch (QueryExecutionException | NumberFormatException ex) {
                log.info("JoSQLUtil.isAlreadyHave : " + ex.toString());
                }
                txtTotalItem.setText(Integer.toString((tableModel.getTotalRecord() - 1)));
                calcBalance();*/
            });

            tblService.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblService.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
                txtRecNo.setText(Integer.toString(tblService.getSelectedRow() + 1));
            });

            tblPatientBill.getColumnModel().getColumn(0).setPreferredWidth(180);//Bill Name
            tblPatientBill.getColumnModel().getColumn(1).setPreferredWidth(70);//Amount
            tblPatientBill.getTableHeader().setFont(Global.lableFont);
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
        formActionKeyMapping(cboCurrency);
        formActionKeyMapping(cboPaymentType);
    }

    private boolean isValidEntry() {
        boolean status = true;
        Patient pt = currVou.getPatient();
        String admissionNo = Util1.isNull(txtAdmissionNo.getText(), "-");

        if (!admissionNo.equals("-")) {
            AdmissionKey key = new AdmissionKey();
            key.setAmsNo(admissionNo);
            key.setRegister(pt);
            try {
                Ams adm = (Ams) dao.find(Ams.class, key);
                if (adm == null) {
                    status = false;
                } else {
                    Date vouDate = DateUtil.toDate(txtDate.getText());
                    Date admDate = DateUtil.toDate(DateUtil.toDateStr(adm.getAmsDate(), "dd/MM/yyyy"));
                    Date dcDate = adm.getDcDateTime();

                    if (vouDate.compareTo(admDate) < 0) {
                        status = false;
                    }

                    if (dcDate != null) {
                        if (vouDate.compareTo(dcDate) > 0) {
                            status = false;
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("admission error : " + " " + admissionNo + " " + ex.getMessage());
                status = false;
            } finally {
                dao.close();
            }

            if (!status) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Check voud date with admission date.",
                        "Invalid Vou Date", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        if (!Util1.hashPrivilege("CanEditOTCheckPoint")) {
            if (lblStatus.getText().equals("NEW")) {
                try {
                    long cnt = dao.getRowCount("select count(*) from c_bk_ot_his where ot_date >= '"
                            + DateUtil.toDateStrMYSQL(txtDate.getText()) + "'");
                    if (cnt > 0) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot entry voucher in back date.",
                                "Check Point", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                } catch (HeadlessException ex) {
                    log.error("isValidEntry : " + ex.toString());
                }
            }
        }

        if (tblService.getCellEditor() != null) {
            tblService.getCellEditor().stopCellEditing();
        }

        double vouTtl = NumberUtil.NZero(txtVouTotal.getValue());
        double modelTotal = tableModel.getTotal();

        if (vouTtl != modelTotal) {
            log.error(txtVouNo.getText().trim() + " OT Voucher Total Error : vouTtl : "
                    + vouTtl + " modelTtl : " + modelTotal);
            JOptionPane.showMessageDialog(Util1.getParent(), "Please check voucher total.",
                    "Voucher Total Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        txtVouTotal.setValue(modelTotal);
        calcBalance();
        double vouBalance = NumberUtil.NZero(txtVouBalance.getText());

        if (!DateUtil.isValidDate(txtDate.getText())) {
            log.error("OT date error : " + txtVouNo.getText());
            status = false;
        } else if (txtVouNo.getText().trim().length() < 15) {
            log.error("OT vou error : " + txtVouNo.getText() + " - " + txtDate.getText());
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid vou no.",
                    "Vou No.", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (!tableModel.isValidEntry()) {
            status = false;
        } else if (vouBalance != 0 && currVou.getPatient() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid registeration number.",
                    "Reg No", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtPatientNo.requestFocusInWindow();
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
            currVou.setPatientName(txtPatientName.getText());
            currVou.setAdmissionNo(txtAdmissionNo.getText());
            if (lblStatus.getText().equals("EDIT") || lblStatus.getText().equals("DELETED")) {
                currVou.setUpdatedBy(Global.loginUser);
                currVou.setUpdatedDate(new Date());
            } else if (lblStatus.getText().equals("NEW")) {
                currVou.setCreatedBy(Global.loginUser);
                currVou.setDeleted(false);
                currVou.setSession(Global.sessionId);
                currVou.setCreatedDate(new Date());
            }

            if (txtBill.getText() == null) {
                currVou.setOtId(null);
            } else if (txtBill.getText().isEmpty()) {
                currVou.setOtId(null);
            } else {
                currVou.setOtId(txtBill.getText());
            }
        }

        return status;
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
            int row = tblService.getSelectedRow();

            if (row >= 0) {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                        "Are you sure to delete?",
                        "OT item delete", JOptionPane.YES_NO_OPTION);
                if (yes_no == 0) {
                    OTDetailHis opdh = tableModel.getOPDDetailHis(row);

                    if (opdh.getService() != null) {
                        try {
                            if (tblService.getCellEditor() != null) {
                                tblService.getCellEditor().stopCellEditing();
                            }
                        } catch (Exception ex) {
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

        txtVouTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtDiscA.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtDiscP.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTaxA.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTaxP.setFormatterFactory(NumberUtil.getDecimalFormat());
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
        txtAdmissionNo.setText(null);
        cboPaymentType.setSelectedIndex(0);
        vouEngine.setPeriod(DateUtil.getPeriod(txtDate.getText()));
        cboPaymentType.setSelectedItem(ptCash);
        genVouNo();
        tableModel.setOtInvId(txtVouNo.getText());
    }

    private Action actionTblServiceEnterKey = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblService.getCellEditor() != null) {
                    tblService.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            int row = tblService.getSelectedRow();
            int col = tblService.getSelectedColumn();

            OTDetailHis record = tableModel.getOPDDetailHis(row);
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

    public void getPatientBill(String regNo) {
        try {
            List<PatientBillPayment> listPBP = new ArrayList();
            Double totalBalance = 0.0;
            String currency = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();

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

    private String getPatientOpDate(String regNo, String tranDate, String currency) {
        String strSql = "select a.reg_no, a.cur_code, a.payment_type_id, ifnull(max(op_date), '1900-01-01') op_date\n"
                + "    from (select pd.reg_no, cur.cur_code, pt.payment_type_id from patient_detail pd, currency cur, payment_type pt \n"
                + "    where pd.reg_no = '" + regNo + "') a \n"
                + "    left join (select * from patient_op po where reg_no = '" + regNo + "' and date(op_date) <= '" + tranDate + "' \n"
                + "    and (currency = '" + currency + "')) po \n"
                + "      on a.reg_no = po.reg_no and a.cur_code = po.currency and a.payment_type_id = po.bill_type\n"
                + "   where a.reg_no = '" + regNo + "' and (a.cur_code = '" + currency + "')\n"
                + "   group by a.reg_no, a.cur_code, a.payment_type_id";
        String strOpDate = "1900-01-01";

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                if (rs.next()) {
                    strOpDate = DateUtil.toDateStr(rs.getDate("op_date"), "yyyy-MM-dd");
                }
                rs.close();
            }
        } catch (Exception ex) {
            log.error("getPatientOpDate : " + ex.getMessage());
        } finally {

        }

        return strOpDate;
    }

    private void setEditStatus(String invId) {
        //canEdit
        /*List<SessionCheckCheckpoint> list = dao.findAllHSQL(
                "select o from SessionCheckCheckpoint o where o.tranOption = 'OT' "
                + " and o.tranInvId = '" + invId + "'");*/

        canEdit = Util1.hashPrivilege("OTVoucherEditChange");
        boolean isAllowEdit = Util1.hashPrivilege("OTCreditVoucherEdit");
        double vouPaid = NumberUtil.NZero(currVou.getPaid());

        if (!canEdit) {
            if (!isAllowEdit) {
                return;
            }
        }

        if (!Util1.hashPrivilege("CanEditOTCheckPoint")) {
            if (currVou != null) {
                if (currVou.getAdmissionNo() != null) {
                    if (!currVou.getAdmissionNo().trim().isEmpty()) {
                        AdmissionKey key = new AdmissionKey();
                        key.setRegister(currVou.getPatient());
                        key.setAmsNo(currVou.getAdmissionNo());
                        try {
                            Ams admPt = (Ams) dao.find(Ams.class, key);
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
                                "select * from c_bk_ot_his where ot_inv_id = '" + invId + "'");
                        if (list != null) {
                            canEdit = list.isEmpty();
                        } else {
                            canEdit = true;
                        }
                    } catch (Exception ex) {
                        log.error("setEditStatus Check BK data : " + invId + " : " + ex.toString());
                    } finally {
                        dao.close();
                    }
                }
            }
        } else {
            canEdit = true;
        }
    }

    private void uploadToAccount(String vouNo) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            String rootUrl = Util1.getPropValue("system.intg.api.url");
            if (!rootUrl.isEmpty() && !rootUrl.equals("-")) {
                try ( CloseableHttpClient httpClient = createHttpClientWithTimeouts()) {
                    String url = rootUrl + "/ot";
                    final HttpPost request = new HttpPost(url);
                    final List<NameValuePair> params = new ArrayList();
                    params.add(new BasicNameValuePair("vouNo", vouNo));
                    request.setEntity(new UrlEncodedFormEntity(params));
                    CloseableHttpResponse response = httpClient.execute(request);
                    log.info(url + response.toString());
                } catch (IOException e) {
                    log.error("uploadToAccount : " + e.getMessage());
                    updateNull(vouNo);
                }
            }
        } else {
            updateNull(vouNo);
        }
    }

    private void updateNull(String vouNo) {
        try {
            dao.execSql("update ot_his set intg_upd_status = null where ot_inv_id = '" + vouNo + "'");
        } catch (Exception ex) {
            log.error("uploadToAccount error : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private CloseableHttpClient createHttpClientWithTimeouts() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ONE_MILLISECOND)
                .build();
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    private void deleteDetail() {
        String deleteSQL = "delete from ot_doctor_fee where ot_detail_id in ("
                + tableModel.getDeletedList() + ")";
        try {
            if (deleteSQL != null) {
                dao.execSql(deleteSQL);
            }

            deleteSQL = tableModel.getDeleteSql();
            if (deleteSQL != null) {
                dao.execSql(deleteSQL);
            }

            String deleteDrId = tableModel.getDelDrFee();
            if (!deleteDrId.isEmpty()) {
                dao.execSql("delete from ot_doctor_fee where ot_detail_id in ("
                        + deleteDrId + ")");
            }
        } catch (Exception ex) {
            log.error("deleteDetail : " + ex.toString());
            dao.rollBack();
        } finally {
            dao.close();
        }
    }

    private void saveTranLog(String vouNo, String tranDesp) {
        if (Util1.getPropValue("system.ot.tran.log").equals("Y")) {
            try {
                OTEntryTranLog otetl = new OTEntryTranLog();
                otetl.setTranDesp(tranDesp);
                otetl.setVouNo(vouNo);
                otetl.setTranDate(new Date());
                otetl.setUserId(Global.loginUser.getUserId());
                dao.save(otetl);
                log.info("saveTranLog : Save");
            } catch (Exception ex) {
                log.error("saveTranLog : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    private void updateVouTotal(String vouNo) {
        String strSql = "update ot_his ph\n"
                + "join (select vou_no, sum(ifnull(amount,0)) as ttl_amt \n"
                + "from ot_details_his where vou_no = '" + vouNo + "' \n"
                + " and service_id not in \n"
                + "(select sys_prop_value from sys_prop where sys_prop_desp in \n"
                + "('system.ot.deposite.id','system.ot.disc.id','system.ot.paid.id','system.ot.refund.id'))"
                + " group by vou_no) pd \n"
                + "on ph.ot_inv_id = pd.vou_no set ph.vou_total = pd.ttl_amt\n"
                + "where ph.ot_inv_id = '" + vouNo + "'";
        try {
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("updateVouTotal : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private List<OTDetailHis> getVerifiedUniqueId(String vouNo, List<OTDetailHis> listDetail) {
        if (listDetail == null) {
            return null;
        }

        OTDetailHis ddh = listDetail.stream().filter(o -> NumberUtil.NZeroInt(o.getUniqueId()) != 0)
                .max(Comparator.comparingInt(OTDetailHis::getUniqueId))
                .orElse(null);
        int maxId = 0;
        if (ddh != null) {
            maxId = ddh.getUniqueId();
        }

        HashMap<Integer, OTDetailHis> hm = new HashMap();
        for (OTDetailHis tmp : listDetail) {
            if (NumberUtil.NZeroInt(tmp.getUniqueId()) != 0) {
                if (hm.containsKey(tmp.getUniqueId())) {
                    log.error("OT Unique ID Error : " + tmp.getUniqueId());
                    maxId++;
                    tmp.setUniqueId(maxId);
                    tmp.setOpdDetailId(vouNo + "-" + tmp.getUniqueId().toString());
                }
                hm.put(tmp.getUniqueId(), tmp);
            }

            if (NumberUtil.NZeroInt(tmp.getUniqueId()) == 0) {
                tmp.setUniqueId(maxId++);
            }
        }

        return listDetail;
    }

    private void openBill() {
        try {
            Patient pt = (Patient) dao.find(Patient.class, txtPatientNo.getText().trim());
            if (pt != null) {
                if (pt.getOtId() == null) {
                    RegNo regNo = new RegNo(dao, "OT-ID");
                    pt.setOtId(regNo.getRegNo());
                    dao.save(pt);
                    regNo.updateRegNo();
                    txtBill.setText(pt.getOtId());
                    cboPaymentType.setSelectedItem(ptCredit);
                    butOTID.setEnabled(false);

                    BillOpeningHis boh = new BillOpeningHis();
                    boh.setAdmNo(pt.getAdmissionNo());
                    boh.setBillId(pt.getOtId());
                    boh.setBillOPDate(new Date());
                    boh.setOpenBy(Global.loginUser.getUserId());
                    boh.setRegNo(pt.getRegNo());
                    boh.setStatus(true);
                    dao.save(boh);
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Patient is already opened bill.",
                            "Bill Id", JOptionPane.ERROR_MESSAGE);
                    txtBill.setText(pt.getOtId());
                    butOTID.setEnabled(false);
                }
            } else {
                log.error("openBill : Invalid registration number :" + txtPatientNo.getText().trim());
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid registration number.",
                        "Invalid Patient", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("openBill : " + txtPatientNo.getText().trim() + " : " + ex.getMessage());
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

        txtVouTotal = new javax.swing.JFormattedTextField();
        txtDiscA = new javax.swing.JFormattedTextField();
        txtTaxA = new javax.swing.JFormattedTextField();
        jLabel12 = new javax.swing.JLabel();
        txtPaid = new javax.swing.JFormattedTextField();
        txtAdmissionNo = new javax.swing.JTextField();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtDiscP = new javax.swing.JFormattedTextField();
        txtTaxP = new javax.swing.JFormattedTextField();
        txtDate = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtDoctorNo = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtDoctorName = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtRecNo = new javax.swing.JTextField();
        txtTotalItem = new javax.swing.JTextField();
        chkVouPrinter = new javax.swing.JCheckBox();
        chkCloseBill = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox();
        cboPaymentType = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPatientNo = new javax.swing.JTextField();
        txtPatientName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPatientBill = new javax.swing.JTable();
        txtBillTotal = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        butAdmit = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblService = new javax.swing.JTable();
        butOTID = new javax.swing.JButton();
        txtBill = new javax.swing.JTextField();

        txtVouTotal.setEditable(false);
        txtVouTotal.setFont(Global.textFont);

        txtDiscA.setEditable(false);
        txtDiscA.setFont(Global.textFont);

        txtTaxA.setEditable(false);
        txtTaxA.setFont(Global.textFont);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Adm No.");

        txtPaid.setEditable(false);
        txtPaid.setFont(Global.textFont);

        txtAdmissionNo.setEditable(false);
        txtAdmissionNo.setFont(Global.lableFont);

        txtVouBalance.setEditable(false);
        txtVouBalance.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Vou Total :");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Discount :");

        txtDiscP.setEditable(false);
        txtDiscP.setFont(Global.textFont);

        txtTaxP.setEditable(false);
        txtTaxP.setFont(Global.textFont);

        txtDate.setEditable(false);
        txtDate.setFont(Global.textFont);
        txtDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDateMouseClicked(evt);
            }
        });

        jLabel14.setFont(Global.lableFont);
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Tax :");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Doctor");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Paid :");

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

        jLabel16.setFont(Global.lableFont);
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Vou Balance :");

        txtDoctorName.setEditable(false);
        txtDoctorName.setFont(Global.textFont);
        txtDoctorName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDoctorNameMouseClicked(evt);
            }
        });

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

        chkVouPrinter.setFont(Global.lableFont);
        chkVouPrinter.setText("Vou Printer");

        chkCloseBill.setFont(Global.lableFont);
        chkCloseBill.setText("Close Bill");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalItem, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addGap(18, 18, 18)
                        .addComponent(txtRecNo, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkVouPrinter)
                    .addComponent(chkCloseBill))
                .addGap(8, 29, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(txtRecNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtTotalItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkVouPrinter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCloseBill)
                .addGap(0, 6, Short.MAX_VALUE))
        );

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark ");

        txtRemark.setFont(Global.textFont);
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Currency");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Payment");

        cboCurrency.setFont(Global.textFont);
        cboCurrency.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboCurrencyFocusGained(evt);
            }
        });

        cboPaymentType.setFont(Global.textFont);
        cboPaymentType.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboPaymentTypeFocusGained(evt);
            }
        });
        cboPaymentType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPaymentTypeActionPerformed(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No ");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

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
        txtPatientName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPatientNameFocusGained(evt);
            }
        });
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

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Date ");

        tblPatientBill.setFont(Global.textFont);
        tblPatientBill.setModel(tblPatientBillTableModel);
        tblPatientBill.setRowHeight(23);
        jScrollPane2.setViewportView(tblPatientBill);

        txtBillTotal.setEditable(false);
        txtBillTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBillTotal.setFont(Global.textFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Total :");

        butAdmit.setFont(Global.lableFont);
        butAdmit.setText("Admit");
        butAdmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butAdmitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 338, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(butAdmit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBillTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtBillTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11)
                        .addComponent(butAdmit))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
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

        butOTID.setFont(Global.lableFont);
        butOTID.setText("Bill ID");
        butOTID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOTIDActionPerformed(evt);
            }
        });

        txtBill.setEditable(false);
        txtBill.setFont(Global.lableFont);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtPatientNo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPatientName, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtDoctorNo, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDoctorName, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(butOTID)
                                .addContainerGap(214, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtRemark, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtBill, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cboPaymentType, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtAdmissionNo, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel14)
                                        .addComponent(jLabel15))
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtPaid, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtVouTotal, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(txtTaxP)
                                            .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtDiscA)
                                            .addComponent(txtTaxA, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jScrollPane1))
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jLabel6, jLabel7});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butOTID)
                    .addComponent(txtBill, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtPatientNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtPatientName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(txtAdmissionNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboPaymentType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtDoctorNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDoctorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDiscA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTaxA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDateMouseClicked
        if (evt.getClickCount() == 2) {
            if (!canEdit) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                        "Check Point", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (Util1.hashPrivilege("SaleDateChange")) {
                String strDate = DateUtil.getDateDialogStr();

                if (strDate != null) {
                    txtDate.setText(strDate);
                    if (lblStatus.getText().equals("NEW")) {
                        vouEngine.setPeriod(DateUtil.getPeriod(txtDate.getText()));
                        genVouNo();
                    }
                }
            }
        }
    }//GEN-LAST:event_txtDateMouseClicked

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
            DoctorSearchDialog dialog = new DoctorSearchDialog(dao, this);
        }
    }//GEN-LAST:event_txtDoctorNameMouseClicked

    private void cboPaymentTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPaymentTypeActionPerformed
        if (cboBindStatus) {
            //if (!isPaid) {
            PaymentType pt = (PaymentType) cboPaymentType.getSelectedItem();
            double discount = NumberUtil.NZero(txtDiscA.getValue());
            double tax = NumberUtil.NZero(txtTaxA.getValue());
            double vouTotal = NumberUtil.NZero(txtVouTotal.getValue());

            if (pt.getPaymentTypeId() == 1) {
                txtPaid.setValue((vouTotal + tax) - discount);
            } else {
                txtPaid.setValue(0);
            }
            calcBalance();
            //}
        }
    }//GEN-LAST:event_cboPaymentTypeActionPerformed

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
            PatientSearch dialog = new PatientSearch(dao, this);
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

    private void tblServiceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblServiceFocusGained
        focusCtrlName = "tblService";
        int selRow = tblService.getSelectedRow();

        if (selRow == -1) {
            //tblService.editCellAt(0, 0);
            tblService.changeSelection(0, 0, false, false);
        }
    }//GEN-LAST:event_tblServiceFocusGained

    private void txtPatientNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPatientNameFocusGained
        focusCtrlName = "txtPatientName";
        txtPatientName.selectAll();
    }//GEN-LAST:event_txtPatientNameFocusGained

    private void cboCurrencyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboCurrencyFocusGained
        focusCtrlName = "cboCurrency";
    }//GEN-LAST:event_cboCurrencyFocusGained

    private void cboPaymentTypeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboPaymentTypeFocusGained
        focusCtrlName = "cboPaymentType";
    }//GEN-LAST:event_cboPaymentTypeFocusGained

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

    private void tblServiceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblServiceMouseClicked
        if (evt.getClickCount() == 2) {
            int index = tblService.convertRowIndexToModel(tblService.getSelectedRow());
            tableModel.showDoctorDetail(index);
        }
    }//GEN-LAST:event_tblServiceMouseClicked

    private void butOTIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOTIDActionPerformed
        openBill();
    }//GEN-LAST:event_butOTIDActionPerformed

    private void applySecurityPolicy() {
        if (lblStatus.getText().equals("EDIT")) {
            if (!Util1.hashPrivilege("OTVoucherEditChange") || !canEdit) {
                if (!canEdit) {
                    disableControl();
                } else {
                    enableControl();
                }
            } else {
                enableControl();
            }
        } else {
            enableControl();
        }
    }

    private void enableControl() {
        boolean status = true;
        txtPatientNo.setEnabled(status);
        txtPatientName.setEnabled(status);
        txtDoctorNo.setEnabled(status);
        cboCurrency.setEnabled(status);
        cboPaymentType.setEnabled(status);
        txtRemark.setEnabled(status);
    }

    private void disableControl() {
        boolean status = false;
        txtPatientNo.setEnabled(status);
        txtPatientName.setEnabled(status);
        txtDoctorNo.setEnabled(status);
        cboCurrency.setEnabled(status);
        cboPaymentType.setEnabled(status);
        txtRemark.setEnabled(status);
    }

    private void tblServiceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblServiceFocusLost
        /*try{
         if(tblService.getCellEditor() != null){
         tblService.getCellEditor().stopCellEditing();
         }
         }catch(Exception ex){
            
         }*/
    }//GEN-LAST:event_tblServiceFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butAdmit;
    private javax.swing.JButton butOTID;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboPaymentType;
    private javax.swing.JCheckBox chkCloseBill;
    private javax.swing.JCheckBox chkVouPrinter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblPatientBill;
    private javax.swing.JTable tblService;
    private javax.swing.JTextField txtAdmissionNo;
    private javax.swing.JTextField txtBill;
    private javax.swing.JFormattedTextField txtBillTotal;
    private javax.swing.JFormattedTextField txtDate;
    private javax.swing.JFormattedTextField txtDiscA;
    private javax.swing.JFormattedTextField txtDiscP;
    private javax.swing.JTextField txtDoctorName;
    private javax.swing.JTextField txtDoctorNo;
    private javax.swing.JFormattedTextField txtPaid;
    private javax.swing.JTextField txtPatientName;
    private javax.swing.JTextField txtPatientNo;
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
