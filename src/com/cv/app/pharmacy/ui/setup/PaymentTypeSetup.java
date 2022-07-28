/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * PaymentTypeSetup.java
 *
 * Created on May 29, 2012, 9:17:12 PM
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.ui.common.PaymentTypeTableModel;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author winswe
 */
public class PaymentTypeSetup extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(PaymentTypeSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private PaymentTypeTableModel tableModel = new PaymentTypeTableModel();
    private BestAppFocusTraversalPolicy focusPolicy;
    private PaymentType currPaymenttype = new PaymentType();
    private int selectedRow = -1;

    /**
     * Creates new form PaymentTypeSetup
     */
    public PaymentTypeSetup() {
        initComponents();
        try {
            dao.open();
            initTable();
            dao.close();
        } catch (Exception ex) {
            log.error("PaymentTypeSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
    }

    public void setCurrPaymentType(PaymentType currPaymenttype) {
        this.currPaymenttype = currPaymenttype;
        txtPaymentType.setText(currPaymenttype.getPaymentTypeName());
        lblStatus.setText("EDIT");
    }

    private void clear() {
        tblPaymentType.clearSelection();
        selectedRow = -1;
        lblStatus.setText("NEW");
        currPaymenttype = new PaymentType();
        txtPaymentType.setText(null);
        setFocus();
    }

    private void initTable() {
        try {
            //Get PaymentType from database.
            tableModel.setListPaymentType(dao.findAll("PaymentType"));

            //Define table selection model to single row selection.
            tblPaymentType.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            //Adding table row selection listener.
            tblPaymentType.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (tblPaymentType.getSelectedRow() >= 0) {
                        selectedRow = tblPaymentType.convertRowIndexToModel(
                                tblPaymentType.getSelectedRow());
                    }
                    if (selectedRow >= 0) {
                        setCurrPaymentType(tableModel.getPaymentType(selectedRow));
                    }
                }
            });
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public void setFocus() {
        txtPaymentType.requestFocusInWindow();
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector<Component>(7);

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
        boolean status = true;

        if (Util1.nullToBlankStr(txtPaymentType.getText()).equals("")) {
            status = false;
        } else {
            currPaymenttype.setPaymentTypeName(txtPaymentType.getText().trim());
        }

        return status;
    }

    private void delete() {
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Payment Type Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(currPaymenttype);
                    int tmpRow = selectedRow;
                    selectedRow = -1;
                    tableModel.deletePaymentType(tmpRow);
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete this category.",
                        "Payment Type Delete", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }

        clear();
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
        tblPaymentType = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtPaymentType = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        tblPaymentType.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblPaymentType.setModel(tableModel);
        tblPaymentType.setRowHeight(20);
        tblPaymentType.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblPaymentType);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Payment Type");

        txtPaymentType.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        butSave.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        lblStatus.setText("NEW");

        jButton1.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        jButton1.setText("Delete");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtPaymentType, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butSave, jButton1});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtPaymentType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblStatus)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        try {
            if (isValidEntry()) {
                dao.save(currPaymenttype);
                if (lblStatus.getText().equals("NEW")) {
                    tableModel.addPaymentType(currPaymenttype);
                } else {
                    tableModel.setPaymentType(selectedRow, currPaymenttype);
                }
                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Payment Type", JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } catch (Exception ex) {
            log.error("butSaveActionPerformed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butSaveActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        delete();
    }//GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butSave;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblPaymentType;
    private javax.swing.JTextField txtPaymentType;
    // End of variables declaration//GEN-END:variables
}
