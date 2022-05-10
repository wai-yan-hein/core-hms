/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.Global;
import com.cv.app.inpatient.database.healper.CurrPTBalance;
import com.cv.app.opd.ui.common.PatientBalanceCheckTableModel;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class PatientBalanceCheck extends javax.swing.JPanel {

    private final AbstractDataAccess dao = Global.dao;
    static Logger log = Logger.getLogger(PatientBalanceCheck.class.getName());
    private final PatientBalanceCheckTableModel pbcModel = new PatientBalanceCheckTableModel();
    /**
     * Creates new form PatientBalanceCheck
     */
    public PatientBalanceCheck() {
        initComponents();
        initCombo();
        txtDate.setText(DateUtil.getTodayDateStr());
        initTable();
    }

    private void getData() {
        String regNo = "-";
        String strDate = DateUtil.getTodayDateStrMYSQL();
        String userId = Global.loginUser.getUserId();
        String currency = getCurrencyId();
        List<CurrPTBalance> listBal;
        Double total = 0.0;

        try {
            dao.execProc("patient_balance", strDate, currency, userId, regNo);
            String strSQLs = "update tmp_bill_payment tbp, (\n"
                    + "select dh.patient_id, max(dh.admission_no) admission_no, max(dh.dc_date) dc_date\n"
                    + "from dc_his dh\n"
                    + "where dh.dc_status is not null and date(dh.dc_date) between '"
                    + strDate + "' and '" + strDate + "'\n"
                    + "and dh.deleted = false\n"
                    + "group by dh.patient_id) dc\n"
                    + "set tbp.dc_date = dc.dc_date\n"
                    + "where tbp.user_id = '" + userId
                    + "' and tbp.reg_no = dc.patient_id and tbp.admission_no is null";
            dao.execSql(strSQLs);
            strSQLs = "select a.reg_no, a.currency, round(sum(amt),0) balance,a.patient_name, a.admission_no ams_no, tbp.dc_date\n"
                    + "    from (\n"
                    + "	  select po.reg_no, po1.patient_name, po1.admission_no, po.currency, sum(ifnull(op_amount,0)) amt,\n"
                    + "	         po1.dc_date\n"
                    + "	    from patient_op po, tmp_bill_payment po1\n"
                    + "	   where date(po.op_date) <= $P{to_date} and po1.user_id = $P{user_id}\n"
                    + "	     and (po.currency = $P{currency} or '-' = $P{currency})\n"
                    + "	     and po.reg_no = po1.reg_no and po.bill_type = po1.bill_type and po.currency = po1.currency\n"
                    + "	     and po.op_date = po1.op_date\n"
                    + "	   group by po.reg_no, po1.patient_name, po1.admission_no, po.currency, po1.dc_date\n"
                    + "	   union all\n"
                    + "	  select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, sum(a.amt), tbp.dc_date\n"
                    + "              from tmp_bill_payment tbp join (\n"
                    + "                   select sh.reg_no, sh.currency_id,date(sh.sale_date) sale_date, sum(ifnull(sh.balance,0)) amt\n"
                    + "                     from sale_his sh\n"
                    + "                    where deleted = false and sh.reg_no is not null\n"
                    + "                    group by sh.reg_no,sh.currency_id,date(sh.sale_date)) a on tbp.reg_no = a.reg_no\n"
                    + "               and tbp.currency = a.currency_id and a.sale_date between tbp.op_date and $P{to_date}\n"
                    + "             where tbp.user_id = $P{user_id} and (tbp.currency = $P{currency} or '-' = $P{currency})\n"
                    + "             group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n"
                    + "	   union all\n"
                    + "	  select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, sum(ifnull(oh.amt,0)) amt, tbp.dc_date\n"
                    + "	    from tmp_bill_payment tbp join (\n"
                    + "                    select patient_id, currency_id, date(opd_date) opd_date, sum(ifnull(vou_balance,0)) amt\n"
                    + "                      from opd_his\n"
                    + "                     where deleted = false and patient_id is not null\n"
                    + "                     group by patient_id, currency_id, date(opd_date)) oh on tbp.reg_no = oh.patient_id\n"
                    + "                and tbp.currency = oh.currency_id and date(oh.opd_date) between tbp.op_date and $P{to_date}\n"
                    + "              where tbp.user_id = $P{user_id} and (tbp.currency = $P{currency} or '-' = $P{currency})\n"
                    + "              group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n"
                    + "	    union all\n"
                    + "	   select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, sum(ifnull(oh.amt,0)) amt, tbp.dc_date\n"
                    + "	     from tmp_bill_payment tbp join (\n"
                    + "	          select patient_id, currency_id, date(ot_date) ot_date, sum(ifnull(vou_balance,0)) amt\n"
                    + "	            from ot_his\n"
                    + "	           where deleted = false and patient_id is not null\n"
                    + "		 group by patient_id, currency_id, date(ot_date)) oh on tbp.reg_no = oh.patient_id\n"
                    + "                and tbp.currency = oh.currency_id\n"
                    + "	      and date(oh.ot_date) between tbp.op_date and $P{to_date}\n"
                    + "	    where (tbp.currency = $P{currency} or '-' = $P{currency}) and tbp.user_id = $P{user_id}\n"
                    + "	    group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n"
                    + "	    union all\n"
                    + "	   select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency,sum(ifnull(oh.amt,0)) amt, tbp.dc_date\n"
                    + "	     from tmp_bill_payment tbp join (\n"
                    + "                    select patient_id, currency_id, date(dc_date) dc_date, sum(ifnull(vou_balance,0)) amt\n"
                    + "		 from dc_his\n"
                    + "		where deleted = false and patient_id is not null\n"
                    + "		group by patient_id, currency_id, date(dc_date)) oh on tbp.reg_no = oh.patient_id\n"
                    + "                and tbp.currency = oh.currency_id and date(oh.dc_date) between tbp.op_date and $P{to_date}\n"
                    + "	    where (tbp.currency = $P{currency} or '-' = $P{currency}) and tbp.user_id = $P{user_id}\n"
                    + "	    group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n"
                    + "	    union all\n"
                    + "	   select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, (sum(ifnull(rih.amt,0))*-1) amt,\n"
                    + "                     tbp.dc_date\n"
                    + "	     from tmp_bill_payment tbp join (\n"
                    + "                    select reg_no, currency, date(ret_in_date) ret_in_date, sum(ifnull(balance,0)) amt\n"
                    + "		 from ret_in_his\n"
                    + "		where deleted = false and reg_no is not null\n"
                    + "	          group by reg_no, currency, date(ret_in_date)) rih on tbp.reg_no = rih.reg_no\n"
                    + "                and tbp.currency = rih.currency and date(rih.ret_in_date) between tbp.op_date and $P{to_date}\n"
                    + "	    where tbp.user_id = $P{user_id} and (tbp.currency = $P{currency} or '-' = $P{currency})\n"
                    + "	    group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n"
                    + "	    union all\n"
                    + "	   select tbp.reg_no, tbp.patient_name,tbp.admission_no, tbp.currency,\n"
                    + "                    (sum(ifnull(opbp.amt,0))*-1) amt, tbp.dc_date\n"
                    + "	     from tmp_bill_payment tbp join (\n"
                    + "                     select reg_no, currency_id, date(pay_date) pay_date, (sum(ifnull(pay_amt,0))) amt\n"
                    + "		  from opd_patient_bill_payment\n"
                    + "		 where reg_no is not null\n"
                    + "		 group by reg_no, currency_id, date(pay_date)) opbp on tbp.reg_no = opbp.reg_no\n"
                    + "                 and tbp.currency = opbp.currency_id and date(opbp.pay_date) between tbp.op_date and $P{to_date}\n"
                    + "	     where tbp.user_id = $P{user_id} and (tbp.currency = $P{currency} or '-' = $P{currency})\n"
                    + "	     group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date) a\n"
                    + "    join (select * from tmp_bill_payment where (currency = $P{currency} or '-' = $P{currency})) tbp on a.reg_no = tbp.reg_no and tbp.user_id = $P{user_id}\n"
                    + "   where (a.reg_no = $P{reg_no} or '-' = $P{reg_no})\n"
                    + "   group by a.reg_no, a.currency, a.patient_name, tbp.dc_date\n"
                    + "   having round(sum(a.amt)) <> 0\n"
                    + "order by a.patient_name";
            strSQLs = strSQLs.replace("$P{to_date}", "'" + strDate + "'")
                    .replace("$P{currency}", "'" + currency + "'")
                    .replace("$P{reg_no}", "'" + regNo + "'")
                    .replace("$P{user_id}", "'" + userId + "'");
            log.info("SQL : " + strSQLs);
            ResultSet rs = dao.execSQL(strSQLs);
            if (rs != null) {
                listBal = new ArrayList();
                total = 0.0;
                while (rs.next()) {
                    CurrPTBalance cpb = new CurrPTBalance(
                            rs.getString("reg_no"),
                            rs.getString("ams_no"),
                            rs.getString("patient_name"),
                            rs.getDouble("balance")
                    );
                    total += cpb.getBalance();
                    listBal.add(cpb);
                }
                rs.close();
                pbcModel.setList(listBal);
                txtTtlBalance.setValue(total);
            }else{
                txtTtlBalance.setValue(0);
            }
            txtDiffAmt.setValue(0);
        } catch (Exception ex) {
            log.error("getData : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private String getCurrencyId() {
        String currencyId;

        if (cboCurrency.getSelectedItem() instanceof Currency) {
            Currency curr = (Currency) cboCurrency.getSelectedItem();
            currencyId = curr.getCurrencyCode();
        } else {
            currencyId = "-";
        }

        return currencyId;
    }

    private void initCombo() {
        BindingUtil.BindComboFilter(cboCurrency, dao.findAll("Currency"));
    }

    private void initTable(){
        tblBalance.getColumnModel().getColumn(0).setPreferredWidth(30);//Reg No.
        tblBalance.getColumnModel().getColumn(1).setPreferredWidth(30);//Adm No
        tblBalance.getColumnModel().getColumn(2).setPreferredWidth(150);//Patient
        tblBalance.getColumnModel().getColumn(3).setPreferredWidth(10);//S
        tblBalance.getColumnModel().getColumn(4).setPreferredWidth(50);//Balance
        tblBalance.getColumnModel().getColumn(5).setPreferredWidth(50);//C-Balance
    }
    
    private String getCurrency(){
        Object obj = cboCurrency.getSelectedItem();
        if(obj instanceof Currency){
            Currency curr = (Currency)obj;
            return curr.getCurrencyCode();
        }else{
            return "MMK";
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

        txtDate = new javax.swing.JFormattedTextField();
        butGetBalance = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBalance = new javax.swing.JTable();
        cboCurrency = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        txtChkTotal = new javax.swing.JFormattedTextField();
        txtFilter = new javax.swing.JTextField();
        butCheck = new javax.swing.JButton();
        txtTtlBalance = new javax.swing.JFormattedTextField();
        txtDiffAmt = new javax.swing.JFormattedTextField();

        txtDate.setBorder(javax.swing.BorderFactory.createTitledBorder("Date"));

        butGetBalance.setText("Get Balance");
        butGetBalance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butGetBalanceActionPerformed(evt);
            }
        });

        tblBalance.setModel(pbcModel);
        tblBalance.setRowHeight(23);
        jScrollPane1.setViewportView(tblBalance);

        cboCurrency.setBorder(javax.swing.BorderFactory.createTitledBorder("Currency"));
        cboCurrency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCurrencyActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 305, Short.MAX_VALUE)
        );

        txtChkTotal.setEditable(false);
        txtChkTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));

        butCheck.setText("Check");
        butCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCheckActionPerformed(evt);
            }
        });

        txtTtlBalance.setEditable(false);
        txtTtlBalance.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtDiffAmt.setEditable(false);
        txtDiffAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtDiffAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtChkTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butGetBalance)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butCheck))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtChkTotal, txtDiffAmt, txtTtlBalance});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butGetBalance)
                    .addComponent(cboCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butCheck))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtChkTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTtlBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDiffAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboCurrency, txtDate});

    }// </editor-fold>//GEN-END:initComponents

    private void butGetBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butGetBalanceActionPerformed
        getData();
    }//GEN-LAST:event_butGetBalanceActionPerformed

    private void butCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCheckActionPerformed
        pbcModel.setTranDate(txtDate.getText());
        pbcModel.setCurrency(getCurrency());
        pbcModel.checkBalance();
        double chkTotal = pbcModel.getCheckTotal();
        txtChkTotal.setValue(chkTotal);
        double ttlBalance = NumberUtil.NZero(txtTtlBalance.getValue());
        double diffAmt = ttlBalance - chkTotal;
        txtDiffAmt.setValue(diffAmt);
    }//GEN-LAST:event_butCheckActionPerformed

    private void cboCurrencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCurrencyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboCurrencyActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butCheck;
    private javax.swing.JButton butGetBalance;
    private javax.swing.JComboBox<String> cboCurrency;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblBalance;
    private javax.swing.JFormattedTextField txtChkTotal;
    private javax.swing.JFormattedTextField txtDate;
    private javax.swing.JFormattedTextField txtDiffAmt;
    private javax.swing.JTextField txtFilter;
    private javax.swing.JFormattedTextField txtTtlBalance;
    // End of variables declaration//GEN-END:variables
}
