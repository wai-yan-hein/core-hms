/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * PackingTemplateSetup.java
 *
 * Created on May 6, 2012, 8:58:57 AM
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.PackingTemplate;
import com.cv.app.pharmacy.database.entity.PackingTemplateDetail;
import com.cv.app.pharmacy.ui.common.PackingTemplateTableModel;
import com.cv.app.pharmacy.ui.common.TableUnitCellEditor;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.JTableBinding.ColumnBinding;
import org.jdesktop.swingbinding.SwingBindings;

/**
 *
 * @author winswe
 */
public class PackingTemplateSetup extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(PackingTemplateSetup.class.getName());
    private java.util.List<PackingTemplate> listPackingTemplate
            = new java.util.ArrayList();
    private JComboBox cboUnit = new JComboBox();
    private PackingTemplate currPackingTemplate = new PackingTemplate();
    private final AbstractDataAccess dao = Global.dao;
    private BestAppFocusTraversalPolicy focusPolicy;
    private PackingTemplateTableModel tableModel = new PackingTemplateTableModel();

    /**
     * Creates new form PackingTemplateSetup
     */
    public PackingTemplateSetup() {
        initComponents();

        try {
            dao.open();
            initCombo();
            initTableRelation();
            initTableTemplate();
            dao.close();
        } catch (Exception ex) {
            log.error("PackingTemplateSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        applyFocusPolicy();
        AddFocusMoveKey();
        this.setFocusTraversalPolicy(focusPolicy);
        tableModel.addNewRow();
        actionMapping();
    }

    public void setCurrPackingTemplate(PackingTemplate currPackingTemplate) {
        try {
            lblStatus.setText("EDIT");
            stopCellEditing(tblRelation);
            dao.open();
            this.currPackingTemplate = (PackingTemplate) dao.find(PackingTemplate.class,
                    currPackingTemplate.getTemplateId());
            txtRelationString.setText(this.currPackingTemplate.getRelStr());

            if (this.currPackingTemplate.getLstPackingTemplateDetail() != null) {
                List<PackingTemplateDetail> list = this.currPackingTemplate.getLstPackingTemplateDetail();
                tableModel.setListTemplate(list);
            }

            dao.close();
        } catch (Exception ex) {
            log.error("setCurrPackingTemplate : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    private void clear() {
        lblStatus.setText("NEW");
        stopCellEditing(tblRelation);
        txtRelationString.setText(null);
        currPackingTemplate = new PackingTemplate();
        tableModel.removeAll();
        tableModel.addNewRow();

        setFocus();
    }

    private void initCombo() {
        try {
            BindingUtil.BindCombo(cboUnit, dao.findAll("ItemUnit"));
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public void setFocus() {
        tblRelation.requestFocusInWindow();
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

    private void actionMapping() {
        //Enter event on tblRelation
        tblRelation.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ENTER-Action");
        tblRelation.getActionMap().put("ENTER-Action", actionTblRelationEnterKey);

        //F8 event on tblRelation
        tblRelation.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblRelation.getActionMap().put("F8-Action", actionItemDelete);
    }
    private Action actionTblRelationEnterKey = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (tblRelation.getCellEditor() != null) {
                    tblRelation.getCellEditor().stopCellEditing();
                }
            } catch (Exception ex) {
            }

            int row = tblRelation.getSelectedRow();
            int col = tblRelation.getSelectedColumn();

            PackingTemplateDetail ptd = tableModel.getTemplateDetail(row);

            if (col == 0 && ptd.getUnitQty() != null) {
                tblRelation.setColumnSelectionInterval(1, 1); //Move to Unit
            } else if (col == 1 && ptd.getItemUnit() != null) {
                if ((row + 1) <= tableModel.getListTemplate().size() + 1) {
                    tblRelation.setRowSelectionInterval(row + 1, row + 1);
                }
                tblRelation.setColumnSelectionInterval(0, 0); //Move to Code
            }
        }
    };
    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int row = tblRelation.getSelectedRow();

            if (row >= 0) {
                stopCellEditing(tblRelation);
                tableModel.deleteRow(row);
            }
        }
    };

    public void stopCellEditing(JTable table) {
        try {
            if (table.getCellEditor() != null) {
                table.getCellEditor().stopCellEditing();
            }
        } catch (Exception ex) {
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
        tblTemplate = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtRelationString = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRelation = new javax.swing.JTable();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        butDelete = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();

        tblTemplate.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblTemplate.setModel(new javax.swing.table.DefaultTableModel(
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
        tblTemplate.setRowHeight(23);
        tblTemplate.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblTemplate);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Relation String");

        txtRelationString.setEditable(false);
        txtRelationString.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        tblRelation.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblRelation.setRowHeight(23);
        tblRelation.setShowVerticalLines(false);
        jScrollPane2.setViewportView(tblRelation);

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

        lblStatus.setText("NEW");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, 0, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(10, 10, 10)
                        .addComponent(txtRelationString, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butDelete)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblStatus)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butDelete, butSave});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtRelationString, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave)
                            .addComponent(butDelete))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblStatus)
                        .addGap(75, 75, 75))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        try {
            stopCellEditing(tblRelation);
            if (tableModel.isValidEntry()) {
                currPackingTemplate.setRelStr(txtRelationString.getText());
                List<PackingTemplateDetail> listDetail
                        = tableModel.getListTemplate();
                currPackingTemplate.setLstPackingTemplateDetail(listDetail);

                dao.save(currPackingTemplate);
                clear();
                dao.open();
                initTableRelation();
                initTableTemplate();
                dao.close();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Relation", JOptionPane.ERROR_MESSAGE);
            dao.rollBack();
        } catch (Exception ex) {
            log.error("butSaveActionPerformed : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butSaveActionPerformed

    private void butDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butDeleteActionPerformed
        try {
            int row = tblTemplate.getSelectedRow();
            dao.delete(currPackingTemplate);
            clear();
            listPackingTemplate.remove(row);
        } catch (Exception ex) {
            log.error("butDeleteActionPerformed " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }//GEN-LAST:event_butDeleteActionPerformed

    private void initTableRelation() {
        try {
            tblRelation.setModel(tableModel);
            addTableModelListener();
            tblRelation.getColumnModel().getColumn(0).setCellEditor(
                    new BestTableCellEditor());
            tblRelation.getColumnModel().getColumn(1).setCellEditor(
                    new TableUnitCellEditor(dao.findAll("ItemUnit")));
        } catch (Exception ex) {
            log.error("initTableRelation : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void addTableModelListener() {
        tblRelation.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                txtRelationString.setText(tableModel.getRelCodeAndRelStr());
            }
        });
    }

    private void initTableTemplate() {
        try {
            listPackingTemplate = ObservableCollections.observableList(dao.findAll("PackingTemplate"));

            JTableBinding jTableBinding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE,
                    listPackingTemplate, tblTemplate);
            ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${relStr}"));
            columnBinding.setColumnName("Relation String");
            columnBinding.setColumnClass(String.class);
            columnBinding.setEditable(false);
            System.out.println("Before bind.");
            jTableBinding.bind();

            tblTemplate.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tblTemplate.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    int row = tblTemplate.getSelectedRow();
                    if (row >= 0) {
                        setCurrPackingTemplate(listPackingTemplate.get(tblTemplate.convertRowIndexToModel(row)));
                    }
                }
            });
        } catch (Exception ex) {
            log.error("initTableTemplate : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblRelation;
    private javax.swing.JTable tblTemplate;
    private javax.swing.JTextField txtRelationString;
    // End of variables declaration//GEN-END:variables
}
