/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.database.entity.AdmissionKey;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.controller.SchoolDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.RetInDetailHis;
import com.cv.app.pharmacy.database.entity.RetInHis;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.pharmacy.database.view.ReturnInItemList;
import com.cv.app.pharmacy.database.view.VMarchant;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.MedInfo;
import com.cv.app.pharmacy.ui.common.RetInTableModel;
import com.cv.app.pharmacy.ui.common.SaleTableCodeCellEditor;
import com.cv.app.pharmacy.ui.util.MarchantSearch;
import com.cv.app.pharmacy.ui.util.MedListDialog1;
import com.cv.app.pharmacy.ui.util.ReturnInItemSearchDialog;
import com.cv.app.pharmacy.ui.util.TraderSearchDialog;
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
import java.util.*;
import javax.jms.MapMessage;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;
import org.jdesktop.observablecollections.ObservableCollections;

/**
 *
 * @author WSwe
 */
public class ReturnIn extends javax.swing.JPanel implements SelectionObserver, FormAction,
        MedInfo, KeyPropagate {

    static Logger log = Logger.getLogger(ReturnIn.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private GenVouNoImpl vouEngine = null;
    private List<RetInDetailHis> listDetail
            = ObservableCollections.observableList(new ArrayList<>());
    private MedicineUP medUp = new MedicineUP(dao);
    private BestAppFocusTraversalPolicy focusPolicy;
    private RetInHis currRetIn = new RetInHis();
    private RetInTableModel retInTableModel = new RetInTableModel(listDetail, dao,
            medUp, this);
    private PaymentType ptCash;
    private PaymentType ptCredit;
    private boolean canEdit = true;
    private int mouseClick = 2;

    /**
     * Creates new form Sale
     */
    public ReturnIn() {
        initComponents();

        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
            dao.open();
            initCombo();
            dao.close();
        } catch (Exception ex) {
            log.error("ReturnIn : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        actionMapping();
        initRetInTable();
        initTextBoxAlign();
        initTextBoxValue();

        txtReturnInDate.setText(DateUtil.getTodayDateStr());
        vouEngine = new GenVouNoImpl(dao, "RetIn", DateUtil.getPeriod(txtReturnInDate.getText()));
        try {
            ptCash = (PaymentType) dao.find(PaymentType.class,
                    NumberUtil.NZeroInt(Util1.getPropValue("system.paymenttype.cash")));
            ptCredit = (PaymentType) dao.find(PaymentType.class,
                    NumberUtil.NZeroInt(Util1.getPropValue("system.paymenttype.credit")));
        } catch (Exception ex) {
            log.error("ReturnIn : " + ex.getMessage());
        } finally {
            dao.close();
        }
        genVouNo();
        addNewRow();

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
        retInTableModel.setParent(tblRetIn);

        lblStatus.setText("NEW");

        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
            lblPatient.setText("Patient No/Name");
        } else if (Util1.getPropValue("system.app.usage.type").equals("School")) {
            lblPatient.setText("Student No/Name");
        } else {
            lblPatient.setText("Customer No/Name");
        }
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
    }

    private void genVouNo() {
        try {
            String vouNo = vouEngine.getVouNo();
            txtVouNo.setText(vouNo);
            List<RetInHis> listRIH = dao.findAllHSQL(
                    "select o from RetInHis o where o.retInId = '" + txtVouNo.getText() + "'");
            if (listRIH != null) {
                if (!listRIH.isEmpty()) {
                    vouEngine.updateVouNo();
                    vouNo = vouEngine.getVouNo();
                    txtVouNo.setText(vouNo);
                    listRIH = null;
                    listRIH = dao.findAllHSQL(
                            "select o from RetInHis o where o.retInId = '" + txtVouNo.getText() + "'");
                    if (listRIH != null) {
                        if (!listRIH.isEmpty()) {
                            log.error("Duplicate purchase vour error : " + txtVouNo.getText() + " @ "
                                    + txtReturnInDate.getText());
                            JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate return in vou no. Exit the program and try again.",
                                    "Return In Vou No", JOptionPane.ERROR_MESSAGE);
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
            if (tblRetIn.getCellEditor() != null) {
                tblRetIn.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {

        }

        canEdit = true;
        //Clear text box.
        txtVouNo.setText("");
        txtReturnInDate.setText("");
        txtCusId.setText("");
        txtCusName.setText("");
        txtRemark.setText("");
        txtAdmissionNo.setText("");
        lblStatus.setText("NEW");
        lblOTID.setText(null);
        cboPayment.setSelectedItem(ptCash);
        retInTableModel.setCanEdit(canEdit);
        retInTableModel.clear();
        assignDefaultValueModel();
        initTextBoxValue();
        vouEngine.setPeriod(DateUtil.getPeriod(txtReturnInDate.getText()));
        genVouNo();
        setFocus();
        assignDefaultValue();
        applySecurityPolicy();
        System.gc();
    }

    private void deleteDetail() {
        try {
            String deleteSQL = retInTableModel.getDeleteSql();
            log.info("deleteSQL : " + deleteSQL);
            if (deleteSQL != null) {
                dao.execSql(deleteSQL);
            }
        } catch (Exception ex) {
            log.error("deleteDetail : " + ex.toString());
        } finally {

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

    // <editor-fold defaultstate="collapsed" desc="initCombo">
    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboPayment, dao.findAll("PaymentType"));
            BindingUtil.BindCombo(cboLocation, getLocationFilter());
            BindingUtil.BindCombo(cboCurrency, dao.findAll("Currency"));

            new ComBoBoxAutoComplete(cboPayment);
            new ComBoBoxAutoComplete(cboLocation);
            new ComBoBoxAutoComplete(cboCurrency);
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
                        + "' and a.isAllowRetIn = true) order by o.locationName");
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
        tblRetIn.getInputMap().put(KeyStroke.getKeyStroke("F3"), "F3-Action");
        tblRetIn.getActionMap().put("F3-Action", actionMedList);

        //F8 event on tblSale
        tblRetIn.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblRetIn.getActionMap().put("F8-Action", actionItemDelete);

        //Enter event on tblSale
        tblRetIn.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblRetIn.getActionMap().put("ENTER-Action", actionTblRetInEnterKey);

        formActionKeyMapping(txtVouNo);
        formActionKeyMapping(txtRemark);
        formActionKeyMapping(txtReturnInDate);
        formActionKeyMapping(cboLocation);
        formActionKeyMapping(txtCusId);
        formActionKeyMapping(txtCusName);
        formActionKeyMapping(cboPayment);
        formActionKeyMapping(txtVouTotal);
        formActionKeyMapping(txtVouPaid);
        formActionKeyMapping(txtVouBalance);
        formActionKeyMapping(tblRetIn);
    }// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionMedList">
    private Action actionMedList = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblRetIn.getCellEditor() != null) {
                    tblRetIn.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
                //No entering medCode, only press F3
                try {
                    dao.open();
                    getMedList("");
                    dao.close();
                } catch (Exception ex1) {
                    log.error("actionMedList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex1.toString());
                }
            }
        }
    };// </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="actionItemDelete">
    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!canEdit) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot delete.",
                        "Check Point", JOptionPane.ERROR_MESSAGE);
                return;
            }
            RetInDetailHis retdh;
            int yes_no = -1;

            if (tblRetIn.getSelectedRow() >= 0) {
                retdh = listDetail.get(tblRetIn.getSelectedRow());

                if (retdh.getMedicineId().getMedId() != null) {
                    try {
                        yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                                "Sale item delete", JOptionPane.YES_NO_OPTION);

                        if (tblRetIn.getCellEditor() != null) {
                            tblRetIn.getCellEditor().stopCellEditing();
                        }
                    } catch (Exception ex) {
                        log.error("actionItemDelete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    }

                    if (yes_no == 0) {
                        retInTableModel.delete(tblRetIn.getSelectedRow());
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
        if (currRetIn.getCustomer() != null) {
            if (currRetIn.getCustomer().getTraderGroup() != null) {
                cusGroup = currRetIn.getCustomer().getTraderGroup().getGroupId();
            }
        }
        MedListDialog1 dialog = new MedListDialog1(dao, filter, locationId, cusGroup);

        if (dialog.getSelect() != null) {
            selected("MedicineList", dialog.getSelect());
        }
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="selected">
    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "CustomerList":
                Trader cus = (Trader) selectObj;

                currRetIn.setCustomer(cus);
                if (cus != null) {
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        txtCusId.setText(cus.getStuCode());
                    } else {
                        txtCusId.setText(cus.getTraderId());
                    }
                    txtCusName.setText(cus.getTraderName());

                    String selCusId;
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        selCusId = cus.getStuCode();
                    } else {
                        selCusId = cus.getTraderId();
                    }
                    //Change payment type to credit
                    //PaymentType pt = (PaymentType) dao.find(PaymentType.class, 2);
                    if (Util1.getPropValue("system.default.customer").equals(selCusId)) {
                        cboPayment.setSelectedItem(ptCash);
                        cboPayment.setEnabled(false);
                    } else {
                        cboPayment.setSelectedItem(ptCredit);
                        cboPayment.setEnabled(true);
                    }
                } else {
                    txtCusId.setText(null);
                    txtCusName.setText(null);
                }
                break;
            case "MedicineList":
                try {
                dao.open();
                Medicine med = (Medicine) dao.find(Medicine.class, ((Medicine) selectObj).getMedId());
                medUp.add(med);
                int selectRow = tblRetIn.getSelectedRow();
                retInTableModel.setMed(med, selectRow);
                dao.close();
            } catch (Exception ex) {
                log.error("MedicineList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
            break;
            case "RetInVouList":
                try {
                VoucherSearch vs = (VoucherSearch) selectObj;
                dao.open();
                currRetIn = (RetInHis) dao.find(RetInHis.class, vs.getInvNo());

                if (Util1.getNullTo(currRetIn.isDeleted())) {
                    lblStatus.setText("DELETED");
                } else {
                    lblStatus.setText("EDIT");
                }

                cboLocation.setSelectedItem(currRetIn.getLocation());
                cboPayment.setSelectedItem(currRetIn.getPaymentType());
                cboCurrency.setSelectedItem(currRetIn.getCurrency());

                txtVouNo.setText(currRetIn.getRetInId());
                txtReturnInDate.setText(DateUtil.toDateStr(currRetIn.getRetInDate()));
                txtAdmissionNo.setText(currRetIn.getAdmissionNo());

                if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                    if (currRetIn.getPatient() != null) {
                        txtCusId.setText(currRetIn.getPatient().getRegNo());
                        txtCusName.setText(currRetIn.getPatient().getPatientName());
                    } else {
                        txtCusId.setText(null);
                        txtCusName.setText(null);
                    }
                } else if (currRetIn.getCustomer() != null) {
                    if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
                        txtCusId.setText(currRetIn.getCustomer().getStuCode());
                    } else {
                        txtCusId.setText(currRetIn.getCustomer().getTraderId());
                    }
                    txtCusName.setText(currRetIn.getCustomer().getTraderName());
                } else {
                    txtCusId.setText(null);
                    txtCusName.setText(null);
                }

                //txtCusId.setText(currRetIn.getCustomer().getTraderId());
                //txtCusName.setText(currRetIn.getCustomer().getTraderName());
                txtRemark.setText(currRetIn.getRemark());
                txtVouTotal.setValue(currRetIn.getVouTotal());
                txtVouPaid.setValue(currRetIn.getPaid());
                txtVouBalance.setValue(currRetIn.getVouTotal() - currRetIn.getPaid());

                listDetail = dao.findAllHSQL(
                        "select o from RetInDetailHis o where o.vouNo = '"
                        + currRetIn.getRetInId() + "' order by o.uniqueId"
                );
                currRetIn.setListDetail(listDetail);
                /*if (currRetIn.getListDetail().size() > 0) {
                    listDetail = currRetIn.getListDetail();
                }*/

                for (RetInDetailHis ridh : listDetail) {
                    medUp.add(ridh.getMedicineId());
                }
                retInTableModel.setListDetail(listDetail);
                dao.close();

                lblOTID.setText(currRetIn.getOtId());
            } catch (Exception ex) {
                log.error("RetInVouList : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
            setEditStatus(currRetIn.getRetInId());
            retInTableModel.setCanEdit(canEdit);
            applySecurityPolicy();
            tblRetIn.requestFocusInWindow();
            break;
            case "PatientSearch":
                Patient pt = (Patient) selectObj;
                currRetIn.setPatient(pt);
                currRetIn.setAdmissionNo(pt.getAdmissionNo());
                txtAdmissionNo.setText(pt.getAdmissionNo());
                txtCusId.setText(pt.getRegNo());
                txtCusName.setText(pt.getPatientName());
                if (currRetIn.getAdmissionNo() != null) {
                    if (currRetIn.getAdmissionNo().isEmpty()) {
                        cboPayment.setSelectedItem(ptCash);
                        retInTableModel.setCusType("A");
                    } else {
                        cboPayment.setSelectedItem(ptCredit);
                        retInTableModel.setCusType("N");
                    }
                } else {
                    cboPayment.setSelectedItem(ptCash);
                    retInTableModel.setCusType("A");
                }
                break;
            case "RetInItemSearch":
                ReturnInItemList retInItem = (ReturnInItemList) selectObj;
                retInTableModel.addRetInItem(retInItem);
                medUp.add(retInItem.getKey().getItem());
                break;
            case "VMarchantSearch":
                VMarchant vm = (VMarchant) selectObj;
                txtCusId.setText(vm.getPersonNumber());
                txtCusName.setText(vm.getPersonName());
                currRetIn.setRegNo(vm.getPersonId());
                currRetIn.setStuName(vm.getPersonName());
                currRetIn.setStuNo(vm.getPersonNumber());
                break;
        }
    }// </editor-fold>

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
        RetInDetailHis his = new RetInDetailHis();

        his.setMedicineId(new Medicine());
        listDetail.add(his);
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="hasNewRow">
    private boolean hasNewRow() {
        boolean status = false;
        RetInDetailHis retInDetailHis = listDetail.get(listDetail.size() - 1);

        String ID = retInDetailHis.getMedicineId().getMedId();

        if (ID == null) {
            status = true;
        }

        return status;
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="calculateAmont">
    private void calculateAmount(int row) {
        RetInDetailHis retIndh = listDetail.get(row);
        String key = "";

        if (retIndh.getUnit() != null) {
            key = retIndh.getMedicineId().getMedId() + "-" + retIndh.getUnit().getItemUnitCode();
        }

        retIndh.setSmallestQty(NumberUtil.NZeroFloat(retIndh.getQty())
                * medUp.getQtyInSmallest(key));
        tblRetIn.setValueAt((NumberUtil.NZero(retIndh.getQty()) * NumberUtil.NZero(retIndh.getPrice())), row, 12);
        calculateTotalAmount();
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="calculateTotalAmount">
    private void calculateTotalAmount() {
        Double totalAmount = 0d;

        for (RetInDetailHis sdh : listDetail) {
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

    // <editor-fold defaultstate="collapsed" desc="initRetInTable">
    private void initRetInTable() {
        if (Util1.getPropValue("system.grid.cell.selection").equals("Y")) {
            tblRetIn.setCellSelectionEnabled(true);
        }
        tblRetIn.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblRetIn.getColumnModel().getColumn(0).setPreferredWidth(50);//Code
        tblRetIn.getColumnModel().getColumn(1).setPreferredWidth(300);//Medicine Name
        tblRetIn.getColumnModel().getColumn(2).setPreferredWidth(60);//Relstr
        tblRetIn.getColumnModel().getColumn(3).setPreferredWidth(50);//Expire Date
        tblRetIn.getColumnModel().getColumn(4).setPreferredWidth(40);//Qty
        tblRetIn.getColumnModel().getColumn(5).setPreferredWidth(30);//Unit
        tblRetIn.getColumnModel().getColumn(6).setPreferredWidth(60);//Ret In price
        tblRetIn.getColumnModel().getColumn(7).setPreferredWidth(70);//Amount
        tblRetIn.getColumnModel().getColumn(4).setCellEditor(new BestTableCellEditor(this));

        addRetInTableModelListener();

        //Change JTable cell editor
        tblRetIn.getColumnModel().getColumn(0).setCellEditor(
                new SaleTableCodeCellEditor(dao));
        tblRetIn.getColumnModel().getColumn(5).setCellEditor(
                new ReturnIn.RetInTableUnitCellEditor());
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="addRetInTableModelListener">
    private void addRetInTableModelListener() {
        tblRetIn.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int column = e.getColumn();

                //if (column >= 0) {
                //Need to add action for updating table
                calculateTotalAmount();
                //}
            }
        });
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="RetInTableUnitCellEditor">
    private class RetInTableUnitCellEditor extends AbstractCellEditor implements TableCellEditor {

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
                System.out.println("Unit");

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
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot save edit voucher.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (isValidEntry() && retInTableModel.isValidEntry()) {
            Date vouSaleDate = DateUtil.toDate(txtReturnInDate.getText());
            Date lockDate = PharmacyUtil.getLockDate(dao);
            if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                        + DateUtil.toDateStr(lockDate) + ".",
                        "Locked Data", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //removeEmptyRow();
            try {
                dao.execProc("bkreturnin",
                        currRetIn.getRetInId(),
                        Global.loginUser.getUserId(),
                        Global.machineId,
                        currRetIn.getVouTotal().toString(),
                        currRetIn.getPaid().toString(),
                        currRetIn.getBalance().toString());
            } catch (Exception ex) {
                log.error("bkreturnin : " + ex.getStackTrace()[0].getLineNumber() + " - " + currRetIn.getRetInId() + " - " + ex);
            } finally {
                dao.close();
            }

            try {
                for (RetInDetailHis rid : listDetail) {
                    if (rid.getMedicineId() != null) {
                        if (rid.getMedicineId().getTypeOption() != null) {
                            if (rid.getMedicineId().getTypeOption().equals("PACKING")) {
                                dao.execProc("insert_packing", currRetIn.getRetInId(),
                                        rid.getMedicineId().getMedId(),
                                        rid.getUniqueId().toString(), "Return In",
                                        rid.getQty().toString());
                            }
                        }
                    }

                }
            } catch (Exception ex) {
                log.error("insert packing : " + ex.getStackTrace()[0].getLineNumber() + " - " + currRetIn.getRetInId() + " - " + ex);
            } finally {
                dao.close();
            }

            try {
                List<RetInDetailHis> listTmp = retInTableModel.getListDetail();
                String vouNo = currRetIn.getRetInId();
                dao.open();
                dao.beginTran();
                for (RetInDetailHis ridh : listTmp) {
                    ridh.setVouNo(vouNo);
                    if (ridh.getRetInDetailId() == null) {
                        ridh.setRetInDetailId(vouNo + "-" + ridh.getUniqueId().toString());
                    }
                    log.info("med_id : " + ridh.getMedicineId().getMedId() + " ret_in_detail_id : " + ridh.getRetInDetailId());
                    dao.save1(ridh);
                }
                currRetIn.setListDetail(listTmp);

                dao.save1(currRetIn);
                dao.commit();
                deleteDetail();
                updateVouTotal(currRetIn.getRetInId());
                //For upload to account
                uploadToAccount(currRetIn);

                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.updateVouNo();
                }

                newForm();
            } catch (Exception ex) {
                dao.rollBack();
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + currRetIn.getRetInId() + " - " + ex.toString());
                JOptionPane.showMessageDialog(Util1.getParent(), "Error : " + ex.toString(),
                        "Return In Save", JOptionPane.ERROR_MESSAGE);
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
                "Return In Voucher Search", dao, locationId);
        dialog.setPreferredSize(new Dimension(1200, 600));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void delete() {
        Date vouSaleDate = DateUtil.toDate(txtReturnInDate.getText());
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
        if (Util1.getNullTo(currRetIn.isDeleted())) {
            JOptionPane.showConfirmDialog(Util1.getParent(), "Voucher already deleted.",
                    "Return In voucher delete", JOptionPane.ERROR);
        } else {
            int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                    "Return In voucher delete", JOptionPane.YES_NO_OPTION);

            if (yes_no == 0) {
                try {
                    dao.execProc("bkreturnin",
                            currRetIn.getRetInId(),
                            Global.loginUser.getUserId(),
                            Global.machineId,
                            currRetIn.getVouTotal().toString(),
                            currRetIn.getPaid().toString(),
                            currRetIn.getBalance().toString());
                } catch (Exception ex) {
                    log.error("bkreturnin : " + ex.getStackTrace()[0].getLineNumber() + " - " + currRetIn.getRetInId() + " - " + ex);
                } finally {
                    dao.close();
                }

                currRetIn.setDeleted(true);
                currRetIn.setIntgUpdStatus(null);

                String vouNo = currRetIn.getRetInId();
                try {
                    dao.execSql("update ret_in_his set deleted = true, intg_upd_status = null where ret_in_id = '" + vouNo + "'");
                    //For upload to account
                    uploadToAccount(currRetIn);
                } catch (Exception ex) {
                    log.error("delete error : " + ex.getMessage());
                } finally {
                    dao.close();
                }
                //save();
                newForm();
            }
        }
    }

    @Override
    public void deleteCopy() {
        System.out.println("Delete Copy");
    }

    @Override
    public void print() {
        if (isValidEntry() && retInTableModel.isValidEntry()) {
            Date vouSaleDate = DateUtil.toDate(txtReturnInDate.getText());
            Date lockDate = PharmacyUtil.getLockDate(dao);
            boolean isDataLock = false;
            if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
                isDataLock = true;
            }

            try {
                dao.execProc("bkreturnin",
                        currRetIn.getRetInId(),
                        Global.loginUser.getUserId(),
                        Global.machineId,
                        currRetIn.getVouTotal().toString(),
                        currRetIn.getPaid().toString(),
                        currRetIn.getBalance().toString());
            } catch (Exception ex) {
                log.error("bkreturnin : " + ex.getStackTrace()[0].getLineNumber() + " - " + currRetIn.getRetInId() + " - " + ex);
            } finally {
                dao.close();
            }

            try {
                if (canEdit) {
                    if (!isDataLock) {
                        List<RetInDetailHis> listTmp = retInTableModel.getListDetail();
                        String vouNo = currRetIn.getRetInId();
                        dao.open();
                        dao.beginTran();
                        for (RetInDetailHis ridh : listTmp) {
                            ridh.setVouNo(vouNo);
                            if (ridh.getRetInDetailId() == null) {
                                ridh.setRetInDetailId(vouNo + "-" + ridh.getUniqueId().toString());
                            }

                            dao.save1(ridh);
                        }
                        currRetIn.setListDetail(listTmp);
                        dao.save1(currRetIn);
                        dao.commit();
                        deleteDetail();
                        updateVouTotal(currRetIn.getRetInId());
                        //For upload to account
                        uploadToAccount(currRetIn);

                        if (lblStatus.getText().equals("NEW")) {
                            vouEngine.updateVouNo();
                        }
                    }
                }
            } catch (Exception ex) {
                dao.rollBack();
                log.error("Print : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                JOptionPane.showMessageDialog(Util1.getParent(), "Error : " + ex.toString(),
                        "Return In Print", JOptionPane.ERROR_MESSAGE);
            } finally {
                dao.close();
            }

            String printerName = Util1.getPropValue("report.vou.printer");
            Map<String, Object> params = new HashMap();
            String compName = Util1.getPropValue("report.company.name");
            String printMode = Util1.getPropValue("report.vou.printer.mode");

            params.put("invoiceNo", currRetIn.getRetInId());
            if (currRetIn.getCustomer() != null) {
                params.put("customerName", currRetIn.getCustomer().getTraderName());
            }
            if (currRetIn.getPatient() != null) {
                params.put("customerName", currRetIn.getPatient().getPatientName());
            }
            params.put("retInDate", currRetIn.getRetInDate());
            params.put("grandTotal", currRetIn.getVouTotal());
            params.put("paid", currRetIn.getPaid());
            params.put("balance", currRetIn.getBalance());
            params.put("user", Global.loginUser.getUserShortName());
            params.put("compName", compName);

            if (lblStatus.getText().equals("NEW")) {
                params.put("vou_status", " ");
            } else {
                params.put("vou_status", lblStatus.getText());
            }

            String reportPath = Util1.getPropValue("system.pharmacy.returnin.report");
            if (!reportPath.isEmpty()) {
                reportPath = Util1.getAppWorkFolder()
                        + Util1.getPropValue("report.folder.path")
                        + reportPath;
                if (printMode.equals("View")) {
                    ReportUtil.viewReport(reportPath, params, dao.getConnection());
                } else {
                    JasperPrint jp = ReportUtil.getReport(reportPath, params, dao.getConnection());
                    ReportUtil.printJasper(jp, printerName);
                }
            } else {
                reportPath = Util1.getAppWorkFolder()
                        + Util1.getPropValue("report.folder.path")
                        + "rptRetInVoucher";
                if (printMode.equals("View")) {
                    ReportUtil.viewReport(reportPath, params, dao.getConnection());
                    //ReportUtil.viewReport(reportPath, params, currRetIn.getListDetail());
                } else {
                    //JasperPrint jp = ReportUtil.getReport(reportPath, params, currRetIn.getListDetail());
                    JasperPrint jp1 = ReportUtil.getReport(reportPath, params, dao.getConnection());
                    ReportUtil.printJasper(jp1, printerName);
                }
            }

            newForm();
        }
    }

    private void printA4() {

    }

    public void setFocus() {
        txtCusId.requestFocusInWindow();
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        focusOrder.add(txtCusId);
        focusOrder.add(cboLocation);
        focusOrder.add(cboPayment);
        focusOrder.add(txtRemark);
        focusOrder.add(tblRetIn);

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
        boolean status = true;
        double vouBalance = NumberUtil.NZero(txtVouBalance.getText());
        Patient pt = currRetIn.getPatient();
        String admissionNo = Util1.isNull(pt.getAdmissionNo(), "-");

        if (!admissionNo.equals("-")) {
            AdmissionKey key = new AdmissionKey();
            key.setAmsNo(admissionNo);
            key.setRegister(pt);
            try {
                Ams adm = (Ams) dao.find(Ams.class, key);
                if (adm == null) {
                    status = false;
                } else {
                    Date vouDate = DateUtil.toDate(txtReturnInDate.getText());
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
        
        if (!Util1.hashPrivilege("CanEditReturnCheckPoint")) {
            if (lblStatus.getText().equals("NEW")) {
                try {
                    long cnt = dao.getRowCount("select count(*) from c_bk_ret_in_his where ret_in_date >= '"
                            + DateUtil.toDateStrMYSQL(txtReturnInDate.getText()) + "'");
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

        if (txtVouNo.getText().isEmpty()) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid voucher no.",
                    "Invalid Voucher ID", JOptionPane.ERROR_MESSAGE);
            status = false;
        } else if (currRetIn.getCustomer() == null
                && !Util1.getPropValue("system.app.usage.type").equals("School")
                && !Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
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
        } else if (vouBalance != 0 && currRetIn.getPatient() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid registeration number.",
                    "Reg No", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtCusId.requestFocusInWindow();
        } else {
            try {
                if (tblRetIn.getCellEditor() != null) {
                    tblRetIn.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {

            }

            currRetIn.setRetInId(txtVouNo.getText());
            currRetIn.setLocation((Location) cboLocation.getSelectedItem());
            currRetIn.setPaymentType((PaymentType) cboPayment.getSelectedItem());
            currRetIn.setRemark(txtRemark.getText());
            currRetIn.setVouTotal(NumberUtil.getDouble(txtVouTotal.getText()));
            currRetIn.setPaid(NumberUtil.getDouble(txtVouPaid.getText()));
            currRetIn.setBalance(NumberUtil.getDouble(txtVouBalance.getText()));
            currRetIn.setDeleted(Util1.getNullTo(currRetIn.isDeleted()));
            currRetIn.setAdmissionNo(txtAdmissionNo.getText());
            currRetIn.setIntgUpdStatus(null);

            if (lblStatus.getText().equals("NEW")) {
                currRetIn.setDeleted(false);
                currRetIn.setRetInDate(DateUtil.toDateTime(txtReturnInDate.getText()));
            } else {
                Date tmpDate = DateUtil.toDate(txtReturnInDate.getText());
                if (!DateUtil.isSameDate(tmpDate, currRetIn.getRetInDate())) {
                    currRetIn.setRetInDate(DateUtil.toDateTime(txtReturnInDate.getText()));
                }
            }

            currRetIn.setCurrency((Currency) cboCurrency.getSelectedItem());

            if (lblStatus.getText().equals("NEW")) {
                currRetIn.setCreatedBy(Global.loginUser);
                currRetIn.setSession(Global.sessionId);
            } else {
                currRetIn.setUpdatedBy(Global.loginUser);
                currRetIn.setUpdatedDate(DateUtil.toDate(DateUtil.getTodayDateStr()));
            }

            if (Util1.getPropValue("system.app.usage.type").equals("School")) {
                currRetIn.setStuName(txtCusName.getText());
            }

            if (lblOTID.getText() == null) {
                currRetIn.setOtId(null);
            } else if (!lblOTID.getText().isEmpty()) {
                currRetIn.setOtId(lblOTID.getText());
            } else {
                currRetIn.setOtId(null);
            }

            if (NumberUtil.NZeroL(currRetIn.getExrId()) == 0) {
                Long exrId = getExchangeId(txtReturnInDate.getText(), currRetIn.getCurrency().getCurrencyCode());
                currRetIn.setExrId(exrId);
            }
        }

        return status;
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
                    Patient ptt = null;
                    List<Patient> listP = dao.findAllHSQL(
                            "select o from Patient o where o.regNo = '" + txtCusId.getText().trim() + "'");
                    if (listP != null) {
                        if (!listP.isEmpty()) {
                            ptt = listP.get(0);
                        }
                    }

                    if (ptt == null) {
                        txtCusId.setText(null);
                        txtCusName.setText(null);
                        currRetIn.setRegNo(null);
                        lblOTID.setText(null);
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Invalid Patient code.",
                                "Patient Code", JOptionPane.ERROR_MESSAGE);
                        txtCusId.requestFocus();
                    } else {
                        currRetIn.setPatient(ptt);
                        currRetIn.setAdmissionNo(ptt.getAdmissionNo());
                        txtAdmissionNo.setText(ptt.getAdmissionNo());
                        txtCusId.setText(ptt.getRegNo());
                        txtCusName.setText(ptt.getPatientName());
                        lblOTID.setText(ptt.getOtId());
                        
                        if (currRetIn.getAdmissionNo() != null) {
                            if (currRetIn.getAdmissionNo().isEmpty()) {
                                cboPayment.setSelectedItem(ptCash);
                                retInTableModel.setCusType("A");
                            } else {
                                cboPayment.setSelectedItem(ptCredit);
                                retInTableModel.setCusType("N");
                            }
                        } else {
                            cboPayment.setSelectedItem(ptCash);
                            retInTableModel.setCusType("A");
                        }
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
                        currRetIn.setRegNo(null);
                        currRetIn.setStuName(null);
                        currRetIn.setStuNo(null);

                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Invalid student number or not enroll student.",
                                "Student Number", JOptionPane.ERROR_MESSAGE);
                    } else {
                        txtCusId.setText(vm.getPersonNumber());
                        txtCusName.setText(vm.getPersonName());
                        currRetIn.setRegNo(vm.getPersonId());
                        currRetIn.setStuName(vm.getPersonName());
                        currRetIn.setStuNo(vm.getPersonNumber());
                        tblRetIn.requestFocusInWindow();
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
                        currRetIn.setCustomer(null);

                        if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Invalid patient code.",
                                    "Patient Code", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(Util1.getParent(),
                                    "Invalid customer code.",
                                    "Trader Code", JOptionPane.ERROR_MESSAGE);
                        }

                    } else {
                        selected("CustomerList", cus);
                        tblRetIn.requestFocusInWindow();
                    }
                }
            } catch (Exception ex) {
                log.error("getCustomer : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        }
    }

    private void getCustomerList() {
        if (!canEdit) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                    "Check Point", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Customer List", dao);
        //PatientSearch dialog1 = new PatientSearch(dao, this);
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
    private Action actionTblRetInEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblRetIn.getCellEditor() != null) {
                    tblRetIn.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            int row = tblRetIn.getSelectedRow();
            int col = tblRetIn.getSelectedColumn();

            RetInDetailHis ridh = listDetail.get(row);

            if (col == 0 && ridh.getMedicineId().getMedId() != null) {
                tblRetIn.setColumnSelectionInterval(4, 4); //Move to Qty
            } else if (col == 1 && ridh.getMedicineId().getMedId() != null) {
                tblRetIn.setColumnSelectionInterval(4, 4); //Move to Qty
            } else if (col == 2 && ridh.getMedicineId().getMedId() != null) {
                tblRetIn.setColumnSelectionInterval(4, 4); //Move to Qty
            } else if (col == 3 && ridh.getMedicineId().getMedId() != null) {
                tblRetIn.setColumnSelectionInterval(4, 4); //Move to Qty
            } else if (col == 4 && ridh.getMedicineId().getMedId() != null) {
                tblRetIn.setColumnSelectionInterval(6, 6); //Price
            } else if (col == 6 && ridh.getMedicineId().getMedId() != null) {
                if ((row + 1) <= listDetail.size()) {
                    tblRetIn.setRowSelectionInterval(row + 1, row + 1);
                }
                tblRetIn.setColumnSelectionInterval(0, 0); //Move to Code
            }
        }
    };

    private void assignDefaultValueModel() {
        listDetail = ObservableCollections.observableList(new ArrayList<RetInDetailHis>());
        retInTableModel.setListDetail(listDetail);
        currRetIn.setPatient(null);
        txtReturnInDate.setText(DateUtil.getTodayDateStr());
    }

    private void assignDefaultValue() {
        Object tmpObj;

        if (!Util1.getPropValue("system.location.trader.filter").equals("Y")) {
            try {
                tmpObj = dao.find(Trader.class, Util1.getPropValue("system.default.customer"));
                if (tmpObj != null) {
                    selected("CustomerList", tmpObj);
                }
            } catch (Exception ex) {
                log.error("assignDefaultValue : " + ex.getMessage());
            } finally {
                dao.close();
            }
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

        /*tmpObj = Util1.getDefaultValue("PaymentType");
        if (tmpObj != null) {
            cboPayment.setSelectedItem(tmpObj);
        }*/
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

    private Action traderF3Action = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            getCustomerList();
        }
    };

    private void uploadToAccount(RetInHis rih) {
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
                        msg.setString("entity", "RETURNIN");
                        msg.setString("VOUCHER-NO", rih.getRetInId());
                        /*msg.setString("remark", rih.getRemark());
                        msg.setString("cusId", rih.getCustomer().getAccountId());
                        msg.setBoolean("deleted", rih.isDeleted());
                        msg.setString("retInDate", DateUtil.toDateStr(rih.getRetInDate(), "yyyy-MM-dd"));
                        //msg.setDouble("vouTotal", rih.getVouTotal());
                        msg.setDouble("vouTotal", rih.getBalance());
                        msg.setDouble("payment", rih.getPaid());
                        msg.setString("currency", rih.getCurrency().getCurrencyAccId());
                        if (rih.getCustomer().getTraderGroup() != null) {
                            msg.setString("sourceAccId", rih.getCustomer().getTraderGroup().getAccountId());
                        } else {
                            msg.setString("sourceAccId", "-");
                        }*/
                        msg.setString("queueName", "INVENTORY");
                        /*msg.setString("dept", "-");
                        if (rih.getCustomer().getTraderGroup() != null) {
                            if (rih.getCustomer().getTraderGroup().getDeptId() != null) {
                                if (!rih.getCustomer().getTraderGroup().getDeptId().isEmpty()) {
                                    msg.setString("dept", rih.getCustomer().getTraderGroup().getDeptId().trim());
                                }
                            }
                        }*/
                        mq.sendMessage(Global.queueName, msg);
                    } catch (Exception ex) {
                        log.error("uploadToAccount : " + ex.getStackTrace()[0].getLineNumber() + " - " + rih.getRetInId() + " - " + ex);
                    }
                }
            }
        }
    }

    private void setEditStatus(String invId) {
        //canEdit
        /*List<SessionCheckCheckpoint> list = dao.findAllHSQL(
                "select o from SessionCheckCheckpoint o where o.tranOption = 'PHARMACY-Return In' "
                + " and o.tranInvId = '" + invId + "'");*/
        boolean isAllowEdit = Util1.hashPrivilege("RetInCreditVoucherEdit");
        double vouPaid = NumberUtil.NZero(currRetIn.getPaid());

        if (!Util1.hashPrivilege("CanEditReturnCheckPoint")) {
            if (currRetIn != null) {
                if (currRetIn.getAdmissionNo() != null) {
                    if (!currRetIn.getAdmissionNo().trim().isEmpty()) {
                        AdmissionKey key = new AdmissionKey();
                        key.setRegister(currRetIn.getPatient());
                        key.setAmsNo(currRetIn.getAdmissionNo());
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
                                "select * from c_bk_ret_in_his where ret_in_id = '" + invId + "'");
                        if (list != null) {
                            canEdit = list.isEmpty();
                        } else {
                            canEdit = true;
                        }
                    } catch (Exception ex) {
                        log.error("setEditStatus Check BK data " + invId + " : " + ex.toString());
                    } finally {
                        dao.close();
                    }
                }
            }
        } else {
            canEdit = true;
        }
    }

    private void applySecurityPolicy() {
        txtCusId.setEditable(canEdit);
        txtCusName.setEditable(canEdit);
        txtRemark.setEditable(canEdit);
        cboLocation.setEnabled(canEdit);
        cboPayment.setEnabled(canEdit);
        cboCurrency.setEnabled(canEdit);
        butGSI.setEnabled(canEdit);
        /*if (lblStatus.getText().equals("NEW")) {
            
        }else{
            if (canEdit) {

            } else {

            }
        }*/
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
                List<Trader> listTrader = dao.findAllHSQL("select o from Trader o where "
                        + "o.active = true and o.traderId in (select a.key.traderId "
                        + "from LocationTraderMapping a where a.key.locationId = "
                        + locationId + ") and " + strFieldName + " = '" + traderId + "' order by o.traderName");
                if (listTrader != null) {
                    if (!listTrader.isEmpty()) {
                        cus = listTrader.get(0);
                    }
                }
            } else {
                List<Trader> listTrader = dao.findAllHSQL("select o from Trader o where " + strFieldName + " = '" + traderId + "'");
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

    private void updateVouTotal(String vouNo) {
        String strSql = "update ret_in_his rih\n"
                + "join (select vou_no, sum(ifnull(ret_in_amount,0)) as ttl_amt \n"
                + "from ret_in_detail_his where vou_no = '" + vouNo + "' group by vou_no) rd\n"
                + "on rih.ret_in_id = rd.vou_no set rih.vou_total = rd.ttl_amt\n"
                + "where rih.ret_in_id = '" + vouNo + "'";
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

    /*
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
        txtReturnInDate = new javax.swing.JFormattedTextField();
        lblPatient = new javax.swing.JLabel();
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
        butGSI = new javax.swing.JButton();
        txtAdmissionNo = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        lblOTID = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRetIn = new javax.swing.JTable(retInTableModel);
        jPanel3 = new javax.swing.JPanel();
        txtVouTotal = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtVouPaid = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        txtVouBalance = new javax.swing.JFormattedTextField();
        jLabel11 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        chkA4 = new javax.swing.JCheckBox();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Vou No");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Return In Date ");

        txtReturnInDate.setEditable(false);
        txtReturnInDate.setFont(Global.textFont);
        txtReturnInDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtReturnInDateMouseClicked(evt);
            }
        });

        lblPatient.setFont(Global.lableFont);
        lblPatient.setText("Customer No/Name");

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

        butGSI.setText("Get Sale Item");
        butGSI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butGSIActionPerformed(evt);
            }
        });

        txtAdmissionNo.setEditable(false);
        txtAdmissionNo.setFont(Global.textFont);

        jLabel4.setText("Bill ID ");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel5)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txtRemark))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jLabel1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(txtVouNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 137, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel1Layout.createSequentialGroup()
                                .add(lblPatient)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(txtCusId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jLabel2)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(txtReturnInDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 169, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(txtCusName))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel7))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(jPanel1Layout.createSequentialGroup()
                            .add(cboLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 127, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(txtAdmissionNo))
                        .add(jPanel1Layout.createSequentialGroup()
                            .add(cboCurrency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 122, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                            .add(butGSI)))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(cboPayment, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(lblOTID, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 79, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(new java.awt.Component[] {cboCurrency, cboLocation, cboPayment}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(txtVouNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2)
                    .add(txtReturnInDate, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7)
                    .add(cboLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txtAdmissionNo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lblOTID, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(cboPayment, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel6)
                        .add(jLabel4))
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(txtCusId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(txtCusName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(lblPatient)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(txtRemark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(cboCurrency, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(butGSI)))
        );

        tblRetIn.setFont(Global.textFont);
        tblRetIn.setModel(retInTableModel);
        tblRetIn.setRowHeight(23);
        tblRetIn.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tblRetInFocusLost(evt);
            }
        });
        jScrollPane1.setViewportView(tblRetIn);

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
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
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

        chkA4.setText("A4");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(lblStatus, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 193, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(12, 12, 12)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                        .add(chkA4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(jLabel8))
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
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(txtVouTotal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(jLabel8))
                            .add(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .add(chkA4)))
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

    private void txtReturnInDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtReturnInDateMouseClicked
        if (evt.getClickCount() == mouseClick) {
            if (!canEdit) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Check point found. You cannot edit.",
                        "Check Point", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtReturnInDate.setText(strDate);
                if (lblStatus.getText().equals("NEW")) {
                    vouEngine.setPeriod(DateUtil.getPeriod(txtReturnInDate.getText()));
                    genVouNo();
                }
            }
        }
    }//GEN-LAST:event_txtReturnInDateMouseClicked

    private void txtCusIdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusIdMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusIdMouseClicked

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == mouseClick) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusNameMouseClicked

    private void txtCusIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCusIdActionPerformed
        if (txtCusId.getText() == null || txtCusId.getText().isEmpty()) {
            txtCusName.setText(null);
            currRetIn.setCustomer(null);
            currRetIn.setRegNo(null);
            currRetIn.setStuName(null);
            currRetIn.setStuNo(null);
            lblOTID.setText(null);
        } else {
            getCustomer();
        }
    }//GEN-LAST:event_txtCusIdActionPerformed

    private void txtCusIdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtCusIdFocusLost
        getCustomer();
    }//GEN-LAST:event_txtCusIdFocusLost

    private void cboPaymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPaymentActionPerformed
        if (currRetIn != null) {
            if (currRetIn.getCustomer() != null) {
                if (currRetIn.getCustomer().getTraderId().equals(Util1.getPropValue("system.default.customer"))) {
                    PaymentType pt = (PaymentType) cboPayment.getSelectedItem();
                    if (!pt.getPaymentTypeId().equals(ptCash.getPaymentTypeId())) {
                        cboPayment.setSelectedItem(ptCash);
                    }
                }
            }
        }
        calculateTotalAmount();
    }//GEN-LAST:event_cboPaymentActionPerformed

    private void butGSIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butGSIActionPerformed
        if (!txtCusId.getText().isEmpty()) {
            ReturnInItemSearchDialog dialog = new ReturnInItemSearchDialog(this,
                    txtCusId.getText(), txtCusName.getText());
        } else {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select trader.",
                    "No trader", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_butGSIActionPerformed

    private void tblRetInFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tblRetInFocusLost
        /*try{
         if(tblRetIn.getCellEditor() != null){
         tblRetIn.getCellEditor().stopCellEditing();
         }
         }catch(Exception ex){
            
         }*/
    }//GEN-LAST:event_tblRetInFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butGSI;
    private javax.swing.JComboBox cboCurrency;
    private javax.swing.JComboBox cboLocation;
    private javax.swing.JComboBox cboPayment;
    private javax.swing.JCheckBox chkA4;
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
    private javax.swing.JLabel lblOTID;
    private javax.swing.JLabel lblPatient;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblRetIn;
    private javax.swing.JTextField txtAdmissionNo;
    private javax.swing.JTextField txtCusId;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtReturnInDate;
    private javax.swing.JFormattedTextField txtVouBalance;
    private javax.swing.JFormattedTextField txtVouNo;
    private javax.swing.JFormattedTextField txtVouPaid;
    private javax.swing.JFormattedTextField txtVouTotal;
    // End of variables declaration//GEN-END:variables
}
