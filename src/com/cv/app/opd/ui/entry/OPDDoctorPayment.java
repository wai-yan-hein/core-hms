/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.entry;

import com.cv.app.common.ActiveMQConnection;
import com.cv.app.common.Global;
import com.cv.app.common.KeyPropagate;
import com.cv.app.common.SelectionObserver;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.helper.OPDDrPayment;
import com.cv.app.opd.ui.common.OPDDrPaymentTableModel;
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
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.Timeout;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author asus
 */
public class OPDDoctorPayment extends javax.swing.JPanel implements KeyPropagate, SelectionObserver {

    static Logger log = Logger.getLogger(OPDDoctorPayment.class.getName());
    private final OPDDrPaymentTableModel tblModel = new OPDDrPaymentTableModel();
    private final AbstractDataAccess dao = Global.dao;
    private String selectedDrId = "-";
    private Integer saveId = -1;
    private GenVouNoImpl vouEngine = null;

    /**
     * Creates new form ReaderEntry
     */
    public OPDDoctorPayment() {
        initComponents();
        initTable();
        initCombo();

        txtTtlRefer.setFormatterFactory(NumberUtil.getDecimalFormat());
        txtTtlReader.setFormatterFactory(NumberUtil.getDecimalFormat());
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
        vouEngine = new GenVouNoImpl(dao, "OPDDRPAY",
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
        tblReaderEntry.getColumnModel().getColumn(10).setPreferredWidth(40);//MO Fee
        tblReaderEntry.getColumnModel().getColumn(11).setPreferredWidth(40);//Staff Fee
        tblReaderEntry.getColumnModel().getColumn(12).setPreferredWidth(40);//Tech Fee
        tblReaderEntry.getColumnModel().getColumn(13).setPreferredWidth(40);//Refer Fee
        tblReaderEntry.getColumnModel().getColumn(14).setPreferredWidth(40);//Reader Fee

        tblReaderEntry.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
    }

    @Override
    public void keyEvent(KeyEvent e) {

    }

    private void initCombo() {
        try {
            BindingUtil.BindComboFilter(cboSession, dao.findAll("Session"));
            BindingUtil.BindCombo(cboExpenseType,
                    dao.findAllHSQL("select o from ExpenseType o where o.expenseOption = 'OPD' order by o.expenseName"));

            //cboService.setSelectedItem(null);
            cboExpenseType.setSelectedItem(null);

            AutoCompleteDecorator.decorate(cboExpenseType);
            AutoCompleteDecorator.decorate(cboSession);
        } catch (Exception ex) {
            log.error("initCombo : " + ex.getMessage());
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
            JOptionPane.showMessageDialog(Util1.getParent(), "Invalid OPD custom group.",
                    "OPD Custom Groupo", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String groupId = et.getCusGroupoId().toString();

        String sessionId = "0";
        if (cboSession.getSelectedItem() instanceof Session) {
            sessionId = ((Session) cboSession.getSelectedItem()).getSessionId().toString();
        }

        String strSql = "select opd_date, opd_inv_id, patient_id, patient_name, admission_no, service_name, qty, price, amount,\n"
                + "ifnull(srv_fees2,0) mo_fee, ifnull(srv_fees3,0) staff_fee, ifnull(srv_fees4,0) tech_fee,\n"
                + "if(refer_doctor_id='" + selectedDrId + "',srv_fees5,0) refer_fee, if(reader_doctor_id='" + selectedDrId + "',srv_fees6,0) reader_fee,\n"
                + "charge_type \n"
                + "from v_opd_dr_fee_payment\n"
                + "where (ifnull(fee2_id,'')='' or ifnull(fee3_id,'')='' or ifnull(fee4_id,'')='' or ifnull(fee5_id,'')='' or ifnull(fee6_id,'')='')\n"
                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n";

        if (!groupId.equals("-") && !groupId.isEmpty()) {
            strSql = strSql + " and cat_id in (select opd_cat_id "
                    + "from opd_cus_lab_group_detail a where cus_grp_id = " + groupId + ")";
        }

        if (!sessionId.equals("0")) {
            strSql = strSql + " and session_id = " + sessionId;
        }

        switch (et.getSysCode()) {
            case "OPDCF": //CF Fee Payment
                if (et.getNeedDr()) {
                    strSql = strSql + " and ifnull(refer_doctor_id,doctor_id) = '" + selectedDrId
                            + "' and ifnull(fee1_id,'')=''";
                } else {
                    strSql = strSql + " and ifnull(fee1_id,'')=''";
                }
                break;
            case "OPDREFER": //Refer Fee Payment
                strSql = strSql + " and refer_doctor_id = '" + selectedDrId
                        + "' and if(refer_doctor_id='" + selectedDrId
                        + "',srv_fees5,0) <> 0 and ifnull(fee5_id,'')=''";
                break;
            case "OPDREAD": //Reader Fee Payment
                strSql = strSql + " and reader_doctor_id = '" + selectedDrId
                        + "' and if(reader_doctor_id='" + selectedDrId
                        + "',srv_fees6,0) <> 0 and ifnull(fee6_id,'')=''";
                break;
            case "OPDMO": //MO Fee Payment
                strSql = strSql + " and ifnull(srv_fees2,0) <> 0 and ifnull(fee2_id,'')=''"
                        + " and (mo_id = '" + selectedDrId + "' or mo_id = crvf2_ref_dr)";
                break;
            case "OPDTECH": //Tech Fee Payment
                if (et.getNeedDr()) {
                    strSql = strSql + " and tech_id = '" + selectedDrId
                            + "' and if(tech_id='" + selectedDrId
                            + "',srv_fees4,0) <> 0 and ifnull(fee4_id,'')=''";
                } else {
                    strSql = strSql + " and ifnull(fee4_id,'')='' and ifnull(srv_fees4,0) <> 0";
                }
                break;
            case "OPDSTAFF": //Staff Fee Payment
                strSql = strSql + " and ifnull(srv_fees3,0) <> 0 and ifnull(fee3_id,'')=''"
                        + " and (staff_id = '" + selectedDrId + "' or staff_id = crvf3_ref_dr)";
                break;
        }
        strSql = strSql + " order by opd_date, opd_inv_id, patient_name";

        try {
            log.info("sql : " + strSql);
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                List<OPDDrPayment> listDetail = new ArrayList();
                double ttlAmt = 0;
                double ttlMo = 0;
                double ttlStaff = 0;
                double ttlTech = 0;
                double ttlRefer = 0;
                double ttlReader = 0;
                while (rs.next()) {
                    OPDDrPayment opdPay = new OPDDrPayment();
                    opdPay.setAdmissionNo(rs.getString("admission_no"));
                    opdPay.setAmount(rs.getDouble("amount"));
                    opdPay.setMoFee(rs.getDouble("mo_fee"));
                    opdPay.setPrice(rs.getDouble("price"));
                    opdPay.setPtName(rs.getString("patient_name"));
                    opdPay.setQty(rs.getInt("qty"));
                    opdPay.setReaderFee(rs.getDouble("reader_fee"));
                    opdPay.setReferFee(rs.getDouble("refer_fee"));
                    opdPay.setRegNo(rs.getString("patient_id"));
                    opdPay.setServiceName(rs.getString("service_name"));
                    opdPay.setStaffFee(rs.getDouble("staff_fee"));
                    opdPay.setTechFee(rs.getDouble("tech_fee"));
                    opdPay.setTranDate(rs.getDate("opd_date"));
                    opdPay.setVouNo(rs.getString("opd_inv_id"));
                    opdPay.setChargeType(rs.getString("charge_type"));

                    ttlAmt += NumberUtil.NZero(rs.getDouble("amount"));
                    ttlMo += NumberUtil.NZero(rs.getDouble("mo_fee"));
                    ttlStaff += NumberUtil.NZero(rs.getDouble("staff_fee"));
                    ttlTech += NumberUtil.NZero(rs.getDouble("tech_fee"));
                    ttlRefer += NumberUtil.NZero(rs.getDouble("refer_fee"));
                    ttlReader += NumberUtil.NZero(rs.getDouble("reader_fee"));

                    listDetail.add(opdPay);
                }

                txtTtlAmt.setValue(ttlAmt);
                txtTtlMo.setValue(ttlMo);
                txtTtlStaff.setValue(ttlStaff);
                txtTtlTech.setValue(ttlTech);
                txtTtlRefer.setValue(ttlRefer);
                txtTtlReader.setValue(ttlReader);

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
            ExpenseType et = (ExpenseType) cboExpenseType.getSelectedItem();
            if (selectedDrId.equals("-")) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid doctor.",
                        "No Doctor Selected", JOptionPane.ERROR_MESSAGE);
                status = false;
            } else if (cboExpenseType.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid expense type.",
                        "Expense Type", JOptionPane.ERROR_MESSAGE);
                status = false;
            } else if (et.getCusGroupoId() == null) {
                JOptionPane.showMessageDialog(Util1.getParent(), "Invalid OPD custom group.",
                        "OPD Custom Groupo", JOptionPane.ERROR_MESSAGE);
                status = false;
            } else {
                switch (et.getSysCode()) {
                    case "OPDCF": //CF Fee Payment
                        double totalAmt = NumberUtil.NZero(txtTtlAmt.getValue());
                        if (totalAmt == 0) {
                            JOptionPane.showMessageDialog(Util1.getParent(), "No CF fee data to print.",
                                    "No Data", JOptionPane.ERROR_MESSAGE);
                            status = false;
                        }
                        break;
                    case "OPDREFER": //Refer Fee Payment
                        double totalRefer = NumberUtil.NZero(txtTtlRefer.getValue());
                        if (totalRefer == 0) {
                            JOptionPane.showMessageDialog(Util1.getParent(), "No refer fee data to print.",
                                    "No Data", JOptionPane.ERROR_MESSAGE);
                            status = false;
                        }
                        break;
                    case "OPDREAD": //Reader Fee Payment
                        double totalReader = NumberUtil.NZero(txtTtlReader.getValue());
                        if (totalReader == 0) {
                            JOptionPane.showMessageDialog(Util1.getParent(), "No reader fee data to print.",
                                    "No Data", JOptionPane.ERROR_MESSAGE);
                            status = false;
                        }
                        break;
                    case "OPDMO": //MO Fee Payment
                        double totalMO = NumberUtil.NZero(txtTtlMo.getValue());
                        if (totalMO == 0) {
                            JOptionPane.showMessageDialog(Util1.getParent(), "No mo fee data to print.",
                                    "No Data", JOptionPane.ERROR_MESSAGE);
                            status = false;
                        }
                        break;
                    case "OPDTECH": //Tech Fee Payment
                        double totalTech = NumberUtil.NZero(txtTtlTech.getValue());
                        if (totalTech == 0) {
                            JOptionPane.showMessageDialog(Util1.getParent(), "No tech fee data to print.",
                                    "No Data", JOptionPane.ERROR_MESSAGE);
                            status = false;
                        }
                        break;
                    case "OPDSTAFF": //Staff Fee Payment
                        double totalStaff = NumberUtil.NZero(txtTtlStaff.getValue());
                        if (totalStaff == 0) {
                            JOptionPane.showMessageDialog(Util1.getParent(), "No staff fee data to print.",
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
            String drPaySetting = Util1.getPropValue("system.drpayment");

            String sessionId = "0";
            if (cboSession.getSelectedItem() instanceof Session) {
                sessionId = ((Session) cboSession.getSelectedItem()).getSessionId().toString();
            }

            switch (et.getSysCode()) {
                case "OPDCF": //CF Fee Payment
                    if (et.getNeedDr()) {
                        strSql = "update opd_his oh "
                                + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                                + "JOIN opd_service os on odh.service_id = os.service_id "
                                + "set fee1_id = '?' "
                                + "where oh.deleted = 0 and ifnull(odh.refer_doctor_id,oh.doctor_id) = '" + selectedDrId + "'\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and ifnull(fee1_id,'')='' ";
                    } else {
                        strSql = "update opd_his oh "
                                + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                                + "JOIN opd_service os on odh.service_id = os.service_id "
                                + "set fee1_id = '?' "
                                + "where oh.deleted = 0 \n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and ifnull(fee1_id,'')='' ";
                    }
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and cat_id in (select opd_cat_id "
                                + "from opd_cus_lab_group_detail a where cus_grp_id = " + groupId + ")";
                    }

                    if (drPaySetting.equals("IPD")) {
                        if (et.getNeedDr()) {
                            strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.amount,0)) as amount, veac.exp_acc_id \n"
                                    + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id  "
                                    + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                    + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                    + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                    + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                    + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                    + "and doctor_id = '" + selectedDrId + "' and ifnull(fee1_id,'')='' ";
                        } else {
                            strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.amount,0)) as amount, veac.exp_acc_id \n"
                                    + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id  "
                                    + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                    + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                    + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                    + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                    + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                    + " and ifnull(fee1_id,'')='' ";
                        }
                    } else if (et.getNeedDr()) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.amount,0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id  "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and doctor_id = '" + selectedDrId + "' and ifnull(fee1_id,'')='' ";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.amount,0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id  "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + " and ifnull(fee1_id,'')='' ";
                    }
                    break;
                case "OPDREFER": //Refer Fee Payment
                    strSql = "update opd_his oh "
                            + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                            + "JOIN opd_service os on odh.service_id = os.service_id "
                            + "set fee5_id = '?' "
                            + "where oh.deleted = 0 and refer_doctor_id = '" + selectedDrId + "'\n"
                            + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                            + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                            + "and if(refer_doctor_id='" + selectedDrId + "',odh.srv_fees5,0) <> 0 and ifnull(odh.fee5_id,'')=''";
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and cat_id in (select opd_cat_id "
                                + "from opd_cus_lab_group_detail a where cus_grp_id = " + groupId + ")";
                    }

                    if (drPaySetting.equals("IPD")) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(if(refer_doctor_id='"
                                + selectedDrId + "',srv_fees5,0),0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id  "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and refer_doctor_id = '" + selectedDrId + "' and ifnull(fee5_id,'')='' \n"
                                + " and ifnull(if(refer_doctor_id='" + selectedDrId + "',srv_fees5,0),0) <> 0 \n";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(if(refer_doctor_id='"
                                + selectedDrId + "',srv_fees5,0),0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and refer_doctor_id = '" + selectedDrId + "' and ifnull(fee5_id,'')='' \n"
                                + " and ifnull(if(refer_doctor_id='" + selectedDrId + "',srv_fees5,0),0) <> 0 \n";
                    }
                    break;
                case "OPDREAD": //Reader Fee Payment
                    strSql = "update opd_his oh "
                            + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                            + "JOIN opd_service os on odh.service_id = os.service_id "
                            + "set fee6_id = '?' "
                            + "where oh.deleted = 0 and reader_doctor_id = '" + selectedDrId + "'\n"
                            + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                            + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                            + "and if(reader_doctor_id='" + selectedDrId + "',odh.srv_fees6,0) <> 0 and ifnull(odh.fee6_id,'')=''";
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and cat_id in (select opd_cat_id "
                                + "from opd_cus_lab_group_detail a where cus_grp_id = " + groupId + ")";
                    }

                    if (drPaySetting.equals("IPD")) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(if(reader_doctor_id='"
                                + selectedDrId + "',srv_fees6,0),0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and reader_doctor_id = '" + selectedDrId + "' and ifnull(fee6_id,'')='' \n"
                                + " and ifnull(if(reader_doctor_id='" + selectedDrId + "',srv_fees6,0),0) <> 0 \n";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(if(reader_doctor_id='"
                                + selectedDrId + "',srv_fees6,0),0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and reader_doctor_id = '" + selectedDrId + "' and ifnull(fee6_id,'')='' \n"
                                + " and ifnull(if(reader_doctor_id='" + selectedDrId + "',srv_fees6,0),0) <> 0 \n";
                    }
                    break;
                case "OPDMO": //MO Fee Payment
                    strSql = "update opd_his oh "
                            + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                            + "JOIN opd_service os on odh.service_id = os.service_id "
                            + "join opd_category oc on os.cat_id = oc.cat_id "
                            + "set fee2_id = '?' "
                            + "where oh.deleted = 0 \n"
                            + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                            + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                            + "and ifnull(odh.srv_fees2,0) <> 0 and ifnull(odh.fee2_id,'')=''"
                            + " and ifnull(fee2_id,'')='' \n"
                            + " and (IF(IFNULL(odh.mo_id, '') = '',IF(oc.crvf2_ref_dr = 'REFER',"
                            + "odh.refer_doctor_id, IF(oc.crvf2_ref_dr = 'READ', odh.reader_doctor_id,"
                            + "IF(oc.crvf2_ref_dr = 'TECH', odh.tech_id, '-'))),"
                            + "odh.mo_id) = '" + selectedDrId + "' or if(ifnull(odh.mo_id,'-')='','-',ifnull(odh.mo_id,'-')) = crvf2_ref_dr) ";
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and os.cat_id in (select opd_cat_id "
                                + "from opd_cus_lab_group_detail a where cus_grp_id = " + groupId + ")";
                    }

                    if (drPaySetting.equals("IPD")) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(srv_fees2,0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) \n"
                                + "and ifnull(srv_fees2,0) <> 0 \n"
                                + " and ifnull(fee2_id,'')='' "
                                + " and (vdfp.mo_id = '" + selectedDrId + "' or vdfp.mo_id = vdfp.crvf2_ref_dr)";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(srv_fees2,0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) \n"
                                + "and ifnull(srv_fees2,0) <> 0 \n"
                                + " and ifnull(fee2_id,'')='' "
                                + " and (vdfp.mo_id = '" + selectedDrId + "' or vdfp.mo_id = vdfp.crvf2_ref_dr)";
                    }
                    break;
                case "OPDTECH": //Tech Fee Payment
                    if (et.getNeedDr()) {
                        strSql = "update opd_his oh "
                                + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                                + "JOIN opd_service os on odh.service_id = os.service_id "
                                + "set fee4_id = '?' "
                                + "where oh.deleted = 0 and tech_id = '" + selectedDrId + "'\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + "and if(tech_id='" + selectedDrId + "',odh.srv_fees4,0) <> 0 and ifnull(odh.fee4_id,'')=''";
                    } else {
                        strSql = "update opd_his oh "
                                + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                                + "JOIN opd_service os on odh.service_id = os.service_id "
                                + "set fee4_id = '?' "
                                + "where oh.deleted = 0 \n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + "and ifnull(odh.fee4_id,'')=''";
                    }
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and cat_id in (select opd_cat_id "
                                + "from opd_cus_lab_group_detail a where cus_grp_id = " + groupId + ")";
                    }

                    if (drPaySetting.equals("IPD")) {
                        if (et.getNeedDr()) {
                            strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(if(tech_id='"
                                    + selectedDrId + "', srv_fees4,0),0)) as amount, veac.exp_acc_id \n"
                                    + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                    + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                    + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                    + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                    + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                    + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                    + "and tech_id = '" + selectedDrId + "' and ifnull(fee4_id,'')='' \n"
                                    + " and ifnull(if(tech_id='" + selectedDrId + "',srv_fees4,0),0) <> 0 \n"
                                    + "group by source_acc_id, acc_id, dept_code, use_for";
                        } else {
                            strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(srv_fees4,0)) as amount, veac.exp_acc_id \n"
                                    + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                    + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                    + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                    + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                    + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                    + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                    + "and ifnull(fee4_id,'')='' \n"
                                    + " and ifnull(srv_fees4,0) <> 0 \n"
                                    + "group by source_acc_id, acc_id, dept_code, use_for";
                        }
                    } else if (et.getNeedDr()) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(if(tech_id='"
                                + selectedDrId + "', srv_fees4,0),0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and tech_id = '" + selectedDrId + "' and ifnull(fee4_id,'')='' \n"
                                + " and ifnull(if(tech_id='" + selectedDrId + "',srv_fees4,0),0) <> 0 \n";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(srv_fees4,0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and ifnull(fee4_id,'')='' \n"
                                + " and ifnull(srv_fees4,0) <> 0 \n";
                    }
                    break;
                case "OPDSTAFF": //Staff Fee Payment
                    strSql = "update opd_his oh "
                            + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                            + "JOIN opd_service os on odh.service_id = os.service_id "
                            + "join opd_category oc on os.cat_id = oc.cat_id "
                            + "set fee3_id = '?' "
                            + "where oh.deleted = 0 \n"
                            + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                            + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                            + "and ifnull(odh.srv_fees3,0) <> 0 and ifnull(odh.fee3_id,'')=''"
                            + " and (IF(IFNULL(odh.staff_id, '') = '',\n"
                            + "            IF(oc.crvf3_ref_dr = 'REFER',\n"
                            + "                odh.refer_doctor_id,\n"
                            + "                IF(oc.crvf3_ref_dr = 'READ',\n"
                            + "                    odh.reader_doctor_id,\n"
                            + "                    IF(oc.crvf3_ref_dr = 'TECH',\n"
                            + "                        odh.tech_id,\n"
                            + "                        '-'))),\n"
                            + "            odh.staff_id) = '" + selectedDrId + "' or if(ifnull(odh.staff_id,'-')='-','-',ifnull(odh.staff_id,'-')) = crvf3_ref_dr) ";
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and os.cat_id in (select opd_cat_id "
                                + "from opd_cus_lab_group_detail a where cus_grp_id = " + groupId + ")";
                    }

                    if (drPaySetting.equals("IPD")) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(srv_fees3,0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) \n"
                                + " and ifnull(srv_fees3,0) <> 0 \n"
                                + " and ifnull(fee3_id,'')='' "
                                + " and (vdfp.staff_id = '" + selectedDrId + "' or vdfp.staff_id = vdfp.crvf3_ref_dr)";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(srv_fees3,0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(txtFrom.getText())
                                + "' and '" + DateUtil.toDateStrMYSQL(txtTo.getText()) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) \n"
                                + " and ifnull(srv_fees3,0) <> 0 \n"
                                + " and ifnull(fee3_id,'')='' "
                                + " and (vdfp.staff_id = '" + selectedDrId + "' or vdfp.staff_id = vdfp.crvf3_ref_dr)";
                    }
                    break;
            }

            if (!sessionId.equals("0")) {
                strSql = strSql + " and oh.session_id = " + sessionId;
                strSqlExp = strSqlExp + " and session_id = " + sessionId;
            }

            strSqlExp = strSqlExp + " group by source_acc_id, acc_id, dept_code, use_for, veac.exp_acc_id ";

            try {
                String appCurr = Util1.getPropValue("system.app.currency");
                ResultSet rs = dao.execSQL(strSqlExp);

                strSql = strSql.replace("?", vouNo);
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
                            rec.setAccId(rs.getString("acc_id"));
                            rec.setSourceAccId(rs.getString("exp_acc_id"));
                        } else {
                            rec.setAccId(rs.getString("acc_id"));
                            rec.setSourceAccId(rs.getString("source_acc_id"));
                        }
                        rec.setDeptId(rs.getString("dept_code"));
                        rec.setPaidFor(rs.getString("use_for"));
                        rec.setVouNo(vouNo);
                        rec.setExpenseOpotion(et.getExpenseOption());
                        rec.setDoctorId(selectedDrId);
                        rec.setUpp(chkUPP.isSelected());
                        rec.setDeleted(false);
                        rec.setRecLock(Boolean.FALSE);
                        dao.save(rec);

                        uploadToAccount(rec.getGeneId().toString());
                    }

                    printPayment(vouNo);
                }
            } catch (Exception ex) {
                dao.rollBack();
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
            case "OPD":
                switch (et.getSysCode()) {
                    case "OPDCF": //CF Fee Payment
                        reportName = "OPDDoctorCFPayment";
                        break;
                    case "OPDREFER": //Refer Fee Payment
                        reportName = "OPDDoctorReferPayment";
                        break;
                    case "OPDREAD": //Reader Fee Payment
                        reportName = "OPDDoctorReaderPayment";
                        break;
                    case "OPDMO": //MO Fee Payment
                        reportName = "OPDDoctorMOPayment";
                        break;
                    case "OPDTECH": //Tech Fee Payment
                        reportName = "OPDDoctorTechPayment";
                        break;
                    case "OPDSTAFF": //Staff Fee Payemnt
                        reportName = "OPDDoctorStaffPayment";
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
            params.put("p_tran_date", txtTranDate.getText());
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
        txtTtlMo.setValue(0);
        txtTtlReader.setValue(0);
        txtTtlRefer.setValue(0);
        txtTtlStaff.setValue(0);
        txtTtlTech.setValue(0);
        chkUPP.setSelected(false);

        genVouNo();
    }

    private void uploadToAccount(String vouNo) {
        String isIntegration = Util1.getPropValue("system.integration");
        if (isIntegration.toUpperCase().equals("Y")) {
            String rootUrl = Util1.getPropValue("system.intg.api.url");
            if (!rootUrl.isEmpty() && !rootUrl.equals("-")) {
                try (CloseableHttpClient httpClient = createHttpClientWithTimeouts()) {
                    String url = rootUrl + "/expense";
                    final HttpPost request = new HttpPost(url);
                    final List<NameValuePair> params = new ArrayList();
                    params.add(new BasicNameValuePair("vouNo", vouNo));
                    request.setEntity(new UrlEncodedFormEntity(params));
                    CloseableHttpResponse response = httpClient.execute(request);
                    log.info(url + response.toString());
                } catch (IOException e) {
                    log.error("uploadToAccount : " + e.getMessage());
                    updateNull(vouNo);
                }
            }
        } else {
            updateNull(vouNo);
        }
    }

    private void updateNull(String vouNo) {
        try {
            dao.execSql("update gen_expense set intg_upd_status = null where gene_id = '" + vouNo + "'");
        } catch (Exception ex) {
            log.error("uploadToAccount error : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private CloseableHttpClient createHttpClientWithTimeouts() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ONE_MILLISECOND)
                .build();
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
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
        txtTtlRefer = new javax.swing.JFormattedTextField();
        txtTtlReader = new javax.swing.JFormattedTextField();
        txtDoctor = new javax.swing.JTextField();
        txtTtlTech = new javax.swing.JFormattedTextField();
        txtTtlStaff = new javax.swing.JFormattedTextField();
        txtTtlMo = new javax.swing.JFormattedTextField();
        txtTtlAmt = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        butClear = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        txtTotalRecords = new javax.swing.JFormattedTextField();
        txtTranDate = new javax.swing.JFormattedTextField();
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

        txtTtlRefer.setEditable(false);
        txtTtlRefer.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTtlReader.setEditable(false);
        txtTtlReader.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTtlReader.setToolTipText("");

        txtDoctor.setEditable(false);
        txtDoctor.setBorder(javax.swing.BorderFactory.createTitledBorder("Doctor"));
        txtDoctor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDoctorMouseClicked(evt);
            }
        });

        txtTtlTech.setEditable(false);
        txtTtlTech.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTtlStaff.setEditable(false);
        txtTtlStaff.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTtlMo.setEditable(false);
        txtTtlMo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtTtlAmt.setEditable(false);
        txtTtlAmt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalRecords, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlAmt, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlMo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlStaff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlTech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlRefer, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlReader, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDoctor, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboExpenseType, 0, 121, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butSearch)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTranDate, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chkUPP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(butPrint)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTtlAmt, txtTtlMo, txtTtlReader, txtTtlRefer, txtTtlStaff, txtTtlTech});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtTo, txtTranDate});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(butPrint)
                        .addComponent(butSearch)
                        .addComponent(butClear)
                        .addComponent(txtTranDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(chkUPP))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDoctor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboExpenseType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtVouNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboSession, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTtlRefer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlReader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlTech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlStaff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlMo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlAmt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(txtTotalRecords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                genVouNo();
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
    private javax.swing.JFormattedTextField txtTtlMo;
    private javax.swing.JFormattedTextField txtTtlReader;
    private javax.swing.JFormattedTextField txtTtlRefer;
    private javax.swing.JFormattedTextField txtTtlStaff;
    private javax.swing.JFormattedTextField txtTtlTech;
    private javax.swing.JFormattedTextField txtVouNo;
    // End of variables declaration//GEN-END:variables
}
