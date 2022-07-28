/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.auditlog;

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
import com.cv.app.inpatient.database.entity.InpService;
import com.cv.app.inpatient.ui.common.DCTableCellEditor;
import com.cv.app.inpatient.ui.common.DCTableModel;
import com.cv.app.inpatient.ui.util.AdmissionSearch;
import com.cv.app.inpatient.ui.util.DCDoctorFeeDialog;
import com.cv.app.inpatient.ui.util.DCVouSearchDialog;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.entity.PatientBillPayment;
import com.cv.app.opd.ui.entry.OPD;
import com.cv.app.opd.ui.util.DoctorSearchDialog;
import com.cv.app.ot.database.entity.DrDetailId;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.PatientBillTableModel;
import com.cv.app.pharmacy.util.GenVouNoImpl;
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
import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;
import org.josql.QueryResults;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author admin
 */
public class DCEntryRestoreView extends javax.swing.JPanel implements FormAction, KeyPropagate,
        SelectionObserver, KeyListener {

    static Logger log = Logger.getLogger(DCEntryRestoreView.class.getName());
    private AbstractDataAccess dao = Global.dao;
    private DCHis currVou = new DCHis();
    private DCTableModel tableModel = new DCTableModel(dao);
    private boolean cboBindStatus = false;
    private GenVouNoImpl vouEngine = null;
    private String focusCtrlName = "-";
    private PatientBillTableModel tblPatientBillTableModel = new PatientBillTableModel();
    private PaymentType ptCash;
    private PaymentType ptCredit;

    /**
     * Creates new form DCEntry1
     */
    public DCEntryRestoreView() {
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

    //FormAction
    @Override
    public void save() {
        /*if (isValidEntry()) {
            //DCConfirDialog dialog = new DCConfirDialog(currVou);
            //if (dialog.isStatus()) {

            try {
                Date d = new Date();
                dao.execProc("bk_dc",
                        currVou.getOpdInvId(),
                        DateUtil.toDateTimeStrMYSQL(d),
                        Global.loginUser.getUserId());
            } catch (Exception ex) {
                log.error("bk_dc : " + ex.getStackTrace()[0].getLineNumber() + " - " + currVou.getOpdInvId() + " - " + ex);
            } finally {
                dao.close();
            }

            try {
                if (currVou.getDcStatus() != null) {
                    String strSql = " update building_structure bs inner join admission a on bs.id = "
                            + " a.building_structure_id set bs.reg_no = null "
                            + " where a.ams_no ='" + currVou.getAdmissionNo() + "'";
                    dao.execSql(strSql);
                }

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

                if (currVou.getDcStatus() != null) {
                    Patient pt = currVou.getPatient();
                    AdmissionKey key = new AdmissionKey(pt, pt.getAdmissionNo());
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

                    if (pt.getAdmissionNo() != null) {
                        if (pt.getAdmissionNo().equals(currVou.getAdmissionNo())) {
                            pt.setAdmissionNo(null);
                        }
                    }
                    dao.save(pt);
                }
                newForm();
            } catch (Exception ex) {
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }*/
    }

    @Override
    public void newForm() {
        currVou = new DCHis();
        txtDoctorName.setText(null);
        txtDoctorNo.setText(null);
        txtPatientName.setText(null);
        txtPatientNo.setText(null);
        txtRemark.setText(null);
        txtVouNo.setText(null);
        tableModel.clear();
        txtPatientNo.setEditable(true);
        txtPatientName.setEditable(true);
        lblStatus.setText("NEW");
        txtPatientNo.requestFocusInWindow();
        txtAdmissionNo.setText(null);
        txtBedNo.setText(null);
        assignDefaultValue();
        txtBillTotal.setText(null);
        tblPatientBillTableModel.setListPBP(new ArrayList());
        txtPatientNo.requestFocus();
        butAdmit.setEnabled(false);
    }

    @Override
    public void history() {
        DCVouSearchDialog dialog = new DCVouSearchDialog(this, "ENTRY");
    }

    @Override
    public void delete() {

    }

    @Override
    public void deleteCopy() {

    }

    private void copyVoucher(String invNo) {
        try {
            DCHis tmpVou = (DCHis) dao.find(DCHis.class, invNo);

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
                log.error("selected DoctorSearch : " + ex.getMessage());
            } finally {
                dao.close();
            }
            break;
            case "PatientSearch":
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
                if (!Util1.getNullTo(patient.getAdmissionNo(), "").trim().isEmpty()) {
                    butAdmit.setEnabled(true);
                } else {
                    try {
                        butAdmit.setEnabled(false);
                        AdmissionKey key = new AdmissionKey();
                        key.setRegister(patient);
                        key.setAmsNo(patient.getAdmissionNo());
                        Ams ams = (Ams) dao.find(Ams.class, key);
                        if (ams != null) {
                            lblAdmissionDate.setText(DateUtil.toDateTimeStr(ams.getAmsDate(),
                                    "dd/MM/yyyy HH:mm:ss"));
                            if (ams.getBuildingStructure() != null) {
                                txtBedNo.setText(ams.getBuildingStructure().getDescription());
                            }
                        }
                        if (Util1.getPropValue("system.admission.paytype").equals("CREDIT")) {
                            cboPaymentType.setSelectedItem(ptCredit);
                        } else {
                            cboPaymentType.setSelectedItem(ptCash);
                        }
                    } catch (Exception ex) {
                        log.error("selected PatientSearch : " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                }
                getPatientBill(patient.getRegNo());
                break;
            case "DCVouList":
                try {
                newForm();

                currVou = (DCHis) selectObj;
                if (currVou.getListOPDDetailHis().size() > 0) {
                    tableModel.clear();
                    tableModel.setListOPDDetailHis(currVou.getListOPDDetailHis());
                }

                txtVouNo.setText(currVou.getOpdInvId());
                txtDate.setText(DateUtil.toDateStr(currVou.getInvDate()));
                cboCurrency.setSelectedItem(currVou.getCurrency());
                cboPaymentType.setSelectedItem(currVou.getPaymentType());
                txtRemark.setText(currVou.getRemark());
                cboDCStatus.setSelectedItem(currVou.getDcStatus());
                cboDiagnosis.setSelectedItem(currVou.getDiagnosis());
                txtAdmissionNo.setText(currVou.getAdmissionNo());
                cboAgeRange.setSelectedItem(currVou.getAgeRange());

                if (currVou.isDeleted()) {
                    lblStatus.setText("DELETED");
                } else {
                    lblStatus.setText("RESTORE");
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
        if (!vouEngine.isIsError()) {
            String vouNo = vouEngine.getVouNo();
            txtVouNo.setText(vouNo);

            if (vouNo.equals("-")) {
                log.error("DC voucher error : " + txtVouNo.getText() + " @ "
                        + txtDate.getText() + " @ " + vouEngine.getVouInfo());
                JOptionPane.showMessageDialog(Util1.getParent(),
                        "DC vou no error. Exit the program and try again.",
                        "DC Vou No", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } else {
                try {
                    List<DCHis> listOPDH = dao.findAllHSQL(
                            "select o from DCHis o where o.opdInvId = '" + vouNo + "'");
                    if (listOPDH != null) {
                        if (!listOPDH.isEmpty()) {
                            log.error("Duplicate DC voucher error : " + txtVouNo.getText() + " @ "
                                    + txtDate.getText());
                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Duplicate DC vou no. Exit the program and try again.",
                                    "DC Vou No", JOptionPane.ERROR_MESSAGE);
                            System.exit(1);
                        }
                    }
                } catch (Exception ex) {
                    log.error("genVouNo : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }
        } else {
            log.error("DC voucher error : " + txtVouNo.getText() + " @ "
                    + txtDate.getText() + " @ " + vouEngine.getVouInfo());
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "DC vou no error. Exit the program and try again.",
                    "DC Vou No", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getDoctor() {
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

    private void calcBalance() {
        if (cboPaymentType.getSelectedItem() != null) {
            PaymentType pt = (PaymentType) cboPaymentType.getSelectedItem();
            double discount = NumberUtil.NZero(txtDiscA.getValue());
            double tax = NumberUtil.NZero(txtTaxA.getValue());
            double vouTotal = NumberUtil.NZero(txtVouTotal.getValue());

            /*if (pt.getPaymentTypeId() == 1) {
             txtPaid.setValue((vouTotal + tax) - discount);
             } else {
             txtPaid.setValue(0);
             }*/
            double paid = NumberUtil.NZero(txtPaid.getValue());
            txtVouBalance.setValue((vouTotal + tax) - (discount + paid));
        } else {
            //Payment type is not selected
        }
    }

    private void getPatient() {
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

            JComboBox cboChargeType = new JComboBox();
            BindingUtil.BindCombo(cboChargeType, dao.findAll("ChargeType"));
            tblService.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cboChargeType));

            tblService.getModel().addTableModelListener(new TableModelListener() {

                @Override
                public void tableChanged(TableModelEvent e) {
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
            int row = tblService.getSelectedRow();

            if (row >= 0) {
                DCDetailHis opdh = tableModel.getOPDDetailHis(row);

                if (opdh.getService() != null) {
                    try {
                        if (tblService.getCellEditor() != null) {
                            tblService.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {
                        log.error(ex.getStackTrace()[0].getLineNumber() + " - " + ex);
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
        cboDCStatus.setSelectedIndex(0);
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
        boolean status = true;

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
            currVou.setInvDate(DateUtil.toDateTime(txtDate.getText()));
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

            if (lblStatus.getText().equals("EDIT") || lblStatus.getText().equals("DELETED")) {
                currVou.setUpdatedBy(Global.loginUser);
                currVou.setUpdatedDate(new Date());
            } else if (lblStatus.getText().equals("NEW")) {
                currVou.setCreatedBy(Global.loginUser);
                currVou.setDeleted(false);
                currVou.setSession(Global.sessionId);
            }

            try {
                if (tblService.getCellEditor() != null) {
                    tblService.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }
        }

        return status;
    }

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

    public double getPatientBalance(String regNo) {
        Double totalBalance = 0.0;
        try {
            String currency = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
            try ( //dao.open();
                    ResultSet resultSet = dao.getPro("patient_bill_payment",
                            regNo, DateUtil.toDateStrMYSQL(txtDate.getText()),
                            currency, Global.machineId)) {
                while (resultSet.next()) {
                    double bal = resultSet.getDouble("balance");
                    totalBalance += bal;
                }
            }
        } catch (Exception ex) {
            log.error("PatientSearch : Patient_bill_Payment :" + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }

        return totalBalance;
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
        cboDiagnosis = new javax.swing.JComboBox<String>();
        jPanel5 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cboAgeRange = new javax.swing.JComboBox<String>();
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
        txtTaxA = new javax.swing.JFormattedTextField();
        txtDiscA = new javax.swing.JFormattedTextField();
        txtVouTotal = new javax.swing.JFormattedTextField();
        txtPaid = new javax.swing.JFormattedTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtTaxP = new javax.swing.JFormattedTextField();
        txtDiscP = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        chkDetails = new javax.swing.JCheckBox();
        chkA5 = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        lblAdmissionDate = new javax.swing.JLabel();
        butRestore = new javax.swing.JButton();

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
        txtAdmissionNo.setFont(Global.textFont);

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
                        .addComponent(txtBedNo, javax.swing.GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
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
        txtBillTotal.setFont(Global.textFont);

        lblTotal.setFont(Global.textFont);
        lblTotal.setText("Total Balance : ");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(lblTotal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtBillTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBillTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal)))
        );

        txtTaxA.setEditable(false);
        txtTaxA.setFont(Global.textFont);

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

        jLabel18.setFont(Global.lableFont);
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("Paid :");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 204, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 141, Short.MAX_VALUE)
        );

        chkDetails.setText("Details");

        chkA5.setText("A5");
        chkA5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkA5ActionPerformed(evt);
            }
        });

        jLabel14.setText("Admission Date : ");

        butRestore.setText("Restore");
        butRestore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRestoreActionPerformed(evt);
            }
        });

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
                                .addComponent(chkA5)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(butRestore)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel16)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel15)
                                        .addComponent(jLabel18))
                                    .addComponent(jLabel10)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblAdmissionDate, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel10, jLabel15, jLabel16, jLabel18, jLabel9});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chkDetails)
                            .addComponent(chkA5)))
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtVouTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel14)
                            .addComponent(lblAdmissionDate, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtDiscA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10)
                                    .addComponent(txtDiscP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtTaxA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTaxP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel18))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtVouBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(butRestore))))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDateMouseClicked
        if (evt.getClickCount() == 2) {
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
            DoctorSearchDialog dialog = new DoctorSearchDialog(dao, this);
        }
    }//GEN-LAST:event_txtDoctorNameMouseClicked

    private void cboDCStatusFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboDCStatusFocusGained
        focusCtrlName = "cboDCStatus";
    }//GEN-LAST:event_cboDCStatusFocusGained

    private void cboPaymentTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPaymentTypeActionPerformed
        if (cboBindStatus) {
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
            DCDetailHis dcdHis = tableModel.getOPDDetailHis(index);
            if (dcdHis != null) {
                if (dcdHis.getListDCDF() != null) {
                    if (!dcdHis.getListDCDF().isEmpty()) {
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
            }
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

    private void butRestoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRestoreActionPerformed
        vouEngine.setPeriod(DateUtil.getPeriod(txtDate.getText()));
        genVouNo();
        String vouNo = vouEngine.getVouNo();
        String oldVouNo = currVou.getOpdInvId();
        String serail = vouNo.substring(0, 7);
        String month = vouNo.substring(7, 9);
        String year = vouNo.substring(9, 13);
        vouNo = serail + "-" + month + "-" + year;

        currVou.setOpdInvId(vouNo);

        try {
            currVou.setListOPDDetailHis(tableModel.getListOPDDetailHis());
            dao.save(currVou);
            vouEngine.updateVouNo();
            newForm();
        } catch (Exception ex) {
            log.error("butRestoreActionPerformed : " + oldVouNo + ex.toString());
        } finally {
            dao.close();
        }

        log.error("save log - Old VouNo : " + oldVouNo + " New VouNo : " + vouNo);
    }//GEN-LAST:event_butRestoreActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butAdmit;
    private javax.swing.JButton butRestore;
    private javax.swing.JComboBox<String> cboAgeRange;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboDCStatus;
    private javax.swing.JComboBox<String> cboDiagnosis;
    private javax.swing.JComboBox cboPaymentType;
    private javax.swing.JCheckBox chkA5;
    private javax.swing.JCheckBox chkDetails;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblAdmissionDate;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTotal;
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
