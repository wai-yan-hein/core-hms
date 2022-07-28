/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * SessionSetup.java
 *
 * Created on May 29, 2012, 10:04:54 PM
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Session;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.JTableBinding.ColumnBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author winswe
 */
public class SessionSetup extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(SessionSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private List<Session> listSession = ObservableCollections.
            observableList(new ArrayList<Session>());
    private BestAppFocusTraversalPolicy focusPolicy;
    private Session currSession = new Session();

    /**
     * Creates new form SessionSetup
     */
    public SessionSetup() {
        initComponents();
        try {
            dao.open();
            initTable();
            dao.close();
        } catch (Exception ex) {
            log.error("SessionSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
    }

    public void setCurrSession(Session currSession) {
        this.currSession = currSession;
        txtEndTime.setText(currSession.getEndTime());
        txtSessionName.setText(currSession.getSessionName());
        txtStartTime.setText(currSession.getStartTime());
        lblStatus.setText("EDIT");
    }

    private void clear() {
        lblStatus.setText("NEW");
        currSession = new Session();
        txtEndTime.setText(null);
        txtSessionName.setText(null);
        txtStartTime.setText(null);
        setFocus();
    }

    private void initTable() {
        try {
            //Get Session from database.
            listSession = ObservableCollections.observableList(dao.findAll("Session"));

            //Binding table with listCategory using beansbinding library.
            JTableBinding jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE,
                    listSession, tblSession);
            ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${sessionName}"));
            columnBinding.setColumnName("Session Name");
            columnBinding.setColumnClass(String.class);
            columnBinding.setEditable(false);
            jTableBinding.bind();

            //Define table selection model to single row selection.
            tblSession.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            //Adding table row selection listener.
            tblSession.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    int row = tblSession.getSelectedRow();

                    if (row != -1) {
                        setCurrSession(listSession.get(tblSession.convertRowIndexToModel(row)));
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
        txtSessionName.requestFocusInWindow();
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

        if (Util1.nullToBlankStr(txtSessionName.getText()).equals("")) {
            status = false;
        } else {
            currSession.setSessionName(txtSessionName.getText().trim());
            currSession.setEndTime(txtEndTime.getText());
            currSession.setStartTime(txtStartTime.getText());
        }

        return status;
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
        tblSession = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtSessionName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtStartTime = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtEndTime = new javax.swing.JFormattedTextField();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        tblSession.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblSession.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblSession.setRowHeight(23);
        tblSession.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblSession);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Session Name");

        txtSessionName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Start Time");

        txtStartTime.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("End Time");

        txtEndTime.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtStartTime, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                            .addComponent(txtSessionName, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                            .addComponent(txtEndTime, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                            .addComponent(txtSessionName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtStartTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtEndTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave))
                        .addGap(21, 21, 21)
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
                dao.save(currSession);
                clear();
                dao.open();
                initTable();
                dao.close();
            }
        } catch (Exception ex) {
            log.error("butSaveActionPerformed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }//GEN-LAST:event_butSaveActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblSession;
    private javax.swing.JFormattedTextField txtEndTime;
    private javax.swing.JTextField txtSessionName;
    private javax.swing.JFormattedTextField txtStartTime;
    // End of variables declaration//GEN-END:variables
}
