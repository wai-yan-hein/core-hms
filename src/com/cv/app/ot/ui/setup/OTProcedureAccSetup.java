/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.ot.database.entity.OTProcedureGroup;
import com.cv.app.ot.ui.common.OTGroupTableModel1;
import com.cv.app.ot.ui.common.OTMedUsageTableModel;
import com.cv.app.ot.ui.common.OTServiceTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.ui.common.FormAction;
import com.cv.app.ui.common.BestTableCellEditor;
import com.cv.app.util.Util1;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author winswe
 */
public class OTProcedureAccSetup extends javax.swing.JPanel implements FormAction, KeyPropagate,
        SelectionObserver, KeyListener {

    private final AbstractDataAccess dao = Global.dao;
    private final OTGroupTableModel1 tblOTGroupTableModel = new OTGroupTableModel1(dao);
    private final OTServiceTableModel tblOTServiceTableModel = new OTServiceTableModel(dao);
    private final OTMedUsageTableModel tblOTServiceMedUsageTableModel = new OTMedUsageTableModel(dao, this);
    private final StartWithRowFilter swrfGroup;
    private final TableRowSorter<TableModel> sorterGroup;

    /**
     * Creates new form OTProcedure
     */
    public OTProcedureAccSetup() {
        initComponents();
        initTable();
        actionMapping();

        swrfGroup = new StartWithRowFilter(txtFilterGroup);
        sorterGroup = new TableRowSorter(tblOTGroup.getModel());
        tblOTGroup.setRowSorter(sorterGroup);

    }

    private void formActionKeyMapping(JComponent jc) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.CTRL_DOWN_MASK);
        jc.getInputMap().put(keyStroke, "Ctrl-F8-Action");
        jc.getActionMap().put("Ctrl-F8-Action", actionDelete);
    }

    private final Action actionDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            delete();
        }
    };

    //FormAction
    @Override
    public void save() {

    }

    @Override
    public void newForm() {

    }

    @Override
    public void history() {

    }

    @Override
    public void delete() {

    }

    private void actionMapping() {

        //F8 event on tblCat
        tblOTGroup.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblOTGroup.getActionMap().put("F8-Action", actionCatDelete);
    }

    private final Action actionItemDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    private final Action actionSerDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {

        }
    };

    private final Action actionCatDelete = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int yes_no = -1;

            if (tblOTGroup.getSelectedRow() >= 0) {

                try {
                    yes_no = JOptionPane.showConfirmDialog(Util1.getParent(),
                            "Are you sure to delete?",
                            "OT Group delete", JOptionPane.YES_NO_OPTION);

                    if(tblOTGroup.getCellEditor() != null){
                        tblOTGroup.getCellEditor().stopCellEditing();
                    }
                } catch (Exception ex) {
                }

                if (yes_no == 0) {
                    tblOTGroupTableModel.delete(tblOTGroup.getSelectedRow());

                }

            }
        }
    };

    @Override
    public void deleteCopy() {

    }

    @Override
    public void print() {

    }

    //KeyPropagate
    @Override
    public void keyEvent(KeyEvent e) {

    }

    //SelectionObserver
    @Override
    public void selected(Object source, Object selectObj) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        /*if (Util1.getPropValue("system.app.usage.type").equals("Hospital")) {
         if (!focusCtrlName.equals("-")) {
         if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtPatientNo")) {
         txtDoctorNo.requestFocus();
         } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtDoctorNo")) {
         txtDonorName.requestFocus();
         } else if (e.getKeyCode() == KeyEvent.VK_DOWN && focusCtrlName.equals("txtDonorName")) {
         tblService.requestFocus();
         }else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtCusId")) {
         txtDonorName.requestFocus();
         } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("txtDoctorNo")) {
         txtPatientNo.requestFocus();
         } else if (e.getKeyCode() == KeyEvent.VK_UP && focusCtrlName.equals("tblService")) {
         int selRow = tblService.getSelectedRow();

         if (selRow == -1 || selRow == 0) {
         txtDonorName.requestFocus();
         }
         }
         }
         }*/
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private void initForFocus() {
        /*txtPatientNo.addKeyListener(this);
         txtDoctorNo.addKeyListener(this);
         txtDonorName.addKeyListener(this);
         tblService.addKeyListener(this);*/
    }

    private void initTable() {
        tblOTGroup.getTableHeader().setFont(Global.lableFont);
        tblOTGroup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblOTGroup.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tblOTGroup.getSelectedRow() >= 0) {
                    int selectRow = tblOTGroup.convertRowIndexToModel(tblOTGroup.getSelectedRow());
                    OTProcedureGroup otpg = tblOTGroupTableModel.getListOTG().get(selectRow);
                    Integer groupId = otpg.getGroupId();

                    if (groupId != null) {
                        tblOTServiceTableModel.setGroupId(groupId);
                    } else {
                        tblOTServiceTableModel.setGroupId(-1);
                        tblOTServiceMedUsageTableModel.setSrvId(-1);
                    }
                }
            }
        });
        tblOTGroupTableModel.getOTGroup();
        tblOTGroup.getColumnModel().getColumn(0).setPreferredWidth(120);
        tblOTGroup.getColumnModel().getColumn(0).setCellEditor(new BestTableCellEditor());
        tblOTGroup.getColumnModel().getColumn(1).setCellEditor(new BestTableCellEditor());
        tblOTGroup.getColumnModel().getColumn(2).setCellEditor(new BestTableCellEditor());
        tblOTGroup.getColumnModel().getColumn(3).setCellEditor(new BestTableCellEditor());
        tblOTGroup.getColumnModel().getColumn(4).setCellEditor(new BestTableCellEditor());
        tblOTGroup.getColumnModel().getColumn(5).setCellEditor(new BestTableCellEditor());
        tblOTGroup.getColumnModel().getColumn(6).setCellEditor(new BestTableCellEditor());
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
        tblOTGroup = new javax.swing.JTable();
        txtFilterGroup = new javax.swing.JTextField();

        tblOTGroup.setFont(Global.textFont);
        tblOTGroup.setModel(tblOTGroupTableModel);
        tblOTGroup.setMinimumSize(new java.awt.Dimension(150, 64));
        tblOTGroup.setRowHeight(23);
        tblOTGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tblOTGroupKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(tblOTGroup);

        txtFilterGroup.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterGroupKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                    .addComponent(txtFilterGroup))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFilterGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblOTGroupKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblOTGroupKeyReleased
        if (txtFilterGroup.getText().isEmpty()) {
            sorterGroup.setRowFilter(null);
        } else {
            if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
                sorterGroup.setRowFilter(swrfGroup);
            } else {
                sorterGroup.setRowFilter(RowFilter.regexFilter(txtFilterGroup.getText()));
            }
        }
    }//GEN-LAST:event_tblOTGroupKeyReleased

    private void txtFilterGroupKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFilterGroupKeyReleased
        if (txtFilterGroup.getText().isEmpty()) {
            sorterGroup.setRowFilter(null);
        } else {
            if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
                sorterGroup.setRowFilter(swrfGroup);
            } else {
                sorterGroup.setRowFilter(RowFilter.regexFilter(txtFilterGroup.getText()));
            }
        }
    }//GEN-LAST:event_txtFilterGroupKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblOTGroup;
    private javax.swing.JTextField txtFilterGroup;
    // End of variables declaration//GEN-END:variables
}
