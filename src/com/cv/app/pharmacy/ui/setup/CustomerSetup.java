/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.NumberKeyListener;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.BusinessType;
import com.cv.app.pharmacy.database.entity.Customer;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.PayMethod;
import com.cv.app.pharmacy.database.entity.Township;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.TraderType;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.TraderTableModel;
import com.cv.app.pharmacy.ui.util.GenChildCustomer;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.richclient.application.Application;

/**
 *
 * @author WSwe
 */
public class CustomerSetup extends javax.swing.JPanel implements FormAction, KeyPropagate {

    static Logger log = Logger.getLogger(CustomerSetup.class.getName());
    private Customer currCustomer = new Customer();
    private final AbstractDataAccess dao = Global.dao;
    private BestAppFocusTraversalPolicy focusPolicy;
    private TableRowSorter<TableModel> sorter;
    private TraderTableModel tableModel = new TraderTableModel();
    private int selectRow = -1;
    private boolean statusFilter = false;
    private boolean bindStatus = false;
    private StartWithRowFilter swrf;
    //private List<CustomerGroup> listCG = new ArrayList();

    /**
     * Creates new form CustomerSetup
     */
    public CustomerSetup() {
        initComponents();

        try {
            swrf = new StartWithRowFilter(txtFilter);
            initCombo();
            applyFocusPolicy();
            AddFocusMoveKey();
            this.setFocusTraversalPolicy(focusPolicy);
            lblStatus.setText("NEW");
            chkActive.setSelected(true);
            assignCusId();
            dao.close();
            sorter = new TableRowSorter(tblCustomer.getModel());
            tblCustomer.setRowSorter(sorter);
            actionMapping();
            txtCreditLimit.addKeyListener(new NumberKeyListener());
            txtCreditDays.addKeyListener(new NumberKeyListener());
            //cboGroup.setEnabled(false);
            butGCC.setVisible(false);
            butUploadAll.setVisible(false);
            cboGroup.setSelectedItem(null);
            if (Util1.getPropValue("system.customer.code.editable").equals("True")) {
                txtId.setEditable(true);
            }
            chkFilter.setSelected(true);
            initTable();
        } catch (Exception ex) {
            log.error("CustomerSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    public void setCurrCustomer(Customer currCustomer) {
        this.currCustomer = currCustomer;
        txtId.setText(currCustomer.getTraderId());
        cboGroup.setSelectedItem(currCustomer.getTraderGroup());
        cboBusinessType.setSelectedItem(currCustomer.getBusinessType());
        txtName.setText(currCustomer.getTraderName());
        txtAddress.setText(currCustomer.getAddress());
        txtPhone.setText(currCustomer.getPhone());
        txtEMail.setText(currCustomer.getEmail());
        cboPayMethod.setSelectedItem(currCustomer.getPayMethod());
        txtRemark.setText(currCustomer.getRemark());
        txtId.setEnabled(false);

        if (currCustomer.getCreditLimit() != null) {
            txtCreditLimit.setText(currCustomer.getCreditLimit().toString());
        } else {
            txtCreditLimit.setText(null);
        }

        if (currCustomer.getCreditDays() != null) {
            txtCreditDays.setText(currCustomer.getCreditDays().toString());
        } else {
            txtCreditDays.setText(null);
        }

        String strBaseGroup = Util1.getPropValue("system.cus.base.group");
        if (strBaseGroup.isEmpty()) {
            strBaseGroup = "-";
        }
        /*if (currCustomer.getTraderGroup() == null) {
            butGCC.setEnabled(true);
        } else if (strBaseGroup.equals(currCustomer.getTraderGroup().getGroupId())) {
            butGCC.setEnabled(true);
        } else {
            butGCC.setEnabled(false);
        }*/
        cboType.setSelectedItem(currCustomer.getTypeId());
        txtParent.setText(currCustomer.getParent());
        txtAccountId.setText(currCustomer.getAccountCode());
        chkActive.setSelected(currCustomer.getActive());
        cboTownship.setSelectedItem(currCustomer.getTownship());
        txtCode.setText(currCustomer.getStuCode());
    }

    private void clear() {
        System.out.println("Clear");
        tblCustomer.clearSelection();
        currCustomer = new Customer();
        lblStatus.setText("NEW");
        chkActive.setSelected(true);
        txtId.setEnabled(true);
        txtId.setText(null);
        cboGroup.setSelectedItem(null);
        txtName.setText(null);
        txtAddress.setText(null);
        txtPhone.setText(null);
        txtEMail.setText(null);
        txtCreditLimit.setText(null);
        txtCreditDays.setText(null);
        cboType.setSelectedItem(null);
        txtParent.setText(null);
        txtAccountId.setText(null);
        txtRemark.setText(null);
        cboTownship.setSelectedItem(null);
        txtCode.setText(null);
        selectRow = -1;
        //butGCC.setEnabled(false);
        setFocus();
    }

    private void initTable() {
        filterItem();
        //tableModel.setListTrader(dao.findAllHSQL("select o from Customer o"));

        //Adjust table column width
        TableColumn column;
        column = tblCustomer.getColumnModel().getColumn(0);
        column.setPreferredWidth(60);

        column = tblCustomer.getColumnModel().getColumn(1);
        column.setPreferredWidth(300);

        column = tblCustomer.getColumnModel().getColumn(2);
        column.setPreferredWidth(20);

        //Define table selection model to single row selection.
        tblCustomer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblCustomer.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblCustomer.getSelectedRow() >= 0) {
                    selectRow = tblCustomer.convertRowIndexToModel(tblCustomer.getSelectedRow());
                }

                if (selectRow >= 0) {
                    setCurrCustomer((Customer) tableModel.getTrader(selectRow));
                    lblStatus.setText("EDIT");
                }
            }
        });
    }

    private void initCombo() {
        try {
            bindStatus = true;

            BindingUtil.BindCombo(cboGroup,
                    dao.findAllHSQL("select o from CustomerGroup o where o.useFor = 'CUS' order by o.groupName"));
            BindingUtil.BindComboFilter(cboCusGroup,
                    dao.findAllHSQL("select o from CustomerGroup o where o.useFor = 'CUS' order by o.groupName"));
            BindingUtil.BindCombo(cboType, dao.findAll("TraderType"));
            BindingUtil.BindCombo(cboTownship, dao.findAllHSQL("select o from Township o order by o.townshipName"));
            BindingUtil.BindCombo(cboPayMethod, dao.findAll("PayMethod"));
            BindingUtil.BindCombo(cboBusinessType, dao.findAllHSQL("select o from BusinessType o order by o.description"));
            BindingUtil.BindComboFilter(cboLocation, dao.findAllHSQL("select o from Location o order by o.locationName"));
            BindingUtil.BindComboFilter(cboFilterTownship, dao.findAllHSQL("select o from Township o order by o.townshipName"));

            new ComBoBoxAutoComplete(cboGroup, this);
            new ComBoBoxAutoComplete(cboType, this);
            new ComBoBoxAutoComplete(cboTownship, this);
            new ComBoBoxAutoComplete(cboPayMethod, this);
            new ComBoBoxAutoComplete(cboBusinessType, this);

            bindStatus = false;
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void showDialog(String panelName) {
        SetupDialog dialog = new SetupDialog(Application.instance().getActiveWindow().getControl(),
                true, panelName);

        //Calculate dialog position to centre.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        int x = (screen.width - dialog.getWidth()) / 2;
        int y = (screen.height - dialog.getHeight()) / 2;

        dialog.setLocation(x, y);
        dialog.show();
        System.out.println("After dialog close");
    }

    public void setFocus() {
        cboGroup.requestFocusInWindow();
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        focusOrder.add(txtName);
        focusOrder.add(txtAddress);
        focusOrder.add(txtPhone);
        focusOrder.add(txtEMail);
        focusOrder.add(txtCreditLimit);
        focusOrder.add(txtCreditDays);
        focusOrder.add(txtParent);
        focusOrder.add(txtAccountId);
        focusOrder.add(chkActive);

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

        if (Util1.getPropValue("system.sale.emitted.prifix").equals("Y")) {
            if (txtCode.getText() == null) {
                status = false;
                JOptionPane.showMessageDialog(this, "Code cannot be blank.",
                        "Blank", JOptionPane.ERROR_MESSAGE);
                txtCode.requestFocus();
                return status;
            } else if (txtCode.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Code cannot be blank.",
                        "Blank", JOptionPane.ERROR_MESSAGE);
                txtCode.requestFocus();
                return status;
            }

            if (txtCode.getText() != null) {
                if (!txtCode.getText().trim().isEmpty()) {
                    String strCode = txtCode.getText().trim();
                    try {
                        List<Trader> listTr = dao.findAllHSQL("select o from Trader o where o.stuCode = '"
                                + strCode + "' and o.traderId <> '" + txtId.getText() + "'");
                        if (listTr != null) {
                            if (!listTr.isEmpty()) {
                                status = false;
                                JOptionPane.showMessageDialog(this, "Duplicate code.",
                                        "Duplicate", JOptionPane.ERROR_MESSAGE);
                                return status;
                            }
                        }
                    } catch (Exception ex) {
                        log.error("isValidEntry : " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                }
            }
        }

        if (Util1.nullToBlankStr(txtName.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(this, "Name cannot be blank or null.",
                    "Name null error.", JOptionPane.ERROR_MESSAGE);
            txtName.requestFocusInWindow();
        } else if (!NumberUtil.isNumber(txtCreditLimit.getText().trim())) {
            status = false;
            JOptionPane.showMessageDialog(this, "Credit Limit should be number.",
                    "Not number.", JOptionPane.ERROR_MESSAGE);
        } else if (NumberUtil.NZero(txtCreditLimit.getText().trim()) < 0) {
            status = false;
            JOptionPane.showMessageDialog(this, "Credit Limit cannot be minus value.",
                    "Minus error.", JOptionPane.ERROR_MESSAGE);
            txtCreditLimit.requestFocusInWindow();
        } else if (!NumberUtil.isNumber(txtCreditDays.getText().trim())) {
            status = false;
            JOptionPane.showMessageDialog(this, "Credit day should be number.",
                    "Not number.", JOptionPane.ERROR_MESSAGE);
        } else if (NumberUtil.NZeroInt(txtCreditDays.getText().trim()) < 0) {
            status = false;
            JOptionPane.showMessageDialog(this, "Credit days cannot be minus value.",
                    "Minus error.", JOptionPane.ERROR_MESSAGE);
        } else if (!isDuplicate(txtId.getText().trim())) {
            currCustomer.setTraderId(txtId.getText());

            if (cboGroup.getSelectedItem() != null) {
                currCustomer.setTraderGroup((CustomerGroup) cboGroup.getSelectedItem());
            } else {
                currCustomer.setTraderGroup(null);
            }

            if (cboBusinessType.getSelectedItem() != null) {
                currCustomer.setBusinessType((BusinessType) cboBusinessType.getSelectedItem());
            } else {
                currCustomer.setBusinessType(null);
            }

            currCustomer.setTraderName(txtName.getText().trim());

            if (!txtAddress.getText().isEmpty()) {
                currCustomer.setAddress(txtAddress.getText().trim());
            } else {
                currCustomer.setAddress(null);
            }

            if (!txtPhone.getText().isEmpty()) {
                currCustomer.setPhone(txtPhone.getText().trim());
            } else {
                currCustomer.setPhone(null);
            }

            if (!txtEMail.getText().isEmpty()) {
                currCustomer.setEmail(txtEMail.getText().trim());
            } else {
                currCustomer.setEmail(null);
            }

            if (!txtCreditLimit.getText().isEmpty()) {
                currCustomer.setCreditLimit(NumberUtil.NZeroInt(txtCreditLimit.getText().trim()));
            } else {
                currCustomer.setCreditLimit(null);
            }

            if (!txtCreditDays.getText().isEmpty()) {
                currCustomer.setCreditDays(NumberUtil.NZeroInt(txtCreditDays.getText().trim()));
            } else {
                currCustomer.setCreditDays(null);
            }

            if (cboType.getSelectedItem() != null) {
                currCustomer.setTypeId((TraderType) cboType.getSelectedItem());
            } else {
                currCustomer.setTypeId(null);
            }

            if (!txtParent.getText().isEmpty()) {
                currCustomer.setParent(txtParent.getText().trim());
            } else {
                currCustomer.setParent(null);
            }

            if (!txtAccountId.getText().isEmpty()) {
                currCustomer.setAccountCode(txtAccountId.getText().trim());
            } else {
                currCustomer.setAccountCode(null);
            }

            currCustomer.setActive(chkActive.isSelected());
            currCustomer.setTownship((Township) cboTownship.getSelectedItem());
            currCustomer.setPayMethod((PayMethod) cboPayMethod.getSelectedItem());
            currCustomer.setRemark(txtRemark.getText());
            if (lblStatus.getText().equals("NEW")) {
                currCustomer.setCreatedBy(Global.loginUser.getUserId());
                currCustomer.setCreatedDate(DateUtil.toDateTime(DateUtil.getTodayDateStr()));
            } else {
                currCustomer.setUpdatedBy(Global.loginUser.getUserId());
            }
            currCustomer.setUpdatedDate(DateUtil.toDateTime(DateUtil.getTodayDateStr()));
            if (txtCode.getText() != null) {
                currCustomer.setStuCode(txtCode.getText().trim());
            } else {
                currCustomer.setStuCode(null);
            }
        }

        return status;
    }

    private boolean isDuplicate(String traderId) {
        boolean status = false;

        try {
            if (lblStatus.getText().equals("NEW")) {
                List<Trader> listT = dao.findAllHSQL("select o from Trader o where o.traderId = '"
                        + traderId + "'");
                if (listT != null) {
                    if (!listT.isEmpty()) {
                        status = true;
                        JOptionPane.showMessageDialog(this, "Duplicate customer id. You cannot save.",
                                "Duplicate", JOptionPane.ERROR_MESSAGE);
                        log.error("isDuplicate : " + traderId);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("isDuplicate : " + ex.getMessage());
        } finally {
            dao.close();
        }

        return status;
    }

    private void assignCusId() {
        String strFilter;
        Object maxCusId;
        CustomerGroup cg = (CustomerGroup) cboGroup.getSelectedItem();

        int maxLength = NumberUtil.NZeroInt(Util1.getPropValue("system.customer.code.length"));

        try {
            String strBaseGroup = Util1.getPropValue("system.cus.base.group");
            if (strBaseGroup.isEmpty()) {
                strBaseGroup = "-";
            }
            if (cg != null) {
                if (cg.getGroupId().equals(strBaseGroup)) {
                    cg = null;
                }
            }
            if (cg == null) {
                strFilter = "group_id is null and discriminator = 'C'";
            } else {
                strFilter = "group_id = '" + cg.getGroupId() + "' and discriminator = 'C'";
            }

            if (cg != null) {
                maxCusId = dao.getMax("CONVERT(replace(trader_id,'CUS" + cg.getGroupId() + "', ''),UNSIGNED INTEGER)",
                        "trader", strFilter);
            } else {
                maxCusId = dao.getMax("trader_id", "trader", strFilter);
            }

            if (maxCusId == null) {
                //maxCusId = "001";
                int id = 1;
                maxCusId = String.format("%0" + maxLength + "d", id);
            } else {
                String strCusIdSerial;

                if (cg != null) {
                    maxCusId = maxCusId.toString().replace(cg.getGroupId(), "");
                }

                Integer tmpSerial = NumberUtil.getNumber(maxCusId.toString()) + 1;

                strCusIdSerial = tmpSerial.toString();
                int i = strCusIdSerial.length();

                for (; i < maxLength; i++) {
                    strCusIdSerial = "0" + strCusIdSerial;
                }

                /*if (strCusIdSerial.length() == 1) {
                 strCusIdSerial = "00" + strCusIdSerial;
                 } else if (strCusIdSerial.length() == 2) {
                 strCusIdSerial = "0" + strCusIdSerial;
                 }*/
                maxCusId = strCusIdSerial;
            }

            if (cg == null) {
                txtId.setText("CUS" + maxCusId.toString());
            } else {
                txtId.setText("CUS" + cg.getGroupId() + maxCusId.toString());
            }
        } catch (Exception ex) {
            log.error("assignCusId : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    @Override
    public void save() {
        try {
            if (isValidEntry()) {

                //For BK Pagolay
                if (lblStatus.getText().equals("EDIT")) {
                    Date d = new Date();
                    dao.execProc("bktrader",
                            currCustomer.getTraderId(),
                            DateUtil.toDateTimeStrMYSQL(d));
                }
                //For BK Pagolay
                dao.save(currCustomer);

                //For integration with account
                if (Global.mqConnection != null) {
                    ActiveMQConnection mq = Global.mqConnection;
                    if (mq.isStatus()) {
                        uploadCustomer(currCustomer);
                    }
                }
                //=====================================================

                tblCustomer.setRowSorter(null);

                /*if (lblStatus.getText().equals("NEW")) {
                    tableModel.addTrader(currCustomer);
                } else {
                    tableModel.setTrader(selectRow, currCustomer);
                }*/
                tableModel.setListTrader(dao.findAllHSQL("select o from Customer o order by o.traderName"));
                tblCustomer.setRowSorter(sorter);
                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Customer Name", JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } catch (Exception ex) {
            log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    @Override
    public void newForm() {
        clear();
    }

    @Override
    public void history() {
    }

    @Override
    public void delete() {
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Customer Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(currCustomer);

                    //For integration with account
                    if (Global.mqConnection != null) {
                        ActiveMQConnection mq = Global.mqConnection;
                        if (mq.isStatus()) {
                            currCustomer.setActive(false);
                            uploadCustomer(currCustomer);
                        }
                    }
                    //=====================================================

                    int tmpRow = selectRow;
                    selectRow = -1;
                    tblCustomer.setRowSorter(null);
                    tableModel.deleteTrader(tmpRow);
                    tblCustomer.setRowSorter(sorter);
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete this customer.",
                        "Customer Delete", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }

        clear();
    }

    @Override
    public void deleteCopy() {
    }

    @Override
    public void print() {
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

    private void actionMapping() {
        formActionKeyMapping(txtId);
        formActionKeyMapping(txtFilter);
        formActionKeyMapping(cboGroup);
        formActionKeyMapping(txtName);
        formActionKeyMapping(txtAddress);
        formActionKeyMapping(txtPhone);
        formActionKeyMapping(txtEMail);
        formActionKeyMapping(txtCreditLimit);
        formActionKeyMapping(txtCreditDays);
        formActionKeyMapping(cboType);
        formActionKeyMapping(txtParent);
        formActionKeyMapping(txtAccountId);
        formActionKeyMapping(chkActive);
        formActionKeyMapping(tblCustomer);
    }

    private void formActionKeyMapping(JComponent jc) {
        //Save
        jc.getInputMap().put(KeyStroke.getKeyStroke("F5"), "F5-Action");
        jc.getActionMap().put("F5-Action", actionSave);

        //Clear
        jc.getInputMap().put(KeyStroke.getKeyStroke("F10"), "F10-Action");
        jc.getActionMap().put("F10-Action", actionNewForm);
    }
    private Action actionSave = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            save();
        }
    };
    private Action actionNewForm = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            newForm();
        }
    };

    private void filterItem() {
        //tableModel.setListTrader(dao.findAll("Customer", strFilter));
        //statusFilter = true;
        tblCustomer.getSelectionModel().clearSelection();
        String filter = "";
        if (chkFilter.isSelected()) {
            if (filter.isEmpty()) {
                filter = "o.active = true";
            } else {
                filter = filter + " and o.active = true";
            }
        } else if (filter.isEmpty()) {
            filter = "o.active = false";
        } else {
            filter = filter + " and o.active = false";
        }

        if (cboLocation.getSelectedItem() instanceof Location) {
            int locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
            if (filter.isEmpty()) {
                filter = "o.traderId in (select a.key.traderId "
                        + "from LocationTraderMapping a where a.key.locationId = "
                        + locationId + ")";
            } else {
                filter = filter + " and o.traderId in (select a.key.traderId "
                        + "from LocationTraderMapping a where a.key.locationId = "
                        + locationId + ")";
            }
        }

        if (cboFilterTownship.getSelectedItem() instanceof Township) {
            int id = ((Township) cboFilterTownship.getSelectedItem()).getTownshipId();
            if (filter.isEmpty()) {
                filter = "o.township.townshipId = " + id;
            } else {
                filter = filter + " and o.township.townshipId = " + id;
            }
        }

        if (cboCusGroup.getSelectedItem() instanceof CustomerGroup) {
            String group = ((CustomerGroup) cboCusGroup.getSelectedItem()).getGroupId();
            if (filter.isEmpty()) {
                filter = "o.traderGroup.groupId = '" + group + "'";
            } else {
                filter = filter + " and o.traderGroup.groupId = '" + group + "'";
            }
        }

        log.info("filter : " + filter);
        if (filter.isEmpty()) {
            filter = "select o from Customer o where o.discrimator = 'C' order by o.traderName";
        } else {
            filter = "select o from Customer o where o.discrimator = 'C' and " + filter;
        }

        try {
            List<Trader> listCUS = dao.findAllHSQL(filter);
            tableModel.setListTrader(listCUS);
        } catch (Exception ex) {
            log.error("filterItem : " + ex.getMessage());
        } finally {
            dao.close();
        }
        statusFilter = true;
    }

    private void uploadCustomer(Customer cus) {
        try {
            ActiveMQConnection mq = Global.mqConnection;
            MapMessage msg = mq.getMapMessageTemplate();

            msg.setString("program", Global.programId);
            msg.setString("entity", "TRADER");
            if (cus.getAccountId() != null) {
                msg.setString("accountId", cus.getAccountId());
            }
            msg.setString("queueName", "INVENTORY");
            msg.setString("name", cus.getTraderName());
            msg.setBoolean("active", cus.getActive());
            msg.setString("address", cus.getAddress());
            msg.setString("CODE", cus.getTraderId());
            if (cus.getTraderGroup() != null) {
                msg.setString("coa", cus.getTraderGroup().getAccountId());
            } else {
                msg.setString("coa", "-");
            }
            mq.sendMessage(Global.queueName, msg);
        } catch (JMSException ex) {
            log.error("uploadCustomer : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblCustomer = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAddress = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        txtPhone = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtEMail = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtCreditLimit = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtCreditDays = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cboGroup = new javax.swing.JComboBox();
        jLabel9 = new javax.swing.JLabel();
        txtParent = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        txtFilter = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtAccountId = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        cboType = new javax.swing.JComboBox();
        lblStatus = new javax.swing.JLabel();
        butGroup = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        cboTownship = new javax.swing.JComboBox();
        butTownship = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        cboPayMethod = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        butGCC = new javax.swing.JButton();
        butUploadAll = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        butBusiness = new javax.swing.JButton();
        cboBusinessType = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        txtCode = new javax.swing.JTextField();
        cboFilterTownship = new javax.swing.JComboBox<>();
        cboLocation = new javax.swing.JComboBox<>();
        chkFilter = new javax.swing.JCheckBox();
        cboCusGroup = new javax.swing.JComboBox<>();

        tblCustomer.setFont(Global.textFont);
        tblCustomer.setModel(tableModel);
        tblCustomer.setRowHeight(23);
        jScrollPane1.setViewportView(tblCustomer);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("ID");

        txtId.setEditable(false);
        txtId.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Name");

        txtName.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Address");

        txtAddress.setColumns(20);
        txtAddress.setFont(Global.textFont);
        txtAddress.setRows(5);
        jScrollPane2.setViewportView(txtAddress);

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Phone");

        txtPhone.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("E-Mail");

        txtEMail.setFont(Global.textFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Credit Limit");

        txtCreditLimit.setFont(Global.textFont);
        txtCreditLimit.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Credit Days");

        txtCreditDays.setFont(Global.textFont);
        txtCreditDays.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Group");

        cboGroup.setFont(Global.textFont);
        cboGroup.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboGroupItemStateChanged(evt);
            }
        });
        cboGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboGroupActionPerformed(evt);
            }
        });

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Parent");

        txtParent.setFont(Global.textFont);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Active");

        txtFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtFilter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFilterMouseClicked(evt);
            }
        });
        txtFilter.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtFilterInputMethodTextChanged(evt);
            }
        });
        txtFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilterActionPerformed(evt);
            }
        });
        txtFilter.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                txtFilterPropertyChange(evt);
            }
        });
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtFilterKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtFilterKeyTyped(evt);
            }
        });

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Acc ID");

        txtAccountId.setFont(Global.textFont);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Price Type");

        cboType.setFont(Global.textFont);

        lblStatus.setText("NEW");

        butGroup.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butGroup.setText("...");
        butGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butGroupActionPerformed(evt);
            }
        });

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Township ");

        cboTownship.setFont(Global.textFont);
        cboTownship.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTownshipActionPerformed(evt);
            }
        });

        butTownship.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butTownship.setText("...");
        butTownship.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butTownshipActionPerformed(evt);
            }
        });

        jLabel14.setFont(Global.lableFont);
        jLabel14.setText("Pay Method");

        cboPayMethod.setFont(Global.textFont);

        jLabel15.setFont(Global.lableFont);
        jLabel15.setText("Remark");

        txtRemark.setFont(Global.textFont);

        butGCC.setText("Gen-Child-Cus");
        butGCC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butGCCActionPerformed(evt);
            }
        });

        butUploadAll.setText("Upload All");
        butUploadAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butUploadAllActionPerformed(evt);
            }
        });

        jLabel16.setText("Business");
        jLabel16.setFont(Global.lableFont);

        butBusiness.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butBusiness.setText("...");
        butBusiness.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBusinessActionPerformed(evt);
            }
        });

        cboBusinessType.setFont(Global.textFont);

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Code");

        txtCode.setFont(Global.textFont);

        cboFilterTownship.setFont(Global.textFont);
        cboFilterTownship.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboFilterTownshipActionPerformed(evt);
            }
        });

        cboLocation.setFont(Global.textFont);
        cboLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLocationActionPerformed(evt);
            }
        });

        chkFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkFilterActionPerformed(evt);
            }
        });

        cboCusGroup.setFont(Global.textFont);
        cboCusGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCusGroupActionPerformed(evt);
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
                        .addComponent(txtFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboFilterTownship, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkFilter)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtId, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboGroup, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butGroup))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCode))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel11)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtParent)
                            .addComponent(txtAccountId)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cboTownship, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(butTownship))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkActive)
                        .addGap(18, 18, 18)
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboPayMethod, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtRemark)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butUploadAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butGCC))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel12)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cboBusinessType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(butBusiness))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                            .addComponent(txtCreditDays)
                            .addComponent(txtCreditLimit)
                            .addComponent(txtEMail)
                            .addComponent(txtPhone)
                            .addComponent(txtName)
                            .addComponent(cboType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel10, jLabel11, jLabel12, jLabel17, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabel9});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboFilterTownship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkFilter)
                    .addComponent(cboCusGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel8)
                                .addComponent(butGroup))
                            .addComponent(cboGroup, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(butBusiness)
                                .addComponent(cboBusinessType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel16)))
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtEMail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtCreditLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(txtCreditDays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(txtParent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(txtAccountId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(cboTownship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(butTownship))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(cboPayMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(chkActive, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butGCC)
                            .addComponent(butUploadAll))
                        .addGap(0, 55, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {butGroup, cboBusinessType, cboGroup});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {txtCode, txtId});

    }// </editor-fold>//GEN-END:initComponents

    private void cboGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboGroupActionPerformed
        try {
            if (currCustomer != null) {
                if (currCustomer.getTraderId() == null) {
                    if (!bindStatus) {
                        assignCusId();
                    }
                }
            }
            dao.close();
        } catch (Exception ex) {
            log.error("cboGroupActionPerformed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }//GEN-LAST:event_cboGroupActionPerformed

    private void cboGroupItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboGroupItemStateChanged
        System.out.println("Item State Changed");
    }//GEN-LAST:event_cboGroupItemStateChanged

  private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
      /*if (statusFilter) {
          tableModel.setListTrader(dao.findAll("Customer"));
          statusFilter = false;
      }*/

      if (txtFilter.getText().isEmpty()) {
          sorter.setRowFilter(null);
      } else if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
          sorter.setRowFilter(swrf);
      } else {
          sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
      }
  }//GEN-LAST:event_txtFilterKeyReleased

    private void butGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butGroupActionPerformed
        try {
            showDialog("Customer Group Setup");
            CustomerGroup cg = (CustomerGroup) cboGroup.getSelectedItem();
            BindingUtil.BindCombo(cboGroup,
                    dao.findAllHSQL("select o from CustomerGroup o where o.useFor = 'CUS' order by o.groupName"));
            cboGroup.setSelectedItem(cg);
        } catch (Exception ex) {
            log.error("butGroupActionPerformed : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butGroupActionPerformed

  private void butTownshipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butTownshipActionPerformed
      try {
          TownshipSetup setup = new TownshipSetup();
          setup.setVisible(true);
          Township ts = (Township) cboTownship.getSelectedItem();
          BindingUtil.BindCombo(cboTownship, dao.findAll("Township"));
          cboTownship.setSelectedItem(ts);
      } catch (Exception ex) {
          log.error("butTownshipActionPerformed : " + ex.getMessage());
      } finally {
          dao.close();
      }
  }//GEN-LAST:event_butTownshipActionPerformed

    private void butGCCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butGCCActionPerformed
        GenChildCustomer gcc = new GenChildCustomer(currCustomer, dao);
        gcc = null;
    }//GEN-LAST:event_butGCCActionPerformed

    private void butUploadAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUploadAllActionPerformed
        List<Trader> listCus = tableModel.getListTrader();
        if (listCus != null) {
            if (Global.mqConnection != null) {
                ActiveMQConnection mq = Global.mqConnection;
                if (mq.isStatus()) {
                    //uploadCustomer((Customer) listCus.get(1));
                    for (Trader tr : listCus) {
                        uploadCustomer((Customer) tr);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "ActiveMQ is not connected.",
                            "ActiveMQ", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "ActiveMQ is not initialize.",
                        "ActiveMQ", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No customer list.",
                    "Customer", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_butUploadAllActionPerformed

    private void butBusinessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBusinessActionPerformed
        // TODO add your handling code here:
        //showDialog("Business Type SetUp");
        try {
            BusinessTypeSetup setup = new BusinessTypeSetup();
            setup.setSize(800, 600);
            setup.setTitle("Business Type Setup");
            setup.setLocationRelativeTo(this);
            setup.setVisible(true);
            BusinessType bst = (BusinessType) cboBusinessType.getSelectedItem();
            BindingUtil.BindCombo(cboBusinessType, dao.findAll("BusinessType"));
            cboBusinessType.setSelectedItem(bst);
        } catch (Exception ex) {
            log.error("butBusinessActionPerformed : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butBusinessActionPerformed

    private void cboTownshipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTownshipActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboTownshipActionPerformed

    private void txtFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterActionPerformed

    private void txtFilterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFilterMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterMouseClicked

    private void txtFilterInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtFilterInputMethodTextChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterInputMethodTextChanged

    private void txtFilterPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_txtFilterPropertyChange
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterPropertyChange

    private void txtFilterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterKeyPressed

    private void txtFilterKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterKeyTyped

    private void cboFilterTownshipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboFilterTownshipActionPerformed
        if (!bindStatus) {
            filterItem();
        }
    }//GEN-LAST:event_cboFilterTownshipActionPerformed

    private void cboLocationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLocationActionPerformed
        if (!bindStatus) {
            filterItem();
        }
    }//GEN-LAST:event_cboLocationActionPerformed

    private void chkFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkFilterActionPerformed
        if (!bindStatus) {
            filterItem();
        }
    }//GEN-LAST:event_chkFilterActionPerformed

    private void cboCusGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCusGroupActionPerformed
        if (!bindStatus) {
            filterItem();
        }
    }//GEN-LAST:event_cboCusGroupActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butBusiness;
    private javax.swing.JButton butGCC;
    private javax.swing.JButton butGroup;
    private javax.swing.JButton butTownship;
    private javax.swing.JButton butUploadAll;
    private javax.swing.JComboBox cboBusinessType;
    private javax.swing.JComboBox<String> cboCusGroup;
    private javax.swing.JComboBox<String> cboFilterTownship;
    private javax.swing.JComboBox cboGroup;
    private javax.swing.JComboBox<String> cboLocation;
    private javax.swing.JComboBox cboPayMethod;
    private javax.swing.JComboBox cboTownship;
    private javax.swing.JComboBox cboType;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JCheckBox chkFilter;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblCustomer;
    private javax.swing.JTextField txtAccountId;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtCreditDays;
    private javax.swing.JTextField txtCreditLimit;
    private javax.swing.JTextField txtEMail;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtParent;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtRemark;
    // End of variables declaration//GEN-END:variables
}
