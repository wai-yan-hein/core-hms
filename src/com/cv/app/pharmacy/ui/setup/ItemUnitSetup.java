/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * ItemUnitSetup.java
 *
 * Created on Apr 22, 2012, 9:52:24 PM
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.ui.common.ItemUnitTableModel;
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
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author winswe
 */
public class ItemUnitSetup extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(ItemUnitSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private ItemUnitTableModel tableModel = new ItemUnitTableModel();
    private ItemUnit currItemUnit = new ItemUnit();
    private BestAppFocusTraversalPolicy focusPolicy;
    private TableRowSorter<TableModel> sorter;
    private int selectedRow = -1;

    /**
     * Creates new form ItemUnitSetup
     */
    public ItemUnitSetup() {
        initComponents();
        try {
            dao.open();
            initTable();
            sorter = new TableRowSorter(tblUnit.getModel());
            tblUnit.setRowSorter(sorter);
            dao.close();
        } catch (Exception ex) {
            log.error("ItemUnitSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
    }

    public void setCurrItemUnit(ItemUnit currItemUnit) {
        this.currItemUnit = currItemUnit;
        txtUnitDesc.setText(currItemUnit.getItemUnitName());
        txtUnitShort.setText(currItemUnit.getItemUnitCode());
        lblStatus.setText("EDIT");
    }

    private void clear() {
        tblUnit.clearSelection();
        selectedRow = -1;
        lblStatus.setText("NEW");
        currItemUnit = new ItemUnit();
        txtUnitDesc.setText(null);
        txtUnitShort.setText(null);
        setFocus();
    }

    public void setFocus() {
        txtUnitShort.requestFocusInWindow();
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

        if (Util1.nullToBlankStr(txtUnitShort.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(this, "Unit Code cannot be blank or null.",
                    "Unit Code null error.", JOptionPane.ERROR_MESSAGE);
            txtUnitShort.requestFocusInWindow();
        } else if (Util1.nullToBlankStr(txtUnitDesc.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(this, "Unit Name cannot be blank or null.",
                    "Unit Name null error.", JOptionPane.ERROR_MESSAGE);
            txtUnitDesc.requestFocusInWindow();
        } else if (txtUnitShort.getText().length() > 10) {
            status = false;
            JOptionPane.showMessageDialog(this, "Character length at most 10.",
                    "Character Length", JOptionPane.ERROR_MESSAGE);
            txtUnitShort.requestFocusInWindow();
        } else {
            currItemUnit.setItemUnitCode(txtUnitShort.getText().trim());
            currItemUnit.setItemUnitName(txtUnitDesc.getText().trim());
        }

        return status;
    }

    private void delete() {
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Item Unit Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(currItemUnit);
                    int tmpRow = selectedRow;
                    selectedRow = -1;
                    tblUnit.setRowSorter(null);
                    tableModel.deleteItemUnit(tmpRow);
                    tblUnit.setRowSorter(sorter);
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete this item unit.",
                        "Item Unit Delete", JOptionPane.ERROR_MESSAGE);
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
        tblUnit = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtUnitShort = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtUnitDesc = new javax.swing.JTextField();
        butSave = new javax.swing.JButton();
        butClear = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        butDelete = new javax.swing.JButton();

        tblUnit.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblUnit.setModel(tableModel);
        tblUnit.setRowHeight(20);
        tblUnit.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblUnit);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Unit Short ");

        txtUnitShort.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("Unit Desc");

        txtUnitDesc.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        butSave.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtUnitShort)
                            .addComponent(txtUnitDesc))))
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
                            .addComponent(txtUnitShort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtUnitDesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave)
                            .addComponent(butDelete))
                        .addGap(11, 11, 11)
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
                dao.save(currItemUnit);
                if (lblStatus.getText().equals("NEW")) {
                    tableModel.addItemUnit(currItemUnit);
                } else {
                    tableModel.setItemUnit(selectedRow, currItemUnit);
                }
                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Unit Desc", JOptionPane.ERROR_MESSAGE);
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

    private void initTable() {
        try {
            tableModel.setListItemUnit(dao.findAll("ItemUnit"));

            tblUnit.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblUnit.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (tblUnit.getSelectedRow() >= 0) {
                        selectedRow = tblUnit.convertRowIndexToModel(
                                tblUnit.getSelectedRow());
                        setCurrItemUnit(tableModel.getItemUnit(selectedRow));
                    }
                }
            });
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblUnit;
    private javax.swing.JTextField txtUnitDesc;
    private javax.swing.JTextField txtUnitShort;
    // End of variables declaration//GEN-END:variables
}
