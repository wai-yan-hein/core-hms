/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.entry;

import com.cv.app.inpatient.ui.common.CurrPTBalanceTableModel;
import com.cv.app.inpatient.ui.common.CurrPTBalanceTranTableModel;
import com.cv.app.common.Global;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.Util1;
import com.cv.app.util.DateUtil;
import com.cv.app.inpatient.database.healper.CurrPTBalanceTran;
import com.cv.app.inpatient.ui.common.GroupTotalTableModel;
import com.cv.app.pharmacy.database.helper.SessionTtl;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.NumberUtil;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author ACER
 */
public class DCPatientBalance extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(DCPatientBalance.class.getName());
    private final CurrPTBalanceTableModel ptBalTableModel = new CurrPTBalanceTableModel();
    private final CurrPTBalanceTranTableModel ptBalTranTableModel = new CurrPTBalanceTranTableModel();
    private final AbstractDataAccess dao = Global.dao;
    private final GroupTotalTableModel gtTablemodel = new GroupTotalTableModel();

    /**
     * Creates new form DCPatientBalance
     */
    public DCPatientBalance() {
        initComponents();
        initTable();
    }

    private void initTable() {
        ptBalTableModel.getData();
        txtTtlBal.setValue(ptBalTableModel.getTotal());
        tblPtList.getTableHeader().setFont(Global.lableFont);
        tblPtList.getColumnModel().getColumn(0).setPreferredWidth(20); //Reg No
        tblPtList.getColumnModel().getColumn(1).setPreferredWidth(20); //Adm No
        tblPtList.getColumnModel().getColumn(2).setPreferredWidth(150); //Patient
        tblPtList.getColumnModel().getColumn(3).setPreferredWidth(50); //Balance
        tblTran.getColumnModel().getColumn(1).setCellRenderer(new TableDateFieldRenderer());
    }

    private void getBillInfo() {
        String admissionNo = txtAdmNo.getText().trim();
        if (admissionNo.isEmpty()) {

        } else {
            try {
                List<Ams> listAms = dao.findAllHSQL(
                        "select o from Ams o where o.key.amsNo = '" + txtAdmNo.getText().trim() + "'"
                );

                if (listAms != null) {
                    if (!listAms.isEmpty()) {
                        Ams ams = listAms.get(0);
                        txtRegNo.setText(ams.getKey().getRegister().getRegNo());
                        lblPtName.setText(ams.getPatientName());
                        txtAdmNo.setText(ams.getKey().getAmsNo());
                        String admitDate = DateUtil.toDateTimeStr(ams.getAmsDate(), "yyyy-MM-dd");
                        String regNo = txtRegNo.getText();
                        String admNo = txtAdmNo.getText().trim();
                        String tranDate = DateUtil.getTodayDateStrMYSQL();
                        String strSql = "select a.group_name, a.tran_option, a.tran_date, a.vou_no, a.item_name, a.qty, a.price, a.amount from (\n"
                                + "select 'DC' as tran_option, date(dh.dc_date) as tran_date, dh.dc_inv_id as vou_no,\n"
                                + "if(ifnull(ddf.doctor_id,'')='',ins.service_name, concat(ins.service_name, ' (', dr.doctor_name, ')')) as item_name, \n"
                                + "ddh.qty, ddh.price, ddh.amount, ddh.unique_id, 'DC' as group_name \n"
                                + "from dc_his dh\n"
                                + "join dc_details_his ddh on dh.dc_inv_id = ddh.vou_no\n"
                                + "join inp_service ins on ddh.service_id = ins.service_id\n"
                                + "left join dc_doctor_fee ddf on ddh.dc_detail_id = ddf.dc_detail_id\n"
                                + "left join doctor dr on ddf.doctor_id = dr.doctor_id\n"
                                + "where dh.deleted = false and dh.patient_id = '" + regNo + "' and dh.admission_no = '" + admNo + "' \n"
                                + "and date(dh.dc_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                                + "and ddh.service_id not in (select sys_prop_value from sys_prop \n"
                                + "	where sys_prop_desp in ('system.dc.deposite.id','system.dc.disc.id','system.dc.paid.id','system.dc.refund.id')) \n"
                                + "union all\n"
                                + "select 'OT' as tran_option, date(oh.ot_date) as tran_date, oh.ot_inv_id as vou_no,\n"
                                + "if(ifnull(odf.doctor_id,'')='', os.service_name, concat(os.service_name, ' (', dr.doctor_name, ')')) as item_name, \n"
                                + "odh.qty, odh.price, odh.amount, odh.unique_id, 'OT' as group_name \n"
                                + "from ot_his oh\n"
                                + "join ot_details_his odh on oh.ot_inv_id = odh.vou_no\n"
                                + "join ot_service os on odh.service_id = os.service_id\n"
                                + "left join ot_doctor_fee odf on odh.ot_detail_id = odf.ot_detail_id\n"
                                + "left join doctor dr on odf.doctor_id = dr.doctor_id\n"
                                + "where oh.deleted = false and oh.patient_id = '" + regNo + "' and oh.admission_no = '" + admNo + "' \n"
                                + "and date(oh.ot_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                                + "and odh.service_id not in (select sys_prop_value from sys_prop \n"
                                + "	where sys_prop_desp in ('system.ot.deposite.id','system.ot.disc.id','system.ot.paid.id','system.ot.refund.id')) \n"
                                + "union all\n"
                                + "select og.group_name as tran_option,date(oh.opd_date) as tran_date, oh.opd_inv_id as vou_no,\n"
                                + "os.service_name as item_name, odh.qty, odh.price, odh.amount, odh.unique_id, og.group_name as group_name \n"
                                + "from opd_his oh\n"
                                + "join opd_details_his odh on oh.opd_inv_id = odh.vou_no\n"
                                + "join opd_service os on odh.service_id = os.service_id\n"
                                + "join opd_category oc on os.cat_id = oc.cat_id\n"
                                + "join opd_group og on oc.group_id = og.group_id\n"
                                + "where oh.deleted = false and  oh.patient_id = '" + regNo + "' and oh.admission_no = '" + admNo + "' \n"
                                + "and date(oh.opd_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                                + "union all\n"
                                + "select concat('Pharmacy-',loc.location_name) as tran_option,date(sh.sale_date) as tran_date, sh.sale_inv_id as vou_no, \n"
                                + "concat(sdh.med_id, '-', med.med_name) as item_name, concat(sdh.sale_qty,sdh.item_unit) as qty, sdh.sale_price as price, \n"
                                + "sdh.sale_amount as amount, sdh.unique_id, 'Pharmacy' as group_name \n"
                                + "from sale_his sh, sale_detail_his sdh, location loc, medicine med\n"
                                + "where sh.sale_inv_id = sdh.vou_no and sh.location_id = loc.location_id and sdh.med_id = med.med_id \n"
                                + "and sh.deleted = false and sh.reg_no = '" + regNo + "' and sh.admission_no = '" + admNo + "' \n"
                                + "and date(sh.sale_date) between '" + admitDate + "' and '" + tranDate + "') a \n"
                                + "order by a.group_name, a.tran_option, a.tran_date, a.vou_no, a.unique_id";
                        ResultSet rs = dao.execSQL(strSql);
                        if (rs != null) {
                            String appCurr = Util1.getPropValue("system.app.currency");
                            List<CurrPTBalanceTran> list = new ArrayList();
                            HashMap<String, SessionTtl> hmTtl = new HashMap();
                            List<SessionTtl> listTtl = new ArrayList();
                            double ttlExpense = 0;
                            while (rs.next()) {
                                CurrPTBalanceTran cptbt = new CurrPTBalanceTran(
                                        rs.getString("tran_option"),
                                        rs.getDate("tran_date"),
                                        rs.getString("vou_no"),
                                        rs.getString("item_name"),
                                        rs.getString("qty"),
                                        rs.getDouble("price"),
                                        NumberUtil.roundTo(rs.getDouble("amount"), 0)
                                );
                                String groupName = rs.getString("group_name");
                                if (groupName.equals("OPD")) {
                                    log.info("Group : " + groupName);
                                }
                                SessionTtl st = hmTtl.get(groupName);
                                double tranAmt = NumberUtil.NZero(cptbt.getAmount());
                                if (st == null) {
                                    st = new SessionTtl(groupName, appCurr, tranAmt);
                                    hmTtl.put(groupName, st);
                                    listTtl.add(st);
                                } else {
                                    st.setTtlPaid(NumberUtil.NZero(st.getTtlPaid()) + tranAmt);
                                }

                                ttlExpense += tranAmt;
                                list.add(cptbt);
                            }

                            gtTablemodel.setListBal(listTtl);
                            ptBalTranTableModel.setList(list);
                            txtTtlExpense.setValue(NumberUtil.roundTo(ttlExpense, 0));
                        }
                    } else {
                        clear();
                        JOptionPane.showMessageDialog(Util1.getParent(),
                                "Invalid admitted patient code.",
                                "Admitted Patient Code", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    clear();
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Invalid admitted patient code.",
                            "Admitted Patient Code", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                log.error("getBillInfo : " + ex.toString());
            } finally {
                dao.close();
            }
        }
    }

    private void clear() {
        txtRegNo.setText("");
        lblPtName.setText("");
        txtAdmNo.setText("");
        txtTranFilter.setText(null);
        txtTtlExpense.setValue(0);
        ptBalTranTableModel.clear();
        gtTablemodel.clear();
    }

    private void getPatientPayment(String regNo, String admNo, String admitDate,
            String tranDate) {
        String strSql = "select a.group_name, a.tran_option, a.tran_date, a.vou_no, a.item_name, a.qty, a.price, a.amount from (\n"
                + "select 'DC' as tran_option, date(dh.dc_date) as tran_date, dh.dc_inv_id as vou_no,\n"
                + "ins.service_name as item_name, \n"
                + "ddh.qty, ddh.price, ddh.amount, ddh.unique_id, 'DC' as group_name \n"
                + "from dc_his dh\n"
                + "join dc_details_his ddh on dh.dc_inv_id = ddh.vou_no\n"
                + "join inp_service ins on ddh.service_id = ins.service_id\n"
                + "where dh.deleted = false and dh.patient_id = '" + regNo + "' and dh.admission_no = '" + admNo + "' \n"
                + "and date(dh.dc_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                + "and ddh.service_id in (select sys_prop_value from sys_prop \n"
                + "	where sys_prop_desp in ('system.dc.deposite.id','system.dc.disc.id','system.dc.paid.id','system.dc.refund.id')) \n"
                + "union all\n"
                + "select 'OT' as tran_option, date(oh.ot_date) as tran_date, oh.ot_inv_id as vou_no,\n"
                + "os.service_name as item_name, \n"
                + "odh.qty, odh.price, odh.amount, odh.unique_id, 'OT' as group_name \n"
                + "from ot_his oh\n"
                + "join ot_details_his odh on oh.ot_inv_id = odh.vou_no\n"
                + "join ot_service os on odh.service_id = os.service_id\n"
                + "where oh.deleted = false and oh.patient_id = '" + regNo + "' and oh.admission_no = '" + admNo + "' \n"
                + "and date(oh.ot_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                + "and odh.service_id in (select sys_prop_value from sys_prop \n"
                + "	where sys_prop_desp in ('system.ot.deposite.id','system.ot.disc.id','system.ot.paid.id','system.ot.refund.id')) \n"
                + "union all\n"
                + "select 'OPD' as tran_option,date(oh.opd_date) as tran_date, oh.opd_inv_id as vou_no,\n"
                + "'OPD Discount' as item_name, 1 as qty, oh.disc_a as price , oh.disc_a as amount, odh.unique_id, 'OPD' as group_name \n"
                + "from opd_his oh\n"
                + "where oh.deleted = false and  oh.patient_id = '" + regNo + "' and oh.admission_no = '" + admNo + "' \n"
                + "and date(oh.opd_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                + "union all\n"
                + "select 'OPD' as tran_option,date(oh.opd_date) as tran_date, oh.opd_inv_id as vou_no,\n"
                + "'OPD Paid' as item_name, 1 as qty, oh.disc_a as price , oh.disc_a as amount, odh.unique_id, 'OPD' as group_name \n"
                + "from opd_his oh\n"
                + "where oh.deleted = false and  oh.patient_id = '" + regNo + "' and oh.admission_no = '" + admNo + "' \n"
                + "and date(oh.opd_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                + "union all\n"
                + "select concat('Pharmacy-',loc.location_name) as tran_option,date(sh.sale_date) as tran_date, sh.sale_inv_id as vou_no, \n"
                + "concat(sdh.med_id, '-', med.med_name) as item_name, concat(sdh.sale_qty,sdh.item_unit) as qty, sdh.sale_price as price, \n"
                + "sdh.sale_amount as amount, sdh.unique_id, 'Pharmacy' as group_name \n"
                + "from sale_his sh, sale_detail_his sdh, location loc, medicine med\n"
                + "where sh.sale_inv_id = sdh.vou_no and sh.location_id = loc.location_id and sdh.med_id = med.med_id \n"
                + "and sh.deleted = false and sh.reg_no = '" + regNo + "' and sh.admission_no = '" + admNo + "' \n"
                + "and date(sh.sale_date) between '" + admitDate + "' and '" + tranDate + "') a \n"
                + "order by a.group_name, a.tran_option, a.tran_date, a.vou_no, a.unique_id";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtPtFilter = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPtList = new javax.swing.JTable();
        txtTranFilter = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTran = new javax.swing.JTable();
        txtTtlBal = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        txtTtlExpense = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtRegNo = new javax.swing.JTextField();
        txtAdmNo = new javax.swing.JTextField();
        lblPtName = new javax.swing.JLabel();
        butClear = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblGroupTotal = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();

        tblPtList.setFont(Global.textFont);
        tblPtList.setModel(ptBalTableModel);
        tblPtList.setRowHeight(23);
        jScrollPane1.setViewportView(tblPtList);

        txtTranFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));

        tblTran.setFont(Global.textFont);
        tblTran.setModel(ptBalTranTableModel);
        tblTran.setRowHeight(23);
        jScrollPane2.setViewportView(tblTran);

        txtTtlBal.setEditable(false);
        txtTtlBal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Total : ");

        txtTtlExpense.setEditable(false);
        txtTtlExpense.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Total Expense : ");

        txtRegNo.setEditable(false);
        txtRegNo.setBorder(javax.swing.BorderFactory.createTitledBorder("Reg No"));

        txtAdmNo.setBorder(javax.swing.BorderFactory.createTitledBorder("Adm No"));
        txtAdmNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAdmNoActionPerformed(evt);
            }
        });

        lblPtName.setText(" ");

        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Payment"));

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
        jScrollPane3.setViewportView(jTable1);

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder("Group Total"));

        tblGroupTotal.setFont(Global.textFont);
        tblGroupTotal.setModel(gtTablemodel);
        tblGroupTotal.setRowHeight(23);
        jScrollPane4.setViewportView(tblGroupTotal);

        jScrollPane5.setBorder(javax.swing.BorderFactory.createTitledBorder("Error Voucher"));

        jTable3.setFont(Global.textFont);
        jTable3.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable3.setRowHeight(23);
        jScrollPane5.setViewportView(jTable3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(txtPtFilter)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlBal, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAdmNo, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPtName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTranFilter))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(butClear)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTtlExpense, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                        .addGap(1, 1, 1)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtAdmNo, txtRegNo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtPtFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtAdmNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblPtName)
                    .addComponent(txtTranFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTtlBal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTtlExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(butClear))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lblPtName, txtAdmNo});

    }// </editor-fold>//GEN-END:initComponents

    private void txtAdmNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAdmNoActionPerformed
        getBillInfo();
    }//GEN-LAST:event_txtAdmNoActionPerformed

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable3;
    private javax.swing.JLabel lblPtName;
    private javax.swing.JTable tblGroupTotal;
    private javax.swing.JTable tblPtList;
    private javax.swing.JTable tblTran;
    private javax.swing.JTextField txtAdmNo;
    private javax.swing.JTextField txtPtFilter;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JTextField txtTranFilter;
    private javax.swing.JFormattedTextField txtTtlBal;
    private javax.swing.JFormattedTextField txtTtlExpense;
    // End of variables declaration//GEN-END:variables
}
