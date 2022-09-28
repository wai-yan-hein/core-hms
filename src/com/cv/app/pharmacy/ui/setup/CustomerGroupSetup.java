/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.ui.common.CustomerGroupTableModel;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.jms.MapMessage;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author WSwe
 */
public class CustomerGroupSetup extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(CustomerGroupSetup.class.getName());
    private CustomerGroup currCustomerGroup = new CustomerGroup();
    private final AbstractDataAccess dao = Global.dao; // Data access object.
    private BestAppFocusTraversalPolicy focusPolicy;
    private CustomerGroupTableModel tableModel = new CustomerGroupTableModel();
    private int selectedRow = -1;
    private TableRowSorter<TableModel> sorter;

    /**
     * Creates new form CustomerGroupSetup
     */
    public CustomerGroupSetup() {
        initComponents();
        try {
            dao.open();
            initTable();
            sorter = new TableRowSorter(tblCustomerGroup.getModel());
            tblCustomerGroup.setRowSorter(sorter);
            dao.close();
        } catch (Exception ex) {
            log.error("CustomerGroupSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
    }

    public void setCurrCustomerGroup(CustomerGroup currCustomerGroup) {
        this.currCustomerGroup = currCustomerGroup;
        txtGroupId.setText(currCustomerGroup.getGroupId());
        txtGroupName.setText(currCustomerGroup.getGroupName());
        lblStatus.setText("EDIT");
        txtGroupId.setEnabled(false);
        txtAccountId.setText(currCustomerGroup.getAccountId());
        txtReportName.setText(currCustomerGroup.getReportName());
        txtDept.setText(currCustomerGroup.getDeptId());
        cboUseFor.setSelectedItem(currCustomerGroup.getUseFor());
        if (currCustomerGroup.getOverDueVouCnt() == null) {
            txtOverDueVouCnt.setText(null);
        } else {
            txtOverDueVouCnt.setText(currCustomerGroup.getOverDueVouCnt().toString());
        }
    }

    private void clear() {
        tblCustomerGroup.clearSelection();
        selectedRow = -1;
        lblStatus.setText("NEW");
        currCustomerGroup = new CustomerGroup();
        txtGroupId.setText(null);
        txtGroupName.setText(null);
        txtGroupId.setEnabled(true);
        txtAccountId.setText(null);
        txtReportName.setText(null);
        txtDept.setText(null);
        txtOverDueVouCnt.setText(null);
        cboUseFor.setSelectedItem(null);
        setFocus();
    }

    /*
     * Initialize tblCustomerGroup
     */
    private void initTable() {
        tblCustomerGroup.getColumnModel().getColumn(0).setPreferredWidth(5);//Code
        tblCustomerGroup.getColumnModel().getColumn(1).setPreferredWidth(200);
        //Get Category from database.
        try {
            tableModel.setListCustomerGroup(dao.findAllHSQL(
                    "select o from CustomerGroup o order by o.groupName"));
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
        //Define table selection model to single row selection.
        tblCustomerGroup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblCustomerGroup.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblCustomerGroup.getSelectedRow() >= 0) {
                    selectedRow = tblCustomerGroup.convertRowIndexToModel(
                            tblCustomerGroup.getSelectedRow());
                }
                if (selectedRow >= 0) {
                    setCurrCustomerGroup(tableModel.getCustomerGroup(selectedRow));
                }
            }
        });
    }

    public void setFocus() {
        //txtGroupName.requestFocusInWindow();
        txtGroupId.requestFocusInWindow();
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector(7);

        focusOrder.add(txtGroupId);
        focusOrder.add(txtGroupName);
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

        if (Util1.nullToBlankStr(txtGroupId.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(this, "Group Id should not be blank.",
                    "Null error.", JOptionPane.ERROR_MESSAGE);
        } else if (Util1.nullToBlankStr(txtGroupName.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(this, "Group name should not be blank.",
                    "Null error.", JOptionPane.ERROR_MESSAGE);
        } else if (txtGroupId.getText().length() > 5) {
            status = false;
            JOptionPane.showMessageDialog(this, "Group Id at most 5 characters.",
                    "Character Length", JOptionPane.ERROR_MESSAGE);
        } else {
            currCustomerGroup.setGroupName(txtGroupName.getText().trim());
            currCustomerGroup.setGroupId(txtGroupId.getText().trim());
            currCustomerGroup.setAccountId(txtAccountId.getText().trim());
            currCustomerGroup.setUpdatedDate(new Date());
            currCustomerGroup.setReportName(txtReportName.getText().trim());
            currCustomerGroup.setDeptId(txtDept.getText().trim());
            if (cboUseFor.getSelectedItem() == null) {
                currCustomerGroup.setUseFor(null);
            } else {
                currCustomerGroup.setUseFor(cboUseFor.getSelectedItem().toString());
            }
            if (txtOverDueVouCnt.getText() != null) {
                if (!txtOverDueVouCnt.getText().trim().isEmpty()) {
                    currCustomerGroup.setOverDueVouCnt(Integer.parseInt(txtOverDueVouCnt.getText().trim()));
                } else {
                    currCustomerGroup.setOverDueVouCnt(null);
                }
            } else {
                currCustomerGroup.setOverDueVouCnt(null);
            }
        }

        return status;
    }

    private void delete() {
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Customer Group Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(currCustomerGroup);

                    //Upload to account
                    //(currCustomerGroup, "DELETE");
                    int tmpRow = selectedRow;
                    selectedRow = -1;
                    tableModel.deleteChargeType(tmpRow);
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete this customer group.",
                        "Customer Group Delete", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }

        clear();
    }

    private void uploadToAccount(CustomerGroup cg, String action) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            if (Global.mqConnection != null) {
                if (Global.mqConnection.isStatus()) {
                    try {
                        ActiveMQConnection mq = Global.mqConnection;
                        MapMessage msg = mq.getMapMessageTemplate();
                        msg.setString("program", Global.programId);
                        msg.setString("entity", "CUSGROUP");
                        msg.setString("groupId", cg.getGroupId());
                        msg.setString("groupName", cg.getGroupName());
                        msg.setString("accountId", cg.getAccountId());
                        msg.setString("queueName", "INVENTORY");
                        msg.setString("action", action);

                        mq.sendMessage(Global.queueName, msg);
                    } catch (Exception ex) {
                        log.error("uploadToAccount : " + ex.getStackTrace()[0].getLineNumber() + " - " + cg.getGroupId() + " - " + ex);
                    }
                }
            }
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
        tblCustomerGroup = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtGroupName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtAccountId = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butUploadAll = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        txtOverDueVouCnt = new javax.swing.JTextField();
        txtGroupId = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtDept = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        cboUseFor = new javax.swing.JComboBox<>();
        txtReportName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        butDelete = new javax.swing.JButton();

        tblCustomerGroup.setFont(Global.textFont);
        tblCustomerGroup.setModel(tableModel);
        tblCustomerGroup.setRowHeight(23);
        jScrollPane1.setViewportView(tblCustomerGroup);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Group Name");

        txtGroupName.setFont(Global.textFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("OVD Vou C");

        txtAccountId.setFont(Global.textFont);
        txtAccountId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAccountIdActionPerformed(evt);
            }
        });

        butClear.setFont(Global.lableFont);
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        butUploadAll.setFont(Global.lableFont);
        butUploadAll.setText("Upload All");
        butUploadAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butUploadAllActionPerformed(evt);
            }
        });

        butSave.setFont(Global.lableFont);
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        txtGroupId.setFont(Global.textFont);

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Department");

        jLabel7.setFont(Global.lableFont);
        jLabel7.setText("Use For");

        txtDept.setFont(Global.textFont);

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Group Id");

        jLabel4.setFont(Global.lableFont);
        jLabel4.setText("Report Name ");

        lblStatus.setText("NEW");

        cboUseFor.setFont(Global.textFont);
        cboUseFor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CUS", "SUP" }));

        txtReportName.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Account Id");

        butDelete.setFont(Global.lableFont);
        butDelete.setText("Delete");
        butDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtGroupName)
                                    .addComponent(txtGroupId)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtAccountId))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtOverDueVouCnt)
                            .addComponent(txtDept)
                            .addComponent(txtReportName)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butUploadAll))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboUseFor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butDelete, butSave});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtGroupId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtAccountId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtReportName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtOverDueVouCnt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cboUseFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butClear)
                    .addComponent(butSave)
                    .addComponent(butDelete)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butUploadAll)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        try {
            if (isValidEntry()) {
                currCustomerGroup.setIntgUpdStatus(null);
                dao.save(currCustomerGroup);

                //Upload to account
                //(currCustomerGroup, "SAVE");
                if (lblStatus.getText().equals("NEW")) {
                    tableModel.addCustomerGroup(currCustomerGroup);
                } else {
                    tableModel.setCustomerGroup(selectedRow, currCustomerGroup);
                }
                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Customer Group Save", JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } catch (Exception ex) {
            log.error("butSaveActionPerformed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butSaveActionPerformed

    private void butDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleteActionPerformed
        delete();
    }//GEN-LAST:event_butDeleteActionPerformed

    private void txtAccountIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAccountIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAccountIdActionPerformed

    private void butUploadAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butUploadAllActionPerformed
        List<CustomerGroup> listCG = tableModel.getListCustomerGroup();
        for (CustomerGroup cg : listCG) {
            uploadToAccount(cg, "New");
        }
    }//GEN-LAST:event_butUploadAllActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JButton butUploadAll;
    private javax.swing.JComboBox<String> cboUseFor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblCustomerGroup;
    private javax.swing.JTextField txtAccountId;
    private javax.swing.JTextField txtDept;
    private javax.swing.JTextField txtGroupId;
    private javax.swing.JTextField txtGroupName;
    private javax.swing.JTextField txtOverDueVouCnt;
    private javax.swing.JTextField txtReportName;
    // End of variables declaration//GEN-END:variables
}
