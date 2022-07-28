/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.LocationType;
import com.cv.app.pharmacy.ui.common.LocationTypeTableModel;
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
import javax.swing.RowFilter;
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
public class LocationTypeSetupDialog extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(LocationTypeSetupDialog.class.getName());
    private final AbstractDataAccess dao = Global.dao; // Data access object.    
    private LocationTypeTableModel ltTableModel = new LocationTypeTableModel();
    private LocationType currLT = new LocationType();
    private BestAppFocusTraversalPolicy focusPolicy;
    private int selectedRow = -1;
    private final TableRowSorter<TableModel> sorter;
    private final StartWithRowFilter swrf;

    /**
     * Creates new form LocationTypeSetupDialog
     */
    public LocationTypeSetupDialog() {
        super(Util1.getParent(), true);
        initComponents();

        try {
            dao.open();
            initTable();
            dao.close();
        } catch (Exception ex) {
            log.error("CategorySetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        //applyFocusPolicy();
        //AddFocusMoveKey();
        //this.setFocusTraversalPolicy(focusPolicy);
        swrf = new StartWithRowFilter(txtFilter);
        sorter = new TableRowSorter(tblLocationType.getModel());
        tblLocationType.setRowSorter(sorter);
    }

    public void setLocationType(LocationType lt) {
        currLT = lt;
        txtTypeName.setText(currLT.getDescription());
        lblStatus.setText("EDIT");
    }

    private void clear() {
        selectedRow = -1;
        currLT = new LocationType();
        txtTypeName.setText(null);
        lblStatus.setText("NEW");
        setFocus();
        tblLocationType.clearSelection();
    }

    /*
     * Initialize tblCategory
     */
    private void initTable() {
        //Get Category from database.
        try {
            ltTableModel.setListLT(dao.findAllHSQL("select o from LocationType o order by o.description"));
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
        //Define table selection model to single row selection.
        tblLocationType.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblLocationType.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblLocationType.getSelectedRow() >= 0) {
                    selectedRow = tblLocationType.convertRowIndexToModel(
                            tblLocationType.getSelectedRow());
                }

                if (selectedRow >= 0) {
                    setLocationType(ltTableModel.getLocationType(selectedRow));
                }
            }
        });
    }

    public void setFocus() {
        txtTypeName.requestFocusInWindow();
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

        if (Util1.nullToBlankStr(txtTypeName.getText()).equals("")) {
            status = false;
        } else {
            currLT.setDescription(txtTypeName.getText().trim());
        }

        return status;
    }

    private void delete() {
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Category Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(currLT);
                    int tmpRow = selectedRow;
                    selectedRow = -1;
                    ltTableModel.deleteLocationType(tmpRow);
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete this category.",
                        "Category Delete", JOptionPane.ERROR_MESSAGE);
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

        lblStatus = new javax.swing.JLabel();
        txtFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLocationType = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtTypeName = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        butDelete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Location Type Setup");
        setMinimumSize(new java.awt.Dimension(500, 400));
        setPreferredSize(new java.awt.Dimension(600, 500));

        lblStatus.setText("NEW");

        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        tblLocationType.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblLocationType.setModel(ltTableModel);
        tblLocationType.setRowHeight(23);
        tblLocationType.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblLocationType);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Type Name");

        txtTypeName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

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

        butDelete.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butDelete.setText("Delete");
        butDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilter)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTypeName))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtTypeName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave)
                            .addComponent(butDelete))
                        .addGap(17, 17, 17)
                        .addComponent(lblStatus))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );

        pack();
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

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        try {
            if (isValidEntry()) {
                dao.save(currLT);
                if (lblStatus.getText().equals("NEW")) {
                    ltTableModel.addLocationType(currLT);
                } else {
                    ltTableModel.setLocationType(selectedRow, currLT);
                }
                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Category Save", JOptionPane.ERROR_MESSAGE);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblLocationType;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtTypeName;
    // End of variables declaration//GEN-END:variables
}
