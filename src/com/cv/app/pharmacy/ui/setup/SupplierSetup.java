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
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.PayMethod;
import com.cv.app.pharmacy.database.entity.Supplier;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.pharmacy.ui.common.TraderTableModel;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.jms.MapMessage;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author WSwe
 */
public class SupplierSetup extends javax.swing.JPanel implements FormAction, KeyPropagate {

    static Logger log = Logger.getLogger(SupplierSetup.class.getName());
    private Supplier currSupplier = new Supplier();
    private final AbstractDataAccess dao = Global.dao;
    private BestAppFocusTraversalPolicy focusPolicy;
    private TableRowSorter<TableModel> sorter;
    private TraderTableModel tableModel = new TraderTableModel();
    private int selectedRow = -1;
    private StartWithRowFilter swrf;

    /**
     * Creates new form CustomerSetup
     */
    public SupplierSetup() {
        initComponents();
        try {
            swrf = new StartWithRowFilter(txtFilter);
            dao.open();
            initTable();
            assignCusId();
            dao.close();
            sorter = new TableRowSorter(tblSupplier.getModel());
            tblSupplier.setRowSorter(sorter);
        } catch (Exception ex) {
            log.error("SupplierSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        applyFocusPolicy();
        AddFocusMoveKey();
        actionMapping();
        this.setFocusTraversalPolicy(focusPolicy);
        initCombo();
        chkActive.setSelected(true);
    }

    public void setCurrSupplier(Supplier currSupplier) {
        this.currSupplier = currSupplier;

        txtAccountId.setText(currSupplier.getAccountCode());
        txtAddress.setText(currSupplier.getAddress());
        txtEMail.setText(currSupplier.getEmail());
        txtId.setText(currSupplier.getTraderId());
        txtName.setText(currSupplier.getTraderName());
        txtParent.setText(currSupplier.getParent());
        txtPhone.setText(currSupplier.getPhone());
        chkActive.setSelected(currSupplier.getActive());
        cboPayMethod.setSelectedItem(currSupplier.getPayMethod());
        cboGroup.setSelectedItem(currSupplier.getTraderGroup());
        txtRemark.setText(currSupplier.getRemark());
        lblStatus.setText("EDIT");
        txtId.setEnabled(false);
        if (currSupplier.getCreditDays() != null) {
            txtCreditDays.setText(currSupplier.getCreditDays().toString());
        } else {
            txtCreditDays.setText(null);
        }
    }

    private void clear() {
        chkActive.setSelected(true);
        tblSupplier.clearSelection();
        currSupplier = new Supplier();
        txtAccountId.setText(null);
        txtAddress.setText(null);
        txtEMail.setText(null);
        txtCreditDays.setText(null);
        txtId.setText(null);
        txtName.setText(null);
        txtParent.setText(null);
        txtPhone.setText(null);
        txtRemark.setText(null);
        txtExpenseP.setText(null);
        chkActive.setSelected(false);
        lblStatus.setText("NEW");
        txtId.setEnabled(true);
        cboGroup.setSelectedItem(null);
        selectedRow = -1;
        assignCusId();
        dao.close();
        setFocus();
    }

    private void initCombo() {
        BindingUtil.BindCombo(cboPayMethod, dao.findAll("PayMethod"));
        BindingUtil.BindCombo(cboGroup, 
                dao.findAllHSQL("select o from CustomerGroup o where o.useFor = 'SUP' order by o.groupName"));

        new ComBoBoxAutoComplete(cboGroup, this);
    }

    private void initTable() {

        tableModel.setListTrader(dao.findAllHSQL("select o from Supplier o"));
        
        //Adjust table column width
        TableColumn column;
        column = tblSupplier.getColumnModel().getColumn(0);
        column.setPreferredWidth(60);

        column = tblSupplier.getColumnModel().getColumn(1);
        column.setPreferredWidth(300);

        column = tblSupplier.getColumnModel().getColumn(2);
        column.setPreferredWidth(20);

        //Define table selection model to single row selection.
        tblSupplier.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblSupplier.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblSupplier.getSelectedRow() >= 0) {
                    selectedRow = tblSupplier.convertRowIndexToModel(tblSupplier.getSelectedRow());
                }
                if (selectedRow >= 0) {
                    setCurrSupplier((Supplier) tableModel.getTrader(selectedRow));
                }
            }
        });
    }

    public void setFocus() {
        txtName.requestFocusInWindow();
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector<Component>();

        focusOrder.add(txtName);
        focusOrder.add(txtAddress);
        focusOrder.add(txtPhone);
        focusOrder.add(txtEMail);
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

        if (txtName.getText().isEmpty()) {
            status = false;
            JOptionPane.showMessageDialog(this, "Name cannot be blank or null.",
                    "Name null error.", JOptionPane.ERROR_MESSAGE);
            txtName.requestFocusInWindow();
        } else if (!NumberUtil.isNumber(txtCreditDays.getText().trim())) {
            status = false;
            JOptionPane.showMessageDialog(this, "Credit day should be number.",
                    "Not number.", JOptionPane.ERROR_MESSAGE);
        } else if (NumberUtil.NZeroInt(txtCreditDays.getText().trim()) < 0) {
            status = false;
            JOptionPane.showMessageDialog(this, "Credit days cannot be minus value.",
                    "Minus error.", JOptionPane.ERROR_MESSAGE);
        } else {

            if (!isDuplicate(txtId.getText().trim())) {
                currSupplier.setTraderId(txtId.getText());
                currSupplier.setTraderName(txtName.getText().trim());
                currSupplier.setAddress(txtAddress.getText().trim());
                currSupplier.setPhone(txtPhone.getText().trim());
                currSupplier.setEmail(txtEMail.getText().trim());
                currSupplier.setParent(txtParent.getText().trim());
                currSupplier.setAccountCode(txtAccountId.getText().trim());
                currSupplier.setActive(chkActive.isSelected());
                currSupplier.setPayMethod((PayMethod) cboPayMethod.getSelectedItem());
                currSupplier.setRemark(txtRemark.getText());
                currSupplier.setExpensePercent(NumberUtil.NZeroFloat(txtExpenseP.getText()));
                
                if (lblStatus.getText().equals("NEW")) {
                    currSupplier.setCreatedBy(Global.loginUser.getUserId());
                    currSupplier.setCreatedDate(DateUtil.toDateTime(DateUtil.getTodayDateStr()));
                } else {
                    currSupplier.setUpdatedBy(Global.loginUser.getUserId());
                }
                currSupplier.setUpdatedDate(DateUtil.toDateTime(DateUtil.getTodayDateStr()));
                if (!txtCreditDays.getText().isEmpty()) {
                    currSupplier.setCreditDays(NumberUtil.NZeroInt(txtCreditDays.getText().trim()));
                } else {
                    currSupplier.setCreditDays(null);
                }
                if (cboGroup.getSelectedItem() != null) {
                    currSupplier.setTraderGroup((CustomerGroup) cboGroup.getSelectedItem());
                } else {
                    currSupplier.setTraderGroup(null);
                }
            }
        }

        return status;
    }

    private boolean isDuplicate(String traderId) {
        boolean status = false;

        if (lblStatus.getText().equals("NEW")) {
            List<Trader> listT = dao.findAllHSQL("select o from Trader o where o.traderId = '"
                    + traderId + "'");
            if (listT != null) {
                if (!listT.isEmpty()) {
                    status = true;
                    JOptionPane.showMessageDialog(this, "Duplicate supplier id. You cannot save.",
                            "Duplicate", JOptionPane.ERROR_MESSAGE);
                    log.error("isDuplicate : " + traderId);
                }
            }
        }

        return status;
    }
    
    private void assignCusId() {
        String strFilter = "discriminator = 'S'";
        Object maxSupId;
        int maxLength = NumberUtil.NZeroInt(Util1.getPropValue("system.supplier.code.length"));

        try {
            maxSupId = dao.getMax("CONVERT(replace(trader_id,'SUP', ''),UNSIGNED INTEGER)", "trader", strFilter);

            if (maxSupId == null) {
                maxSupId = "SUP001";
            } else {
                String strSupIdSerial;
                Integer tmpSerial = Integer.parseInt(maxSupId.toString().replaceAll("SUP", "")) + 1;

                strSupIdSerial = tmpSerial.toString();
                int i = strSupIdSerial.length();

                for (; i < maxLength; i++) {
                    strSupIdSerial = "0" + strSupIdSerial;
                }

                strSupIdSerial = "SUP" + strSupIdSerial;

                /*if (strSupIdSerial.length() == 1) {
                 strSupIdSerial = "SUP00" + strSupIdSerial;
                 } else if (strSupIdSerial.length() == 2) {
                 strSupIdSerial = "SUP0" + strSupIdSerial;
                 }*/
                maxSupId = strSupIdSerial;
            }

            txtId.setText(maxSupId.toString());
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
                            currSupplier.getTraderId(),
                            DateUtil.toDateTimeStrMYSQL(d));
                }
                //For BK Pagolay
                currSupplier.setIntgUpdStatus(null);
                dao.save(currSupplier);

                //For integration with account
                if (Global.mqConnection != null) {
                    ActiveMQConnection mq = Global.mqConnection;
                    if (mq.isStatus()) {
                        uploadSupplier(currSupplier);
                    }
                }
                //=====================================================

                /*if (lblStatus.getText().equals("NEW")) {
                    tableModel.addTrader(currSupplier);
                } else {
                    tableModel.setTrader(selectedRow, currSupplier);
                }*/
                tableModel.setListTrader(dao.findAllHSQL("select o from Supplier o"));

                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Supplier Name", JOptionPane.ERROR_MESSAGE);
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
                        "Spllier Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    currSupplier.setIntgUpdStatus(null);
                    dao.delete(currSupplier);

                    //For integration with account
                    if (Global.mqConnection != null) {
                        ActiveMQConnection mq = Global.mqConnection;
                        if (mq.isStatus()) {
                            currSupplier.setActive(false);
                            uploadSupplier(currSupplier);
                        }
                    }
                    //=====================================================

                    int tmpRow = selectedRow;
                    selectedRow = -1;
                    tableModel.deleteTrader(tmpRow);
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
        formActionKeyMapping(butFilter);
        formActionKeyMapping(txtName);
        formActionKeyMapping(txtAddress);
        formActionKeyMapping(txtPhone);
        formActionKeyMapping(txtEMail);
        formActionKeyMapping(txtParent);
        formActionKeyMapping(txtAccountId);
        formActionKeyMapping(chkActive);
        formActionKeyMapping(tblSupplier);
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

    private void uploadSupplier(Supplier sup) {
        try {
            ActiveMQConnection mq = Global.mqConnection;
            MapMessage msg = mq.getMapMessageTemplate();

            msg.setString("program", Global.programId);
            msg.setString("entity", "TRADER");
            if (sup.getAccountId() != null) {
                msg.setString("accountId", sup.getAccountId());
            }
            msg.setString("queueName", "INVENTORY");
            msg.setString("name", sup.getTraderName());
            msg.setBoolean("active", sup.getActive());
            msg.setString("address", sup.getAddress());
            msg.setString("CODE", sup.getTraderId());
            if (sup.getTraderGroup() != null) {
                msg.setString("coa", sup.getTraderGroup().getAccountId());
            } else {
                msg.setString("coa", "-");
            }
            mq.sendMessage(Global.queueName, msg);
        } catch (Exception ex) {
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
        tblSupplier = new javax.swing.JTable();
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
        jLabel9 = new javax.swing.JLabel();
        txtParent = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        txtFilter = new javax.swing.JTextField();
        butFilter = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        txtAccountId = new javax.swing.JTextField();
        lblStatus = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        cboPayMethod = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        butUploadAll = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        cboGroup = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        txtCreditDays = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtExpenseP = new javax.swing.JTextField();

        tblSupplier.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblSupplier.setModel(tableModel);
        tblSupplier.setRowHeight(23);
        tblSupplier.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblSupplier);

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

        jLabel9.setFont(Global.lableFont);
        jLabel9.setText("Parent");

        txtParent.setFont(Global.textFont);

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Active");

        txtFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        butFilter.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butFilter.setText("...");

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Acc ID");

        txtAccountId.setFont(Global.textFont);

        lblStatus.setText("NEW");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Pay Method");

        cboPayMethod.setFont(Global.textFont);

        jLabel8.setFont(Global.lableFont);
        jLabel8.setText("Remark");

        txtRemark.setFont(Global.textFont);

        butUploadAll.setText("Upload All");
        butUploadAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butUploadAllActionPerformed(evt);
            }
        });

        jLabel11.setFont(Global.lableFont);
        jLabel11.setText("Group");

        cboGroup.setFont(Global.textFont);

        jLabel12.setFont(Global.lableFont);
        jLabel12.setText("Credit Days");

        txtCreditDays.setFont(Global.textFont);
        txtCreditDays.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel13.setFont(Global.lableFont);
        jLabel13.setText("Expense %");

        txtExpenseP.setFont(Global.textFont);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butUploadAll))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chkActive)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCreditDays))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtId))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboPayMethod, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRemark, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                                .addGap(5, 5, 5))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtAccountId))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtEMail)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                                    .addComponent(txtName)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtParent))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cboGroup, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPhone)))
                        .addGap(6, 6, 6))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtExpenseP, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                        .addGap(11, 11, 11)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel10, jLabel11, jLabel13, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabel9});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butFilter))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(cboGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtEMail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(txtCreditDays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtParent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtAccountId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(cboPayMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txtExpenseP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(chkActive, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblStatus)
                                .addGap(2, 2, 2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butUploadAll)
                        .addGap(0, 78, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

  private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
      if (txtFilter.getText().isEmpty()) {
          sorter.setRowFilter(null);
      } else {
          if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
              sorter.setRowFilter(swrf);
          } else {
              sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
          }
      }
  }//GEN-LAST:event_txtFilterKeyReleased

    private void butUploadAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUploadAllActionPerformed
        List<Trader> listCus = tableModel.getListTrader();
        if (listCus != null) {
            if (Global.mqConnection != null) {
                ActiveMQConnection mq = Global.mqConnection;
                if (mq.isStatus()) {
                    for (Trader tr : listCus) {
                        uploadSupplier((Supplier) tr);
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
                    "Supplier", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_butUploadAllActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butFilter;
    private javax.swing.JButton butUploadAll;
    private javax.swing.JComboBox cboGroup;
    private javax.swing.JComboBox cboPayMethod;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JTable tblSupplier;
    private javax.swing.JTextField txtAccountId;
    private javax.swing.JTextArea txtAddress;
    private javax.swing.JTextField txtCreditDays;
    private javax.swing.JTextField txtEMail;
    private javax.swing.JTextField txtExpenseP;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtParent;
    private javax.swing.JTextField txtPhone;
    private javax.swing.JTextField txtRemark;
    // End of variables declaration//GEN-END:variables
}
