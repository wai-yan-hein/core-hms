/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.Initial;
import com.cv.app.opd.ui.common.InitialTableModel;
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
public class InitialSetupDialog extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(InitialSetupDialog.class.getName());
    private AbstractDataAccess dao;
    private InitialTableModel tableModel = new InitialTableModel();
    private int selectRow = -1;
    private Initial currInitial = new Initial();
    private BestAppFocusTraversalPolicy focusPolicy;

    /**
     * Creates new form InitialSetupDialog
     */
    public InitialSetupDialog(AbstractDataAccess dao) {
        super(Util1.getParent(), true);
        initComponents();
        this.dao = dao;
        initTable();
        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
        txtInitial.requestFocusInWindow();
    }

    private void initTable() {
        try {
            tableModel.setListInitial(dao.findAll("Initial"));
            tblInitial.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblInitial.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (tblInitial.getSelectedRow() >= 0) {
                        selectRow = tblInitial.convertRowIndexToModel(tblInitial.getSelectedRow());
                    }

                    if (selectRow >= 0) {
                        setRecord(tableModel.getInitial(selectRow));
                    }
                }
            });
        } catch (Exception ex) {
            log.error("initTable");
        } finally {
            dao.close();
        }
    }

    private void setRecord(Initial initial) {
        currInitial = initial;
        txtInitial.setText(initial.getInitialName());
        lblStatus.setText("EDIT");
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        focusOrder.add(txtInitial);
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
        currInitial = new Initial();
        txtInitial.setText(null);
        lblStatus.setText("NEW");
        System.gc();
        txtInitial.requestFocusInWindow();
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (Util1.nullToBlankStr(txtInitial.getText()).equals("")) {
            JOptionPane.showMessageDialog(this, "Initial must not be blank.",
                    "Blank", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtInitial.requestFocusInWindow();
        } else {
            currInitial.setInitialName(txtInitial.getText());
        }

        return status;
    }

    private void save() {
        if (isValidEntry()) {
            try {
                dao.save(currInitial);
                if (lblStatus.getText().equals("NEW")) {
                    tableModel.addInitial(currInitial);
                } else {
                    tableModel.setInitial(selectRow, currInitial);
                }
                clear();
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Duplicate entry.",
                        "Initial", JOptionPane.ERROR_MESSAGE);
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
        tblInitial = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtInitial = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Initial Setup");

        tblInitial.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblInitial.setModel(tableModel);
        tblInitial.setRowHeight(23);
        jScrollPane1.setViewportView(tblInitial);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Initial");

        txtInitial.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtInitial, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtInitial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave))
                        .addGap(20, 20, 20)
                        .addComponent(lblStatus)
                        .addGap(0, 78, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblInitial;
    private javax.swing.JTextField txtInitial;
    // End of variables declaration//GEN-END:variables
}
