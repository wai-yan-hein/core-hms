/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.AdjType;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author WSwe
 */
public class AdjTypeSetup extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(AdjTypeSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao; // Data access object.    
    private List<AdjType> listAdjType = ObservableCollections.
            observableList(new ArrayList<AdjType>());
    private BestAppFocusTraversalPolicy focusPolicy;
    private AdjType currAdjType = new AdjType();

    /**
     * Creates new form AdjTypeSetup
     */
    public AdjTypeSetup() {
        initComponents();

        try {
            dao.open();
            initTable();
            dao.close();
        } catch (Exception ex) {
            log.error("AdjTypeSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
    }

    public void setCurrAdjType(AdjType currAdjType) {
        lblStatus.setText("EDIT");
        this.currAdjType = currAdjType;
        txtDesp.setText(currAdjType.getAdjDesp());
        txtId.setText(currAdjType.getAdjTypeId());
    }

    private void clear() {
        lblStatus.setText("NEW");
        txtDesp.setText(null);
        txtId.setText(null);
        currAdjType = new AdjType();
        setFocus();
    }

    private void initTable() {
        try {
            listAdjType = ObservableCollections.observableList(dao.findAll("AdjType"));

            //Binding table with listAdjType using beansbinding library.
            JTableBinding jTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ_WRITE,
                    listAdjType, tblAdjType);
            JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${adjDesp}"));
            columnBinding.setColumnName("Description");
            columnBinding.setColumnClass(String.class);
            columnBinding.setEditable(false);
            jTableBinding.bind();

            //Define table selection model to single row selection.
            tblAdjType.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            //Adding table row selection listener.
            tblAdjType.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    int row = tblAdjType.getSelectedRow();

                    if (row != -1) {
                        setCurrAdjType(listAdjType.get(tblAdjType.convertRowIndexToModel(row)));
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
        txtId.requestFocusInWindow();
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector(7);

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

        if (Util1.nullToBlankStr(txtId.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(this, "Type Id cannot be blank or null.",
                    "Id null error.", JOptionPane.ERROR_MESSAGE);
            txtId.requestFocusInWindow();
        } else if (Util1.nullToBlankStr(txtDesp.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(this, "Type Desp cannot be blank or null.",
                    "Desp null error.", JOptionPane.ERROR_MESSAGE);
            txtDesp.requestFocusInWindow();
        } else {
            currAdjType.setAdjTypeId(txtId.getText().trim());
            currAdjType.setAdjDesp(txtDesp.getText().trim());
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
        tblAdjType = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDesp = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        tblAdjType.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblAdjType.setModel(new javax.swing.table.DefaultTableModel(
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
        tblAdjType.setRowHeight(23);
        tblAdjType.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblAdjType);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Type Id");

        txtId.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Type Desp");

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDesp)
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 58, Short.MAX_VALUE)
                        .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                            .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtDesp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave))
                        .addGap(13, 13, 13)
                        .addComponent(lblStatus))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        try {
            if (isValidEntry()) {
                dao.save(currAdjType);
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblAdjType;
    private javax.swing.JTextField txtDesp;
    private javax.swing.JTextField txtId;
    // End of variables declaration//GEN-END:variables
}
