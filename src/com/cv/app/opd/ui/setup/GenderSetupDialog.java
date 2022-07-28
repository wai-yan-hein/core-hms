/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.Gender;
import com.cv.app.opd.ui.common.GenderTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
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
 * @author wswe
 */
public class GenderSetupDialog extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(GenderSetupDialog.class.getName());
    private AbstractDataAccess dao;
    private GenderTableModel tableModel = new GenderTableModel();
    private int selectRow = -1;
    private Gender currGender = new Gender();
    private BestAppFocusTraversalPolicy focusPolicy;

    /**
     * Creates new form InitialSetupDialog
     */
    public GenderSetupDialog(AbstractDataAccess dao) {
        super(Util1.getParent(), true);
        initComponents();
        this.dao = dao;
        initTable();
        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
        txtGenderId.requestFocusInWindow();
    }

    private void initTable() {
        try {
            tableModel.setListGender(dao.findAll("Gender"));
            tblGenderl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblGenderl.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (tblGenderl.getSelectedRow() >= 0) {
                        selectRow = tblGenderl.convertRowIndexToModel(tblGenderl.getSelectedRow());
                    }

                    if (selectRow >= 0) {
                        setRecord(tableModel.getGender(selectRow));
                    }
                }
            });
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void setRecord(Gender gender) {
        currGender = gender;
        txtDesp.setText(gender.getDescription());
        txtGenderId.setText(gender.getGenderId());
        txtGenderId.setEditable(false);
        lblStatus.setText("EDIT");
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        focusOrder.add(txtGenderId);
        focusOrder.add(txtDesp);
        focusOrder.add(butSave);
        focusOrder.add(butClear);

        focusPolicy = new BestAppFocusTraversalPolicy(focusOrder);
    }

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
    }

    private void clear() {
        selectRow = -1;
        currGender = new Gender();
        txtDesp.setText(null);
        txtGenderId.setText(null);
        lblStatus.setText("NEW");
        txtGenderId.setEditable(true);
        System.gc();
        txtGenderId.requestFocusInWindow();
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (Util1.nullToBlankStr(txtGenderId.getText()).equals("")) {
            JOptionPane.showMessageDialog(this, "ID must not be blank.",
                    "Blank", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtDesp.requestFocusInWindow();
        } else if (Util1.nullToBlankStr(txtDesp.getText()).equals("")) {
            JOptionPane.showMessageDialog(this, "Description must not be blank.",
                    "Blank", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtDesp.requestFocusInWindow();
        } else if (txtGenderId.getText().length() > 2) {
            JOptionPane.showMessageDialog(this, "Character length cannot more then 2.",
                    "Gender ID length", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtGenderId.requestFocusInWindow();
        } else {
            currGender.setGenderId(txtGenderId.getText());
            currGender.setDescription(txtDesp.getText());
        }

        return status;
    }

    private void save() {
        if (isValidEntry()) {
            try {
                dao.save(currGender);
                if (lblStatus.getText().equals("NEW")) {
                    tableModel.addGender(currGender);
                } else {
                    tableModel.setGender(selectRow, currGender);
                }
                clear();
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Duplicate entry.",
                        "Gender", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                dao.rollBack();
            } finally {
                dao.close();
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
        tblGenderl = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtGenderId = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Gender Setup");

        tblGenderl.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblGenderl.setModel(tableModel);
        tblGenderl.setRowHeight(23);
        jScrollPane1.setViewportView(tblGenderl);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Desp");

        txtDesp.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

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

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("ID");

        txtGenderId.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtDesp, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addComponent(lblStatus)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(txtGenderId)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butSave});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtGenderId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave))
                        .addGap(20, 20, 20)
                        .addComponent(lblStatus)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblGenderl;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JTextField txtGenderId;
    // End of variables declaration//GEN-END:variables
}
