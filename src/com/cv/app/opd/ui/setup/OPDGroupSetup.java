/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.opd.database.entity.OPDGroup;
import com.cv.app.opd.ui.common.OPDGroupTableModel;
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
 * @author winswe
 */
public class OPDGroupSetup extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(OPDGroupSetup.class.getName());
    private OPDGroupTableModel tableModel = new OPDGroupTableModel();
    private final AbstractDataAccess dao = Global.dao; // Data access object.
    private int selectRow = -1;
    private BestAppFocusTraversalPolicy focusPolicy;
    private OPDGroup currOPDGroup = new OPDGroup();

    /**
     * Creates new form OPDGroupSetup
     */
    public OPDGroupSetup() {
        super(Util1.getParent(), true);
        initComponents();
        /*applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);*/
        initTable();
        lblStatus.setText("NEW");
        setVisible(true);
    }

    private void initTable() {
        try {
            tableModel.setListOPDGroup(dao.findAll("OPDGroup"));
            tblGroup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblGroup.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (tblGroup.getSelectedRow() >= 0) {
                        selectRow = tblGroup.convertRowIndexToModel(tblGroup.getSelectedRow());
                    }

                    if (selectRow >= 0) {
                        setRecord(tableModel.getOPDGroup(selectRow));
                    }
                }
            });
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void setRecord(OPDGroup oPDGroup) {
        currOPDGroup = oPDGroup;
        txtGroupName.setText(oPDGroup.getGroupName());
        lblStatus.setText("EDIT");
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        //focusOrder.add(txtCusId);
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
        currOPDGroup = new OPDGroup();
        txtGroupName.setText(null);
        lblStatus.setText("NEW");
        System.gc();
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (Util1.nullToBlankStr(txtGroupName.getText()).equals("")) {
            JOptionPane.showMessageDialog(this, "Group name must not be blank.",
                    "Blank", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtGroupName.requestFocusInWindow();
        } else {
            currOPDGroup.setGroupName(txtGroupName.getText());
        }

        return status;
    }

    private void save() {
        if (isValidEntry()) {
            try {
                dao.save(currOPDGroup);
                if (lblStatus.getText().equals("NEW")) {
                    tableModel.addOPDGroup(currOPDGroup);
                } else {
                    tableModel.setOPDGroup(selectRow, currOPDGroup);
                }
                clear();
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Duplicate entry.",
                        "Group Name", JOptionPane.ERROR_MESSAGE);
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
        tblGroup = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtGroupName = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("OPD Group Setup");

        tblGroup.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblGroup.setModel(tableModel);
        tblGroup.setRowHeight(23);
        jScrollPane1.setViewportView(tblGroup);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Group Name ");

        txtGroupName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtGroupName))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 53, Short.MAX_VALUE)
                        .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                            .addComponent(txtGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave))
                        .addGap(24, 24, 24)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
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
    private javax.swing.JTable tblGroup;
    private javax.swing.JTextField txtGroupName;
    // End of variables declaration//GEN-END:variables
}
