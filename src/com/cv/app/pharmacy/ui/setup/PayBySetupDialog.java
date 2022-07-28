/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.PayBy;
import com.cv.app.util.Util1;
import com.cv.app.pharmacy.ui.common.PayByTableModel;
import javax.swing.JOptionPane;
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
public class PayBySetupDialog extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(PayBySetupDialog.class.getName());
    private final AbstractDataAccess dao = Global.dao; // Data access object.    
    private PayByTableModel ltTableModel = new PayByTableModel();
    private PayBy payBy = new PayBy();
    private int selectedRow = -1;
    private final TableRowSorter<TableModel> sorter;
    private final StartWithRowFilter swrf;

    /**
     * Creates new form LocationTypeSetupDialog
     */
    public PayBySetupDialog() {
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

    public void setPayBy(PayBy pb) {
        payBy = pb;
        txtTypeName.setText(pb.getDesp());
        chkStatus.setSelected(pb.getActiveStatus());
        lblStatus.setText("EDIT");
    }

    private void clear() {
        selectedRow = -1;
        chkStatus.setSelected(true);
        payBy = new PayBy();
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
            ltTableModel.setListPB(dao.findAllHSQL("select o from PayBy o order by o.desp"));
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
                    setPayBy(ltTableModel.getPayBy(selectedRow));
                }
            }
        });
    }

    public void setFocus() {
        txtTypeName.requestFocusInWindow();
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (Util1.nullToBlankStr(txtTypeName.getText()).equals("")) {
            status = false;
        } else {
            payBy.setDesp(txtTypeName.getText().trim());
            payBy.setActiveStatus(chkStatus.isSelected());
        }

        return status;
    }

    private void delete() {
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(payBy);
                    int tmpRow = selectedRow;
                    selectedRow = -1;
                    ltTableModel.delete(tmpRow);
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
        jLabel2 = new javax.swing.JLabel();
        chkStatus = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Location Type Setup");
        setMinimumSize(new java.awt.Dimension(500, 400));

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
        jLabel1.setText("Desp");

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

        jLabel2.setText("Status");

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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblStatus)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                        .addGap(14, 14, 14))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(chkStatus))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave)
                            .addComponent(butDelete))
                        .addGap(17, 17, 17)
                        .addComponent(lblStatus)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
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
                dao.save(payBy);
                if (lblStatus.getText().equals("NEW")) {
                    ltTableModel.addPayBy(payBy);
                } else {
                    ltTableModel.setPayBy(selectedRow, payBy);
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
    private javax.swing.JCheckBox chkStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblLocationType;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtTypeName;
    // End of variables declaration//GEN-END:variables
}
