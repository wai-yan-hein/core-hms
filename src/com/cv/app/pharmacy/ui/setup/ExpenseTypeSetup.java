/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.BestAppFocusTraversalPolicy;
import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ExpenseType;
import com.cv.app.opd.database.entity.OPDCusLabGroup;
import com.cv.app.ot.database.entity.OTCusGroup;
import com.cv.app.inpatient.database.entity.DCCusGroup;
import com.cv.app.pharmacy.database.entity.ExpenseTypeAcc;
import com.cv.app.pharmacy.ui.common.ExpenseTypeTableModel;
import com.cv.app.pharmacy.ui.common.ExpenseTypeAccountSetupTableModel;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.Util1;
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
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author WSwe
 */
public class ExpenseTypeSetup extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(ExpenseTypeSetup.class.getName());
    private final AbstractDataAccess dao = Global.dao; // Data access object.    
    private ExpenseTypeTableModel tableModel = new ExpenseTypeTableModel();
    private BestAppFocusTraversalPolicy focusPolicy;
    private ExpenseType currExpenseType = new ExpenseType();
    private int selectedRow = -1;
    private ExpenseTypeAccountSetupTableModel tableModel1 = new ExpenseTypeAccountSetupTableModel();

    /**
     * Creates new form SaleExpenseTypeSetup
     */
    public ExpenseTypeSetup() {
        initComponents();
        try {
            dao.open();
            initTable();
            dao.close();
        } catch (Exception ex) {
            log.error("ExpenseTypeSetup : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        applyFocusPolicy();
        AddFocusMoveKey();
        actionMapping();
        this.setFocusTraversalPolicy(focusPolicy);
        cboUseFor.setSelectedItem(null);
    }

    public void setCurrExpenseType(ExpenseType currExpenseType) {
        this.currExpenseType = currExpenseType;
        txtExpName.setText(currExpenseType.getExpenseName());
        cboUseFor.setSelectedItem(currExpenseType.getExpenseOption());
        txtSysCode.setText(currExpenseType.getSysCode());
        lblStatus.setText("EDIT");
        chkNeedDoctor.setSelected(currExpenseType.getNeedDr());
        try {
            String useFor = currExpenseType.getExpenseOption();
            switch (useFor) {
                case "Pharmacy":
                    //BindingUtil.BindCombo(cboGroup, dao.findAllHSQL("select o from ItemType o order by o.itemTypeName"));
                    //cboGroup.setSelectedItem(ABORT);
                    break;
                case "OPD":
                    if (currExpenseType.getCusGroupoId() == null) {
                        cboGroup.setSelectedItem(null);
                    } else {
                        OPDCusLabGroup opdGroup = (OPDCusLabGroup) dao.find(OPDCusLabGroup.class, currExpenseType.getCusGroupoId());
                        cboGroup.setSelectedItem(opdGroup);
                    }
                    break;
                case "OT":
                    if (currExpenseType.getCusGroupoId() == null) {
                        cboGroup.setSelectedItem(null);
                    } else {
                        OTCusGroup otGroup = (OTCusGroup) dao.find(OTCusGroup.class, currExpenseType.getCusGroupoId());
                        cboGroup.setSelectedItem(otGroup);
                    }
                    break;
                case "DC":
                    if (currExpenseType.getCusGroupoId() == null) {
                        cboGroup.setSelectedItem(null);
                    } else {
                        DCCusGroup dcGroup = (DCCusGroup) dao.find(DCCusGroup.class, currExpenseType.getCusGroupoId());
                        cboGroup.setSelectedItem(dcGroup);
                    }
                    break;
            }

            Integer id = currExpenseType.getExpenseId();
            tableModel1.setExpTypeId(id);
            List<ExpenseTypeAcc> listDetail = dao.findAllHSQL(
                    "select o from ExpenseTypeAcc o where o.expTypeId = " + id.toString() + " order by o.id");
            tableModel1.setListAS(listDetail);
        } catch (Exception ex) {
            log.error("setCurrExpenseType : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void clear() {
        tblSaleExpType.clearSelection();
        tableModel1.clear();
        selectedRow = -1;
        lblStatus.setText("NEW");
        txtExpName.setText(null);
        currExpenseType = new ExpenseType();
        cboUseFor.setSelectedItem(null);
        cboGroup.setSelectedItem(null);
        txtSysCode.setText(null);
        chkNeedDoctor.setSelected(false);
        setFocus();
    }

    private void initTable() {
        //Get ExpenseType from database.
        try {
            tableModel.setListExpenseType(
                    dao.findAllHSQL("select o from ExpenseType o order by o.expenseName"));
        } catch (Exception ex) {
            log.error("initTable : " + ex.toString());
        } finally {
            dao.close();
        }
        //Binding table with listCategory using beansbinding library.
        /*JTableBinding jTableBinding = SwingBindings.createJTableBinding(AutoBinding.UpdateStrategy.READ_WRITE,
         listExpenseType, tblSaleExpType);
         JTableBinding.ColumnBinding columnBinding = jTableBinding.addColumnBinding(org.jdesktop.beansbinding.ELProperty.create("${expenseName}"));
         columnBinding.setColumnName("Expense Name");
         columnBinding.setColumnClass(String.class);
         columnBinding.setEditable(false);
         jTableBinding.bind();*/
        //Define table selection model to single row selection.
        tblSaleExpType.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //Adding table row selection listener.
        tblSaleExpType.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (tblSaleExpType.getSelectedRow() >= 0) {
                        selectedRow = tblSaleExpType.convertRowIndexToModel(
                                tblSaleExpType.getSelectedRow());
                    }
                    if (selectedRow >= 0) {
                        setCurrExpenseType(tableModel.getExpenseType(selectedRow));
                    }
                }
            }
        });

        tblAccount.getColumnModel().getColumn(0).setPreferredWidth(30);//Source Account
        tblAccount.getColumnModel().getColumn(1).setPreferredWidth(30);//Account
        tblAccount.getColumnModel().getColumn(2).setPreferredWidth(30);//Dept
        tblAccount.getColumnModel().getColumn(3).setPreferredWidth(30);//Use For
        tblAccount.getColumnModel().getColumn(4).setPreferredWidth(100);//Remark
        tblAccount.getColumnModel().getColumn(5).setPreferredWidth(30);//Un Paid Account

        tblAccount.getColumnModel().getColumn(0).setCellEditor(new BestTableCellEditor());
        tblAccount.getColumnModel().getColumn(1).setCellEditor(new BestTableCellEditor());
        tblAccount.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor());
        tblAccount.getColumnModel().getColumn(3).setCellEditor(new BestTableCellEditor());
        tblAccount.getColumnModel().getColumn(4).setCellEditor(new BestTableCellEditor());
        tblAccount.getColumnModel().getColumn(5).setCellEditor(new BestTableCellEditor());
        //JComboBox cboAccUseFor = new JComboBox();
        //List<String> listUseFor = new ArrayList();
        //listUseFor.add("OPD");
        //listUseFor.add("Admission");
        //BindingUtil.BindCombo(cboAccUseFor, listUseFor);
        //tblAccount.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cboAccUseFor));
        
    }

    public void setFocus() {
        txtExpName.requestFocusInWindow();
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

        if (Util1.nullToBlankStr(txtExpName.getText()).equals("")) {
            status = false;
            JOptionPane.showMessageDialog(this, "Exp-Name cannot be blank or null.",
                    "Null error.", JOptionPane.ERROR_MESSAGE);
            txtExpName.requestFocusInWindow();
        } else {
            currExpenseType.setExpenseName(txtExpName.getText().trim());
            currExpenseType.setSysCode(txtSysCode.getText().trim());
            currExpenseType.setNeedDr(chkNeedDoctor.isSelected());
            if (cboUseFor.getSelectedItem() != null) {
                currExpenseType.setExpenseOption(cboUseFor.getSelectedItem().toString());
            } else {
                currExpenseType.setExpenseOption(null);
            }
            if (cboGroup.getSelectedItem() != null) {
                Integer id = -1;
                String useFor = currExpenseType.getExpenseOption();
                switch (useFor) {
                    case "Pharmacy":
                        //BindingUtil.BindCombo(cboGroup, dao.findAllHSQL("select o from ItemType o order by o.itemTypeName"));
                        //cboGroup.setSelectedItem(ABORT);
                        break;
                    case "OPD":
                        OPDCusLabGroup opdGroup = (OPDCusLabGroup) cboGroup.getSelectedItem();
                        id = opdGroup.getId();
                        break;
                    case "OT":
                        OTCusGroup otGroup = (OTCusGroup) cboGroup.getSelectedItem();
                        id = otGroup.getId();
                        break;
                    case "DC":
                        DCCusGroup dcGroup = (DCCusGroup) cboGroup.getSelectedItem();
                        id = dcGroup.getId();
                        break;
                }
                currExpenseType.setCusGroupoId(id);
            } else {
                currExpenseType.setCusGroupoId(null);
                status = false;
                JOptionPane.showMessageDialog(this, "Invalid group. Please select one of the group.",
                        "Invalid group.", JOptionPane.ERROR_MESSAGE);
            }
        }

        return status;
    }

    private void delete() {
        if (lblStatus.getText().equals("EDIT")) {
            try {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(), "Are you sure to delete?",
                        "Expense Type Delete", JOptionPane.YES_NO_OPTION);

                if (yes_no == 0) {
                    dao.delete(currExpenseType);
                    int tmpRow = selectedRow;
                    selectedRow = -1;
                    tableModel.deleteExpenseType(tmpRow);
                }
            } catch (ConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "Cannot delete this expense type.",
                        "Expense Type Delete", JOptionPane.ERROR_MESSAGE);
                dao.rollBack();
            } catch (Exception ex) {
                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            } finally {
                dao.close();
            }
        }

        clear();
    }

    private void initCombo(String useFor) {
        try {
            switch (useFor) {
                case "Pharmacy":
                    //BindingUtil.BindCombo(cboGroup, dao.findAllHSQL("select o from ItemType o order by o.itemTypeName"));
                    break;
                case "OPD":
                    BindingUtil.BindCombo(cboGroup, dao.findAllHSQL("select o from OPDCusLabGroup o order by o.groupName"));
                    break;
                case "OT":
                    BindingUtil.BindCombo(cboGroup, dao.findAllHSQL("select o from OTCusGroup o order by o.groupName"));
                    break;
                case "DC":
                    BindingUtil.BindCombo(cboGroup, dao.findAllHSQL("select o from DCCusGroup o order by o.groupName"));
                    break;
            }
        } catch (Exception ex) {
            log.error("initCombo : " + ex.toString());
        } finally {
            dao.close();
        }
        new ComBoBoxAutoComplete(cboGroup);
    }

    private Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (tblAccount.getSelectedRow() >= 0) {
                int yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                        "Are you sure to delete?",
                        "Sale item delete", JOptionPane.YES_NO_OPTION);
                if (tblAccount.getCellEditor() != null) {
                    tblAccount.getCellEditor().stopCellEditing();
                }
                if (yes_no == 0) {
                    tableModel1.delete(tblAccount.getSelectedRow());
                }
            }
        }
    };

    private void actionMapping() {
        tblAccount.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblAccount.getActionMap().put("F8-Action", actionItemDelete);
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
        tblSaleExpType = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtExpName = new javax.swing.JTextField();
        butClear = new javax.swing.JButton();
        butSave = new javax.swing.JButton();
        lblStatus = new javax.swing.JLabel();
        butDelete = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cboUseFor = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtSysCode = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        cboGroup = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAccount = new javax.swing.JTable();
        chkNeedDoctor = new javax.swing.JCheckBox();

        setPreferredSize(new java.awt.Dimension(1024, 600));

        tblSaleExpType.setFont(Global.textFont);
        tblSaleExpType.setModel(tableModel);
        tblSaleExpType.setRowHeight(23);
        tblSaleExpType.setShowVerticalLines(false);
        jScrollPane1.setViewportView(tblSaleExpType);

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Description");

        txtExpName.setFont(Global.textFont);

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

        butDelete.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butDelete.setText("Delete");
        butDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butDeleteActionPerformed(evt);
            }
        });

        jLabel3.setFont(Global.lableFont);
        jLabel3.setText("Use For ");

        cboUseFor.setFont(Global.textFont);
        cboUseFor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pharmacy", "OPD", "OT", "DC" }));
        cboUseFor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUseForActionPerformed(evt);
            }
        });

        jLabel5.setFont(Global.lableFont);
        jLabel5.setText("Sys Code");

        txtSysCode.setFont(Global.textFont);

        jLabel6.setFont(Global.lableFont);
        jLabel6.setText("Group");

        cboGroup.setFont(Global.textFont);

        tblAccount.setFont(Global.textFont);
        tblAccount.setModel(tableModel1);
        tblAccount.setRowHeight(23);
        jScrollPane2.setViewportView(tblAccount);

        chkNeedDoctor.setText("Need Doctor");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cboUseFor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSysCode)
                            .addComponent(txtExpName)))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(chkNeedDoctor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(butSave)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(butDelete)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(butClear, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cboGroup, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butDelete, butSave});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel3, jLabel5, jLabel6});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                        .addGap(14, 14, 14))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtExpName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cboUseFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtSysCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(cboGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(butClear)
                            .addComponent(butSave)
                            .addComponent(butDelete)
                            .addComponent(lblStatus)
                            .addComponent(chkNeedDoctor))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        try {
            if (isValidEntry()) {
                dao.save(currExpenseType);
                if (lblStatus.getText().equals("NEW")) {
                    tableModel.addExpenseType(currExpenseType);
                } else {
                    tableModel.setExpenseType(selectedRow, currExpenseType);
                }
                clear();
            }
        } catch (ConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Duplicate entry.",
                    "Expense Type Name", JOptionPane.ERROR_MESSAGE);
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

    private void cboUseForActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUseForActionPerformed
        String useFor = "-";
        if (cboUseFor.getSelectedItem() != null) {
            useFor = cboUseFor.getSelectedItem().toString();
        }
        initCombo(useFor);
    }//GEN-LAST:event_cboUseForActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butDelete;
    private javax.swing.JButton butSave;
    private javax.swing.JComboBox<String> cboGroup;
    private javax.swing.JComboBox<String> cboUseFor;
    private javax.swing.JCheckBox chkNeedDoctor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblAccount;
    private javax.swing.JTable tblSaleExpType;
    private javax.swing.JTextField txtExpName;
    private javax.swing.JTextField txtSysCode;
    // End of variables declaration//GEN-END:variables
}
