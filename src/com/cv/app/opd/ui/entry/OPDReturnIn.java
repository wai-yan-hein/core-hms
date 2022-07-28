/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
import com.cv.app.util.DateUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.opd.database.entity.OPDDetailHis;
import com.cv.app.opd.database.entity.OPDHis;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.entity.DoctorFeesMapping;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.RegNo;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.AdmissionKey;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.opd.database.entity.PatientBillPayment;
import com.cv.app.opd.database.tempentity.TempAmountLink;
import com.cv.app.opd.ui.common.OPDTableCellEditor;
import com.cv.app.opd.ui.common.OPDTableModel;
import com.cv.app.opd.ui.util.AmountLinkDialog;
import com.cv.app.opd.ui.util.DoctorSearchNameFilterDialog;
import com.cv.app.opd.ui.util.OPDConfirDialog;
import com.cv.app.opd.ui.util.OPDVouSearchDialog;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.ot.ui.common.OTDrFeeTableCellEditor;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.PatientBillTableModel;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.ui.common.VouFormatFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author Eitar
 */
public class OPDReturnIn extends javax.swing.JPanel implements FormAction, KeyPropagate,
        SelectionObserver, KeyListener {

    static Logger log = Logger.getLogger(OPDReturnIn.class.getName());
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

    /**
     * Creates new form OPD
     */
    public OPDReturnIn() {
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

    @Override
    public void save() {
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot save edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isValidEntry()) {
            boolean status;

            if (chkAmount.isSelected()) {
                OPDConfirDialog dialog = new OPDConfirDialog(currVou);
                status = dialog.isStatus();
            } else {
                status = true;
            }

            if (status) {
                try {
                    Date d = new Date();
                    dao.execProc("bk_opd",
                            currVou.getOpdInvId(),
                            DateUtil.toDateTimeStrMYSQL(d),
                            Global.loginUser.getUserId(),
                            Global.machineId,
                            currVou.getVouTotal().toString(),
                            currVou.getDiscountA().toString(),
                            currVou.getPaid().toString(),
                            currVou.getVouBalance().toString());
                } catch (Exception ex) {
                    log.error("bk_opd : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
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

                    dao.save(currVou);
                    if (lblStatus.getText().equals("NEW")) {
                        vouEngine.updateVouNo();
                    }

                    newForm();
                } catch (Exception ex) {
                    log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
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
        tableModel.clear();
        tableModel.setVouStatus("NEW");
        txtPatientNo.setEditable(true);
        txtPatientName.setEditable(true);
        txtPatientNo.requestFocusInWindow();
        lblStatus.setText("NEW");
        tableModel.setCanEdit(canEdit);
        assignDefaultValue();
        txtBillTotal.setText(null);
        tblPatientBillTableModel.setListPBP(new ArrayList());
        butAdmit.setEnabled(false);
        txtPatientNo.requestFocus();
        lblOTID.setText(null);
        tableModel.setReaderDoctor("-");
        applySecurityPolicy();
    }

    @Override
    public void history() {
        OPDVouSearchDialog dialog = new OPDVouSearchDialog(this, "ENTRY");
    }

    @Override
    public void delete() {
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot delete edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (lblStatus.getText().equals("EDIT")) {
            if (!Util1.hashPrivilege("OPDVoucherEditChange")) {
                return;
            }
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
                    dao.execProc("bk_opd",
                            currVou.getOpdInvId(),
                            DateUtil.toDateTimeStrMYSQL(d),
                            Global.loginUser.getUserId(),
                            Global.machineId,
                            currVou.getVouTotal().toString(),
                            currVou.getDiscountA().toString(),
                            currVou.getPaid().toString(),
                            currVou.getVouBalance().toString());
                } catch (Exception ex) {
                    log.error("bk_opd : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
                } finally {
                    dao.close();
                }

                currVou.setDeleted(true);
                try {
                    dao.save(currVou);
                    if (lblStatus.getText().equals("NEW")) {
                        vouEngine.updateVouNo();
                    }

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
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot delete and copy edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (lblStatus.getText().equals("EDIT")) {
            if (!Util1.hashPrivilege("OPDVoucherEditChange")) {
                return;
            }
        }
        int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete and copy?",
                "OPD voucher delete", JOptionPane.YES_NO_OPTION);

        if (yes_no == 0) {
            isDeleteCopy = true;
            currVou.setDeleted(true);
            try {
                Date d = new Date();
                dao.execProc("bk_opd",
                        currVou.getOpdInvId(),
                        DateUtil.toDateTimeStrMYSQL(d),
                        Global.loginUser.getUserId(),
                        Global.machineId,
                        currVou.getVouTotal().toString(),
                        currVou.getDiscountA().toString(),
                        currVou.getPaid().toString(),
                        currVou.getVouBalance().toString());
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
            List<OPDDetailHis> listDetailTmp = tmpVou.getListOPDDetailHis();

            for (OPDDetailHis detail : listDetailTmp) {
                OPDDetailHis tmpDetail = new OPDDetailHis();
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
        try {
            Date d = new Date();
            dao.execProc("bk_opd",
                    currVou.getOpdInvId(),
                    DateUtil.toDateTimeStrMYSQL(d),
                    Global.loginUser.getUserId(),
                    Global.machineId,
                    currVou.getVouTotal().toString(),
                    currVou.getDiscountA().toString(),
                    currVou.getPaid().toString(),
                    currVou.getVouBalance().toString());
        } catch (Exception ex) {
            log.error("bk_opd : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
        } finally {
            dao.close();
        }

        boolean status = false;
        if (isValidEntry()) {
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
                        dao.save(currVou);
                        if (lblStatus.getText().equals("NEW")) {
                            vouEngine.updateVouNo();
                        }
                    }
                } catch (Exception ex) {
                    log.error("print : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    JOptionPane.showMessageDialog(Util1.getParent(), "Error cannot print.",
                            "OPD print", JOptionPane.ERROR_MESSAGE);
                    return;
                } finally {
                    dao.close();
                }
            }
        }

        if (!status) {
            return;
        }
        //Properties prop = ReportUtil.loadReportPathProperties();
        String reportName = Util1.getPropValue("report.file.opd");
        String reportPath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path")
                + reportName;
        String printerName = Util1.getPropValue("report.vou.printer");
        Map<String, Object> params = new HashMap();
        params.put("link_amt_status", "N");
        params.put("link_amt", 0);
        if (Util1.getPropValue("system.link.amount").equals("OPD")
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
                        + "		   where ifnull(paid,0) <> 0 and tran_option in ('OPD','OT') and deleted = false\n"
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

        String compName = Util1.getPropValue("report.company.name");
        String printMode = Util1.getPropValue("report.vou.printer.mode");
        String phoneNo = Util1.getPropValue("report.phone");
        String imagePath = Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path") + "\\img\\logo.jpg";

        params.put("imagePath", imagePath);
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
        params.put("grandTotal", currVou.getVouTotal());
        params.put("paid", currVou.getPaid());
        params.put("discount", currVou.getDiscountA());
        params.put("tax", currVou.getTaxA());
        params.put("balance", currVou.getVouBalance());
        params.put("user", Global.loginUser.getUserShortName());
        params.put("compName", compName);
        params.put("phoneNo", phoneNo);
        params.put("remark", txtRemark.getText());
        params.put("user_id", Global.machineId);
        params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));
        params.put("REPORT_CONNECTION", dao.getConnection());

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
            ReportUtil.viewReport(reportPath, params, tableModel.getListOPDDetailHis());
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
            tblService.getTableHeader().setFont(Global.lableFont);
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
            BindingUtil.BindCombo(cboTechnician,
                    dao.findAllHSQL("select o from Technician o where o.active = true order by o.techName"));
            tblService.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(cboTechnician));

            tblService.getModel().addTableModelListener(new TableModelListener() {

                @Override
                public void tableChanged(TableModelEvent e) {
                    txtVouTotal.setValue(tableModel.getTotal());
                    txtTotalItem.setText(Integer.toString((tableModel.getTotalRecord() - 1)));
                    calcBalance();
                }
            });

            tblService.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblService.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {

                @Override
                public void valueChanged(ListSelectionEvent e) {
                    txtRecNo.setText(Integer.toString(tblService.getSelectedRow() + 1));
                }
            });

            tblPatientBill.getColumnModel().getColumn(0).setPreferredWidth(180);//Bill Name
            tblPatientBill.getColumnModel().getColumn(1).setPreferredWidth(70);//Amount
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
                    return;
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
        genVouNo();
    }

    private void calcBalance() {
        if (cboPaymentType.getSelectedItem() != null) {
            PaymentType pt = (PaymentType) cboPaymentType.getSelectedItem();
            double discount = NumberUtil.NZero(txtDiscA.getValue());
            double tax = NumberUtil.NZero(txtTaxA.getValue());
            double vouTotal = NumberUtil.NZero(txtVouTotal.getValue());

            if (pt.getPaymentTypeId() == 1) {
                txtPaid.setValue((vouTotal + tax) - discount);
            } else {
                txtPaid.setValue(0);
            }

            double paid = NumberUtil.NZero(txtPaid.getValue());
            txtVouBalance.setValue((vouTotal + tax) - (discount + paid));
        } else {
            //Payment type is not selected
        }
    }

    private boolean isValidEntry() {
        if (lblStatus.getText().equals("NEW")) {
            try {
                long cnt = dao.getRowCount("select count(*) from c_bk_opd_his where opd_date >= '"
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

        boolean status = true;

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

            if (lblOTID.getText() == null) {
                currVou.setOtId(null);
            } else if (!lblOTID.getText().isEmpty()) {
                currVou.setOtId(lblOTID.getText());
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
                Doctor doctor = (Doctor) selectObj;
                doctor = (Doctor) dao.find(Doctor.class, doctor.getDoctorId());

                if (doctor.getListFees() != null) {
                    if (doctor.getListFees().size() > 0) {
                        List<DoctorFeesMapping> listFees = doctor.getListFees();
                        HashMap<Integer, Double> doctFees = new HashMap();

                        for (DoctorFeesMapping dfm : listFees) {
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
                txtDoctorNo.setText(doctor.getDoctorId());
                txtDoctorName.setText((doctor.getDoctorName()));
                txtVouTotal.setValue(tableModel.getTotal());
                calcBalance();
                tblService.requestFocus();
                tableModel.addAutoServiceByDoctor();
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
                txtAdmissionNo.setText(patient.getAdmissionNo());
                txtAge.setText(isNull(patient.getAge()));
                txtMonth.setText(isNull(patient.getMonth()));
                txtDay.setText(isNull(patient.getDay()));

                /*if (!Util1.getNullTo(patient.getAdmissionNo(), "").trim().isEmpty()) {
                        butAdmit.setEnabled(true);
                    } else*/
                if (Util1.getNullTo(patient.getAdmissionNo(), "").trim().isEmpty()) {
                    butAdmit.setEnabled(true);
                    cboPaymentType.setSelectedItem(ptCash);
                } else {
                    butAdmit.setEnabled(false);
                    if (Util1.getPropValue("system.admission.paytype").equals("CREDIT")) {
                        cboPaymentType.setSelectedItem(ptCredit);
                    } else {
                        cboPaymentType.setSelectedItem(ptCash);
                    }
                }
                txtPatientNo.setText(patient.getRegNo());
                txtPatientName.setText(patient.getPatientName());
                txtPatientName.setEditable(false);
                if (patient.getDoctor() != null) {
                    selected("DoctorSearch", patient.getDoctor());
                }
                txtDoctorNo.requestFocus();
                if (Util1.nullToBlankStr(patient.getAdmissionNo()).isEmpty()) {
                    lblOTID.setText(patient.getOtId());
                } else {
                    lblOTID.setText(null);
                }
                getPatientBill(patient.getRegNo());
            } catch (Exception ex) {
                log.error("select PatientSearch : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
            break;
            case "OPDVouList":
                String vouId = ((OPDHis) selectObj).getOpdInvId();
                try {
                    newForm();

                    currVou = (OPDHis) dao.find(OPDHis.class, vouId);
                    if (currVou.getListOPDDetailHis().size() > 0) {
                        tableModel.clear();
                        tableModel.setListOPDDetailHis(currVou.getListOPDDetailHis());
                    }

                    txtVouNo.setText(currVou.getOpdInvId());
                    txtDate.setText(DateUtil.toDateStr(currVou.getInvDate()));
                    cboCurrency.setSelectedItem(currVou.getCurrency());
                    cboPaymentType.setSelectedItem(currVou.getPaymentType());
                    txtRemark.setText(currVou.getRemark());
                    txtAdmissionNo.setText(currVou.getAdmissionNo());

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
                    lblOTID.setText(currVou.getOtId());

                    setEditStatus(currVou.getOpdInvId());
                    applySecurityPolicy();
                    tableModel.setVouStatus("EDIT");
                    tableModel.setCanEdit(canEdit);
                } catch (Exception ex) {
                    log.error("select OPDVouList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                } finally {
                    dao.close();
                }
                break;
        }

        tblService.requestFocusInWindow();
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
                Doctor dr;

                dao.open();
                dr = (Doctor) dao.find(Doctor.class, txtDoctorNo.getText());
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
                disableControl();
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
        /*List<SessionCheckCheckpoint> list = dao.findAllHSQL(
                "select o from SessionCheckCheckpoint o where o.tranOption = 'OPD' "
                + " and o.tranInvId = '" + invId + "'");*/
        if (!Util1.hashPrivilege("CanEditOPDCheckPoint")) {
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
        } else {
            canEdit = true;
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
        txtVouTotal = new javax.swing.JFormattedTextField();
        txtDiscA = new javax.swing.JFormattedTextField();
        txtTaxA = new javax.swing.JFormattedTextField();
        txtPaid = new javax.swing.JFormattedTextField();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtDiscP = new javax.swing.JFormattedTextField();
        txtTaxP = new javax.swing.JFormattedTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtRecNo = new javax.swing.JTextField();
        txtTotalItem = new javax.swing.JTextField();
        chkAmount = new javax.swing.JCheckBox();
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
        jLabel18 = new javax.swing.JLabel();
        lblOTID = new javax.swing.JLabel();
        txtMonth = new javax.swing.JTextField();
        txtDay = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No ");

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

        txtVouTotal.setEditable(false);
        txtVouTotal.setFont(Global.textFont);

        txtDiscA.setEditable(false);
        txtDiscA.setFont(Global.textFont);

        txtTaxA.setEditable(false);
        txtTaxA.setFont(Global.textFont);

        txtPaid.setEditable(false);
        txtPaid.setFont(Global.textFont);

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

        jLabel14.setFont(Global.lableFont);
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Tax :");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("Paid :");

        jLabel16.setFont(Global.lableFont);
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("Vou Balance :");

        lblStatus.setText("NEW");

        jLabel23.setForeground(new java.awt.Color(182, 175, 175));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Rec No : ");

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

        chkAmount.setText("Check Amount");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTotalItem, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRecNo, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkAmount))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel22, jLabel23});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkAmount)
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

        tblPatientBill.setFont(Global.textFont);
        tblPatientBill.setModel(tblPatientBillTableModel);
        tblPatientBill.setRowHeight(23);
        jScrollPane2.setViewportView(tblPatientBill);

        txtBillTotal.setEditable(false);
        txtBillTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBillTotal.setFont(Global.textFont);

        jLabel13.setFont(Global.textFont);
        jLabel13.setText("Total : ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBillTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        txtAdmissionNo.setFont(Global.textFont);

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

        jLabel18.setText("OT ID");

        txtMonth.setFont(Global.textFont);

        txtDay.setFont(Global.textFont);

        jLabel19.setText("Y");

        jLabel20.setText("M");

        jLabel21.setText("D");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1084, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtPatientNo, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                                    .addComponent(txtDoctorNo))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtPatientName)
                                    .addComponent(txtDoctorName, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE))))
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel7))
                                .addGap(18, 18, 18)
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
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtDonorName, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                    .addComponent(txtAdmissionNo)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(txtRemark)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblOTID, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(92, 92, 92))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel19))
                                    .addComponent(butAdmit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDay, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
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
                                    .addComponent(txtTaxP, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                                    .addComponent(txtDiscP))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtDiscA, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                                    .addComponent(txtTaxA)))
                            .addComponent(txtVouBalance))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel5, jLabel6, jLabel7});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel14, jLabel15, jLabel16});

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
                    .addComponent(jLabel11)
                    .addComponent(txtDonorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtPatientNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPatientName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(cboPaymentType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(txtAdmissionNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butAdmit))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOTID, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtDoctorNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDoctorName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addGap(10, 10, 10)
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
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            calcBalance();
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
        getPatient();
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
                calcBalance();
            }
        }
    }//GEN-LAST:event_tblServiceMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butAdmit;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboPaymentType;
    private javax.swing.JCheckBox chkAmount;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblOTID;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblPatientBill;
    private javax.swing.JTable tblService;
    private javax.swing.JTextField txtAdmissionNo;
    private javax.swing.JTextField txtAge;
    private javax.swing.JFormattedTextField txtBillTotal;
    private javax.swing.JFormattedTextField txtDate;
    private javax.swing.JTextField txtDay;
    private javax.swing.JFormattedTextField txtDiscA;
    private javax.swing.JFormattedTextField txtDiscP;
    private javax.swing.JTextField txtDoctorName;
    private javax.swing.JTextField txtDoctorNo;
    private javax.swing.JTextField txtDonorName;
    private javax.swing.JTextField txtMonth;
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
