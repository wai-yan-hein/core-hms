/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Customer;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.PurDetailHis;
import com.cv.app.pharmacy.database.entity.PurHis;
import com.cv.app.pharmacy.database.entity.PurchaseExpense;
import com.cv.app.pharmacy.database.entity.PurchaseOutstand;
import com.cv.app.pharmacy.database.entity.RelationGroup;
import com.cv.app.pharmacy.database.entity.Supplier;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.Trader1;
import com.cv.app.pharmacy.database.entity.VouStatus;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.database.tempentity.BarcodeFilter;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.PurchaseExpTableModel;
import com.cv.app.pharmacy.ui.common.PurchaseTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.util.MedListDialog1;
import com.cv.app.pharmacy.ui.util.PriceChangeDialog;
import com.cv.app.pharmacy.ui.util.PromoVou;
import com.cv.app.pharmacy.ui.util.PurchaseConfirmDialog;
import com.cv.app.pharmacy.ui.util.PurchaseItemPromoDialog;
import com.cv.app.pharmacy.ui.util.PurchaseOutstandingDialog;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
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

/**
 *
 * @author WSwe
 */
public class Purchase extends javax.swing.JPanel implements SelectionObserver, FormAction,
        MedInfo, KeyPropagate, KeyListener {

    static Logger log = Logger.getLogger(Purchase.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private GenVouNoImpl vouEngine = null;
    private List<PurDetailHis> listDetail
            = ObservableCollections.observableList(new ArrayList<>());
    private List<PurchaseExpense> listExpense
            = ObservableCollections.observableList(new ArrayList<>());
    private MedicineUP medUp = new MedicineUP(dao);
    private BestAppFocusTraversalPolicy focusPolicy;
    private PurHis currPurVou = new PurHis();
    private PurchaseExpTableModel expTableModel = new PurchaseExpTableModel(listExpense);
    private PurchaseTableModel purTableModel = new PurchaseTableModel(listDetail, dao,
            medUp, this);
    private final SaleTableCodeCellEditor codeCellEditor = new SaleTableCodeCellEditor(dao);
    private double cusLastBalance = 0;
    private String deleteOutstandList;
    private String strPrvDate;
    private Object prvLocation;
    private Object prvPymet;
    private String focusCtrlName = "-";
    private boolean bindStatus = false;
    private boolean canEdit = true;
    private int mouseClick = 2;

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
            if (!focusCtrlName.equals("-")) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtCusId")) {
                    txtRemark.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtRemark")) {
                    tblPurchase.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtCusId")) {
                    txtRemark.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtRemark")) {
                    txtCusId.requestFocus();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("tblPurchase")) {
                    int selRow = tblPurchase.getSelectedRow();

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
        txtCusId.addKeyListener(this);
        txtRemark.addKeyListener(this);
        tblPurchase.addKeyListener(this);
    }

    /**
     * Creates new form Sale
     */
    public Purchase() {
        initComponents();

        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
            dao.open();
            initCombo();
            initExpenseTable();
            dao.close();
        } catch (Exception ex) {
            log.error("Purchase : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        txtPurDate.setText(DateUtil.getTodayDateStr());
        vouEngine = new GenVouNoImpl(dao, "Purchase", DateUtil.getPeriod(txtPurDate.getText()));

        initPurTable();
        actionMapping();
        initTextBoxAlign();
        initTextBoxValue();
        genVouNo();
        addNewRow();
        applyFocusPolicy();
        //AddFocusMoveKey();
        initForFocus();

        txtTaxP.setText(Util1.getPropValue("system.purchase.tax.percent"));

        this.setFocusTraversalPolicy(focusPolicy);
        purTableModel.setParent(tblPurchase);
        purTableModel.setLblItemBrand(lblBrandName);
        purTableModel.setLblRemark(lblRemark);
        lblStatus.setText("NEW");
        assignDefaultValue();
        //F3 trader code
        txtCusId.registerKeyboardAction(traderF3Action, KeyStroke.getKeyStroke("F3"),
                JComponent.WHEN_FOCUSED);

        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
            tblExpense.setVisible(false);
            jPanel5.setVisible(false);
            chkCashOut.setVisible(false);
            jLabel13.setVisible(false);
            txtTotalExpense.setVisible(false);
            lblPrvBalance.setVisible(false);
            jLabel12.setVisible(false);
            txtPrvBalance.setVisible(false);
            txtCusLastBalance.setVisible(false);
            //jPanel6.setVisible(false);
            //butOutstanding.setVisible(false);
        }

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

    private void genVouNo() {
        try {
            String vouNo = vouEngine.getVouNo();
            txtVouNo.setText(vouNo);
            List<PurHis> listPH = dao.findAllHSQL(
                    "select o from PurHis o where o.purInvId = '" + txtVouNo.getText() + "'");
            if (listPH != null) {
                if (!listPH.isEmpty()) {
                    vouEngine.updateVouNo();
                    vouNo = vouEngine.getVouNo();
                    txtVouNo.setText(vouNo);
                    //listPH = null;
                    listPH = dao.findAllHSQL(
                            "select o from PurHis o where o.purInvId = '" + txtVouNo.getText() + "'");

                    if (listPH != null) {
                        if (!listPH.isEmpty()) {
                            log.error("Duplicate purchase vour error : " + txtVouNo.getText() + " @ "
                                    + txtPurDate.getText());
                            JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate purchase vou no. Exit the program and try again.",
                                    "Purchase Vou No", JOptionPane.ERROR_MESSAGE);
                            System.exit(1);
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

    private void clear() {
        try {
            if (tblPurchase.getCellEditor() != null) {
                tblPurchase.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        if (lblStatus.getText().equals("NEW")) {
            strPrvDate = txtPurDate.getText();
            prvLocation = cboLocation.getSelectedItem();
            prvPymet = cboPayment.getSelectedItem();
        }
        canEdit = true;
        //Clear text box.
        txtVouNo.setText("");
        txtPurDate.setText("");
        txtDueDate.setText("");
        txtCusId.setText("");
        txtCusName.setText("");
        txtRemark.setText("");
        txtRefNo.setText("");
        lblStatus.setText("NEW");
        lblStatus.setForeground(Color.BLACK);
        cusLastBalance = 0;
        lblPrvBalance.setText("Balance :");
        chkCashOut.setSelected(false);
        currPurVou = new PurHis();
        purTableModel.clear();
        lblRemark.setText("");
        assignDefaultValueModel();
        initTextBoxValue();
        if (strPrvDate != null) {
            txtPurDate.setText(strPrvDate);
        }
        vouEngine.setPeriod(DateUtil.getPeriod(txtPurDate.getText()));
        genVouNo();
        //setFocus();
        assignDefaultValue();
        txtTaxP.setText(Util1.getPropValue("system.purchase.tax.percent"));
    }

    // <editor-fold defaultstate="collapsed" desc="initCombo">
    private void initCombo() {
        try {
            bindStatus = true;
            BindingUtil.BindCombo(cboPayment, dao.findAll("PaymentType"));
            BindingUtil.BindCombo(cboLocation, getLocationFilter());
            BindingUtil.BindCombo(cboVouStatus, dao.findAll("VouStatus"));
            BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));

            new ComBoBoxAutoComplete(cboPayment, this);
            new ComBoBoxAutoComplete(cboLocation, this);
            new ComBoBoxAutoComplete(cboVouStatus, this);
            new ComBoBoxAutoComplete(cboCurrency, this);
            bindStatus = false;
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }// </editor-fold>

    private List getLocationFilter() {
        try {
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                return dao.findAllHSQL(
                        "select o from Location o where o.locationId in ("
                        + "select a.key.locationId from UserLocationMapping a "
                        + "where a.key.userId = '" + Global.loginUser.getUserId()
                        + "' and a.isAllowPur = true) order by o.locationName");
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

    // <editor-fold defaultstate="collapsed" desc="actionMapping">
    private void actionMapping() {
        //F3 event on tblSale
        tblPurchase.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblPurchase.getActionMap().put("F3-Action", actionMedList);

        //F8 event on tblSale
        tblPurchase.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblPurchase.getActionMap().put("F8-Action", actionItemDelete);

        //Enter event on tblSale
        tblPurchase.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblPurchase.getActionMap().put("ENTER-Action", actionTblSaleEnterKey);

        //Enter event on tblSale
        tblExpense.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblExpense.getActionMap().put("ENTER-Action", actionTblExpEnterKey);

        //F8 event on tblExpense
        tblExpense.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblExpense.getActionMap().put("F8-Action", actionItemDeleteExp);

        formActionKeyMapping(txtVouNo);
        formActionKeyMapping(txtRemark);
        formActionKeyMapping(txtPurDate);
        formActionKeyMapping(txtDueDate);
        formActionKeyMapping(cboLocation);
        formActionKeyMapping(txtCusId);
        formActionKeyMapping(txtCusName);
        formActionKeyMapping(txtRefNo);
        formActionKeyMapping(cboPayment);
        formActionKeyMapping(cboCurrency);
        formActionKeyMapping(cboVouStatus);
        formActionKeyMapping(tblPurchase);
        formActionKeyMapping(tblExpense);
        formActionKeyMapping(txtTotalExpense);
        formActionKeyMapping(txtVouTotal);
        formActionKeyMapping(txtVouPaid);
        formActionKeyMapping(txtVouDiscount);
        formActionKeyMapping(txtVouBalance);
        formActionKeyMapping(txtCusLastBalance);
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionMedList">
    private Action actionMedList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblPurchase.getCellEditor() != null) {
                    tblPurchase.getCellEditor().stopCellEditing();
                }

                //No entering medCode, only press F3
                try {
                    dao.open();
                    getMedList("");
                    dao.close();
                } catch (Exception ex1) {
                    log.error("actionMedList : " + ex1.getStackTrace()[0].getLineNumber() + " - " + ex1.toString());
                }
            } catch (Exception ex) {

            }
        }
    };// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionItemDelete">
    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            PurDetailHis pdh;
            int yes_no = -1;

            if (tblPurchase.getSelectedRow() >= 0) {
                pdh = listDetail.get(tblPurchase.getSelectedRow());

                if (pdh.getMedId().getMedId() != null) {
                    try {
                        yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                                "Purchase item delete", JOptionPane.YES_NO_OPTION);
                        if (tblPurchase.getCellEditor() != null) {
                            tblPurchase.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {
                        //log.error(ex.toString());
                    }

                    if (yes_no == 0) {
                        purTableModel.delete(tblPurchase.getSelectedRow());
                        deleteOutstand(pdh.getMedId().getMedId(), pdh.getExpireDate(),
                                pdh.getChargeId().getChargeTypeId());
                        calculateTotalAmount();
                    }
                }
            }
        }
    };// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionItemDeleteExp">
    private Action actionItemDeleteExp = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            PurchaseExpense se;
            int yes_no = -1;

            if (tblExpense.getSelectedRow() >= 0) {
                se = listExpense.get(tblExpense.getSelectedRow());

                if (se.getExpType().getExpenseId() != null) {
                    try {
                        yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                                "Expense item delete", JOptionPane.YES_NO_OPTION);

                        if (tblExpense.getCellEditor() != null) {
                            tblExpense.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {
                        log.error("actionItemDeleteExp : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    }

                    if (yes_no == 0) {
                        expTableModel.delete(tblExpense.getSelectedRow());
                        calculateTotalAmount();
                    }
                }
            }
        }
    };// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="getMedList">
    private void getMedList(String filter) {
        int locationId = -1;
        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }
        String cusGroup = "-";
        /*if(currSaleVou.getCustomerId() != null){
            if(currSaleVou.getCustomerId().getTraderGroup() != null){
                cusGroup = currSaleVou.getCustomerId().getTraderGroup().getGroupId();
            }
        }*/
        MedListDialog1 dialog = new MedListDialog1(dao, filter, locationId, cusGroup);

        if (dialog.getSelect() != null) {
            selected("MedicineList", dialog.getSelect());
        }
    }// </editor-fold>

    @Override
    public void selected(Object source, Object selectObj) {
        try {
            dao.open();
            switch (source.toString()) {
                case "CustomerList":
                    Trader cus = (Trader) dao.find(Trader.class, ((Trader) selectObj).getTraderId());

                    currPurVou.setCustomerId(cus);

                    if (cus != null) {
                        txtCusId.setText(cus.getTraderId());
                        txtCusName.setText(cus.getTraderName());

                        if (cus instanceof Supplier) {
                            int creditDay = NumberUtil.NZeroInt(((Supplier) cus).getCreditDays());

                            if (creditDay != 0) {
                                txtDueDate.setText(DateUtil.subDateTo(DateUtil.toDate(txtPurDate.getText()), creditDay));
                                //txtDueDate.setText(DateUtil.addTodayDateTo(creditDay));
                            }
                        }

                        //Change payment type to credit
                        PaymentType pt = (PaymentType) dao.find(PaymentType.class, 2);
                        cboPayment.setSelectedItem(pt);
                    } else {
                        txtCusId.setText(null);
                        txtCusName.setText(null);
                    }

                    dao.close();
                    getTraderLastBalance();
                    calculateTotalAmount();
                    break;

                case "MedicineList":
                    Medicine med = (Medicine) dao.find(Medicine.class, ((Medicine) selectObj).getMedId());
                    List<RelationGroup> listRel = dao.findAllHSQL("select o from RelationGroup o where o.medId = '" 
                        + med.getMedId() + "' order by o.relUniqueId");
                    med.setRelationGroupId(listRel);
                    medUp.add(med);

                    //Medicine med1 = new Medicine();
                    //BeanUtils.copyProperties(med, med1);
                    int selectRow = tblPurchase.getSelectedRow();

                    purTableModel.setMed(med, selectRow);
                    break;
                case "PurVouList":
                    VoucherSearch vs = (VoucherSearch) selectObj;
                    currPurVou = (PurHis) dao.find(PurHis.class, vs.getInvNo());

                    if (Util1.getNullTo(currPurVou.getDeleted())) {
                        lblStatus.setText("DELETED");
                        lblStatus.setForeground(Color.red);
                    } else {
                        lblStatus.setText("EDIT");
                        lblStatus.setForeground(Color.BLACK);
                    }
                    setEditStatus(currPurVou.getPurInvId());
                    cboLocation.setSelectedItem(currPurVou.getLocationId());
                    cboVouStatus.setSelectedItem(currPurVou.getVouStatus());
                    cboCurrency.setSelectedItem(currPurVou.getCurrency());
                    cboPayment.setSelectedItem(currPurVou.getPaymentTypeId());

                    txtVouNo.setText(currPurVou.getPurInvId());
                    txtPurDate.setText(DateUtil.toDateStr(currPurVou.getPurDate()));
                    txtDueDate.setText(DateUtil.toDateStr(currPurVou.getDueDate()));
                    txtCusId.setText(currPurVou.getCustomerId().getTraderId());
                    txtCusName.setText(currPurVou.getCustomerId().getTraderName());
                    txtRemark.setText(currPurVou.getRemark());
                    txtVouTotal.setValue(currPurVou.getVouTotal());
                    txtVouPaid.setValue(currPurVou.getPaid());
                    txtVouDiscount.setValue(currPurVou.getDiscount());
                    txtDiscP.setValue(currPurVou.getDiscP());
                    txtTotalExpense.setValue(currPurVou.getExpenseTotal());
                    txtVouBalance.setValue((NumberUtil.NZero(currPurVou.getVouTotal())
                            + NumberUtil.NZero(currPurVou.getExpenseTotal())
                            + NumberUtil.NZero(currPurVou.getTaxAmt()))
                            - (NumberUtil.NZero(currPurVou.getPaid())
                            + NumberUtil.NZero(currPurVou.getDiscount())));
                    //txtVouBalance.setValue(NumberUtil.NZero(currPurVou.getBalance()));
                    txtTaxP.setValue(currPurVou.getTaxP());
                    txtTaxAmt.setValue(currPurVou.getTaxAmt());
                    txtRefNo.setText(currPurVou.getRefNo());

                    listDetail = dao.findAllHSQL(
                            "select o from PurDetailHis o where o.vouNo = '"
                            + currPurVou.getPurInvId() + "' order by o.uniqueId");
                    currPurVou.setPurDetailHis(listDetail);
                    /*if (currPurVou.getPurDetailHis().size() > 0) {
                        currPurVou.setPurDetailHis(currPurVou.getPurDetailHis());
                    }
                    listDetail = currPurVou.getPurDetailHis();*/
                    //This statment is for Outstanding lazy loading
                    List<PurchaseOutstand> listOuts = dao.findAllHSQL(
                            "select o from PurchaseOutstand o where o.vouNo = '" + currPurVou.getPurInvId()
                            + "'"
                    );
                    currPurVou.setListOuts(listOuts);
                    //if (currPurVou.getListOuts().size() > 0) {
                    //}
                    //=============================================

                    /*if (currPurVou.getListExpense().size() > 0) {
                        listExpense = currPurVou.getListExpense();
                    }*/
                    listExpense = dao.findAllHSQL(
                            "select o from PurchaseExpense o where o.vouNo = '"
                            + currPurVou.getPurInvId() + "' order by o.uniqueId"
                    );
                    currPurVou.setListExpense(listExpense);

                    for (PurDetailHis pdh : listDetail) {
                        medUp.add(pdh.getMedId());
                    }
                    purTableModel.setListDetail(listDetail);
                    expTableModel.setListDetail(listExpense);
                    tblPurchase.requestFocusInWindow();
                    dao.close();

                    getTraderLastBalance();
                    calculateTotalAmount();
                    break;
            }
        } catch (Exception ex) {
            log.error("Purchase.select.PurVouList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void initTextBoxAlign() {
        txtVouTotal.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtVouPaid.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtVouDiscount.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtVouBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtCusLastBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTotalExpense.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtDiscP.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTaxP.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtTaxAmt.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtPrvBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);

        txtVouTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouDiscount.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtCusLastBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTotalExpense.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtDiscP.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTaxP.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTaxAmt.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtPrvBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
    }

    private void initTextBoxValue() {
        txtVouTotal.setValue(0.0);
        txtVouPaid.setValue(0.0);
        txtVouDiscount.setValue(0.0);
        txtVouBalance.setValue(0.0);
        txtCusLastBalance.setValue(0.0);
        txtTotalExpense.setValue(0.0);
        txtDiscP.setValue(0.0);
        txtTaxP.setValue(0.0);
        txtTaxAmt.setValue(0.0);
        txtPrvBalance.setValue(0.0);
    }

    // <editor-fold defaultstate="collapsed" desc="addNewRow">
    private void addNewRow() {
        PurDetailHis his = new PurDetailHis();

        his.setMedId(new Medicine());
        listDetail.add(his);
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="hasNewRow">
    private boolean hasNewRow() {
        boolean status = false;
        PurDetailHis purDetailHis = listDetail.get(listDetail.size() - 1);

        String ID = purDetailHis.getMedId().getMedId();
        if (ID == null) {
            status = true;
        }

        return status;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="initPurTable">
    private void initPurTable() {
        try {
            if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
                tblPurchase.setCellSelectionEnabled(true);
            }
            tblPurchase.getTableHeader().setFont(Global.lableFont);
            //Adjust column width
            tblPurchase.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
            tblPurchase.getColumnModel().getColumn(1).setPreferredWidth(150);//Medicine Name
            tblPurchase.getColumnModel().getColumn(2).setPreferredWidth(50);//Relstr
            /*if(Util1.getPropValue("system.purchase.item.expense").equals("Y")){
            
         }else{*/
            tblPurchase.getColumnModel().getColumn(3).setPreferredWidth(50);//Expire Date
            tblPurchase.getColumnModel().getColumn(3).setCellRenderer(new TableDateFieldRenderer());
            //}
            tblPurchase.getColumnModel().getColumn(4).setPreferredWidth(40);//Qty
            tblPurchase.getColumnModel().getColumn(5).setPreferredWidth(30);//Unit
            tblPurchase.getColumnModel().getColumn(6).setPreferredWidth(60);//Pur price
            tblPurchase.getColumnModel().getColumn(7).setPreferredWidth(20);//Discount1
            tblPurchase.getColumnModel().getColumn(8).setPreferredWidth(50);//Discount2
            tblPurchase.getColumnModel().getColumn(9).setPreferredWidth(30);//FOC
            tblPurchase.getColumnModel().getColumn(10).setPreferredWidth(30);//FOC Unit
            tblPurchase.getColumnModel().getColumn(11).setPreferredWidth(50);//Expense
            tblPurchase.getColumnModel().getColumn(12).setPreferredWidth(50);//Unit cost
            tblPurchase.getColumnModel().getColumn(13).setPreferredWidth(15);//Charge Type
            tblPurchase.getColumnModel().getColumn(14).setPreferredWidth(70);//Amount

            addPurTableModelListener();

            //Change JTable cell editor
            tblPurchase.getColumnModel().getColumn(0).setCellEditor(codeCellEditor);
            tblPurchase.getColumnModel().getColumn(5).setCellEditor(
                    new Purchase.PurchaseTableUnitCellEditor());
            tblPurchase.getColumnModel().getColumn(10).setCellEditor(
                    new Purchase.PurchaseTableUnitCellEditor());
            tblPurchase.getColumnModel().getColumn(4).setCellEditor(new BestTableCellEditor());
            tblPurchase.getColumnModel().getColumn(6).setCellEditor(new BestTableCellEditor());
            tblPurchase.getColumnModel().getColumn(7).setCellEditor(new BestTableCellEditor());
            tblPurchase.getColumnModel().getColumn(8).setCellEditor(new BestTableCellEditor());
            tblPurchase.getColumnModel().getColumn(9).setCellEditor(new BestTableCellEditor());

            JComboBox cboChargeType = new JComboBox();
            BindingUtil.BindCombo(cboChargeType, dao.findAll("ChargeType"));
            tblPurchase.getColumnModel().getColumn(13).setCellEditor(new DefaultCellEditor(cboChargeType));

            if (Util1.getPropValue("system.purchase.detail.location").equals("Y")) {
                JComboBox cboLocationCell = new JComboBox();
                BindingUtil.BindCombo(cboLocationCell, dao.findAll("Location"));
                tblPurchase.getColumnModel().getColumn(15).setCellEditor(new DefaultCellEditor(cboLocationCell));
                purTableModel.setLocation((Location) cboLocation.getSelectedItem());
                tblPurchase.getColumnModel().getColumn(15).setPreferredWidth(50);//Location
            } else {
                tblPurchase.getColumnModel().getColumn(15).setPreferredWidth(0);//Location
                tblPurchase.getColumnModel().getColumn(15).setMaxWidth(0);
                tblPurchase.getColumnModel().getColumn(15).setMaxWidth(0);
            }

            tblPurchase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblPurchase.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
                if (tblPurchase.getSelectedRow() < purTableModel.getRowCount()) {
                    lblBrandName.setText(purTableModel.getBrandName(tblPurchase.getSelectedRow()));
                    lblBrandName.setText(purTableModel.getRemark(tblPurchase.getSelectedRow()));
                }
            });
        } catch (Exception ex) {
            log.error("initPurTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="addPurTableModelListener">
    private void addPurTableModelListener() {
        tblPurchase.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int column = e.getColumn();

                if (column >= 0) {
                    //Need to add action for updating table
                    switch (column) {
                        case 0: //Code
                        case 3: //Expire Date
                        case 4: //Qty
                        case 5: //Unit
                        case 6: //Sale price
                        case 7: //Discount1
                        case 8: //Discount2
                        case 12: //Charge Type
                            calculateAmount(tblPurchase.getSelectedRow());
                            break;
                    }
                }
            }
        });
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="calculateAmont">
    private void calculateAmount(int row) {
        PurDetailHis pdh = listDetail.get(row);
        String key = "";

        if (pdh.getUnitId() != null) {
            key = pdh.getMedId().getMedId() + "-" + pdh.getUnitId().getItemUnitCode();
        }

        pdh.setPurSmallestQty(NumberUtil.NZeroFloat(pdh.getQuantity())
                * medUp.getQtyInSmallest(key));
        /*
         * tblPurchase.setValueAt(((NumberUtil.NZero(pdh.getQuantity()) *
         * NumberUtil.NZero(pdh.getPrice())) - (NumberUtil.NZero(pdh.getDiscount1())
         * + NumberUtil.NZero(pdh.getDiscount2()))), row, 12);
         */
        calculateTotalAmount();
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="calculateTotalAmount">
    private void calculateTotalAmount() {
        double totalAmount = 0;
        double totalExp = 0;

        for (PurDetailHis sdh : listDetail) {
            totalAmount += NumberUtil.NZero(sdh.getAmount());
        }
        txtVouTotal.setValue(totalAmount);

        for (PurchaseExpense exp : listExpense) {
            totalExp += NumberUtil.NZero(exp.getExpAmount());
        }
        txtTotalExpense.setValue(totalExp);

        if (chkCashOut.isSelected()) {
            totalExp = 0;
        }

        try {
            PaymentType pt = (PaymentType) cboPayment.getSelectedItem();

            if (pt != null) {
                if (pt.getPaymentTypeId() == 1)//Cash
                {
                    txtVouPaid.setValue(totalAmount + totalExp);
                } else if (pt.getPaymentTypeId() == 2) //Credit
                {
                    txtVouPaid.setValue(0);
                }
            }
        } catch (NullPointerException ex) {
            log.error(ex.toString());
        }

        //double ttlExpense = NumberUtil.NZero(txtTotalExpense.getValue());
        txtVouBalance.setValue((NumberUtil.NZero(txtVouTotal.getValue())
                + totalExp + NumberUtil.NZero(txtTaxAmt.getValue()))
                - (NumberUtil.NZero(txtVouPaid.getValue())
                + NumberUtil.NZero(txtVouDiscount.getValue())));
        txtCusLastBalance.setValue(NumberUtil.NZero(txtVouBalance.getValue()) + cusLastBalance);
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="PurchaseTableUnitCellEditor">
    private class PurchaseTableUnitCellEditor extends AbstractCellEditor implements TableCellEditor {

        JComponent component = null;
        int colIndex = -1;
        // This method is called when a cell value is edited by the user.

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)

            colIndex = vColIndex;

            if (isSelected) {
                // cell (and perhaps other cells) are selected
            }

            String medId = listDetail.get(rowIndex).getMedId().getMedId();

            try {
                JComboBox jb = new JComboBox();

                BindingUtil.BindCombo(jb, medUp.getUnitList(medId));
                component = jb;
            } catch (Exception ex) {
                log.error("PurchaseTabelUnitCellEditor : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
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
            Object obj = ((JComboBox) component).getSelectedItem();

            return obj;
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="FormAction Implementation">
    @Override
    public void save() {
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot save edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            if (tblPurchase.getCellEditor() != null) {
                tblPurchase.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        if (isValidEntry() && purTableModel.isValidEntry() && expTableModel.isValidEntry()) {
            PurchaseConfirmDialog dialog = new PurchaseConfirmDialog(currPurVou,
                    dao, cusLastBalance, chkCashOut.isSelected());

            if (dialog.getConfStatus()) {
                //removeEmptyRow();

                //For BK Pagolay
                try {
                    Date d = new Date();
                    dao.execProc("bkpur",
                            currPurVou.getPurInvId(),
                            DateUtil.toDateTimeStrMYSQL(d),
                            Global.loginUser.getUserId(),
                            Global.machineId,
                            currPurVou.getVouTotal().toString(),
                            currPurVou.getDiscount().toString(),
                            currPurVou.getPaid().toString(),
                            currPurVou.getBalance().toString());
                } catch (Exception ex) {
                    log.error("bkpur : " + ex.getStackTrace()[0].getLineNumber() + " - " + currPurVou.getPurInvId() + " - " + ex);
                } finally {
                    dao.close();
                }

                try {
                    for (PurDetailHis pdh : listDetail) {
                        if (pdh.getMedId() != null) {
                            if (pdh.getMedId().getTypeOption() != null) {
                                if (pdh.getMedId().getTypeOption().equals("PACKING")) {
                                    dao.execProc("insert_packing", currPurVou.getPurInvId(),
                                            pdh.getMedId().getMedId(),
                                            pdh.getUniqueId().toString(), "Purchase",
                                            pdh.getQuantity().toString());
                                }
                            }
                        }

                    }
                } catch (Exception ex) {
                    log.error("insert packing : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                } finally {
                    dao.close();
                }

                try {
                    List<PurDetailHis> listTmp = purTableModel.getListDetail();
                    String vouNo = currPurVou.getPurInvId();
                    dao.open();
                    dao.beginTran();
                    for (PurDetailHis pdh : listTmp) {
                        pdh.setVouNo(vouNo);
                        if (pdh.getPurDetailId() == null) {
                            pdh.setPurDetailId(vouNo + "-" + pdh.getUniqueId().toString());
                        }
                        dao.save1(pdh);
                    }
                    currPurVou.setPurDetailHis(listTmp);

                    List<PurchaseExpense> listExp = expTableModel.getListDetail();
                    for (PurchaseExpense pe : listExp) {
                        pe.setVouNo(vouNo);
                        if (pe.getPurchaseExpId() == null) {
                            pe.setPurchaseExpId(vouNo + "-" + pe.getUniqueId());
                        }
                        dao.save1(pe);
                    }
                    currPurVou.setListExpense(listExp);

                    List<PurchaseOutstand> listPO = getOutstandingItem();
                    Integer uniqueId = 0;
                    for (PurchaseOutstand po : listPO) {
                        po.setVouNo(vouNo);
                        if (po.getOutsId() == null) {
                            uniqueId++;
                            po.setOutsId(vouNo + "-" + uniqueId.toString());
                        } else {
                            String[] tmpIds = po.getOutsId().split("-");
                            if (tmpIds.length > 1) {
                                uniqueId = Integer.parseInt(tmpIds[1]);
                            }
                        }

                        dao.save1(po);
                    }
                    currPurVou.setListOuts(listPO);

                    dao.save1(currPurVou);
                    dao.commit();
                    if (lblStatus.getText().equals("NEW")) {
                        vouEngine.updateVouNo();
                    }
                    log.info("before deleteDetail");
                    deleteDetail();
                    updateVouTotal(currPurVou.getPurInvId());
                    //For upload to account
                    uploadToAccount(currPurVou.getPurInvId());
                    dao.getPro("update_pur_price", currPurVou.getPurInvId());
                    newForm();
                } catch (Exception ex) {
                    dao.rollBack();
                    log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    JOptionPane.showMessageDialog(Util1.getParent(), "Error : " + ex.toString(),
                            "Purchase Save", JOptionPane.ERROR_MESSAGE);
                } finally {
                    dao.close();
                }
            }
        }
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        try {
            if (tblPurchase.getCellEditor() != null) {
                tblPurchase.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Purchase Voucher Search", dao, -1);
        dialog.setPreferredSize(new Dimension(1200, 600));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void delete() {
        Date vouSaleDate = DateUtil.toDate(txtPurDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (Util1.getNullTo(currPurVou.getDeleted())) {
            JOptionPane.showConfirmDialog(Util1.getParent(), "Voucher already deleted.",
                    "Purchase voucher delete", JOptionPane.DEFAULT_OPTION);
        } else {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "Purchase voucher delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                currPurVou.setDeleted(true);
                try {
                    Date d = new Date();
                    dao.execProc("bkpur",
                            currPurVou.getPurInvId(),
                            DateUtil.toDateTimeStrMYSQL(d),
                            Global.loginUser.getUserId(),
                            Global.machineId,
                            currPurVou.getVouTotal().toString(),
                            currPurVou.getDiscount().toString(),
                            currPurVou.getPaid().toString(),
                            currPurVou.getBalance().toString());
                } catch (Exception ex) {
                    log.error("bkpur : " + ex.getStackTrace()[0].getLineNumber() + " - " + currPurVou.getPurInvId() + " - " + ex);
                } finally {
                    dao.close();
                }

                String vouNo = currPurVou.getPurInvId();
                try {
                    dao.execSql("update pur_his set deleted = true, intg_upd_status = null where pur_inv_id = '" + vouNo + "'");
                    //For upload to account
                    uploadToAccount(currPurVou.getPurInvId());
                    newForm();
                } catch (Exception ex) {
                    log.error("delete error : " + ex.getMessage());
                } finally {
                    dao.close();
                }

                //save();
            }
        }
    }

    @Override
    public void deleteCopy() {
        System.out.println("Delete Copy");
    }

    @Override
    public void print() {
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot save edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            if (tblPurchase.getCellEditor() != null) {
                tblPurchase.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        if (isValidEntry() && purTableModel.isValidEntry()
                && expTableModel.isValidEntry()) {
            VouStatus vs = (VouStatus) cboVouStatus.getSelectedItem();

            PurchaseConfirmDialog dialog = new PurchaseConfirmDialog(currPurVou,
                    dao, cusLastBalance, chkCashOut.isSelected());

            if (dialog.getConfStatus()) {
                /*removeEmptyRow();
                currPurVou.setPurDetailHis(listDetail);
                currPurVou.setListExpense(listExpense);
                currPurVou.setListOuts(getOutstandingItem());*/

                //For BK Pagolay
                try {
                    Date d = new Date();
                    dao.execProc("bkpur",
                            currPurVou.getPurInvId(),
                            DateUtil.toDateTimeStrMYSQL(d),
                            Global.loginUser.getUserId(),
                            Global.machineId,
                            currPurVou.getVouTotal().toString(),
                            currPurVou.getDiscount().toString(),
                            currPurVou.getPaid().toString(),
                            currPurVou.getBalance().toString());
                } catch (Exception ex) {
                    log.error("bkpur : " + ex.getStackTrace()[0].getLineNumber() + " - " + currPurVou.getPurInvId() + " - " + ex);
                } finally {
                    dao.close();
                }

                try {
                    List<PurDetailHis> listTmp = purTableModel.getListDetail();
                    String vouNo = currPurVou.getPurInvId();
                    dao.open();
                    dao.beginTran();
                    for (PurDetailHis pdh : listTmp) {
                        pdh.setVouNo(vouNo);
                        if (pdh.getPurDetailId() == null) {
                            pdh.setPurDetailId(vouNo + "-" + pdh.getUniqueId().toString());
                        }
                        dao.save1(pdh);
                    }
                    currPurVou.setPurDetailHis(listTmp);

                    List<PurchaseExpense> listExp = expTableModel.getListDetail();
                    for (PurchaseExpense pe : listExp) {
                        pe.setVouNo(vouNo);
                        if (pe.getPurchaseExpId() == null) {
                            pe.setPurchaseExpId(vouNo + "-" + pe.getUniqueId());
                        }
                        dao.save1(pe);
                    }
                    currPurVou.setListExpense(listExp);

                    List<PurchaseOutstand> listPO = getOutstandingItem();
                    Integer uniqueId = 0;
                    for (PurchaseOutstand po : listPO) {
                        po.setVouNo(vouNo);
                        if (po.getOutsId() == null) {
                            uniqueId++;
                            po.setOutsId(vouNo + "-" + uniqueId.toString());
                        } else {
                            String[] tmpIds = po.getOutsId().split("-");
                            if (tmpIds.length > 1) {
                                uniqueId = Integer.parseInt(tmpIds[1]);
                            }
                        }

                        dao.save1(po);
                    }
                    currPurVou.setListOuts(listPO);

                    dao.save1(currPurVou);
                    dao.commit();
                    if (lblStatus.getText().equals("NEW")) {
                        vouEngine.updateVouNo();
                    }
                    deleteDetail();
                    updateVouTotal(currPurVou.getPurInvId());

                    //For upload to account
                    uploadToAccount(currPurVou.getPurInvId());
                    dao.getPro("update_pur_price", currPurVou.getPurInvId());

                    String reportPath = Util1.getAppWorkFolder()
                            + Util1.getPropValue("report.folder.path");
                    Map<String, Object> params = new HashMap();

                    if (vs.getStatusId() == 2) { //Order
                        reportPath = reportPath + "rptPurchaseOrder";
                        params.put("prm_vou_no", currPurVou.getPurInvId());
                        params.put("prm_cus_name", currPurVou.getCustomerId().getTraderName());
                        params.put("prm_vou_date", currPurVou.getPurDate());

                        if (lblStatus.getText().equals("NEW")) {
                            params.put("prm_vou_status", " ");
                        } else {
                            params.put("prm_vou_status", lblStatus.getText());
                        }

                        ReportUtil.viewReport(reportPath, params, listDetail);
                    } else {
                        List<PurDetailHis> purDetailHis = currPurVou.getPurDetailHis();
                        int total = 0;

                        for (PurDetailHis pdh : purDetailHis) {
                            if (NumberUtil.NZeroFloat(pdh.getQuantity()) > 0) {
                                total += NumberUtil.NZeroFloat(pdh.getQuantity());
                            }
                        }

                        ReportTypeDialog rtd = new ReportTypeDialog(total);

                        if (rtd.getSelType().equals("Default")) {
                            if (Util1.getPropValue("system.purchase.voutype").equals("A4")) {
                                reportPath = reportPath + "rptPurVouA4";
                            } else {
                                reportPath = reportPath + "rptPurVou";
                            }
                            params.put("pur_inv_id", currPurVou.getPurInvId());
                            params.put("prvBal", txtPrvBalance.getText());
                            params.put("lasBal", txtCusLastBalance.getText());
                            params.put("compName", Util1.getPropValue("report.company.name"));
                            params.put("comp_address", Util1.getPropValue("report.address"));
                            params.put("phone", Util1.getPropValue("report.phone"));

                            ReportUtil.viewReport(reportPath, params, dao.getConnection());
                        } else if (rtd.getSelType().equals("Barcode")) {
                            insertBarcodeFilter();
                        }
                    }

                    newForm();
                } catch (Exception ex) {
                    dao.rollBack();
                    log.error("print : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    JOptionPane.showMessageDialog(Util1.getParent(), "Error : " + ex.toString(),
                            "Purchase Print", JOptionPane.ERROR_MESSAGE);
                } finally {
                    dao.close();
                }
            }
        }
    }
    // </editor-fold>

    private void insertBarcodeFilter() {
        List<PurDetailHis> purDetailHis = currPurVou.getPurDetailHis();
        List<BarcodeFilter> listBarcodeFilter = new ArrayList();

        for (PurDetailHis pdh : purDetailHis) {
            if (NumberUtil.NZeroFloat(pdh.getQuantity()) > 0) {
                for (int i = 0; i < pdh.getQuantity(); i++) {
                    BarcodeFilter bf = new BarcodeFilter();

                    bf.setItemId(pdh.getMedId().getMedId());
                    bf.setUserId(Global.machineId);
                    listBarcodeFilter.add(bf);
                }
            }
        }

        try {
            String strSQL = "delete from tmp_barcode_filter where user_id = '"
                    + Global.machineId + "'";

            dao.execSql(strSQL);
            dao.saveBatch(listBarcodeFilter);

            Map<String, Object> params = new HashMap();
            String reportPath = Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path")
                    + "rptBarCode";

            params.put("user_id", Global.machineId);
            params.put("compName", Util1.getPropValue("report.company.name"));

            ReportUtil.viewReport(reportPath, params, dao.getConnection());
        } catch (Exception ex) {
            log.error("insertBarcodeFilter : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    public void setFocus() {
        txtCusId.requestFocus();
    }

    private void setEditStatus(String invId) {
        try {
            if (!Util1.hashPrivilege("CanEditPurchaseCheckPoint")) {
                List list = dao.findAllSQLQuery(
                        "select * from c_bk_pur_his where pur_inv_id = '" + invId + "'");
                if (list != null) {
                    canEdit = list.isEmpty();
                } else {
                    canEdit = true;
                }
            } else {
                canEdit = true;
            }
        } catch (Exception ex) {
            log.error("setEditStatus : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        focusOrder.add(txtCusId);
        focusOrder.add(cboLocation);
        focusOrder.add(cboPayment);
        focusOrder.add(cboVouStatus);
        focusOrder.add(tblPurchase);

        focusPolicy = new BestAppFocusTraversalPolicy(focusOrder);
    }

    // <editor-fold defaultstate="collapsed" desc="AddFocusMoveKey">
    private void AddFocusMoveKey() {
        Set backwardKeys = getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
        Set newBackwardKeys = new HashSet(backwardKeys);

        Set forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set newForwardKeys = new HashSet(forwardKeys);

        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

        newBackwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));

        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);
        setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, newBackwardKeys);
    }//</editor-fold>

    private boolean isValidEntry() {
        Date vouSaleDate = DateUtil.toDate(txtPurDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean status = true;

        if (!Util1.hashPrivilege("CanEditPurchaseCheckPoint")) {
            if (lblStatus.getText().equals("NEW")) {
                try {
                    long cnt = dao.getRowCount("select count(*) from c_bk_pur_his where sale_date >= '"
                            + DateUtil.toDateStrMYSQL(txtPurDate.getText()) + "'");
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

        if (txtVouNo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid voucher no.",
                    "Invalid Voucher ID.", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (currPurVou.getCustomerId() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Supplier cannot be blank.",
                    "No supplier.", JOptionPane.ERROR_MESSAGE);
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
            try {
                if (tblPurchase.getCellEditor() != null) {
                    tblPurchase.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }
            currPurVou.setPurInvId(txtVouNo.getText());
            currPurVou.setDueDate(DateUtil.toDate(txtDueDate.getText()));
            currPurVou.setLocationId((Location) cboLocation.getSelectedItem());
            currPurVou.setPaymentTypeId((PaymentType) cboPayment.getSelectedItem());
            currPurVou.setVouStatus((VouStatus) cboVouStatus.getSelectedItem());
            currPurVou.setRemark(txtRemark.getText());
            currPurVou.setVouTotal(NumberUtil.getDouble(txtVouTotal.getText()));
            currPurVou.setPaid(NumberUtil.getDouble(txtVouPaid.getText()));
            currPurVou.setDiscount(NumberUtil.getDouble(txtVouDiscount.getText()));
            currPurVou.setExpenseTotal(NumberUtil.getDouble(txtTotalExpense.getText()));
            currPurVou.setBalance(NumberUtil.getDouble(txtVouBalance.getText()));
            currPurVou.setDiscP(NumberUtil.getDouble(txtDiscP.getText()));
            currPurVou.setTaxP(NumberUtil.getDouble(txtTaxP.getText()));
            currPurVou.setTaxAmt(NumberUtil.getDouble(txtTaxAmt.getText()));
            currPurVou.setCashOut(chkCashOut.isSelected());
            currPurVou.setRefNo(txtRefNo.getText());
            currPurVou.setDeleted(Util1.getNullTo(currPurVou.getDeleted()));
            if (lblStatus.getText().equals("NEW")) {
                currPurVou.setDeleted(false);
                currPurVou.setPurDate(DateUtil.toDateTime(txtPurDate.getText()));
            } else {
                Date tmpDate = DateUtil.toDate(txtPurDate.getText());
                if (!DateUtil.isSameDate(tmpDate, currPurVou.getPurDate())) {
                    currPurVou.setPurDate(DateUtil.toDateTime(txtPurDate.getText()));
                }
            }

            currPurVou.setCurrency((Currency) cboCurrency.getSelectedItem()); //Need to change

            if (lblStatus.getText().equals("NEW")) {
                currPurVou.setCreatedBy(Global.loginUser);
                currPurVou.setSession(Global.sessionId);
            } else {
                currPurVou.setUpdatedBy(Global.loginUser);
                currPurVou.setUpdatedDate(DateUtil.toDate(DateUtil.getTodayDateStr()));
            }

            try {
                if (tblPurchase.getCellEditor() != null) {
                    tblPurchase.getCellEditor().stopCellEditing();
                }
                if (tblExpense.getCellEditor() != null) {
                    tblExpense.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }

            if (NumberUtil.NZeroL(currPurVou.getExrId()) == 0) {
                Long exrId = getExchangeId(txtPurDate.getText(), currPurVou.getCurrency().getCurrencyCode());
                currPurVou.setExrId(exrId);
            }
        }

        return status;
    }

    private void assignDefaultValueModel() {
        listDetail = ObservableCollections.observableList(new ArrayList<PurDetailHis>());
        purTableModel.setListDetail(listDetail);

        listExpense = ObservableCollections.observableList(new ArrayList<PurchaseExpense>());
        expTableModel.setListDetail(listExpense);

        txtPurDate.setText(DateUtil.getTodayDateStr());
    }

    private void assignDefaultValue() {
        Object tmpObj;

        try {
            tmpObj = dao.find(Trader.class, Util1.getPropValue("system.default.supplier"));
            if (tmpObj != null) {
                selected("CustomerList", tmpObj);
            }
        } catch (Exception ex) {
            log.error("assignDefaultValue : " + ex.getMessage());
        } finally {
            dao.close();
        }
        tmpObj = Util1.getDefaultValue("Currency");
        if (tmpObj != null) {
            cboCurrency.setSelectedItem(tmpObj);
        }

        if (prvLocation != null) {
            tmpObj = prvLocation;
        } else {
            tmpObj = Util1.getDefaultValue("Location");
        }
        if (tmpObj != null) {
            cboLocation.setSelectedItem(tmpObj);
            Object l = cboLocation.getSelectedItem();
            if (l == null) {
                if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                    if (cboLocation.getItemCount() > 0) {
                        cboLocation.setSelectedIndex(0);
                    }
                } else {
                    cboLocation.setSelectedItem(tmpObj);
                }
            }
        }

        if (prvPymet != null) {
            tmpObj = prvPymet;
        } else {
            tmpObj = Util1.getDefaultValue("PaymentType");
        }
        if (tmpObj != null) {
            cboPayment.setSelectedItem(tmpObj);
        }

        tmpObj = Util1.getDefaultValue("VouStatus");
        if (tmpObj != null) {
            cboVouStatus.setSelectedItem(tmpObj);
        }
    }

    @Override
    public void getMedInfo(String medCode) {
        Medicine medicine;

        try {
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
                System.out.println("Blank medicine code.");
            }
        } catch (Exception ex) {
            log.error("getMedInfo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private Action actionTblSaleEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblPurchase.getCellEditor() != null) {
                    tblPurchase.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            int row = tblPurchase.getSelectedRow();
            int col = tblPurchase.getSelectedColumn();

            PurDetailHis sdh = listDetail.get(row);

            if (col == 0 && sdh.getMedId().getMedId() != null) {
                tblPurchase.setColumnSelectionInterval(3, 3); //Move to Qty
            } else if (col == 1 && sdh.getMedId().getMedId() != null) {
                tblPurchase.setColumnSelectionInterval(3, 3); //Move to Qty
            } else if (col == 2 && sdh.getMedId().getMedId() != null) {
                tblPurchase.setColumnSelectionInterval(4, 4); //Move to Qty
            } else if (col == 3 && sdh.getMedId().getMedId() != null) {
                tblPurchase.setColumnSelectionInterval(4, 4); //Move to Qty
            } else if (col == 4 && sdh.getMedId().getMedId() != null) {
                tblPurchase.setColumnSelectionInterval(6, 6); //Move to Unit
            } else if (col == 5 && sdh.getMedId().getMedId() != null) {
                tblPurchase.setColumnSelectionInterval(6, 6); //Move to Sale Price
            } else if (col == 6 && sdh.getMedId().getMedId() != null) {
                tblPurchase.setColumnSelectionInterval(7, 7); //Move to Discount
            } else if (col == 7 && sdh.getMedId().getMedId() != null) {
                tblPurchase.setColumnSelectionInterval(8, 8); //Move to Charge Type
            } else if (col == 8 && sdh.getMedId().getMedId() != null) {
                tblPurchase.setColumnSelectionInterval(9, 9); //Move to FOC
            } else if (col == 9 && sdh.getMedId().getMedId() != null) {
                tblPurchase.setColumnSelectionInterval(11, 11); //Move to Expense
            } else if (col == 11 && sdh.getMedId().getMedId() != null) {
                if ((row + 1) <= listDetail.size()) {
                    tblPurchase.setRowSelectionInterval(row + 1, row + 1);
                }
                tblPurchase.setColumnSelectionInterval(0, 0); //Move to Code
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

            PurchaseExpense pe = listExpense.get(row);

            if (col == 0 && pe.getExpType() != null) {
                tblExpense.setColumnSelectionInterval(1, 1); //Amount
            } else if (col == 1 && pe.getExpType() != null) {
                if ((row + 1) < listExpense.size()) {
                    tblExpense.setRowSelectionInterval(row + 1, row + 1);
                }
                tblExpense.setColumnSelectionInterval(0, 0); //Move to Exp Type
            }
        }
    };

    // <editor-fold defaultstate="collapsed" desc="initExpenseTable">
    private void initExpenseTable() {
        try {
            if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
                tblExpense.setCellSelectionEnabled(true);
            }
            //Adjust column width
            tblExpense.getColumnModel().getColumn(0).setPreferredWidth(50); //Expense Date
            tblExpense.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
            tblExpense.getColumnModel().getColumn(1).setPreferredWidth(150);//Expense Type
            tblExpense.getColumnModel().getColumn(2).setPreferredWidth(60);//Amount

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
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="addExpenseTableModelListener">
    private void addExpenseTableModelListener() {
        tblExpense.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int column = e.getColumn();

                if (column >= 0 && e.getType() == 0) {
                    //Need to add action for updating table
                    switch (column) {
                        case 2: //Amount
                            Double expTotal = 0d;

                            for (PurchaseExpense exp : listExpense) {
                                expTotal += NumberUtil.NZero(exp.getExpAmount());
                            }

                            txtTotalExpense.setValue(expTotal);
                            calculateTotalAmount();
                            break;
                    }
                }
            }
        });
    }// </editor-fold>

    private void getCustomer() {
        Customer cus = null;

        if (txtCusId.getText() != null && !txtCusId.getText().isEmpty()) {
            try {
                dao.open();
                String traderId = txtCusId.getText().trim().toUpperCase();
                if (Util1.getPropValue("system.purchase.emitted.prifix").equals("Y")) {
                    if (!traderId.contains("SUP")) {
                        traderId = "SUP" + traderId;
                    }
                }
                cus = (Customer) dao.find(Customer.class, traderId);
                dao.close();
            } catch (Exception ex) {
                log.error("getCustomer : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        }

        if (cus == null && !txtCusId.getText().isEmpty()) {
            getCustomerList();
        } else {
            selected("CustomerList", cus);
            tblPurchase.requestFocusInWindow();
        }
    }

    private void getCustomerList() {
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Supplier List", dao, -1);
        dialog.setPreferredSize(new Dimension(1200, 600));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void removeEmptyRow() {
        listDetail.remove(listDetail.size() - 1);
        listExpense.remove(listExpense.size() - 1);
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

    private void formActionKeyMapping(JComponent jc) {
        //Save
        jc.getInputMap().put(KeyStroke.getKeyStroke("F5"), "F5-Action");
        jc.getActionMap().put("F5-Action", actionSave);

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

        //Price Change
        keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK);
        jc.getInputMap().put(keyStroke, "Ctrl-P-Action");
        jc.getActionMap().put("Ctrl-P-Action", actionPriceChange);

        //Print 
        jc.getInputMap().put(KeyStroke.getKeyStroke("F7"), "F7-Action");
        jc.getActionMap().put("F7-Action", actionPrint);
    }
    private Action actionSave = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            save();
        }
    };// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionHistory">
    private Action actionHistory = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            history();
        }
    };// </editor-fold>
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
    private Action actionPriceChange = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            PriceChangeDialog dialogPC = new PriceChangeDialog();
            dialogPC.addPanel(purTableModel.getMedIdList());
            dialogPC.setLocationRelativeTo(null);
            dialogPC.setVisible(true);
        }
    };
    private Action actionPrint = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print();
        }
    };

    private void getTraderLastBalance() {
        String strTodayDateTime = getTranDateTime();

        //Trader last balance
        if (strTodayDateTime != null) {
            if (!strTodayDateTime.isEmpty()) {
                String strTrdOpt;
                try {
                    Trader1 t1 = (Trader1) dao.find(Trader1.class, currPurVou.getCustomerId().getTraderId());
                    if (t1.getDiscriminator().equals("S")) {
                        strTrdOpt = "SUP";
                    } else {
                        strTrdOpt = "CUS";
                    }
                    Currency curr = (Currency) cboCurrency.getSelectedItem();
                    ResultSet resultSet = dao.getPro("trader_last_balance",
                            currPurVou.getCustomerId().getTraderId(), strTrdOpt,
                            strTodayDateTime, curr.getCurrencyCode());
                    if (resultSet != null) {
                        resultSet.next();
                        cusLastBalance = resultSet.getDouble("var_last_balance");
                        /*if (strTrdOpt.equals("SUP")) {
                         cusLastBalance = cusLastBalance * -1;
                         }*/

                        //txtCusLastBalance.setValue(cusLastBalance);
                        txtPrvBalance.setValue(cusLastBalance);

                        if (resultSet.getDate("var_last_sale_date") != null) {
                            String strLastSaleDate = DateUtil.toDateStr(resultSet.getDate("var_last_sale_date")) + " Balance :";
                            lblPrvBalance.setText(strLastSaleDate);
                        } else {
                            lblPrvBalance.setText("Balance : ");
                        }
                    }
                } catch (SQLException ex) {
                    log.error("getTraderLastBalance : " + ex.toString());
                } catch (Exception ex1) {
                    log.error("getTraderLastBalance1 : " + ex1.getMessage());
                } finally {
                    dao.close();
                }
            }
        }
    }

    private String getTranDateTime() {
        String strTodayDateTime = null;

        switch (lblStatus.getText()) {
            case "NEW":
                strTodayDateTime = DateUtil.getTodayDateTimeStrMYSQL();
                break;
            case "EDIT":
                strTodayDateTime = DateUtil.toDateTimeStrMYSQL(currPurVou.getPurDate());
                break;
        }

        return strTodayDateTime;
    }

    private void purchaseOutstanding() {
        if (listDetail.size() > 1) {
            PurchaseOutstandingDialog dialog = new PurchaseOutstandingDialog(listDetail,
                    medUp, currPurVou.getListOuts());
            currPurVou.setListOuts(dialog.getOutsList());
        }
    }

    private void itemPromo() {
        if (listDetail.size() > 1) {
            PurchaseItemPromoDialog dialog = new PurchaseItemPromoDialog(listDetail,
                    medUp);
        }
    }

    private List<PurchaseOutstand> getOutstandingItem() {
        String className = "com.cv.app.pharmacy.database.entity.PurchaseOutstand";
        String strSQL = "SELECT * FROM " + className
                + " WHERE outsQtySmall > 0 ";

        List<PurchaseOutstand> list = JoSQLUtil.getResult(strSQL, currPurVou.getListOuts());

        return list;
    }

    private void deleteOutstand(String medId, Date expDate, int chargeType) {
        List<PurchaseOutstand> listOut = currPurVou.getListOuts();
        String option;

        if (chargeType == 2) {
            option = "Purchase-Foc";
        } else {
            option = "Purchase";
        }

        if (listOut != null) {
            if (!listOut.isEmpty()) {
                for (int i = 0; i < listOut.size(); i++) {
                    PurchaseOutstand po = listOut.get(i);

                    if (po.getItemId().getMedId().equals(medId)
                            && po.getOutsOption().equals(option)
                            && po.getExpDate() == expDate && NumberUtil.NZeroL(po.getOutsId()) > 0) {

                        if (deleteOutstandList == null) {
                            deleteOutstandList = "'" + po.getOutsId() + "'";
                        } else {
                            deleteOutstandList = deleteOutstandList + ",'" + po.getOutsId() + "'";
                        }

                        listOut.remove(i);
                        i = listOut.size();
                    }
                }
            }
        }
    }

    private void deleteDetail() {
        String deleteSQL;

        //All detail section need to explicity delete
        //because of save function only delete to join table
        try {
            deleteSQL = purTableModel.getDeleteSql();
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
                PurDetailHis pdh = listDetail.get(i);
                Medicine med = pdh.getMedId();

                if (med != null) {
                    if (med.getMedId().contains(str) || med.getMedName().contains(str)) {
                        indexFound = i;
                        i = listDetail.size();
                    }
                }
            }

            if (indexFound != -1) {
                tblPurchase.setRowSelectionInterval(indexFound, indexFound);
                Rectangle rect = tblPurchase.getCellRect(tblPurchase.getSelectedRow(), 0, true);
                tblPurchase.scrollRectToVisible(rect);
            }
        }
    }

    private void uploadToAccount(String vouNo) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            String rootUrl = Util1.getPropValue("system.intg.api.url");
            if (!rootUrl.isEmpty() && !rootUrl.equals("-")) {
                try (CloseableHttpClient httpClient = createHttpClientWithTimeouts()) {
                    String url = rootUrl + "/purchase";
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
            dao.execSql("update pur_his set intg_upd_status = null where pur_inv_id = '" + vouNo + "'");
        } catch (Exception ex) {
            log.error("purchase updateNull: " + ex.getMessage());
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

    private void updateVouTotal(String vouNo) {
        String strSql = "update pur_his ph\n"
                + "join (select vou_no, sum(ifnull(pur_amount,0)) as ttl_amt \n"
                + "from pur_detail_his where vou_no = '" + vouNo + "' group by vou_no) pd\n"
                + "on ph.pur_inv_id = pd.vou_no set ph.vou_total = pd.ttl_amt\n"
                + "where ph.pur_inv_id = '" + vouNo + "'";
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtPurDate = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDueDate = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtCusId = new javax.swing.JTextField();
        txtCusName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cboPayment = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        txtRefNo = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        cboVouStatus = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox();
        txtFilter = new javax.swing.JTextField();
        butPromo = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPurchase = new javax.swing.JTable(purTableModel);
        jPanel3 = new javax.swing.JPanel();
        lblStatus = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        txtDiscP = new javax.swing.JFormattedTextField();
        jLabel17 = new javax.swing.JLabel();
        txtVouPaid = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtVouDiscount = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        txtTaxAmt = new javax.swing.JFormattedTextField();
        txtTaxP = new javax.swing.JFormattedTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        txtTotalExpense = new javax.swing.JFormattedTextField();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblExpense = new javax.swing.JTable(expTableModel);
        chkCashOut = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        txtCusLastBalance = new javax.swing.JFormattedTextField();
        txtPrvBalance = new javax.swing.JFormattedTextField();
        lblPrvBalance = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        lblBrandName = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        lblRemark = new javax.swing.JLabel();
        butOutstanding = new javax.swing.JButton();
        butItemPromo = new javax.swing.JButton();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Pur Date ");

        txtPurDate.setEditable(false);
        txtPurDate.setFont(Global.textFont);
        txtPurDate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtPurDateFocusGained(evt);
            }
        });
        txtPurDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPurDateMouseClicked(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Due Date");

        txtDueDate.setEditable(false);
        txtDueDate.setFont(Global.textFont);
        txtDueDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDueDateMouseClicked(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Supplier No/Name");

        txtCusId.setFont(Global.textFont);
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

        txtCusName.setEditable(false);
        txtCusName.setFont(Global.textFont);
        txtCusName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusNameMouseClicked(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Remark");

        txtRemark.setFont(Global.textFont);
        txtRemark.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtRemarkFocusGained(evt);
            }
        });

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Payment ");

        cboPayment.setFont(Global.textFont);
        cboPayment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPaymentActionPerformed(evt);
            }
        });

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Location");

        cboLocation.setFont(Global.textFont);
        cboLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLocationActionPerformed(evt);
            }
        });

        txtRefNo.setFont(Global.textFont);

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Ref No");

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Vou Status");

        cboVouStatus.setFont(Global.textFont);

        jLabel16.setFont(Global.lableFont);
        jLabel16.setText("Currency");

        cboCurrency.setFont(Global.textFont);

        txtFilter.setFont(Global.textFont);
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

        butPromo.setText("Promo");
        butPromo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPromoActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtRemark)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 141, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                                .add(jLabel1)
                                .add(18, 18, 18)
                                .add(txtVouNo))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                                .add(jLabel4)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(txtCusId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 85, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtCusName)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jLabel2)
                                .add(18, 18, 18)
                                .add(txtPurDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(butPromo)
                                .add(0, 0, Short.MAX_VALUE)))))
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3)
                    .add(jLabel14)
                    .add(jLabel16))
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(txtRefNo)
                        .add(cboCurrency, 0, 117, Short.MAX_VALUE))
                    .add(txtDueDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel7)
                    .add(jLabel15)
                    .add(jLabel6))
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cboPayment, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, cboLocation, 0, 125, Short.MAX_VALUE)
                        .add(cboVouStatus, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .add(12, 12, 12))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(txtVouNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(txtPurDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(txtDueDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7)
                    .add(cboLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(butPromo))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtCusId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtCusName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4)
                    .add(jLabel6)
                    .add(cboPayment, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtRefNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel14))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(txtRemark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel15)
                    .add(cboVouStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel16)
                    .add(cboCurrency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        tblPurchase.setFont(Global.textFont);
        tblPurchase.setModel(purTableModel);
        tblPurchase.setToolTipText("");
        tblPurchase.setCellSelectionEnabled(true);
        tblPurchase.setRowHeight(23);
        tblPurchase.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tblPurchaseFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblPurchaseFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(tblPurchase);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
        );

        lblStatus.setFont(new java.awt.Font("Zawgyi-One", 0, 24)); // NOI18N
        lblStatus.setText("DELETED");

        txtVouTotal.setEditable(false);
        txtVouTotal.setFont(Global.textFont);

        txtDiscP.setEditable(false);
        txtDiscP.setFont(Global.textFont);

        jLabel17.setText("%");

        txtVouPaid.setEditable(false);
        txtVouPaid.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Paid :");

        jLabel8.setFont(Global.lableFont);
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("Total :");

        txtVouDiscount.setEditable(false);
        txtVouDiscount.setFont(Global.textFont);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Discount :");

        txtTaxAmt.setEditable(false);
        txtTaxAmt.setFont(Global.textFont);

        txtTaxP.setEditable(false);
        txtTaxP.setFont(Global.textFont);

        jLabel18.setText("%");

        jLabel19.setFont(Global.lableFont);
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText("Tax  :");

        txtVouBalance.setEditable(false);
        txtVouBalance.setFont(Global.textFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("Vou Balance :");

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .add(0, 20, Short.MAX_VALUE)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel9)
                    .add(jLabel8)
                    .add(jLabel10)
                    .add(jLabel19)
                    .add(jLabel11))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(txtDiscP)
                            .add(txtTaxP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 51, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel17)
                            .add(jLabel18))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txtTaxAmt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 81, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txtVouDiscount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 81, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(txtVouTotal)
                    .add(txtVouBalance)
                    .add(txtVouPaid)))
        );

        jPanel4Layout.linkSize(new java.awt.Component[] {jLabel10, jLabel11, jLabel19, jLabel8, jLabel9}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtVouTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtVouDiscount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtDiscP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel10)
                    .add(jLabel17))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtTaxAmt, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtTaxP, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel19)
                    .add(jLabel18))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtVouPaid, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtVouBalance, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel11)))
        );

        jPanel4Layout.linkSize(new java.awt.Component[] {txtDiscP, txtVouDiscount}, org.jdesktop.layout.GroupLayout.VERTICAL);

        txtTotalExpense.setEditable(false);
        txtTotalExpense.setFont(Global.textFont);

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Exp Total");

        tblExpense.setFont(Global.textFont);
        tblExpense.setModel(expTableModel);
        tblExpense.setRowHeight(23);
        jScrollPane2.setViewportView(tblExpense);

        chkCashOut.setText("Cash Out");
        chkCashOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCashOutActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(chkCashOut)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(txtTotalExpense, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jPanel5Layout.createSequentialGroup()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 393, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(txtTotalExpense, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel13))
                    .add(chkCashOut))
                .add(3, 3, 3))
        );

        jLabel12.setFont(Global.lableFont);
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("Sup Last Balance :");

        txtCusLastBalance.setEditable(false);
        txtCusLastBalance.setFont(Global.textFont);

        txtPrvBalance.setEditable(false);
        txtPrvBalance.setFont(Global.textFont);

        lblPrvBalance.setFont(Global.lableFont);
        lblPrvBalance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPrvBalance.setText(":");

        jLabel20.setFont(Global.lableFont);
        jLabel20.setText("Brand Name :");

        lblBrandName.setFont(Global.textFont);
        lblBrandName.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel21.setFont(Global.lableFont);
        jLabel21.setText("Remark :");

        lblRemark.setFont(Global.textFont);
        lblRemark.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        org.jdesktop.layout.GroupLayout jPanel6Layout = new org.jdesktop.layout.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
            .add(jPanel6Layout.createSequentialGroup()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblPrvBalance, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jLabel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtPrvBalance)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, txtCusLastBalance, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)))
            .add(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(jLabel20)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lblBrandName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 181, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel6Layout.createSequentialGroup()
                        .add(jLabel21, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lblRemark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 181, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6Layout.createSequentialGroup()
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtPrvBalance, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblPrvBalance))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtCusLastBalance, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel12))
                .add(18, 18, 18)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel20, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblBrandName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jLabel21, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(lblRemark, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        butOutstanding.setFont(Global.textFont);
        butOutstanding.setText("Outstanding");
        butOutstanding.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOutstandingActionPerformed(evt);
            }
        });

        butItemPromo.setText("Item Promo");
        butItemPromo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butItemPromoActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lblStatus, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(butItemPromo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(butOutstanding, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 381, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(lblStatus)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(butOutstanding)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(butItemPromo))
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtPurDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPurDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtPurDate.setText(strDate);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.setPeriod(DateUtil.getPeriod(txtPurDate.getText()));
                    genVouNo();
                }
            }
        }
    }//GEN-LAST:event_txtPurDateMouseClicked

    private void txtDueDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDueDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtDueDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtDueDateMouseClicked

    private void txtCusIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusIdActionPerformed
        if (txtCusId.getText() == null || txtCusId.getText().isEmpty()) {
            txtCusName.setText(null);
            currPurVou.setCustomerId(null);
        } else {
            getCustomer();
        }
    }//GEN-LAST:event_txtCusIdActionPerformed

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == mouseClick) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusNameMouseClicked

    private void txtCusIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusIdFocusLost
        //getCustomer();
    }//GEN-LAST:event_txtCusIdFocusLost

    private void cboPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPaymentActionPerformed
        calculateTotalAmount();
    }//GEN-LAST:event_cboPaymentActionPerformed

  private void txtCusIdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusIdMouseClicked
      if (evt.getClickCount() == 2) {
          getCustomerList();
      }
  }//GEN-LAST:event_txtCusIdMouseClicked

    private void chkCashOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCashOutActionPerformed
        calculateTotalAmount();
    }//GEN-LAST:event_chkCashOutActionPerformed

    private void butOutstandingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOutstandingActionPerformed
        purchaseOutstanding();
    }//GEN-LAST:event_butOutstandingActionPerformed

    private void cboLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLocationActionPerformed
        if (!bindStatus) {
            Location location = (Location) cboLocation.getSelectedItem();
            purTableModel.setLocation(location);
            codeCellEditor.setLocationId(location.getLocationId());
        }
    }//GEN-LAST:event_cboLocationActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased

    }//GEN-LAST:event_txtFilterKeyReleased

    private void txtFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilterActionPerformed
        finder(txtFilter.getText());
    }//GEN-LAST:event_txtFilterActionPerformed

    private void txtCusIdFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusIdFocusGained
        focusCtrlName = "txtCusId";
        txtCusId.selectAll();
    }//GEN-LAST:event_txtCusIdFocusGained

    private void txtPurDateFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPurDateFocusGained
        focusCtrlName = "txtPurDate";
        txtPurDate.selectAll();
    }//GEN-LAST:event_txtPurDateFocusGained

    private void tblPurchaseFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblPurchaseFocusGained
        focusCtrlName = "tblPurchase";
        int selRow = tblPurchase.getSelectedRow();

        if (selRow == -1) {
            //tblPurchase.editCellAt(0, 0);
            tblPurchase.changeSelection(0, 0, false, false);
        }
    }//GEN-LAST:event_tblPurchaseFocusGained

    private void txtRemarkFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRemarkFocusGained
        focusCtrlName = "txtRemark";
    }//GEN-LAST:event_txtRemarkFocusGained

    private void butPromoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPromoActionPerformed
        PromoVou dialog = new PromoVou(currPurVou);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }//GEN-LAST:event_butPromoActionPerformed

    private void butItemPromoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butItemPromoActionPerformed
        // TODO add your handling code here:
        itemPromo();
    }//GEN-LAST:event_butItemPromoActionPerformed

    private void tblPurchaseFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblPurchaseFocusLost
        /*try{
            if(tblPurchase.getCellEditor() != null){
                tblPurchase.getCellEditor().stopCellEditing();
            }
        }catch(Exception ex){
            
        }*/
    }//GEN-LAST:event_tblPurchaseFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butItemPromo;
    private javax.swing.JButton butOutstanding;
    private javax.swing.JButton butPromo;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JComboBox cboVouStatus;
    private javax.swing.JCheckBox chkCashOut;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblBrandName;
    private javax.swing.JLabel lblPrvBalance;
    private javax.swing.JLabel lblRemark;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblExpense;
    private javax.swing.JTable tblPurchase;
    private javax.swing.JTextField txtCusId;
    private javax.swing.JFormattedTextField txtCusLastBalance;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JFormattedTextField txtDiscP;
    private javax.swing.JFormattedTextField txtDueDate;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JFormattedTextField txtPrvBalance;
    private javax.swing.JFormattedTextField txtPurDate;
    private javax.swing.JTextField txtRefNo;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtTaxAmt;
    private javax.swing.JFormattedTextField txtTaxP;
    private javax.swing.JFormattedTextField txtTotalExpense;
    private javax.swing.JFormattedTextField txtVouBalance;
    private javax.swing.JFormattedTextField txtVouDiscount;
    private javax.swing.JFormattedTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouPaid;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables
}
