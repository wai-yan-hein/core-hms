/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.inpatient.database.entity.Diagnosis;
import com.cv.app.inpatient.ui.common.DiagnosisTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.controller.BestDataAccess;
import com.cv.app.util.Util1;
import java.awt.Component;
import java.awt.Dimension;
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
public class DiagnosisSetup extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(DiagnosisSetup.class.getName());
    private final DiagnosisTableModel tableModel = new DiagnosisTableModel();
    private final AbstractDataAccess dao = Global.dao; // Data access object.
    private int selectRow = -1;
    private Diagnosis currDiag = new Diagnosis();
    private BestAppFocusTraversalPolicy focusPolicy;
    private TableRowSorter<TableModel> sorter;

    /**
     * Creates new form CitySetup
     */
    public DiagnosisSetup() {
        super(Util1.getParent(), true);
        initComponents();
        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
        initTable();
        sorter = new TableRowSorter(tblCity.getModel());
        tblCity.setRowSorter(sorter);
        lblStatus.setText("NEW");

        Dimension screen = Util1.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        setLocation(x, y);
        setVisible(true);
    }

    private void initTable() {
        try {
            tableModel.setListDiag(dao.findAll("Diagnosis"));
            tblCity.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblCity.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (tblCity.getSelectedRow() >= 0) {
                        selectRow = tblCity.convertRowIndexToModel(tblCity.getSelectedRow());
                    }

                    if (selectRow >= 0) {
                        setRecord(tableModel.getDiagnosis(selectRow));
                    }
                }
            });
        } catch (Exception ex) {
            log.equals("initTable : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void setRecord(Diagnosis diagnosis) {
        currDiag = diagnosis;
        txtLocalName.setText(diagnosis.getLocalName());
        txtIntCode.setText(diagnosis.getIntCode());
        txtIntName.setText(diagnosis.getIntName());
        lblStatus.setText("EDIT");
    }

    private void applyFocusPolicy() {
        @SuppressWarnings("UseOfObsoleteCollectionType")
        Vector<Component> focusOrder = new Vector();

        focusOrder.add(txtLocalName);
        focusOrder.add(txtIntCode);
        focusOrder.add(txtIntName);
        focusOrder.add(butSave);
        focusOrder.add(butDelete);
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
        lblStatus.setText("NEW");
        selectRow = -1;
        currDiag = new Diagnosis();
        txtLocalName.setText(null);
        txtIntCode.setText(null);
        txtIntName.setText(null);
        txtLocalName.requestFocusInWindow();
        System.gc();
    }

    private boolean isValidEntry() {
        boolean status = true;

        if (Util1.nullToBlankStr(txtLocalName.getText()).equals("")) {
            JOptionPane.showMessageDialog(this, "Local name must not be blank.",
                    "Blank", JOptionPane.ERROR_MESSAGE);
            status = false;
            txtLocalName.requestFocusInWindow();
        } else {
            currDiag.setLocalName(txtLocalName.getText());
            currDiag.setIntCode(txtIntCode.getText());
            currDiag.setIntName(txtIntName.getText());
        }

        return status;
    }

    private void save() {
        if (isValidEntry()) {
            try {
                dao.save(currDiag);
                if (lblStatus.getText().equals("NEW")) {
                    tableModel.addDiag(currDiag);
                } else {
                    tableModel.setDiagnosis(selectRow, currDiag);
                }
                clear();
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Duplicate entry.",
                        "Local Name", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                dao.rollBack();
            } finally {
                dao.close();
            }
        }
    }

    private void delete() {
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Diagnosis Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(currDiag);
                    int tmpRow = selectRow;
                    selectRow = -1;
                    tableModel.deleteDiag(tmpRow);
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete this Diagnosis.",
                        "Diagnosis Delete", JOptionPane.ERROR_MESSAGE);
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
        tblCity = new javax.swing.JTable();
        txtFilter = new javax.swing.JTextField();
        txtLocalName = new javax.swing.JTextField();
        txtIntCode = new javax.swing.JTextField();
        txtIntName = new javax.swing.JTextField();
        butSave = new javax.swing.JButton();
        butDelete = new javax.swing.JButton();
        butClear = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Diagnosis Setup");
        setPreferredSize(new java.awt.Dimension(1042, 600));

        tblCity.setFont(Global.textFont);
        tblCity.setModel(tableModel);
        tblCity.setRowHeight(23);
        jScrollPane1.setViewportView(tblCity);

        txtFilter.setFont(Global.textFont);
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        txtLocalName.setFont(Global.textFont);

        txtIntCode.setFont(Global.textFont);

        txtIntName.setFont(Global.textFont);

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

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("IDC Name");

        lblStatus.setText("NEW");

        jLabel2.setFont(Global.lableFont);
        jLabel2.setText("IDC Code");

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Local Name");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addComponent(jLabel1)
                                            .addGap(18, 18, 18))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel2)
                                            .addGap(30, 30, 30)))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtIntName)
                                        .addComponent(txtIntCode)
                                        .addComponent(txtLocalName, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addContainerGap()))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 81, Short.MAX_VALUE)
                        .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear)
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butDelete, butSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtLocalName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtIntCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtIntName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave)
                            .addComponent(butDelete))
                        .addGap(14, 14, 14)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void butDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleteActionPerformed
        delete();
    }//GEN-LAST:event_butDeleteActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void txtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterKeyReleased
        if (txtFilter.getText().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter(txtFilter.getText()));
        }
    }//GEN-LAST:event_txtFilterKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblCity;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JTextField txtIntCode;
    private javax.swing.JTextField txtIntName;
    private javax.swing.JTextField txtLocalName;
    // End of variables declaration//GEN-END:variables
}
