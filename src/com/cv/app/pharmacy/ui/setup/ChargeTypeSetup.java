/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * ChargeTypeSetup.java
 *
 * Created on May 29, 2012, 8:25:36 PM
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ChargeType;
import com.cv.app.pharmacy.ui.common.ChargeTypeTableModel;
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
public class ChargeTypeSetup extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(ChargeTypeSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private ChargeTypeTableModel tableModel = new ChargeTypeTableModel();
    private BestAppFocusTraversalPolicy focusPolicy;
    private ChargeType currChargeType = new ChargeType();
    private int selectedRow = -1;

    /**
     * Creates new form ChargeTypeSetup
     */
    public ChargeTypeSetup() {
        initComponents();
        try {
            dao.open();
            initTable();
            dao.close();
        } catch (Exception ex) {
            log.error("ChargeTypeSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
    }

    public void setCurrChargeType(ChargeType currChargeType) {
        this.currChargeType = currChargeType;
        txtChargeType.setText(currChargeType.getChargeTypeDesc());
        if (currChargeType.getFactor() == null) {
            txtFactor.setText("");
        } else {
            txtFactor.setText(currChargeType.getFactor().toString());
        }
        if (currChargeType.getIsAmount() == null) {
            chkIsAmount.setSelected(false);
        } else {
            chkIsAmount.setSelected(currChargeType.getIsAmount());
        }
        lblStatus.setText("EDIT");
    }

    private void clear() {
        lblStatus.setText("NEW");
        selectedRow = -1;
        tblChargeType.clearSelection();
        currChargeType = new ChargeType();
        txtChargeType.setText(null);
        txtFactor.setText("");
        chkIsAmount.setSelected(false);

        setFocus();
    }

    private void initTable() {
        try {
            //Get ChargeType from database.
            tableModel.setListChargeType(dao.findAll("ChargeType"));

            //Define table selection model to single row selection.
            tblChargeType.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            //Adding table row selection listener.
            tblChargeType.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (tblChargeType.getSelectedRow() >= 0) {
                        selectedRow = tblChargeType.convertRowIndexToModel(
                                tblChargeType.getSelectedRow());
                    }

                    if (selectedRow >= 0) {
                        setCurrChargeType(tableModel.getChargeType(selectedRow));
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
        txtChargeType.requestFocusInWindow();
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

        if (Util1.nullToBlankStr(txtChargeType.getText()).equals("")) {
            status = false;
        } else {
            currChargeType.setChargeTypeDesc(txtChargeType.getText().trim());
            if (!txtFactor.getText().trim().isEmpty()) {
                float tmpFactor = Float.parseFloat(txtFactor.getText().trim());
                currChargeType.setFactor(tmpFactor);
            } else {
                currChargeType.setFactor(0f);
            }
            currChargeType.setIsAmount(chkIsAmount.isSelected());
        }

        return status;
    }

    private void delete() {
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Charge Type Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(currChargeType);
                    int tmpRow = selectedRow;
                    selectedRow = -1;
                    tableModel.deleteChargeType(tmpRow);
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete this charge type.",
                        "Charge Type Delete", JOptionPane.ERROR_MESSAGE);
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
        tblChargeType = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtChargeType = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        butDelete = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtFactor = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        chkIsAmount = new javax.swing.JCheckBox();

        tblChargeType.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblChargeType.setModel(tableModel);
        tblChargeType.setRowHeight(23);
        tblChargeType.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblChargeType);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Charge Type");

        txtChargeType.setFont(Global.textFont);

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

        butDelete.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butDelete.setText("Delete");
        butDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteActionPerformed(evt);
            }
        });

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Factor");

        txtFactor.setFont(Global.textFont);

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Is Amount");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtChargeType))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtFactor))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblStatus)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(chkIsAmount)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butDelete, butSave});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2, jLabel3});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtChargeType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFactor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(chkIsAmount))
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave)
                            .addComponent(butDelete))
                        .addGap(13, 13, 13)
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
                dao.save(currChargeType);
                if (lblStatus.getText().equals("NEW")) {
                    tableModel.addChargeType(currChargeType);
                } else {
                    tableModel.setChargeType(selectedRow, currChargeType);
                }
                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Charge Type Save", JOptionPane.ERROR_MESSAGE);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JCheckBox chkIsAmount;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblChargeType;
    private javax.swing.JTextField txtChargeType;
    private javax.swing.JTextField txtFactor;
    // End of variables declaration//GEN-END:variables
}
