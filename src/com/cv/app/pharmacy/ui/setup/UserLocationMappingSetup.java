/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.setup;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.ui.common.UserLocationMappingTableModel;
import com.cv.app.util.BindingUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class UserLocationMappingSetup extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(UserLocationMappingSetup.class.getName());
    private final UserLocationMappingTableModel tableModel = new UserLocationMappingTableModel();
    private final AbstractDataAccess dao = Global.dao;
    private boolean bindStatus = false;

    /**
     * Creates new form UserLocationMappingSetup
     */
    public UserLocationMappingSetup() {
        initComponents();
        initCombo();
        initTable();
    }

    private void initCombo() {
        bindStatus = true;
        try {
            BindingUtil.BindCombo(cboUser, dao.findAllHSQL("select o from Appuser o order by o.userName"));
            cboUser.setSelectedItem(null);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
        bindStatus = false;
    }

    private void initTable() {
        tblUserLocationMapping.getColumnModel().getColumn(0).setPreferredWidth(150);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cboUser = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblUserLocationMapping = new javax.swing.JTable();

        jLabel1.setText("User : ");

        cboUser.setFont(Global.textFont);
        cboUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUserActionPerformed(evt);
            }
        });

        tblUserLocationMapping.setFont(Global.textFont);
        tblUserLocationMapping.setModel(tableModel);
        tblUserLocationMapping.setRowHeight(23);
        jScrollPane1.setViewportView(tblUserLocationMapping);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboUser, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cboUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cboUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUserActionPerformed
        if (cboUser.getSelectedItem() != null && !bindStatus) {
            tableModel.setUserId(((Appuser) cboUser.getSelectedItem()).getUserId());
        }
    }//GEN-LAST:event_cboUserActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblUserLocationMapping;
    // End of variables declaration//GEN-END:variables
}
