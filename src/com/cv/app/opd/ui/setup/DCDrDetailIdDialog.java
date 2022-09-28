/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.inpatient.database.view.VDCDrDetailId;
import com.cv.app.inpatient.ui.common.DCDrDetailSetupModel;
import com.cv.app.inpatient.ui.common.DCTableCellEditor;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.*;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import org.apache.log4j.Logger;

/**
 *
 * @author ACER
 */
public class DCDrDetailIdDialog extends javax.swing.JDialog {

    static Logger log = Logger.getLogger(DCDrDetailIdDialog.class.getName());
    private DCDrDetailSetupModel tableModel = new DCDrDetailSetupModel();
    private final AbstractDataAccess dao = Global.dao;

    /**
     * Creates new form DCDrDetailIdDialog
     */
    public DCDrDetailIdDialog() {
        super(Util1.getParent(), true);
        initComponents();
        initTable();
        actionMapping();
    }

    private void actionMapping() {
        //F8 event on tblService
        tblDrId.getInputMap().put(KeyStroke.getKeyStroke("F8"), "F8-Action");
        tblDrId.getActionMap().put("F8-Action", actionItemDelete);
    }

    private final Action actionItemDelete = new AbstractAction() {

        @Override
        public void actionPerformed(ActionEvent e) {
            int row = tblDrId.getSelectedRow();

            if (row >= 0) {
                if (tblDrId.getCellEditor() != null) {
                    tblDrId.getCellEditor().stopCellEditing();
                }
                tableModel.delete(row);
            }
        }
    };

    private void initTable() {
        try {
            List<VDCDrDetailId> list = dao.findAll("VDCDrDetailId");
            tableModel.setList(list);
            tblDrId.getColumnModel().getColumn(0).setCellEditor(
                    new DCTableCellEditor(dao));
        } catch (Exception ex) {
            log.error("initTable : " + ex.getMessage());
        } finally {
            dao.close();
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
        tblDrId = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("DC Doctor Detail ID");

        tblDrId.setModel(tableModel);
        tblDrId.setRowHeight(23);
        jScrollPane1.setViewportView(tblDrId);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblDrId;
    // End of variables declaration//GEN-END:variables
}
