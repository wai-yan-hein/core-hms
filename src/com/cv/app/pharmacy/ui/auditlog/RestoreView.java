/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.auditlog;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.pharmacy.ui.common.SaleExpTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.common.TTranDetailTableModel;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.TransactionTableModel;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.SaleTableModel;
import com.cv.app.pharmacy.database.entity.TraderPayHis;
import com.cv.app.pharmacy.database.entity.SaleDetailHis;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.SaleOutstand;
import com.cv.app.pharmacy.database.entity.VouStatus;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.SaleWarranty;
import com.cv.app.pharmacy.database.entity.Customer;
import com.cv.app.pharmacy.database.entity.SaleExpense;
import com.cv.app.pharmacy.database.entity.SaleHis;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.RegNo;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.entity.PatientBillPayment;
import com.cv.app.opd.ui.util.DoctorSearchDialog;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.controller.SchoolDataAccess;
import com.cv.app.pharmacy.database.helper.Stock;
import com.cv.app.pharmacy.database.helper.TTranDetail;
import com.cv.app.pharmacy.database.helper.TraderTransaction;
import com.cv.app.pharmacy.database.tempentity.TraderUnpaidVou;
import com.cv.app.pharmacy.database.view.VMarchant;
import com.cv.app.pharmacy.ui.common.PatientBillTableModel;
import com.cv.app.pharmacy.ui.common.SaleStockTableModel;
import com.cv.app.pharmacy.ui.util.MedListDialog1;
import com.cv.app.pharmacy.ui.util.MedPriceAutoCompleter;
import com.cv.app.pharmacy.ui.util.PaymentListDialog;
import com.cv.app.pharmacy.ui.util.SaleOutstandingDialog;
import com.cv.app.pharmacy.ui.util.SaleWarrantyDialog;
import com.cv.app.pharmacy.ui.util.MarchantSearch;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.pharmacy.util.StockList;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.jms.MapMessage;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableCollections;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author WSwe
 */
public class RestoreView extends javax.swing.JPanel implements SelectionObserver, FormAction,
        MedInfo, KeyPropagate, KeyListener {

    static Logger log = Logger.getLogger(RestoreView.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private GenVouNoImpl vouEngine = null;
    private List<SaleDetailHis> listDetail = ObservableCollections.observableList(new ArrayList());
    private List<SaleExpense> listExpense = new ArrayList();
    private MedicineUP medUp = new MedicineUP(dao);
    private BestAppFocusTraversalPolicy focusPolicy;
    private SaleTableModel saleTableModel = new SaleTableModel(listDetail, dao,
            medUp, this, this);
    private SaleHis currSaleVou = new SaleHis();
    private SaleExpTableModel expTableModel = new SaleExpTableModel(listExpense);
    private StockList stockList = new StockList(dao, medUp);
    private TransactionTableModel tranTableModel = new TransactionTableModel();
    private List<TTranDetail> listTTDetail = new ArrayList();
    private TTranDetailTableModel ttdTableModel = new TTranDetailTableModel();
    private String deleteOutstandList;
    private String strLastSaleDate;
    private boolean haveTransaction = false;
    private String strPrvDate;
    private Object prvLocation;
    private Object prvPymet;
    private final PaymentType ptCash;
    private final PaymentType ptCredit;
    private List<String> tmpVouList = new ArrayList(); //For temp voucher
    private String focusCtrlName = "-";
    private PatientBillTableModel tblPatientBillTableModel = new PatientBillTableModel();
    private SaleStockTableModel stockTableModel = new SaleStockTableModel();

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
            if (!focusCtrlName.equals("-")) {
                if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                        && focusCtrlName.equals("txtCusId")) {
                    txtDrCode.requestFocus();
                } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                        && focusCtrlName.equals("txtDrCode")) {
                    //cboLocation.requestFocus();
                    txtRemark.requestFocus();
                } else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                        && focusCtrlName.equals("txtRemark")) {
                    /*if (!cboLocation.isPopupVisible()) {
                     cboPayment.requestFocus();
                     }*/
                    //cboLocation.requestFocus();
                    //txtSaleDate.requestFocus();
                    tblSale.requestFocus();
                } /*else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_ENTER)
                 && focusCtrlName.equals("txtSaleDate")) {
                 tblSale.requestFocus();
                 }*/ else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtCusId")) {
                    //txtSaleDate.requestFocus();
                    txtRemark.requestFocus();
                } /*else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtSaleDate")) {
                 txtDrCode.requestFocus();
                 }*/ else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtDrCode")) {
                    txtCusId.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("cboPayment")) {
                    if (!cboPayment.isPopupVisible()) {
                        cboLocation.requestFocus();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("tblSale")) {
                    int selRow = tblSale.getSelectedRow();
                    if (selRow == -1 || selRow == 0) {
                        txtDrCode.requestFocus();
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    /**
     * Creates new form Sale
     */
    public RestoreView() {
        initComponents();

        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
            dao.open();
            initCombo();
            initSaleTable();
            initExpenseTable();
            dao.close();
        } catch (Exception ex) {
            log.error("Sale : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        actionMapping();
        initTextBoxAlign();
        initTextBoxValue();
        //formActionMapping();
        addSelectionListenerTblTransaction();
        butPayment.setVisible(false);
        if (Util1.getPropValue("system.login.default.value").equals("Y")) {
            if (Global.loginDate == null) {
                Global.loginDate = DateUtil.getTodayDateStr();
            }
            txtSaleDate.setText(Global.loginDate);
        } else {
            txtSaleDate.setText(DateUtil.getTodayDateStr());
        }
        saleTableModel.setSaleDate(DateUtil.toDate(txtSaleDate.getText()));
        vouEngine = new GenVouNoImpl(dao, "Sale", DateUtil.getPeriod(txtSaleDate.getText()));
        genVouNo();
        //Tax
        txtTaxP.setText(Util1.getPropValue("system.sale.tax.percent"));

        addNewRow();
        initForFocus();

        //applyFocusPolicy();
        //AddFocusMoveKey();
        //jPanel1.setFocusCycleRoot(false);
        //jPanel1.setFocusTraversalPolicyProvider(true);
        //jPanel1.setFocusTraversalPolicy(focusPolicy);
        //jPanel7.setFocusTraversalPolicy(new SimpleFocusTraversalPolicy(txtCusId, txtDrCode));
        //jPanel7.setFocusTraversalPolicyProvider(true);
        //jPanel7.setFocusCycleRoot(false);
        saleTableModel.setParent(tblSale);
        lblStatus.setText("NEW");

        String cusType = Util1.getPropValue("system.sale.default.cus.type");
        if (cusType.isEmpty()) {
            saleTableModel.setCusType("N","con");
        } else {
            saleTableModel.setCusType(cusType,"con");
        }
        applySecurityPolicy();

        switch (Util1.getPropValue("system.app.usage.type")) {
            case "Hospital":
                lblPatient.setText("Patient No/Name");
                lblDoctor.setVisible(true);
                txtDrCode.setVisible(true);
                txtDrName.setVisible(true);
                txtCusAddress.setVisible(false);
                lblRemark1.setVisible(false);
                txtRemark1.setVisible(false);
                butWarranty.setVisible(false);
                butAdmit.setVisible(true);
                butAdmit.setEnabled(false);
                jPanel5.setVisible(false);
                tblStockList.setVisible(true);
                break;
            case "School":
                lblPatient.setText("Student No/Name");
                lblDoctor.setVisible(false);
                txtDrCode.setVisible(false);
                txtDrName.setVisible(false);
                lblRemark1.setVisible(false);
                txtRemark1.setVisible(false);
                txtCusAddress.setVisible(false);
                butAdmit.setVisible(false);
                jScrollPane6.setVisible(false);
                break;
            default:
                lblPatient.setText("Cus No/Name");
                lblDoctor.setText("Sale Man");
                /*lblDoctor.setVisible(false);
                 txtDrCode.setVisible(false);
                 txtDrName.setVisible(false);*/
                jScrollPane5.setVisible(false);
                tblPatientBill.setVisible(false);
                txtBillTotal.setVisible(false);
                jLabel4.setVisible(false);
                butAdmit.setVisible(false);
                jScrollPane6.setVisible(false);
                break;
        }

        butWarranty.setVisible(false);
        //butOutstanding.setVisible(false);
        ptCash = (PaymentType) dao.find(PaymentType.class,
                NumberUtil.NZeroInt(Util1.getPropValue("system.paymenttype.cash")));
        ptCredit = (PaymentType) dao.find(PaymentType.class,
                NumberUtil.NZeroInt(Util1.getPropValue("system.paymenttype.credit")));
        assignDefaultValue();

        //timerFocus();
    }

    private void initForFocus() {
        txtCusId.addKeyListener(this);
        txtDrCode.addKeyListener(this);
        txtRemark.addKeyListener(this);
        txtSaleDate.addKeyListener(this);
        cboLocation.getEditor().getEditorComponent().addKeyListener(this);
        cboLocation.getEditor().getEditorComponent().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cboLocation.requestFocus();
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
        cboPayment.getEditor().getEditorComponent().addKeyListener(this);
        cboPayment.getEditor().getEditorComponent().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cboPayment.requestFocus();
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
        tblSale.addKeyListener(this);
    }

    private void assignDefaultValue() {
        Object tmpObj;
        String tmpCusId = "-";
        //tmpObj = Util1.getDefaultValue("Trader");
        switch (Util1.getPropValue("system.app.usage.type")) {
            case "Hospital":
                cboPayment.setSelectedItem(ptCash);
                break;
            case "School":
                break;
            default:
                tmpObj = dao.find(Trader.class, Util1.getPropValue("system.default.customer"));
                if (tmpObj != null) {
                    tmpCusId = ((Trader) tmpObj).getTraderId();
                    selected("CustomerList", tmpObj);
                }
                if (currSaleVou.getCustomerId() != null) {
                    if (currSaleVou.getCustomerId().getTraderId().equals(tmpCusId)) {
                        cboPayment.setSelectedItem(ptCash);
                    } else {
                        cboPayment.setSelectedItem(ptCredit);
                    }
                }
                break;
        }

        tmpObj = Util1.getDefaultValue("Currency");
        if (tmpObj != null) {
            cboCurrency.setSelectedItem(tmpObj);
        }

        if (prvLocation != null) {
            tmpObj = prvLocation;
        } else if (Util1.getPropValue("system.login.default.value").equals("Y")) {
            tmpObj = Global.loginLocation;
        } else {
            tmpObj = Util1.getDefaultValue("Location");
        }
        if (tmpObj != null) {
            cboLocation.setSelectedItem(tmpObj);
            saleTableModel.setLocation((Location) tmpObj);
        }

        /*if (prvPymet != null) {
         tmpObj = prvPymet;
         } else {
         tmpObj = Util1.getDefaultValue("PaymentType");
         }

         if (tmpObj != null) {
         cboPayment.setSelectedItem(tmpObj);
         }*/
        tmpObj = Util1.getDefaultValue("VouStatus");
        if (tmpObj != null) {
            cboVouStatus.setSelectedItem(tmpObj);
        }
    }

    private void genVouNo() {
        String vouNo = vouEngine.getVouNo();
        txtVouNo.setText(vouNo);
        List<SaleHis> listSH = dao.findAllHSQL(
                "select o from SaleHis o where o.saleInvId = '" + vouNo + "'");
        if (listSH != null) {
            if (!listSH.isEmpty()) {
                log.error("Duplicate Sale vour error : " + txtVouNo.getText() + " @ "
                        + txtSaleDate.getText());
                JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate sale vou no. Exit the program and try again.",
                        "Sale Vou No", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void initCombo() {
        BindingUtil.BindCombo(cboPayment, dao.findAll("PaymentType"));
        BindingUtil.BindCombo(cboLocation, dao.findAll("Location"));
        BindingUtil.BindCombo(cboVouStatus, dao.findAll("VouStatus"));
        BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));

        ComBoBoxAutoComplete comBoBoxAutoComplete = new ComBoBoxAutoComplete(cboPayment, this);
        new ComBoBoxAutoComplete(cboLocation, this);
        new ComBoBoxAutoComplete(cboVouStatus, this);
        new ComBoBoxAutoComplete(cboCurrency, this);
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "CustomerList":
                Trader cus = (Trader) selectObj;

                //Check overdue
                if (Util1.getPropValue("system.overdue.check").equals("Y") && cus != null) {
                    if (isOverDue(cus.getTraderId())) {
                        clear();
                        return;
                    }
                }

                currSaleVou.setCustomerId(cus);

                if (cus != null) {
                    txtCusId.setText(cus.getTraderId());
                    txtCusName.setText(cus.getTraderName());

                    if (cus instanceof Customer) {
                        txtCreditLimit.setValue(((Customer) cus).getCreditLimit());
                        int creditDay = NumberUtil.NZeroInt(((Customer) cus).getCreditDays());

                        if (creditDay != 0) {
                            txtDueDate.setText(DateUtil.subDateTo(DateUtil.toDate(txtSaleDate.getText()), creditDay));
                            //txtDueDate.setText(DateUtil.addTodayDateTo(creditDay));
                        }
                    } else {
                        txtCreditLimit.setValue(0.0);
                    }

                    if (cus.getTypeId() != null) {
                        saleTableModel.setCusType(cus.getTypeId().getDescription(),"cus");
                    } else {
                        saleTableModel.setCusType("N","cus");
                    }
                    //Trader transaction
                    if (Util1.hashPrivilege("SaleCustomerInfoShow")) {
                        getTraderLastBalance();
                        getTraderTransaction();
                    }
                    calculateTotalAmount();

                    if (cus.getTraderId().equals(Util1.getPropValue("system.default.customer"))) {
                        cboPayment.setSelectedItem(ptCash);
                    } else {
                        cboPayment.setSelectedItem(ptCredit);
                    }
                } else {
                    txtCusId.setText(null);
                    txtCusName.setText(null);
                }
                break;
            case "PatientList":
                Patient ptt = (Patient) selectObj;

                currSaleVou.setPatientId(ptt);
                currSaleVou.setAdmissionNo(ptt.getAdmissionNo());

                if (ptt != null) {
                    if (ptt.getAdmissionNo() != null) {
                        String priceType = Util1.getPropValue("system.sale.adm.price");
                        if (priceType.isEmpty()) {
                            saleTableModel.setCusType("N","pt");
                        } else {
                            saleTableModel.setCusType(priceType,"pt");
                        }
                    }
                    txtCusId.setText(ptt.getRegNo());
                    txtCusName.setText(ptt.getPatientName());
                    txtCreditLimit.setValue(0.0);
                    calculateTotalAmount();
                    if (ptt.getDoctor() != null) {
                        txtDrCode.setText(ptt.getDoctor().getDoctorId());
                        txtDrName.setText(ptt.getDoctor().getDoctorName());
                    } else {
                        txtDrCode.requestFocus();
                    }
                    txtAdmissionNo.setText(ptt.getAdmissionNo());
                    if (!Util1.getNullTo(ptt.getAdmissionNo(), "").trim().isEmpty()) {
                        butAdmit.setEnabled(true);
                    } else {
                        butAdmit.setEnabled(false);
                        if (Util1.getPropValue("system.admission.paytype").equals("CREDIT")) {
                            cboPayment.setSelectedItem(ptCredit);
                        } else {
                            cboPayment.setSelectedItem(ptCash);
                        }
                    }
                    getPatientBill(ptt.getRegNo());
                } else {
                    txtCusId.setText(null);
                    txtCusName.setText(null);
                }
                break;
            case "PatientSearch":
                Patient patient = (Patient) selectObj;

                currSaleVou.setPatientId(patient);
                if (patient != null) {
                    if (patient.getAdmissionNo() != null) {
                        String priceType = Util1.getPropValue("system.sale.adm.price");
                        if (priceType.isEmpty()) {
                            saleTableModel.setCusType("N","pt");
                        } else {
                            saleTableModel.setCusType(priceType,"pt");
                        }
                    }
                    txtCusId.setText(patient.getRegNo());
                    txtCusName.setText(patient.getPatientName());
                    if (patient.getDoctor() != null) {
                        txtDrCode.setText(patient.getDoctor().getDoctorId());
                        txtDrName.setText(patient.getDoctor().getDoctorName());
                    } else {
                        txtDrCode.requestFocus();
                    }
                    txtAdmissionNo.setText(patient.getAdmissionNo());
                    if (!Util1.getNullTo(patient.getAdmissionNo(), "").trim().isEmpty()) {
                        butAdmit.setEnabled(true);
                    } else {
                        butAdmit.setEnabled(false);
                        if (Util1.getPropValue("system.admission.paytype").equals("CREDIT")) {
                            cboPayment.setSelectedItem(ptCredit);
                        } else {
                            cboPayment.setSelectedItem(ptCash);
                        }
                    }
                    getPatientBill(patient.getRegNo());
                }
                break;
            case "DoctorSearch":
                Doctor doc = (Doctor) selectObj;

                if (doc != null) {
                    txtDrCode.setText(doc.getDoctorId());
                    txtDrName.setText(doc.getDoctorName());
                    currSaleVou.setDoctor(doc);
                    tblSale.requestFocus();
                }
                break;
            case "MedicineList":
                Medicine med;

                try {
                    dao.open();
                    med = (Medicine) dao.find(Medicine.class, ((Medicine) selectObj).getMedId());
                    List<RelationGroup> listRel = med.getRelationGroupId();
                    med.setRelationGroupId(listRel);

                    if (listRel.size() > 0) {
                        medUp.add(med);
                        if (Util1.getPropValue("system.app.sale.stockBalance").equals("Y")) {
                            stockList.add(med, (Location) cboLocation.getSelectedItem());
                        } else if (Util1.getPropValue("system.app.sale.stockBalance").equals("H")) {
                            stockList.add(med, null);
                            List<Stock> listStock = stockList.getStockList(med.getMedId());
                            if (listStock == null) {
                                listStock = new ArrayList();
                            }
                            stockTableModel.setListStock(listStock);
                        }

                        int selectRow = tblSale.getSelectedRow();
                        saleTableModel.setMed(med, selectRow, stockList);
                        //Calculate total items of voucher
                        txtTotalItem.setText(Integer.toString((listDetail.size() - 1)));
                    } else {
                        System.out.println("Sale.select MedicineList : Cannot get relation group");
                    }
                } catch (Exception ex) {
                    log.error("selected : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                } finally {
                    dao.close();
                }
                break;
            case "SaleVouList":
                try {
                    clear();
                    dao.open();
                    if (selectObj instanceof SaleHis) {
                        /*currSaleVou = (SaleHis) dao.find(SaleHis.class,
                         ((SaleHis) selectObj).getSaleInvId());*/
                        currSaleVou = (SaleHis) selectObj;
                    } else {
                        currSaleVou = (SaleHis) dao.find(SaleHis.class,
                                selectObj.toString());
                    }

                    if (Util1.getNullTo(currSaleVou.getDeleted())) {
                        lblStatus.setText("DELETED");
                    } else {
                        lblStatus.setText("RESTORE");
                    }

                    cboLocation.setSelectedItem(currSaleVou.getLocationId());
                    cboVouStatus.setSelectedItem(currSaleVou.getVouStatus());
                    cboPayment.setSelectedItem(currSaleVou.getPaymentTypeId());

                    txtVouNo.setText(currSaleVou.getSaleInvId());
                    txtSaleDate.setText(DateUtil.toDateStr(currSaleVou.getSaleDate()));
                    saleTableModel.setSaleDate(DateUtil.toDate(txtSaleDate.getText()));
                    txtDueDate.setText(DateUtil.toDateStr(currSaleVou.getDueDate()));
                    if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                        if (currSaleVou.getPatientId() != null) {
                            txtCusId.setText(currSaleVou.getPatientId().getRegNo());
                            txtCusName.setText(currSaleVou.getPatientId().getPatientName());
                        } else {
                            txtCusId.setText(null);
                            txtCusName.setText(null);
                        }
                    } else if (currSaleVou.getCustomerId() != null) {
                        txtCusId.setText(currSaleVou.getCustomerId().getTraderId());
                        txtCusName.setText(currSaleVou.getCustomerId().getTraderName());
                    } else {
                        txtCusId.setText(null);
                        txtCusName.setText(null);
                    }

                    txtRemark.setText(currSaleVou.getRemark());
                    txtAdmissionNo.setText(currSaleVou.getAdmissionNo());

                    Doctor dr = currSaleVou.getDoctor();
                    if (dr != null) {
                        txtDrCode.setText(dr.getDoctorId());
                        txtDrName.setText(dr.getDoctorName());
                    } else {
                        txtDrCode.setText(null);
                        txtDrName.setText(null);
                    }

                    txtVouTotal.setValue(currSaleVou.getVouTotal());
                    txtVouPaid.setValue(currSaleVou.getPaid());
                    txtVouDiscount.setValue(currSaleVou.getDiscount());

                    txtTotalExpense.setValue(currSaleVou.getExpenseTotal());
                    txtTtlExpIn.setValue(currSaleVou.getTtlExpenseIn());

                    txtDiscP.setText(NumberUtil.NZero(currSaleVou.getDiscP()).toString());
                    txtTaxP.setText(NumberUtil.NZero(currSaleVou.getTaxP()).toString());
                    txtTax.setValue(currSaleVou.getTaxAmt());

                    txtGrandTotal.setValue((currSaleVou.getVouTotal()
                            + NumberUtil.NZero(currSaleVou.getTaxAmt())
                            + NumberUtil.NZero(currSaleVou.getExpenseTotal()))
                            - NumberUtil.NZero(currSaleVou.getDiscount()));

                    txtVouBalance.setValue((currSaleVou.getVouTotal()
                            + NumberUtil.NZero(currSaleVou.getTaxAmt())
                            + NumberUtil.NZero(currSaleVou.getExpenseTotal()))
                            - (NumberUtil.NZero(currSaleVou.getDiscount())
                            + NumberUtil.NZero(currSaleVou.getPaid())));
                    if (!Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                        if (currSaleVou.getCustomerId() instanceof Customer) {
                            txtCreditLimit.setValue(((Customer) currSaleVou.getCustomerId()).getCreditLimit());
                        }

                        if (currSaleVou.getCustomerId().getTypeId() != null) {
                            saleTableModel.setCusType(currSaleVou.getCustomerId().getTypeId().getDescription(),"vou");
                        }
                    }

                    //This statment is for Outstanding lazy loading
                    if (currSaleVou.getListOuts() != null) {
                        if (currSaleVou.getListOuts().size() > 0) {
                            List<SaleOutstand> listOuts = currSaleVou.getListOuts();
                            currSaleVou.setListOuts(listOuts);
                        }
                    }
                    //=============================================

                    //This statment is for Warranty laxy loading
                    if (currSaleVou.getWarrandy() != null) {
                        if (currSaleVou.getWarrandy().size() > 0) {
                            List<SaleWarranty> listWarranty = currSaleVou.getWarrandy();
                            currSaleVou.setWarrandy(listWarranty);
                        }
                    }
                    //=============================================

                    if (currSaleVou.getExpense() != null) {
                        if (currSaleVou.getExpense().size() > 0) {
                            currSaleVou.setExpense(currSaleVou.getExpense());
                        }
                    }

                    listDetail = currSaleVou.getSaleDetailHis();
                    for (SaleDetailHis sdh : listDetail) {
                        medUp.add(sdh.getMedId());
                    }
                    saleTableModel.setListDetail(listDetail);

                    listExpense = currSaleVou.getExpense();
                    expTableModel.setListDetail(listExpense);
                    dao.close();

                    haveTransaction = isHaveTransaction(currSaleVou.getSaleInvId());
                    if (!Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                        getTraderTransaction();
                        getTraderLastBalance();
                    }

                    dao.close();

                    double lastBalance = NumberUtil.NZero(txtSaleLastBalance.getValue())
                            + NumberUtil.NZero(txtVouBalance.getValue())
                            + tranTableModel.getTotal();

                    txtCusLastBalance.setValue(lastBalance);
                    txtDifference.setValue(NumberUtil.NZero(txtCreditLimit.getValue())
                            - NumberUtil.NZero(txtCusLastBalance.getValue()));
                    txtTotalItem.setText(String.valueOf(listDetail.size()));
                    //calculateTotalAmount();
                } catch (Exception ex) {
                    log.error("selected 1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }

                tblSale.requestFocusInWindow();
                applySecurityPolicy();
                break;
            case "SelectPayment":
                TraderPayHis tph = (TraderPayHis) selectObj;
                TraderTransaction tran = new TraderTransaction();

                tran.setTranOption("Payment");
                tran.setTranDate(tph.getPayDate());
                tran.setAmount(tph.getPaidAmtP() * -1);
                tran.setRemark(tph.getRemark());
                tran.setPaymentId(tph.getPaymentId());
                tran.setSaleVouNo(tph.getSaleVou());
                tran.setUserId(Global.loginUser.getUserId());
                tran.setTranType("D");
                tran.setSortId(2);

                try {
                    dao.save(tran);
                } catch (Exception ex) {
                    log.error("selected 2 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                } finally {
                    dao.close();
                }

                tranTableModel.add(tran);

                calculateTotalAmount();
                break;
            case "SaleQtyUpdate":
                //Update to stock balance list
                if (lblStatus.getText().equals("NEW")) {
                    SaleDetailHis sdh = (SaleDetailHis) selectObj;
                    String entity;
                    String strSQL;
                    List<Stock> list = null;

                    if (sdh.getMedId().getMedId() != null) {
                        entity = "com.cv.app.pharmacy.database.helper.Stock";
                        strSQL = "SELECT * FROM " + entity + " where med.medId = '"
                                + sdh.getMedId().getMedId() + "' and expDate = "
                                + sdh.getExpireDate();
                        list = JoSQLUtil.getResult(strSQL,
                                stockList.getStockList(sdh.getMedId().getMedId()));
                    }

                    if (list != null) {
                        if (list.size() > 0) {
                            for (Stock stock : list) {
                                stock.setQtyStrDeman(sdh.getQuantity() + sdh.getUnitId().getItemUnitCode());
                                stock.setUnit(sdh.getUnitId());
                                stock.setUnitQty(sdh.getQuantity());

                                if (NumberUtil.NZeroInt(sdh.getFocQty()) != 0) {
                                    stock.setFocQtyStr(sdh.getFocQty() + sdh.getFocUnit().getItemUnitCode());
                                    stock.setFocUnit(sdh.getFocUnit());
                                    stock.setFocUnitQty(sdh.getFocQty());
                                }

                                float demanQtySmall = 0;
                                float focQtySmall = 0;
                                String demanKey = null;
                                String focKey = null;

                                if (stock.getUnit() != null) {
                                    demanKey = stock.getMed().getMedId() + "-"
                                            + stock.getUnit().getItemUnitCode();
                                }

                                if (stock.getFocUnit() != null) {
                                    focKey = stock.getMed().getMedId() + "-"
                                            + stock.getFocUnit().getItemUnitCode();
                                }

                                if (demanKey != null) {
                                    demanQtySmall = medUp.getQtyInSmallest(demanKey);
                                }

                                if (focKey != null) {
                                    focQtySmall = medUp.getQtyInSmallest(focKey);
                                }

                                float balance = NumberUtil.NZeroInt(stock.getQtySmallest())
                                        - ((NumberUtil.NZeroFloat(stock.getUnitQty()) * demanQtySmall)
                                        + (NumberUtil.NZeroInt(stock.getFocUnitQty()) * focQtySmall));

                                stock.setQtyStrBal(MedicineUtil.getQtyInStr(sdh.getMedId(),
                                        balance));
                            }
                        }
                    }
                }
                break;
            case "VMarchantSearch":
                VMarchant vm = (VMarchant) selectObj;
                txtCusId.setText(vm.getPersonNumber());
                txtCusName.setText(vm.getPersonName());
                currSaleVou.setRegNo(vm.getPersonId());
                currSaleVou.setStuName(vm.getPersonName());
                currSaleVou.setStuNo(vm.getPersonNumber());
                break;
        }
    }

    private void initSaleTable() {
        tblSale.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblSale.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblSale.getColumnModel().getColumn(1).setPreferredWidth(300);//Medicine Name
        tblSale.getColumnModel().getColumn(2).setPreferredWidth(60);//Relstr
        tblSale.getColumnModel().getColumn(3).setPreferredWidth(50);//Expire Date
        tblSale.getColumnModel().getColumn(4).setPreferredWidth(30);//Qty
        tblSale.getColumnModel().getColumn(5).setPreferredWidth(15);//Unit
        tblSale.getColumnModel().getColumn(6).setPreferredWidth(60);//Sale price
        tblSale.getColumnModel().getColumn(7).setPreferredWidth(20);//Discount
        tblSale.getColumnModel().getColumn(8).setPreferredWidth(20);//FOC
        tblSale.getColumnModel().getColumn(9).setPreferredWidth(15);//FOC-Unit
        tblSale.getColumnModel().getColumn(10).setPreferredWidth(70);//Amount

        addSaleTableModelListener();

        //Change JTable cell editor
        tblSale.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
        tblSale.getColumnModel().getColumn(4).setCellEditor(new BestTableCellEditor(this));
        tblSale.getColumnModel().getColumn(7).setCellEditor(new BestTableCellEditor(this));
        tblSale.getColumnModel().getColumn(6).setCellEditor(new SaleTableUnitCellEditor());
        tblSale.getColumnModel().getColumn(8).setCellEditor(new BestTableCellEditor(this));
        //tblSale.getColumnModel().getColumn(3).setCellRenderer(new TableDateFieldRenderer());
        tblSale.getColumnModel().getColumn(10).setCellEditor(new BestTableCellEditor(this));

        JComboBox cboChargeType = new JComboBox();
        BindingUtil.BindCombo(cboChargeType, dao.findAll("ChargeType"));
        //Replace with FOC column
        //tblSale.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(cboChargeType));

        if (Util1.getPropValue("system.sale.detail.location").equals("Y")) {
            JComboBox cboLocationCell = new JComboBox();
            cboLocationCell.setFont(Global.textFont); // NOI18N
            BindingUtil.BindCombo(cboLocationCell, dao.findAll("Location"));
            tblSale.getColumnModel().getColumn(11).setCellEditor(new DefaultCellEditor(cboLocationCell));
            saleTableModel.setLocation((Location) cboLocation.getSelectedItem());
            tblSale.getColumnModel().getColumn(11).setPreferredWidth(50);//Location
        } else {
            tblSale.getColumnModel().getColumn(11).setPreferredWidth(0);//Location
            tblSale.getColumnModel().getColumn(11).setMaxWidth(0);
            tblSale.getColumnModel().getColumn(11).setMaxWidth(0);
        }

        tblSale.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSale.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                txtRecNo.setText(Integer.toString(tblSale.getSelectedRow() + 1));
                if (tblSale.getSelectedRow() < saleTableModel.getRowCount()) {
                    Object tmp = tblSale.getValueAt(tblSale.getSelectedRow(), 0);
                    if (tmp != null) {
                        String selectMedId = tmp.toString();
                        List<Stock> listStock = stockList.getStockList(selectMedId);
                        if (listStock == null) {
                            listStock = new ArrayList();
                        }
                        stockTableModel.setListStock(listStock);
                    } else {
                        List<Stock> listStock = new ArrayList();
                        stockTableModel.setListStock(listStock);
                    }
                }
            }
        });

        tblPatientBill.getColumnModel().getColumn(0).setPreferredWidth(180);//Bill Name
        tblPatientBill.getColumnModel().getColumn(1).setPreferredWidth(70);//Amount
    }

    private void addSaleTableModelListener() {
        tblSale.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int column = e.getColumn();

                if (column >= 0) {
                    switch (column) {
                        case 0: //Code
                        case 4: //Qty
                        case 5: //Unit
                        case 6: //Sale price
                        case 7: //Discount
                        case 8: //Charge type
                            calculateTotalAmount();
                            break;
                    }
                }
            }
        });
    }

    private void addNewRow() {
        SaleDetailHis his = new SaleDetailHis();
        his.setMedId(new Medicine());
        listDetail.add(his);
    }

    /*private boolean hasNewRow() {
     boolean statusNewRow = false;
     SaleDetailHis saleDetailHis = listDetail.get(listDetail.size() - 1);

     String ID = saleDetailHis.getMedId().getMedId();
     if (ID == null) {
     statusNewRow = true;
     }

     return statusNewRow;
     }*/
    @Override
    public void getMedInfo(String medCode) {
        Medicine medicine;

        if (!medCode.trim().isEmpty()) {
            medicine = (Medicine) dao.find("Medicine", "medId = '"
                    + medCode + "' and active = true");

            if (medicine != null) {
                selected("MedicineList", medicine);
            } else { //For barcode
                medicine = (Medicine) dao.find("Medicine", "barcode = '"
                        + medCode + "' and active = true");

                if (medicine != null) {
                    selected("MedicineList", medicine);
                } else {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            log.info("getMedInfo : Blank medicine code.");
        }
    }

    private void getMedList(String filter) {
        int locationId = -1;
        if(cboLocation.getSelectedItem() instanceof Location){
            locationId = ((Location)cboLocation.getSelectedItem()).getLocationId();
        }
        String cusGroup = "-";
        if(currSaleVou.getCustomerId() != null){
            if(currSaleVou.getCustomerId().getTraderGroup() != null){
                cusGroup = currSaleVou.getCustomerId().getTraderGroup().getGroupId();
            }
        }
        MedListDialog1 dialog = new MedListDialog1(dao, filter, locationId, cusGroup);

        if (dialog.getSelect() != null) {
            selected("MedicineList", dialog.getSelect());
        }
    }

    private class SaleTableUnitCellEditor extends javax.swing.AbstractCellEditor implements TableCellEditor {

        JComponent component = null;
        int colIndex = -1;
        private Object oldValue;
        // This method is called when a cell value is edited by the user.

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            oldValue = value;
            colIndex = vColIndex;

            if (isSelected) {
                // cell (and perhaps other cells) are selected
            }

            String medId = listDetail.get(rowIndex).getMedId().getMedId();

            try {
                switch (vColIndex) {
                    case 0: //Code column
                        JTextField code = new JTextField();
                        component = code;
                        if (value != null) {
                            ((JTextField) component).setText(value.toString());
                        }
                        ((JTextField) component).selectAll();
                        break;
                    case 5: //Unit column
                        JComboBox jb = new JComboBox();

                        BindingUtil.BindCombo(jb, medUp.getUnitList(medId));
                        component = jb;
                        break;
                    case 6: //Price column
                        JTextField jtf = new JTextField();
                        component = jtf;
                        if (value != null) {
                            ((JTextField) component).setText(value.toString());
                        }
                        ((JTextField) component).selectAll();

                        String unit = listDetail.get(rowIndex).getUnitId().getItemUnitCode();
                        MedPriceAutoCompleter completer = new MedPriceAutoCompleter(jtf,
                                medUp.getPriceList(medId + "-" + unit), this);
                        break;
                }
            } catch (Exception ex) {
                log.error("getTableCellEditorComponent : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
            // Configure the component with the specified value
            //((JTextField) component).setText("");

            // Return the configured component
            return component;
        }

        // This method is called when editing is completed.
        // It must return the new value to be stored in the cell.
        @Override
        public Object getCellEditorValue() {
            Object obj = null;

            if (component instanceof JComboBox) {
                obj = ((JComboBox) component).getSelectedItem();
            } else if (component instanceof JTextField) {
                obj = ((JTextField) component).getText();
            }

            switch (colIndex) {
                case 0: //Code
                    if (obj != null) {
                        getMedInfo(obj.toString());
                    }

                    tblSale.setColumnSelectionInterval(0, 0);
                    break;
            }

            return obj;
        }

        /*
         * To prevent mouse click cell editing
         */
        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return !(anEvent instanceof MouseEvent);
        }
    }

    private void calculateTotalAmount() {
        double totalAmount = 0;
        double totalExp = 0;
        double totalExpIn = 0;

        for (SaleDetailHis sdh : listDetail) {
            totalAmount += NumberUtil.NZero(sdh.getAmount());
        }

        txtVouTotal.setValue(totalAmount);

        for (SaleExpense exp : listExpense) {
            totalExp += NumberUtil.NZero(exp.getExpAmount());
            totalExpIn += NumberUtil.NZero(exp.getExpenseIn());
        }

        //Total Expense In, Out
        txtTotalExpense.setValue(totalExp);//Out
        txtTtlExpIn.setValue(totalExpIn);//In

        double taxp = NumberUtil.NZero(txtTaxP.getText());
        double afterDiscountAmt = totalAmount - NumberUtil.NZero(txtVouDiscount.getValue());
        double totalTax = (afterDiscountAmt * taxp) / 100;
        txtTax.setValue(totalTax);

        try {
            PaymentType pt = (PaymentType) cboPayment.getSelectedItem();

            if (pt != null) {
                //if(lblStatus.getText().equals("NEW")){
                if (pt.getPaymentTypeId() == 1)//Cash
                {
                    txtVouPaid.setValue(totalAmount + totalExp + totalExpIn + totalTax);
                } else //if (pt.getPaymentTypeId() == 2) //Credit
                {
                    txtVouPaid.setValue(0);
                }
                //}
            }
        } catch (NullPointerException ex) {
            log.error("calculateTotalAmount : " + ex.toString());
        }

        txtGrandTotal.setValue((NumberUtil.NZero(txtVouTotal.getValue())
                + NumberUtil.NZero(txtTotalExpense.getValue())
                + NumberUtil.NZero(txtTax.getValue())
                + NumberUtil.NZero(txtTtlExpIn.getValue()))
                - NumberUtil.NZero(txtVouDiscount.getValue()));

        txtVouBalance.setValue(NumberUtil.NZero(txtGrandTotal.getValue())
                - NumberUtil.NZero(txtVouPaid.getValue()));

        double lastBalance = NumberUtil.NZero(txtSaleLastBalance.getValue())
                + NumberUtil.NZero(txtVouBalance.getValue())
                + tranTableModel.getTotal();

        txtCusLastBalance.setValue(lastBalance);
        txtDifference.setValue(NumberUtil.NZero(txtCreditLimit.getValue())
                - NumberUtil.NZero(txtCusLastBalance.getValue()));
    }

    private void actionMapping() {
        //F3 event on tblSale
        tblSale.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblSale.getActionMap().put("F3-Action", actionMedList);

        //F8 event on tblSale
        tblSale.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblSale.getActionMap().put("F8-Action", actionItemDelete);

        //Enter event on tblSale
        tblSale.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblSale.getActionMap().put("ENTER-Action", actionTblSaleEnterKey);

        //Enter event on tblExpense
        tblExpense.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblExpense.getActionMap().put("ENTER-Action", actionTblExpEnterKey);

        //F8 event on tblExpense
        tblExpense.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblExpense.getActionMap().put("F8-Action", actionItemDeleteExp);

        //F8 event on tblTransaction
        tblTransaction.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblTransaction.getActionMap().put("F8-Action", actionItemDeleteTran);

        //F3 trader code
        txtCusId.registerKeyboardAction(traderF3Action, KeyStroke.getKeyStroke("F3"),
                JComponent.WHEN_FOCUSED);

        formActionKeyMapping(txtVouNo);
        formActionKeyMapping(txtRemark);
        formActionKeyMapping(txtSaleDate);
        formActionKeyMapping(txtCusId);
        formActionKeyMapping(txtCusName);
        formActionKeyMapping(txtDueDate);
        formActionKeyMapping(txtDrCode);
        formActionKeyMapping(txtDrName);
        formActionKeyMapping(tblSale);
        formActionKeyMapping(tblExpense);
        formActionKeyMapping(tblTransaction);
        formActionKeyMapping(butPayment);
        formActionKeyMapping(txtSaleLastBalance);
        formActionKeyMapping(txtCreditLimit);
        formActionKeyMapping(txtCusLastBalance);
        formActionKeyMapping(txtDifference);
        formActionKeyMapping(txtTotalExpense);
        formActionKeyMapping(txtTtlExpIn);
        formActionKeyMapping(txtVouTotal);
        formActionKeyMapping(txtDiscP);
        formActionKeyMapping(txtVouDiscount);
        formActionKeyMapping(txtTaxP);
        formActionKeyMapping(txtTax);
        formActionKeyMapping(txtGrandTotal);
        formActionKeyMapping(txtVouPaid);
        formActionKeyMapping(txtVouBalance);
    }
    private Action actionMedList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if(tblSale.getCellEditor() != null){
                    tblSale.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
                //No entering medCode, only press F3
                try {
                    dao.open();
                    //getMedInfo("");
                    getMedList("");
                    dao.close();
                } catch (Exception ex1) {
                    log.error("actionMedList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex1.toString());
                }
            }
        }
    };
    private Action actionTblSaleEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if(tblSale.getCellEditor() != null){
                    tblSale.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            if (!Util1.getPropValue("system.sale.barcode").equals("Y")) {
                int row = tblSale.getSelectedRow();
                int col = tblSale.getSelectedColumn();

                SaleDetailHis sdh = listDetail.get(row);

                if (col == 0 && sdh.getMedId().getMedId() != null) {
                    tblSale.setColumnSelectionInterval(4, 4); //Move to Qty
                } else if (col == 1 && sdh.getMedId().getMedId() != null) {
                    tblSale.setColumnSelectionInterval(4, 4); //Move to Qty
                } else if (col == 2 && sdh.getMedId().getMedId() != null) {
                    tblSale.setColumnSelectionInterval(4, 4); //Move to Qty
                } else if (col == 3 && sdh.getMedId().getMedId() != null) {
                    tblSale.setColumnSelectionInterval(4, 4); //Move to Qty
                } else if (col == 4 && sdh.getMedId().getMedId() != null) {
                    tblSale.setColumnSelectionInterval(6, 6); //Move to Unit
                } else if (col == 5 && sdh.getMedId().getMedId() != null) {
                    tblSale.setColumnSelectionInterval(6, 6); //Move to Sale Price
                } else if (col == 6 && sdh.getMedId().getMedId() != null) {
                    tblSale.setColumnSelectionInterval(10, 10); //Move to Discount
                } else if (col == 7 && sdh.getMedId().getMedId() != null) {
                    tblSale.setColumnSelectionInterval(10, 10); //Move to Charge Type
                } else if (col == 8 && sdh.getMedId().getMedId() != null) {
                    tblSale.setColumnSelectionInterval(10, 10); //Move to Charge Type
                } else if (col == 9 && sdh.getMedId().getMedId() != null) {
                    tblSale.setColumnSelectionInterval(10, 10); //Move to Charge Type
                } else if (col == 10 && sdh.getMedId().getMedId() != null) {
                    if ((row + 1) <= listDetail.size()) {
                        tblSale.setRowSelectionInterval(row + 1, row + 1);
                    }
                    tblSale.setColumnSelectionInterval(0, 0); //Move to Code
                }
            }
        }
    };
    private Action actionTblExpEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if(tblExpense.getCellEditor() != null){
                    tblExpense.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            int row = tblExpense.getSelectedRow();
            int col = tblExpense.getSelectedColumn();

            SaleExpense se = listExpense.get(row);

            if (col == 0 && se.getExpType() != null) {
                tblExpense.setColumnSelectionInterval(1, 1); //Amount
            } else if (col == 1 && se.getExpType() != null) {
                if ((row + 1) < listExpense.size()) {
                    tblExpense.setRowSelectionInterval(row + 1, row + 1);
                }
                tblExpense.setColumnSelectionInterval(0, 0); //Move to Exp Type
            }
        }
    };
    private Action actionSave = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            save();
        }
    };
    private Action actionHistory = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            history();
        }
    };
    private Action actionPrint = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print();
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
    private Action actionNewForm = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            newForm();
        }
    };
    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            SaleDetailHis sdh;
            int yes_no = -1;

            if (tblSale.getSelectedRow() >= 0) {
                sdh = listDetail.get(tblSale.getSelectedRow());

                if (sdh.getMedId().getMedId() != null) {
                    try {
                        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                            yes_no = 0;
                        } else {
                            yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                                    "Are you sure to delete?",
                                    "Sale item delete", JOptionPane.YES_NO_OPTION);
                        }

                        if(tblSale.getCellEditor() != null){
                            tblSale.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {
                    }

                    if (yes_no == 0) {
                        if (sdh.getChargeId() != null) {
                            stockList.delete(sdh.getMedId().getMedId(), sdh.getExpireDate(),
                                    sdh.getChargeId().getChargeTypeDesc());
                        }

                        saleTableModel.delete(tblSale.getSelectedRow());
                        deleteOutstand(sdh.getMedId().getMedId(), sdh.getExpireDate(),
                                sdh.getChargeId().getChargeTypeId());
                        deleteWarranty(sdh.getMedId().getMedId());
                        calculateTotalAmount();
                        //Calculate total items of voucher
                        txtTotalItem.setText(Integer.toString((listDetail.size() - 1)));
                    }
                }
            }
        }
    };
    private Action actionItemDeleteExp = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            SaleExpense se;
            int yes_no = -1;

            if (tblExpense.getSelectedRow() >= 0) {
                se = listExpense.get(tblExpense.getSelectedRow());

                if (se.getExpType().getExpenseId() != null) {
                    try {
                        yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                                "Expense item delete", JOptionPane.YES_NO_OPTION);

                        if(tblExpense.getCellEditor() != null){
                            tblExpense.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {
                    }

                    if (yes_no == 0) {
                        expTableModel.delete(tblExpense.getSelectedRow());
                        calculateTotalAmount();
                    }
                }
            }
        }
    };
    private Action actionItemDeleteTran = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            tranTableModel.delete(tblTransaction.getSelectedRow());
            calculateTotalAmount();
        }
    };

    public void setFocus() {
        //txtCusId.requestFocusInWindow();
        txtCusId.requestFocus();
    }

    private void clear() {
        if (lblStatus.getText().equals("NEW")) {
            strPrvDate = txtSaleDate.getText();
            prvLocation = cboLocation.getSelectedItem();
            prvPymet = cboPayment.getSelectedItem();
        }

        haveTransaction = false;
        //Clear text box.
        txtVouNo.setText("");
        txtSaleDate.setText("");
        txtDueDate.setText("");
        txtCusId.setText("");
        txtCusId.setEditable(true);
        txtCusName.setText("");
        txtDrCode.setText("");
        txtDrName.setText("");
        txtRemark.setText("");
        lblStatus.setText("NEW");
        txtCreditLimit.setValue(0.0);
        saleTableModel.setCusType("N","vou");
        lblSaleLastBal.setText("Balance : ");
        tranTableModel.clear();
        lblDate.setText("");
        lblTranOption.setText("");
        currSaleVou = new SaleHis();
        assignDefaultValueModel();
        initTextBoxValue();
        if (Util1.getPropValue("system.login.default.value").equals("Y")) {
            txtSaleDate.setText(Global.loginDate);
        } else if (strPrvDate != null) {
            txtSaleDate.setText(strPrvDate);
            saleTableModel.setSaleDate(DateUtil.toDate(txtSaleDate.getText()));
        }
        vouEngine.setPeriod(DateUtil.getPeriod(txtSaleDate.getText()));
        genVouNo();
        //setFocus();

        medUp.clear();
        stockList.clear();

        System.gc();
        applySecurityPolicy();
        assignDefaultValue();
        txtTaxP.setText(Util1.getPropValue("system.sale.tax.percent"));

        txtBillTotal.setText(null);
        butAdmit.setEnabled(false);
        tblPatientBillTableModel.setListPBP(new ArrayList());
        txtAdmissionNo.setText(null);
        //txtCusId.requestFocus();
    }

    private void initTextBoxAlign() {
        txtVouTotal.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtVouPaid.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtVouDiscount.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtVouBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtCusLastBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTotalExpense.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTtlExpIn.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTax.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtGrandTotal.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtCreditLimit.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtDifference.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtSaleLastBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);

        txtVouTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouDiscount.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtCusLastBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTotalExpense.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTtlExpIn.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTax.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtGrandTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtCreditLimit.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtDifference.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtSaleLastBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
    }

    private void initTextBoxValue() {
        txtVouTotal.setValue(0.0);
        txtVouPaid.setValue(0.0);
        txtVouDiscount.setValue(0.0);
        txtVouBalance.setValue(0.0);
        txtCusLastBalance.setValue(0.0);
        txtTotalExpense.setValue(0.0);
        txtTtlExpIn.setValue(0.0);
        txtTax.setValue(0.0);
        txtGrandTotal.setValue(0.0);
        txtCreditLimit.setValue(0.0);
        txtDifference.setValue(0.0);
        txtSaleLastBalance.setValue(0.0);

        txtDiscP.setText("0");
        txtTaxP.setText("0");
        txtTotalItem.setText("0");
    }

    private void AddFocusMoveKey() {
        Set backwardKeys = jPanel1.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
        Set newBackwardKeys = new HashSet(backwardKeys);

        Set forwardKeys = jPanel1.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set newForwardKeys = new HashSet(forwardKeys);

        //newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

        //newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
        jPanel1.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);
        jPanel1.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newBackwardKeys);
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        focusOrder.add(txtCusId);
        focusOrder.add(txtDrCode);
        /*focusOrder.add(cboLocation);
         focusOrder.add(cboPayment);
         focusOrder.add(cboVouStatus);
         focusOrder.add(txtRemark);
         focusOrder.add(tblSale);*/

        focusPolicy = new BestAppFocusTraversalPolicy(focusOrder);
    }

    private void initExpenseTable() {
        //Adjust column width
        tblExpense.getColumnModel().getColumn(0).setPreferredWidth(60); //Date
        tblExpense.getColumnModel().getColumn(1).setPreferredWidth(150);//Expense Type
        tblExpense.getColumnModel().getColumn(2).setPreferredWidth(60);//Amt-In
        tblExpense.getColumnModel().getColumn(3).setPreferredWidth(60);//Amt-Out

        addExpenseTableModelListener();

        JComboBox cboExpenseType = new JComboBox();
        cboExpenseType.setFont(new java.awt.Font("Zawgyi-One", 0, 11));
        BindingUtil.BindCombo(cboExpenseType, dao.findAll("ExpenseType"));
        tblExpense.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cboExpenseType));
    }

    private void addExpenseTableModelListener() {
        tblExpense.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int column = e.getColumn();

                if (column >= 0) {
                    //Need to add action for updating table
                    switch (column) {
                        case 2: //Amt In
                        case 3: //Amt Out
                            /*Double expTotal = new Double(0);
                             Double expTtlIn = new Double(0);
                            
                             for (SaleExpense exp : listExpense) {
                             expTotal += NumberUtil.NZero(exp.getExpAmount());
                             expTtlIn += NumberUtil.NZero(exp.getExpenseIn());
                             }

                             txtTotalExpense.setValue(expTotal);
                             txtTtlExpIn.setValue(expTtlIn);*/

                            calculateTotalAmount();
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void save() {

    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Sale Voucher Search", dao);
    }

    @Override
    public void delete() {
        if (Util1.getNullTo(currSaleVou.getDeleted())) {
            JOptionPane.showConfirmDialog(Util1.getParent(), "Voucher already deleted.",
                    "Sale voucher delete", JOptionPane.ERROR);
        } else {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "Sale voucher delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                currSaleVou.setDeleted(true);
                save();
                if (!Util1.getPropValue("system.app.usage.type").equals("School")
                        || !Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                    if (currSaleVou.getCustomerId() != null) {
                        String vouNo = currSaleVou.getSaleInvId();
                        String traderId = currSaleVou.getCustomerId().getTraderId();
                        updatePayent(vouNo, traderId);
                    }
                }
            }
        }
    }

    @Override
    public void deleteCopy() {
        System.out.println("Delete Copy");
        int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete and copy?",
                "Sale voucher delete", JOptionPane.YES_NO_OPTION);

        if (yes_no == 0) {
            currSaleVou.setDeleted(true);
            String vouNo = currSaleVou.getSaleInvId();
            String traderId = "-";

            if (currSaleVou.getCustomerId() != null) {
                traderId = currSaleVou.getCustomerId().getTraderId();
            }

            if (isValidEntry() && saleTableModel.isValidEntry() && expTableModel.isValidEntry()) {

                removeEmptyRow();
                currSaleVou.setSaleDetailHis(listDetail);
                currSaleVou.setExpense(listExpense);
                currSaleVou.setListOuts(getOutstandingItem());

                //For BK Pagolay
                try {
                    Date d = new Date();
                    dao.execProc("bksale",
                            currSaleVou.getSaleInvId(),
                            DateUtil.toDateTimeStrMYSQL(d),
                            Global.loginUser.getUserId());
                } catch (Exception ex) {
                    log.error("bksale : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                } finally {
                    dao.close();
                }

                try {
                    dao.save(currSaleVou);

                    //Upload to Account
                    uploadToAccount(currSaleVou);

                    if (!Util1.getPropValue("system.app.usage.type").equals("School")
                            || !Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                        updatePayment();
                    }

                    if (lblStatus.getText().equals("NEW")) {
                        vouEngine.updateVouNo();
                    }

                    deleteDetail();
                } catch (Exception ex) {
                    log.error("Delete Copy : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                }

            }

            if (!Util1.getPropValue("system.app.usage.type").equals("School")
                    || !Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                updatePayent(vouNo, traderId);
            }

            try {
                dao.open();
                copyVoucher(currSaleVou.getSaleInvId());
                dao.close();
                vouEngine.setPeriod(DateUtil.getPeriod(txtSaleDate.getText()));
                genVouNo();
            } catch (Exception ex) {
                log.error("deleteCope : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        }
    }

    private void copyVoucher(String vouNo) {
        SaleHis tmpSaleHis = (SaleHis) dao.find(SaleHis.class, vouNo);

        txtSaleDate.setText(DateUtil.toDateStr(tmpSaleHis.getSaleDate()));
        saleTableModel.setSaleDate(DateUtil.toDate(txtSaleDate.getText()));
        txtDueDate.setText(DateUtil.toDateStr(tmpSaleHis.getDueDate()));
        txtRemark.setText(tmpSaleHis.getRemark());

        if (tmpSaleHis.getCustomerId() != null) {
            txtCusId.setText(tmpSaleHis.getCustomerId().getTraderId());
            txtCusName.setText(tmpSaleHis.getCustomerId().getTraderName());
            txtCreditLimit.setValue(((Customer) tmpSaleHis.getCustomerId()).getCreditLimit());
        } else if (tmpSaleHis.getPatientId() != null) {
            txtCusId.setText(tmpSaleHis.getPatientId().getRegNo());
            txtCusName.setText(tmpSaleHis.getPatientId().getPatientName());
        } else {
            txtCusId.setText(null);
            txtCusName.setText(null);
            txtCreditLimit.setValue(null);
        }

        //txtDrCode.setText();
        //txtDrName.setText();
        cboLocation.setSelectedItem(tmpSaleHis.getLocationId());
        cboPayment.setSelectedItem(tmpSaleHis.getPaymentTypeId());
        cboVouStatus.setSelectedItem(tmpSaleHis.getVouStatus());

        List<SaleDetailHis> listSdh = tmpSaleHis.getSaleDetailHis();
        List<SaleExpense> listSe = tmpSaleHis.getExpense();

        //listDetail.removeAll(listDetail);
        //listExpense.removeAll(listExpense);
        listDetail = new ArrayList();
        listExpense = new ArrayList();

        for (SaleDetailHis sdh : listSdh) {
            SaleDetailHis tmpSdh = new SaleDetailHis();

            BeanUtils.copyProperties(sdh, tmpSdh);
            tmpSdh.setSaleDetailId(null);
            listDetail.add(tmpSdh);
        }
        listDetail.add(new SaleDetailHis());

        for (SaleExpense se : listSe) {
            SaleExpense tmpSe = new SaleExpense();

            BeanUtils.copyProperties(se, tmpSe);
            tmpSe.setSaleExpenseId(null);
            listExpense.add(tmpSe);
        }

        listExpense.add(new SaleExpense());

        List<SaleOutstand> listOuts = new ArrayList();
        List<SaleOutstand> tmpListOuts = currSaleVou.getListOuts();
        for (SaleOutstand so : tmpListOuts) {
            SaleOutstand tmpSo = new SaleOutstand();
            BeanUtils.copyProperties(so, tmpSo);
            tmpSo.setOutsId(null);
            listOuts.add(tmpSo);
        }

        List<SaleWarranty> listWarrandy = new ArrayList();
        List<SaleWarranty> tmpListW = currSaleVou.getWarrandy();
        for (SaleWarranty sw : tmpListW) {
            SaleWarranty tmpSw = new SaleWarranty();
            BeanUtils.copyProperties(sw, tmpSw);
            tmpSw.setWarrantyId(null);
            listWarrandy.add(tmpSw);
        }

        lblStatus.setText("NEW");

        txtVouTotal.setValue(tmpSaleHis.getVouTotal());
        txtVouPaid.setValue(tmpSaleHis.getPaid());
        txtVouDiscount.setValue(tmpSaleHis.getDiscount());
        txtTotalExpense.setValue(tmpSaleHis.getExpenseTotal());
        txtTtlExpIn.setValue(tmpSaleHis.getTtlExpenseIn());
        txtVouBalance.setValue((tmpSaleHis.getVouTotal() + tmpSaleHis.getExpenseTotal() + tmpSaleHis.getTtlExpenseIn())
                - (tmpSaleHis.getPaid() + tmpSaleHis.getDiscount()));

        //dao.evict(tmpSaleHis);
        currSaleVou = new SaleHis();
        BeanUtils.copyProperties(tmpSaleHis, currSaleVou);
        currSaleVou.setSaleDetailHis(listDetail);
        currSaleVou.setExpense(listExpense);
        currSaleVou.setListOuts(listOuts);
        currSaleVou.setWarrandy(listWarrandy);
        currSaleVou.setDeleted(false);
        saleTableModel.setSaleDate(DateUtil.toDate(txtSaleDate.getText()));
        saleTableModel.setListDetail(listDetail);
        expTableModel.setListDetail(listExpense);
    }

    @Override
    public void print() {

    }

    private boolean isValidEntry() {
        boolean status = true;

        /*if(haveTransaction){
         JOptionPane.showMessageDialog(Util1.getParent(), "This voucher have related transaction. Changes will not be effected.",
         "Related transaction.", JOptionPane.ERROR_MESSAGE);
         } else*/
        if (!DateUtil.isValidDate(txtSaleDate.getText())) {
            log.error("Sale date error : " + txtVouNo.getText());
            status = false;
            txtSaleDate.requestFocusInWindow();
        } else if (txtVouNo.getText().trim().length() < 15) {
            log.error("Sale vour error : " + txtVouNo.getText());
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid sale vou no.",
                    "Sale Vou No", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (currSaleVou.getCustomerId() == null
                && (!Util1.getPropValue("system.app.usage.type").equals("School"))
                && (!Util1.getPropValue("system.app.usage.type").equals("Hospital"))) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Customer cannot be blank.",
                    "No customer.", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCusId.requestFocusInWindow();
        } else if (cboLocation.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Choose location.",
                    "No location.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboLocation.requestFocusInWindow();
        } else if (cboPayment.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Choose payment type.",
                    "No payment type.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboPayment.requestFocusInWindow();
        } else if (cboVouStatus.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Choose vou status.",
                    "No vou status.", JOptionPane.ERROR_MESSAGE);
            status = false;
            cboVouStatus.requestFocusInWindow();
        } else {
            currSaleVou.setSaleInvId(txtVouNo.getText());
            currSaleVou.setDueDate(DateUtil.toDate(txtDueDate.getText()));
            currSaleVou.setLocationId((Location) cboLocation.getSelectedItem());
            currSaleVou.setPaymentTypeId((PaymentType) cboPayment.getSelectedItem());
            currSaleVou.setVouStatus((VouStatus) cboVouStatus.getSelectedItem());
            currSaleVou.setRemark(txtRemark.getText());
            currSaleVou.setVouTotal(NumberUtil.getDouble(txtVouTotal.getText()));
            currSaleVou.setPaid(NumberUtil.getDouble(txtVouPaid.getText()));
            currSaleVou.setDiscount(NumberUtil.getDouble(txtVouDiscount.getText()));
            currSaleVou.setExpenseTotal(NumberUtil.getDouble(txtTotalExpense.getText()));
            currSaleVou.setTtlExpenseIn(NumberUtil.getDouble(txtTtlExpIn.getText()));
            currSaleVou.setBalance(NumberUtil.getDouble(txtVouBalance.getText()));
            currSaleVou.setDiscP(NumberUtil.getDouble(txtDiscP.getText()));
            currSaleVou.setTaxP(NumberUtil.getDouble(txtTaxP.getText()));
            currSaleVou.setTaxAmt(NumberUtil.getDouble(txtTax.getText()));
            currSaleVou.setDeleted(Util1.getNullTo(currSaleVou.getDeleted()));

            if (lblStatus.getText().equals("NEW")) {
                currSaleVou.setDeleted(false);
                currSaleVou.setSaleDate(DateUtil.toDateTime(txtSaleDate.getText()));
            } else {
                Date tmpDate = DateUtil.toDate(txtSaleDate.getText());
                if (!DateUtil.isSameDate(tmpDate, currSaleVou.getSaleDate())) {
                    currSaleVou.setSaleDate(DateUtil.toDateTime(txtSaleDate.getText()));
                }
            }

            currSaleVou.setCurrencyId((Currency) cboCurrency.getSelectedItem());
            if (lblStatus.getText().equals("NEW")) {
                currSaleVou.setCreatedBy(Global.loginUser);
                currSaleVou.setSession(Global.sessionId);
            } else {
                currSaleVou.setUpdatedBy(Global.loginUser);
                currSaleVou.setUpdatedDate(DateUtil.getTodayDateTime());
            }

            currSaleVou.setPaidCurrencyExRate(1.0);
            currSaleVou.setPaidCurrencyAmt(currSaleVou.getPaid());
            currSaleVou.setPaidCurrency(currSaleVou.getCurrencyId());

            if (Util1.getPropValue("system.app.usage.type").equals("School")) {
                currSaleVou.setStuName(txtCusName.getText());
            }

            currSaleVou.setAdmissionNo(txtAdmissionNo.getText());
        }

        return status;
    }

    private void getCustomerList() {
        if (Util1.hashPrivilege("SaleCustomerChange")) {
            switch (Util1.getPropValue("system.app.usage.type")) {
                case "Hospital": {
                    PatientSearch dialog = new PatientSearch(dao, this);
                    break;
                }
                case "School": {
                    MarchantSearch dialog = new MarchantSearch(dao, this);
                    break;
                }
                default: {
                    UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Customer List", dao);
                    break;
                }
            }
        }
    }

    private void assignDefaultValueModel() {
        listDetail = new ArrayList();
        saleTableModel.setListDetail(listDetail);

        listExpense = new ArrayList();
        expTableModel.setListDetail(listExpense);

        listTTDetail = new ArrayList();
        ttdTableModel.clear();

        txtSaleDate.setText(DateUtil.getTodayDateStr());
        saleTableModel.setSaleDate(DateUtil.toDate(txtSaleDate.getText()));
    }

    private void getCustomer() {
        if (txtCusId.getText() != null && !txtCusId.getText().isEmpty()) {
            try {

                switch (Util1.getPropValue("system.app.usage.type")) {
                    case "Hospital":
                        //cus = (Patient) dao.find(Patient.class, txtCusId.getText());

                        dao.open();
                        Patient ptt = (Patient) dao.find("Patient", "v.regNo = '" + txtCusId.getText() + "'");
                        //Patient ptt = null;
                        List<Patient> listP = dao.findAllHSQL(
                                "select o from Patient o where o.regNo = '" + txtCusId.getText().trim() + "'");
                        if (listP != null) {
                            if (!listP.isEmpty()) {
                                ptt = listP.get(0);
                            }
                        }
                        dao.close();
                        if (ptt == null) {
                            txtCusId.setText(null);
                            txtCusName.setText(null);
                            currSaleVou.setCustomerId(null);

                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Invalid Patient code.",
                                    "Patient Code", JOptionPane.ERROR_MESSAGE);
                            txtCusId.requestFocus();
                        } else {
                            selected("PatientList", ptt);
                            txtDrCode.requestFocus();
                        }
                        break;
                    case "School":
                        //String url = Util1.getPropValue("system.app.school.url");
                        dao.open();
                        SchoolDataAccess sda = new SchoolDataAccess(dao);
                        String stuNo = txtCusId.getText();
                        VMarchant vm = sda.getMarchant(stuNo);
                        dao.close();
                        if (vm == null) {
                            txtCusId.setText(null);
                            txtCusName.setText(null);
                            currSaleVou.setRegNo(null);
                            currSaleVou.setStuName(null);
                            currSaleVou.setStuNo(null);

                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Invalid student number or not enroll student.",
                                    "Student Number", JOptionPane.ERROR_MESSAGE);
                        } else {
                            txtCusId.setText(vm.getPersonNumber());
                            txtCusName.setText(vm.getPersonName());
                            currSaleVou.setRegNo(vm.getPersonId());
                            currSaleVou.setStuName(vm.getPersonName());
                            currSaleVou.setStuNo(vm.getPersonNumber());
                            tblSale.requestFocusInWindow();
                        }
                        break;
                    default:
                        dao.open();
                        Trader cus = (Trader) dao.find(Trader.class, txtCusId.getText());
                        dao.close();
                        if (cus == null) {
                            txtCusId.setText(null);
                            txtCusName.setText(null);
                            currSaleVou.setCustomerId(null);

                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Invalid customer code.",
                                    "Trader Code", JOptionPane.ERROR_MESSAGE);

                        } else {
                            selected("CustomerList", cus);
                            tblSale.requestFocusInWindow();
                        }
                        break;
                }
            } catch (Exception ex) {
                log.error("getCustomer : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtCusId.requestFocus();
        }

        /*
         * if (cus == null && !txtCusId.getText().isEmpty()) { getCustomerList(); }
         * else { selected("CustomerList", cus); tblSale.requestFocusInWindow(); }
         */
    }

    private void removeEmptyRow() {
        if (listDetail.size() > 0) {
            listDetail.remove(listDetail.size() - 1);
        }
        if (listExpense != null) {
            if (!listExpense.isEmpty()) {
                if (listExpense.size() > 0) {
                    listExpense.remove(listExpense.size() - 1);
                }
            }
        }
    }

    private void getTraderTransaction() {
        Trader trader = currSaleVou.getCustomerId();
        String strTrdOpt;
        String strTodayDateTime = getTranDateTime();

        if (trader.getTraderId().contains("SUP")) {
            strTrdOpt = "SUP";
        } else {
            strTrdOpt = "CUS";
        }
        String appCurr = Util1.getPropValue("system.app.currency");
        
        try {
            dao.execProc("get_trader_transaction",
                    trader.getTraderId(), strTrdOpt, strTodayDateTime, appCurr,
                    Global.loginUser.getUserId(),
                    Global.machineId);

            List<TraderTransaction> listTran = dao.findAll("TraderTransaction",
                    "userId = '" + Global.loginUser.getUserId() + "' and tranType = 'D'"
                    + " and machineId = '" + Global.machineId + "'");
            if (listTran != null) {
                tranTableModel.setListDetail(listTran);
            }

            listTTDetail = dao.findAll("TTranDetail", "userId = '" + Global.loginUser.getUserId()
                    + "' and machineId = '" + Global.machineId + "'");
            ttdTableModel.setListTTranDetail(listTTDetail);
        } catch (Exception ex) {
            log.error("getTraderTransaction : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    private String getTranDateTime() {
        String strTodayDateTime = null;

        switch (lblStatus.getText()) {
            case "NEW":
            case "DELETED":
                strTodayDateTime = DateUtil.toDateStrMYSQL(txtSaleDate.getText()) + " "
                        + DateUtil.getTodayTimeStrMYSQL();
                break;
            case "EDIT":
            case "RESTORE":
                strTodayDateTime = DateUtil.toDateTimeStrMYSQL(currSaleVou.getSaleDate());
                break;
            default:

        }

        return strTodayDateTime;
    }

    private void getTraderLastBalance() {
        String strTodayDateTime = getTranDateTime();

        //Trader last balance
        if (!strTodayDateTime.isEmpty()) {
            String strTrdOpt;
            if (currSaleVou.getCustomerId().getTraderId().contains("SUP")) {
                strTrdOpt = "SUP";
            } else {
                strTrdOpt = "CUS";
            }

            String appCurr = Util1.getPropValue("system.app.currency");
            try {
                ResultSet resultSet = dao.getPro("trader_last_balance",
                        currSaleVou.getCustomerId().getTraderId(), strTrdOpt,
                        strTodayDateTime, appCurr);

                //TraderTransaction tt = new TraderTransaction();
                if (resultSet != null) {
                    resultSet.next();
                    txtSaleLastBalance.setValue(resultSet.getDouble("var_last_balance"));
                    //tt.setAmount(resultSet.getDouble("var_last_balance"));

                    if (resultSet.getDate("var_last_sale_date") != null) {
                        strLastSaleDate = DateUtil.toDateStr(resultSet.getDate("var_last_sale_date")) + " Balance : ";
                        lblSaleLastBal.setText(strLastSaleDate);
                    } else {
                        lblSaleLastBal.setText("Balance : ");
                    }
                    //getPayment(resultSet.getString("var_last_sale_date"), strTodayDateTime);
                    /*tt.setTranDate(resultSet.getDate("var_last_sale_date"));
                     tt.setTranOption("Pre. Balance : ");
                     tt.setUserId(Global.loginUser.getUserId());
                     tt.setTranType("N");
                     tt.setSortId(1);
                    
                     dao.save(tt);*/
                }
            } catch (Exception ex) {
                log.error("getTraderLastBalance : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        }
    }

    private void getPayment(String lastSaleDateTime, String currVouDateTime) {
        String strSql = "select v from TraderPayHis v where v.trader.traderId = '"
                + currSaleVou.getCustomerId().getTraderId()
                + "' and v.deleted = false and v.payDt > '"
                + lastSaleDateTime + "' and v.payDt <= '" + currVouDateTime + "'";
        List<TraderPayHis> listPay = dao.findAllHSQL(strSql);

        for (TraderPayHis tph : listPay) {
            selected("SelectPayment", tph);
        }
    }

    private boolean isHaveTransaction(String vouNo) {
        boolean status = false;
        ResultSet resultSet = dao.getPro("is_have_vou_transaction", vouNo);

        try {
            if (resultSet != null) {
                resultSet.next();
                String value = resultSet.getString("tstatus");

                if (value.equals("Y")) {
                    status = true;
                }
            }
        } catch (Exception ex) {
            log.error("isHaveTransaction : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        return status;
    }

    private void updatePayment() {
        String strDeleteHSQL = "update TraderPayHis h set h.saleVou = null, h.updatedDate = null where h.paymentId = ?";

        try {
            dao.open();
            dao.beginTran();

            if (!Util1.getNullTo(currSaleVou.getDeleted())) {
                //Add Payment
                String strHSQL = "update TraderPayHis h set h.saleVou=?, h.updatedDate=? where h.paymentId =?";
                List<TraderTransaction> listTransaction = tranTableModel.getListDetail();
                for (TraderTransaction tt : listTransaction) {
                    if (tt.getTranOption().equals("Payment")) {
                        dao.executeUpdateHSQL(strHSQL, currSaleVou.getSaleInvId(),
                                currSaleVou.getSaleDate(),
                                tt.getPaymentId());
                    }
                }
            } else {
                //Remove Payment when voucher delete
                List<TraderTransaction> listTransaction = tranTableModel.getListDetail();
                for (TraderTransaction tt : listTransaction) {
                    if (tt.getTranOption().equals("Payment")) {
                        dao.executeUpdateHSQL(strDeleteHSQL, tt.getPaymentId());
                    }
                }
            }

            //Remove Payment
            List<Integer> deleteIds = tranTableModel.getDeletePayment();
            for (Integer id : deleteIds) {
                dao.executeUpdateHSQL(strDeleteHSQL, id);
                dao.deleteSQLNoTran("delete from trader_tran where user_id = '"
                        + Global.loginUser.getUserId() + "' and pay_id = "
                        + id);
            }
            tranTableModel.removeDeletePayment();

            dao.commit();
        } catch (Exception ex) {
            dao.rollBack();
            log.error("updatePayment : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
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

    @Override
    public void keyEvent(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (e.isControlDown() && (keyCode == KeyEvent.VK_F8)) {
            delete();
        } else if (e.isShiftDown() && (keyCode == KeyEvent.VK_F8)) {
            deleteCopy();
        } else if (keyCode == KeyEvent.VK_F5) {
            save();
        } else if (keyCode == KeyEvent.VK_F7) {
            print();
        } else if (keyCode == KeyEvent.VK_F9) {
            history();
        } else if (keyCode == KeyEvent.VK_F10) {
            newForm();
        }
    }

    private void applySecurityPolicy() {
        if (lblStatus.getText().equals("NEW")) {
            txtRemark.setEditable(true);
            if (!Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                txtCusId.setEditable(Util1.hashPrivilege("SaleCustomerChange"));
            }
            cboCurrency.setEnabled(Util1.hashPrivilege("SaleCurrencyChange"));
            cboLocation.setEnabled(Util1.hashPrivilege("SaleLocationChange"));
            cboPayment.setEnabled(Util1.hashPrivilege("SalePaymentChange"));
            cboVouStatus.setEnabled(Util1.hashPrivilege("SaleVouStatus"));
        } else {
            txtRemark.setEditable(Util1.hashPrivilege("SaleEditVoucherChange"));
            if (Util1.hashPrivilege("SaleEditVoucherChange")) {
                txtCusId.setEditable(Util1.hashPrivilege("SaleCustomerChange"));
                cboCurrency.setEnabled(Util1.hashPrivilege("SaleCurrencyChange"));
                cboLocation.setEnabled(Util1.hashPrivilege("SaleLocationChange"));
                cboPayment.setEnabled(Util1.hashPrivilege("SalePaymentChange"));
                cboVouStatus.setEnabled(Util1.hashPrivilege("SaleVouStatus"));
            } else {
                txtCusId.setEditable(false);
                cboCurrency.setEnabled(false);
                cboLocation.setEnabled(false);
                cboPayment.setEnabled(false);
                cboVouStatus.setEnabled(false);
            }
        }

        jScrollPane2.setVisible(Util1.hashPrivilege("SaleExpenseEntry"));
        jScrollPane3.setVisible(Util1.hashPrivilege("SaleCustomerInfoShow"));
        jScrollPane4.setVisible(Util1.hashPrivilege("SaleCustomerInfoShow"));
        //butPayment.setVisible(Util1.hashPrivilege("SaleCustomerInfoShow"));
        txtTotalExpense.setVisible(Util1.hashPrivilege("SaleExpenseEntry"));
        txtTtlExpIn.setVisible(Util1.hashPrivilege("SaleExpenseEntry"));
        lblExpTotal.setVisible(Util1.hashPrivilege("SaleExpenseEntry"));
        tblTransaction.setVisible(Util1.hashPrivilege("CusTransactionShow"));
        lblSaleLastBal.setVisible(Util1.hashPrivilege("SaleCustomerInfoShow"));
        txtSaleLastBalance.setVisible(Util1.hashPrivilege("SaleCustomerInfoShow"));
        lblCreditLimit.setVisible(Util1.hashPrivilege("SaleCustomerInfoShow"));
        txtCreditLimit.setVisible(Util1.hashPrivilege("SaleCustomerInfoShow"));
        lblLastBalance.setVisible(Util1.hashPrivilege("SaleCustomerInfoShow"));
        txtCusLastBalance.setVisible(Util1.hashPrivilege("SaleCustomerInfoShow"));
        lblDifference.setVisible(Util1.hashPrivilege("SaleCustomerInfoShow"));
        txtDifference.setVisible(Util1.hashPrivilege("SaleCustomerInfoShow"));
        butOutstanding.setVisible(Util1.hashPrivilege("SaleExpenseEntry"));
    }

    private void addSelectionListenerTblTransaction() {
        //Define table selection model to single row selection.
        tblTransaction.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Adding table row selection listener.
        tblTransaction.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblTransaction.getSelectedRow() >= 0) {
                    int selRow = tblTransaction.convertRowIndexToModel(
                            tblTransaction.getSelectedRow());
                    TraderTransaction tt = tranTableModel.getTransaction(selRow);

                    if (!tt.getTranOption().equals("Payment")) {
                        applyTblTranDetailFilter("tranOption = '" + tt.getTranOption().trim()
                                + "' AND tranDate = '" + tt.getTranDate().toString() + "'");
                        lblDate.setText(DateUtil.toDateStr(tt.getTranDate()));
                        lblTranOption.setText(tt.getTranOption());
                    } else {
                        lblDate.setText("");
                        lblTranOption.setText("");
                        ttdTableModel.clear();
                    }
                }
            }
        });
    }

    private void applyTblTranDetailFilter(String filter) {
        final String TABLE = "com.cv.app.pharmacy.database.helper.TTranDetail";
        String strSQL = "SELECT * FROM " + TABLE
                + " WHERE " + filter;

        List<TTranDetail> listTTD = JoSQLUtil.getResult(strSQL, listTTDetail);
        ttdTableModel.setListTTranDetail(listTTD);
    }

    private void getDoctor() {
        if (txtDrCode.getText() != null && !txtDrCode.getText().isEmpty()) {
            try {
                Doctor dr;

                dao.open();
                dr = (Doctor) dao.find(Doctor.class, txtDrCode.getText());
                dao.close();

                if (dr == null) {
                    txtDrCode.setText(null);
                    txtDrName.setText(null);
                    currSaleVou.setDoctor(null);
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid doctor code.",
                            "Doctor Code", JOptionPane.ERROR_MESSAGE);
                    txtDrCode.requestFocus();
                } else {
                    selected("DoctorSearch", dr);
                    tblSale.requestFocus();
                }
            } catch (Exception ex) {
                log.error("getDoctor : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        } else {
            txtDrCode.setText(null);
            txtDrName.setText(null);
            currSaleVou.setDoctor(null);
            //txtDrCode.requestFocus();
        }
    }

    /*
     * This method is used for when voucher delete then
     * set null in payment his.
     */
    private void updatePayent(String vouNo, String traderId) {
        String strSql = "update payment_his set sale_vou = null "
                + "where sale_vou = '" + vouNo + "' and trader_id = '" + traderId
                + "' and deleted = false";

        try {
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("updatePayent : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void warranty() {
        SaleWarrantyDialog dialog = new SaleWarrantyDialog(listDetail,
                currSaleVou.getWarrandy(), txtVouNo.getText());
        currSaleVou.setWarrandy(dialog.getWarranty());
    }

    private Action traderF3Action = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            getCustomerList();
        }
    };

    public void finder(String str) {
        if (!str.isEmpty()) {
            int indexFound = -1;
            for (int i = 0; i < listDetail.size(); i++) {
                SaleDetailHis sdh = listDetail.get(i);
                Medicine med = sdh.getMedId();

                if (med != null) {
                    if (med.getMedId().contains(str) || med.getMedName().contains(str)) {
                        indexFound = i;
                        i = listDetail.size();
                    }
                }
            }

            if (indexFound != -1) {
                tblSale.setRowSelectionInterval(indexFound, indexFound);
                Rectangle rect = tblSale.getCellRect(tblSale.getSelectedRow(), 0, true);
                tblSale.scrollRectToVisible(rect);
            }
        }
    }

    private void txtRemark1ActionPerformed(java.awt.event.ActionEvent evt) {

    }

    private void txtCusAddressMouseClicked(java.awt.event.MouseEvent evt) {

    }

    private void uploadToAccount(SaleHis sh) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            if (!Global.mqConnection.isStatus()) {
                String mqUrl = Util1.getPropValue("system.mqserver.url");
                Global.mqConnection = new ActiveMQConnection(mqUrl);
            }
            if (Global.mqConnection != null) {
                if (Global.mqConnection.isStatus()) {
                    try {
                        ActiveMQConnection mq = Global.mqConnection;
                        MapMessage msg = mq.getMapMessageTemplate();
                        msg.setString("program", Global.programId);
                        msg.setString("entity", "SALE");
                        msg.setString("vouNo", sh.getSaleInvId());
                        msg.setString("remark", sh.getRemark());
                        msg.setString("cusId", sh.getCustomerId().getAccountId());
                        msg.setString("saleDate", DateUtil.toDateStr(sh.getSaleDate(), "yyyy-MM-dd"));
                        msg.setBoolean("deleted", sh.getDeleted());
                        msg.setDouble("vouTotal", sh.getVouTotal());
                        msg.setDouble("discount", sh.getDiscount());
                        msg.setDouble("payment", sh.getPaid());
                        msg.setDouble("tax", sh.getTaxAmt());
                        msg.setString("currency", sh.getCurrencyId().getCurrencyAccId());
                        if (sh.getCustomerId().getTraderGroup() != null) {
                            msg.setString("sourceAccId", sh.getCustomerId().getTraderGroup().getAccountId());
                        } else {
                            msg.setString("sourceAccId", "-");
                        }
                        msg.setString("queueName", "INVENTORY");

                        mq.sendMessage(Global.queueName, msg);
                    } catch (Exception ex) {
                        log.error("uploadToAccount : " + ex.getStackTrace()[0].getLineNumber() + " - " + sh.getSaleInvId() + " - " + ex);
                    }
                } else {
                    log.error("Connection status error : " + sh.getSaleInvId());
                }
            } else {
                log.error("Connection error : " + sh.getSaleInvId());
            }
        }
    }

    public void timerFocus() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtCusId.requestFocus();
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                if (dao.getRowCount("select count(*) from item_type_mapping where group_id =" + Global.loginUser.getUserRole().getRoleId()) > 0) {
                    Global.listItem = dao.findAll("Medicine", "active = true and medTypeId.itemTypeCode in (select a.key.itemType.itemTypeCode from ItemTypeMapping a)");
                } else {
                    Global.listItem = dao.findAll("Medicine", "active = true");
                }
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                System.gc();
                log.info("Sale Timer work.");
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void getPatientBill(String regNo) {
        try {
            List<PatientBillPayment> listPBP = new ArrayList();
            Double totalBalance = 0.0;
            String currency = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();

            try ( //dao.open();
                    ResultSet resultSet = dao.getPro("patient_bill_payment",
                            regNo, DateUtil.toDateStrMYSQL(txtSaleDate.getText()),
                            currency, Global.loginUser.getUserId())) {
                while (resultSet.next()) {
                    double bal = resultSet.getDouble("balance");
                    if (bal != 0) {
                        PatientBillPayment pbp = new PatientBillPayment();

                        pbp.setBillTypeDesp(resultSet.getString("payment_type_name"));
                        pbp.setBillTypeId(resultSet.getInt("bill_type"));
                        pbp.setCreatedBy(Global.loginUser.getUserId());
                        pbp.setCurrency(resultSet.getString("currency"));
                        pbp.setPayDate(DateUtil.toDate(txtSaleDate.getText()));
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

    private void insertTraderFilterCode(String traderId) {
        String currencyId;
        String strSQLDelete = "delete from tmp_trader_bal_filter where user_id = '"
                + Global.loginUser.getUserId() + "'";
        String strSQL = "insert into tmp_trader_bal_filter(trader_id,currency,op_date,user_id, amount) "
                + "select t.trader_id, t.cur_code, "
                + " ifnull(trop.op_date, '1900-01-01'),'" + Global.loginUser.getUserId()
                + "', trop.op_amount"
                + " from v_trader_cur t left join "
                + "(select trader_id, currency, max(op_date) op_date, op_amount from trader_op "
                + " where op_date <= '" + DateUtil.toDateStrMYSQL(DateUtil.subDateTo(DateUtil.toDate(txtSaleDate.getText()), -1)) + "'";

        strSQL = strSQL + " group by trader_id, currency) trop on t.trader_id = trop.trader_id "
                + " and t.cur_code = trop.currency where t.active = true ";

        if (cboCurrency.getSelectedItem() instanceof Currency) {
            currencyId = ((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
            strSQL = strSQL + " and t.cur_code = '" + currencyId + "'";
        }

        strSQL = strSQL + " and t.trader_id = '" + traderId + "'";

        try {
            dao.open();
            dao.execSql(strSQLDelete);
            dao.close();

            dao.open();
            dao.execSql(strSQL);
            //dao.execSql(strSQLDelete, strSQL);
        } catch (Exception ex) {
            log.error("insertTraderFilter : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void execTraderBalanceWithUPV() {
        dao.execProc("get_trader_unpaid_vou", Global.loginUser.getUserId(),
                DateUtil.toDateStrMYSQL(txtSaleDate.getText()),
                DateUtil.toDateStrMYSQL(txtDueDate.getText()));
    }

    private boolean isOverDue(String traderId) {
        insertTraderFilterCode(traderId);
        execTraderBalanceWithUPV();
        Date tmpDate = DateUtil.toDate(txtSaleDate.getText(), "dd/MM/yyyy");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(tmpDate); // Now use today date.
            int ttlDay = 0;
            String strTmpValue = Util1.getPropValue("system.overdue.ttlday");
            if (strTmpValue != null) {
                if (!strTmpValue.isEmpty()) {
                    ttlDay = Integer.parseInt(strTmpValue);
                }
            }
            c.add(Calendar.DAY_OF_MONTH, ttlDay);
            tmpDate = c.getTime();
        } catch (Exception ex) {
            log.error("isOverDue : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        String strTmpDate = DateUtil.toDateStr(tmpDate, "dd/MM/yyyy");
        boolean status = false;
        String strSql = "select o from TraderUnpaidVou o where o.userId = '"
                + Global.loginUser.getUserId() + "' and o.dueDate < '"
                + DateUtil.toDateStrMYSQL(strTmpDate) + "'";
        List<TraderUnpaidVou> listTUV = dao.findAllHSQL(strSql);
        if (listTUV != null) {
            if (!listTUV.isEmpty()) {
                status = true;
                JOptionPane.showMessageDialog(Util1.getParent(), "Overdue voucher have.\nYou cannot open new voucher.",
                        "Overdue Voucher", JOptionPane.ERROR_MESSAGE);
            }
        }
        return status;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        lblPatient = new javax.swing.JLabel();
        lblDoctor = new javax.swing.JLabel();
        txtCusId = new javax.swing.JTextField();
        txtDrCode = new javax.swing.JTextField();
        txtCusName = new javax.swing.JTextField();
        txtDrName = new javax.swing.JTextField();
        lblRemark1 = new javax.swing.JLabel();
        txtRemark1 = new javax.swing.JTextField();
        txtCusAddress = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtSaleDate = new javax.swing.JFormattedTextField();
        txtDueDate = new javax.swing.JFormattedTextField();
        cboCurrency = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        cboPayment = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        cboVouStatus = new javax.swing.JComboBox();
        txtFilter = new javax.swing.JTextField();
        txtAdmissionNo = new javax.swing.JTextField();
        butAdmit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSale = new javax.swing.JTable(saleTableModel);
        jPanel3 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        panelExpense = new javax.swing.JPanel();
        lblDate = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblExpense = new javax.swing.JTable(expTableModel);
        lblExpTotal = new javax.swing.JLabel();
        txtTotalExpense = new javax.swing.JFormattedTextField();
        txtTtlExpIn = new javax.swing.JFormattedTextField();
        jPanel5 = new javax.swing.JPanel();
        lblCreditLimit = new javax.swing.JLabel();
        txtCreditLimit = new javax.swing.JFormattedTextField();
        lblDifference = new javax.swing.JLabel();
        txtDifference = new javax.swing.JFormattedTextField();
        txtSaleLastBalance = new javax.swing.JFormattedTextField();
        lblSaleLastBal = new javax.swing.JLabel();
        lblLastBalance = new javax.swing.JLabel();
        txtCusLastBalance = new javax.swing.JFormattedTextField();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblStockList = new javax.swing.JTable();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTransaction = new javax.swing.JTable();
        lblTranOption = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblTranDetail = new javax.swing.JTable();
        butPayment = new javax.swing.JButton();
        butWarranty = new javax.swing.JButton();
        butOutstanding = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblPatientBill = new javax.swing.JTable();
        txtBillTotal = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtRecNo = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtTotalItem = new javax.swing.JTextField();
        butRestore = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        txtTax = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtVouDiscount = new javax.swing.JFormattedTextField();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        txtTaxP = new javax.swing.JTextField();
        txtDiscP = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtGrandTotal = new javax.swing.JFormattedTextField();
        txtVouPaid = new javax.swing.JFormattedTextField();

        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.setNextFocusableComponent(txtSaleDate);
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(txtVouNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 144, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtRemark))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel1)
                .add(txtVouNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLabel5)
                .add(txtRemark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        lblPatient.setFont(Global.lableFont);
        lblPatient.setText("Patient No/Name");

        lblDoctor.setFont(Global.lableFont);
        lblDoctor.setText("Doctor No/Name");

        txtCusId.setFont(Global.textFont);
        txtCusId.setName("txtCusId"); // NOI18N
        txtCusId.setNextFocusableComponent(tblSale);
        txtCusId.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtCusIdFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtCusIdFocusLost(evt);
            }
        });
        txtCusId.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusIdMouseClicked(evt);
            }
        });
        txtCusId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusIdActionPerformed(evt);
            }
        });

        txtDrCode.setFont(Global.textFont);
        txtDrCode.setName("txtDrCode"); // NOI18N
        txtDrCode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtDrCodeFocusGained(evt);
            }
        });
        txtDrCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDrCodeActionPerformed(evt);
            }
        });

        txtCusName.setEditable(false);
        txtCusName.setFont(Global.textFont);
        txtCusName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusNameMouseClicked(evt);
            }
        });
        txtCusName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCusNameActionPerformed(evt);
            }
        });

        txtDrName.setEditable(false);
        txtDrName.setFont(Global.textFont);
        txtDrName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDrNameMouseClicked(evt);
            }
        });

        lblRemark1.setFont(Global.lableFont);
        lblRemark1.setText("Remark");

        txtRemark1.setFont(Global.textFont);
        txtRemark1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemark1FocusGained(evt);
            }
        });
        txtRemark1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRemark1ActionPerformed(evt);
            }
        });

        txtCusAddress.setEditable(false);
        txtCusAddress.setFont(Global.textFont);
        txtCusAddress.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusAddressMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(lblPatient)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(txtCusId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                        .add(lblDoctor)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(txtDrCode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtCusName)
                    .add(txtDrName))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(lblRemark1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtRemark1))
                    .add(txtCusAddress))
                .add(0, 0, 0))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(txtCusName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(txtCusAddress, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(lblPatient)
                        .add(txtCusId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(lblRemark1)
                        .add(txtRemark1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(txtDrCode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(txtDrName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(lblDoctor))))
        );

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Sale Date ");

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Due Date");

        jLabel24.setFont(Global.lableFont);
        jLabel24.setText("Currency");

        txtSaleDate.setEditable(false);
        txtSaleDate.setFont(Global.textFont);
        txtSaleDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtSaleDateFocusGained(evt);
            }
        });
        txtSaleDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtSaleDateMouseClicked(evt);
            }
        });
        txtSaleDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSaleDateActionPerformed(evt);
            }
        });

        txtDueDate.setEditable(false);
        txtDueDate.setFont(Global.textFont);
        txtDueDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDueDateMouseClicked(evt);
            }
        });

        cboCurrency.setFont(Global.textFont);
        cboCurrency.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Location");

        cboLocation.setFont(Global.textFont);
        cboLocation.setModel(new javax.swing.DefaultComboBoxModel());
        cboLocation.setName("cboLocation"); // NOI18N
        cboLocation.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboLocationFocusGained(evt);
            }
        });
        cboLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLocationActionPerformed(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Payment ");

        cboPayment.setFont(Global.textFont);
        cboPayment.setModel(new javax.swing.DefaultComboBoxModel());
        cboPayment.setName("cboPayment"); // NOI18N
        cboPayment.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                cboPaymentFocusGained(evt);
            }
        });
        cboPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPaymentActionPerformed(evt);
            }
        });

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("Vou Status");

        cboVouStatus.setFont(Global.textFont);
        cboVouStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel8Layout.createSequentialGroup()
                .add(14, 14, 14)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel2)
                    .add(jLabel3)
                    .add(jLabel24))
                .add(18, 18, 18)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(cboCurrency, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(txtDueDate)
                    .add(txtSaleDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 137, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel6)
                    .add(jLabel16)
                    .add(jLabel7))
                .add(18, 18, 18)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, cboPayment, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, cboLocation, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(cboVouStatus, 0, 121, Short.MAX_VALUE)))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel3)
                            .add(txtDueDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel24)
                            .add(cboCurrency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel7)
                            .add(cboLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel2)
                            .add(txtSaleDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel6)
                            .add(cboPayment, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel16)
                            .add(cboVouStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .add(0, 8, Short.MAX_VALUE))
        );

        txtFilter.setFont(Global.textFont);
        txtFilter.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFilterFocusGained(evt);
            }
        });
        txtFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilterActionPerformed(evt);
            }
        });
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        txtAdmissionNo.setEditable(false);
        txtAdmissionNo.setFont(Global.textFont);

        butAdmit.setText("Admit");
        butAdmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butAdmitActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(txtFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 194, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(txtAdmissionNo)
                        .addContainerGap())
                    .add(butAdmit)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(txtAdmissionNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(butAdmit)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        tblSale.setFont(Global.textFont);
        tblSale.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblSale.setRowHeight(23);
        tblSale.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblSaleFocusGained(evt);
            }
        });
        jScrollPane1.setViewportView(tblSale);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1176, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );

        jScrollPane2.setPreferredSize(new java.awt.Dimension(454, 150));

        tblExpense.setFont(Global.textFont);
        tblExpense.setModel(expTableModel);
        tblExpense.setRowHeight(23);
        jScrollPane2.setViewportView(tblExpense);

        lblExpTotal.setFont(Global.lableFont);
        lblExpTotal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblExpTotal.setText("Exp Total : ");

        txtTotalExpense.setEditable(false);
        txtTotalExpense.setFont(Global.textFont);

        txtTtlExpIn.setEditable(false);
        txtTtlExpIn.setFont(Global.textFont);

        org.jdesktop.layout.GroupLayout panelExpenseLayout = new org.jdesktop.layout.GroupLayout(panelExpense);
        panelExpense.setLayout(panelExpenseLayout);
        panelExpenseLayout.setHorizontalGroup(
            panelExpenseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .add(panelExpenseLayout.createSequentialGroup()
                .add(lblExpTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtTtlExpIn)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtTotalExpense))
        );
        panelExpenseLayout.setVerticalGroup(
            panelExpenseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelExpenseLayout.createSequentialGroup()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(panelExpenseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblExpTotal)
                    .add(txtTotalExpense, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtTtlExpIn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        lblCreditLimit.setFont(Global.lableFont);
        lblCreditLimit.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCreditLimit.setText("Credit limit : ");

        txtCreditLimit.setEditable(false);
        txtCreditLimit.setFont(Global.textFont);

        lblDifference.setFont(Global.lableFont);
        lblDifference.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDifference.setText("Difference : ");

        txtDifference.setEditable(false);
        txtDifference.setFont(Global.textFont);

        txtSaleLastBalance.setEditable(false);
        txtSaleLastBalance.setFont(Global.textFont);

        lblSaleLastBal.setFont(Global.lableFont);
        lblSaleLastBal.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSaleLastBal.setText("Balance : ");

        lblLastBalance.setFont(Global.lableFont);
        lblLastBalance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblLastBalance.setText("Last Balance : ");

        txtCusLastBalance.setEditable(false);
        txtCusLastBalance.setFont(Global.textFont);

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(lblLastBalance)
                    .add(lblDifference))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtCusLastBalance, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                    .add(txtDifference))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lblCreditLimit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                    .add(lblSaleLastBal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(txtSaleLastBalance, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                    .add(txtCreditLimit)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtSaleLastBalance, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblSaleLastBal)
                    .add(txtCusLastBalance, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblLastBalance))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblCreditLimit)
                    .add(txtCreditLimit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtDifference, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblDifference)))
        );

        tblStockList.setFont(Global.textFont);
        tblStockList.setModel(stockTableModel);
        tblStockList.setRowHeight(23);
        jScrollPane6.setViewportView(tblStockList);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelExpense, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(panelExpense, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        tblTransaction.setFont(Global.textFont);
        tblTransaction.setModel(tranTableModel);
        tblTransaction.setRowHeight(23);
        jScrollPane3.setViewportView(tblTransaction);

        tblTranDetail.setFont(Global.textFont);
        tblTranDetail.setModel(ttdTableModel);
        tblTranDetail.setRowHeight(23);
        jScrollPane4.setViewportView(tblTranDetail);

        butPayment.setFont(Global.textFont);
        butPayment.setText("Payment");
        butPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPaymentActionPerformed(evt);
            }
        });

        butWarranty.setFont(Global.textFont);
        butWarranty.setText("Warranty");
        butWarranty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butWarrantyActionPerformed(evt);
            }
        });

        butOutstanding.setFont(Global.textFont);
        butOutstanding.setText("Outstanding");
        butOutstanding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOutstandingActionPerformed(evt);
            }
        });

        tblPatientBill.setFont(Global.textFont);
        tblPatientBill.setModel(tblPatientBillTableModel);
        tblPatientBill.setRowHeight(23);
        jScrollPane5.setViewportView(tblPatientBill);

        txtBillTotal.setEditable(false);
        txtBillTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtBillTotal.setFont(Global.textFont);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Total : ");

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel12Layout.createSequentialGroup()
                        .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel12Layout.createSequentialGroup()
                                .add(jLabel4)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(txtBillTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 129, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 352, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(0, 0, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel12Layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(lblTranOption, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 357, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 357, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jPanel12Layout.createSequentialGroup()
                                    .add(butPayment, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 106, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(butWarranty)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(butOutstanding, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 357, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel12Layout.createSequentialGroup()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblTranOption, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 50, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(butPayment)
                    .add(butWarranty)
                    .add(butOutstanding))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtBillTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4)))
        );

        lblStatus.setFont(new java.awt.Font("Velvenda Cooler", 0, 40)); // NOI18N
        lblStatus.setText("NEW");

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("Rec No : ");

        txtRecNo.setEditable(false);
        txtRecNo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtRecNo.setBorder(null);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("Total Item : ");

        txtTotalItem.setEditable(false);
        txtTotalItem.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalItem.setBorder(null);

        butRestore.setText("Restore");
        butRestore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRestoreActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel13Layout = new org.jdesktop.layout.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(lblStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel13Layout.createSequentialGroup()
                        .add(jLabel23)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtRecNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel13Layout.createSequentialGroup()
                        .add(jLabel22)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtTotalItem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(butRestore)
                .addContainerGap())
        );

        jPanel13Layout.linkSize(new java.awt.Component[] {jLabel22, jLabel23}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .add(lblStatus)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel23)
                    .add(txtRecNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel22)
                    .add(txtTotalItem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 66, Short.MAX_VALUE)
                .add(butRestore)
                .addContainerGap())
        );

        txtTax.setEditable(false);
        txtTax.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Paid : ");

        jLabel19.setFont(Global.lableFont);
        jLabel19.setText("Tax(+) : ");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Discount : ");

        jLabel18.setText("%");

        txtVouDiscount.setEditable(false);
        txtVouDiscount.setFont(Global.textFont);

        txtVouBalance.setEditable(false);
        txtVouBalance.setFont(Global.textFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Vou Balance : ");

        txtTaxP.setEditable(false);
        txtTaxP.setFont(Global.textFont);
        txtTaxP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtDiscP.setEditable(false);
        txtDiscP.setFont(Global.textFont);
        txtDiscP.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel17.setText("%");

        jLabel14.setFont(Global.lableFont);
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText("Grand Total : ");

        txtVouTotal.setEditable(false);
        txtVouTotal.setFont(Global.textFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Gross Total : ");

        txtGrandTotal.setEditable(false);
        txtGrandTotal.setFont(Global.textFont);

        txtVouPaid.setEditable(false);
        txtVouPaid.setFont(Global.textFont);
        txtVouPaid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVouPaidActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 78, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel19))
                            .add(jLabel14))
                        .add(jLabel9))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel11))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(txtGrandTotal)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtVouPaid)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtVouTotal)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtVouBalance)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel9Layout.createSequentialGroup()
                        .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtTaxP, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                            .add(txtDiscP))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel17)
                            .add(jLabel18))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(txtVouDiscount)
                            .add(txtTax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator2)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator1)
        );

        jPanel9Layout.linkSize(new java.awt.Component[] {jLabel10, jLabel11, jLabel14, jLabel8, jLabel9}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtVouTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtVouDiscount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtDiscP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel18)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel17)
                    .add(txtTaxP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel19)
                    .add(txtTax, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(4, 4, 4)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtGrandTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel14))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtVouPaid, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtVouBalance, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel11))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9Layout.linkSize(new java.awt.Component[] {txtGrandTotal, txtVouPaid}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel9Layout.linkSize(new java.awt.Component[] {txtDiscP, txtVouDiscount}, org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel9Layout.linkSize(new java.awt.Component[] {txtTaxP, txtVouBalance}, org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 245, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(0, 0, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(10, 10, 10))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 208, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSaleDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSaleDateMouseClicked
        if (evt.getClickCount() == 2) {
            if (Util1.hashPrivilege("SaleDateChange")) {
                String strDate = DateUtil.getDateDialogStr();

                if (strDate != null) {
                    txtSaleDate.setText(strDate);
                    saleTableModel.setSaleDate(DateUtil.toDate(txtSaleDate.getText()));
                    if (lblStatus.getText().equals("NEW")) {
                        vouEngine.setPeriod(DateUtil.getPeriod(txtSaleDate.getText()));
                        genVouNo();
                        if (currSaleVou.getCustomerId() != null) {
                            int creditDay = NumberUtil.NZeroInt(((Customer) currSaleVou.getCustomerId()).getCreditDays());
                            if (creditDay != 0) {
                                txtDueDate.setText(DateUtil.subDateTo(DateUtil.toDate(strDate), creditDay));
                            } else {
                                txtDueDate.setText(null);
                            }
                        } else {
                            txtDueDate.setText(null);
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_txtSaleDateMouseClicked

    private void txtDueDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDueDateMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtDueDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtDueDateMouseClicked

    private void txtCusIdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusIdMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusIdMouseClicked

    private void txtCusIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusIdActionPerformed
        if (txtCusId.getText() == null || txtCusId.getText().isEmpty()) {
            txtCusName.setText(null);
            currSaleVou.setCustomerId(null);
            currSaleVou.setRegNo(null);
            currSaleVou.setStuName(null);
            currSaleVou.setStuNo(null);
            //txtCusId.requestFocus();
        } else {
            getCustomer();
        }
    }//GEN-LAST:event_txtCusIdActionPerformed

    private void txtCusIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusIdFocusLost
        //if(!txtCusId.getText().equals(""))
        //getCustomer();
    }//GEN-LAST:event_txtCusIdFocusLost

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusNameMouseClicked

    private void cboPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPaymentActionPerformed
        calculateTotalAmount();
    }//GEN-LAST:event_cboPaymentActionPerformed

  private void txtDrNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDrNameMouseClicked
      DoctorSearchDialog dialog = new DoctorSearchDialog(dao, this);
  }//GEN-LAST:event_txtDrNameMouseClicked

  private void txtDrCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDrCodeActionPerformed
      getDoctor();
  }//GEN-LAST:event_txtDrCodeActionPerformed

  private void txtCusNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusNameActionPerformed
      // TODO add your handling code here:
  }//GEN-LAST:event_txtCusNameActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilterActionPerformed
        finder(txtFilter.getText());
    }//GEN-LAST:event_txtFilterActionPerformed

    private void txtVouPaidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVouPaidActionPerformed
        calculateTotalAmount();
    }//GEN-LAST:event_txtVouPaidActionPerformed

    private void butOutstandingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOutstandingActionPerformed
        saleOutstanding();
    }//GEN-LAST:event_butOutstandingActionPerformed

    private void butWarrantyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butWarrantyActionPerformed
        warranty();
    }//GEN-LAST:event_butWarrantyActionPerformed

    private void butPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPaymentActionPerformed
        if (!txtCusId.getText().isEmpty()) {
            PaymentListDialog dialog = new PaymentListDialog(dao, this, txtCusId.getText());
        }
    }//GEN-LAST:event_butPaymentActionPerformed

    private void txtSaleDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSaleDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSaleDateActionPerformed

    private void cboLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLocationActionPerformed
        saleTableModel.setLocation((Location) cboLocation.getSelectedItem());
    }//GEN-LAST:event_cboLocationActionPerformed

    private void txtCusIdFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusIdFocusGained
        focusCtrlName = "txtCusId";
        txtCusId.selectAll();
    }//GEN-LAST:event_txtCusIdFocusGained

    private void txtDrCodeFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtDrCodeFocusGained
        focusCtrlName = "txtDrCode";
        txtDrCode.selectAll();
    }//GEN-LAST:event_txtDrCodeFocusGained

    private void cboLocationFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboLocationFocusGained
        focusCtrlName = "cboLocation";
    }//GEN-LAST:event_cboLocationFocusGained

    private void cboPaymentFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cboPaymentFocusGained
        focusCtrlName = "cboPayment";
    }//GEN-LAST:event_cboPaymentFocusGained

    private void tblSaleFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblSaleFocusGained
        int selRow = tblSale.getSelectedRow();

        focusCtrlName = "tblSale";
        if (selRow == -1) {
            //tblSale.editCellAt(0, 0);
            tblSale.changeSelection(0, 0, false, false);
        }
    }//GEN-LAST:event_tblSaleFocusGained

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
        // TODO add your handling code here:
        txtCusId.requestFocus();
    }//GEN-LAST:event_formFocusGained

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        txtCusId.requestFocus();
    }//GEN-LAST:event_formMouseClicked

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained
        focusCtrlName = "txtRemark";
        txtRemark.selectAll();
    }//GEN-LAST:event_txtRemarkFocusGained

    private void txtRemark1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemark1FocusGained
        txtRemark1.selectAll();
    }//GEN-LAST:event_txtRemark1FocusGained

    private void txtSaleDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtSaleDateFocusGained
        focusCtrlName = "txtSaleDate";
        txtSaleDate.selectAll();
    }//GEN-LAST:event_txtSaleDateFocusGained

    private void txtFilterFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFilterFocusGained
        txtFilter.selectAll();
    }//GEN-LAST:event_txtFilterFocusGained

    private void butAdmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butAdmitActionPerformed
        Patient pt = currSaleVou.getPatientId();
        if (txtAdmissionNo.getText().isEmpty() && pt != null) {
            try {
                RegNo regNo = new RegNo(dao, "amsNo");
                Ams ams = new Ams();

                ams.getKey().setRegister(pt);
                ams.getKey().setAmsNo(regNo.getRegNo());
                ams.setAmsDate(DateUtil.toDateTime(txtSaleDate.getText()));
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
                pt.setAdmissionNo(ams.getKey().getAmsNo());
                dao.save(pt);
                txtAdmissionNo.setText(ams.getKey().getAmsNo());
                if (Util1.getPropValue("system.admission.paytype").equals("CREDIT")) {
                    cboPayment.setSelectedItem(ptCredit);
                } else {
                    cboPayment.setSelectedItem(ptCash);
                }
            } catch (Exception ex) {
                log.error("admit : " + ex.getStackTrace()[0].getLineNumber() + " - " + pt.getRegNo() + " - " + ex);
            } finally {
                dao.close();
            }
        }
    }//GEN-LAST:event_butAdmitActionPerformed

    private void butRestoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRestoreActionPerformed
        vouEngine.setPeriod(DateUtil.getPeriod(txtSaleDate.getText()));
        genVouNo();

        removeEmptyRow();
        String vouNo = vouEngine.getVouNo();
        currSaleVou.setSaleInvId(vouNo);
        currSaleVou.setSaleDetailHis(listDetail);
        currSaleVou.setExpense(listExpense);
        currSaleVou.setListOuts(getOutstandingItem());

        //For BK Pagolay
        try {
            dao.save(currSaleVou);
            vouEngine.updateVouNo();
            //Upload to Account
            uploadToAccount(currSaleVou);
            newForm();
            deleteDetail();
        } catch (Exception ex) {
            log.error("restore : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
        JOptionPane.showMessageDialog(Util1.getParent(), "Success!",
                "Restore Voucher", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_butRestoreActionPerformed
    // </editor-fold>

    private void saleOutstanding() {
        if (listDetail.size() > 1) {
            SaleOutstandingDialog dialog = new SaleOutstandingDialog(listDetail,
                    medUp, currSaleVou.getListOuts());
            currSaleVou.setListOuts(dialog.getOutsList());
        }
    }

    private List<SaleOutstand> getOutstandingItem() {
        String className = "com.cv.app.pharmacy.database.entity.SaleOutstand";
        String strSQL = "SELECT * FROM " + className
                + " WHERE outsQtySmall > 0 ";

        List<SaleOutstand> list = JoSQLUtil.getResult(strSQL, currSaleVou.getListOuts());

        return list;
    }

    private void deleteOutstand(String medId, Date expDate, int chargeType) {
        List<SaleOutstand> listOut = currSaleVou.getListOuts();
        String option;

        if (chargeType == 2) {
            option = "Sale-Foc";
        } else {
            option = "Sale";
        }

        if (listOut != null) {
            try {
                dao.open();
                if (listOut.size() > 0) {
                    for (int i = 0; i < listOut.size(); i++) {
                        SaleOutstand so = listOut.get(i);

                        if (so.getItemId().getMedId().equals(medId)
                                && so.getOutsOption().equals(option)
                                && so.getExpDate() == expDate && NumberUtil.NZeroL(so.getOutsId()) > 0) {

                            if (deleteOutstandList == null) {
                                deleteOutstandList = so.getOutsId().toString();
                            } else {
                                deleteOutstandList = deleteOutstandList + "," + so.getOutsId().toString();
                            }

                            listOut.remove(i);
                            i = listOut.size();
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("deleteOutstand : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            } finally {
                dao.close();
            }
        }
    }

    private void deleteWarranty(String itemId) {
        List<SaleWarranty> listSW = currSaleVou.getWarrandy();

        if (listSW != null) {
            try {
                dao.open();
                if (listSW.size() > 0) {
                    for (int i = 0; i < listSW.size(); i++) {
                        SaleWarranty sw = listSW.get(i);

                        if (sw.getItem().getMedId().equals(itemId)) {
                            listSW.remove(i);
                            i = listSW.size();
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("deleteWarranty : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            } finally {
                dao.close();
            }
        }
    }

    private void deleteDetail() {
        String deleteSQL;

        //All detail section need to explicity delete
        //because of save function only delete to join table
        deleteSQL = saleTableModel.getDeleteSql();
        if (deleteSQL != null) {
            dao.execSql(deleteSQL);
        }

        deleteSQL = expTableModel.getDeleteSql();
        if (deleteSQL != null) {
            dao.execSql(deleteSQL);
        }

        if (deleteOutstandList != null) {
            dao.execSql("delete from sale_outstanding where outstanding_id in ("
                    + deleteOutstandList + ")");
            deleteOutstandList = null;
        }
        //delete section end
    }
    // <editor-fold defaultstate="collapsed" desc="Control Declaration">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butAdmit;
    private javax.swing.JButton butOutstanding;
    private javax.swing.JButton butPayment;
    private javax.swing.JButton butRestore;
    private javax.swing.JButton butWarranty;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JComboBox cboVouStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblCreditLimit;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDifference;
    private javax.swing.JLabel lblDoctor;
    private javax.swing.JLabel lblExpTotal;
    private javax.swing.JLabel lblLastBalance;
    private javax.swing.JLabel lblPatient;
    private javax.swing.JLabel lblRemark1;
    private javax.swing.JLabel lblSaleLastBal;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTranOption;
    private javax.swing.JPanel panelExpense;
    private javax.swing.JTable tblExpense;
    private javax.swing.JTable tblPatientBill;
    private javax.swing.JTable tblSale;
    private javax.swing.JTable tblStockList;
    private javax.swing.JTable tblTranDetail;
    private javax.swing.JTable tblTransaction;
    private javax.swing.JTextField txtAdmissionNo;
    private javax.swing.JFormattedTextField txtBillTotal;
    private javax.swing.JFormattedTextField txtCreditLimit;
    private javax.swing.JTextField txtCusAddress;
    private javax.swing.JTextField txtCusId;
    private javax.swing.JFormattedTextField txtCusLastBalance;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JFormattedTextField txtDifference;
    private javax.swing.JTextField txtDiscP;
    private javax.swing.JTextField txtDrCode;
    private javax.swing.JTextField txtDrName;
    private javax.swing.JFormattedTextField txtDueDate;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JFormattedTextField txtGrandTotal;
    private javax.swing.JTextField txtRecNo;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JTextField txtRemark1;
    private javax.swing.JFormattedTextField txtSaleDate;
    private javax.swing.JFormattedTextField txtSaleLastBalance;
    private javax.swing.JFormattedTextField txtTax;
    private javax.swing.JTextField txtTaxP;
    private javax.swing.JFormattedTextField txtTotalExpense;
    private javax.swing.JTextField txtTotalItem;
    private javax.swing.JFormattedTextField txtTtlExpIn;
    private javax.swing.JFormattedTextField txtVouBalance;
    private javax.swing.JFormattedTextField txtVouDiscount;
    private javax.swing.JFormattedTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouPaid;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
}
