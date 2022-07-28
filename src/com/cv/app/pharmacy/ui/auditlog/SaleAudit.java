/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.auditlog;

import com.cv.app.common.Global;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Patient;
import com.cv.app.opd.ui.util.PatientSearch;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.ChargeType;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.ExpenseType;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.entity.PaymentType;
import com.cv.app.pharmacy.database.entity.SaleDetailHis;
import com.cv.app.pharmacy.database.entity.SaleExpense;
import com.cv.app.pharmacy.database.entity.SaleHis;
import com.cv.app.pharmacy.database.entity.SaleOutstand;
import com.cv.app.pharmacy.database.entity.SaleWarranty;
import com.cv.app.pharmacy.database.entity.Trader;
import com.cv.app.pharmacy.database.entity.VouStatus;
import com.cv.app.pharmacy.ui.util.UtilDialog;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.awt.BorderLayout;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class SaleAudit extends javax.swing.JPanel implements SelectionObserver {

    private final AbstractDataAccess dao = Global.dao;
    private final SaleAuditTableModel model = new SaleAuditTableModel();
    static Logger log = Logger.getLogger(SaleAudit.class.getName());
    private SaleHis sh;
    private final RestoreView rv = new RestoreView();
    
    /**
     * Creates new form SaleAudit
     */
    public SaleAudit() {
        initComponents();
        initTable();
        panelRestore.setLayout(new ScrollPaneLayout());
        panelRestore.setViewportView(rv);
        
        txtFrom.setText(DateUtil.getTodayDateStr());
        txtTo.setText(DateUtil.getTodayDateStr());
    }

    private String getSql() {
        String strSql = "select bsh.bk_sale_id, bsh.sale_inv_id, bsh.sale_date, bsh.balance, bsh.sale_exp_total, "
                + "bsh.paid_amount,bsh.tax_amt, bsh.tax_p, bsh.refund, sess.session_name, u.user_name bk_user, "
                + "bsh.bk_sale_date, bsh.vou_total\n"
                + "from bk_sale_his bsh\n"
                + "left join appuser u on bsh.bk_user = u.user_id\n"
                + "left join session sess on bsh.session_id = sess.session_id\n"
                + " where date(bsh.sale_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'";
                

        if(!txtCusNo.getText().isEmpty()){
            strSql = strSql + " and bsh.cus_id = '" + txtCusNo.getText().trim() + "' ";
        }
        
        strSql = strSql + " order by bsh.sale_inv_id, bsh.bk_sale_date";
        
        return strSql;
    }

    private void search() {
        try {
            ResultSet rs = dao.execSQL(getSql());
            if (rs != null) {
                List<SaleAuditLog> listSAL = new ArrayList();
                while (rs.next()) {
                    SaleAuditLog sal = new SaleAuditLog();
                    sal.setBkId(rs.getLong("bk_sale_id"));
                    sal.setTranDate(rs.getTimestamp("bk_sale_date"));
                    sal.setTranUser(rs.getString("bk_user"));
                    sal.setVouDate(rs.getTimestamp("sale_date"));
                    sal.setVouNo(rs.getString("sale_inv_id"));
                    sal.setVouTotal(rs.getDouble("vou_total"));

                    listSAL.add(sal);
                }

                rs.close();
                model.setListSAL(listSAL);
            }
        } catch (Exception ex) {
            log.error("search : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void initTable() {
        tblAuditList.getTableHeader().setFont(Global.lableFont);
        tblAuditList.getColumnModel().getColumn(0).setPreferredWidth(80);//Vou No
        tblAuditList.getColumnModel().getColumn(1).setPreferredWidth(100);//Bak-Date
        tblAuditList.getColumnModel().getColumn(2).setPreferredWidth(50);//Vou-Date
        tblAuditList.getColumnModel().getColumn(3).setPreferredWidth(50);//Vou Total
        tblAuditList.getColumnModel().getColumn(4).setPreferredWidth(50);//User

        tblAuditList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAuditList.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    if (tblAuditList.getSelectedRow() < model.getRowCount()) {
                        int selRow = tblAuditList.convertRowIndexToModel(tblAuditList.getSelectedRow());
                        SaleAuditLog sal = model.getSaleAuditLog(selRow);
                        //log.info("valueChanged : " + e.getValueIsAdjusting() + " - " + sal.getBkId());
                        if (sal != null) {
                            getSaleData(sal);
                            rv.selected("SaleVouList", sh);
                        } else {
                            rv.newForm();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void selected(Object source, Object selectObj) {
        if (selectObj != null) {
            switch (source.toString()) {
                case "PatientSearch":
                    Patient pt = (Patient) selectObj;
                    txtCusNo.setText(pt.getRegNo());
                    txtCusName.setText(pt.getPatientName());
                    break;
                case "CustomerList":
                    Trader trd = (Trader) selectObj;
                    txtCusNo.setText(trd.getTraderId());
                    txtCusName.setText(trd.getTraderName());
                    break;
            }
        }
    }

    private void getCustomerList() {
        switch (Util1.getPropValue("system.app.usage.type")) {
            case "Hospital":
            case "School":
                PatientSearch ptDialog = new PatientSearch(dao, this);
                break;
            /*case "School":
                MarchantSearch dialog = new MarchantSearch(dao, this);
                break;*/
            default:
                UtilDialog dialog = new UtilDialog(Util1.getParent(), true, 
                        this, "Customer List", dao, -1);
                break;
        }
    }

    private void getSaleData(SaleAuditLog sal) {
        try {
            ResultSet rs = dao.execSQL("select * from bk_sale_his where bk_sale_id = " + sal.getBkId());
            if (rs != null) {
                rs.next();
                //Vou Info
                sh = new SaleHis();
                sh.setAdmissionNo(rs.getString("admission_no"));
                sh.setBalance(rs.getDouble("balance"));
                Appuser usr = (Appuser) dao.find(Appuser.class, rs.getString("created_by"));
                sh.setCreatedBy(usr);
                sh.setCreatedDate(rs.getTimestamp("created_date"));
                Currency curr = (Currency) dao.find(Currency.class, rs.getString("currency_id"));
                sh.setCurrencyId(curr);
                if(rs.getString("cus_id") != null){
                    Trader trd = (Trader) dao.find(Trader.class, rs.getString("cus_id"));
                    sh.setCustomerId(trd);
                }
                sh.setDeleted(Boolean.FALSE);
                sh.setDiscP(rs.getDouble("disc_p"));
                sh.setDiscount(rs.getDouble("discount"));
                if(rs.getString("doctor_id") != null){
                    Doctor dr = (Doctor) dao.find(Doctor.class, rs.getString("doctor_id"));
                    sh.setDoctor(dr);
                }
                sh.setDueDate(rs.getDate("due_date"));
                sh.setExRateP(rs.getDouble("exchange_rate_p"));
                sh.setExpenseTotal(rs.getDouble("sale_exp_total"));
                Location loc = (Location) dao.find(Location.class, rs.getInt("location_id"));
                sh.setLocationId(loc);
                sh.setMigId(rs.getString("mig_id"));
                sh.setPaid(rs.getDouble("paid_amount"));
                Currency pcurr = (Currency) dao.find(Currency.class, rs.getString("paid_currency"));
                sh.setPaidCurrency(pcurr);
                sh.setPaidCurrencyAmt(rs.getDouble("paid_currency_amt"));
                sh.setPaidCurrencyExRate(rs.getDouble("paid_currency_ex_rate"));
                if(rs.getString("reg_no") != null){
                    Patient pat = (Patient) dao.find(Patient.class, rs.getString("reg_no"));
                    sh.setPatientId(pat);
                }
                sh.setPayAmt(rs.getDouble("pay_amt"));
                PaymentType pt = (PaymentType) dao.find(PaymentType.class, rs.getInt("payment_type_id"));
                sh.setPaymentTypeId(pt);
                sh.setRefund(rs.getDouble("refund"));
                sh.setRegNo(rs.getString("stu_reg_no"));
                sh.setRemark(rs.getString("remark"));
                sh.setRemark1(rs.getString("remark1"));
                sh.setSaleDate(rs.getTimestamp("sale_date"));
                sh.setSaleInvId(rs.getString("sale_inv_id"));
                sh.setSession(rs.getInt("session_id"));
                sh.setStuName(rs.getString("stu_name"));
                sh.setStuNo(rs.getString("stu_no"));
                sh.setTaxAmt(rs.getDouble("tax_amt"));
                sh.setTaxP(rs.getDouble("tax_p"));
                sh.setTtlExpenseIn(rs.getDouble("sale_exp_total_in"));
                if(rs.getString("updated_by") != null){
                    Appuser uusr = (Appuser) dao.find(Appuser.class, rs.getString("updated_by"));
                    sh.setUpdatedBy(uusr);
                }
                sh.setUpdatedDate(rs.getTimestamp("updated_date"));
                VouStatus vs = (VouStatus) dao.find(VouStatus.class, rs.getInt("vou_status"));
                sh.setVouStatus(vs);
                sh.setVouTotal(rs.getDouble("vou_total"));

                //Expense
                ResultSet rsExpense = dao.execSQL(
                        "select * from bk_sale_expense where bk_sale_id = " + sal.getBkId()
                        + " order by unique_id"
                );
                if (rsExpense != null) {
                    List<SaleExpense> listSE = new ArrayList();
                    while (rsExpense.next()) {
                        SaleExpense se = new SaleExpense();
                        se.setCreatedDate(rsExpense.getTimestamp("created_date"));
                        se.setExpAmount(rsExpense.getDouble("expense_amt"));
                        ExpenseType et = (ExpenseType) dao.find(ExpenseType.class, rsExpense.getInt("expense_type"));
                        se.setExpType(et);
                        se.setExpenseDate(rsExpense.getDate("expense_date"));
                        se.setExpenseIn(rsExpense.getDouble("expense_amt_in"));
                        se.setUniqueId(rsExpense.getInt("unique_id"));

                        listSE.add(se);
                    }

                    if (!listSE.isEmpty()) {
                        sh.setExpense(listSE);
                    }

                    rsExpense.close();
                }

                //Outstanding
                ResultSet rsOuts = dao.execSQL(
                        "select * from bk_sale_outstanding where bk_sale_id = " + sal.getBkId()
                        + " order by outstanding_id"
                );
                if (rsOuts != null) {
                    List<SaleOutstand> listSO = new ArrayList();
                    while (rsOuts.next()) {
                        SaleOutstand so = new SaleOutstand();
                        so.setExpDate(rsOuts.getDate("exp_date"));
                        Medicine med = (Medicine) dao.find(Medicine.class, rsOuts.getString("item_id"));
                        so.setItemId(med);
                        so.setOutsOption(rsOuts.getString("outs_option"));
                        so.setOutsQtySmall(rsOuts.getFloat("outs_qty_small"));
                        so.setQtyStr(rsOuts.getString("qty_str"));

                        listSO.add(so);
                    }

                    if (!listSO.isEmpty()) {
                        sh.setListOuts(listSO);
                    }

                    rsOuts.close();
                }

                //Sale Detail
                ResultSet rsSD = dao.execSQL(
                        "select * from bk_sale_detail_his where bk_sale_id = " + sal.getBkId()
                        + " order by unique_id"
                );
                if (rsSD != null) {
                    List<SaleDetailHis> listSDH = new ArrayList();
                    while (rsSD.next()) {
                        SaleDetailHis sdh = new SaleDetailHis();
                        sdh.setAmount(rsSD.getDouble("sale_amount"));
                        ChargeType ct = (ChargeType) dao.find(ChargeType.class, rsSD.getInt("charge_type"));
                        sdh.setChargeId(ct);
                        sdh.setDiscount(rsSD.getDouble("item_discount"));
                        sdh.setExpireDate(rsSD.getDate("expire_date"));
                        sdh.setFocQty(rsSD.getFloat("foc_qty"));
                        sdh.setFocSmallestQty(rsSD.getFloat("foc_smallest_qty"));
                        if(rsSD.getString("foc_unit") != null){
                            ItemUnit iu = (ItemUnit) dao.find(ItemUnit.class, rsSD.getString("foc_unit"));
                            sdh.setFocUnit(iu);
                        }
                        sdh.setItemDiscP(rsSD.getDouble("item_discount_p"));
                        Location itemLoc = (Location) dao.find(Location.class, rsSD.getInt("location_id"));
                        sdh.setLocation(itemLoc);
                        Medicine med = (Medicine) dao.find(Medicine.class, rsSD.getString("med_id"));
                        sdh.setMedId(med);
                        sdh.setPrice(rsSD.getDouble("sale_price"));
                        sdh.setQuantity(rsSD.getFloat("sale_qty"));
                        sdh.setSaleAmtP(rsSD.getDouble("sale_amount_p"));
                        sdh.setSalePriceP(rsSD.getDouble("sale_price_p"));
                        sdh.setSaleSmallestQty(rsSD.getFloat("sale_smallest_qty"));
                        sdh.setUniqueId(rsSD.getInt("unique_id"));
                        ItemUnit siu = (ItemUnit) dao.find(ItemUnit.class, rsSD.getString("item_unit"));
                        sdh.setUnitId(siu);

                        listSDH.add(sdh);
                    }

                    if (!listSDH.isEmpty()) {
                        sh.setSaleDetailHis(listSDH);
                    }

                    rsSD.close();
                }

                //Warranty
                ResultSet rsW = dao.execSQL(
                        "select * from bk_sale_warranty where bk_sale_id = " + sal.getBkId()
                        + " order by warranty_id"
                );
                if (rsW != null) {
                    List<SaleWarranty> listSW = new ArrayList();
                    while (rsW.next()) {
                        SaleWarranty sw = new SaleWarranty();
                        sw.setEndDate(rsW.getDate("end_date"));
                        Medicine med = (Medicine) dao.find(Medicine.class, rsW.getString("item_id"));
                        sw.setItem(med);
                        sw.setSerialNo(rsW.getString("serial_no"));
                        sw.setStartDate(rsW.getDate("start_date"));
                        sw.setWarranty(rsW.getString("warranty"));

                        listSW.add(sw);
                    }

                    if (!listSW.isEmpty()) {
                        sh.setWarrandy(listSW);
                    }

                    rsW.close();
                }

                rs.close();
            }
        } catch (Exception ex) {
            log.error("getSaleData : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void restore() {
        try {
            GenVouNoImpl vouEngine = new GenVouNoImpl(dao, "Sale",
                    DateUtil.getPeriod(DateUtil.toDateStr(sh.getSaleDate())));
            String vouNo = vouEngine.getVouNo1();
            sh.setSaleInvId(vouNo);
            dao.save(ui);
            vouEngine.updateVouNo();

            //Clear
        } catch (Exception ex) {
            log.error("restore : " + ex.getMessage());
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
        tblAuditList = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtFrom = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTo = new javax.swing.JFormattedTextField();
        butSearch = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtCusNo = new javax.swing.JTextField();
        txtCusName = new javax.swing.JTextField();
        panelRestore = new javax.swing.JScrollPane();

        tblAuditList.setFont(Global.textFont);
        tblAuditList.setModel(model);
        tblAuditList.setRowHeight(23);
        jScrollPane1.setViewportView(tblAuditList);

        jLabel1.setText("From ");

        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });

        jLabel2.setText("To ");

        txtTo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtToMouseClicked(evt);
            }
        });

        butSearch.setText("Search");
        butSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butSearchActionPerformed(evt);
            }
        });

        jLabel3.setText("Cus ");

        txtCusNo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusNoMouseClicked(evt);
            }
        });

        txtCusName.setEditable(false);
        txtCusName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtCusNameMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCusNo, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCusName))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(butSearch)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelRestore, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel3});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(butSearch))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtCusNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCusName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE))
                    .addComponent(panelRestore))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
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

    private void txtCusNoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNoMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusNoMouseClicked

    private void txtCusNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtCusNameMouseClicked
        if (evt.getClickCount() == 2) {
            getCustomerList();
        }
    }//GEN-LAST:event_txtCusNameMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butSearch;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane panelRestore;
    private javax.swing.JTable tblAuditList;
    private javax.swing.JTextField txtCusName;
    private javax.swing.JTextField txtCusNo;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JFormattedTextField txtTo;
    // End of variables declaration//GEN-END:variables
}
