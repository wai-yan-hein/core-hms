/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.entry;

import com.cv.app.common.ComBoBoxAutoComplete;
import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
//import com.cv.app.common.SelectionObserver;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.Customer;
import com.cv.app.pharmacy.database.entity.PaymentVou;
import com.cv.app.pharmacy.database.entity.TraderPayHis;
import com.cv.app.pharmacy.database.helper.VoucherPayment;
import com.cv.app.pharmacy.ui.common.SPaymentEntryTableModel;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author lenovo
 */
public class SupplierPayment extends javax.swing.JPanel implements SelectionObserver {

    static Logger log = Logger.getLogger(SupplierPayment.class.getName());
    private final SPaymentEntryTableModel tblPaymentEntry = new SPaymentEntryTableModel();
    private final TableRowSorter<TableModel> sorter;
    private final StartWithRowFilter swrf;
    private final AbstractDataAccess dao = Global.dao;
    private int mouseClick = 2;

    //private final TableRowSorter<TableModel> tblTraderSorter;
    /**
     * Creates new form CustomerPayment1
     */
    public SupplierPayment() {
        initComponents();
        initTableCusPay();
        txtPayDate.setText(DateUtil.getTodayDateStr());
        try {
            BindingUtil.BindCombo(cboAccount,
                    dao.findAllHSQL("select o from TraderPayAccount o where o.status = true"));
            new ComBoBoxAutoComplete(cboAccount);
        } catch (Exception ex) {
            log.error("SupplierPayment : " + ex.getMessage());
        } finally {
            dao.close();
        }
        tblPaymentEntry.setCboPayment(cboAccount);
        swrf = new StartWithRowFilter(txtFilter);
        sorter = new TableRowSorter(tblVouList.getModel());
        tblVouList.setRowSorter(sorter);

        String propValue = Util1.getPropValue("system.date.mouse.click");
        if (propValue != null) {
            if (!propValue.equals("-")) {
                if (!propValue.isEmpty()) {
                    int tmpValue = NumberUtil.NZeroInt(propValue);
                    if (tmpValue != 0) {
                        mouseClick = tmpValue;
                    }
                }
            }
        }
    }

    private void initTableCusPay() {

        try {
            String strSql = "select a.*, if(a.ttl_overdue<0,0,a.ttl_overdue) as ttl_overdue1 from (\n"
                    + "select sh.pur_date, pur_inv_id vou_no, sh.cus_id, t.trader_name, 'PURCHASE' vou_type, sh.currency,\n"
                    + "       sh.due_date, sh.remark ref_no, (sh.vou_total-ifnull(sh.discount,0)) as vou_total, (sh.paid+ifnull(pah.pay_amt,0)) as ttl_paid, "
                    + "sh.discount, sh.balance,\n"
                    + "	   sum(ifnull(balance,0))-(ifnull(pah.pay_amt,0)) bal, \n"
                    + "	   if(ifnull(sh.due_date,'-')='-',0,if(DATEDIFF(sysdate(),sh.due_date)<0,0,DATEDIFF(sysdate(),sh.due_date))) as ttl_overdue, \n"
                    + "    t.stu_no \n"
                    + "from pur_his sh\n"
                    + "left join trader t on sh.cus_id = t.trader_id\n"
                    + "left join (select pv.vou_no, sum(ifnull(pv.vou_paid,0)+ifnull(pv.discount,0)) pay_amt, pv.vou_type\n"
                    + "			   from payment_his ph, pay_his_join phj, payment_vou pv\n"
                    + "			  where ph.payment_id = phj.payment_id and phj.tran_id = pv.tran_id\n"
                    + "				and ph.deleted = false\n"
                    + "				and pv.vou_type = 'PURCHASE'\n"
                    + "			  group by pv.vou_no, pv.vou_type) pah on sh.pur_inv_id = pah.vou_no\n"
                    + "where sh.deleted = false\n"
                    + "group by sh.pur_inv_id, sh.pur_date,sh.vou_total, sh.currency, sh.paid, sh.discount, sh.balance, t.stu_no) a\n"
                    + "where a.balance > 0 and a.bal > 0.9 order by a.pur_date, a.vou_no";
            ResultSet rs = dao.execSQL(strSql);

            List<VoucherPayment> listVP = null;
            if (rs != null) {
                listVP = new ArrayList();
                //double ttlBalance = 0.0;
                //double ttlDiscount = 0.0;
                //double ttlPaid = 0.0;
                while (rs.next()) {
                    //ttlBalance += rs.getDouble("bal");
                    //ttlDiscount += rs.getDouble("discount");
                    //ttlPaid += rs.getDouble("ttl_paid");

                    listVP.add(new VoucherPayment(
                            rs.getDate("pur_date"),
                            rs.getString("vou_no"),
                            rs.getString("cus_id"),
                            rs.getString("trader_name"),
                            rs.getString("vou_type"),
                            rs.getDate("due_date"),
                            rs.getDouble("vou_total"),
                            rs.getDouble("discount"),
                            rs.getDouble("ttl_paid"),
                            rs.getDouble("bal"),
                            rs.getInt("ttl_overdue1"),
                            rs.getString("currency"),
                            DateUtil.toDate(DateUtil.getTodayDateStr()),
                            rs.getString("stu_no")
                    ));
                }
                //txtTotalBalance.setValue(ttlBalance);
                //txtTotalDiscount.setValue(ttlDiscount);
                //txtTotalPaid.setValue(ttlPaid);
            }
            tblPaymentEntry.setListVP(listVP);

        } catch (Exception ex) {
            log.error("initTable tblOTProcedure : " + ex.getMessage());
        } finally {
            dao.close();
        }
        tblVouList.getTableHeader().setFont(Global.lableFont);

        tblVouList.getColumnModel().getColumn(0).setPreferredWidth(30);//Vou Date
        tblVouList.getColumnModel().getColumn(1).setPreferredWidth(70);//Vou No.
        tblVouList.getColumnModel().getColumn(2).setPreferredWidth(50);//Cus-No
        tblVouList.getColumnModel().getColumn(3).setPreferredWidth(250);//Cus-Name
        tblVouList.getColumnModel().getColumn(4).setPreferredWidth(30);//Due Date
        tblVouList.getColumnModel().getColumn(5).setPreferredWidth(15);//Ttl Overdue
        tblVouList.getColumnModel().getColumn(6).setPreferredWidth(30);//Currency
        tblVouList.getColumnModel().getColumn(7).setPreferredWidth(60);//Vou Total
        tblVouList.getColumnModel().getColumn(8).setPreferredWidth(40);//Ttl Paid
        tblVouList.getColumnModel().getColumn(9).setPreferredWidth(30);//Pay Date
        tblVouList.getColumnModel().getColumn(10).setPreferredWidth(40);//Paid
        tblVouList.getColumnModel().getColumn(11).setPreferredWidth(30);//Discount
        tblVouList.getColumnModel().getColumn(12).setPreferredWidth(80);//Vou Balance
        tblVouList.getColumnModel().getColumn(13).setPreferredWidth(15);//Full Paid

        tblVouList.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
        tblVouList.getColumnModel().getColumn(4).setCellRenderer(new TableDateFieldRenderer());
        calculateTotal();
    }

    private void calculateTotal() {
        Double totalBalance = 0.0;
        Double totalDiscount = 0.0;
        Double totalPayment = 0.0;

        for (int i = 0; i < tblVouList.getRowCount(); i++) {
            totalBalance += NumberUtil.NZero(tblVouList.getValueAt(i, 12));
            totalDiscount += NumberUtil.NZero(tblVouList.getValueAt(i, 11));
            totalPayment += NumberUtil.NZero(tblVouList.getValueAt(i, 10));
        }

        txtTotalBalance.setValue(totalBalance);
        txtTotalDiscount.setValue(totalDiscount);
        txtTotalPaid.setValue(totalPayment);
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
        txtFilter = new javax.swing.JTextField();
        chkOverdue = new javax.swing.JCheckBox();
        butRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVouList = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        txtTotalPaid = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTotalDiscount = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTotalBalance = new javax.swing.JFormattedTextField();
        cboAccount = new javax.swing.JComboBox<>();
        jLabel17 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtPayDate = new javax.swing.JFormattedTextField();

        jLabel1.setFont(Global.lableFont);
        jLabel1.setText("Filter");

        txtFilter.setFont(Global.textFont);
        txtFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFilterActionPerformed(evt);
            }
        });
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFilterKeyReleased(evt);
            }
        });

        chkOverdue.setText("Overdue");
        chkOverdue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkOverdueActionPerformed(evt);
            }
        });

        butRefresh.setText("Refresh");
        butRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRefreshActionPerformed(evt);
            }
        });

        tblVouList.setFont(Global.textFont);
        tblVouList.setModel(tblPaymentEntry);
        tblVouList.setRowHeight(23);
        jScrollPane1.setViewportView(tblVouList);

        jLabel4.setText("Total Paid : ");

        txtTotalPaid.setEditable(false);
        txtTotalPaid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setText("Total Discount : ");

        txtTotalDiscount.setEditable(false);
        txtTotalDiscount.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Total Balance : ");

        txtTotalBalance.setEditable(false);
        txtTotalBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        cboAccount.setFont(Global.textFont);

        jLabel17.setFont(Global.lableFont);
        jLabel17.setText("Pyament Type");

        jLabel10.setFont(Global.lableFont);
        jLabel10.setText("Pay Date ");

        txtPayDate.setEditable(false);
        txtPayDate.setFont(Global.textFont);
        txtPayDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtPayDateMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalPaid, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addComponent(txtPayDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboAccount, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkOverdue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTotalDiscount, txtTotalPaid});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cboAccount, txtPayDate});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkOverdue)
                    .addComponent(butRefresh)
                    .addComponent(jLabel17)
                    .addComponent(cboAccount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(txtPayDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtTotalDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTotalPaid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(jLabel3)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTotalBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
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
        calculateTotal();
    }//GEN-LAST:event_txtFilterKeyReleased

    private void chkOverdueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkOverdueActionPerformed
        sorter.setRowFilter(swrf);
        calculateTotal();
    }//GEN-LAST:event_chkOverdueActionPerformed

    private void butRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRefreshActionPerformed
        initTableCusPay();
        //butRefresh.setEnabled(false);
    }//GEN-LAST:event_butRefreshActionPerformed

    private void txtFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFilterActionPerformed

    private void txtPayDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtPayDateMouseClicked
        if (evt.getClickCount() > 1) {
            String strDate = DateUtil.getDateDialogStr();
            if (strDate != null) {
                txtPayDate.setText(strDate);
                List<VoucherPayment> payments = tblPaymentEntry.getListVP();
                if (!payments.isEmpty()) {
                    for (VoucherPayment p : payments) {
                        p.setPayDate(DateUtil.toDate(strDate));
                    }
                }
                tblPaymentEntry.fireTableDataChanged();
            }
        }
    }//GEN-LAST:event_txtPayDateMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butRefresh;
    private javax.swing.JComboBox<String> cboAccount;
    private javax.swing.JCheckBox chkOverdue;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblVouList;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JFormattedTextField txtPayDate;
    private javax.swing.JFormattedTextField txtTotalBalance;
    private javax.swing.JFormattedTextField txtTotalDiscount;
    private javax.swing.JFormattedTextField txtTotalPaid;
    // End of variables declaration//GEN-END:variables

    @Override
    public void selected(Object source, Object selectObj) {
        if (source != null) {
            switch (source.toString()) {
                case "CalculateTotal":
                    calculateTotal();
                    break;
                case "Paid":
                    if (selectObj != null) {
                        VoucherPayment record = (VoucherPayment) selectObj;
                        record.setRemark("Paid");
                        record.setPayDate(new Date());
                        record.setUserId(Global.loginUser.getUserId());
                        assignData();
                    }
                    break;
                case "Discount":
                    if (selectObj != null) {
                        VoucherPayment record = (VoucherPayment) selectObj;
                        record.setRemark("Discount");
                        record.setPayDate(new Date());
                        record.setUserId(Global.loginUser.getUserId());
                        assignData();
                    }
                    break;
                case "FullPaid":
                    if (selectObj != null) {
                        VoucherPayment record = (VoucherPayment) selectObj;
                        record.setRemark("FullPaid");
                        record.setPayDate(new Date());
                        record.setUserId(Global.loginUser.getUserId());
                        assignData();
                    }
                    break;
            }
        }
    }

    private void assignData() {
        try {
            TraderPayHis tph = new TraderPayHis();
            VoucherPayment vp = new VoucherPayment();
            tph.setPayDate(vp.getPayDate());
            List<Customer> cus = dao.findAllHSQL("select o from Customer o where o.traderId='" + vp.getTraderId() + "'");
            tph.setTrader(cus.get(0));
            tph.setRemark(vp.getRemark());
            tph.setPaidAmtC(vp.getCurrentPaid());
            tph.setDiscount(vp.getCurrentDiscount());
            String appCurr = Util1.getPropValue("system.app.currency");
            List<Currency> curr = dao.findAllHSQL("select o from Currency o where o.currencyCode='" + appCurr + "'");
            tph.setCurrency(curr.get(0));
            tph.setExRate(1.0);
            tph.setPaidAmtP(vp.getCurrentPaid());
            List<Appuser> user = dao.findAllHSQL("select o from Appuser o where  o.userId='" + vp.getUserId() + "'");
            tph.setCreatedBy(user.get(0));
            tph.setPayOption("Cash");
            tph.setParentCurr(curr.get(0));
            tph.setPayDt(vp.getPayDate());
            PaymentVou pv = new PaymentVou();
            pv.setBalance(vp.getVouBalance());
            pv.setVouNo(vp.getVouNo());
            pv.setVouPaid(vp.getCurrentPaid());
            pv.setBalance(vp.getVouBalance());
            pv.setVouDate(vp.getTranDate());
            pv.setDiscount(vp.getDiscount());
            pv.setVouType("Sale");
            List<PaymentVou> listPV = new ArrayList();
            listPV.add(pv);
            tph.setListDetail(listPV);

            dao.save(tph);
        } catch (Exception ex) {
            log.error("assignData : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

}
