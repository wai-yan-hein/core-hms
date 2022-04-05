/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.ui.common.PaymentHisTableModel;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class PaymentHistoryJDialog extends javax.swing.JDialog implements SelectionObserver{

    static Logger log = Logger.getLogger(PaymentHistoryJDialog.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private boolean bindStatus = false;
    private final PaymentHisTableModel model = new PaymentHisTableModel();

    /**
     * Creates new form PaymentHistoryJDialog
     */
    public PaymentHistoryJDialog() {
        super(Util1.getParent(), true);
        this.setAlwaysOnTop(true);
        initComponents();
        initCombo();
        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
        initTable();
    }

    private void initCombo() {
        Object tmpObj;
        if (Util1.getPropValue("system.login.default.value").equals("Y")) {
            tmpObj = Global.loginLocation;
        } else {
            tmpObj = Util1.getDefaultValue("Location");
        }
        if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
            BindingUtil.BindCombo(cboLocation, getLocationFilter());
            cboLocation.setSelectedItem(tmpObj);
            int index = cboLocation.getSelectedIndex();
            if (index == -1) {
                if (cboLocation.getItemCount() > 0) {
                    cboLocation.setSelectedIndex(0);
                }
            }
        } else {
            BindingUtil.BindCombo(cboLocation, dao.findAll("Location",
                    new Location(0, "All")));
            cboLocation.setSelectedIndex(0);
        }
        BindingUtil.BindCombo(cboUser, dao.findAll("Appuser",
                new Appuser("000", "All")));

        cboUser.setSelectedIndex(0);

        new ComBoBoxAutoComplete(cboLocation);
        new ComBoBoxAutoComplete(cboUser);

        cboPayOption.removeAllItems();
        cboPayOption.addItem("All");
        cboPayOption.addItem("Cash");
        cboPayOption.addItem("Credit Card");

        bindStatus = true;
    }

    private List getLocationFilter() {
        if (Util1.getPropValue("system.user.location.filter").equals("Y")) {
            return dao.findAllHSQL(
                    "select o from Location o where o.locationId in ("
                    + "select a.key.locationId from UserLocationMapping a "
                    + "where a.key.userId = '" + Global.loginUser.getUserId()
                    + "' and a.isAllowCusPayVou = true) order by o.locationName");
        } else {
            return dao.findAll("Location");
        }
    }

    private void search() {
        String strSql = getHSQL();

        try {
            dao.open();
            model.setListTraderPayHis(dao.findAllHSQL(strSql));
            dao.close();
            dao.closeStatment();
            Double total = model.getTotal();
            txtTotal.setValue(total);
        } catch (Exception ex) {
            log.error("search : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    private String getHSQL() {
        String strSql = "select distinct p from VTraderPayment p where p.payDate between '"
                + DateUtil.toDateTimeStrMYSQL(txtFrom.getText()) + "' and '"
                + DateUtil.toDateStrMYSQLEnd(txtTo.getText()) + "' and p.deleted = false ";

        if (txtCode.getText() != null && !txtCode.getText().isEmpty()) {
            strSql = strSql + " and p.traderId = '" + txtCode.getText() + "'";
        }

        if (((Location) cboLocation.getSelectedItem()).getLocationId() != 0) {
            Location location = (Location) cboLocation.getSelectedItem();
            strSql = strSql + " and p.location = " + location.getLocationId();
        }

        if (!((Appuser) cboUser.getSelectedItem()).getUserId().equals("000")) {
            Appuser user = (Appuser) cboUser.getSelectedItem();
            strSql = strSql + " and p.createdBy = '" + user.getUserId() + "'";
        }

        if (!((String) cboPayOption.getSelectedItem()).equals("All")) {
            String strPayOpt = (String) cboPayOption.getSelectedItem();
            strSql = strSql + " and p.payOpt = '" + strPayOpt + "'";
        }

        if (txtRemark.getText() != null && !txtRemark.getText().isEmpty()) {
            String strRemark = txtRemark.getText();
            strSql = strSql + " and p.remark = '" + strRemark + "'";
        }

        strSql = strSql + " and p.discm = 'C'";

        return strSql;
    }

    private void initTable() {
        search();
        tblPayList.getTableHeader().setFont(Global.lableFont);
        tblPayList.getColumnModel().getColumn(0).setPreferredWidth(20);
        tblPayList.getColumnModel().getColumn(1).setPreferredWidth(30);
        tblPayList.getColumnModel().getColumn(2).setPreferredWidth(100);
        tblPayList.getColumnModel().getColumn(3).setPreferredWidth(40);
    }

    private void getCustomerList() {
        //UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Customer List", dao);
        int locationId = -1;
        if (cboLocation.getSelectedItem() instanceof Location) {
            locationId = ((Location) cboLocation.getSelectedItem()).getLocationId();
        }

        TraderSearchDialog dialog = new TraderSearchDialog(this,
                "Customer List", locationId);
        dialog.setTitle("Customer List");
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    @Override
    public void selected(Object source, Object selectObj){
        switch (source.toString()) {
            case "CustomerList":
                model.removeAll();
                Trader searchTrader = (Trader) selectObj;
                txtCode.setText(searchTrader.getTraderId());
                txtName.setText(searchTrader.getTraderName());
                break;
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

        jPanel1 = new javax.swing.JPanel();
        txtFrom = new javax.swing.JFormattedTextField();
        txtTo = new javax.swing.JFormattedTextField();
        cboLocation = new javax.swing.JComboBox<>();
        cboUser = new javax.swing.JComboBox<>();
        cboPayOption = new javax.swing.JComboBox<>();
        butSearch = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        txtCode = new javax.swing.JTextField();
        txtName = new javax.swing.JTextField();
        txtRemark = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPayList = new javax.swing.JTable();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Payment History");
        setPreferredSize(new java.awt.Dimension(1024, 600));

        txtFrom.setBorder(javax.swing.BorderFactory.createTitledBorder("From"));
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        txtTo.setBorder(javax.swing.BorderFactory.createTitledBorder("To"));
        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        cboLocation.setFont(Global.textFont);
        cboLocation.setBorder(javax.swing.BorderFactory.createTitledBorder("Location"));

        cboUser.setFont(Global.textFont);
        cboUser.setBorder(javax.swing.BorderFactory.createTitledBorder("User"));

        cboPayOption.setFont(Global.textFont);
        cboPayOption.setBorder(javax.swing.BorderFactory.createTitledBorder("Pay Option"));

        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboLocation, 0, 105, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboUser, 0, 93, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboPayOption, 0, 95, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butSearch)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cboLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cboUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cboPayOption, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(butSearch))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboLocation, txtFrom, txtTo});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboPayOption, cboUser});

        txtCode.setBorder(javax.swing.BorderFactory.createTitledBorder("Code"));
        txtCode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCodeMouseClicked(evt);
            }
        });

        txtName.setBorder(javax.swing.BorderFactory.createTitledBorder("Name"));
        txtName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtNameMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtName)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        txtRemark.setBorder(javax.swing.BorderFactory.createTitledBorder("Remark"));

        tblPayList.setFont(Global.textFont);
        tblPayList.setModel(model);
        tblPayList.setRowHeight(23);
        jScrollPane1.setViewportView(tblPayList);

        txtTotal.setEditable(false);

        jLabel1.setText("Total : ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRemark)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtFromMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtFromMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtFrom.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtFromMouseClicked

    private void txtToMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtToMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtTo.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtToMouseClicked

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
    }//GEN-LAST:event_butSearchActionPerformed

    private void txtNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtNameMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtNameMouseClicked

    private void txtCodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCodeMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCodeMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JComboBox<String> cboLocation;
    private javax.swing.JComboBox<String> cboPayOption;
    private javax.swing.JComboBox<String> cboUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPayList;
    private javax.swing.JTextField txtCode;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JFormattedTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
