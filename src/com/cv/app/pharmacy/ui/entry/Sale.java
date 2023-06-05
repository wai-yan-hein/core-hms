/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.RegNo;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.AdmissionKey;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.opd.database.entity.Booking;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.database.entity.PatientBillPayment;
import com.cv.app.opd.database.tempentity.TempAmountLink;
import com.cv.app.opd.ui.util.AmountLinkDialog;
import com.cv.app.opd.ui.util.AppointmentDoctorDialog;
import com.cv.app.opd.ui.util.DoctorSearchNameFilterDialog;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.controller.SchoolDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Customer;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.MachineInfo;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.PurchaseIMEINo;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.entity.SaleDetailHis;
import com.cv.app.pharmacy.database.entity.SaleExpense;
import com.cv.app.pharmacy.database.entity.SaleHis;
import com.cv.app.pharmacy.database.entity.SaleOutstand;
import com.cv.app.pharmacy.database.entity.SaleWarranty;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.Trader1;
import com.cv.app.pharmacy.database.entity.TraderPayHis;
import com.cv.app.pharmacy.database.entity.VouStatus;
import com.cv.app.pharmacy.database.helper.Stock;
import com.cv.app.pharmacy.database.helper.TTranDetail;
import com.cv.app.pharmacy.database.helper.TraderTransaction;
import com.cv.app.pharmacy.database.tempentity.TraderUnpaidVou;
import com.cv.app.pharmacy.database.view.VMarchant;
import com.cv.app.pharmacy.database.view.VMedicine1;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.PatientBillTableModel;
import com.cv.app.pharmacy.ui.common.SaleExpTableModel;
import com.cv.app.pharmacy.ui.common.SaleStockTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.common.SaleTableModel;
import com.cv.app.pharmacy.ui.common.TTranDetailTableModel;
import com.cv.app.pharmacy.ui.common.TransactionTableModel;
import com.cv.app.pharmacy.ui.util.MarchantSearch;
import com.cv.app.pharmacy.ui.util.MedListDialog1;
import com.cv.app.pharmacy.ui.util.MedPriceAutoCompleter;
import com.cv.app.pharmacy.ui.util.PaymentListDialog;
import com.cv.app.pharmacy.ui.util.PriceChangeDialog;
import com.cv.app.pharmacy.ui.util.SaleConfirmDialog1;
import com.cv.app.pharmacy.ui.util.SaleOutstandingDialog;
import com.cv.app.pharmacy.ui.util.SaleWarrantyDialog;
import com.cv.app.pharmacy.ui.util.TraderSearchDialog;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.MedicineUtil;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.pharmacy.util.StockList;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
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
import org.jdesktop.observablecollections.ObservableCollections;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author WSwe
 */
public class Sale extends javax.swing.JPanel implements SelectionObserver, FormAction,
        MedInfo, KeyPropagate, KeyListener {

    static Logger log = Logger.getLogger(Sale.class.getName());
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
    private PaymentType ptCash;
    private PaymentType ptCredit;
    private List<String> tmpVouList = new ArrayList(); //For temp voucher
    private String focusCtrlName = "-";
    private PatientBillTableModel tblPatientBillTableModel = new PatientBillTableModel();
    private SaleStockTableModel stockTableModel = new SaleStockTableModel();
    private boolean isBind = false;
    private boolean isDeleteCopy = false;
    private final SaleTableCodeCellEditor codeCellEditor = new SaleTableCodeCellEditor(dao);
    private final String strCodeFilter = Util1.getPropValue("system.item.location.filter");
    private boolean canEdit = true;
    private int mouseClick = 2;

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
    public Sale() {
        initComponents();
        initButtonGroup();
        initSpinner();
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
        saleTableModel.setLblRemark(lblRemark);
        saleTableModel.setLblItemBrand(lblBrandName);
        saleTableModel.setStockTableModel(stockTableModel);
        lblStatus.setText("NEW");

        String cusType = Util1.getPropValue("system.sale.default.cus.type");
        if (cusType.isEmpty()) {
            saleTableModel.setCusType("N", "con");
        } else {
            saleTableModel.setCusType(cusType, "con");
        }
        applySecurityPolicy();

        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
            lblPatient.setText("Patient No/Name");
            lblDoctor.setVisible(true);
            txtDrCode.setVisible(true);
            txtDrName.setVisible(true);
            txtCusAddress.setVisible(false);
            lblRemark1.setVisible(false);
            txtRemark1.setVisible(false);
            butWarranty.setVisible(false);
            butSaveTemp.setVisible(false);
            butTempList.setVisible(false);
            butAdmit.setVisible(true);
            butAdmit.setEnabled(false);
            jPanel5.setVisible(false);
            tblStockList.setVisible(true);

            /*jScrollPane2.setVisible(false);
            tblExpense.setVisible(false);
            jScrollPane3.setVisible(false);
            jScrollPane4.setVisible(false);
            lblTranOption.setVisible(false);*/
        } else if (Util1.getPropValue("system.app.usage.type").equals("School")) {
            lblPatient.setText("Student No/Name");
            lblDoctor.setVisible(false);
            txtDrCode.setVisible(false);
            txtDrName.setVisible(false);
            lblRemark1.setVisible(false);
            txtRemark1.setVisible(false);
            txtCusAddress.setVisible(false);
            butAdmit.setVisible(false);
            jScrollPane6.setVisible(false);
        } else {
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
            //tblStockList.setVisible(true);
            if (Util1.getPropValue("system.sale.show.stocktable").equals("Y")) {
                jScrollPane6.setVisible(true);
            } else {
                jScrollPane6.setVisible(false);
            }
        }
        lblDueRemark.setVisible(false);
        butSaveTemp.setVisible(false);
        butWarranty.setVisible(false);
        //butOutstanding.setVisible(false);
        butTempList.setVisible(false);
        butAdmit.setVisible(false);

        chkVouComp.setText(Util1.getPropValue("system.sale.comp.check.name"));
        try {
            ptCash = (PaymentType) dao.find(PaymentType.class,
                    NumberUtil.NZeroInt(Util1.getPropValue("system.paymenttype.cash")));
            ptCredit = (PaymentType) dao.find(PaymentType.class,
                    NumberUtil.NZeroInt(Util1.getPropValue("system.paymenttype.credit")));
        } catch (Exception ex) {
            log.error("Sale : " + ex.getMessage());
        } finally {
            dao.close();
        }
        assignDefaultValue();

        //timerFocus();
        butOTID.setEnabled(false);
        cboEntryUser.setVisible(false);

        String propValue = Util1.getPropValue("system.date.mouse.click");
        if (propValue != null) {
            if (!propValue.equals("-")) {
                if (!propValue.isEmpty()) {
                    int tmpValue = NumberUtil.NZeroInt(propValue);
                    if (tmpValue != 0) {
                        mouseClick = tmpValue;
                    }
                }
            }
        }
    }

    private void initButtonGroup() {
        ButtonGroup g = new ButtonGroup();
        g.add(chkA5);
        g.add(chkVouComp);
        g.add(chkPrintOption);
    }

    private void initSpinner() {
        int count = Util1.getIntegerOne(Util1.getPropValue("system.sale.print.count"));
        if (Util1.getPropValue("system.pharmacy.sale.print.double").equals("Y")) {
            count = 2;
        }
        spPrint.setModel(new SpinnerNumberModel(count, 0, 10, 1));
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
                try {
                tmpObj = dao.find(Trader.class, Util1.getPropValue("system.default.customer"));
                if (tmpObj != null) {
                    tmpCusId = ((Trader) tmpObj).getTraderId();
                    selected("CustomerList", tmpObj);
                }
            } catch (Exception ex) {
                log.error("assignDefaultValue : " + ex.getMessage());
            } finally {
                dao.close();
            }
            if (currSaleVou.getCustomerId() != null) {
                if (currSaleVou.getCustomerId().getTraderId().equals(tmpCusId)) {
                    cboPayment.setSelectedItem(ptCash);
                } else {
                    cboPayment.setSelectedItem(ptCredit);
                }
            } else {
                isBind = true;
                cboPayment.setSelectedItem(Global.loginUser.getUserRole().getPaymentType());
                isBind = false;
            }
            break;
        }

        tmpObj = Util1.getDefaultValue("Currency");
        if (tmpObj != null) {
            cboCurrency.setSelectedItem(tmpObj);
        }

        /*if (prvLocation != null) {
         tmpObj = prvLocation;
         } else*/
        if (Util1.getPropValue("system.login.default.value").equals("Y")) {
            tmpObj = Global.loginLocation;
        } else {
            tmpObj = Util1.getDefaultValue("Location");
        }
        if (tmpObj != null) {
            cboLocation.setSelectedItem(tmpObj);
            saleTableModel.setLocation((Location) tmpObj);
            int index = cboLocation.getSelectedIndex();
            if (index == -1) {
                if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                    if (cboLocation.getItemCount() > 0) {
                        cboLocation.setSelectedIndex(0);
                        saleTableModel.setLocation((Location) cboLocation.getSelectedItem());
                    }
                }
            }
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

        tmpObj = Util1.getDefaultValue("VouPrinter");
        if (tmpObj != null) {
            chkPrintOption.setSelected((Boolean) tmpObj);
        }
        chkA5.setSelected(Util1.getPropValue("chk.sale.A5").equals("Y"));
    }

    private void genVouNo() {
        try {
            String vouNo = vouEngine.getVouNo();
            txtVouNo.setText(vouNo);
            if (vouNo.equals("-")) {
                log.error("Voucher error : " + txtVouNo.getText() + " @ "
                        + txtSaleDate.getText() + " @ " + vouEngine.getVouInfo());
                JOptionPane.showMessageDialog(Util1.getParent(),
                        "Vou no error. Exit the program and try again.",
                        "Vou No", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            } else {
                List<SaleHis> listSH = dao.findAllHSQL(
                        "select o from SaleHis o where o.saleInvId = '" + txtVouNo.getText() + "'");
                if (listSH != null) {
                    if (!listSH.isEmpty()) {
                        vouEngine.updateVouNo();
                        vouNo = vouEngine.getVouNo();
                        txtVouNo.setText(vouNo);
                        listSH = null;
                        listSH = dao.findAllHSQL(
                                "select o from SaleHis o where o.saleInvId = '" + txtVouNo.getText() + "'");
                        if (listSH != null) {
                            if (!listSH.isEmpty()) {
                                log.error("Duplicate Sale vour error : " + txtVouNo.getText() + " @ "
                                        + txtSaleDate.getText());
                                JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate sale vou no. Exit the program and try again.",
                                        "Sale Vou No", JOptionPane.ERROR_MESSAGE);
                                System.exit(1);
                                //txtVouNo.setText(null);
                            }
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

    private void initCombo() {
        try {
            isBind = true;
            BindingUtil.BindCombo(cboPayment, dao.findAllHSQL(
                    "select o from PaymentType o order by o.paymentTypeName"));
            BindingUtil.BindCombo(cboLocation, getLocationFilter());
            BindingUtil.BindCombo(cboVouStatus, dao.findAll("VouStatus"));
            BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));

            new ComBoBoxAutoComplete(cboPayment, this);
            new ComBoBoxAutoComplete(cboLocation, this);
            new ComBoBoxAutoComplete(cboVouStatus, this);
            new ComBoBoxAutoComplete(cboCurrency, this);
            isBind = false;
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private List getLocationFilter() {
        try {
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                return dao.findAllHSQL(
                        "select o from Location o where o.locationId in ("
                        + "select a.key.locationId from UserLocationMapping a "
                        + "where a.key.userId = '" + Global.loginUser.getUserId()
                        + "' and a.isAllowSale = true) order by o.locationName");
            } else {
                return dao.findAllHSQL("select o from Location o order by o.locationName");
            }
        } catch (Exception ex) {
            log.error("getLocationFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }
        return null;
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "CustomerList":
                try {
                Trader cus = (Trader) selectObj;
                String selCusId = cus.getTraderId();
                //Check overdue
                /*if (Util1.getPropValue("system.overdue.check").equals("Y") && cus != null) {
                    if (isOverDue(cus.getTraderId())) {
                        clear();
                        return;
                    }
                } else if (Util1.getPropValue("system.overdue.check").equals("S") && cus != null) {
                    if (isOverdue((Customer) cus)) {
                        clear();
                        return;
                    }
                }*/
                //Check overdue
                String tmpCusId = Util1.getPropValue("system.default.customer");
                if (Util1.getPropValue("system.overdue.check").equals("Y") && cus != null) {
                    if (isOverDue(cus.getTraderId())) {
                        //clear();
                        txtCusId.setText("");
                        txtCusName.setText("");
                        return;
                    }
                } else if (Util1.getPropValue("system.overdue.check").equals("S") && cus != null) {
                    if (!tmpCusId.equals(selCusId)) {
                        Customer tmpCus = (Customer) dao.find(Customer.class, cus.getTraderId());
                        if (tmpCus.getTraderGroup() == null) {
                            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid Customer Group.",
                                    "Customer Group", JOptionPane.ERROR_MESSAGE);
                            //return;
                        } else {
                            if (tmpCus.getTraderGroup().getGroupId().equals(Util1.getPropValue("system.cus.base.group"))
                                    && !tmpCus.getTraderId().equals(Util1.getPropValue("system.default.customer"))) {
                                if (isBaseGroupOverdue(currSaleVou.getCustomerId())) {
                                    txtCusId.setText("");
                                    txtCusName.setText("");
                                    //clear();
                                    return;
                                }
                            } else if (isOverdue((Customer) tmpCus)) {
                                txtCusId.setText("");
                                txtCusName.setText("");
                                //clear();
                                return;
                            }
                        }
                    }
                }

                currSaleVou.setCustomerId(cus);

                if (cus != null) {
                    txtCusId.setText(cus.getTraderId());
                    txtCusName.setText(cus.getTraderName());

                    if (cus.getTraderGroup() != null) {
                        String group = cus.getTraderGroup().getGroupId();
                        codeCellEditor.setCusGroup(cus.getTraderGroup().getGroupId());
                        if (!group.equals("KS")) {
                            //chkVouComp.setSelected(true);
                            chkPrintOption.setSelected(false);
                        }
                    } else {
                        codeCellEditor.setCusGroup(null);
                    }

                    Customer tmpCus = (Customer) dao.find(Customer.class, cus.getTraderId());
                    if (tmpCus instanceof Customer) {
                        txtCreditLimit.setValue(tmpCus.getCreditLimit());
                        int creditDay = NumberUtil.NZeroInt(tmpCus.getCreditDays());

                        if (creditDay != 0) {
                            txtDueDate.setText(DateUtil.subDateTo(DateUtil.toDate(txtSaleDate.getText()), creditDay));
                            //txtDueDate.setText(DateUtil.addTodayDateTo(creditDay));
                            if (dao.getRowCount("SELECT count(*) FROM sale_his sh where sh.cus_id= '" + tmpCus.getTraderId() + "'") > 0) {
                                if (dao.getRowCount("SELECT count(*) FROM sale_his sh where sh.cus_id= '" + tmpCus.getTraderId() + "' and "
                                        + "sh.due_date < '" + DateUtil.toDateStrMYSQL(txtSaleDate.getText()) + "' "
                                        + "and sh.sale_inv_id not in (select vou_no from payment_vou where vou_type ='SALE')") > 0) {
                                    lblDueRemark.setVisible(true);
                                } else {
                                    lblDueRemark.setVisible(false);
                                }
                            }
                        }
                    } else {
                        txtCreditLimit.setValue(0.0);
                    }

                    if (cus.getTypeId() != null) {
                        saleTableModel.setCusType(cus.getTypeId().getDescription(), "cus");
                    } else {
                        saleTableModel.setCusType("N", "cus");
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
            } catch (Exception ex) {
                log.error("selected CustomerList : " + selectObj.toString() + " - " + ex.getMessage());
            }
            break;
            case "PatientList":
                try {
                txtAdmissionNo.setText(null);
                txtBill.setText(null);
                butOTID.setEnabled(true);
                butAdmit.setEnabled(true);
                Patient ptt = (Patient) selectObj;
                currSaleVou.setPatientId(ptt);
                currSaleVou.setAdmissionNo(ptt.getAdmissionNo());
                if (ptt != null) {
                    if (ptt.getAdmissionNo() != null && !ptt.getAdmissionNo().equals("")) {
                        String priceType = Util1.getPropValue("system.sale.adm.price");
                        if (priceType.isEmpty()) {
                            saleTableModel.setCusType("N", "pt");
                        } else {
                            saleTableModel.setCusType(priceType, "pt");
                        }
                    }
                    txtCusId.setText(ptt.getRegNo());
                    txtCusName.setText(ptt.getPatientName());
                    txtAdmissionNo.setText(ptt.getAdmissionNo());
                    txtCreditLimit.setValue(0.0);
                    calculateTotalAmount();
                    if (ptt.getDoctor() != null) {
                        txtDrCode.setText(ptt.getDoctor().getDoctorId());
                        txtDrName.setText(ptt.getDoctor().getDoctorName());
                        currSaleVou.setDoctor(ptt.getDoctor());
                    } else {
                        txtDrCode.requestFocus();
                    }
                    if (!Util1.isNullOrEmpty(ptt.getAdmissionNo())) {
                        cboPayment.setSelectedItem(ptCredit);
                        butAdmit.setEnabled(false);
                        if (Util1.getPropValue("system.admission.paytype").equals("CASH")) {
                            cboPayment.setSelectedItem(ptCash);
                        }
                    } else if (!Util1.isNullOrEmpty(ptt.getOtId())) {
                        cboPayment.setSelectedItem(ptCredit);
                        butOTID.setEnabled(false);
                        txtBill.setText(ptt.getOtId());
                    } else {
                        cboPayment.setSelectedItem(ptCash);

                    }

                    if (ptt.getOtId() != null) {
                        butOTID.setEnabled(false);
                        txtBill.setText(ptt.getOtId());
                    } else {
                        butOTID.setEnabled(true);
                        txtBill.setText(null);
                    }

                    getPatientBill(ptt.getRegNo());
                    //Booking info
                    String regNo = ptt.getRegNo();
                    List<Booking> listBK = dao.findAllHSQL(
                            "select o from Booking o where o.regNo = '"
                            + regNo + "' and o.bkDate = '" + DateUtil.toDateStrMYSQL(txtSaleDate.getText()) + "'");
                    if (listBK != null) {
                        if (!listBK.isEmpty()) {
                            if (listBK.size() == 1) {
                                Booking bk = listBK.get(0);
                                String visitId = bk.getDoctor().getDoctorId() + "-" + bk.getRegNo() + "-"
                                        + DateUtil.getDatePart(bk.getBkDate(), "ddMMyyyy")
                                        + "-" + bk.getBkSerialNo();
                                currSaleVou.setVisitId(visitId);
                                selected("DoctorSearch", bk.getDoctor());
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
                                    currSaleVou.setVisitId(visitId);
                                    selected("DoctorSearch", selBK.getDoctor());
                                }
                            } else {
                                if (ptt.getDoctor() != null) {
                                    selected("DoctorSearch", ptt.getDoctor());
                                }
                            }
                        }
                    }
                } else {
                    txtCusId.setText(null);
                    txtCusName.setText(null);
                }
            } catch (Exception ex) {
                log.error("selected PatientList : " + selectObj.toString() + " - " + ex.getMessage());
            }
            break;
            case "PatientSearch":
                try {
                Patient patient = (Patient) selectObj;

                currSaleVou.setPatientId(patient);
                if (patient != null) {
                    if (patient.getAdmissionNo() != null && !patient.getAdmissionNo().equals("")) {
                        String priceType = Util1.getPropValue("system.sale.adm.price");
                        if (priceType.isEmpty()) {
                            saleTableModel.setCusType("N", "pt");
                        } else {
                            saleTableModel.setCusType(priceType, "pt");
                        }
                    }
                    txtCusId.setText(patient.getRegNo());
                    txtCusName.setText(patient.getPatientName());
                    if (patient.getDoctor() != null) {
                        txtDrCode.setText(patient.getDoctor().getDoctorId());
                        txtDrName.setText(patient.getDoctor().getDoctorName());
                        currSaleVou.setDoctor(patient.getDoctor());
                    } else {
                        txtDrCode.requestFocus();
                    }
                    txtAdmissionNo.setText(patient.getAdmissionNo());
                    if (Util1.getNullTo(patient.getAdmissionNo(), "").trim().isEmpty()) {
                        butAdmit.setEnabled(true);
                        cboPayment.setSelectedItem(ptCash);
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
            } catch (Exception ex) {
                log.error("selected PatientSearch : " + selectObj.toString() + " - " + ex.getMessage());
            }
            break;
            case "DoctorSearch":
                try {
                Doctor doc = (Doctor) selectObj;

                if (doc != null) {
                    txtDrCode.setText(doc.getDoctorId());
                    txtDrName.setText(doc.getDoctorName());
                    currSaleVou.setDoctor(doc);
                    tblSale.requestFocus();
                }
            } catch (Exception ex) {
                log.error("selected DoctorSearch : " + selectObj.toString() + " - " + ex.getMessage());
            }
            break;
            case "MedicineList":
                Medicine med;

                try {
                    dao.open();

                    if (selectObj instanceof Medicine) {
                        med = (Medicine) dao.find(Medicine.class, ((Medicine) selectObj).getMedId());
                    } else if (selectObj instanceof VMedicine1) {
                        String medId = ((VMedicine1) selectObj).getMedId();
                        med = (Medicine) dao.find(Medicine.class, medId);
                    } else {
                        dao.close();
                        return;
                    }

                    List<RelationGroup> listRel = med.getRelationGroupId();
                    med.setRelationGroupId(listRel);
                    List<Stock> listStock = null;

                    if (!listRel.isEmpty()) {
                        medUp.add(med);
                        if (Util1.getPropValue("system.app.sale.stockBalance").equals("Y")) {
                            stockList.add(med, (Location) cboLocation.getSelectedItem());
                        } else if (Util1.getPropValue("system.app.sale.stockBalance").equals("H")) {
                            stockList.add(med, null);
                            listStock = stockList.getStockList(med.getMedId());
                            if (listStock == null) {
                                listStock = new ArrayList();
                            }
                            stockTableModel.setListStock(listStock);
                        }

                        int selectRow = tblSale.getSelectedRow();
                        if (Util1.getPropValue("system.app.sale.musthavestock").equals("Y")) {
                            if (listStock != null) {
                                if (!listStock.isEmpty()) {
                                    Integer locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
                                    if (isOutOfStock(listStock, locationId)) {
                                        JOptionPane.showMessageDialog(Util1.getParent(),
                                                "No stock to sell (" + med.getMedId() + "-" + med.getMedName() + ")",
                                                "No Stock", JOptionPane.ERROR_MESSAGE);
                                        stockTableModel.setListStock(new ArrayList());
                                    } else {
                                        saleTableModel.setMed(med, selectRow, stockList);
                                        //Calculate total items of voucher
                                        txtTotalItem.setText(Integer.toString((listDetail.size() - 1)));
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(Util1.getParent(),
                                            "No stock to sell (" + med.getMedId() + "-" + med.getMedName() + ")",
                                            "No Stock", JOptionPane.ERROR_MESSAGE);
                                    stockTableModel.setListStock(new ArrayList());
                                }
                            } else {
                                JOptionPane.showMessageDialog(Util1.getParent(),
                                        "No stock to sell (" + med.getMedId() + "-" + med.getMedName() + ")",
                                        "No Stock", JOptionPane.ERROR_MESSAGE);
                                stockTableModel.setListStock(new ArrayList());
                            }
                        } else {
                            saleTableModel.setMed(med, selectRow, stockList);
                            //Calculate total items of voucher
                            txtTotalItem.setText(Integer.toString((listDetail.size() - 1)));
                        }
                    } else {
                        System.out.println("Sale.select MedicineList : Cannot get relation group");
                    }
                } catch (Exception ex) {
                    log.error("selected MedicineList : " + selectObj.toString()
                            + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                } finally {
                    dao.close();
                }
                break;
            case "PurIMEINoList":
                PurchaseIMEINo purchaseIMEINo;
                try {
                    dao.open();
                    purchaseIMEINo = (PurchaseIMEINo) dao.find(PurchaseIMEINo.class, ((PurchaseIMEINo) selectObj).getKey());
                    int selectRow = tblSale.getSelectedRow();
                    saleTableModel.setPurIMEINo(purchaseIMEINo, selectRow);

                } catch (Exception ex) {
                    log.error("selected PurIMEINoList : " + selectObj.toString()
                            + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                } finally {
                    dao.close();
                }
                break;

            case "SaleVouList":
                try {

                clear();
                dao.open();
                if (selectObj instanceof SaleHis) {
                    currSaleVou = (SaleHis) dao.find(SaleHis.class,
                            ((SaleHis) selectObj).getSaleInvId());
                } else {
                    currSaleVou = (SaleHis) dao.find(SaleHis.class,
                            selectObj.toString());
                }

                isBind = true;
                if (Util1.getNullTo(currSaleVou.getDeleted())) {
                    lblStatus.setText("DELETED");
                    lblStatus.setForeground(Color.red);
                } else {
                    lblStatus.setText("EDIT");
                    lblStatus.setForeground(Color.BLACK);
                }

                cboCurrency.setSelectedItem(currSaleVou.getCurrencyId());
                cboLocation.setSelectedItem(currSaleVou.getLocationId());
                cboVouStatus.setSelectedItem(currSaleVou.getVouStatus());
                cboPayment.setSelectedItem(currSaleVou.getPaymentTypeId());

                txtVouNo.setText(currSaleVou.getSaleInvId());
                txtSaleDate.setText(DateUtil.toDateStr(currSaleVou.getSaleDate()));
                saleTableModel.setSaleDate(DateUtil.toDate(txtSaleDate.getText()));
                saleTableModel.setVouStatus("EDIT");
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
                if (currSaleVou.getAdmissionNo() != null) {
                    if (currSaleVou.getAdmissionNo().isEmpty()) {
                        saleTableModel.setCusType("N", "vou");
                    } else {
                        String priceType = Util1.getPropValue("system.sale.adm.price");
                        if (priceType.isEmpty()) {
                            saleTableModel.setCusType("N", "vou");
                        } else {
                            saleTableModel.setCusType(priceType, "vou");
                        }
                    }
                }
                Doctor dr = currSaleVou.getDoctor();
                if (dr != null) {
                    txtDrCode.setText(dr.getDoctorId());
                    txtDrName.setText(dr.getDoctorName());
                } else if (currSaleVou.getPatientId() != null) {
                    Doctor pdr = currSaleVou.getPatientId().getDoctor();
                    if (pdr != null) {
                        txtDrCode.setText(pdr.getDoctorId());
                        txtDrName.setText(pdr.getDoctorName());
                    } else {
                        txtDrCode.setText(null);
                        txtDrName.setText(null);
                    }
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
                        + NumberUtil.NZero(currSaleVou.getExpenseTotal())
                        + NumberUtil.NZero(currSaleVou.getTtlExpenseIn()))
                        - NumberUtil.NZero(currSaleVou.getDiscount()));

                txtVouBalance.setValue((currSaleVou.getVouTotal()
                        + NumberUtil.NZero(currSaleVou.getTaxAmt())
                        + NumberUtil.NZero(currSaleVou.getExpenseTotal()))
                        - (NumberUtil.NZero(currSaleVou.getDiscount())
                        + NumberUtil.NZero(currSaleVou.getPaid())));
                if (!Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                    if (currSaleVou.getCustomerId() != null) {
                        if (currSaleVou.getCustomerId() instanceof Customer) {
                            txtCreditLimit.setValue(((Customer) currSaleVou.getCustomerId()).getCreditLimit());
                        }

                        if (currSaleVou.getCustomerId().getTypeId() != null) {
                            saleTableModel.setCusType(currSaleVou.getCustomerId().getTypeId().getDescription(), "vou");
                        }
                    }

                }

                String vouNo = currSaleVou.getSaleInvId();
                //This statment is for Outstanding lazy loading
                /*if (currSaleVou.getListOuts().size() > 0) {
                        List<SaleOutstand> listOuts = currSaleVou.getListOuts();
                        currSaleVou.setListOuts(listOuts);
                    }*/

                List<SaleOutstand> listOuts = dao.findAllHSQL(
                        "select o from SaleOutstand o where o.vouNo = '"
                        + vouNo + "'");
                currSaleVou.setListOuts(listOuts);
                //=============================================

                //This statment is for Warranty laxy loading
                /*if (currSaleVou.getWarrandy().size() > 0) {
                        List<SaleWarranty> listWarranty = currSaleVou.getWarrandy();
                        currSaleVou.setWarrandy(listWarranty);
                    }*/
                List<SaleWarranty> listWarranty = dao.findAllHSQL(
                        "select o from SaleWarranty o where o.vouNo = '"
                        + vouNo + "'"
                );
                currSaleVou.setWarrandy(listWarranty);
                //=============================================

                /*if (currSaleVou.getExpense().size() > 0) {
                        currSaleVou.setExpense(currSaleVou.getExpense());
                    }*/
                List<SaleExpense> listSaleExpense = dao.findAllHSQL(
                        "select o from SaleExpense o where o.vouNo = '"
                        + vouNo + "'"
                );
                currSaleVou.setExpense(listSaleExpense);

                //listDetail = currSaleVou.getSaleDetailHis();
                listDetail = dao.findAllHSQL(
                        "select o from SaleDetailHis o where o.vouNo = '"
                        + vouNo + "' order by o.uniqueId"
                );
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

                if (currSaleVou.getStuName() != null) {
                    txtCusName.setText(currSaleVou.getStuName());
                }

                double lastBalance = NumberUtil.NZero(txtSaleLastBalance.getValue())
                        + NumberUtil.NZero(txtVouBalance.getValue())
                        + tranTableModel.getTotal();

                txtCusLastBalance.setValue(lastBalance);
                txtDifference.setValue(NumberUtil.NZero(txtCreditLimit.getValue())
                        - NumberUtil.NZero(txtCusLastBalance.getValue()));
                txtTotalItem.setText(String.valueOf(listDetail.size()));

                txtBill.setText(currSaleVou.getOtId());
                //cboPayment.setSelectedItem(ptCredit);
                if (txtBill.getText() == null) {
                    butOTID.setEnabled(true);
                } else if (!txtBill.getText().isEmpty()) {
                    butOTID.setEnabled(false);
                } else {
                    butOTID.setEnabled(true);
                }

                //String group = "-";
                if (currSaleVou.getCustomerId() != null) {
                    String cusId = currSaleVou.getCustomerId().getTraderId();
                    txtCusId.setText(cusId);
                    Customer cus = (Customer) dao.find(Customer.class, cusId);
                    saleTableModel.setCusType(cus.getTypeId().getDescription(), "vou");
                    /*if (cus.getTraderGroup() != null) {
                        group = cus.getTraderGroup().getGroupId();
                    }*/
                }
                /*if (!group.equals("KS")) {
                        chkVouComp.setSelected(true);
                        chkPrintOption.setSelected(false);
                    }*/

                //calculateTotalAmount();
                isBind = false;
            } catch (Exception ex) {
                log.error("selected 1 : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }

            tblSale.requestFocusInWindow();
            setEditStatus(currSaleVou.getSaleInvId());
            saleTableModel.setCanEdit(canEdit);
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
                tran.setUserId(Global.machineId);
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

    private boolean isOutOfStock(List<Stock> listStock, Integer locationId) {
        boolean status = false;
        float ttlStock = 0;
        if (listStock != null) {
            if (!listStock.isEmpty()) {
                ttlStock = listStock.stream()
                        .filter(stk -> (stk.getLocationId().equals(locationId)))
                        .map(stk -> stk.getBalance())
                        .reduce(ttlStock, (accumulator, _item) -> accumulator + _item);
            }
        }

        if (ttlStock < 1) {
            status = true;
        }

        return status;
    }

    private void initSaleTable() {
        try {
            if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
                tblSale.setCellSelectionEnabled(true);
            }
            tblSale.getTableHeader().setReorderingAllowed(false);
            tblSale.getTableHeader().setFont(Global.lableFont);
            tblSale.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
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
            tblSale.getColumnModel().getColumn(11).setPreferredWidth(70);//Location
            tblSale.getColumnModel().getColumn(12).setPreferredWidth(70);//STK-Balance
            //tblSale.getColumnModel().getColumn(11).setMinWidth(0);
            //tblSale.getColumnModel().getColumn(11).setMaxWidth(0);
            //tblSale.getColumnModel().getColumn(11).setWidth(0);
            addSaleTableModelListener();

            //Change JTable cell editor
            /*tblSale.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));*/
            tblSale.getColumnModel().getColumn(0).setCellEditor(codeCellEditor);
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
            tblSale.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
                txtRecNo.setText(Integer.toString(tblSale.getSelectedRow() + 1));
                if (tblSale.getSelectedRow() < saleTableModel.getRowCount()) {
                    int row = tblSale.convertRowIndexToModel(tblSale.getSelectedRow());
                    lblBrandName.setText(saleTableModel.getBrandName(row));
                    lblRemark.setText(saleTableModel.getRemark(row));
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
            });

            tblPatientBill.getColumnModel().getColumn(0).setPreferredWidth(180);//Bill Name
            tblPatientBill.getColumnModel().getColumn(1).setPreferredWidth(70);//Amount
        } catch (Exception ex) {
            log.error("initSaleTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void addSaleTableModelListener() {
        tblSale.getModel().addTableModelListener((TableModelEvent e) -> {
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
        PurchaseIMEINo purimeino;
        ResultSet rs;

        try {

            if (Util1.getPropValue("system.check.imei").equals("Y")) {
                String strSQL = "select * from v_sale where deleted='false' and ( imei1='" + medCode
                        + "' or imei2='" + medCode + "' or sd_no ='" + medCode + "')";
                rs = dao.execSQL(strSQL);

                if (rs.next()) {
                    JOptionPane.showMessageDialog(Util1.getParent(), "This Item is already saled.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

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
                        String strSql = "select distinct o from Medicine o join o.relationGroupId r "
                                + "where r.unitBarcode = '" + medCode + "'";
                        List<Medicine> listMed = dao.findAllHSQL(strSql);
                        if (listMed != null) {
                            if (!listMed.isEmpty()) {
                                medicine = listMed.get(0);
                                if (!medicine.getRelationGroupId().isEmpty()) {
                                    List<RelationGroup> listRG = medicine.getRelationGroupId();
                                    medicine.setRelationGroupId(listRG);

                                    for (int i = 0; i < listRG.size(); i++) {
                                        RelationGroup rg = listRG.get(i);
                                        if (rg.getUnitBarcode() != null) {
                                            if (rg.getUnitBarcode().equals(medCode)) {
                                                saleTableModel.setStrBarcodeUnit(rg.getUnitId().getItemUnitCode());
                                                i = listRG.size();
                                            }
                                        }
                                    }
                                }
                                selected("MedicineList", medicine);
                            } else {
                                strSql = "select distinct o from Medicine o join o.relationGroupId r "
                                        + "where concat(o.medId,r.relUniqueId) = '" + medCode + "'";
                                listMed = dao.findAllHSQL(strSql);

                                if (listMed != null) {
                                    if (!listMed.isEmpty()) {
                                        medicine = listMed.get(0);
                                        if (medicine.getRelationGroupId().size() > 0) {
                                            List<RelationGroup> listRG = medicine.getRelationGroupId();
                                            medicine.setRelationGroupId(listRG);

                                            for (int i = 0; i < listRG.size(); i++) {
                                                RelationGroup rg = listRG.get(i);
                                                String key = medicine.getMedId() + rg.getRelUniqueId().toString();
                                                if (key.equals(medCode)) {
                                                    saleTableModel.setStrBarcodeUnit(rg.getUnitId().getItemUnitCode());
                                                    i = listRG.size();
                                                }
                                            }
                                        }
                                        selected("MedicineList", medicine);
                                    } else {
                                        purimeino = (PurchaseIMEINo) dao.find("PurchaseIMEINo", "imei1 = '"
                                                + medCode + "' or imei2 = '" + medCode + "'");

                                        if (purimeino != null) {
                                            selected("PurIMEINoList", purimeino);
                                        } else {
                                            /*JOptionPane.showMessageDialog(Util1.getParent(), "Invalid Item code.",
                                                    "Invalid.", JOptionPane.ERROR_MESSAGE);*/
 /*try {
                                                if (tblSale.getCellEditor() != null) {
                                                    tblSale.getCellEditor().stopCellEditing();
                                                }
                                            } catch (Exception ex) {

                                            }*/
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                log.info("getMedInfo : Blank medicine code.");
            }

        } catch (Exception e) {
            log.error("getMedInfo : " + e.getMessage());
        }

    }

    private void getMedList(String filter) {
        int locationId = -1;
        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }
        String cusGroup = "-";
        if (currSaleVou.getCustomerId() != null) {
            if (currSaleVou.getCustomerId().getTraderGroup() != null) {
                cusGroup = currSaleVou.getCustomerId().getTraderGroup().getGroupId();
            }
        }
        MedListDialog1 dialog = new MedListDialog1(dao, filter, locationId, cusGroup);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
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

                        SaleDetailHis sdh = listDetail.get(rowIndex);
                        if (sdh != null) {
                            if (sdh.getUnitId() != null) {
                                String unit = sdh.getUnitId().getItemUnitCode();
                                MedPriceAutoCompleter completer = new MedPriceAutoCompleter(jtf,
                                        medUp.getPriceList(medId + "-" + unit), this);
                            }
                        }
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
            if (anEvent instanceof MouseEvent) {
                return false;
            } else {
                return true;
            }
        }
    }

    private void calculateTotalAmount() {

        double totalAmount = 0;
        double totalExp = 0;
        double totalExpIn = 0;

        totalAmount = saleTableModel.getTotalAmount();
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
                    //For Royal Myeik
                    if (Util1.getPropValue("system.admission.cash.not.showrpt").equals("Y")) {
                        txtAdmissionNo.setText(null);
                    }
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
        formActionKeyMapping(chkAmount);
        formActionKeyMapping(chkPrintOption);
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
            /*try {
            //if (tblSale.getCellEditor() != null) {
            //tblSale.getCellEditor().stopCellEditing();
            //}
            
            //No entering medCode, only press F3
            try {
            dao.open();
            //getMedInfo("");
            getMedList("");
            dao.close();
            } catch (Exception ex1) {
            log.error("actionMedList : " + ex1.getStackTrace()[0].getLineNumber() + " - " + ex1.toString());
            }
            } catch (Exception ex) {
            
            }*/
        }
    };

    private Action actionTblSaleEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblSale.getCellEditor() != null) {
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
                if (tblExpense.getCellEditor() != null) {
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
            clear();
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
            SaleDetailHis sdh;
            int yes_no = -1;

            if (!Util1.hashPrivilege("SaleEditVoucherChange") && lblStatus.getText().equals("EDIT")
                    && !canEdit) {

            } else if (tblSale.getSelectedRow() >= 0) {
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

                        if (tblSale.getCellEditor() != null) {
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

            if (!Util1.hashPrivilege("SaleEditVoucherChange") && lblStatus.getText().equals("EDIT")) {

            } else if (tblExpense.getSelectedRow() >= 0) {
                se = listExpense.get(tblExpense.getSelectedRow());

                if (se.getExpType().getExpenseId() != null) {
                    try {
                        yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                                "Expense item delete", JOptionPane.YES_NO_OPTION);

                        if (tblExpense.getCellEditor() != null) {
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
            if (!Util1.hashPrivilege("SaleEditVoucherChange") && lblStatus.getText().equals("EDIT")) {

            } else {
                tranTableModel.delete(tblTransaction.getSelectedRow());
                calculateTotalAmount();
            }
        }
    };

    private Action actionPriceChange = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            PriceChangeDialog dialogPC = new PriceChangeDialog();
            dialogPC.addPanel(saleTableModel.getMedIdList());
            dialogPC.setLocationRelativeTo(null);
            dialogPC.setVisible(true);

            //Load updated price
            try {
                List<SaleDetailHis> tmpList = currSaleVou.getSaleDetailHis();
                for (SaleDetailHis sdh : tmpList) {
                    if (sdh.getMedId() != null) {
                        if (sdh.getMedId().getMedId() != null) {
                            Medicine med = (Medicine) dao.find(Medicine.class, sdh.getMedId().getMedId());
                            if (med.getRelationGroupId().size() > 0) {
                                med.setRelationGroupId(med.getRelationGroupId());
                            }
                            medUp.add(med);
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("actionPriceChange : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    };

    public void setFocus() {
        //txtCusId.requestFocusInWindow();
        txtCusId.requestFocus();
    }

    private void clear() {
        try {
            if (tblSale.getCellEditor() != null) {
                tblSale.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        if (lblStatus.getText().equals("NEW")) {
            strPrvDate = txtSaleDate.getText();
            prvLocation = cboLocation.getSelectedItem();
            prvPymet = cboPayment.getSelectedItem();
        }
        canEdit = true;
        codeCellEditor.setCusGroup(null);
        saleTableModel.setVouStatus("NEW");
        saleTableModel.setCanEdit(canEdit);
        saleTableModel.clear();
        isDeleteCopy = false;
        haveTransaction = false;
        //Clear text box.
        txtVouNo.setText("");
        txtSaleDate.setText("");
        txtDueDate.setText("");
        txtCusId.setText("");
        txtCusId.setEditable(true);
        txtCusName.setText("");
        txtCusName.setEnabled(true);
        txtDrCode.setText("");
        txtDrCode.setEnabled(true);
        txtDrName.setText("");
        txtDrName.setEnabled(true);
        txtRemark.setText("");
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.BLACK);
        txtCreditLimit.setValue(0.0);
        saleTableModel.setCusType1("N");
        lblSaleLastBal.setText("Balance : ");
        tranTableModel.clear();
        lblDueRemark.setVisible(false);
        lblDate.setText("");
        lblTranOption.setText("");
        lblRemark.setText(null);
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
        if (Util1.getPropValue("system.sale.barcode").equals("Y")) {
            tblSale.requestFocus();
        }
        chkVouComp.setSelected(false);
        butOTID.setEnabled(false);
        txtBill.setText(null);
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
        try {
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
        } catch (Exception ex) {
            log.error("initExpenseTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
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

    private void save1() {
        Date vouSaleDate = DateUtil.toDate(txtSaleDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return;
        }

        log.error("save1() start : " + currSaleVou.getSaleInvId());
        //For BK Pagolay
        try {
            Date d = new Date();
            dao.execProc("bksale",
                    currSaleVou.getSaleInvId(),
                    DateUtil.toDateTimeStrMYSQL(d),
                    Global.loginUser.getUserId(),
                    Global.machineId,
                    currSaleVou.getVouTotal().toString(),
                    currSaleVou.getDiscount().toString(),
                    currSaleVou.getPaid().toString(),
                    currSaleVou.getBalance().toString());
        } catch (Exception ex) {
            log.error("bksale : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
        log.error("save1() after bksale : " + currSaleVou.getSaleInvId());
        //For BK Pagolay

        try {
            for (SaleDetailHis sdh : listDetail) {
                if (sdh.getMedId() != null) {
                    if (sdh.getMedId().getTypeOption() != null) {
                        if (sdh.getMedId().getTypeOption().equals("PACKING")) {
                            dao.execProc("insert_packing", currSaleVou.getSaleInvId(),
                                    sdh.getMedId().getMedId(),
                                    sdh.getUniqueId().toString(), "Sale",
                                    sdh.getQuantity().toString());
                        }
                    }

                }

            }
        } catch (Exception ex) {
            log.error("insert packing : " + " - " + currSaleVou.getSaleInvId() + " - " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
        } finally {
            dao.close();
        }
        log.error("save1() after insert_packing : " + currSaleVou.getSaleInvId());
        try {
            boolean iAllow = true;
            if (iAllow == true) {
                String vouNo = currSaleVou.getSaleInvId();
                List<SaleDetailHis> tmpListSDH = saleTableModel.getListDetail();
                dao.open();
                dao.beginTran();
                int ttlItem = 0;
                int fttlItem = NumberUtil.NZeroInt(txtTotalItem.getText());
                if (tmpListSDH != null) {
                    for (SaleDetailHis sdh : tmpListSDH) {
                        sdh.setVouNo(vouNo);
                        if (sdh.getSaleDetailId() == null) {
                            sdh.setSaleDetailId(vouNo + "-" + sdh.getUniqueId().toString());
                        }
                        dao.save1(sdh);
                        ttlItem++;
                    }
                }
                log.error("save1() after save sale detail his : " + currSaleVou.getSaleInvId() + " - " + tmpListSDH.size());
                if (ttlItem != fttlItem) {
                    log.error("Error in total item, Vou No : " + currSaleVou.getSaleInvId()
                            + " List Items Total : " + fttlItem + " Save Items Total : " + ttlItem);
                }
                //currSaleVou.setSaleDetailHis(tmpListSDH);
                listExpense = expTableModel.getListExpense();
                if (listExpense != null) {
                    for (SaleExpense se : listExpense) {
                        if (se.getExpType() != null) {
                            if (se.getVouNo() == null) {
                                se.setVouNo(vouNo);
                                se.setSaleExpenseId(vouNo + "-" + se.getUniqueId().toString());
                            }
                            dao.save1(se);
                        }
                    }
                }
                //currSaleVou.setExpense(listExpense);
                log.error("save1() after sale expense save : " + currSaleVou.getSaleInvId());
                List<SaleOutstand> listOuts = getOutstandingItem();
                if (listOuts != null) {
                    for (SaleOutstand so : listOuts) {
                        if (so.getVouNo() == null) {
                            so.setVouNo(vouNo);
                            so.setOutsId(vouNo + "-" + so.getItemId().getMedId());
                        }
                        dao.save1(so);
                    }
                }
                //currSaleVou.setListOuts(listOuts);
                log.error("save1() after sale outstanding save : " + currSaleVou.getSaleInvId());
                List<SaleWarranty> listWarranty = currSaleVou.getWarrandy();
                if (listWarranty != null) {
                    for (SaleWarranty sw : listWarranty) {
                        sw.setVouNo(vouNo);
                        dao.save1(sw);
                    }
                }
                log.error("save1() after sale warranty save : " + currSaleVou.getSaleInvId());
                dao.save1(currSaleVou);
                dao.commit();
                //dao.save(currSaleVou);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }
                log.error("save1() after voucher update : " + currSaleVou.getSaleInvId());
                deleteDetail();
                log.error("save1() after deleteDetail : " + currSaleVou.getSaleInvId());
                updateVouTotal(currSaleVou.getSaleInvId());
                if (!Util1.getPropValue("system.app.usage.type").equals("School")
                        || !Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                    updatePayment();
                }

                //Upload to Account
                uploadToAccount(currSaleVou.getSaleInvId());
                log.error("save1() after uploadToAccount : " + currSaleVou.getSaleInvId());
                clear();

            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Need Permission",
                        "Sale Save", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            dao.rollBack();
            log.error("save : " + currSaleVou.getSaleInvId() + " - " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }

        log.error("save1() end : " + currSaleVou.getSaleInvId());
    }

    @Override
    public void save() {
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot save edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isValidEntry() && saleTableModel.isValidEntry() && expTableModel.isValidEntry()) {
            log.info("Sale Date" + currSaleVou.getSaleDate().toString());
            Date vouSaleDate = DateUtil.toDate(txtSaleDate.getText());
            Date lockDate = PharmacyUtil.getLockDate(dao);
            if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                        + DateUtil.toDateStr(lockDate) + ".",
                        "Locked Data", JOptionPane.ERROR_MESSAGE);
                return;
            }
            boolean status = false;

            if (chkAmount.isSelected()) {
                SaleConfirmDialog1 dialog = new SaleConfirmDialog1(currSaleVou,
                        NumberUtil.NZero(txtSaleLastBalance.getValue()), dao,
                        "Confirm");
                status = dialog.getConfStatus();
            }

            if (status || !chkAmount.isSelected()) {
                //removeEmptyRow();
                //currSaleVou.setSaleDetailHis(listDetail);

                log.error("save() start : " + currSaleVou.getSaleInvId());
                //For BK Pagolay
                try {
                    Date d = new Date();
                    dao.execProc("bksale",
                            currSaleVou.getSaleInvId(),
                            DateUtil.toDateTimeStrMYSQL(d),
                            Global.loginUser.getUserId(),
                            Global.machineId,
                            currSaleVou.getVouTotal().toString(),
                            currSaleVou.getDiscount().toString(),
                            currSaleVou.getPaid().toString(),
                            currSaleVou.getBalance().toString());
                } catch (Exception ex) {
                    log.error("bksale : " + ex.getStackTrace()[0].getLineNumber() + "-"
                            + currSaleVou.getSaleInvId() + " - " + ex);
                } finally {
                    dao.close();
                }
                //For BK Pagolay
                log.error("save() after bksale : " + currSaleVou.getSaleInvId());

                try {
                    for (SaleDetailHis sdh : listDetail) {
                        if (sdh.getMedId() != null) {
                            if (sdh.getMedId().getTypeOption() != null) {
                                if (sdh.getMedId().getTypeOption().equals("PACKING")) {
                                    dao.execProc("insert_packing", currSaleVou.getSaleInvId(),
                                            sdh.getMedId().getMedId(),
                                            sdh.getUniqueId().toString(), "Sale",
                                            sdh.getQuantity().toString());
                                }
                            }

                        }

                    }
                } catch (Exception ex) {
                    log.error("insert packing : " + currSaleVou.getSaleInvId() + " - " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                } finally {
                    dao.close();
                }
                log.error("save() sfter insert_packing : " + currSaleVou.getSaleInvId());
                try {
                    boolean iAllow = true;
                    if (lblStatus.getText().equals("EDIT")) {
                        if (!Util1.hashPrivilege("SaleEditVoucherChange")) {
                            iAllow = false;
                        }
                    }
                    if (iAllow == true) {
                        String vouNo = currSaleVou.getSaleInvId();
                        dao.open();
                        dao.beginTran();
                        List<SaleDetailHis> tmpListSDH = saleTableModel.getListDetail();
                        int ttlItem = 0;
                        int fttlItem = NumberUtil.NZeroInt(txtTotalItem.getText());
                        if (tmpListSDH != null) {
                            for (SaleDetailHis sdh : tmpListSDH) {
                                sdh.setVouNo(vouNo);
                                if (sdh.getSaleDetailId() == null) {
                                    sdh.setSaleDetailId(vouNo + "-" + sdh.getUniqueId().toString());
                                }
                                dao.save1(sdh);
                                ttlItem++;
                            }
                        }
                        log.error("save() after sale detail his save : " + currSaleVou.getSaleInvId() + " - " + tmpListSDH.size());
                        if (ttlItem != (fttlItem - 1)) {
                            log.error("Error in total item, Vou No : " + currSaleVou.getSaleInvId()
                                    + " List Items Total : " + fttlItem + " Save Items Total : " + ttlItem);
                        }
                        //currSaleVou.setSaleDetailHis(tmpListSDH);
                        listExpense = expTableModel.getListExpense();
                        if (listExpense != null) {
                            for (SaleExpense se : listExpense) {
                                if (se.getExpType() != null) {
                                    se.setVouNo(vouNo);
                                    se.setSaleExpenseId(vouNo + "-" + se.getUniqueId().toString());
                                    dao.save1(se);
                                }
                            }
                        }
                        //currSaleVou.setExpense(listExpense);
                        log.error("save() after sale expense save : " + currSaleVou.getSaleInvId());
                        List<SaleOutstand> listOuts = getOutstandingItem();
                        if (listOuts != null) {
                            for (SaleOutstand so : listOuts) {
                                so.setVouNo(vouNo);
                                so.setOutsId(vouNo + "-" + so.getItemId().getMedId());
                                dao.save1(so);
                            }
                        }
                        //currSaleVou.setListOuts(listOuts);
                        log.error("save() after outstanding save : " + currSaleVou.getSaleInvId());
                        List<SaleWarranty> listWarranty = currSaleVou.getWarrandy();
                        if (listWarranty != null) {
                            for (SaleWarranty sw : listWarranty) {
                                sw.setVouNo(vouNo);
                                dao.save1(sw);
                            }
                        }
                        log.error("save() after sale warranty save : " + currSaleVou.getSaleInvId());
                        dao.save1(currSaleVou);
                        dao.commit();
                        if (lblStatus.getText().equals("NEW")) {
                            vouEngine.updateVouNo();
                        }
                        deleteDetail();
                        log.error("save() after delete detail : " + currSaleVou.getSaleInvId());
                        updateVouTotal(currSaleVou.getSaleInvId());
                        if (!Util1.getPropValue("system.app.usage.type").equals("School")
                                || !Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                            updatePayment();
                        }

                        //Upload to Account
                        uploadToAccount(currSaleVou.getSaleInvId());

                        clear();

                    } else {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Need Permission",
                                "Sale Save", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception ex) {
                    dao.rollBack();
                    log.error("save : vou no : " + currSaleVou.getSaleInvId() + " : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    JOptionPane.showMessageDialog(Util1.getParent(), "Error : " + ex.toString(),
                            "Sale Save", JOptionPane.ERROR_MESSAGE);
                } finally {
                    dao.close();
                }

                log.error("save() finished : " + currSaleVou.getSaleInvId());
            }
        }
    }

    @Override
    public void newForm() {
        if (Util1.getPropValue("system.sale.newvou.conform").equals("Y")) {
            int status = JOptionPane.showConfirmDialog(this, "Are you sure to want new voucher?", "Message",
                    JOptionPane.CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (status == 0) {
                clear();
            }
        } else {
            clear();
        }
    }

    @Override
    public void history() {
        int locationId = -1;
        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Sale Voucher Search", dao, locationId);
        dialog.setPreferredSize(new Dimension(1200, 800));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void delete() {
        Date vouSaleDate = DateUtil.toDate(txtSaleDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot delete.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (Util1.getNullTo(currSaleVou.getDeleted())) {
            JOptionPane.showConfirmDialog(Util1.getParent(), "Voucher already deleted.",
                    "Sale voucher delete", JOptionPane.ERROR);
        } else if (Util1.hashPrivilege("SaleDelete")) {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "Sale voucher delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                currSaleVou.setDeleted(true);
                //save();
                //save1();
                try {
                    Date d = new Date();
                    dao.execProc("bksale",
                            currSaleVou.getSaleInvId(),
                            DateUtil.toDateTimeStrMYSQL(d),
                            Global.loginUser.getUserId(),
                            Global.machineId,
                            currSaleVou.getVouTotal().toString(),
                            currSaleVou.getDiscount().toString(),
                            currSaleVou.getPaid().toString(),
                            currSaleVou.getBalance().toString());
                } catch (Exception ex) {
                    log.error("bksale : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                } finally {
                    dao.close();
                }

                String vouNo = currSaleVou.getSaleInvId();
                try {
                    dao.execSql("update sale_his set deleted = true, intg_upd_status = null where sale_inv_id = '" + vouNo + "'");
                } catch (Exception ex) {
                    log.error("delete error : " + ex.getMessage());
                } finally {
                    dao.close();
                }
                //Upload to Account
                uploadToAccount(currSaleVou.getSaleInvId());
                log.error("save1() after uploadToAccount : " + currSaleVou.getSaleInvId());
                if (!Util1.getPropValue("system.app.usage.type").equals("School")
                        || !Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                    if (currSaleVou.getCustomerId() != null) {

                        String traderId = currSaleVou.getCustomerId().getTraderId();
                        updatePayent(vouNo, traderId);
                    }
                }
                clear();
            }
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "You have no permission to delete voucher.",
                    "Sale voucher delete", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void deleteCopy() {
        Date vouSaleDate = DateUtil.toDate(txtSaleDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot delete and copy.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (Util1.hashPrivilege("SaleDeleteCopy")) {
            System.out.println("Delete Copy");
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete and copy?",
                    "Sale voucher delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                isDeleteCopy = true;
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
                                Global.loginUser.getUserId(),
                                Global.machineId,
                                currSaleVou.getVouTotal().toString(),
                                currSaleVou.getDiscount().toString(),
                                currSaleVou.getPaid().toString(),
                                currSaleVou.getBalance().toString());
                    } catch (Exception ex) {
                        log.error("bksale : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                    } finally {
                        dao.close();
                    }

                    try {
                        dao.save(currSaleVou);

                        //Upload to Account
                        uploadToAccount(currSaleVou.getSaleInvId());

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
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "You have no permission to delete voucher.",
                    "Sale voucher delete & copy", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void copyVoucher(String vouNo) {
        try {
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

            //List<SaleDetailHis> listSdh = tmpSaleHis.getSaleDetailHis();
            List<SaleDetailHis> listSdh = dao.findAllHSQL(
                    "select o from SaleDetailHis o where o.vouNo = '"
                    + vouNo + "' order by o.uniqueId"
            );

            //List<SaleExpense> listSe = tmpSaleHis.getExpense();
            List<SaleExpense> listSe = dao.findAllHSQL(
                    "select o from SaleExpense o where o.vouNo = '"
                    + vouNo + "'"
            );

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
            //List<SaleOutstand> tmpListOuts = currSaleVou.getListOuts();
            List<SaleOutstand> tmpListOuts = dao.findAllHSQL(
                    "select o from SaleOutstand o where o.vouNo = '"
                    + vouNo + "'");
            for (SaleOutstand so : tmpListOuts) {
                SaleOutstand tmpSo = new SaleOutstand();
                BeanUtils.copyProperties(so, tmpSo);
                tmpSo.setOutsId(null);
                listOuts.add(tmpSo);
            }

            List<SaleWarranty> listWarrandy = new ArrayList();
            //List<SaleWarranty> tmpListW = currSaleVou.getWarrandy();
            List<SaleWarranty> tmpListW = dao.findAllHSQL(
                    "select o from SaleWarranty o where o.vouNo = '"
                    + vouNo + "'"
            );
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
        } catch (Exception ex) {
            log.error("copyVoucher : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    @Override
    public void print() {
        if (isValidEntry() && saleTableModel.isValidEntry() && expTableModel.isValidEntry()) {
            Date vouSaleDate = DateUtil.toDate(txtSaleDate.getText());
            Date lockDate = PharmacyUtil.getLockDate(dao);
            boolean isDataLock = false;
            if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                isDataLock = true;
            }

            boolean status = false;
            boolean iAllow = true;

            if (lblStatus.getText().equals("EDIT")) {
                if (!Util1.hashPrivilege("SaleEditVoucherChange")) {
                    iAllow = false;
                }
            }
            if (iAllow == true) {
                if (chkAmount.isSelected()) {
                    SaleConfirmDialog1 dialog = new SaleConfirmDialog1(currSaleVou,
                            NumberUtil.NZero(txtSaleLastBalance.getValue()), dao,
                            "Print");
                    status = dialog.getConfStatus();
                }

                if (status || !chkAmount.isSelected()) {
                    //removeEmptyRow();
                    //currSaleVou.setSaleDetailHis(listDetail);
                    //currSaleVou.setSaleDetailHis(saleTableModel.getListDetail());
                    //currSaleVou.setExpense(listExpense);

                    log.error("print() start : " + currSaleVou.getSaleInvId());
                    //For BK Pagolay
                    try {
                        Date d = new Date();
                        dao.execProc("bksale",
                                currSaleVou.getSaleInvId(),
                                DateUtil.toDateTimeStrMYSQL(d),
                                Global.loginUser.getUserId(),
                                Global.machineId,
                                currSaleVou.getVouTotal().toString(),
                                currSaleVou.getDiscount().toString(),
                                currSaleVou.getPaid().toString(),
                                currSaleVou.getBalance().toString());
                    } catch (Exception ex) {
                        log.error("bksale : " + currSaleVou.getSaleInvId() + " - " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                    } finally {
                        dao.close();
                    }
                    log.error("print() after bksale : " + currSaleVou.getSaleInvId());
                    /*txtCusLastBalance.setValue(
                            (NumberUtil.getDouble(txtCusLastBalance.getText())
                            + NumberUtil.getDouble(currSaleVou.getBalance()))
                            + tranTableModel.getTotal()
                    );*/
                    try {
                        if (!isDataLock) {
                            if (canEdit) {
                                String vouNo = currSaleVou.getSaleInvId();
                                List<SaleDetailHis> tmpListSDH = saleTableModel.getListDetail();
                                //dao.open();
                                //dao.beginTran();
                                int ttlItem = 0;
                                int fttlItem = NumberUtil.NZeroInt(txtTotalItem.getText());
                                if (tmpListSDH != null) {
                                    for (SaleDetailHis sdh : tmpListSDH) {
                                        sdh.setVouNo(vouNo);
                                        if (sdh.getSaleDetailId() == null) {
                                            sdh.setSaleDetailId(vouNo + "-" + sdh.getUniqueId().toString());
                                        }
                                        dao.save(sdh);
                                        ttlItem++;
                                    }
                                }
                                log.error("print() after save sale detail his : " + currSaleVou.getSaleInvId() + " - " + tmpListSDH.size());
                                if (ttlItem != (fttlItem - 1)) {
                                    log.error("Error in total item, Vou No : " + currSaleVou.getSaleInvId()
                                            + " List Items Total : " + fttlItem + " Save Items Total : " + ttlItem);
                                }
                                //currSaleVou.setSaleDetailHis(tmpListSDH);

                                listExpense = expTableModel.getListExpense();
                                if (listExpense != null) {
                                    for (SaleExpense se : listExpense) {
                                        if (se.getExpType() != null) {
                                            if (se.getVouNo() == null) {
                                                se.setVouNo(vouNo);
                                                se.setSaleExpenseId(vouNo + "-" + se.getUniqueId().toString());
                                            }
                                            dao.save(se);
                                        }
                                    }
                                }
                                //currSaleVou.setExpense(listExpense);
                                log.error("print() after sale expense save : " + currSaleVou.getSaleInvId());
                                List<SaleOutstand> listOuts = getOutstandingItem();
                                if (listOuts != null) {
                                    for (SaleOutstand so : listOuts) {
                                        if (so.getVouNo() == null) {
                                            so.setVouNo(vouNo);
                                            so.setOutsId(vouNo + "-" + so.getItemId().getMedId());
                                        }
                                        dao.save(so);
                                    }
                                }
                                //currSaleVou.setListOuts(listOuts);
                                log.error("print() after SaleOutstand save : " + currSaleVou.getSaleInvId());
                                List<SaleWarranty> listWarranty = currSaleVou.getWarrandy();
                                if (listWarranty != null) {
                                    for (SaleWarranty sw : listWarranty) {
                                        sw.setVouNo(vouNo);
                                        dao.save(sw);
                                    }
                                }
                                log.error("print() after SaleWarrany save : " + currSaleVou.getSaleInvId());
                                dao.save(currSaleVou);
                                //dao.commit();
                                //dao.save(currSaleVou);
                                if (lblStatus.getText().equals("NEW")) {
                                    vouEngine.updateVouNo();
                                }
                                updatePayment();
                                deleteDetail();
                                updateVouTotal(currSaleVou.getSaleInvId());
                                //Upload to Account
                                uploadToAccount(currSaleVou.getSaleInvId());
                                log.error("print() after uploadToAccount : " + currSaleVou.getSaleInvId());
                            }
                        }
                        String traderId = Util1.getPropValue("system.sale.general.customer");
                        if (currSaleVou.getCustomerId() != null) {
                            if (!currSaleVou.getCustomerId().getTraderId().equals(traderId)) {
                                TraderTransaction tt = new TraderTransaction();
                                tt.setAmount(
                                        NumberUtil.getDouble(txtSaleLastBalance.getText())
                                        + NumberUtil.getDouble(currSaleVou.getBalance())
                                        + tranTableModel.getTotal()
                                );
                                tt.setUserId(Global.machineId);
                                //tt.setTranOption("Last Balance : ");
                                if (tt.getAmount() < 0) {
                                    tt.setTranOption(Util1.getPropValue("system.app.sale.lastbalanceM"));
                                } else {
                                    tt.setTranOption(Util1.getPropValue("system.app.sale.lastbalance"));
                                }
                                tt.setTranDate(currSaleVou.getSaleDate());
                                tt.setTranType("N");
                                tt.setSortId(3);
                                tt.setMachineId(Global.machineId);
                                dao.save(tt);

                                //For Expense
                                //List<SaleExpense> listExpense = expTableModel.getListExpense();
                                if (listExpense != null) {
                                    if (!listExpense.isEmpty()) {
                                        for (SaleExpense se : listExpense) {
                                            tt = new TraderTransaction();
                                            tt.setAmount(se.getExpAmount());
                                            tt.setUserId(Global.machineId);
                                            //tt.setTranOption("Current Vou : ");
                                            tt.setTranOption(se.getExpType().getExpenseName());
                                            tt.setTranDate(currSaleVou.getSaleDate());
                                            tt.setTranType("N");
                                            tt.setSortId(2);
                                            tt.setMachineId(Global.machineId);

                                            dao.save(tt);
                                        }
                                    }
                                }
                                /*tt = new TraderTransaction();
                                tt.setAmount(currSaleVou.getBalance());
                                tt.setUserId(Global.loginUser.getUserId());
                                //tt.setTranOption("Current Vou : ");
                                tt.setTranOption(Util1.getPropValue("system.app.sale.currvoubal"));
                                tt.setTranDate(currSaleVou.getSaleDate());
                                tt.setTranType("N");
                                tt.setSortId(2);
                                tt.setMachineId(Global.machineId);

                                dao.save(tt);*/

                                tt = new TraderTransaction();
                                tt.setAmount(NumberUtil.NZero(txtSaleLastBalance.getText()));
                                tt.setUserId(Global.machineId);
                                //tt.setTranOption("Pre. Balance : ");
                                tt.setTranOption(Util1.getPropValue("system.app.sale.prvbalance"));
                                tt.setTranDate(DateUtil.toDate(strLastSaleDate));
                                tt.setTranType("N");
                                tt.setSortId(1);
                                tt.setMachineId(Global.machineId);

                                dao.save(tt);
                            }
                        }
                    } catch (Exception ex) {
                        //dao.rollBack();
                        log.error("print : " + currSaleVou.getSaleInvId() + " : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                        JOptionPane.showMessageDialog(Util1.getParent(), "Error cannot print.",
                                "Sale print", JOptionPane.ERROR_MESSAGE);
                        return;
                    } finally {
                        dao.close();
                    }
                }
            }/* else {
             JOptionPane.showMessageDialog(Util1.getParent(), "Need Permission",
             "Sale print", JOptionPane.ERROR_MESSAGE);
             }*/

            log.error("print() end database update : " + currSaleVou.getSaleInvId());

            String reportNameProp;
            if (chkPrintOption.isSelected()) {
                reportNameProp = "report.file.saleV";
            } else if (chkVouComp.isSelected()) {
                reportNameProp = "report.file.comp";
            } else {
                reportNameProp = "report.file.saleW";
            }

            String reportName = Util1.getPropValue(reportNameProp);
            String propValue = Util1.getPropValue("repor.choose");
            if (propValue.equals("Y")) {
                /*ReportChooseDialog diag = new ReportChooseDialog();
                 diag.setLocationRelativeTo(null);
                 diag.setVisible(true);

                 if (diag.getrName().equals("Alpha")) {
                 reportName = "saleVouDotMImageSHIFAAlpha";
                 }*/
                Trader cus = (Trader) currSaleVou.getCustomerId();
                if (cus.getTraderGroup() != null) {
                    String tmpReportName = cus.getTraderGroup().getReportName();
                    if (tmpReportName != null) {
                        if (!tmpReportName.isEmpty()) {
                            reportName = tmpReportName;
                        }
                    }
                }
            }
            String printerName = Util1.getPropValue("report.vou.printer");
            String compName = Util1.getPropValue("report.company.name");
            String bankInfo = Util1.getPropValue("report.bankinfo");
            String printMode = Util1.getPropValue("report.vou.printer.mode");
            if (chkA5.isSelected()) {
                String a5Report = Util1.getPropValue("report.file.saleW");
                if (a5Report.isEmpty() || a5Report.equals("-")) {
                    reportName = "W/SaleVoucherInvoiceA5";
                } else {
                    reportName = a5Report;
                }
                printMode = "View";
            }
            String reportPath = Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path")
                    + reportName;

            Map<String, Object> params = new HashMap();
            params.put("p_bank_desp", bankInfo);
            params.put("link_amt_status", "N");
            params.put("link_amt", 0);

            try {
                String delSql = "delete from tmp_amount_link where user_id = '"
                        + Global.machineId + "'";
                dao.execSql(delSql);
            } catch (Exception ex) {
                log.error("link delete : " + ex.getMessage());
            }

            if (Util1.getPropValue("system.link.amount").equals("Pharmacy")
                    && currSaleVou.getPatientId() != null) {
                try {

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
                            + "'    and tran_date = '" + DateUtil.toDateStr(currSaleVou.getSaleDate(), "yyyy-MM-dd")
                            + "'    and cus_id = '" + currSaleVou.getPatientId().getRegNo() + "'";

                    dao.execSql(strSql);

                    List<TempAmountLink> listTAL = dao.findAllHSQL(
                            "select o from TempAmountLink o where o.key.userId = '" + Global.machineId + "'");
                    if (listTAL != null) {
                        if (!listTAL.isEmpty()) {
                            AmountLinkDialog dialog = new AmountLinkDialog(listTAL);
                            dialog.setVisible(true);
                            double ttlLinkAmt = dialog.getTtlAmt();
                            if (ttlLinkAmt != 0) {
                                params.put("link_amt_status", "Y");
                                params.put("link_amt", ttlLinkAmt + currSaleVou.getPaid());
                            }
                        }
                    }
                } catch (Exception ex) {
                    log.error("print link amount : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                } finally {
                    dao.close();
                }
            }

            params.put("invoiceNo", currSaleVou.getSaleInvId());
            params.put("due_date", currSaleVou.getDueDate());
            if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                if (currSaleVou.getPatientId() != null) {
                    params.put("customerName", currSaleVou.getPatientId().getPatientName());
                    Date regDate = currSaleVou.getPatientId().getRegDate();
                    Date trgDate = DateUtil.toDate("08/10/2018", "dd/MM/yyyy");
                    if (regDate.before(trgDate)) {
                        String strYear = DateUtil.toDateStr(regDate, "yyyy");
                        params.put("reg_no", currSaleVou.getPatientId().getRegNo() + "/" + strYear);
                    } else {
                        params.put("reg_no", currSaleVou.getPatientId().getRegNo());
                    }
                } else {
                    params.put("customerName", txtCusName.getText());
                    params.put("reg_no", "");
                }

            } else if (currSaleVou.getCustomerId() != null) {
                params.put("customerName", currSaleVou.getCustomerId().getTraderName());
                params.put("reg_no", currSaleVou.getCustomerId().getTraderId());
            } else {
                params.put("customerName", "");
                params.put("reg_no", "");
            }

            if (currSaleVou.getDoctor() != null) {
                params.put("doctor", currSaleVou.getDoctor().getDoctorName());
            } else {
                params.put("doctor", "");
            }
            params.put("saleDate", currSaleVou.getSaleDate());
            params.put("grandTotal", currSaleVou.getVouTotal());
            params.put("paid", currSaleVou.getPaid());
            //params.put("paid", currSaleVou.getPayAmt());
            params.put("discount", currSaleVou.getDiscount());
            params.put("tax", currSaleVou.getTaxAmt());
            params.put("voubalance", currSaleVou.getBalance());
            params.put("balance", currSaleVou.getRefund());
            params.put("vou_balance", currSaleVou.getVouTotal() + currSaleVou.getTaxAmt()
                    - (currSaleVou.getDiscount() + currSaleVou.getPaid()));
            params.put("user", Global.loginUser.getUserShortName());
            params.put("listParam", listExpense);
            params.put("compName", compName);
            params.put("prv_date", lblSaleLastBal.getText());
            //double prvBalance = NumberUtil.NZero(txtSaleLastBalance.getValue()) + NumberUtil.NZero(tranTableModel.getTotal());
            params.put("prv_balance", Double.valueOf(txtSaleLastBalance.getValue().toString()));
            params.put("tran_total", NumberUtil.NZero(tranTableModel.getTotal()));
            /*NumberUtil.NZero(txtCusLastBalance.getText()) -
             (NumberUtil.NZero(currSaleVou.getPaid())+
             NumberUtil.NZero(currSaleVou.getDiscount()) + 
             NumberUtil.NZero(currSaleVou.getTaxAmt()))*/
            //double lastBalance = prvBalance + currSaleVou.getBalance();
            params.put("last_balance", txtCusLastBalance.getValue());
            params.put("due_date", txtDueDate.getValue());
            params.put("lblPrvBalance", lblSaleLastBal.getText());
            params.put("prv_date", lblSaleLastBal.getText());
            //params.put("prv_balance", txtSaleLastBalance.getText());
            //params.put("refund", currSaleVou.getRefund());
            params.put("p_machine_id", Global.machineId);
            Location loc = (Location) cboLocation.getSelectedItem();
            if (loc != null) {
                params.put("loc_name", loc.getLocationName());
            } else {
                params.put("loc_name", "-");
            }
            if (currSaleVou.getCustomerId() != null) {
                Trader tr = currSaleVou.getCustomerId();
                if (tr.getTownship() != null) {
                    params.put("township", tr.getTownship().getTownshipName());
                } else {
                    params.put("township", " ");
                }
                if (tr.getAddress() != null) {
                    params.put("address", tr.getAddress());
                } else {
                    params.put("address", " ");
                }
            }

            if (!txtDueDate.getText().isEmpty()) {
                params.put("pay_info", txtDueDate.getText() + " ေငြေကာက္မည္");
            } else {
                params.put("pay_info", " ");
            }

            if (lblStatus.getText().equals("NEW")) {
                params.put("vou_status", " ");
            } else {
                params.put("vou_status", lblStatus.getText());
            }

            params.put("pay_retin", tranTableModel.getTotal());
            params.put("comp_address", Util1.getPropValue("report.address"));
            params.put("phone", Util1.getPropValue("report.phone"));

            params.put("user_id", Global.machineId);
            params.put("user_short", Global.loginUser.getUserShortName());
            params.put("inv_id", currSaleVou.getSaleInvId());
            params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path"));
            params.put("IMAGE_PATH", Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path"));
            String imagePath = Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path") + "img/logo.jpg";
            params.put("imagePath", imagePath);
            params.put("comp_name", Util1.getPropValue("report.company.name1"));
            params.put("category", Util1.getPropValue("report.company.cat"));
            params.put("remark", txtRemark.getText());
            params.put("REPORT_CONNECTION", dao.getConnection());
            PaymentType pt = currSaleVou.getPaymentTypeId();
            if (pt != null) {
                params.put("prm_pay_type", pt.getPaymentTypeName());
            }
            params.put("user_desp", "Customer Voucher, Thanks You.");
            //String reportPath1 = "D:\\mws\\BEST\\BEST-Software\\src\\com\\best\\app\\pharmacy\\report\\test.jrxml";
            //ReportUtil.viewReport(reportPath1, null, dao.getConnection());

            try {
                if (printMode.equals("View") || !chkPrintOption.isSelected()) {
                    if (reportNameProp.equals("report.file.saleW")
                            || reportNameProp.equals("report.file.comp")) {
                        if (Util1.getPropValue("report.file.type").equals("con")) {
                            ReportUtil.viewReport(reportPath, params, dao.getConnection());
                        } else {
                            ReportUtil.viewReport(reportPath, params, listDetail);
                        }
                    } else if (Util1.getPropValue("report.file.type").equals("con")) {
                        ReportUtil.viewReport(reportPath, params, dao.getConnection());
                    } else {
                        ReportUtil.viewReport(reportPath, params, listDetail);
                    }
                } else {
                    if (Util1.getPropValue("report.file.type").equals("con")) {
                        JasperPrint jp = ReportUtil.getReport(reportPath, params, dao.getConnection());
                        int count = (int) spPrint.getValue();
                        for (int i = 0; i < count; i++) {
                            ReportUtil.printJasper(jp, printerName);
                        }
                    } else {
                        JasperPrint jp = ReportUtil.getReport(reportPath, params, listDetail);
                        ReportUtil.printJasper(jp, printerName);
                        if (Util1.getPropValue("system.pharmacy.sale.print.double").equals("Y")) {
                            params.put("user_desp", "Receive Voucher, Thanks You.");
                            JasperPrint jp1 = ReportUtil.getReport(reportPath, params, dao.getConnection());
                            ReportUtil.printJasper(jp1, printerName);
                        }
                    }
                    txtCusId.requestFocus();
                }
            } catch (Exception ex) {
                log.error("print : " + ex.getMessage());
            }
            clear();
        }
    }

    private boolean isValidEntry() {
        boolean status = true;
        double vouTtl = NumberUtil.NZero(txtVouTotal.getValue());
        double totalAmount = saleTableModel.getTotalAmount();
        Patient pt = currSaleVou.getPatientId();

        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {

            String admissionNo = "-";
            if (pt != null) {
                admissionNo = Util1.isNull(txtAdmissionNo.getText(), "-");
            } else {
                if (Util1.getPropValue("system.sale.patientmusthave").equals("Y")) {
                    if (currSaleVou.getCustomerId() == null) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Please enter registration number.",
                                "Invalid Patient", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
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
                        Date vouDate = DateUtil.toDate(txtSaleDate.getText());
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
                    JOptionPane.showMessageDialog(Util1.getParent(), "Please enter registration no.",
                            "Invalid Vou Date", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        }

        if (vouTtl != totalAmount) {
            log.error(txtVouNo.getText().trim() + " Sale Voucher Total Error : vouTtl : "
                    + vouTtl + " modelTtl : " + totalAmount);
            JOptionPane.showMessageDialog(Util1.getParent(), "Please check voucher total.",
                    "Voucher Total Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        calculateTotalAmount();
        double vouBal = NumberUtil.NZero(txtVouBalance.getText());

        if (!Util1.hashPrivilege("CanEditSaleCheckPoint")) {
            if (lblStatus.getText().equals("NEW")) {
                try {
                    long cnt = dao.getRowCount("select count(*) from c_bk_sale_his where sale_date >= '"
                            + DateUtil.toDateStrMYSQL(txtSaleDate.getText()) + "'");
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

        /*if(haveTransaction){
         JOptionPane.showMessageDialog(Util1.getParent(), "This voucher have related transaction. Changes will not be effected.",
         "Related transaction.", JOptionPane.ERROR_MESSAGE);
         } else*/
        if (txtVouNo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid sale vou no.",
                    "Sale Vou No", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (!DateUtil.isValidDate(txtSaleDate.getText())) {
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
        } else if (vouBal != 0 && currSaleVou.getPatientId() == null && currSaleVou.getCustomerId() == null
                && Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid registeration number.",
                    "Reg No", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCusId.requestFocusInWindow();
        } else {
            try {
                if (tblSale.getCellEditor() != null) {
                    tblSale.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }

            calculateTotalAmount();

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
                if (!isDeleteCopy) {
                    currSaleVou.setSession(Global.sessionId);
                }
            } else {
                currSaleVou.setUpdatedBy(Global.loginUser);
                currSaleVou.setUpdatedDate(DateUtil.getTodayDateTime());
            }

            currSaleVou.setPaidCurrencyExRate(1.0);
            currSaleVou.setPaidCurrencyAmt(currSaleVou.getPaid());
            currSaleVou.setPaidCurrency(currSaleVou.getCurrencyId());

            /*if (Util1.getPropValue("system.app.usage.type").equals("School")) {
                currSaleVou.setStuName(txtCusName.getText());
            }*/
            currSaleVou.setAdmissionNo(txtAdmissionNo.getText());

            if (txtBill.getText() == null) {
                currSaleVou.setOtId(null);
            } else if (txtBill.getText().isEmpty()) {
                currSaleVou.setOtId(null);
            } else {
                currSaleVou.setOtId(txtBill.getText());
            }

            currSaleVou.setStuName(txtCusName.getText());

            if (pt != null) {
                if (pt.getPtType() != null) {
                    currSaleVou.setPtType(pt.getPtType().getGroupId());
                }
            }

            if (NumberUtil.NZeroL(currSaleVou.getExrId()) == 0) {
                Long exrId = getExchangeId(txtSaleDate.getText(), currSaleVou.getCurrencyId().getCurrencyCode());
                currSaleVou.setExrId(exrId);
            }
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
                    //UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Customer List", dao);
                    int locationId = -1;
                    if (cboLocation.getSelectedItem() instanceof Location) {
                        locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
                    }
                    TraderSearchDialog dialog = new TraderSearchDialog(this,
                            "Customer List", locationId);
                    dialog.setTitle("Customer List");
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
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
        //Location location = (Location) cboLocation.getSelectedItem();
        //saleTableModel.setLocation(location);
        //codeCellEditor.setLocationId(location.getLocationId());
    }

    private void getCustomer() {
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (txtCusId.getText() != null && !txtCusId.getText().isEmpty()) {
            try {

                if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
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
                } else if (Util1.getPropValue("system.app.usage.type").equals("School")) {
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
                } else {
                    /*dao.open();
                    String traderId = txtCusId.getText().trim().toUpperCase();
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        if (!traderId.contains("CUS")) {
                            traderId = "CUS" + traderId;
                        }
                    }
                    Trader cus = (Trader) dao.find(Trader.class, traderId);
                    dao.close();*/

                    Trader cus = getTrader(txtCusId.getText().trim().toUpperCase());
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

    private Trader getTrader(String traderId) {
        Trader cus = null;
        try {
            String prefix = traderId.toUpperCase().substring(0, 3);
            if (!prefix.contains("SUP")) {
                if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                    if (!prefix.contains("CUS")) {
                        traderId = "CUS" + traderId;
                    }
                }
            }
            if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                int locationId = -1;
                if (cboLocation.getSelectedItem() instanceof Location) {
                    locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
                }
                List<Trader> listTrader = dao.findAllHSQL("select o from Trader o where "
                        + "o.active = true and o.traderId in (select a.key.traderId "
                        + "from LocationTraderMapping a where a.key.locationId = "
                        + locationId + ") and o.traderId = '" + traderId + "' order by o.traderName");
                if (listTrader != null) {
                    if (!listTrader.isEmpty()) {
                        cus = listTrader.get(0);
                    }
                }
            } else {
                cus = (Trader) dao.find(Trader.class, traderId);
            }
        } catch (Exception ex) {
            log.error("getTrader : " + ex.toString());
        } finally {
            dao.close();
        }

        return cus;
    }

    private void removeEmptyRow() {
        if (listDetail.size() > 0) {
            listDetail.remove(listDetail.size() - 1);
        }

        if (listExpense.size() > 0) {
            listExpense.remove(listExpense.size() - 1);
        }
    }

    private void getTraderTransaction() {
        Trader trader = currSaleVou.getCustomerId();

        String strTrdOpt;
        String strTodayDateTime = getTranDateTime();
        //String tmpStr = trader.getTraderId().substring(0, 3);

        try {
            Trader1 t1 = (Trader1) dao.find(Trader1.class, trader.getTraderId());
            if (t1.getDiscriminator().equals("S")) {
                strTrdOpt = "SUP";
            } else {
                strTrdOpt = "CUS";
            }

            Currency curr = (Currency) cboCurrency.getSelectedItem();
            dao.execProc("get_trader_transaction",
                    trader.getTraderId(), strTrdOpt, strTodayDateTime, curr.getCurrencyCode(),
                    Global.machineId,
                    Global.machineId);

            List<TraderTransaction> listTran = dao.findAll("TraderTransaction",
                    "userId = '" + Global.machineId + "' and tranType = 'D'"
                    + " and machineId = '" + Global.machineId + "'");
            if (listTran != null) {
                tranTableModel.setListDetail(listTran);
            }

            listTTDetail = dao.findAll("TTranDetail", "userId = '" + Global.machineId
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

            try {
                Trader1 t1 = (Trader1) dao.find(Trader1.class, currSaleVou.getCustomerId().getTraderId());
                if (t1.getDiscriminator().equals("S")) {
                    strTrdOpt = "SUP";
                } else {
                    strTrdOpt = "CUS";
                }
                Currency curr = (Currency) cboCurrency.getSelectedItem();
                ResultSet resultSet = dao.getPro("trader_last_balance",
                        currSaleVou.getCustomerId().getTraderId(), strTrdOpt,
                        strTodayDateTime, curr.getCurrencyCode());

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
            } catch (SQLException ex) {
                log.error("getTraderLastBalance : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } catch (Exception ex) {
                log.error("getTraderLastBalance : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    private void getPayment(String lastSaleDateTime, String currVouDateTime) {
        try {
            String strSql = "select v from TraderPayHis v where v.trader.traderId = '"
                    + currSaleVou.getCustomerId().getTraderId()
                    + "' and v.deleted = false and v.payDt > '"
                    + lastSaleDateTime + "' and v.payDt <= '" + currVouDateTime + "'";
            List<TraderPayHis> listPay = dao.findAllHSQL(strSql);

            for (TraderPayHis tph : listPay) {
                selected("SelectPayment", tph);
            }
        } catch (Exception ex) {
            log.error("getPayment : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private boolean isHaveTransaction(String vouNo) {
        boolean status = false;

        try {
            ResultSet resultSet = dao.getPro("is_have_vou_transaction", vouNo);
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
                        + Global.machineId + "' and pay_id = "
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

        //Price Change
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK);
        jc.getInputMap().put(keyStroke, "Ctrl-P-Action");
        jc.getActionMap().put("Ctrl-P-Action", actionPriceChange);
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
            clear();
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
        } else if (Util1.hashPrivilege("SaleEditVoucherChange") && canEdit) {
            txtCusId.setEditable(Util1.hashPrivilege("SaleCustomerChange"));
            cboCurrency.setEnabled(Util1.hashPrivilege("SaleCurrencyChange"));
            cboLocation.setEnabled(Util1.hashPrivilege("SaleLocationChange"));
            cboPayment.setEnabled(Util1.hashPrivilege("SalePaymentChange"));
            cboVouStatus.setEnabled(Util1.hashPrivilege("SaleVouStatus"));
            txtCusName.setEnabled(true);
            txtRemark.setEnabled(true);
            txtRemark1.setEnabled(true);
            txtDrCode.setEnabled(true);
            txtDrName.setEnabled(true);
            butOTID.setEnabled(true);
        } else {
            txtCusId.setEditable(false);
            cboCurrency.setEnabled(false);
            cboLocation.setEnabled(false);
            cboPayment.setEnabled(false);
            cboVouStatus.setEnabled(false);
            txtCusName.setEnabled(false);
            txtRemark.setEnabled(false);
            txtRemark1.setEnabled(false);
            txtDrCode.setEnabled(false);
            txtDrName.setEnabled(false);
            butOTID.setEnabled(false);
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
        tblTransaction.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
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
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (txtDrCode.getText() != null && !txtDrCode.getText().isEmpty()) {
            try {
                Doctor dr = null;

                dao.open();
                //dr = (Doctor) dao.find(Doctor.class, txtDrCode.getText());
                List<Doctor> listDr = dao.findAllHSQL("select o from Doctor o where o.doctorId = '"
                        + txtDrCode.getText().trim() + "' and o.active = true");
                if (listDr != null) {
                    if (!listDr.isEmpty()) {
                        dr = listDr.get(0);
                    }
                }
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
                    if (med.getMedId() != null && med.getMedName() != null) {
                        if (med.getMedId().contains(str) || med.getMedName().contains(str)) {
                            indexFound = i;
                            i = listDetail.size();
                        }
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

    private void uploadToAccount(String vouNo) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            String rootUrl = Util1.getPropValue("system.intg.api.url");
            if (!rootUrl.isEmpty() && !rootUrl.equals("-")) {
                try (CloseableHttpClient httpClient = createHttpClientWithTimeouts()) {
                    String url = rootUrl + "/sale";
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
            dao.execSql("update sale_his set intg_upd_status = null where sale_inv_id = '" + vouNo + "'");
        } catch (Exception ex) {
            log.error("sale updateNull: " + ex.getMessage());
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

    public void timerFocus() {
        Timer timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtCusId.requestFocus();
                try {
                    MachineInfo machine = (MachineInfo) dao.find(MachineInfo.class, Integer.parseInt(Global.machineId));
                    Location loc = (Location) cboLocation.getSelectedItem();
                    if (machine.getActionStatus() != null) {
                        log.info("Sale timerFocus : Strart");
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                        /*if (dao.getRowCount("select count(*) from item_type_mapping where group_id =" + Global.loginUser.getUserRole().getRoleId()) > 0) {
                            Global.listItem = dao.findAll("Medicine", "active = true and medTypeId.itemTypeCode in (select a.key.itemType.itemTypeCode from ItemTypeMapping a)");
                        } else {
                            if (strCodeFilter.equals("Y")) {
                                Location loc = (Location)cboLocation.getSelectedItem();
                                Global.listItem = dao.findAllHSQL(
                                        "select o from Medicine o where o.medId in (select a.key.itemId from "
                                        + "LocationItemMapping a where a.key.locationId = "
                                        + loc.getLocationId().toString() + ") order by o.medId, o.medName");
                            } else {
                                Global.listItem = dao.findAll("Medicine", "active = true");
                            }
                        }*/
                        if (dao.getRowCount("select count(*) from item_type_mapping where group_id ="
                                + Global.loginUser.getUserRole().getRoleId()) > 0) {
                            Global.listItem = dao.findAll("VMedicine1",
                                    "active = true and medTypeId in (select a.key.itemType.itemTypeCode from ItemTypeMapping a where a.key.groupId = " + Global.loginUser.getUserRole().getRoleId() + ")");
                        } else if (strCodeFilter.equals("Y")) {
                            Global.listItem = dao.findAllHSQL(
                                    "select o from VMedicine1 o where o.medId in (select a.key.itemId from "
                                    + "LocationItemMapping a where a.key.locationId = "
                                    + loc.getLocationId().toString() + ") order by o.medId, o.medName");
                        } else {
                            Global.listItem = dao.findAll("VMedicine1", "active = true");
                        }

                        machine.setActionStatus(null);
                        dao.save(machine);
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        log.info("Sale timerFocus : End");
                    }
                } catch (Exception ex) {
                    log.error("timerFocus : " + ex.toString());
                } finally {
                    dao.close();
                }
                System.gc();
                if (Util1.getPropValue("system.sale.barcode").equals("Y")) {
                    tblSale.requestFocus();
                }

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
                            currency, Global.machineId)) {
                while (resultSet.next()) {
                    double bal = resultSet.getDouble("balance");
                    if (bal != 0) {
                        PatientBillPayment pbp = new PatientBillPayment();

                        pbp.setBillTypeDesp(resultSet.getString("payment_type_name"));
                        pbp.setBillTypeId(resultSet.getInt("bill_type"));
                        pbp.setCreatedBy(Global.machineId);
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
                + Global.machineId + "'";
        String strSQL = "insert into tmp_trader_bal_filter(trader_id,currency,op_date,user_id, amount) "
                + "select t.trader_id, t.cur_code, "
                + " ifnull(trop.op_date, '1900-01-01'),'" + Global.machineId
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
        try {
            dao.execProc("get_trader_unpaid_vou", Global.machineId,
                    DateUtil.toDateStrMYSQL(txtSaleDate.getText()),
                    DateUtil.toDateStrMYSQL(txtDueDate.getText()));
        } catch (Exception ex) {
            log.error("execTraderBalanceWithUPV : " + ex.getMessage());
        } finally {
            dao.close();
        }
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
                + Global.machineId + "' and o.dueDate < '"
                + DateUtil.toDateStrMYSQL(strTmpDate) + "'";
        try {
            List<TraderUnpaidVou> listTUV = dao.findAllHSQL(strSql);
            if (listTUV != null) {
                if (!listTUV.isEmpty()) {
                    status = true;
                    JOptionPane.showMessageDialog(Util1.getParent(), "Overdue voucher have.\nYou cannot open new voucher.",
                            "Overdue Voucher", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            log.error("isOverDue1 : " + ex.getMessage());
        } finally {
            dao.close();
        }
        return status;
    }

    private void setEditStatus(String invId) {
        //canEdit
        /*List<SessionCheckCheckpoint> list = dao.findAllHSQL(
                "select o from SessionCheckCheckpoint o where o.tranOption = 'PHARMACY-Sale' "
                + " and o.tranInvId = '" + invId + "'");*/
        boolean isAllowEdit = Util1.hashPrivilege("SaleCreditVoucherEdit");
        double vouPaid = NumberUtil.NZero(currSaleVou.getPaid());

        if (!Util1.hashPrivilege("CanEditSaleCheckPoint")) {
            if (currSaleVou != null) {
                if (currSaleVou.getAdmissionNo() != null) {
                    if (!currSaleVou.getAdmissionNo().trim().isEmpty()) {
                        AdmissionKey key = new AdmissionKey();
                        key.setRegister(currSaleVou.getPatientId());
                        key.setAmsNo(currSaleVou.getAdmissionNo());
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
                                "select * from c_bk_sale_his where sale_inv_id = '" + invId + "'");
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

    private boolean isOverdue(Customer cus) {
        boolean status = false;

        if (cus.getTraderGroup() != null) {
            int overDueVouCnt = NumberUtil.NZeroInt(cus.getTraderGroup().getOverDueVouCnt());
            if (overDueVouCnt > 0) {
                String strSql = "select count(b.vou_no) ttl_cnt from (\n"
                        + "select a.*, if(a.ttl_overdue<0,0,a.ttl_overdue) as ttl_overdue1 from (\n"
                        + "select sh.pur_date, pur_inv_id vou_no, sh.cus_id, t.trader_name, 'PURCHASE' vou_type,\n"
                        + "       sh.due_date, sh.remark ref_no, sh.vou_total, (sh.paid+ifnull(pah.pay_amt,0)) as ttl_paid, sh.discount, sh.balance,\n"
                        + "	   sum(ifnull(balance,0))-(ifnull(pah.pay_amt,0)) bal, \n"
                        + "	   if(ifnull(sh.due_date,'-')='-',0,if(DATEDIFF(sysdate(),sh.due_date)<0,0,DATEDIFF(sysdate(),sh.due_date))) as ttl_overdue \n"
                        + "from pur_his sh\n"
                        + "left join trader t on sh.cus_id = t.trader_id\n"
                        + "left join (select pv.vou_no, sum(ifnull(pv.vou_paid,0)+ifnull(pv.discount,0)) pay_amt, pv.vou_type\n"
                        + "			   from payment_his ph, pay_his_join phj, payment_vou pv\n"
                        + "			  where ph.payment_id = phj.payment_id and phj.tran_id = pv.tran_id\n"
                        + "				and ph.deleted = false\n"
                        + "				and pv.vou_type = 'PURCHASE'\n"
                        + "			  group by pv.vou_no, pv.vou_type) pah on sh.pur_inv_id = pah.vou_no\n"
                        + "where sh.deleted = false\n"
                        + "group by sh.pur_inv_id, sh.pur_date,sh.vou_total, sh.paid, sh.discount, sh.balance) a\n"
                        + "where a.bal > 0.9 and a.ttl_overdue > 0 order by a.cus_id, a.pur_date, a.vou_no) b\n"
                        + "where b.cus_id = '" + cus.getTraderId() + "'";

                try {
                    ResultSet rs = dao.execSQL(strSql);
                    if (rs != null) {
                        if (rs.next()) {
                            int ttlCnt = rs.getInt("ttl_cnt");
                            if (ttlCnt > overDueVouCnt) {
                                status = true;
                            }
                        }
                    }
                } catch (Exception ex) {
                    log.error("isOverdue : " + ex.toString());
                } finally {
                    dao.close();
                }
            }
        }
        return status;
    }

    private void updateVouTotal(String vouNo) {
        String strSql = "update sale_his sh \n"
                + "join (select vou_no, sum(ifnull(sale_amount,0)) as ttl_amt \n"
                + "from sale_detail_his where vou_no = '" + vouNo + "' group by vou_no) sd\n"
                + "on sh.sale_inv_id = sd.vou_no set sh.vou_total = sd.ttl_amt\n"
                + "where sh.sale_inv_id = '" + vouNo + "'";
        try {
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("updateVouTotal : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private Long getExchangeId(String strDate, String curr) {
        long id = 0;
        if (Util1.getPropValue("system.multicurrency").equals("Y")) {
            try {
                Object value = dao.getMax("exr_id", "exchange_rate",
                        "(to_curr = '" + curr + "' or from_curr = '" + curr
                        + "') and date(created_date) = '"
                        + DateUtil.toDateStrMYSQL(strDate)
                        + "'"
                );
                if (value != null) {
                    id = NumberUtil.NZeroL(value);
                }
            } catch (Exception ex) {
                log.error("getExchangeId : " + ex.getMessage());
            }
        }
        return id;
    }

    private boolean isBaseGroupOverdue(Trader cus) {
        boolean status = false;

        if (Util1.getPropValue("system.cus.base.group.ovc").equals("Y")) {
            log.info("Soe group");
            try {
                String saleDate = DateUtil.toDateStrMYSQL(txtSaleDate.getText());
                String strSql = "select t.trader_id, sum(ifnull(ph.paid_amtc,0)+ifnull(ph.discount,0)) ttl_period_paid\n"
                        + "from trader t, payment_his ph\n"
                        + "where t.trader_id = ph.trader_id and ph.deleted = false\n"
                        + "and ph.pay_date between DATE_SUB('" + saleDate + "', INTERVAL ifnull(t.credit_days,0) DAY) "
                        + "and '" + saleDate + "' and t.trader_id = '" + cus.getTraderId() + "'\n"
                        + "group by t.trader_id";
                ResultSet rs = dao.execSQL(strSql);
                Double ttlPeriodPaid = 0.0;
                Double ttlLastBalance = NumberUtil.NZero(txtCusLastBalance.getValue())
                        - NumberUtil.NZero(txtVouBalance.getValue());

                if (rs != null) {
                    if (rs.next()) {
                        ttlPeriodPaid = rs.getDouble("ttl_period_paid");
                    }
                }
                log.info("ttlLastBalance : " + ttlLastBalance + " ttlPeriodPaid : " + ttlPeriodPaid);
                if ((ttlLastBalance - ttlPeriodPaid) > 3) {
                    status = true;
                    currSaleVou.setIsOverdue(status);
                    //lblCreditLimit.setText("Total Paid : ");
                    //txtCreditLimit.setValue(ttlPeriodPaid);
                    String msg = "Overdue have. You cannot open new voucher for this customer.\n"
                            + "Total Balance : " + ttlLastBalance + "\n"
                            + "Total Paid : " + ttlPeriodPaid;
                    JOptionPane.showMessageDialog(Util1.getParent(), msg,
                            "Overdue", JOptionPane.ERROR_MESSAGE);
                } else {
                    status = false;
                    currSaleVou.setIsOverdue(status);
                    //lblCreditLimit.setText("Credit limit : ");
                }
            } catch (Exception ex) {
                log.error("isOverdue system.cus.base.group.ovc : " + ex.toString());
            }
        } else {
            status = false;
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
        lblDueRemark = new javax.swing.JLabel();
        butOTID = new javax.swing.JButton();
        txtBill = new javax.swing.JTextField();
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
        jLabel12 = new javax.swing.JLabel();
        lblBrandName = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        lblRemark = new javax.swing.JLabel();
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
        chkPrintOption = new javax.swing.JCheckBox();
        chkAmount = new javax.swing.JCheckBox();
        chkVouComp = new javax.swing.JCheckBox();
        jLabel23 = new javax.swing.JLabel();
        txtRecNo = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtTotalItem = new javax.swing.JTextField();
        butSaveTemp = new javax.swing.JButton();
        butTempList = new javax.swing.JButton();
        cboEntryUser = new javax.swing.JComboBox<>();
        chkA5 = new javax.swing.JCheckBox();
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
        jLabel20 = new javax.swing.JLabel();
        spPrint = new javax.swing.JSpinner();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSale = new javax.swing.JTable(saleTableModel);

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
        lblPatient.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPatientMouseClicked(evt);
            }
        });

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
        cboLocation.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboLocationItemStateChanged(evt);
            }
        });
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
        txtAdmissionNo.setFont(Global.lableFont);

        butAdmit.setText("Admit");
        butAdmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butAdmitActionPerformed(evt);
            }
        });

        lblDueRemark.setFont(Global.lableFont);
        lblDueRemark.setForeground(new java.awt.Color(255, 0, 0));
        lblDueRemark.setText("This Cust have Due Vou");

        butOTID.setFont(Global.lableFont);
        butOTID.setText("Bill ID");
        butOTID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOTIDActionPerformed(evt);
            }
        });

        txtBill.setEditable(false);
        txtBill.setFont(Global.lableFont);

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
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(jPanel1Layout.createSequentialGroup()
                            .add(txtAdmissionNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 129, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(txtFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 97, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                            .add(butAdmit)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(lblDueRemark)))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(butOTID)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(txtBill, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 151, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
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
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtAdmissionNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txtFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(lblDueRemark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(butAdmit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(butOTID)
                            .add(txtBill, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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
            .add(org.jdesktop.layout.GroupLayout.TRAILING, panelExpenseLayout.createSequentialGroup()
                .addContainerGap()
                .add(panelExpenseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .add(panelExpenseLayout.createSequentialGroup()
                        .add(lblExpTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtTtlExpIn)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtTotalExpense)))
                .addContainerGap())
        );
        panelExpenseLayout.setVerticalGroup(
            panelExpenseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelExpenseLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(panelExpenseLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblExpTotal)
                    .add(txtTotalExpense, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtTtlExpIn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(txtCusLastBalance, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                    .add(txtDifference))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5Layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(lblCreditLimit, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(lblSaleLastBal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(txtSaleLastBalance, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .add(txtCreditLimit))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
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
                    .add(lblDifference))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblStockList.setFont(Global.textFont);
        tblStockList.setModel(stockTableModel);
        tblStockList.setRowHeight(23);
        jScrollPane6.setViewportView(tblStockList);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Brand Name : ");

        lblBrandName.setFont(Global.textFont);
        lblBrandName.setText(" ");

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Remark :");

        lblRemark.setFont(Global.textFont);
        lblRemark.setText(" ");

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jScrollPane6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jLabel12)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblBrandName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jLabel13)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lblRemark, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jScrollPane6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(lblBrandName)
                    .add(jLabel13)
                    .add(lblRemark)))
        );

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelExpense, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(panelExpense, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

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
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel12Layout.createSequentialGroup()
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel12Layout.createSequentialGroup()
                        .addContainerGap(167, Short.MAX_VALUE)
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtBillTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 129, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(5, 5, 5))
                    .add(jPanel12Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lblTranOption, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel12Layout.createSequentialGroup()
                                .add(butPayment, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 106, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(butWarranty)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(butOutstanding, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 95, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
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
                .add(jScrollPane5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtBillTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addContainerGap())
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        lblStatus.setFont(new java.awt.Font("Velvenda Cooler", 0, 40)); // NOI18N
        lblStatus.setText("NEW");

        chkPrintOption.setText("Vou Printer");

        chkAmount.setText("Check Amount");

        chkVouComp.setText("Vou Comp");

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

        butSaveTemp.setFont(Global.textFont);
        butSaveTemp.setText("Save Temp");
        butSaveTemp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveTempActionPerformed(evt);
            }
        });

        butTempList.setFont(Global.textFont);
        butTempList.setText("Temp List");
        butTempList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butTempListActionPerformed(evt);
            }
        });

        cboEntryUser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        chkA5.setText("A5");

        org.jdesktop.layout.GroupLayout jPanel13Layout = new org.jdesktop.layout.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chkAmount, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel13Layout.createSequentialGroup()
                        .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, chkPrintOption, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, lblStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, cboEntryUser, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel13Layout.createSequentialGroup()
                                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(jPanel13Layout.createSequentialGroup()
                                        .add(jLabel22)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(txtTotalItem, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE))
                                    .add(butSaveTemp, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(butTempList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .add(0, 0, Short.MAX_VALUE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel13Layout.createSequentialGroup()
                                .add(jLabel23)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(txtRecNo))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, chkA5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(chkVouComp, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );

        jPanel13Layout.linkSize(new java.awt.Component[] {jLabel22, jLabel23}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .add(lblStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chkPrintOption)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chkAmount)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chkVouComp)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chkA5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel23)
                    .add(txtRecNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel22)
                    .add(txtTotalItem, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cboEntryUser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(butSaveTemp)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(butTempList)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtTax.setEditable(false);
        txtTax.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Paid : ");

        jLabel19.setFont(Global.lableFont);
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
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
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator2)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSeparator1)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel9Layout.createSequentialGroup()
                        .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jPanel9Layout.createSequentialGroup()
                                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jLabel14, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jPanel9Layout.createSequentialGroup()
                                        .add(jLabel11)
                                        .add(0, 4, Short.MAX_VALUE)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                            .add(jPanel9Layout.createSequentialGroup()
                                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel19, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(jLabel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .add(6, 6, 6)))
                        .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel9Layout.createSequentialGroup()
                                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(txtTaxP)
                                    .add(txtDiscP))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jLabel17)
                                    .add(jLabel18))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(txtVouDiscount, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                    .add(txtTax)))
                            .add(txtGrandTotal)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel9Layout.createSequentialGroup()
                                .add(0, 0, Short.MAX_VALUE)
                                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtVouPaid, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 169, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtVouBalance, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 169, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel9Layout.createSequentialGroup()
                        .add(jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtVouTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 169, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
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

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Print Copies :");

        spPrint.setFont(Global.textFont);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel20)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(spPrint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel20)
                    .add(spPrint, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        tblSale.setAutoCreateColumnsFromModel(false);
        tblSale.setFont(Global.textFont);
        tblSale.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tblSale.setRowHeight(23);
        tblSale.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblSaleFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblSaleFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(tblSale);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(10, 10, 10))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jScrollPane1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSaleDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtSaleDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            if (!canEdit) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                        "Check Point", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Util1.hashPrivilege("SaleEditVoucherChange") && lblStatus.getText().equals("EDIT")) {

            } else if (Util1.hashPrivilege("SaleDateChange")) {
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
        if (evt.getClickCount() == mouseClick) {
            if (!canEdit) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                        "Check Point", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Util1.hashPrivilege("SaleEditVoucherChange") && lblStatus.getText().equals("EDIT")) {

            } else {
                String strDate = DateUtil.getDateDialogStr();

                if (strDate != null) {
                    txtDueDate.setText(strDate);
                }
            }
        }
    }//GEN-LAST:event_txtDueDateMouseClicked

    private void txtCusIdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusIdMouseClicked
        if (evt.getClickCount() == 2) {
            if (!canEdit) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                        "Check Point", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Util1.hashPrivilege("SaleEditVoucherChange") && lblStatus.getText().equals("EDIT")) {

            } else {
                getCustomerList();
            }
        }
    }//GEN-LAST:event_txtCusIdMouseClicked

    private void txtCusIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusIdActionPerformed
        if (txtCusId.getText() == null || txtCusId.getText().isEmpty()) {
            txtCusName.setText(null);
            currSaleVou.setCustomerId(null);
            currSaleVou.setRegNo(null);
            currSaleVou.setStuName(null);
            currSaleVou.setStuNo(null);
            butOTID.setEnabled(false);
            txtBill.setText(null);
            codeCellEditor.setCusGroup(null);
            //txtCusId.requestFocus();
        } else {
            String tmpId = txtCusId.getText().trim();
            String[] tmpIds = tmpId.split("-");
            if (tmpIds.length > 1) {
                currSaleVou.setVisitId(tmpId);
                String drId = tmpIds[0];
                String regNo = tmpIds[1];
                txtCusId.setText(regNo);
                getCustomer();
                txtDrCode.setText(drId);
                getDoctor();
            } else {
                currSaleVou.setVisitId(null);
                getCustomer();
            }

        }
    }//GEN-LAST:event_txtCusIdActionPerformed

    private void txtCusIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusIdFocusLost
        //if(!txtCusId.getText().equals(""))
        //getCustomer();
    }//GEN-LAST:event_txtCusIdFocusLost

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == mouseClick) {
            if (!canEdit) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                        "Check Point", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!Util1.hashPrivilege("SaleEditVoucherChange") && lblStatus.getText().equals("EDIT")) {

            } else {
                getCustomerList();
            }
        }
    }//GEN-LAST:event_txtCusNameMouseClicked

    private void cboPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPaymentActionPerformed
        if (!isBind) {
            PaymentType pt = (PaymentType) cboPayment.getSelectedItem();
            if (pt.getPaymentTypeId() == 2) {
                String percent = Util1.getPropValue("system.sale.cashdown.disc.percent");
                if (percent != null) {
                    if (!percent.isEmpty()) {
                        txtDiscP.setText(percent);
                        Double vouTotal = NumberUtil.NZero(txtVouTotal.getText());
                        Double valPercent = NumberUtil.NZero(percent);
                        Double discount = (vouTotal * valPercent) / 100;
                        txtVouDiscount.setText(discount.toString());
                    }
                }

                String creditWeek = Util1.getPropValue("system.sale.credit.week");
                if (creditWeek != null) {
                    if (creditWeek.equals("Y")) {
                        //Show dialoug to enter week
                        String strWeek = JOptionPane.showInputDialog("Number of Weeks.");
                        if (strWeek != null) {
                            int week = Integer.parseInt(strWeek);
                            txtDueDate.setText(DateUtil.subDateTo(
                                    DateUtil.toDate(txtSaleDate.getText()), (week * 7)));
                        }
                    }
                }
            } else {
                txtDiscP.setText(null);
                txtVouDiscount.setText(null);
            }
            calculateTotalAmount();
        }
    }//GEN-LAST:event_cboPaymentActionPerformed

  private void txtDrNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDrNameMouseClicked
      if (!canEdit) {
          JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                  "Check Point", JOptionPane.ERROR_MESSAGE);
          return;
      }
      if (!Util1.hashPrivilege("SaleEditVoucherChange") && lblStatus.getText().equals("EDIT")) {

      } else {
          DoctorSearchNameFilterDialog dialog = new DoctorSearchNameFilterDialog(dao, this);
      }
  }//GEN-LAST:event_txtDrNameMouseClicked

  private void txtDrCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDrCodeActionPerformed
      if (!Util1.hashPrivilege("SaleEditVoucherChange") && lblStatus.getText().equals("EDIT")) {

      } else {
          getDoctor();
      }
  }//GEN-LAST:event_txtDrCodeActionPerformed

  private void txtCusNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusNameActionPerformed
      // TODO add your handling code here:
  }//GEN-LAST:event_txtCusNameActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilterActionPerformed
        finder(txtFilter.getText());
    }//GEN-LAST:event_txtFilterActionPerformed

    private void butTempListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butTempListActionPerformed
        if (tmpVouList.size() > 0) {

        } else {
            butTempList.setEnabled(false);
        }
    }//GEN-LAST:event_butTempListActionPerformed

    private void butSaveTempActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveTempActionPerformed
        tmpVouList.add(currSaleVou.getSaleInvId());
        butTempList.setEnabled(true);
        save();
    }//GEN-LAST:event_butSaveTempActionPerformed

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
        Location location = (Location) cboLocation.getSelectedItem();
        saleTableModel.setLocation(location);
        if (!isBind) {
            codeCellEditor.setLocationId(location.getLocationId());
        }
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

    private void butOTIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOTIDActionPerformed
        try {
            RegNo regNo = new RegNo(dao, "OT-ID");
            Patient pt = currSaleVou.getPatientId();
            if (pt != null) {
                pt.setOtId(regNo.getRegNo());
                dao.save(pt);
                regNo.updateRegNo();
                txtBill.setText(pt.getOtId());
                cboPayment.setSelectedItem(ptCredit);
                butOTID.setEnabled(false);
            }
        } catch (Exception ex) {
            log.error("butOTIDActionPerformed : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butOTIDActionPerformed

    private void tblSaleFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblSaleFocusLost
        /*try{
         if(tblSale.getCellEditor() != null){
         tblSale.getCellEditor().stopCellEditing();
         }
         }catch(Exception ex){
            
         }*/
    }//GEN-LAST:event_tblSaleFocusLost

    private void lblPatientMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPatientMouseClicked
        if (evt.getClickCount() == 2) {
            int locationId = -1;
            if (cboLocation.getSelectedItem() instanceof Location) {
                locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
            }
            TraderSearchDialog dialog = new TraderSearchDialog(this,
                    "Customer List", locationId);
            dialog.setTitle("Customer List");
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
    }//GEN-LAST:event_lblPatientMouseClicked

    private void cboLocationItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboLocationItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cboLocationItemStateChanged
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
        try {
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
        } catch (Exception ex) {
            log.error("deleteDetail : " + ex.toString());
        } finally {
            dao.close();
        }
        //delete section end
    }
    // <editor-fold defaultstate="collapsed" desc="Control Declaration">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butAdmit;
    private javax.swing.JButton butOTID;
    private javax.swing.JButton butOutstanding;
    private javax.swing.JButton butPayment;
    private javax.swing.JButton butSaveTemp;
    private javax.swing.JButton butTempList;
    private javax.swing.JButton butWarranty;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox<String> cboEntryUser;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JComboBox cboVouStatus;
    private javax.swing.JCheckBox chkA5;
    private javax.swing.JCheckBox chkAmount;
    private javax.swing.JCheckBox chkPrintOption;
    private javax.swing.JCheckBox chkVouComp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
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
    private javax.swing.JLabel lblBrandName;
    private javax.swing.JLabel lblCreditLimit;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDifference;
    private javax.swing.JLabel lblDoctor;
    private javax.swing.JLabel lblDueRemark;
    private javax.swing.JLabel lblExpTotal;
    private javax.swing.JLabel lblLastBalance;
    private javax.swing.JLabel lblPatient;
    private javax.swing.JLabel lblRemark;
    private javax.swing.JLabel lblRemark1;
    private javax.swing.JLabel lblSaleLastBal;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTranOption;
    private javax.swing.JPanel panelExpense;
    private javax.swing.JSpinner spPrint;
    private javax.swing.JTable tblExpense;
    private javax.swing.JTable tblPatientBill;
    private javax.swing.JTable tblSale;
    private javax.swing.JTable tblStockList;
    private javax.swing.JTable tblTranDetail;
    private javax.swing.JTable tblTransaction;
    private javax.swing.JTextField txtAdmissionNo;
    private javax.swing.JTextField txtBill;
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
