/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.util;

import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.PaymentVou;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.TraderPayHis;
import com.cv.app.pharmacy.ui.common.FillVouPaymentListTableModel;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class FillVouPaymentDialog extends javax.swing.JDialog implements SelectionObserver {

    static Logger log = Logger.getLogger(FillVouPaymentDialog.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private FillVouPaymentListTableModel tableModel = new FillVouPaymentListTableModel();
    private Trader paymentTrader;
    /**
     * Creates new form FillVouPaymentDialog
     */
    public FillVouPaymentDialog() {
        super(Util1.getParent(), true);
        initComponents();
        initTable();

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        int x = (screen.width - this.getWidth()) / 2;
        int y = (screen.height - this.getHeight()) / 2;

        this.setLocation(x, y);
        this.setVisible(true);
    }

    private void getCustomerList() {
        UtilDialog dialog = new UtilDialog(Util1.getParent(), true, this, "Customer List", dao, -1);
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "CustomerList":
                paymentTrader = (Trader) selectObj;
                txtCusCode.setText(paymentTrader.getTraderId());
                txtCusName.setText(paymentTrader.getTraderName());
                break;
        }
    }

    private void paymentInfo() {
        List<PaymentVou> listVou = new ArrayList();

        try {
            String strTrdOpt;

            if (paymentTrader != null) {

                if (paymentTrader.getTraderId().contains("SUP")) {
                    strTrdOpt = "SUP";
                } else {
                    strTrdOpt = "CUS";
                }

                String strCurrency = Util1.getPropValue("system.app.currency");
                ResultSet resultSet = dao.getPro("GET_PAYMENT_INFO",
                        paymentTrader.getTraderId(), strTrdOpt,
                        DateUtil.getTodayDateStrMYSQL(), strCurrency);
                String strOpDate = null;

                if (resultSet != null) {
                    while (resultSet.next()) {
                        strOpDate = resultSet.getString("VAR_OP_DATE");
                    }
                }

                if(resultSet != null){
                    resultSet.close();
                }
                
                String from = "1900-01-01";
                String to = "1900-01-01";
                
                if(!txtFrom.getText().trim().isEmpty() && !txtTo.getText().trim().isEmpty()){
                    from = DateUtil.toDateStrMYSQL(txtFrom.getText());
                    to = DateUtil.toDateStrMYSQL(txtTo.getText());
                }
                
                resultSet = dao.getPro("GET_UNPAID_VOU", paymentTrader.getTraderId(),
                        strTrdOpt, strOpDate, DateUtil.getTodayDateStrMYSQL(),
                        strCurrency, from, to);
                if (resultSet != null) {
                    while (resultSet.next()) {
                        listVou.add(new PaymentVou(resultSet.getString("VOU_NO"),
                                resultSet.getDouble("BAL"), resultSet.getString("VOU_TYPE"),
                                resultSet.getDate("tran_date"),
                                resultSet.getString("ref_no"),
                                resultSet.getDouble("BAL"),
                                resultSet.getDouble("vou_total"),
                                resultSet.getDouble("paid_amount"),
                                resultSet.getDouble("discount"),
                                resultSet.getDouble("balance")));
                    }
                }

                tableModel.setListVou(listVou);
            }
        } catch (Exception ex) {
            log.error("paymentInfo : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.closeStatment();
        }
    }

    private void save() {
        TraderPayHis traderPayHis = new TraderPayHis();
        
        try {
            traderPayHis.setPayDate(DateUtil.getTodayDateTime());
            traderPayHis.setLocation((Location)dao.find(Location.class, 1));
            traderPayHis.setTrader(paymentTrader);
            traderPayHis.setPayDt(traderPayHis.getPayDate());
            String remark = txtRemark.getText();
            if(remark.isEmpty()){
                traderPayHis.setRemark("Fill Vou Payment");
            }else{
                traderPayHis.setRemark(remark);
            }
            
            traderPayHis.setPaidAmtC(0.0);
            String appCurr = Util1.getPropValue("system.app.currency");
            Currency curr = (Currency)dao.find(Currency.class, appCurr);
            traderPayHis.setCurrency(curr);
            traderPayHis.setExRate(1.0);
            traderPayHis.setPaidAmtP(0.0);
            traderPayHis.setListDetail(tableModel.getEntryVou());
            traderPayHis.setCreatedBy(Global.loginUser);
            traderPayHis.setPayOption("Cash");
            traderPayHis.setParentCurr(curr);
            
            dao.save(traderPayHis);
        } catch (Exception ex) {
            log.error("save : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
            clear();
        }
    }

    private void clear(){
        tableModel.clear();
        txtCusCode.setText(null);
        txtCusName.setText(null);
        txtFrom.setText(null);
        txtTo.setText(null);
    }
    
    private void initTable() {
        tblFillVouPayment.getColumnModel().getColumn(0).setPreferredWidth(15); //Date
        tblFillVouPayment.getColumnModel().getColumn(1).setPreferredWidth(40); //Vou no
        tblFillVouPayment.getColumnModel().getColumn(2).setPreferredWidth(10); //Ref No
        tblFillVouPayment.getColumnModel().getColumn(3).setPreferredWidth(15); //Vou-Ttl
        tblFillVouPayment.getColumnModel().getColumn(4).setPreferredWidth(10); //Vou-Paid
        tblFillVouPayment.getColumnModel().getColumn(5).setPreferredWidth(10); //Vou-Disc
        tblFillVouPayment.getColumnModel().getColumn(6).setPreferredWidth(15); //Vou-Balance
        tblFillVouPayment.getColumnModel().getColumn(7).setPreferredWidth(7); //Vou-Type
        tblFillVouPayment.getColumnModel().getColumn(8).setPreferredWidth(15); //Vou-Last Balance
        tblFillVouPayment.getColumnModel().getColumn(9).setPreferredWidth(10); //Paid
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
        txtCusCode = new javax.swing.JTextField();
        txtCusName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFillVouPayment = new javax.swing.JTable();
        butSave = new javax.swing.JButton();
        butClear = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtRemark = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        txtTo = new javax.swing.JFormattedTextField();
        butSearch = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtVouNo = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();
        txtVouDate = new javax.swing.JFormattedTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDetails = new javax.swing.JTable();
        txtTotal = new javax.swing.JFormattedTextField();
        jLabel7 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Fill Voucher Payment");

        jLabel1.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        jLabel1.setText("Customer : ");

        txtCusCode.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtCusCode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusCodeMouseClicked(evt);
            }
        });

        txtCusName.setEditable(false);
        txtCusName.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        txtCusName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusNameMouseClicked(evt);
            }
        });

        tblFillVouPayment.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblFillVouPayment.setModel(tableModel);
        tblFillVouPayment.setRowHeight(23);
        jScrollPane1.setViewportView(tblFillVouPayment);

        butSave.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSave.setText("Save");
        butSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSaveActionPerformed(evt);
            }
        });

        butClear.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        jLabel2.setText("Remark : ");

        txtRemark.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        jLabel3.setText("From :");

        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        jLabel4.setText("To :");

        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        butSearch.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        jLabel5.setText("Vou No :");

        txtVouNo.setEditable(false);
        txtVouNo.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        jLabel6.setText("Date :");

        txtVouDate.setEditable(false);
        txtVouDate.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        tblDetails.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        tblDetails.setModel(new javax.swing.table.DefaultTableModel(
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
        tblDetails.setRowHeight(23);
        jScrollPane2.setViewportView(tblDetails);

        txtTotal.setEditable(false);
        txtTotal.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Zawgyi-One", 0, 12)); // NOI18N
        jLabel7.setText("Total :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addComponent(jLabel5)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel6)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtVouDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtRemark)
                        .addGap(27, 27, 27)
                        .addComponent(butSave, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(butClear))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtCusName))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(butSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {butClear, butSave});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtFrom, txtTo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(butSearch))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtCusCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(butSave)
                    .addComponent(butClear)
                    .addComponent(jLabel2)
                    .addComponent(txtRemark, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCusCodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusCodeMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusCodeMouseClicked

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusNameMouseClicked

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void butSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSaveActionPerformed
        save();
    }//GEN-LAST:event_butSaveActionPerformed

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        paymentInfo();
    }//GEN-LAST:event_butSearchActionPerformed

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butSave;
    private javax.swing.JButton butSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tblDetails;
    private javax.swing.JTable tblFillVouPayment;
    private javax.swing.JTextField txtCusCode;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JTextField txtRemark;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JFormattedTextField txtTotal;
    private javax.swing.JFormattedTextField txtVouDate;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
