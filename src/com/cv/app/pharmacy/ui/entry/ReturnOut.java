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
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.RetOutDetailHis;
import com.cv.app.pharmacy.database.entity.RetOutHis;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.RetOutTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.util.MedListDialog1;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.swing.*;
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
public class ReturnOut extends javax.swing.JPanel implements SelectionObserver, FormAction,
        MedInfo, KeyPropagate {

    static Logger log = Logger.getLogger(ReturnOut.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private GenVouNoImpl vouEngine = null;
    private List<RetOutDetailHis> listDetail
            = ObservableCollections.observableList(new ArrayList<RetOutDetailHis>());
    //private boolean status = true;
    private MedicineUP medUp = new MedicineUP(dao);
    private BestAppFocusTraversalPolicy focusPolicy;
    private RetOutTableModel retOutTableModel = new RetOutTableModel(listDetail, dao,
            medUp, this);
    private RetOutHis currRetOut = new RetOutHis();
    private boolean canEdit = true;
    private int mouseClick = 2;

    /**
     * Creates new form Sale
     */
    public ReturnOut() {
        initComponents();

        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
            dao.open();
            initCombo();
            dao.close();
        } catch (Exception ex) {
            log.error("ReturnOut : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        actionMapping();
        initRetOutTable();
        addRetOutTableModelListener();
        initTextBoxAlign();
        initTextBoxValue();

        txtReturnOutDate.setText(DateUtil.getTodayDateStr());
        vouEngine = new GenVouNoImpl(dao, "RetOut", DateUtil.getPeriod(txtReturnOutDate.getText()));
        genVouNo();
        addNewRow();

        //applyFocusPolicy();
        //AddFocusMoveKey();
        //this.setFocusTraversalPolicy(focusPolicy);
        retOutTableModel.setParent(tblRetOut);
        lblStatus.setText("NEW");
        assignDefaultValue();
        //F3 trader code
        txtCusId.registerKeyboardAction(traderF3Action, KeyStroke.getKeyStroke("F3"),
                JComponent.WHEN_FOCUSED);

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

        log.info("Return Out");
    }

    private void genVouNo() {
        try {
            String vouNo = vouEngine.getVouNo();
            txtVouNo.setText(vouNo);

            List<RetOutHis> listROH = dao.findAllHSQL(
                    "select o from RetOutHis o where o.retOutId = '" + txtVouNo.getText() + "'");
            if (listROH != null) {
                if (!listROH.isEmpty()) {
                    vouEngine.updateVouNo();
                    vouNo = vouEngine.getVouNo();
                    txtVouNo.setText(vouNo);
                    listROH = null;
                    listROH = dao.findAllHSQL(
                            "select o from RetOutHis o where o.retOutId = '" + txtVouNo.getText() + "'");
                    if (listROH != null) {
                        if (!listROH.isEmpty()) {
                            log.error("Duplicate purchase vour error : " + txtVouNo.getText() + " @ "
                                    + txtReturnOutDate.getText());
                            JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate return out vou no. Exit the program and try again.",
                                    "Return Out Vou No", JOptionPane.ERROR_MESSAGE);
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
            if (tblRetOut.getCellEditor() != null) {
                tblRetOut.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }
        //String deleteSQL;
        canEdit = true;
        //Clear text box.
        txtVouNo.setText("");
        txtReturnOutDate.setText("");
        txtCusId.setText("");
        txtCusName.setText("");
        txtRemark.setText("");
        lblStatus.setText("NEW");
        retOutTableModel.clear();
        assignDefaultValueModel();
        initTextBoxValue();
        vouEngine.setPeriod(DateUtil.getPeriod(txtReturnOutDate.getText()));
        genVouNo();
        setFocus();
        assignDefaultValue();
        System.gc();

        //All detail section need to explicity delete
        //because of save function only delete to join table
        /*deleteSQL = retOutTableModel.getDeleteSql();
        if (deleteSQL != null) {
            dao.execSql(deleteSQL);
        }*/
    }

    // <editor-fold defaultstate="collapsed" desc="initCombo">
    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboPayment, dao.findAll("PaymentType"));
            BindingUtil.BindCombo(cboLocation, getLocationFilter());
            BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));

            new ComBoBoxAutoComplete(cboPayment, this);
            new ComBoBoxAutoComplete(cboLocation, this);
            new ComBoBoxAutoComplete(cboCurrency, this);
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
                        + "' and a.isAllowRetOut = true) order by o.locationName");
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

    private void actionMapping() {
        //F3 event on tblSale
        tblRetOut.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblRetOut.getActionMap().put("F3-Action", actionMedList);

        //F8 event on tblSale
        tblRetOut.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblRetOut.getActionMap().put("F8-Action", actionItemDelete);

        //Enter event on tblSale
        tblRetOut.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblRetOut.getActionMap().put("ENTER-Action", actionTblRetInEnterKey);

        formActionKeyMapping(txtVouNo);
        formActionKeyMapping(txtRemark);
        formActionKeyMapping(txtReturnOutDate);
        formActionKeyMapping(cboLocation);
        formActionKeyMapping(txtCusId);
        formActionKeyMapping(txtCusName);
        formActionKeyMapping(cboPayment);
        formActionKeyMapping(txtVouTotal);
        formActionKeyMapping(txtVouPaid);
        formActionKeyMapping(txtVouBalance);
        formActionKeyMapping(tblRetOut);
    }
    private Action actionMedList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblRetOut.getCellEditor() != null) {
                    tblRetOut.getCellEditor().stopCellEditing();
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
    };
    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            RetOutDetailHis retdh;

            if (tblRetOut.getSelectedRow() >= 0) {
                retdh = listDetail.get(tblRetOut.getSelectedRow());

                int n = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Sale item delete", JOptionPane.YES_NO_OPTION);

                if (retdh.getMedicineId().getMedId() != null && n == 0) {
                    try {
                        if (tblRetOut.getCellEditor() != null) {
                            tblRetOut.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {
                        log.error("actionItemDelete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    }

                    retOutTableModel.delete(tblRetOut.getSelectedRow());
                    calculateTotalAmount();
                }
            }
        }
    };

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
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "CustomerList":
                Trader cus = (Trader) selectObj;

                currRetOut.setCustomer(cus);

                if (cus != null) {
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        txtCusId.setText(cus.getStuCode());
                    } else {
                        txtCusId.setText(cus.getTraderId());
                    }
                    txtCusName.setText(cus.getTraderName());

                    try {
                        //Change payment type to credit
                        PaymentType pt = (PaymentType) dao.find(PaymentType.class, 2);
                        cboPayment.setSelectedItem(pt);
                    } catch (Exception ex) {
                        log.error("selected CustomerList : " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                } else {
                    txtCusId.setText(null);
                    txtCusName.setText(null);
                }
                break;
            case "MedicineList":
                try {
                Medicine med = (Medicine) dao.find(Medicine.class,
                        ((Medicine) selectObj).getMedId());

                medUp.add(med);
                int selectRow = tblRetOut.getSelectedRow();
                retOutTableModel.setMed(med, selectRow);
            } catch (Exception ex) {
                log.error("MedicineList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
            break;
            case "RetOutVouList":
                try {
                dao.open();
                VoucherSearch vs = (VoucherSearch) selectObj;
                currRetOut = (RetOutHis) dao.find(RetOutHis.class, vs.getInvNo());

                if (Util1.getNullTo(currRetOut.isDeleted())) {
                    lblStatus.setText("DELETED");
                } else {
                    lblStatus.setText("EDIT");
                }
                setEditStatus(currRetOut.getRetOutId());

                cboLocation.setSelectedItem(currRetOut.getLocation());
                cboPayment.setSelectedItem(currRetOut.getPaymentType());
                cboCurrency.setSelectedItem(currRetOut.getCurrency());

                txtVouNo.setText(currRetOut.getRetOutId());
                txtReturnOutDate.setText(DateUtil.toDateStr(currRetOut.getRetOutDate()));
                if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                    txtCusId.setText(currRetOut.getCustomer().getStuCode());
                } else {
                    txtCusId.setText(currRetOut.getCustomer().getTraderId());
                }
                txtCusName.setText(currRetOut.getCustomer().getTraderName());
                txtRemark.setText(currRetOut.getRemark());
                txtVouTotal.setValue(currRetOut.getVouTotal());
                txtVouPaid.setValue(currRetOut.getPaid());
                txtVouBalance.setValue(currRetOut.getVouTotal() - currRetOut.getPaid());

                listDetail = dao.findAllHSQL(
                        "select o from RetOutDetailHis o where o.vouNo = '" + currRetOut.getRetOutId()
                        + "' order by o.uniqueId"
                );
                currRetOut.setListDetail(listDetail);
                /*if (currRetOut.getListDetail().size() > 0) {
                    listDetail = currRetOut.getListDetail();
                }*/

                for (RetOutDetailHis rodh : listDetail) {
                    medUp.add(rodh.getMedicineId());
                }
                retOutTableModel.setListDetail(listDetail);
                dao.close();
            } catch (Exception ex) {
                log.error("RetOutVouList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
            tblRetOut.requestFocusInWindow();

            break;
        }
    }

    private void initTextBoxAlign() {
        txtVouTotal.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtVouPaid.setHorizontalAlignment(JFormattedTextField.RIGHT);
        txtVouBalance.setHorizontalAlignment(JFormattedTextField.RIGHT);

        txtVouTotal.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouPaid.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtVouBalance.setFormatterFactory(NumberUtil.getDecimalFormat());
    }

    private void initTextBoxValue() {
        txtVouTotal.setValue(0.0);
        txtVouPaid.setValue(0.0);
        txtVouBalance.setValue(0.0);
    }

    // <editor-fold defaultstate="collapsed" desc="addNewRow">
    private void addNewRow() {
        RetOutDetailHis his = new RetOutDetailHis();

        his.setMedicineId(new Medicine());
        listDetail.add(his);
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="calculateAmont">
    private void calculateAmount(int row) {
        RetOutDetailHis retIndh = listDetail.get(row);
        String key = "";

        if (retIndh.getUnit() != null) {
            key = retIndh.getMedicineId().getMedId() + "-" + retIndh.getUnit().getItemUnitCode();
        }

        retIndh.setSmallestQty(NumberUtil.NZeroFloat(retIndh.getQty())
                * NumberUtil.NZeroFloat(medUp.getQtyInSmallest(key)));
        tblRetOut.setValueAt((NumberUtil.NZeroFloat(retIndh.getQty()) * NumberUtil.NZeroFloat(retIndh.getPrice())), row, 12);
        calculateTotalAmount();
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="calculateTotalAmount">
    private void calculateTotalAmount() {
        Double totalAmount = 0d;

        for (RetOutDetailHis sdh : listDetail) {
            totalAmount += NumberUtil.NZero(sdh.getAmount());
        }
        txtVouTotal.setValue(totalAmount);

        try {
            PaymentType pt = (PaymentType) cboPayment.getSelectedItem();

            if (pt != null) {
                if (pt.getPaymentTypeId() == 1)//Cash
                {
                    txtVouPaid.setValue(totalAmount);
                } else if (pt.getPaymentTypeId() == 2) //Credit
                {
                    txtVouPaid.setValue(0);
                }
            }
        } catch (NullPointerException ex) {
            log.error(ex.toString());
        }

        txtVouBalance.setValue(totalAmount - (NumberUtil.NZero(txtVouPaid.getValue())));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="initRetOutTable">
    private void initRetOutTable() {
        if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
            tblRetOut.setCellSelectionEnabled(true);
        }
        tblRetOut.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblRetOut.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblRetOut.getColumnModel().getColumn(1).setPreferredWidth(300);//Medicine Name
        tblRetOut.getColumnModel().getColumn(2).setPreferredWidth(60);//Relstr
        tblRetOut.getColumnModel().getColumn(3).setPreferredWidth(50);//Expire Date
        tblRetOut.getColumnModel().getColumn(4).setPreferredWidth(40);//Qty
        tblRetOut.getColumnModel().getColumn(5).setPreferredWidth(30);//Unit
        tblRetOut.getColumnModel().getColumn(6).setPreferredWidth(60);//Ret In price
        tblRetOut.getColumnModel().getColumn(7).setPreferredWidth(70);//Amount
        tblRetOut.getColumnModel().getColumn(4).setCellEditor(new BestTableCellEditor());

        //Change JTable cell editor
        tblRetOut.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
        tblRetOut.getColumnModel().getColumn(5).setCellEditor(
                new ReturnOut.RetOutTableUnitCellEditor());
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="addRetOutTableModelListener">
    private void addRetOutTableModelListener() {
        tblRetOut.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int column = e.getColumn();

                if (column >= 0) {
                    calculateTotalAmount();
                }
            }
        });
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="RetOutTableUnitCellEditor">
    private class RetOutTableUnitCellEditor extends AbstractCellEditor implements TableCellEditor {

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

            String medId = listDetail.get(rowIndex).getMedicineId().getMedId();

            try {
                JComboBox jb = new JComboBox();

                BindingUtil.BindCombo(jb, medUp.getUnitList(medId));
                component = jb;
            } catch (Exception ex) {
                log.error("getTableCellEditor : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
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

    @Override
    public void save() {
        Date vouSaleDate = DateUtil.toDate(txtReturnOutDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot save edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isValidEntry() && retOutTableModel.isValidEntry()) {
            //removeEmptyRow();

            //currRetOut.setListDetail(listDetail);
            try {
                if (lblStatus.getText().equals("EDIT")) {
                    dao.execProc("bkreturnout",
                            currRetOut.getRetOutId(),
                            Global.loginUser.getUserId(),
                            Global.machineId,
                            currRetOut.getVouTotal().toString(),
                            currRetOut.getPaid().toString(),
                            currRetOut.getBalance().toString());
                }

                try {
                    for (RetOutDetailHis rod : listDetail) {
                        if (rod.getMedicineId() != null) {
                            if (rod.getMedicineId().getTypeOption() != null) {
                                if (rod.getMedicineId().getTypeOption().equals("PACKING")) {
                                    dao.execProc("insert_packing", currRetOut.getRetOutId(),
                                            rod.getMedicineId().getMedId(),
                                            rod.getUniqueId().toString(), "Return Out",
                                            rod.getQty().toString());
                                }
                            }
                        }

                    }
                } catch (Exception ex) {
                    log.error("insert packing : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                } finally {
                    dao.close();
                }

                List<RetOutDetailHis> listTmp = retOutTableModel.getListDetail();
                String vouNo = currRetOut.getRetOutId();
                dao.open();
                dao.beginTran();
                for (RetOutDetailHis rodh : listTmp) {
                    rodh.setVouNo(vouNo);
                    if (rodh.getRetOutDetailId() == null) {
                        rodh.setRetOutDetailId(vouNo + "-" + rodh.getUniqueId().toString());
                    }

                    dao.save1(rodh);
                }
                currRetOut.setListDetail(listTmp);
                dao.save1(currRetOut);
                dao.commit();

                String deleteSQL = retOutTableModel.getDeleteSql();
                if (deleteSQL != null) {
                    dao.execSql(deleteSQL);
                }

                //For upload to account
                uploadToAccount(currRetOut.getRetOutId());

                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }

                newForm();
            } catch (Exception ex) {
                dao.rollBack();
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                JOptionPane.showMessageDialog(Util1.getParent(), "Error : " + ex.toString(),
                        "Return Out Save", JOptionPane.ERROR_MESSAGE);
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
        int locationId = -1;
        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Return Out Voucher Search", dao, locationId);
        dialog.setPreferredSize(new Dimension(1200, 600));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void delete() {
        Date vouSaleDate = DateUtil.toDate(txtReturnOutDate.getText());
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
        if (Util1.getNullTo(currRetOut.isDeleted())) {
            JOptionPane.showConfirmDialog(Util1.getParent(),
                    "Voucher already deleted.",
                    "Return Out voucher delete", JOptionPane.YES_NO_OPTION);
        } else {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                    "Are you sure to delete?",
                    "Return Out voucher delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                currRetOut.setDeleted(true);
                try {
                    dao.execProc("bkreturnout",
                            currRetOut.getRetOutId(),
                            Global.loginUser.getUserId(),
                            Global.machineId,
                            currRetOut.getVouTotal().toString(),
                            currRetOut.getPaid().toString(),
                            currRetOut.getBalance().toString());
                } catch (Exception ex) {
                    log.error("bkreturnout : " + ex.getMessage());
                } finally {
                    dao.close();
                }

                String vouNo = currRetOut.getRetOutId();
                try {
                    dao.execSql("update ret_out_his set deleted = true, intg_upd_status = null where ret_out_id = '" + vouNo + "'");
                } catch (Exception ex) {
                    log.error("delete error : " + ex.getMessage());
                } finally {
                    dao.close();
                }

                //For upload to account
                uploadToAccount(currRetOut.getRetOutId());
                newForm();

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
        if (isValidEntry() && retOutTableModel.isValidEntry()) {
            Date vouSaleDate = DateUtil.toDate(txtReturnOutDate.getText());
            Date lockDate = PharmacyUtil.getLockDate(dao);
            boolean isDataLock = false;

            if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                isDataLock = true;
            }

            if (canEdit) {
                if (!isDataLock) {
                    try {
                        List<RetOutDetailHis> listTmp = retOutTableModel.getListDetail();
                        String vouNo = currRetOut.getRetOutId();
                        dao.open();
                        dao.beginTran();
                        for (RetOutDetailHis rodh : listTmp) {
                            rodh.setVouNo(vouNo);
                            if (rodh.getRetOutDetailId() == null) {
                                rodh.setRetOutDetailId(vouNo + "-" + rodh.getUniqueId().toString());
                            }
                            dao.save1(rodh);
                        }
                        currRetOut.setListDetail(listTmp);
                        dao.save1(currRetOut);
                        dao.commit();
                        //For upload to account
                        uploadToAccount(currRetOut.getRetOutId());

                        if (lblStatus.getText().equals("NEW")) {
                            vouEngine.updateVouNo();
                        }
                    } catch (Exception ex) {
                        dao.rollBack();
                        log.error("print : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                        JOptionPane.showMessageDialog(Util1.getParent(), "Error : " + ex.toString(),
                                "Return Out Print", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        dao.close();
                    }
                }
            }

            String reportPath = Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path")
                    + "rptRetOutVoucher";
            String printerName = Util1.getPropValue("report.vou.printer");
            Map<String, Object> params = new HashMap();
            String compName = Util1.getPropValue("report.company.name");
            String printMode = Util1.getPropValue("report.vou.printer.mode");

            params.put("invoiceNo", currRetOut.getRetOutId());
            params.put("customerName", currRetOut.getCustomer().getTraderName());
            params.put("retInDate", currRetOut.getRetOutDate());
            params.put("grandTotal", currRetOut.getVouTotal());
            params.put("paid", currRetOut.getPaid());
            params.put("balance", currRetOut.getBalance());
            params.put("user", Global.loginUser.getUserShortName());
            params.put("compName", compName);

            if (lblStatus.getText().equals("NEW")) {
                params.put("vou_status", " ");
            } else {
                params.put("vou_status", lblStatus.getText());
            }

            //if (printMode.equals("View")) {
            ReportUtil.viewReport(reportPath, params, dao.getConnection());
            //} else {
            //    JasperPrint jp = ReportUtil.getReport(reportPath, params, listDetail);
            //    ReportUtil.printJasper(jp, printerName);
            //}

            newForm();
        }
    }

    public void setFocus() {
        txtCusId.requestFocusInWindow();
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        //focusOrder.add(txtCusId);
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
        boolean vStatus = true;

        if (currRetOut.getCustomer() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Customer cannot be blank.",
                    "No customer.", JOptionPane.ERROR_MESSAGE);
            vStatus = false;
            txtCusId.requestFocusInWindow();
        } else if (cboLocation.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Choose location.",
                    "No location.", JOptionPane.ERROR_MESSAGE);
            vStatus = false;
            cboLocation.requestFocusInWindow();
        } else if (cboPayment.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Choose payment type.",
                    "No payment type.", JOptionPane.ERROR_MESSAGE);
            vStatus = false;
            cboPayment.requestFocusInWindow();
        } else {
            try {
                if (tblRetOut.getCellEditor() != null) {
                    tblRetOut.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }
            currRetOut.setRetOutId(txtVouNo.getText());
            currRetOut.setLocation((Location) cboLocation.getSelectedItem());
            currRetOut.setPaymentType((PaymentType) cboPayment.getSelectedItem());
            currRetOut.setRemark(txtRemark.getText());
            currRetOut.setVouTotal(NumberUtil.getDouble(txtVouTotal.getText()));
            currRetOut.setPaid(NumberUtil.getDouble(txtVouPaid.getText()));
            currRetOut.setBalance(NumberUtil.getDouble(txtVouBalance.getText()));
            currRetOut.setDeleted(Util1.getNullTo(currRetOut.isDeleted()));
            if (lblStatus.getText().equals("NEW")) {
                currRetOut.setDeleted(false);
                currRetOut.setRetOutDate(DateUtil.toDateTime(txtReturnOutDate.getText()));
            } else {
                Date tmpDate = DateUtil.toDate(txtReturnOutDate.getText());
                if (!DateUtil.isSameDate(tmpDate, currRetOut.getRetOutDate())) {
                    currRetOut.setRetOutDate(DateUtil.toDateTime(txtReturnOutDate.getText()));
                }
            }

            currRetOut.setCurrency((Currency) cboCurrency.getSelectedItem());

            if (lblStatus.getText().equals("NEW")) {
                currRetOut.setCreatedBy(Global.loginUser);
                currRetOut.setSession(Global.sessionId);
            } else {
                currRetOut.setUpdatedBy(Global.loginUser);
                currRetOut.setUpdatedDate(DateUtil.toDate(DateUtil.getTodayDateStr()));
            }

            if (NumberUtil.NZeroL(currRetOut.getExrId()) == 0) {
                Long exrId = getExchangeId(txtReturnOutDate.getText(), currRetOut.getCurrency().getCurrencyCode());
                currRetOut.setExrId(exrId);
            }
        }

        return vStatus;
    }

    private void assignDefaultValueModel() {
        listDetail = ObservableCollections.observableList(new ArrayList<RetOutDetailHis>());
        retOutTableModel.setListDetail(listDetail);

        txtReturnOutDate.setText(DateUtil.getTodayDateStr());
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

        tmpObj = Util1.getDefaultValue("Location");
        if (tmpObj != null) {
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                if (cboLocation.getItemCount() > 0) {
                    cboLocation.setSelectedIndex(0);
                }
            } else {
                cboLocation.setSelectedItem(tmpObj);
            }
        }

        tmpObj = Util1.getDefaultValue("PaymentType");
        if (tmpObj != null) {
            cboPayment.setSelectedItem(tmpObj);
        }
    }
    private Action actionTblRetInEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblRetOut.getCellEditor() != null) {
                    tblRetOut.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            int row = tblRetOut.getSelectedRow();
            int col = tblRetOut.getSelectedColumn();

            RetOutDetailHis ridh = listDetail.get(row);

            if (col == 0 && ridh.getMedicineId().getMedId() != null) {
                tblRetOut.setColumnSelectionInterval(3, 3); //Move to Exp-Date
            } else if (col == 1 && ridh.getMedicineId().getMedId() != null) {
                tblRetOut.setColumnSelectionInterval(3, 3); //Move to Exp-Date
            } else if (col == 2 && ridh.getMedicineId().getMedId() != null) {
                tblRetOut.setColumnSelectionInterval(3, 3); //Move to Exp-Date
            } else if (col == 3 && ridh.getMedicineId().getMedId() != null) {
                tblRetOut.setColumnSelectionInterval(4, 4); //Move to Qty
            } else if (col == 4 && ridh.getMedicineId().getMedId() != null) {
                tblRetOut.setColumnSelectionInterval(6, 6); //Price
            } else if (col == 6 && ridh.getMedicineId().getMedId() != null) {
                if ((row + 1) <= listDetail.size()) {
                    tblRetOut.setRowSelectionInterval(row + 1, row + 1);
                }
                tblRetOut.setColumnSelectionInterval(0, 0); //Move to Code
            }
        }
    };

    private void getCustomerList() {
        int locationId = -1;
        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this,
                "Supplier List", dao, locationId);
        dialog.setPreferredSize(new Dimension(1200, 600));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void getCustomer() {
        Trader cus = null;
        log.info("trader_id : " + txtCusId.getText().trim().toUpperCase());
        if (txtCusId.getText() != null && !txtCusId.getText().isEmpty()) {
            try {
                cus = getTrader(txtCusId.getText().trim().toUpperCase());
                /*dao.open();
                String traderId = txtCusId.getText().trim().toUpperCase();
                String prefix = traderId.substring(0, 3);
                if (Util1.getPropValue("system.purchase.emitted.prifix").equals("Y")) {
                    if (!prefix.contains("SUP")) {
                        traderId = "SUP" + traderId;
                    }
                }
                cus = (Customer) dao.find(Customer.class, traderId);
                dao.close();*/
            } catch (Exception ex) {
                log.error("getCustomer : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        }

        if (cus == null && !txtCusId.getText().isEmpty()) {
            getCustomerList();
        } else {
            selected("CustomerList", cus);
        }
    }

    private Trader getTrader(String traderId) {
        Trader cus = null;
        try {
            String strFieldName = "o.traderId";
            if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                strFieldName = "o.stuCode";
            }

            if (Util1.getPropValue("system.location.trader.filter").equals("Y")) {
                int locationId = -1;
                if (cboLocation.getSelectedItem() instanceof Location) {
                    locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
                }
                String strSql = "select o from Trader o where "
                        + "o.active = true and o.traderId in (select a.key.traderId "
                        + "from LocationTraderMapping a where a.key.locationId = "
                        + locationId + ") and " + strFieldName + " = '" + traderId + "' order by o.traderName";
                log.info("strSql : " + strSql);
                List<Trader> listTrader = dao.findAllHSQL(strSql);
                if (listTrader != null) {
                    if (!listTrader.isEmpty()) {
                        cus = listTrader.get(0);
                    }
                }
            } else {
                List<Trader> listTrader = dao.findAllHSQL("select o from Trader where " + strFieldName + " = '" + traderId + "'");
                if (listTrader != null) {
                    if (!listTrader.isEmpty()) {
                        cus = listTrader.get(0);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getTrader : " + ex.toString());
        } finally {
            dao.close();
        }

        return cus;
    }

    private void removeEmptyRow() {
        listDetail.remove(listDetail.size() - 1);
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

        //Print
        jc.getInputMap().put(KeyStroke.getKeyStroke("F7"), "F7-Action");
        jc.getActionMap().put("F7-Action", actionPrint);
    }
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

    private Action actionPrint = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print();
        }
    };

    private Action traderF3Action = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            getCustomerList();
        }
    };

    private void uploadToAccount(String vouNo) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            String rootUrl = Util1.getPropValue("system.intg.api.url");
            if (!rootUrl.isEmpty() && !rootUrl.equals("-")) {
                try (CloseableHttpClient httpClient = createHttpClientWithTimeouts()) {
                    String url = rootUrl + "/returnOut";
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
            dao.execSql("update ret_out_his set intg_upd_status = null where ret_out_id = '" + vouNo + "'");
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

    private void setEditStatus(String invId) {
        try {
            if (!Util1.hashPrivilege("CanEditReturnOutCheckPoint")) {
                List list = dao.findAllSQLQuery(
                        "select * from c_bk_ret_out_his where ret_out_id = '" + invId + "'");
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
        txtReturnOutDate = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtCusId = new javax.swing.JTextField();
        txtCusName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cboPayment = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        cboLocation = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        cboCurrency = new javax.swing.JComboBox();
        butBetPurItems = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRetOut = new javax.swing.JTable(retOutTableModel);
        jPanel3 = new javax.swing.JPanel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtVouPaid = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Return Out Date ");

        txtReturnOutDate.setEditable(false);
        txtReturnOutDate.setFont(Global.textFont);
        txtReturnOutDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtReturnOutDateMouseClicked(evt);
            }
        });

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Supplier No/Name");

        txtCusId.setFont(Global.textFont);
        txtCusId.addFocusListener(new java.awt.event.FocusAdapter() {
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

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Currency");

        cboCurrency.setFont(Global.textFont);

        butBetPurItems.setFont(Global.textFont);
        butBetPurItems.setText("Get Purchase Items");
        butBetPurItems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBetPurItemsActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel1)
                        .add(18, 18, 18)
                        .add(txtVouNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 131, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtReturnOutDate))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(jLabel5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtRemark))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(txtCusId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtCusName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 222, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel6)
                            .add(jLabel7))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cboLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, cboPayment, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel3)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(cboCurrency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(butBetPurItems)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(txtVouNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(txtReturnOutDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txtCusId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtCusName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4)
                    .add(jLabel6)
                    .add(cboPayment, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(txtRemark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(cboCurrency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(butBetPurItems)))
            .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(jLabel7)
                .add(cboLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        tblRetOut.setFont(Global.textFont);
        tblRetOut.setModel(retOutTableModel);
        tblRetOut.setRowHeight(23);
        tblRetOut.setShowVerticalLines(false);
        tblRetOut.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblRetOutFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(tblRetOut);

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
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
        );

        txtVouTotal.setEditable(false);
        txtVouTotal.setFont(Global.textFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Total");

        txtVouPaid.setEditable(false);
        txtVouPaid.setFont(Global.textFont);

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Paid");

        txtVouBalance.setEditable(false);
        txtVouBalance.setFont(Global.textFont);

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Vou Balance");

        lblStatus.setFont(new java.awt.Font("Velvenda Cooler", 0, 40)); // NOI18N
        lblStatus.setText("DELETED");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(lblStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 186, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel8)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel9)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel11))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(txtVouTotal, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .add(txtVouPaid)
                    .add(txtVouBalance))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtVouTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel8))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtVouPaid, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel9))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(txtVouBalance, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel11)))
                    .add(lblStatus))
                .add(0, 8, Short.MAX_VALUE))
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

    private void txtReturnOutDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtReturnOutDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtReturnOutDate.setText(strDate);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.setPeriod(DateUtil.getPeriod(txtReturnOutDate.getText()));
                    genVouNo();
                }
            }
        }
    }//GEN-LAST:event_txtReturnOutDateMouseClicked

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == mouseClick) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusNameMouseClicked

    private void txtCusIdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusIdMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusIdMouseClicked

    private void txtCusIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusIdFocusLost
        //getCustomer();
    }//GEN-LAST:event_txtCusIdFocusLost

    private void txtCusIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusIdActionPerformed
        log.info("getCustomer");
        if (txtCusId.getText() == null || txtCusId.getText().isEmpty()) {
            txtCusName.setText(null);
            currRetOut.setCustomer(null);
        } else {
            log.info("getCustomer");
            getCustomer();
        }
    }//GEN-LAST:event_txtCusIdActionPerformed

    private void cboPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPaymentActionPerformed
        calculateTotalAmount();
    }//GEN-LAST:event_cboPaymentActionPerformed

    private void butBetPurItemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBetPurItemsActionPerformed
        if (!txtCusId.getText().isEmpty()) {
            /*ReturnInItemSearchDialog dialog = new ReturnInItemSearchDialog(this,
             txtCusId.getText(), txtCusName.getText());*/
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select trader.",
                    "No trader", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_butBetPurItemsActionPerformed

    private void tblRetOutFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblRetOutFocusLost
        /*try{
            if(tblRetOut.getCellEditor() != null){
                tblRetOut.getCellEditor().stopCellEditing();
            }
        }catch(Exception ex){
            
        }*/
    }//GEN-LAST:event_tblRetOutFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butBetPurItems;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblRetOut;
    private javax.swing.JTextField txtCusId;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtReturnOutDate;
    private javax.swing.JFormattedTextField txtVouBalance;
    private javax.swing.JFormattedTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouPaid;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables
}
