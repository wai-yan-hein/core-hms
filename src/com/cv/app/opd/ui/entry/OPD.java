/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.CalculateObserver;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.RegNo;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.AdmissionKey;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.inpatient.ui.util.PackageSearchDialog;
import com.cv.app.opd.database.entity.BillOpeningHis;
import com.cv.app.opd.database.entity.Booking;
import com.cv.app.opd.database.entity.ClinicPackage;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.DoctorFeesMapping;
import com.cv.app.opd.database.entity.OPDDetailHis;
import com.cv.app.opd.database.entity.OPDHis;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.entity.PatientBillPayment;
import com.cv.app.opd.database.entity.Service;
import com.cv.app.opd.database.tempentity.TempAmountLink;
import com.cv.app.opd.ui.common.OPDTableCellEditor;
import com.cv.app.opd.ui.common.OPDTableModel;
import com.cv.app.opd.ui.util.AmountLinkDialog;
import com.cv.app.opd.ui.util.AppointmentDoctorDialog;
import com.cv.app.opd.ui.util.DoctorSearchNameFilterDialog;
import com.cv.app.opd.ui.util.OPDConfirDialog;
import com.cv.app.opd.ui.util.OPDVouSearchDialog;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.ot.ui.common.OTDrFeeTableCellEditor;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ChargeType;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.SessionCheckCheckpoint;
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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
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
 * @author Eitar
 */
public class OPD extends javax.swing.JPanel implements FormAction, KeyPropagate,
        SelectionObserver, KeyListener, CalculateObserver {

    static Logger log = Logger.getLogger(OPD.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private OPDHis currVou = new OPDHis();
    private OPDTableModel tableModel = new OPDTableModel(dao, this);
    private boolean cboBindStatus = false;
    private GenVouNoImpl vouEngine = null;
    private String focusCtrlName = "-";
    private PatientBillTableModel tblPatientBillTableModel = new PatientBillTableModel();
    private PaymentType ptCash;
    private PaymentType ptCredit;
    private boolean isDeleteCopy = false;
    private boolean canEdit = true;
    String useOPDFactor = Util1.getPropValue("system.opd.chargetype.factor");

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
            if (!focusCtrlName.equals("-")) {
                if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                        && focusCtrlName.equals("txtPatientNo")) {
                    txtDoctorNo.requestFocus();
                } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                        && focusCtrlName.equals("txtDoctorNo")) {
                    txtDonorName.requestFocus();
                } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                        && focusCtrlName.equals("txtDonorName")) {
                    txtRemark.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtCusId")) {
                    txtDonorName.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtDoctorNo")) {
                    txtPatientNo.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("tblService")) {
                    int selRow = tblService.getSelectedRow();
                    if (selRow == -1 || selRow == 0) {
                        txtRemark.requestFocus();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtDonorName")) {
                    txtDoctorNo.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtRemark")) {
                    txtDonorName.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtRemark")) {
                    tblService.requestFocus();
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
        txtDonorName.addKeyListener(this);
        tblService.addKeyListener(this);
        txtRemark.addKeyListener(this);
    }

    @Override
    public void calculate() {
        List<OPDDetailHis> list = tableModel.getListOPDDetailHis();
        double vouTotal = list.stream().filter(o -> o.getService() != null)
                .filter(o -> o.getService().getServiceId() != null)
                .mapToDouble(this::calculateAmount)
                .sum();
        log.info("Vou Total : " + vouTotal);
        txtVouTotal.setValue(vouTotal);
        txtTotalItem.setText(Integer.toString((tableModel.getTotalRecord() - 1)));
        calVouTotal();
    }

    private double calculateAmount(OPDDetailHis record) {
        Double amount = 0d;
        boolean isAmount = false;

        if (record.getChargeType() != null) {
            int chargeType = record.getChargeType().getChargeTypeId();

            switch (chargeType) {
                case 1: //Normal
                    amount = NumberUtil.NZeroInt(record.getQuantity())
                            * NumberUtil.NZero(record.getPrice());
                    break;
                case 2: //FOC
                    break;
                default:
                    if (useOPDFactor.equals("Y")) {
                        float factor = NumberUtil.FloatZero(record.getChargeType().getFactor());
                        if (record.getChargeType() != null) {
                            isAmount = record.getChargeType().getIsAmount();
                        }
                        if (isAmount) {
                            double tmpPrice = NumberUtil.NZero(record.getPrice()) + factor;
                            record.setPrice(tmpPrice);
                        } else {
                            double tmpPercentAmt = (NumberUtil.NZero(record.getPrice()) * factor) / 100;
                            double tmpPrice = NumberUtil.NZero(record.getPrice()) + tmpPercentAmt;
                            record.setPrice(tmpPrice);
                        }
                    }
                    amount = NumberUtil.NZeroInt(record.getQuantity())
                            * NumberUtil.NZero(record.getPrice());
            }
        }
        record.setAmount(amount);

        return amount;
    }

    /**
     * Creates new form OPD
     */
    public OPD() {
        initComponents();

        try {
            initCombo();
            initSpinner();
            txtVouNo.setFormatterFactory(new VouFormatFactory());
            if (Util1.getPropValue("system.login.default.value").equals("Y")) {
                if (Global.loginDate == null) {
                    Global.loginDate = DateUtil.getTodayDateStr();
                }
                txtDate.setText(Global.loginDate);
            } else {
                txtDate.setText(DateUtil.getTodayDateStr());
            }
            vouEngine = new GenVouNoImpl(dao, "OPD",
                    DateUtil.getPeriod(txtDate.getText()));
            ptCash = (PaymentType) dao.find(PaymentType.class,
                    NumberUtil.NZeroInt(Util1.getPropValue("system.paymenttype.cash")));
            ptCredit = (PaymentType) dao.find(PaymentType.class,
                    NumberUtil.NZeroInt(Util1.getPropValue("system.paymenttype.credit")));
            initTable();
            tableModel.setParent(tblService);
            tableModel.setVouStatus("NEW");
            tableModel.addNewRow();
            butOTID.setEnabled(false);
            actionMapping();
            initTextBoxAlign();
            assignDefaultValue();
            initForFocus();
            timerFocus();
        } catch (Exception ex) {
            log.error("OPD : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }

        butAdmit.setEnabled(false);
        butAdmit.setVisible(false);
        butRemove.setEnabled(false);

        if (Util1.getPropValue("system.opd.emg").equals("Y")) {
            jLabel26.setVisible(true);
            txtEmgPercent.setVisible(true);
        } else {
            jLabel26.setVisible(false);
            txtEmgPercent.setVisible(false);
        }
    }

    private void initSpinner() {
        int count = Util1.getIntegerOne(Util1.getPropValue("system.opd.print.count"));
        if (Util1.getPropValue("system.pharmacy.opd.print.double").equals("Y")) {
            count = 2;
        }
        spPrint.setModel(new SpinnerNumberModel(count, 0, 10, 1));
    }

    public void timerFocus() {
        Timer timer = new Timer(500, (ActionEvent e) -> {
            txtPatientNo.requestFocus();
        });

        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public void save() {
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot save edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean status;
        if (chkAmount.isSelected()) {
            OPDConfirDialog dialog = new OPDConfirDialog(currVou);
            status = dialog.isStatus();
        } else {
            status = true;
        }

        log.info("Validation start. : " + new Date());
        if (isValidEntry()) {
            log.info("Validation end. : " + new Date());

            Date vouSaleDate = DateUtil.toDate(txtDate.getText());
            Date lockDate = PharmacyUtil.getLockDate(dao);
            if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                        + DateUtil.toDateStr(lockDate) + ".",
                        "Locked Data", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (status) {
                log.info("Backup start. : " + new Date());
                try {
                    Date d = new Date();
                    String strVouTotal = NumberUtil.NZero(currVou.getVouTotal()).toString();
                    String strDiscA = NumberUtil.NZero(currVou.getDiscountA()).toString();
                    String strPaid = NumberUtil.NZero(currVou.getPaid()).toString();
                    String strBalance = NumberUtil.NZero(currVou.getVouBalance()).toString();
                    dao.execProc("bk_opd",
                            currVou.getOpdInvId(),
                            DateUtil.toDateTimeStrMYSQL(d),
                            Global.loginUser.getUserId(),
                            Global.machineId,
                            strVouTotal,
                            strDiscA,
                            strPaid,
                            strBalance);
                } catch (Exception ex) {
                    log.error("bk_opd : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
                } finally {
                    dao.close();
                }
                log.info("Backup end. : " + new Date());

                try {
                    log.info("Save start. : " + new Date());
                    if (lblStatus.getText().equals("NEW")) {
                        currVou.setCreatedBy(Global.loginUser);
                        currVou.setCreatedDate(new Date());
                    } else {
                        currVou.setUpdatedBy(Global.loginUser);
                        currVou.setUpdatedDate(new Date());
                    }

                    String vouNo = currVou.getOpdInvId();
                    List<OPDDetailHis> listDetail = getVerifiedUniqueId(vouNo, currVou.getListOPDDetailHis());
                    //dao.open();
                    //dao.beginTran();
                    if (currVou.getPkgId() != null) {
                        //delete detail for package
                        String sql = "delete from opd_details_his where vou_no='" + vouNo + "'";
                        dao.execSql(sql);
                    }
                    for (OPDDetailHis odh : listDetail) {
                        odh.setVouNo(vouNo);
                        if (odh.getOpdDetailId() == null) {
                            odh.setOpdDetailId(vouNo + "-" + odh.getUniqueId());
                        }
                        dao.save(odh);
                    }
                    dao.save(currVou);
                    //dao.commit();

                    if (lblStatus.getText().equals("NEW")) {
                        vouEngine.updateVouNo();
                    }

                    deleteDetail();
                    log.info("Save end. : " + new Date());
                    //System.out.println("Before updateVouTotal Vou Save end." + new Date());
                    //updateVouTotal(currVou.getOpdInvId());
                    //System.out.println("After updateVouTotal Vou Save end." + new Date());
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
                    String desp = "-";
                    if (currVou.getPatient() != null) {
                        desp = currVou.getPatient().getRegNo() + "-" + currVou.getPatient().getPatientName();
                    }
                    log.info("Before uploadToAccount." + new Date());
                    /*uploadToAccount(currVou.getOpdInvId(), currVou.isDeleted(),
                            currVou.getVouBalance(), currVou.getDiscountA(),
                            currVou.getPaid(), currVou.getTaxA(), desp);*/
                    uploadToAccount(currVou.getOpdInvId());
                    log.info("After uploadToAccount." + new Date());

                    newForm();
                } catch (Exception ex) {
                    dao.rollBack();
                    log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    JOptionPane.showMessageDialog(Util1.getParent(), "Error : " + ex.toString(),
                            "OPD Save", JOptionPane.ERROR_MESSAGE);
                } finally {
                    dao.close();
                }
            }
        } else {
            tblService.requestFocus();
        }
    }

    @Override
    public void newForm() {
        isDeleteCopy = false;
        canEdit = true;
        chkCloseBill.setSelected(false);
        currVou = new OPDHis();
        txtDoctorName.setText(null);
        txtDoctorNo.setText(null);
        txtPatientName.setText(null);
        txtPatientNo.setText(null);
        txtRemark.setText(null);
        txtVouNo.setText(null);
        txtAge.setText(null);
        txtDay.setText(null);
        txtMonth.setText(null);
        txtEmgPercent.setText(null);
        tableModel.clear();
        tableModel.setVouStatus("NEW");
        txtPatientNo.setEditable(true);
        txtPatientName.setEditable(true);
        txtPatientNo.requestFocusInWindow();
        lblStatus.setText("NEW");
        tableModel.setCanEdit(canEdit);
        tableModel.setBookType("-");
        tableModel.setReferDoctor(null);
        assignDefaultValue();
        txtBillTotal.setText(null);
        tblPatientBillTableModel.setListPBP(new ArrayList());
        butAdmit.setEnabled(false);
        txtPatientNo.requestFocus();
        txtBill.setText(null);
        tableModel.setReaderDoctor("-");
        txtPackageName.setText("");
        txtPrice.setValue(0);
        butRemove.setEnabled(false);
        txtDoctorName.setText("");
        applySecurityPolicy();
    }

    @Override
    public void history() {
        OPDVouSearchDialog dialog = new OPDVouSearchDialog(this, "ENTRY");
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
            if (!Util1.hashPrivilege("OPDVoucherDelete")) {
                JOptionPane.showMessageDialog(Util1.getParent(), "You have no permission to delete voucher.",
                        "OPD voucher delete", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        if (tableModel.isPayAlready()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Doctor payment already made. "
                    + "You cannot delete this voucher.",
                    "Doctor payment", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (Util1.getNullTo(currVou.isDeleted())) {
            JOptionPane.showConfirmDialog(Util1.getParent(), "Voucher already deleted.",
                    "OPD voucher delete", JOptionPane.ERROR);
        } else {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "OPD voucher delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                try {
                    Date d = new Date();
                    String strVouTotal = NumberUtil.NZero(currVou.getVouTotal()).toString();
                    String strDiscA = NumberUtil.NZero(currVou.getDiscountA()).toString();
                    String strPaid = NumberUtil.NZero(currVou.getPaid()).toString();
                    String strBalance = NumberUtil.NZero(currVou.getVouBalance()).toString();
                    dao.execProc("bk_opd",
                            currVou.getOpdInvId(),
                            DateUtil.toDateTimeStrMYSQL(d),
                            Global.loginUser.getUserId(),
                            Global.machineId,
                            strVouTotal,
                            strDiscA,
                            strPaid,
                            strBalance);
                } catch (Exception ex) {
                    log.error("bk_opd : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
                } finally {
                    dao.close();
                }

                currVou.setDeleted(true);
                try {
                    //dao.save(currVou);
                    String vouNo = currVou.getOpdInvId();
                    dao.execSql("update opd_his set deleted = true, intg_upd_status = null where opd_inv_id = '" + vouNo + "'");
                    if (lblStatus.getText().equals("NEW")) {
                        vouEngine.updateVouNo();
                    }
                    uploadToAccount(currVou.getOpdInvId());
                    newForm();
                } catch (Exception ex) {
                    log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
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
            /*if (!Util1.hashPrivilege("OPDVoucherEditChange")) {
                return;
            }*/
            if (!Util1.hashPrivilege("OPDVoucherDelete")) {
                JOptionPane.showMessageDialog(Util1.getParent(), "You have no permission to delete voucher.",
                        "OPD voucher delete", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        if (tableModel.isPayAlready()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Doctor payment already made. You cannot delete this voucher.",
                    "Doctor payment", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete and copy?",
                "OPD voucher delete", JOptionPane.YES_NO_OPTION);

        if (yes_no == 0) {
            isDeleteCopy = true;
            currVou.setDeleted(true);
            try {
                Date d = new Date();
                String strVouTotal = NumberUtil.NZero(currVou.getVouTotal()).toString();
                String strDiscA = NumberUtil.NZero(currVou.getDiscountA()).toString();
                String strPaid = NumberUtil.NZero(currVou.getPaid()).toString();
                String strBalance = NumberUtil.NZero(currVou.getVouBalance()).toString();
                dao.execProc("bk_opd",
                        currVou.getOpdInvId(),
                        DateUtil.toDateTimeStrMYSQL(d),
                        Global.loginUser.getUserId(),
                        Global.machineId,
                        strVouTotal,
                        strDiscA,
                        strPaid,
                        strBalance);
            } catch (Exception ex) {
                log.error("bk_opd : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
            } finally {
                dao.close();
            }

            try {
                currVou.setListOPDDetailHis(tableModel.getListOPDDetailHis());
                dao.save(currVou);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
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
            OPDHis tmpVou = (OPDHis) dao.find(OPDHis.class, invNo);

            currVou = new OPDHis();
            BeanUtils.copyProperties(tmpVou, currVou);
            List<OPDDetailHis> listDetail = new ArrayList();
            List<OPDDetailHis> listDetailTmp = null;

            try {
                listDetailTmp = dao.findAllHSQL(
                        "select o from OPDDetailHis o where o.vouNo = '" + invNo + "' order by o.uniqueId");
            } catch (Exception ex) {
                log.error("copyVoucher : " + ex.toString());
            } finally {
                dao.close();
            }

            for (OPDDetailHis detail : listDetailTmp) {
                OPDDetailHis tmpDetail = new OPDDetailHis();
                BeanUtils.copyProperties(detail, tmpDetail);
                tmpDetail.setOpdDetailId(null);
                listDetail.add(tmpDetail);
            }
            currVou.setListOPDDetailHis(listDetail);
            currVou.setDeleted(false);

            if (!tmpVou.getListOPDDetailHis().isEmpty()) {
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
        try {
            Date d = new Date();
            String strVouTotal = NumberUtil.NZero(currVou.getVouTotal()).toString();
            String strDiscA = NumberUtil.NZero(currVou.getDiscountA()).toString();
            String strPaid = NumberUtil.NZero(currVou.getPaid()).toString();
            String strBalance = NumberUtil.NZero(currVou.getVouBalance()).toString();
            dao.execProc("bk_opd",
                    currVou.getOpdInvId(),
                    DateUtil.toDateTimeStrMYSQL(d),
                    Global.loginUser.getUserId(),
                    Global.machineId,
                    strVouTotal,
                    strDiscA,
                    strPaid,
                    strBalance);
        } catch (Exception ex) {
            log.error("bk_opd : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
        } finally {
            dao.close();
        }

        Date vouSaleDate = DateUtil.toDate(txtDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        boolean isDataLock = false;
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            isDataLock = true;
        }

        boolean status = false;
        if (isValidEntry()) {
            if (!isDataLock) {
                if (chkAmount.isSelected()) {
                    OPDConfirDialog dialog = new OPDConfirDialog(currVou);
                    status = dialog.isStatus();
                } else {
                    status = true;
                }

                if (status) {
                    try {
                        if (lblStatus.getText().equals("NEW")) {
                            currVou.setCreatedBy(Global.loginUser);
                            currVou.setCreatedDate(new Date());
                        } else {
                            currVou.setUpdatedBy(Global.loginUser);
                            currVou.setUpdatedDate(new Date());
                        }
                        if (canEdit) {
                            String vouNo = currVou.getOpdInvId();
                            List<OPDDetailHis> listDetail = getVerifiedUniqueId(vouNo, currVou.getListOPDDetailHis());

                            //dao.open();
                            //dao.beginTran();
                            for (OPDDetailHis odh : listDetail) {
                                odh.setVouNo(vouNo);
                                if (odh.getOpdDetailId() == null) {
                                    odh.setOpdDetailId(vouNo + "-" + odh.getUniqueId());
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

                            try {
                                //double vouBalance = NumberUtil.NZero(currVou.getVouBalance());
                                //double ttlBill = Double.parseDouble(txtBillTotal.getText());
                                if (chkCloseBill.isSelected()) {
                                    Patient pt = currVou.getPatient();
                                    if (pt != null) {
                                        if (pt.getOtId() != null) {
                                            log.error("Bill Close Save ==> OT Vou No : " + currVou.getOpdInvId()
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

                            String desp = "-";
                            if (currVou.getPatient() != null) {
                                desp = currVou.getPatient().getRegNo() + "-" + currVou.getPatient().getPatientName();
                            }
                            uploadToAccount(currVou.getOpdInvId());
                        }
                    } catch (Exception ex) {
                        dao.rollBack();
                        log.error("print : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                        JOptionPane.showMessageDialog(Util1.getParent(), "Error cannot print.",
                                "OPD print", JOptionPane.ERROR_MESSAGE);
                        return;
                    } finally {
                        dao.close();
                    }
                }
            } else {
                status = true;
            }
        }

        if (!status) {
            return;
        }

        Map<String, Object> params = new HashMap();
        //Properties prop = ReportUtil.loadReportPathProperties();
        String reportName = Util1.getPropValue("report.file.opd");

        //For package report
        Long pkgId = currVou.getPkgId();
        if (pkgId != null) {
            reportName = Util1.getPropValue("report.file.opd.package");
            params.put("p_pkg_amt", currVou.getPkgPrice());
        }
        //================================

        String printerName = Util1.getPropValue("report.vou.printer");
        params.put("link_amt_status", "N");
        params.put("link_amt", 0);
        params.put("p_ttl_discount", NumberUtil.NZero(currVou.getDiscountA()));
        if (Util1.getPropValue("system.link.amount").equals("OPD")
                && currVou.getPatient() != null) {
            try {
                String delSql = "delete from tmp_amount_link where user_id = '"
                        + Global.machineId + "'";
                String strSql = "INSERT INTO tmp_amount_link(user_id,tran_option,inv_id,amount,discount,print_status)\n"
                        + "  select '" + Global.machineId + "', tran_source, inv_id, balance,discount, true\n"
                        + "    from (select date(sale_date) tran_date, reg_no cus_id, currency_id currency, "
                        + "                 'Pharmacy' as tran_source, sale_inv_id inv_id,\n"
                        + "                 ifnull(paid_amount,0) balance,discount\n"
                        + "            from sale_his\n"
                        + "   where ifnull(paid_amount,0) <> 0 and deleted = false and reg_no = '"
                        + currVou.getPatient().getRegNo() + "'\n"
                        + "and date(sale_date) = '" + DateUtil.toDateStr(currVou.getInvDate(), "yyyy-MM-dd") + "'\n"
                        + "		   union all\n"
                        + "		  select date(tran_date) tran_date, patient_id cus_id, currency_id currency, \n"
                        + "				 tran_option tran_source, opd_inv_id inv_id, sum(ifnull(paid,0)) balance,disc_a\n"
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
                        if (!chkA5.isSelected()) {
                            dialog.setVisible(true);
                        }
                        double ttlLinkAmt = dialog.getTtlAmt();
                        double ttlDiscount = dialog.getTtlDisount();
                        params.put("p_ttl_discount", ttlDiscount + NumberUtil.NZero(currVou.getDiscountA()));
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

        String compName = Util1.getPropValue("report.company.name");
        String printMode = Util1.getPropValue("report.vou.printer.mode");
        String phoneNo = Util1.getPropValue("report.phone");
        String imagePath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path") + "img/logo.jpg";
        params.put("imagePath", imagePath);
        params.put("invoiceNo", currVou.getOpdInvId());
        params.put("comp_name", Util1.getPropValue("report.company.name1"));
        params.put("category", Util1.getPropValue("report.company.cat"));
        params.put("comp_address", Util1.getPropValue("report.address"));

        if (chkA5.isSelected()) {
            String tmpRptName = Util1.getPropValue("report.file.opd.a5");
            if (!tmpRptName.isEmpty() && !tmpRptName.equals("-")) {
                reportName = tmpRptName;
            } else {
                reportName = "W/OPDVoucherInvoiceA5";
            }
            printMode = "View";
        }
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + reportName;
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
            if (currVou.getPatient().getTownship() != null) {
                params.put("address", currVou.getPatient().getTownship().getTownshipName());
            } else {
                params.put("address", "-");
            }
            if (currVou.getPatient().getSex() != null) {
                params.put("sex", currVou.getPatient().getSex().getDescription());
            } else {
                params.put("sex", "");
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

        if (currVou.getAge() != null) {
            params.put("age", currVou.getAge());
        } else {
            params.put("age", "");
        }
        params.put("saleDate", currVou.getInvDate());
        params.put("opd_date", DateUtil.toDateStr(currVou.getInvDate(), "yyyy-MM-dd"));
        if (pkgId != null) {
            List<OPDDetailHis> listOPDDetailHis = currVou.getListOPDDetailHis();
            double ttlExtraAmt = 0.0;
            for (OPDDetailHis odh : listOPDDetailHis) {
                if (odh.getService() != null) {
                    if (!odh.getPkgItem()) {
                        ttlExtraAmt += odh.getAmount();
                    }
                }
            }
            double vouTtlAmt = currVou.getPkgPrice() + ttlExtraAmt;
            params.put("grandTotal", vouTtlAmt);
        } else {
            params.put("grandTotal", currVou.getVouTotal());
        }
        params.put("paid", currVou.getPaid());
        params.put("discount", currVou.getDiscountA());
        params.put("tax", currVou.getTaxA());
        params.put("balance", currVou.getVouBalance());
        params.put("user", Global.loginUser.getUserShortName());
        params.put("compName", compName);
        params.put("phoneNo", phoneNo);

        if (pkgId != null) {
            params.put("remark", currVou.getPkgName());
        } else {
            params.put("remark", txtRemark.getText());
        }

        params.put("user_id", Global.machineId);
        params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));
        params.put("REPORT_CONNECTION", dao.getConnection());
        params.put("user_desp", "Customer Voucher, Thanks You.");
        params.put("bill_id", Util1.isNull(txtBill.getText(), "-"));
        params.put("IMAGE_PATH", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));

        if (lblStatus.getText().equals("NEW")) {
            params.put("vou_status", " ");
        } else {
            params.put("vou_status", lblStatus.getText());
        }

        if (!tableModel.getReaderDoctor().equals("-")) {
            params.put("read_doctor", tableModel.getReaderDoctor());
        } else {
            params.put("read_doctor", "");
        }

        //log.error("Printer : " + printerName);
        if (printMode.equals("View")) {
            if (Util1.getPropValue("report.file.type").equals("con")) {
                ReportUtil.viewReport(reportPath, params, dao.getConnection());
            } else {
                ReportUtil.viewReport(reportPath, params, tableModel.getListOPDDetailHis());
            }
        } else if (Util1.getPropValue("report.file.type").equals("con")) {
            int count = (int) spPrint.getValue();
            for (int i = 0; i < count; i++) {
                JasperPrint jp = ReportUtil.getReport(reportPath, params, dao.getConnection());
                ReportUtil.printJasper(jp, printerName);
                params.put("user_desp", "Receive Voucher, Thanks You.");
            }
        } else {
            JasperPrint jp = ReportUtil.getReport(reportPath, params, tableModel.getListOPDDetailHis());
            ReportUtil.printJasper(jp, printerName);
        }
        newForm();
    }

    @Override
    public void keyEvent(KeyEvent e) {
        if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
            delete();
        } else if (e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_F8)) {
            deleteCopy();
        } else if (e.getKeyCode() == KeyEvent.VK_F5) {
            save();
        } else if (e.getKeyCode() == KeyEvent.VK_F7) {
            print();
        } else if (e.getKeyCode() == KeyEvent.VK_F9) {
            history();
        } else if (e.getKeyCode() == KeyEvent.VK_F10) {
            newForm();
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

    private void genVouNo() {
        String vouNo = vouEngine.getVouNo();
        txtVouNo.setText(vouNo);

        try {
            List<OPDHis> listOPDH = dao.findAllHSQL(
                    "select o from OPDHis o where o.opdInvId = '" + vouNo + "'");
            if (listOPDH != null) {
                if (!listOPDH.isEmpty()) {
                    vouEngine.updateVouNo();
                    vouNo = vouEngine.getVouNo();
                    txtVouNo.setText(vouNo);
                    listOPDH = null;
                    listOPDH = dao.findAllHSQL(
                            "select o from OPDHis o where o.opdInvId = '" + vouNo + "'");
                    if (listOPDH != null) {
                        if (!listOPDH.isEmpty()) {
                            log.error("Duplicate OPD voucher error : " + txtVouNo.getText() + " @ "
                                    + txtDate.getText());
                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Duplicate OPD vou no. Exit the program and try again.",
                                    "OPD Vou No", JOptionPane.ERROR_MESSAGE);
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
            tblService.getColumnModel().getColumn(5).setPreferredWidth(80);//Refer Dr
            tblService.getColumnModel().getColumn(6).setPreferredWidth(80);//Read Dr
            tblService.getColumnModel().getColumn(7).setPreferredWidth(80);//Technician

            //Change JTable cell editor
            tblService.getColumnModel().getColumn(0).setCellEditor(
                    new OPDTableCellEditor(dao));
            tblService.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor());
            tblService.getColumnModel().getColumn(3).setCellEditor(new BestTableCellEditor());

            JComboBox cboChargeType = new JComboBox();
            BindingUtil.BindCombo(cboChargeType, dao.findAll("ChargeType"));
            tblService.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cboChargeType));
            tblService.getColumnModel().getColumn(5).setCellEditor(new OTDrFeeTableCellEditor(dao, this));
            tblService.getColumnModel().getColumn(6).setCellEditor(new OTDrFeeTableCellEditor(dao, this));
            JComboBox cboTechnician = new JComboBox();
            //BindingUtil.BindCombo(cboTechnician,
            //        dao.findAllHSQL("select o from Technician o where o.active = true order by o.techName"));
            BindingUtil.BindCombo(cboTechnician,
                    dao.findAllHSQL("select o from Doctor o where o.active = true and o.drType = 'Technician' order by o.doctorName"));
            tblService.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(cboTechnician));
            tblService.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblPatientBill.getColumnModel().getColumn(0).setPreferredWidth(180);//Bill Name
            tblPatientBill.getColumnModel().getColumn(1).setPreferredWidth(70);//Amount
            tblPatientBill.getTableHeader().setFont(Global.lableFont);
            tblPatientBill.setFont(Global.textFont);
            tblPatientBill.setRowHeight(Global.rowHeight);
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
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

            OPDDetailHis record = tableModel.getOPDDetailHis(row);
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
        formActionKeyMapping(txtDonorName);
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
                if (!Util1.hashPrivilege("OPDVoucherEditChange")) {
                    if (Util1.hashPrivilege("OPDCreditVoucherEdit")) {
                        if (NumberUtil.NZero(currVou.getPaid()) != 0) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }

            int row = tblService.getSelectedRow();

            if (row >= 0) {
                OPDDetailHis opdh = tableModel.getOPDDetailHis(row);

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
        chkA5.setSelected(Util1.getPropValue("chk.opd.A5").equals("Y"));
        genVouNo();
    }

    private void calVouTotal() {
        txtVouTotal.setValue(tableModel.getTotal());
        PaymentType pt = (PaymentType) cboPaymentType.getSelectedItem();
        if (pt.getPaymentTypeId() == 1) {
            txtPaid.setValue(NumberUtil.NZero(txtVouTotal.getText())
                    + NumberUtil.NZero(txtTaxA.getText()) - NumberUtil.NZero(txtDiscA.getText()));
        } else {
            txtPaid.setValue(0.0);
        }

        double balance = NumberUtil.NZero(txtVouTotal.getText()) + NumberUtil.NZero(txtTaxA.getText())
                - NumberUtil.NZero(txtDiscA.getText()) - NumberUtil.NZero(txtPaid.getText());
        txtVouBalance.setValue(balance);
        txtTotalItem.setText(Integer.toString((tableModel.getTotalRecord() - 1)));
        txtRecNo.setText(Integer.toString(tblService.getSelectedRow() + 1));
    }

    private void calculatePackage() {
        if (cboPaymentType.getSelectedItem() != null) {
            if (currVou.getPkgId() != null) {
                tableModel.calculatePkgTotal();
                double pkgItemTotal = tableModel.getPkgTotal();
                double pkgAmt = currVou.getPkgPrice();
                double diffAmt = pkgItemTotal - pkgAmt;
                String pkgGainId = Util1.getPropValue("system.opd.pkggain.id");
                String hsql = "select o from Service o where o.serviceId = '" + pkgGainId + "'";
                try {
                    List<Service> list = dao.findAllHSQL(hsql);
                    if (!list.isEmpty()) {
                        Service pkService = list.get(0);
                        OPDDetailHis opd = new OPDDetailHis();
                        opd.setService(pkService);
                        opd.setQuantity(diffAmt < 0 ? 1 : -1);
                        opd.setPrice(diffAmt < 0 ? diffAmt * -1 : diffAmt);
                        opd.setAmount(opd.getQuantity() * opd.getPrice());
                        opd.setChargeType(tableModel.getDefaultChargeType());
                        tableModel.addOPDDetailHis(opd);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid Package Id.");
                    }
                } catch (Exception e) {
                    log.error("calculatePackage : " + e.getMessage());
                }
            }
            calVouTotal();
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        Patient pt = currVou.getPatient();
        String admissionNo = "-";

        if (pt != null) {
            admissionNo = Util1.isNull(pt.getAdmissionNo(), "-");
        }

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

        if (!Util1.hashPrivilege("CanEditOPDCheckPoint")) {
            if (lblStatus.getText().equals("NEW")) {
                try {
                    long cnt = dao.getRowCount("select count(*) from c_bk_opd_his where opd_date >= '"
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
        double modelTtl = tableModel.getTotal();
        if (vouTtl != modelTtl) {
            log.error(txtVouNo.getText().trim() + " OPD Voucher Total Error : vouTtl : "
                    + vouTtl + " modelTtl : " + modelTtl);
            JOptionPane.showMessageDialog(Util1.getParent(), "Please check voucher total.",
                    "Voucher Total Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        calVouTotal();
        double paid = NumberUtil.NZero(txtPaid.getText());
        double disc = NumberUtil.NZero(txtDiscA.getText());
        double tax = NumberUtil.NZero(txtTaxA.getText());

        int payType = ((PaymentType) cboPaymentType.getSelectedItem()).getPaymentTypeId();
        if (payType == 1) {

            if (NumberUtil.roundDouble((vouTtl + tax), 0) != NumberUtil.roundDouble((paid + disc), 0)) {
                log.error(txtVouNo.getText().trim() + " OPD Voucher Paid Error : vouTtl : "
                        + vouTtl + " Paid : " + paid);
                JOptionPane.showMessageDialog(Util1.getParent(), "Please check voucher paid.",
                        "Paid Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        double vouBalance = NumberUtil.NZero(txtVouBalance.getText());
        if (Util1.getPropValue("system.opdpatient.mustpaid").equals("Y") && admissionNo.equals("-")) {
            if (vouBalance != 0) {
                log.error("isValidEntry : " + txtVouNo.getText().trim() + " Balance is not zero. " + vouBalance);
                JOptionPane.showMessageDialog(Util1.getParent(), "Balance is not zero.",
                        "Paid Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        if (!DateUtil.isValidDate(txtDate.getText())) {
            log.error("OPD date error : " + txtVouNo.getText());
            status = false;
        } else if (txtVouNo.getText().trim().length() < 15) {
            log.error("OPD vou error : " + txtVouNo.getText() + " - " + txtDate.getText());
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
            currVou.setDonorName(txtDonorName.getText());
            currVou.setPatientName(txtPatientName.getText());
            currVou.setAdmissionNo(txtAdmissionNo.getText());
            currVou.setAge(NumberUtil.NZeroInt(txtAge.getText()));
            currVou.setEmgPercent(NumberUtil.FloatZero(txtEmgPercent.getText()));

            if (payType == 1) {
                if ((NumberUtil.roundDouble(currVou.getVouTotal(), 0) + NumberUtil.roundDouble(currVou.getTaxA(), 0))
                        != (NumberUtil.roundDouble(currVou.getPaid(), 0) + NumberUtil.roundDouble(currVou.getDiscountA(), 0))) {
                    log.error(txtVouNo.getText().trim() + " OPD Voucher Paid Error 2: vouTtl : "
                            + vouTtl + " Paid : " + paid);
                    JOptionPane.showMessageDialog(Util1.getParent(), "2 Please check voucher paid.",
                            "Paid Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            if (lblStatus.getText().equals("EDIT") || lblStatus.getText().equals("DELETED")) {
                currVou.setUpdatedBy(Global.loginUser);
                currVou.setUpdatedDate(new Date());
            } else if (lblStatus.getText().equals("NEW")) {
                currVou.setCreatedBy(Global.loginUser);
                currVou.setCreatedDate(new Date());
                currVou.setDeleted(false);
                if (!isDeleteCopy) {
                    currVou.setSession(Global.sessionId);
                }
            }

            if (currVou.getSession() == null) {
                currVou.setSession(Global.sessionId);
            }

            if (txtBill.getText() == null) {
                currVou.setOtId(null);
            } else if (!txtBill.getText().isEmpty()) {
                currVou.setOtId(txtBill.getText());
            } else {
                currVou.setOtId(null);
            }
            try {
                //check patient dob
                Patient patient = (Patient) dao.find(Patient.class, txtPatientNo.getText());
                if (patient != null) {
                    if (patient.getDob() == null) {

                        try {
                            String sql = "update patient_detail set dob = '"
                                    + DateUtil.toDateStr(getDOB(), "yyyy-MM-dd") + "' "
                                    + "where reg_no = '" + patient.getRegNo() + "'";
                            dao.execSql(sql);
                        } catch (Exception ex) {
                            log.error("upadte patient dob " + ex.getMessage());
                        } finally {
                            dao.close();
                        }

                    }
                }
            } catch (Exception ex) {
                log.error("Patient Error : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }

        return status;
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "DoctorSearch":
                try {
                if (tableModel.isPayAlready()) {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Doctor payment already made. "
                            + "You cannot change doctor.",
                            "Doctor payment", JOptionPane.ERROR_MESSAGE);
                    if (currVou.getDoctor() != null) {
                        txtDoctorNo.setText(currVou.getDoctor().getDoctorId());
                    } else {
                        txtDoctorNo.setText(null);
                    }
                    return;
                }
                Doctor doctor = (Doctor) selectObj;
                doctor = (Doctor) dao.find(Doctor.class, doctor.getDoctorId());
                List<DoctorFeesMapping> listDFM = dao.findAllHSQL(
                        "select o from DoctorFeesMapping o where o.drId = '" + doctor.getDoctorId() + "'");
                if (listDFM != null) {
                    if (!listDFM.isEmpty()) {
                        HashMap<Integer, Double> doctFees = new HashMap();

                        for (DoctorFeesMapping dfm : listDFM) {
                            doctFees.put(dfm.getService().getServiceId(), dfm.getFees());
                            if (tableModel.getTotalRecord() > 0) {
                                if (dfm.getService() != null) {
                                    tableModel.setDoctorFees(dfm.getService().getServiceId(), dfm.getFees());
                                }
                            }
                        }

                        tableModel.setDoctFees(doctFees);
                    }
                }
                currVou.setDoctor(doctor);
                tableModel.setReferDoctor(doctor);
                tableModel.updateReferDoctor();
                txtDoctorNo.setText(doctor.getDoctorId());
                txtDoctorName.setText((doctor.getDoctorName()));
                txtVouTotal.setValue(tableModel.getTotal());
                tblService.requestFocus();
                tableModel.addAutoServiceByDoctor();
            } catch (Exception ex) {
                log.error("select DoctorSearch : " + ex.getMessage());
            } finally {
                dao.close();
            }
            break;
            case "PatientSearch":
                if (tableModel.isPayAlready()) {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Doctor payment already made. "
                            + "You cannot change patient.",
                            "Doctor payment", JOptionPane.ERROR_MESSAGE);
                    if (currVou.getPatient() != null) {
                        txtPatientNo.setText(currVou.getPatient().getRegNo());
                    } else {
                        txtPatientNo.setText(null);
                    }
                    return;
                }
                try {
                    txtAdmissionNo.setText(null);
                    txtBill.setText(null);
                    butAdmit.setEnabled(true);
                    Patient patient = (Patient) selectObj;
                    currVou.setPatient(patient);
                    currVou.setAdmissionNo(patient.getAdmissionNo());
                    txtAdmissionNo.setText(patient.getAdmissionNo());
                    txtAge.setText(isNull(patient.getAge()));
                    txtMonth.setText(isNull(patient.getMonth()));
                    txtDay.setText(isNull(patient.getDay()));
                    txtPatientNo.setText(patient.getRegNo());
                    txtPatientName.setText(patient.getPatientName());
                    txtPatientName.setEditable(false);
                    txtDoctorNo.requestFocus();
                    if (!Util1.isNullOrEmpty(patient.getAdmissionNo())) {
                        cboPaymentType.setSelectedItem(ptCredit);
                        butAdmit.setEnabled(false);
                    } else if (!Util1.isNullOrEmpty(patient.getOtId())) {
                        cboPaymentType.setSelectedItem(ptCredit);
                        txtBill.setText(patient.getOtId());
                    } else {
                        cboPaymentType.setSelectedItem(ptCash);
                    }
                    if (Util1.getPropValue("system.admission.paytype").equals("CASH")) {
                        cboPaymentType.setSelectedItem(ptCash);
                    }
                    if (patient.getOtId() != null) {
                        butOTID.setEnabled(false);
                        txtBill.setText(patient.getOtId());
                    } else {
                        butOTID.setEnabled(true);
                        txtBill.setText(null);
                    }
                    getPatientBill(patient.getRegNo());

                    //Booking info
                    String regNo = patient.getRegNo();
                    List<Booking> listBK = dao.findAllHSQL(
                            "select o from Booking o where o.regNo = '"
                            + regNo + "' and o.bkDate = '" + DateUtil.toDateStrMYSQL(txtDate.getText()) + "'");
                    if (listBK != null) {
                        if (!listBK.isEmpty()) {
                            if (listBK.size() == 1) {
                                Booking bk = listBK.get(0);
                                String visitId = bk.getDoctor().getDoctorId() + "-" + bk.getRegNo() + "-"
                                        + DateUtil.getDatePart(bk.getBkDate(), "ddMMyyyy")
                                        + "-" + bk.getBkSerialNo();
                                currVou.setVisitId(visitId);
                                selected("DoctorSearch", bk.getDoctor());
                                String bookType = bk.getBkType();
                                tableModel.setBookType(bookType);
                            } else if (listBK.size() > 1) {
                                AppointmentDoctorDialog dialog = new AppointmentDoctorDialog();
                                dialog.setLocationRelativeTo(null);
                                dialog.setData(listBK);
                                dialog.setVisible(true);
                                Booking selBK = dialog.getSelectedBK();
                                if (selBK != null) {
                                    String visitId = selBK.getDoctor().getDoctorId() + "-" + selBK.getRegNo() + "-"
                                            + DateUtil.getDatePart(selBK.getBkDate(), "ddMMyyyy")
                                            + "-" + selBK.getBkSerialNo();
                                    currVou.setVisitId(visitId);
                                    selected("DoctorSearch", selBK.getDoctor());
                                    String bookType = selBK.getBkType();
                                    tableModel.setBookType(bookType);
                                }
                            } else {
                                if (patient.getDoctor() != null) {
                                    selected("DoctorSearch", patient.getDoctor());
                                }
                            }
                        } else {
                            if (patient.getDoctor() != null) {
                                selected("DoctorSearch", patient.getDoctor());
                            }
                        }
                    } else {
                        if (patient.getDoctor() != null) {
                            selected("DoctorSearch", patient.getDoctor());
                        }
                    }
                } catch (Exception ex) {
                    log.error("select PatientSearch : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }
                break;
            case "OPDVouList":
                try {
                newForm();
                VoucherSearch vs = (VoucherSearch) selectObj;
                String vouId = vs.getInvNo();
                try {
                    currVou = (OPDHis) dao.find(OPDHis.class, vouId);
                    List<OPDDetailHis> listDetail = dao.findAllHSQL(
                            "select o from OPDDetailHis o where o.vouNo = '" + vouId + "' order by o.uniqueId");
                    currVou.setListOPDDetailHis(listDetail);
                } catch (Exception ex) {
                    log.error("OPDVouList : " + ex.toString());
                } finally {
                    dao.close();
                }
                tableModel.clear();
                tableModel.setListOPDDetailHis(currVou.getListOPDDetailHis());
                tableModel.setReferDoctor(currVou.getDoctor());
                txtVouNo.setText(currVou.getOpdInvId());
                txtDate.setText(DateUtil.toDateStr(currVou.getInvDate()));
                cboCurrency.setSelectedItem(currVou.getCurrency());
                cboPaymentType.setSelectedItem(currVou.getPaymentType());
                txtRemark.setText(currVou.getRemark());
                txtAdmissionNo.setText(currVou.getAdmissionNo());
                //txtEmgPercent.setValue(currVou.getEmgPercent());
                if (currVou.getEmgPercent() != null) {
                    txtEmgPercent.setText(currVou.getEmgPercent().toString());
                }

                if (currVou.getAge() != null) {
                    txtAge.setText(currVou.getAge().toString());
                }
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
                }

                if (currVou.getDoctor() != null) {
                    txtDoctorNo.setText(currVou.getDoctor().getDoctorId());
                    txtDoctorName.setText(currVou.getDoctor().getDoctorName());
                } else {
                    txtDoctorNo.setText("");
                    txtDoctorName.setText("");
                }

                txtVouTotal.setValue(currVou.getVouTotal());
                txtDiscP.setValue(currVou.getDiscountP());
                txtDiscA.setValue(currVou.getDiscountA());
                txtTaxP.setValue(currVou.getTaxP());
                txtTaxA.setValue(currVou.getTaxA());
                txtPaid.setValue(currVou.getPaid());
                txtVouBalance.setValue(currVou.getVouBalance());
                txtBill.setText(currVou.getOtId());

                setEditStatus(currVou.getOpdInvId());
                applySecurityPolicy();
                tableModel.setVouStatus("EDIT");
                tableModel.setCanEdit(canEdit);

                txtPackageName.setText(currVou.getPkgName());
                txtPrice.setValue(currVou.getPkgPrice());
                if (canEdit) {
                    butRemove.setEnabled(true);
                } else {
                    butRemove.setEnabled(true);
                }
            } catch (Exception ex) {
                log.error("select OPDVouList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
            break;
            case "PackageSelect":
                if (selectObj != null) {
                    ClinicPackage cp = (ClinicPackage) selectObj;
                    currVou.setPkgId(cp.getId());
                    currVou.setPkgName(cp.getPackageName());
                    currVou.setPkgPrice(cp.getPrice());
                    txtPackageName.setText(currVou.getPkgName());
                    txtPrice.setValue(currVou.getPkgPrice());
                    butRemove.setEnabled(true);
                    savePackage(txtVouNo.getText().trim(), currVou.getPkgId());
                }
                break;
            case "CAL-TOTAL":
                calVouTotal();
                break;
        }

        tblService.requestFocusInWindow();
    }

    private void savePackage(String vouNo, Long pkgId) {
        if (pkgId != null) {
            tableModel.clear();
            String strSqlDelete = "delete from clinic_package_detail_his where dc_inv_no = '" + vouNo + "' and pkg_opt = 'OPD'";
            String strSql = "insert into clinic_package_detail_his(dc_inv_no, pkg_id, item_key, "
                    + "unit_qty,item_unit,qty_smallest, sys_price, usr_price, pk_detail_id, "
                    + "sys_amt, usr_amt, pkg_opt) \n"
                    + "select '" + vouNo + "', pkg_id, item_key, unit_qty, item_unit, qty_smallest, sys_price, "
                    + "usr_price, id, sys_amt, usr_amt, 'OPD' \n"
                    + "from clinic_package_detail where pkg_id = " + pkgId.toString();
            try {
                dao.execSql(strSqlDelete, strSql);
                strSql = "select item_key, qty_smallest\n"
                        + "from clinic_package_detail_his \n"
                        + "where pkg_opt = 'OPD' and pkg_id = " + pkgId.toString() + " and dc_inv_no = '" + vouNo + "' "
                        + "order by pk_detail_id";
                ResultSet rs = dao.execSQL(strSql);
                if (rs != null) {
                    HashMap<Integer, Double> doctFees = tableModel.getDoctFees();
                    ChargeType defaultChargeType = tableModel.getDefaultChargeType();
                    while (rs.next()) {
                        String itemKey = rs.getString("item_key");
                        Integer qty = rs.getInt("qty_smallest");
                        Integer serviceId = Integer.valueOf(itemKey.replace("OPD-", ""));
                        Service service = (Service) dao.find(Service.class, serviceId);
                        if (service != null) {
                            OPDDetailHis record = new OPDDetailHis();
                            record.setQuantity(qty);
                            record.setService(service);
                            record.setFees1(service.getFees1());
                            record.setFees2(service.getFees2());
                            record.setFees3(service.getFees3());
                            record.setFees4(service.getFees4());
                            record.setFees5(service.getFees5());
                            record.setFees6(service.getFees6());
                            record.setPercent(service.isPercent());
                            record.setReferDr(currVou.getDoctor());
                            record.setLabRemark(service.getLabRemark());
                            record.setFees(service.getFees());
                            record.setPkgItem(true);
                            double amt = record.getQuantity() * record.getFees();
                            record.setAmount(amt);
                            if (doctFees != null) {
                                if (doctFees.containsKey(service.getServiceId())) {
                                    record.setPrice(doctFees.get(service.getServiceId()));
                                } else {
                                    record.setPrice(service.getFees());
                                }
                            } else {
                                record.setPrice(service.getFees());
                            }

                            record.setFeesVersionId(service.getPriceVersionId());
                            record.setChargeType(defaultChargeType);
                            tableModel.addPackageItem(record);
                        }
                    }
                    calculatePackage();
                    tableModel.addNewRow();
                    tableModel.dataChange();
                    rs.close();
                }
            } catch (Exception ex) {
                log.error("savePackage : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    private String isNull(Integer integer) {
        String output;
        if (integer == null) {
            output = "";
        } else {
            output = integer.toString();
        }
        return output;
    }

    private Date getDOB() {
        int year;
        int month;
        int day;
        year = NumberUtil.NZeroInt(txtAge.getText());
        month = NumberUtil.NZeroInt(txtMonth.getText());
        day = NumberUtil.NZeroInt(txtDay.getText());
        String bdDate = DateUtil.getDOB1(year, month, day);
        return DateUtil.toDate(bdDate);
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

    public void getPatientBill(String regNo) {
        try {
            List<PatientBillPayment> listPBP = new ArrayList();
            Double totalBalance = 0.0;
            String currency = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();

            ResultSet resultSet = dao.getPro("patient_bill_payment",
                    regNo, DateUtil.toDateStrMYSQL(txtDate.getText()),
                    currency, Global.machineId);
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

            tblPatientBillTableModel.setListPBP(listPBP);
            txtBillTotal.setValue(totalBalance);
        } catch (SQLException ex) {
            log.error("PatientSearch : Patient_bill_Payment :" + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } catch (Exception ex) {
            log.error("getPatientBill : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void applySecurityPolicy() {
        if (lblStatus.getText().equals("EDIT")) {
            if (!Util1.hashPrivilege("OPDVoucherEditChange") || !canEdit) {
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
        txtDonorName.setEnabled(status);
        txtAge.setEditable(status);
        txtMonth.setEditable(status);
        txtDay.setEditable(status);
    }

    private void disableControl() {
        boolean status = false;
        txtPatientNo.setEnabled(status);
        txtPatientName.setEnabled(status);
        txtDoctorNo.setEnabled(status);
        cboCurrency.setEnabled(status);
        cboPaymentType.setEnabled(status);
        txtRemark.setEnabled(status);
        txtDonorName.setEnabled(status);
        txtAge.setEditable(status);
        txtMonth.setEditable(status);
        txtDay.setEditable(status);
    }

    private void setEditStatus(String invId) {
        //canEdit
        canEdit = Util1.hashPrivilege("OPDVoucherEditChange");
        boolean isAllowEdit = Util1.hashPrivilege("OPDCreditVoucherEdit");
        double vouPaid = NumberUtil.NZero(currVou.getPaid());
        if (!canEdit) {
            if (!isAllowEdit) {
                return;
            }
        }

        if (!Util1.hashPrivilege("CanEditOPDCheckPoint")) {
            try {
                List<SessionCheckCheckpoint> list = dao.findAllHSQL(
                        "select o from SessionCheckCheckpoint o where o.tranOption = 'OPD' "
                        + " and o.tranInvId = '" + invId + "'");
                if (list != null) {
                    if (!list.isEmpty()) {
                        canEdit = false;
                    }
                }
            } catch (Exception ex) {
                log.error("setEditStatus : " + ex.getMessage());
            }
            if (currVou != null) {
                if (currVou.getAdmissionNo() != null) {
                    if (!currVou.getAdmissionNo().trim().isEmpty()) {
                        AdmissionKey key = new AdmissionKey();
                        key.setRegister(currVou.getPatient());
                        key.setAmsNo(currVou.getAdmissionNo());
                        try {
                            Ams admPt = (Ams) dao.find(Ams.class, key);
                            if (admPt != null) {
                                if (!Util1.hashPrivilege("CanEditOnDC")) {
                                    canEdit = admPt.getDcStatus() == null;
                                }
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
                                "select * from c_bk_opd_his where opd_inv_id = '" + invId + "'");
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
            if (currVou != null) {
                if (currVou.getAdmissionNo() != null) {
                    if (!currVou.getAdmissionNo().trim().isEmpty()) {
                        AdmissionKey key = new AdmissionKey();
                        key.setRegister(currVou.getPatient());
                        key.setAmsNo(currVou.getAdmissionNo());
                        try {
                            Ams admPt = (Ams) dao.find(Ams.class, key);
                            if (admPt != null) {
                                if (!Util1.hashPrivilege("CanEditOnDC")) {
                                    canEdit = admPt.getDcStatus() == null;
                                }
                            }
                        } catch (Exception ex) {
                            log.error("setEditStatus Get Admission : " + invId + " : " + ex.toString());
                        } finally {
                            dao.close();
                        }
                    }
                }
            }

            /*String oneDayEdit = Util1.getPropValue("system.one.day.edit");
            if (oneDayEdit.equals("Y")) {
                if (canEdit) {
                    try {
                        List list = dao.findAllSQLQuery(
                                "select * from c_bk_opd_his where opd_inv_id = '" + invId + "'");
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
            }*/
            //canEdit = true;
        }
    }

    private void uploadToAccount(String vouNo) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            String rootUrl = Util1.getPropValue("system.intg.api.url");
            if (!rootUrl.isEmpty() && !rootUrl.equals("-")) {
                try ( CloseableHttpClient httpClient = createHttpClientWithTimeouts()) {
                    String url = rootUrl + "/opd";
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
            dao.execSql("update opd_his set intg_upd_status = null where opd_inv_id = '" + vouNo + "'");
        } catch (Exception ex) {
            log.error("opd updateNull : " + ex.getMessage());
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
        String deleteSQL;

        //All detail section need to explicity delete
        //because of save function only delete to join table
        try {
            deleteSQL = tableModel.getDeleteSql();
            if (deleteSQL != null) {
                dao.execSql(deleteSQL);
            }
        } catch (Exception ex) {
            log.error("deleteDetail : " + ex.toString());
        } finally {
            dao.close();
        }

        //delete section end
    }

    private void updateVouTotal(String vouNo) {
        String strSql = "update opd_his ph\n"
                + "join (select vou_no, sum(ifnull(amount,0)) as ttl_amt \n"
                + "from opd_details_his where vou_no = '" + vouNo + "' group by vou_no) pd\n"
                + "on ph.opd_inv_id = pd.vou_no set ph.vou_total = pd.ttl_amt\n"
                + "where ph.opd_inv_id = '" + vouNo + "'";
        try {
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("updateVouTotal : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private List<OPDDetailHis> getVerifiedUniqueId(String vouNo, List<OPDDetailHis> listDetail) {
        if (listDetail == null) {
            return null;
        }

        OPDDetailHis ddh = listDetail.stream().filter(o -> NumberUtil.NZeroInt(o.getUniqueId()) != 0)
                .max(Comparator.comparingInt(OPDDetailHis::getUniqueId))
                .orElse(null);
        int maxId = 0;
        if (ddh != null) {
            maxId = ddh.getUniqueId();
        }

        HashMap<Integer, OPDDetailHis> hm = new HashMap();
        for (OPDDetailHis tmp : listDetail) {
            if (NumberUtil.NZeroInt(tmp.getUniqueId()) != 0) {
                if (hm.containsKey(tmp.getUniqueId())) {
                    log.error("OPD Unique ID Error : " + tmp.getUniqueId());
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

        jLabel1 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPatientNo = new javax.swing.JTextField();
        txtPatientName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDate = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtDoctorNo = new javax.swing.JTextField();
        txtDoctorName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox();
        cboPaymentType = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblService = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtRecNo = new javax.swing.JTextField();
        txtTotalItem = new javax.swing.JTextField();
        chkAmount = new javax.swing.JCheckBox();
        chkA5 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPatientBill = new javax.swing.JTable();
        txtBillTotal = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtDonorName = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtAdmissionNo = new javax.swing.JTextField();
        butAdmit = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        txtAge = new javax.swing.JTextField();
        txtMonth = new javax.swing.JTextField();
        txtDay = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        txtPackageName = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        butRemove = new javax.swing.JButton();
        txtPrice = new javax.swing.JFormattedTextField();
        chkCloseBill = new javax.swing.JCheckBox();
        jLabel26 = new javax.swing.JLabel();
        txtEmgPercent = new javax.swing.JFormattedTextField();
        txtBill = new javax.swing.JTextField();
        butOTID = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        txtPaid = new javax.swing.JFormattedTextField();
        txtTaxP = new javax.swing.JFormattedTextField();
        txtVouTotal = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtVouBalance = new javax.swing.JFormattedTextField();
        txtTaxA = new javax.swing.JFormattedTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtDiscA = new javax.swing.JFormattedTextField();
        txtDiscP = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        spPrint = new javax.swing.JSpinner();
        butRefresh = new javax.swing.JButton();

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

        txtDate.setEditable(false);
        txtDate.setFont(Global.textFont);
        txtDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDateMouseClicked(evt);
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

        cboPaymentType.setFont(Global.textFont);
        cboPaymentType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPaymentTypeActionPerformed(evt);
            }
        });

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

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblStatus.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
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
        txtRecNo.setFont(Global.lableFont);
        txtRecNo.setForeground(new java.awt.Color(181, 175, 175));
        txtRecNo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecNo.setBorder(null);

        txtTotalItem.setEditable(false);
        txtTotalItem.setFont(Global.lableFont);
        txtTotalItem.setForeground(new java.awt.Color(181, 175, 175));
        txtTotalItem.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalItem.setBorder(null);

        chkAmount.setFont(Global.lableFont);
        chkAmount.setText("Check Amount");

        chkA5.setFont(Global.lableFont);
        chkA5.setText("A5");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRecNo)
                            .addComponent(txtTotalItem)))
                    .addComponent(lblStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkAmount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(chkA5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkAmount)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkA5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(txtRecNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtTotalItem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        tblPatientBill.setFont(Global.textFont);
        tblPatientBill.setModel(tblPatientBillTableModel);
        tblPatientBill.setRowHeight(23);
        jScrollPane2.setViewportView(tblPatientBill);

        txtBillTotal.setEditable(false);
        txtBillTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBillTotal.setFont(Global.textFont);

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Total : ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBillTotal))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBillTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)))
        );

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Donor Name ");

        txtDonorName.setFont(Global.textFont);
        txtDonorName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDonorNameFocusGained(evt);
            }
        });
        txtDonorName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDonorNameActionPerformed(evt);
            }
        });

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Adm No.");

        txtAdmissionNo.setEditable(false);
        txtAdmissionNo.setFont(Global.lableFont);

        butAdmit.setFont(Global.lableFont);
        butAdmit.setText("Admit");
        butAdmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butAdmitActionPerformed(evt);
            }
        });

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Age");

        txtAge.setFont(Global.textFont);
        txtAge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAgeActionPerformed(evt);
            }
        });

        txtMonth.setFont(Global.textFont);

        txtDay.setFont(Global.textFont);

        jLabel19.setText("Y");

        jLabel20.setText("M");

        jLabel21.setText("D");

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel24.setFont(Global.lableFont);
        jLabel24.setText("Package : ");

        txtPackageName.setEditable(false);
        txtPackageName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPackageNameMouseClicked(evt);
            }
        });

        jLabel25.setFont(Global.lableFont);
        jLabel25.setText("Price : ");

        butRemove.setFont(Global.lableFont);
        butRemove.setText("-");
        butRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRemoveActionPerformed(evt);
            }
        });

        txtPrice.setEditable(false);

        chkCloseBill.setFont(Global.lableFont);
        chkCloseBill.setText("Close Bill");

        jLabel26.setText("EMG %");

        txtEmgPercent.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEmgPercent.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtEmgPercentFocusLost(evt);
            }
        });
        txtEmgPercent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmgPercentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chkCloseBill, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPackageName)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addComponent(txtPrice)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(butRemove))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEmgPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(txtPackageName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(butRemove)
                    .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkCloseBill)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(txtEmgPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtBill.setEditable(false);
        txtBill.setFont(Global.lableFont);

        butOTID.setFont(Global.lableFont);
        butOTID.setText("Bill ID");
        butOTID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOTIDActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtPaid.setEditable(false);
        txtPaid.setFont(Global.textFont);

        txtTaxP.setEditable(false);
        txtTaxP.setFont(Global.textFont);

        txtVouTotal.setEditable(false);
        txtVouTotal.setFont(Global.textFont);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Discount :");

        txtVouBalance.setEditable(false);
        txtVouBalance.setFont(Global.textFont);

        txtTaxA.setEditable(false);
        txtTaxA.setFont(Global.textFont);

        jLabel15.setFont(Global.lableFont);
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Paid :");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Tax :");

        jLabel9.setFont(Global.lableFont);
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Vou Total :");

        txtDiscA.setEditable(false);
        txtDiscA.setFont(Global.textFont);

        txtDiscP.setEditable(false);
        txtDiscP.setFont(Global.textFont);

        jLabel16.setFont(Global.lableFont);
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Vou Balance :");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtPaid, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVouTotal, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtTaxP)
                            .addComponent(txtDiscP))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtDiscA)
                            .addComponent(txtTaxA, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtVouBalance))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDiscA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTaxA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addContainerGap())
        );

        jLabel18.setFont(Global.lableFont);
        jLabel18.setText("Print Copies :");

        spPrint.setFont(Global.textFont);

        butRefresh.setFont(Global.lableFont);
        butRefresh.setText("Refresh");
        butRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(spPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(butRefresh))
                        .addGap(44, 44, 44)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtDoctorNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtPatientNo, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtPatientName, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                                    .addComponent(txtDoctorName))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cboPaymentType, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel11)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtDonorName, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtAdmissionNo, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(butOTID)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBill, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel19))
                                    .addComponent(butAdmit))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel20)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtDay, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jLabel6, jLabel7});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtDoctorNo, txtPatientNo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtPatientNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPatientName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtDoctorNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDoctorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(txtDonorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(cboPaymentType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(txtAdmissionNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(butAdmit)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butOTID)
                            .addComponent(txtBill, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(spPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butRefresh)))
                .addContainerGap())
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
                if (!Util1.hashPrivilege("OPDVoucherEditChange")) {
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

    private void cboPaymentTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPaymentTypeActionPerformed
        if (cboBindStatus) {
            calVouTotal();
        }
    }//GEN-LAST:event_cboPaymentTypeActionPerformed

    private void txtDoctorNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDoctorNameMouseClicked
        if (evt.getClickCount() == 2) {
            if (!canEdit) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                        "Check Point", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (lblStatus.getText().equals("EDIT") && !txtDate.getText().equals(DateUtil.getTodayDateStr())) {
                if (!Util1.hashPrivilege("OPDVoucherEditChange")) {
                    return;
                }
            }
            //DoctorSearchDialog dialog = new DoctorSearchDialog(dao, this);
            DoctorSearchNameFilterDialog dialog = new DoctorSearchNameFilterDialog(dao, this);
        }
    }//GEN-LAST:event_txtDoctorNameMouseClicked

    private void txtPatientNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPatientNameMouseClicked
        if (evt.getClickCount() == 2) {
            if (!canEdit) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                        "Check Point", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (lblStatus.getText().equals("EDIT") && !txtDate.getText().equals(DateUtil.getTodayDateStr())) {
                if (!Util1.hashPrivilege("OPDVoucherEditChange")) {
                    return;
                }
            }
            PatientSearch dialog = new PatientSearch(dao, this);
        }
    }//GEN-LAST:event_txtPatientNameMouseClicked

    private void txtPatientNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPatientNoActionPerformed
        if (txtPatientNo.getText() == null || txtPatientNo.getText().trim().isEmpty()) {
            txtPatientNo.setText(null);
            txtPatientName.setText(null);
            txtDoctorNo.setText(null);
            txtDoctorName.setText(null);
            butOTID.setEnabled(false);
        } else {
            String tmpId = txtPatientNo.getText().trim();
            String[] tmpIds = tmpId.split("-");
            if (tmpIds.length > 1) {
                currVou.setVisitId(tmpId);
                String drId = tmpIds[0];
                String regNo = tmpIds[1];
                txtPatientNo.setText(regNo);
                getPatient();
                txtDoctorNo.setText(drId);
                getDoctor();
            } else {
                currVou.setVisitId(null);
                getPatient();
            }
        }
    }//GEN-LAST:event_txtPatientNoActionPerformed

    private void txtPatientNoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPatientNoFocusLost
        //getPatient();
    }//GEN-LAST:event_txtPatientNoFocusLost

    private void txtDoctorNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDoctorNoActionPerformed
        getDoctor();
    }//GEN-LAST:event_txtDoctorNoActionPerformed

  private void txtPatientNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPatientNameActionPerformed
      if (!canEdit) {
          JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                  "Check Point", JOptionPane.ERROR_MESSAGE);
          txtPatientName.setText(null);
          return;
      }
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

    private void txtDonorNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDonorNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDonorNameActionPerformed

    private void txtPatientNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPatientNoFocusGained
        focusCtrlName = "txtPatientNo";
        txtPatientNo.selectAll();
    }//GEN-LAST:event_txtPatientNoFocusGained

    private void txtDoctorNoFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDoctorNoFocusGained
        focusCtrlName = "txtDoctorNo";
        txtDoctorNo.selectAll();
    }//GEN-LAST:event_txtDoctorNoFocusGained

    private void txtDonorNameFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDonorNameFocusGained
        focusCtrlName = "txtDonorName";
        txtDonorName.selectAll();
    }//GEN-LAST:event_txtDonorNameFocusGained

    private void tblServiceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblServiceFocusGained
        focusCtrlName = "tblService";
        int selRow = tblService.getSelectedRow();
        if (selRow == -1) {
            tblService.changeSelection(0, 0, false, false);
        }
    }//GEN-LAST:event_tblServiceFocusGained

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
                pt.setAdmissionNo(regNo.getRegNo());
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

    private void tblServiceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblServiceFocusLost
        /*try{
         if(tblService.getCellEditor() != null){
         tblService.getCellEditor().stopCellEditing();
         }
         }catch(Exception ex){
            
         }*/
    }//GEN-LAST:event_tblServiceFocusLost

    private void txtAgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAgeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAgeActionPerformed

    private void tblServiceMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblServiceMouseClicked
        if (evt.getClickCount() == 2) {

            if (!Util1.getPropValue("system.opd.urgent").equals("-")
                    && !Util1.getPropValue("system.opd.urgent").isEmpty()) {
                int index = tblService.convertRowIndexToModel(tblService.getSelectedRow());
                tableModel.showUrgentDialog(index);
            }
        }
    }//GEN-LAST:event_tblServiceMouseClicked

    private void txtPackageNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPackageNameMouseClicked
        if (evt.getClickCount() == 2) {
            PackageSearchDialog dialog = new PackageSearchDialog(this, "OPD");
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_txtPackageNameMouseClicked

    private void butRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRemoveActionPerformed
        tableModel.removePackage();
        currVou.setPkgId(null);
        currVou.setPkgName(null);
        currVou.setPkgPrice(null);
        txtPackageName.setText("");
        txtPrice.setValue(0);
        txtDiscA.setValue(0);
        butRemove.setEnabled(false);
        try {
            String vouNo = txtVouNo.getText();
            String strSqlDelete = "delete from clinic_package_detail_his where dc_inv_no = '" + vouNo + "' and pkg_opt = 'OPD'";
            dao.execSql(strSqlDelete);
        } catch (Exception ex) {
            log.error("butRemoveActionPerformed : " + ex.toString());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butRemoveActionPerformed

    private void butOTIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOTIDActionPerformed
        openBill();
    }//GEN-LAST:event_butOTIDActionPerformed

    private void butRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRefreshActionPerformed
        if (lblStatus.getText().equals("EDIT")) {
            try {
                String vouNo = currVou.getOpdInvId();
                dao.execSql("update opd_his set intg_upd_status = null where opd_inv_id = '" + vouNo + "'");
                uploadToAccount(vouNo);
            } catch (Exception ex) {
                log.error("");
            } finally {
                dao.close();
            }

            newForm();
        }
    }//GEN-LAST:event_butRefreshActionPerformed

    private void txtEmgPercentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmgPercentActionPerformed
        float percent = NumberUtil.FloatZero(txtEmgPercent.getText());
        tableModel.addEMGPercent(percent);
        calculate();
    }//GEN-LAST:event_txtEmgPercentActionPerformed

    private void txtEmgPercentFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtEmgPercentFocusLost
        float percent = NumberUtil.FloatZero(txtEmgPercent.getText());
        tableModel.addEMGPercent(percent);
        calculate();
    }//GEN-LAST:event_txtEmgPercentFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butAdmit;
    private javax.swing.JButton butOTID;
    private javax.swing.JButton butRefresh;
    private javax.swing.JButton butRemove;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboPaymentType;
    private javax.swing.JCheckBox chkA5;
    private javax.swing.JCheckBox chkAmount;
    private javax.swing.JCheckBox chkCloseBill;
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
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JSpinner spPrint;
    private javax.swing.JTable tblPatientBill;
    private javax.swing.JTable tblService;
    private javax.swing.JTextField txtAdmissionNo;
    private javax.swing.JTextField txtAge;
    private javax.swing.JTextField txtBill;
    private javax.swing.JFormattedTextField txtBillTotal;
    private javax.swing.JFormattedTextField txtDate;
    private javax.swing.JTextField txtDay;
    private javax.swing.JFormattedTextField txtDiscA;
    private javax.swing.JFormattedTextField txtDiscP;
    private javax.swing.JTextField txtDoctorName;
    private javax.swing.JTextField txtDoctorNo;
    private javax.swing.JTextField txtDonorName;
    private javax.swing.JFormattedTextField txtEmgPercent;
    private javax.swing.JTextField txtMonth;
    private javax.swing.JTextField txtPackageName;
    private javax.swing.JFormattedTextField txtPaid;
    private javax.swing.JTextField txtPatientName;
    private javax.swing.JTextField txtPatientNo;
    private javax.swing.JFormattedTextField txtPrice;
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
