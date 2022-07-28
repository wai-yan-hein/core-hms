/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author User
 */
public class VendorBalance extends javax.swing.JPanel {

    private final AbstractDataAccess dao = Global.dao;
    private boolean bindStatus = false;
    static Logger log = Logger.getLogger(VendorBalance.class.getName());

    /**
     * Creates new form VendowBalance
     */
    public VendorBalance() {
        initComponents();
        txtDate.setText(DateUtil.getTodayDateStr());
        initCombo();
    }

    private void initCombo() {
        try {
            bindStatus = true;
            initVendorGroup();
            BindingUtil.BindComboFilter(cboBusinessType, dao.findAllHSQL("select o from BusinessType o order by o.description"));
            BindingUtil.BindComboFilter(cboTownship, dao.findAllHSQL("select o from Township o order by o.townshipName"));
            BindingUtil.BindComboFilter(cboLocation, getLocationFilter());
            bindStatus = false;
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private List getLocationFilter() {
        try {
            if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
                return dao.findAllHSQL(
                        "select o from Location o where o.locationId in ("
                        + "select a.key.locationId from UserLocationMapping a "
                        + "where a.key.userId = '" + Global.loginUser.getUserId()
                        + "' and a.isAllowSale = true) order by o.locationName");
            } else {
                return dao.findAll("Location");
            }
        } catch (Exception ex) {
            log.error("getLocationFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }

        return null;
    }

    private void initVendorGroup() {
        try {
            if (cboType.getSelectedItem().toString().equals("Customer")) {
                BindingUtil.BindComboFilter(cboVendorGroup,
                        dao.findAllHSQL("select o from CustomerGroup o where o.useFor = 'CUS' order by o.groupName"));
            } else {
                BindingUtil.BindComboFilter(cboVendorGroup,
                        dao.findAllHSQL("select o from CustomerGroup o where o.useFor = 'SUP' order by o.groupName"));
            }
        } catch (Exception ex) {
            log.error("initVendorGroup : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void execTraderBalanceDate() {
        try {
            dao.execProc("trader_balance_date",
                    DateUtil.toDateStrMYSQL(txtDate.getText()),
                    Global.machineId);
        } catch (Exception ex) {
            log.error("execTraderBalanceDate : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void insertTraderFilterCode(String disc) {
        String currencyId = "MMK";
        String strSQLDelete = "delete from tmp_trader_bal_filter where user_id = '"
                + Global.machineId + "'";
        String strSQL = "insert into tmp_trader_bal_filter(trader_id,currency,op_date,user_id, amount) "
                + "select t.trader_id, t.cur_code, "
                + " ifnull(trop.op_date, '1900-01-01'),'" + Global.machineId
                + "', ifnull(trop.op_amount,0)"
                + " from v_trader_cur t left join "
                + "(select a.trader_id, a.currency, a.op_date, a.op_amount\n"
                + "from trader_op a, (select trader_id, currency, max(op_date) op_date from trader_op \n"
                + "where op_date <= '" + DateUtil.toDateStrMYSQL(txtDate.getText()) + "' group by trader_id, currency) b\n"
                + "where a.trader_id = b.trader_id and a.currency = b.currency and a.op_date = b.op_date) trop on t.trader_id = trop.trader_id \n"
                + " and t.cur_code = trop.currency ";

        String strFilter = "";

        if (disc.equals("C") || disc.equals("S") || disc.equals("P")) {
            if (strFilter.isEmpty()) {
                strFilter = "discriminator = '" + disc + "'";
            } else {
                strFilter = strFilter + " and discriminator = '" + disc + "'";
            }
        }

        if (strFilter.isEmpty()) {
            strFilter = "t.cur_code = '" + currencyId + "'";
        } else {
            strFilter = strFilter + " and t.cur_code = '" + currencyId + "'";
        }

        /*if (cboVendorGroup.getSelectedItem() instanceof CustomerGroup) {
            String cusGID = ((CustomerGroup) cboVendorGroup.getSelectedItem()).getGroupId();
            if (strFilter.isEmpty()) {
                strFilter = "group_id = '" + cusGID + "'";
            } else {
                strFilter = strFilter + " and group_id = '" + cusGID + "'";
            }
        }

        if (cboTownship.getSelectedItem() instanceof Township) {
            String townshipId = ((Township) cboTownship.getSelectedItem()).getTownshipId().toString();
            if (strFilter.isEmpty()) {
                strFilter = "t.township = " + townshipId;
            } else {
                strFilter = strFilter + " and t.township = " + townshipId;
            }
        }*/

 /*if (traderTableModel.getRowCount() > 1) {
            if (strFilter.isEmpty()) {
                strFilter = "t.trader_id in (select trader_id from tmp_trader_filter "
                        + " where user_id = '" + Global.machineId + "')";
            } else {
                strFilter = strFilter + " and t.trader_id in (select trader_id from tmp_trader_filter "
                        + " where user_id = '" + Global.machineId + "')";
            }
        }*/
        if (!strFilter.isEmpty()) {
            strSQL = strSQL + " where " + strFilter;
        }

        try {
            dao.open();
            dao.execSql(strSQLDelete);
            dao.close();

            dao.open();
            dao.execSql(strSQL);
            //dao.execSql(strSQLDelete, strSQL);
        } catch (Exception ex) {
            log.error("insertTraderFilterCode : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void applyFilter() {

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtFilter = new javax.swing.JTextField();
        cboType = new javax.swing.JComboBox<>();
        txtDate = new javax.swing.JTextField();
        cboVendorGroup = new javax.swing.JComboBox<>();
        cboTownship = new javax.swing.JComboBox<>();
        cboLocation = new javax.swing.JComboBox<>();
        cboStatus = new javax.swing.JComboBox<>();
        cboBusinessType = new javax.swing.JComboBox<>();
        butRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();

        txtFilter.setFont(Global.textFont);

        cboType.setFont(Global.textFont);
        cboType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Customer", "Supplier" }));
        cboType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTypeActionPerformed(evt);
            }
        });

        cboVendorGroup.setFont(Global.textFont);

        cboTownship.setFont(Global.textFont);

        cboLocation.setFont(Global.textFont);

        cboStatus.setFont(Global.textFont);
        cboStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Active", "In Active" }));

        cboBusinessType.setFont(Global.textFont);

        butRefresh.setText("Get Balance");
        butRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRefreshActionPerformed(evt);
            }
        });

        jTable1.setFont(Global.textFont);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable1.setRowHeight(23);
        jScrollPane1.setViewportView(jTable1);

        jFormattedTextField1.setEditable(false);

        jLabel1.setText("Total Amount : ");

        jLabel2.setText("Total Records : ");

        jFormattedTextField2.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboBusinessType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboVendorGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboTownship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butRefresh))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboVendorGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTownship, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboBusinessType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cboTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTypeActionPerformed
        if (!bindStatus) {
            initVendorGroup();
        }
    }//GEN-LAST:event_cboTypeActionPerformed

    private void butRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRefreshActionPerformed
        String disc;
        if (cboType.getSelectedItem().toString().equals("Customer")) {
            disc = "C";
        } else {
            disc = "S";
        }
        insertTraderFilterCode(disc);
        execTraderBalanceDate();
    }//GEN-LAST:event_butRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butRefresh;
    private javax.swing.JComboBox<String> cboBusinessType;
    private javax.swing.JComboBox<String> cboLocation;
    private javax.swing.JComboBox<String> cboStatus;
    private javax.swing.JComboBox<String> cboTownship;
    private javax.swing.JComboBox<String> cboType;
    private javax.swing.JComboBox<String> cboVendorGroup;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtDate;
    private javax.swing.JTextField txtFilter;
    // End of variables declaration//GEN-END:variables
}
