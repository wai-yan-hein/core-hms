/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.entry;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.inpatient.ui.common.DCDrPaymentTableModel;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.helper.OPDDrPayment;
import com.cv.app.opd.ui.util.DoctorSearchNameFilterDialog;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ExpenseType;
import com.cv.app.pharmacy.database.entity.GenExpense;
import com.cv.app.pharmacy.database.entity.Session;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.pharmacy.util.PharmacyUtil;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.ui.common.VouFormatFactory;
import com.cv.app.util.BindingUtil;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jms.MapMessage;
import javax.swing.JOptionPane;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author asus
 */
public class DCDoctorPayment extends javax.swing.JPanel implements KeyPropagate, SelectionObserver {

    static Logger log = Logger.getLogger(DCDoctorPayment.class.getName());
    private final DCDrPaymentTableModel tblModel = new DCDrPaymentTableModel();
    private final AbstractDataAccess dao = Global.dao;
    private String selectedDrId = "-";
    private Integer saveId = -1;
    private GenVouNoImpl vouEngine = null;

    /**
     * Creates new form ReaderEntry
     */
    public DCDoctorPayment() {
        initComponents();
        initTable();
        initCombo();

        txtTtlAmt.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTtlDr.setFormatterFactory(NumberUtil.getDecimalFormat());
        try {
            txtVouNo.setFormatterFactory(new VouFormatFactory());
        } catch (Exception ex) {
            log.error("Vou format error : " + ex.toString());
        }
        if (Util1.getPropValue("system.login.default.value").equals("Y")) {
            if (Global.loginDate == null) {
                Global.loginDate = DateUtil.getTodayDateStr();
            }
            txtFrom.setText(Global.loginDate);
            txtTo.setText(Global.loginDate);
            txtTranDate.setText(Global.loginDate);
        } else {
            txtFrom.setText(DateUtil.getTodayDateStr());
            txtTo.setText(DateUtil.getTodayDateStr());
            txtTranDate.setText(DateUtil.getTodayDateStr());
        }

        vouEngine = new GenVouNoImpl(dao, "DCDRPAY",
                DateUtil.getPeriod(txtTranDate.getText()));
        //butClear.setEnabled(false);
        txtTotalRecords.setValue(0);
        genVouNo();
    }

    private void initTable() {
        tblReaderEntry.getTableHeader().setFont(Global.lableFont);
        //Adjust column width
        tblReaderEntry.getColumnModel().getColumn(0).setPreferredWidth(40);//Date
        tblReaderEntry.getColumnModel().getColumn(1).setPreferredWidth(60);//Vou No
        tblReaderEntry.getColumnModel().getColumn(2).setPreferredWidth(20);//Reg No
        tblReaderEntry.getColumnModel().getColumn(3).setPreferredWidth(130);//Pt-Name
        tblReaderEntry.getColumnModel().getColumn(4).setPreferredWidth(20);//Admission No
        tblReaderEntry.getColumnModel().getColumn(5).setPreferredWidth(130);//Service name
        tblReaderEntry.getColumnModel().getColumn(6).setPreferredWidth(15);//Qty
        tblReaderEntry.getColumnModel().getColumn(7).setPreferredWidth(30);//Price
        tblReaderEntry.getColumnModel().getColumn(8).setPreferredWidth(50);//Charge Type
        tblReaderEntry.getColumnModel().getColumn(9).setPreferredWidth(50);//Amount
        tblReaderEntry.getColumnModel().getColumn(10).setPreferredWidth(40);//Surgeon Fee

        tblReaderEntry.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
    }

    @Override
    public void keyEvent(KeyEvent e) {

    }

    private void initCombo() {
        try {
            BindingUtil.BindComboFilter(cboSession, dao.findAll("Session"));
            BindingUtil.BindCombo(cboExpenseType,
                    dao.findAllHSQL("select o from ExpenseType o where o.expenseOption = 'DC' order by o.expenseName"));

            //cboService.setSelectedItem(null);
            cboExpenseType.setSelectedItem(null);

            AutoCompleteDecorator.decorate(cboExpenseType);
            AutoCompleteDecorator.decorate(cboSession);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    @Override
    public void selected(Object source, Object selectObj) {
        switch (source.toString()) {
            case "DoctorSearch":
                if (selectObj != null) {
                    Doctor selDr = (Doctor) selectObj;
                    selectedDrId = selDr.getDoctorId();
                    txtDoctor.setText(selDr.getDoctorName());
                }
                break;
        }
    }

    private void search() {
        if (selectedDrId.equals("-")) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please select the doctor.",
                    "No Doctor Select", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cboExpenseType.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Please expense type.",
                    "Expense Type", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ExpenseType et = (ExpenseType) cboExpenseType.getSelectedItem();
        if (et.getCusGroupoId() == null) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid DC custom group.",
                    "DC Custom Groupo", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String groupId = et.getCusGroupoId().toString();

        String sessionId = "0";
        if (cboSession.getSelectedItem() instanceof Session) {
            sessionId = ((Session) cboSession.getSelectedItem()).getSessionId().toString();
        }

        String strSql;
        if (et.getNeedDr()) {
            strSql = "select dc_date, dc_inv_id, patient_id, patient_name, admission_no, service_name, qty, price, amount,\n"
                    + "if(pay_dr_id='" + selectedDrId + "',dr_fee,0) dr_fee, charge_type, srv_fee1, srv_fee2, srv_fee3, srv_fee4, srv_fee5 \n"
                    + "from v_dc_dr_fee_payment\n"
                    + "where date(dc_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                    + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n";
        } else {
            strSql = "select dc_date, dc_inv_id, patient_id, patient_name, admission_no, service_name, qty, price, amount,\n"
                    + "ifnull(amount,0) dr_fee, charge_type, srv_fee1, srv_fee2, srv_fee3, srv_fee4, srv_fee5 \n"
                    + "from v_dc_dr_fee_payment\n"
                    + "where date(dc_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                    + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n";
        }
        if (!groupId.equals("-")) {
            strSql = strSql + " and cat_id in (select dc_cat_id from dc_cus_group_detail where cus_grp_id = " + groupId + ")";
        }

        if (!sessionId.equals("0")) {
            strSql = strSql + " and session_id = " + sessionId;
        }

        switch (et.getSysCode()) {
            case "DCDR": //DC Doctor Payment
                if (et.getNeedDr()) {
                    strSql = strSql + " and pay_dr_id = '" + selectedDrId
                            + "' and ifnull(pay_id,'')='' and if(pay_dr_id='" + selectedDrId + "',ifnull(dr_fee,0),0) <> 0";
                } else {
                    strSql = strSql + " and ifnull(amount,0) <> 0 and pay_dr_id is null and pay_id1 is null";
                }
                break;
            case "DCNURSE": //DC Nurse Payment
                strSql = strSql + " and (ifnull(pay_id2,'-')='-') and ifnull(srv_fee2,0) <> 0";
                if (et.getNeedDr()) {
                    strSql = strSql + " and doctor_id = '" + selectedDrId + "'";
                }
                break;
            case "DCMO": //DC MO Payment
                strSql = strSql + " and (ifnull(pay_id4,'-')='-') and ifnull(srv_fee4,0) <> 0";
                if (et.getNeedDr()) {
                    strSql = strSql + " and doctor_id = '" + selectedDrId + "'";
                }
                break;
            case "DCTECH": //DC Tech Payment
                strSql = strSql + " and (ifnull(pay_id3,'-')='-') and ifnull(srv_fee3,0) <> 0";
                if (et.getNeedDr()) {
                    strSql = strSql + " and doctor_id = '" + selectedDrId + "'";
                }
                break;
        }

        strSql = strSql + " order by dc_date, patient_name, dc_inv_id";

        try {
            log.info("sql : " + strSql);
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                List<OPDDrPayment> listDetail = new ArrayList();
                double ttlAmt = 0;
                double ttlDR = 0;
                double ttlNurse = 0;
                double ttlTech = 0;
                double ttlMo = 0;
                while (rs.next()) {
                    OPDDrPayment opdPay = new OPDDrPayment();
                    opdPay.setAdmissionNo(rs.getString("admission_no"));
                    opdPay.setAmount(rs.getDouble("amount"));
                    opdPay.setReaderFee(rs.getDouble("dr_fee"));
                    opdPay.setStaffFee(rs.getDouble("srv_fee2"));
                    opdPay.setTechFee(rs.getDouble("srv_fee3"));
                    opdPay.setMoFee(rs.getDouble("srv_fee4"));
                    opdPay.setPrice(rs.getDouble("price"));
                    opdPay.setPtName(rs.getString("patient_name"));
                    opdPay.setQty(rs.getInt("qty"));
                    opdPay.setRegNo(rs.getString("patient_id"));
                    opdPay.setServiceName(rs.getString("service_name"));
                    opdPay.setTranDate(rs.getDate("dc_date"));
                    opdPay.setVouNo(rs.getString("dc_inv_id"));
                    opdPay.setChargeType(rs.getString("charge_type"));

                    ttlAmt += NumberUtil.NZero(rs.getDouble("amount"));
                    ttlDR += NumberUtil.NZero(rs.getDouble("dr_fee"));
                    ttlNurse += NumberUtil.NZero(rs.getDouble("srv_fee2"));
                    ttlTech += NumberUtil.NZero(rs.getDouble("srv_fee3"));
                    ttlMo += NumberUtil.NZero(rs.getDouble("srv_fee4"));

                    listDetail.add(opdPay);
                }

                txtTtlAmt.setValue(ttlAmt);
                txtTtlDr.setValue(ttlDR);
                txtTtlNurse.setValue(ttlNurse);
                txtTtlTech.setValue(ttlTech);
                txtTtlMO.setValue(ttlMo);
                tblModel.setListDP(listDetail);
                txtTotalRecords.setValue(listDetail.size());
                rs.close();
            }
        } catch (Exception ex) {
            log.error("search : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private boolean isValidEntry() {
        Date vouSaleDate = DateUtil.toDate(txtTranDate.getText());
        Date lockDate = PharmacyUtil.getLockDate(dao);
        if (vouSaleDate.before(lockDate) || vouSaleDate.equals(lockDate)) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Data is locked at "
                    + DateUtil.toDateStr(lockDate) + ".",
                    "Locked Data", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean status = true;
        if (saveId == -1) {
            if (selectedDrId.equals("-")) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid doctor.",
                        "No Doctor Selected", JOptionPane.ERROR_MESSAGE);
                status = false;
            } else if (cboExpenseType.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid expense type.",
                        "Expense Type", JOptionPane.ERROR_MESSAGE);
                status = false;
            } else {
                ExpenseType et = (ExpenseType) cboExpenseType.getSelectedItem();
                switch (et.getExpenseId()) {
                    case 7: //CF Fee Payment
                        double totalAmt = NumberUtil.NZero(txtTtlAmt.getValue());
                        if (totalAmt == 0) {
                            JOptionPane.showMessageDialog(Util1.getParent(), "No ot doctor fee data to print.",
                                    "No Data", JOptionPane.ERROR_MESSAGE);
                            status = false;
                        }
                        break;
                }
            }
        }

        return status;
    }

    private void save() {
        String vouNo = txtVouNo.getText();

        if (isValidEntry()) {
            ExpenseType et = (ExpenseType) cboExpenseType.getSelectedItem();
            String strSql = "-";
            String strSqlExp = "-";
            String groupId = et.getCusGroupoId().toString();

            String sessionId = "0";
            if (cboSession.getSelectedItem() instanceof Session) {
                sessionId = ((Session) cboSession.getSelectedItem()).getSessionId().toString();
            }

            switch (et.getSysCode()) {
                case "DCDR": //DC Doctor Fee Payment
                    if (et.getNeedDr()) {
                        strSql = "update dc_his oh \n"
                                + "JOIN dc_details_his odh on oh.dc_inv_id = odh.vou_no \n"
                                + "JOIN inp_service os on odh.service_id = os.service_id \n"
                                + "join dc_doctor_fee odf on odh.dc_detail_id = odf.dc_detail_id \n"
                                + "set odf.pay_id = '?', odf.pay_amt = if(ifnull(odh.amount,0)=0,0,(ifnull(odh.qty,0)*ifnull(odf.dr_fee,0))) \n"
                                + "where oh.deleted = 0 and odf.doctor_id = '" + selectedDrId + "'\n"
                                + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and ifnull(odf.pay_id,'')='' ";
                    } else {
                        strSql = "update dc_his oh \n"
                                + "JOIN dc_details_his odh on oh.dc_inv_id = odh.vou_no \n"
                                + "JOIN inp_service os on odh.service_id = os.service_id \n"
                                + "set odh.pay_id1 = '?', odh.fee1_pay_amt = ifnull(odh.amount,0) \n"
                                + "where oh.deleted = 0 \n"
                                + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and ifnull(odh.pay_id1,'')='' ";
                    }
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and os.cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id ="
                                + groupId + ")";
                    }

                    if (et.getNeedDr()) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.dr_fee,0)) as amount, veac.exp_acc_id\n"
                                + "from v_dc_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and pay_dr_id = '" + selectedDrId + "' and ifnull(pay_id,'')='' ";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.amount,0)) as amount, veac.exp_acc_id\n"
                                + "from v_dc_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + " and ifnull(pay_id1,'')='' ";
                    }
                    break;
                case "DCNURSE":
                    strSql = "update dc_his oh \n"
                            + "JOIN dc_details_his odh on oh.dc_inv_id = odh.vou_no \n"
                            + "JOIN inp_service os on odh.service_id = os.service_id \n"
                            + "set odh.pay_id2 = '?', odh.fee2_pay_amt = if(ifnull(odh.amount,0)=0,0,(ifnull(odh.qty,0)*ifnull(odh.srv_fee2,0))) \n"
                            + "where oh.deleted = 0 \n"
                            + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                            + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                            + " and ifnull(odh.pay_id2,'')='' and ifnull(odh.srv_fee2,0) <> 0 ";
                    if (et.getNeedDr()) {
                        strSql = strSql + " and oh.doctor_id = '" + selectedDrId + "'";
                    }
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and os.cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id ="
                                + groupId + ")";
                    }

                    strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.srv_fee2,0)) as amount, veac.exp_acc_id\n"
                            + "from v_dc_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                            + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                            + "where vdfp.payable_acc_opt = veac.use_for\n"
                            + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                            + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                            + " and cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id = veac.cus_group_id) "
                            + "and ifnull(pay_id2,'')='' ";
                    if (et.getNeedDr()) {
                        strSqlExp = strSqlExp + " and vdfp.doctor_id = '" + selectedDrId + "'";
                    }
                    break;
                case "DCTECH":
                    strSql = "update dc_his oh \n"
                            + "JOIN dc_details_his odh on oh.dc_inv_id = odh.vou_no \n"
                            + "JOIN inp_service os on odh.service_id = os.service_id \n"
                            + "set odh.pay_id3 = '?', odh.fee3_pay_amt = if(ifnull(odh.amount,0)=0,0,(ifnull(odh.qty,0)*ifnull(odh.srv_fee3,0))) \n"
                            + "where oh.deleted = 0 \n"
                            + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                            + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                            + " and ifnull(odh.pay_id3,'')='' and ifnull(odh.srv_fee3,0) <> 0 ";
                    if (et.getNeedDr()) {
                        strSql = strSql + " and oh.doctor_id = '" + selectedDrId + "'";
                    }
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and os.cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id ="
                                + groupId + ")";
                    }

                    strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.srv_fee3,0)) as amount, veac.exp_acc_id\n"
                            + "from v_dc_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                            + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                            + "where vdfp.payable_acc_opt = veac.use_for\n"
                            + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                            + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                            + " and cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id = veac.cus_group_id) "
                            + "and ifnull(pay_id3,'')='' ";
                    if (et.getNeedDr()) {
                        strSqlExp = strSqlExp + " and vdfp.doctor_id = '" + selectedDrId + "'";
                    }
                    break;
                case "DCMO":
                    strSql = "update dc_his oh \n"
                            + "JOIN dc_details_his odh on oh.dc_inv_id = odh.vou_no \n"
                            + "JOIN inp_service os on odh.service_id = os.service_id \n"
                            + "set odh.pay_id4 = '?', odh.fee4_pay_amt = if(ifnull(odh.amount,0)=0,0,(ifnull(odh.qty,0)*ifnull(odh.srv_fee4,0))) \n"
                            + "where oh.deleted = 0 \n"
                            + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                            + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                            + " and ifnull(odh.pay_id4,'')='' and ifnull(odh.srv_fee4,0) <> 0 ";
                    if (et.getNeedDr()) {
                        strSql = strSql + " and oh.doctor_id = '" + selectedDrId + "'";
                    }
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and os.cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id ="
                                + groupId + ")";
                    }

                    strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.srv_fee4,0)) as amount, veac.exp_acc_id\n"
                            + "from v_dc_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                            + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                            + "where vdfp.payable_acc_opt = veac.use_for\n"
                            + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                            + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                            + " and cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id = veac.cus_group_id) "
                            + "and ifnull(pay_id4,'')='' ";
                    if (et.getNeedDr()) {
                        strSqlExp = strSqlExp + " and vdfp.doctor_id = '" + selectedDrId + "'";
                    }
                    break;
            }

            if (!sessionId.equals("0")) {
                strSql = strSql + " and oh.session_id = " + sessionId;
                strSqlExp = strSqlExp + " and session_id = " + sessionId;
            }

            strSqlExp = strSqlExp + " group by source_acc_id, acc_id, dept_code, use_for, veac.exp_acc_id";

            try {
                String appCurr = Util1.getPropValue("system.app.currency");
                ResultSet rs = dao.execSQL(strSqlExp);

                strSql = strSql.replace("?", vouNo);
                log.info("Save : " + strSql);
                //dao.open();
                dao.execSql(strSql);
                //dao.commit();
                vouEngine.updateVouNo();

                if (rs != null) {
                    //dao.open();
                    //dao.beginTran();
                    while (rs.next()) {
                        GenExpense rec = new GenExpense();
                        rec.setExpDate(DateUtil.toDate(txtTranDate.getText()));
                        rec.setExpType(et);
                        rec.setDesp(selectedDrId + " - " + txtDoctor.getText()
                                + " - (" + txtFrom.getText() + " to " + txtTo.getText() + ")");
                        rec.setRemark(vouNo + " " + rs.getString("use_for") + " "
                                + Global.loginUser.getUserName());
                        rec.setCreatedBy(Global.loginUser.getUserId());
                        rec.setSession(Global.sessionId);
                        rec.setCreatedDate(new Date());
                        rec.setCurrency(appCurr);
                        rec.setAmount(rs.getDouble("amount"));
                        if (chkUPP.isSelected()) {
                            rec.setAccId(rs.getString("source_acc_id"));
                            rec.setSourceAccId(rs.getString("exp_acc_id"));
                        } else {
                            rec.setAccId(rs.getString("acc_id"));
                            rec.setSourceAccId(rs.getString("source_acc_id"));
                        }
                        //rec.setAccId(rs.getString("acc_id"));
                        //rec.setSourceAccId(rs.getString("source_acc_id"));
                        rec.setDeptId(rs.getString("dept_code"));
                        rec.setPaidFor(rs.getString("use_for"));
                        rec.setVouNo(vouNo);
                        rec.setExpenseOpotion(et.getExpenseOption());
                        rec.setDoctorId(selectedDrId);
                        rec.setUpp(chkUPP.isSelected());
                        rec.setDeleted(false);
                        rec.setRecLock(Boolean.FALSE);
                        dao.save(rec);
                        uploadToAccount(rec.getGeneId());
                    }
                }

                printPayment(vouNo);
            } catch (Exception ex) {
                //dao.rollBack();
                log.error("save : " + ex.toString());
            } finally {
                dao.close();
            }

            clear();
        }
    }

    private void print() {
        save();
    }

    private void printPayment(String vouNo) {
        ExpenseType et = (ExpenseType) cboExpenseType.getSelectedItem();
        String reportName = "-";

        switch (et.getExpenseOption()) {
            case "DC":
                switch (et.getSysCode()) {
                    case "DCDR": //OT Doctor Payment
                        reportName = "DCDoctorFeePayment";
                        break;
                    case "DCNURSE":
                        reportName = "DCNurseFeePayment";
                        break;
                    case "DCTECH":
                        reportName = "DCTechFeePayment";
                        break;
                    case "DCMO":
                        reportName = "DCMOFeePayment";
                        break;
                }
                break;
        }

        if (!reportName.equals("-")) {
            String doctorId = selectedDrId;
            String doctorName = txtDoctor.getText();
            String period = txtFrom.getText() + " to " + txtTo.getText();
            String fromDate = DateUtil.toDateStrMYSQL(txtFrom.getText());
            String toDate = DateUtil.toDateStrMYSQL(txtTo.getText());
            String reportPath = Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path")
                    + "clinic/"
                    + reportName;
            Map<String, Object> params = new HashMap();
            String compName = Util1.getPropValue("report.company.name");
            String phoneNo = Util1.getPropValue("report.phone");
            String address = Util1.getPropValue("report.address");
            params.put("p_user_id", Global.machineId);
            params.put("compName", compName);
            params.put("phoneNo", phoneNo);
            params.put("comAddress", address);
            params.put("SUBREPORT_DIR", Util1.getAppWorkFolder()
                    + Util1.getPropValue("report.folder.path"));
            params.put("p_doctor_id", doctorId);
            params.put("p_pay_id", vouNo);
            params.put("p_doctor", doctorName);
            params.put("p_period", period);
            params.put("p_from_date", fromDate);
            params.put("p_to_date", toDate);
            params.put("p_payment_desp", et.getExpenseName());
            try {
                dao.close();
                ReportUtil.viewReport(reportPath, params, dao.getConnection());
                dao.commit();
            } catch (Exception ex) {
                log.error("printXRayForm : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    private void clear() {
        selectedDrId = "-";
        saveId = -1;
        txtDoctor.setText("");
        tblModel.clear();
        cboExpenseType.setSelectedItem(null);
        //txtFrom.setText(DateUtil.getTodayDateStr());
        //txtTo.setText(DateUtil.getTodayDateStr());
        //txtTranDate.setText(DateUtil.getTodayDateStr());
        txtTotalRecords.setValue(0);
        txtTtlAmt.setValue(0);
        txtTtlDr.setValue(0);
        chkUPP.setSelected(false);

        genVouNo();
    }

    private void uploadToAccount(Long vouNo) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            String rootUrl = Util1.getPropValue("system.intg.api.url");

            if (!rootUrl.isEmpty() && !rootUrl.equals("-")) {
                try ( CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    String url = rootUrl + "/expense";
                    final HttpPost request = new HttpPost(url);
                    final List<NameValuePair> params = new ArrayList();
                    params.add(new BasicNameValuePair("expId", vouNo.toString()));
                    request.setEntity(new UrlEncodedFormEntity(params));
                    CloseableHttpResponse response = httpClient.execute(request);
                    try {
                        dao.execSql("update gen_expense set intg_upd_status = null where gene_id = '" + vouNo + "'");
                    } catch (Exception ex) {
                        log.error("uploadToAccount error 1: " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                    // Handle the response
                    try ( BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
                        String output;
                        while ((output = br.readLine()) != null) {
                            if (!output.equals("Sent")) {
                                log.error("Error in server : " + vouNo + " : " + output);
                            }
                        }
                    }
                } catch (IOException e) {
                    try {
                        dao.execSql("update gen_expense set intg_upd_status = null where gene_id = '" + vouNo + "'");
                    } catch (Exception ex) {
                        log.error("uploadToAccount error : " + ex.getMessage());
                    } finally {
                        dao.close();
                    }
                }
            } else {
                try {
                    dao.execSql("update gen_expense set intg_upd_status = null where gene_id = '" + vouNo + "'");
                } catch (Exception ex) {
                    log.error("uploadToAccount error : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }
        }
    }

    private void genVouNo() {
        String vouNo = vouEngine.getVouNo();
        txtVouNo.setText(vouNo);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtFrom = new javax.swing.JFormattedTextField();
        txtTo = new javax.swing.JFormattedTextField();
        butSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReaderEntry = new javax.swing.JTable();
        cboExpenseType = new javax.swing.JComboBox<>();
        butPrint = new javax.swing.JButton();
        txtTtlAmt = new javax.swing.JFormattedTextField();
        txtTtlDr = new javax.swing.JFormattedTextField();
        txtDoctor = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        butClear = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtTotalRecords = new javax.swing.JFormattedTextField();
        txtTranDate = new javax.swing.JFormattedTextField();
        txtTtlMO = new javax.swing.JFormattedTextField();
        txtTtlTech = new javax.swing.JFormattedTextField();
        txtTtlNurse = new javax.swing.JFormattedTextField();
        txtVouNo = new javax.swing.JFormattedTextField();
        cboSession = new javax.swing.JComboBox<>();
        chkUPP = new javax.swing.JCheckBox();

        txtFrom.setBorder(javax.swing.BorderFactory.createTitledBorder("From"));
        txtFrom.setFont(Global.textFont);
        txtFrom.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtFromFocusGained(evt);
            }
        });
        txtFrom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtFromMouseClicked(evt);
            }
        });
        txtFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFromActionPerformed(evt);
            }
        });

        txtTo.setBorder(javax.swing.BorderFactory.createTitledBorder("To"));
        txtTo.setFont(Global.textFont);
        txtTo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtToFocusGained(evt);
            }
        });
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

        tblReaderEntry.setFont(Global.textFont);
        tblReaderEntry.setModel(tblModel);
        tblReaderEntry.setRowHeight(23);
        tblReaderEntry.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblReaderEntryMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblReaderEntry);

        cboExpenseType.setFont(Global.textFont);
        cboExpenseType.setBorder(javax.swing.BorderFactory.createTitledBorder("Expense Type"));

        butPrint.setText("Print");
        butPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrintActionPerformed(evt);
            }
        });

        txtTtlAmt.setEditable(false);
        txtTtlAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTtlDr.setEditable(false);
        txtTtlDr.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTtlDr.setToolTipText("");

        txtDoctor.setEditable(false);
        txtDoctor.setBorder(javax.swing.BorderFactory.createTitledBorder("Doctor"));
        txtDoctor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDoctorMouseClicked(evt);
            }
        });

        jLabel1.setText("Total : ");

        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        jLabel2.setText("Total Records : ");

        txtTotalRecords.setEditable(false);
        txtTotalRecords.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTranDate.setBorder(javax.swing.BorderFactory.createTitledBorder("Tran Date"));
        txtTranDate.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtTranDateMouseClicked(evt);
            }
        });

        txtTtlMO.setEditable(false);
        txtTtlMO.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTtlTech.setEditable(false);
        txtTtlTech.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTtlNurse.setEditable(false);
        txtTtlNurse.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtVouNo.setEditable(false);
        txtVouNo.setBorder(javax.swing.BorderFactory.createTitledBorder("Vou No."));

        cboSession.setBorder(javax.swing.BorderFactory.createTitledBorder("Session"));

        chkUPP.setText("UPP");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDoctor, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboExpenseType, 0, 165, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTranDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkUPP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butPrint))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalRecords, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlDr, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlNurse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlTech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlMO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTtlAmt, txtTtlDr, txtTtlMO, txtTtlNurse, txtTtlTech});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTo, txtTranDate});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboExpenseType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTranDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkUPP))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(butPrint)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(butSearch)
                                .addComponent(butClear)))))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTtlAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlDr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(txtTotalRecords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlMO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlTech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlNurse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cboExpenseType, cboSession, txtDoctor, txtFrom, txtTo, txtTranDate, txtVouNo});

    }// </editor-fold>//GEN-END:initComponents

    private void txtFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFromActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFromActionPerformed

    private void butSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butSearchActionPerformed
        search();
        //butSearch.setEnabled(false);
        //butClear.setEnabled(true);
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

    private void txtFromFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtFromFocusGained
        txtFrom.selectAll();
    }//GEN-LAST:event_txtFromFocusGained

    private void txtToFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtToFocusGained
        txtTo.selectAll();
    }//GEN-LAST:event_txtToFocusGained

    private void butPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintActionPerformed
        print();
    }//GEN-LAST:event_butPrintActionPerformed

    private void tblReaderEntryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblReaderEntryMouseClicked

    }//GEN-LAST:event_tblReaderEntryMouseClicked

    private void txtDoctorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDoctorMouseClicked
        if (evt.getClickCount() == 2) {
            DoctorSearchNameFilterDialog dialog = new DoctorSearchNameFilterDialog(dao, this);
        }
    }//GEN-LAST:event_txtDoctorMouseClicked

    private void butClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butClearActionPerformed
        clear();
    }//GEN-LAST:event_butClearActionPerformed

    private void txtTranDateMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTranDateMouseClicked
        if (evt.getClickCount() == 2) {
            String strDate = DateUtil.getDateDialogStr();

            if (strDate != null) {
                txtTranDate.setText(strDate);
            }
        }
    }//GEN-LAST:event_txtTranDateMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butPrint;
    private javax.swing.JButton butSearch;
    private javax.swing.JComboBox<String> cboExpenseType;
    private javax.swing.JComboBox<String> cboSession;
    private javax.swing.JCheckBox chkUPP;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblReaderEntry;
    private javax.swing.JTextField txtDoctor;
    private javax.swing.JFormattedTextField txtFrom;
    private javax.swing.JFormattedTextField txtTo;
    private javax.swing.JFormattedTextField txtTotalRecords;
    private javax.swing.JFormattedTextField txtTranDate;
    private javax.swing.JFormattedTextField txtTtlAmt;
    private javax.swing.JFormattedTextField txtTtlDr;
    private javax.swing.JFormattedTextField txtTtlMO;
    private javax.swing.JFormattedTextField txtTtlNurse;
    private javax.swing.JFormattedTextField txtTtlTech;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
