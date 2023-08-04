/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.helper.OPDDrPayment;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ExpenseType;
import com.cv.app.pharmacy.database.entity.GenExpense;
import com.cv.app.pharmacy.util.GenVouNoImpl;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
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

/**
 *
 * @author WSwe
 */
public class UnPaidPayableTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(UnPaidPayableTableModel.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private List<OPDDrPayment> list = new ArrayList();
    private final String[] columnNames = {"Tran Type", "Tran Date", "Payment Name",
        "Doctor", "Ttl Amount", "Ttl Pay", "Ttl Balance", "Un Paid"};
    private String tranDate;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (list == null) {
            return false;
        }

        if (list.isEmpty()) {
            return false;
        }

        OPDDrPayment record = list.get(row);

        return column == 7 && !record.getPaid();
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Tran Type
                return String.class;
            case 1: //Tran Date
                return Date.class;
            case 2: //Payment Name
                return String.class;
            case 3: //Doctor
                return String.class;
            case 4: //Ttl Amount
                return Double.class;
            case 5: //Ttl Pay
                return Double.class;
            case 6: //Ttl Balance
                return Double.class;
            case 7: //Un Paid
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return null;
        }

        OPDDrPayment record = list.get(row);

        switch (column) {
            case 0: //Tran Type
                return record.getAdmissionNo();
            case 1: //Tran Date
                return record.getTranDate();
            case 2: //Payment Name
                return record.getServiceName();
            case 3: //Doctor
                return record.getPtName();
            case 4: //Ttl Amount
                return record.getAmount();
            case 5: //Ttl Pay
                return record.getMoFee();
            case 6: //Ttl Balance
                return record.getPrice();
            case 7: //Un Paid
                return record.getPaid();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (list == null) {
            return;
        }

        if (list.isEmpty()) {
            return;
        }

        OPDDrPayment record = list.get(row);
        switch (column) {
            case 7: //Un Paid
                record.setPaid((boolean) value);
                if (record.getPaid()) {
                    saveRecord(record);
                }
                break;
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<OPDDrPayment> getList() {
        return list;
    }

    public void setList(List<OPDDrPayment> list) {
        this.list = list;
        fireTableDataChanged();
    }

    public void clear() {
        list = new ArrayList();
        fireTableDataChanged();
    }

    public void setTranDate(String tranDate) {
        this.tranDate = tranDate;
    }

    private void saveRecord(OPDDrPayment record) {
        String tranOption = record.getAdmissionNo();
        switch (tranOption) {
            case "OPD":
                saveOPD(record);
                break;
            case "OT":
                saveOT(record);
                break;
            case "DC":
                saveDC(record);
                break;
        }
    }

    private void saveOPD(OPDDrPayment record) {
        try {
            GenVouNoImpl vouEngine = new GenVouNoImpl(dao, "OPDDRPAY",
                    DateUtil.getPeriod(tranDate));
            String vouNo = vouEngine.getVouNo1();

            ExpenseType et = (ExpenseType) dao.find(ExpenseType.class, record.getQty());
            String strSql = "-";
            String strSqlExp = "-";
            String groupId = et.getCusGroupoId().toString();
            String drPaySetting = Util1.getPropValue("system.drpayment");
            String selectedDrId = record.getRegNo();
            String sessionId = "0";
            String strTmpDate = DateUtil.toDateStr(record.getTranDate());
            boolean isNeedDr = et.getNeedDr();

            if (selectedDrId.equals("-") && isNeedDr) {
                isNeedDr = false;
            }
            /*if (cboSession.getSelectedItem() instanceof Session) {
            sessionId = ((Session) cboSession.getSelectedItem()).getSessionId().toString();
        }*/

            switch (et.getSysCode()) {
                case "OPDCF": //CF Fee Payment
                    if (isNeedDr) {
                        strSql = "update opd_his oh "
                                + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                                + "JOIN opd_service os on odh.service_id = os.service_id "
                                + "set fee1_id = '?' "
                                + "where oh.deleted = 0 and oh.doctor_id = '" + selectedDrId + "'\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and ifnull(fee1_id,'')='' ";
                    } else {
                        strSql = "update opd_his oh "
                                + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                                + "JOIN opd_service os on odh.service_id = os.service_id "
                                + "set fee1_id = '?' "
                                + "where oh.deleted = 0 \n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and ifnull(fee1_id,'')='' ";
                    }
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and cat_id in (select opd_cat_id "
                                + "from opd_cus_lab_group_detail a where cus_grp_id = " + groupId + ")";
                    }

                    if (drPaySetting.equals("IPD")) {
                        if (isNeedDr) {
                            strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.amount,0)) as amount, veac.exp_acc_id \n"
                                    + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id  "
                                    + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                    + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                    + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                    + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                    + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                    + "and doctor_id = '" + selectedDrId + "' and ifnull(fee1_id,'')='' ";
                        } else {
                            strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.amount,0)) as amount, veac.exp_acc_id \n"
                                    + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id  "
                                    + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                    + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                    + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                    + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                    + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                    + " and ifnull(fee1_id,'')='' ";
                        }
                    } else if (isNeedDr) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.amount,0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id  "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and doctor_id = '" + selectedDrId + "' and ifnull(fee1_id,'')='' ";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.amount,0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id  "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + " and ifnull(fee1_id,'')='' ";
                    }
                    break;
                case "OPDREFER": //Refer Fee Payment
                    strSql = "update opd_his oh "
                            + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                            + "JOIN opd_service os on odh.service_id = os.service_id "
                            + "set fee5_id = '?' "
                            + "where oh.deleted = 0 and ifnull(refer_doctor_id,'-') = '" + selectedDrId + "'\n"
                            + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                            + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                            + "and if(ifnull(refer_doctor_id,'-')='" + selectedDrId + "',odh.srv_fees5,0) <> 0 and ifnull(odh.fee5_id,'')=''";
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and cat_id in (select opd_cat_id "
                                + "from opd_cus_lab_group_detail a where cus_grp_id = " + groupId + ")";
                    }

                    if (drPaySetting.equals("IPD")) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(if(ifnull(refer_doctor_id,'-')='"
                                + selectedDrId + "',srv_fees5,0),0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id  "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and ifnull(refer_doctor_id,'-') = '" + selectedDrId + "' and ifnull(fee5_id,'')='' \n"
                                + " and ifnull(if(ifnull(refer_doctor_id,'-')='" + selectedDrId + "',srv_fees5,0),0) <> 0 \n";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(if(ifnull(refer_doctor_id,'-')='"
                                + selectedDrId + "',srv_fees5,0),0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and ifnull(refer_doctor_id,'-') = '" + selectedDrId + "' and ifnull(fee5_id,'')='' \n"
                                + " and ifnull(if(ifnull(refer_doctor_id,'-')='" + selectedDrId + "',srv_fees5,0),0) <> 0 \n";
                    }
                    break;
                case "OPDREAD": //Reader Fee Payment
                    strSql = "update opd_his oh "
                            + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                            + "JOIN opd_service os on odh.service_id = os.service_id "
                            + "set fee6_id = '?' "
                            + "where oh.deleted = 0 and ifnull(reader_doctor_id,'-') = '" + selectedDrId + "'\n"
                            + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                            + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                            + "and if(ifnull(reader_doctor_id,'-')='" + selectedDrId + "',odh.srv_fees6,0) <> 0 and ifnull(odh.fee6_id,'')=''";
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and cat_id in (select opd_cat_id "
                                + "from opd_cus_lab_group_detail a where cus_grp_id = " + groupId + ")";
                    }

                    if (drPaySetting.equals("IPD")) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(if(ifnull(reader_doctor_id,'-')='"
                                + selectedDrId + "',srv_fees6,0),0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and ifnull(reader_doctor_id,'-') = '" + selectedDrId + "' and ifnull(fee6_id,'')='' \n"
                                + " and ifnull(if(ifnull(reader_doctor_id,'-')='" + selectedDrId + "',srv_fees6,0),0) <> 0 \n";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(if(ifnull(reader_doctor_id,'-')='"
                                + selectedDrId + "',srv_fees6,0),0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and ifnull(reader_doctor_id,'-') = '" + selectedDrId + "' and ifnull(fee6_id,'')='' \n"
                                + " and ifnull(if(ifnull(reader_doctor_id,'-')='" + selectedDrId + "',srv_fees6,0),0) <> 0 \n";
                    }
                    break;
                case "OPDMO": //MO Fee Payment
                    strSql = "update opd_his oh "
                            + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                            + "JOIN opd_service os on odh.service_id = os.service_id "
                            + "join opd_category oc on os.cat_id = oc.cat_id "
                            + "set fee2_id = '?' "
                            + "where oh.deleted = 0 \n"
                            + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                            + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
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
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) \n"
                                + "and ifnull(srv_fees2,0) <> 0 \n"
                                + " and ifnull(fee2_id,'')='' "
                                + " and (vdfp.mo_id = '" + selectedDrId + "' or vdfp.mo_id = vdfp.crvf2_ref_dr)";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(srv_fees2,0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) \n"
                                + "and ifnull(srv_fees2,0) <> 0 \n"
                                + " and ifnull(fee2_id,'')='' "
                                + " and (vdfp.mo_id = '" + selectedDrId + "' or vdfp.mo_id = vdfp.crvf2_ref_dr)";
                    }
                    break;
                case "OPDTECH": //Tech Fee Payment
                    strSql = "update opd_his oh "
                            + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                            + "JOIN opd_service os on odh.service_id = os.service_id "
                            + "set fee4_id = '?' "
                            + "where oh.deleted = 0 and tech_id = '" + selectedDrId + "'\n"
                            + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                            + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                            + "and if(tech_id='" + selectedDrId + "',odh.srv_fees4,0) <> 0 and ifnull(odh.fee4_id,'')=''";
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and cat_id in (select opd_cat_id "
                                + "from opd_cus_lab_group_detail a where cus_grp_id = " + groupId + ")";
                    }

                    if (drPaySetting.equals("IPD")) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(if(tech_id='"
                                + selectedDrId + "', srv_fees4,0),0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and tech_id = '" + selectedDrId + "' and ifnull(fee4_id,'')='' \n"
                                + " and ifnull(if(tech_id='" + selectedDrId + "',srv_fees4,0),0) <> 0 \n"
                                + "group by source_acc_id, acc_id, dept_code, use_for";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(if(tech_id='"
                                + selectedDrId + "', srv_fees4,0),0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and tech_id = '" + selectedDrId + "' and ifnull(fee4_id,'')='' \n"
                                + " and ifnull(if(tech_id='" + selectedDrId + "',srv_fees4,0),0) <> 0 \n";
                    }
                    break;
                case "OPDSTAFF": //Staff Fee Payment
                    strSql = "update opd_his oh "
                            + "JOIN opd_details_his odh on oh.opd_inv_id = odh.vou_no "
                            + "JOIN opd_service os on odh.service_id = os.service_id "
                            + "join opd_category oc on os.cat_id = oc.cat_id "
                            + "set fee3_id = '?' "
                            + "where oh.deleted = 0 \n"
                            + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                            + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
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
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and cat_id in (select opd_cat_id from opd_cus_lab_group_detail a where cus_grp_id = veac.cus_group_id) \n"
                                + " and ifnull(srv_fees3,0) <> 0 \n"
                                + " and ifnull(fee3_id,'')='' "
                                + " and (vdfp.staff_id = '" + selectedDrId + "' or vdfp.staff_id = vdfp.crvf3_ref_dr)";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(srv_fees3,0)) as amount, veac.exp_acc_id \n"
                                + "from v_opd_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(opd_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
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

            ResultSet rs = dao.execSQL(strSqlExp);
            if (rs != null) {
                //dao.open();
                //dao.beginTran();
                while (rs.next()) {
                    GenExpense rec = new GenExpense();
                    rec.setExpDate(DateUtil.toDate(tranDate));
                    rec.setExpType(et);
                    rec.setDesp(selectedDrId + " - " + "Unpaid Payment"
                            + " - (" + strTmpDate + " to " + strTmpDate + ")");
                    rec.setRemark(vouNo + " " + rs.getString("use_for") + " "
                            + Global.loginUser.getUserName());
                    rec.setCreatedBy(Global.loginUser.getUserId());
                    rec.setSession(Global.sessionId);
                    rec.setCreatedDate(new Date());
                    rec.setAmount(rs.getDouble("amount"));
                    //if (chkUPP.isSelected()) {
                    rec.setAccId(rs.getString("acc_id"));
                    rec.setSourceAccId(rs.getString("exp_acc_id"));
                    /*} else {
                        rec.setAccId(rs.getString("acc_id"));
                        rec.setSourceAccId(rs.getString("source_acc_id"));
                    }*/
                    rec.setDeptId(rs.getString("dept_code"));
                    rec.setPaidFor(rs.getString("use_for"));
                    rec.setVouNo(vouNo);
                    rec.setExpenseOpotion(et.getExpenseOption());
                    rec.setDoctorId(selectedDrId);
                    rec.setUpp(true);
                    rec.setDeleted(false);

                    dao.save(rec);
                    uploadToAccount(rec.getGeneId().toString());
                }
                strSql = strSql.replace("?", vouNo);
                dao.execSql(strSql);
                //dao.commit();
                vouEngine.updateVouNo();
            }

        } catch (Exception ex) {
            //dao.rollBack();
            log.error("save : " + ex.toString());
        } finally {
            dao.close();
        }

    }

    private void saveOT(OPDDrPayment record) {
        try {
            GenVouNoImpl vouEngine = new GenVouNoImpl(dao, "OTDRPAY",
                    DateUtil.getPeriod(tranDate));
            String vouNo = vouEngine.getVouNo1();

            ExpenseType et = (ExpenseType) dao.find(ExpenseType.class, record.getQty());
            String strSql = "-";
            String strSqlExp = "-";
            String groupId = et.getCusGroupoId().toString();
            String drPaySetting = Util1.getPropValue("system.drpayment");
            String selectedDrId = record.getRegNo();
            String sessionId = "0";
            String strTmpDate = DateUtil.toDateStr(record.getTranDate());
            boolean isNeedDr = et.getNeedDr();

            if (selectedDrId.equals("-") && isNeedDr) {
                isNeedDr = false;
            }
            /*if (cboSession.getSelectedItem() instanceof Session) {
                sessionId = ((Session) cboSession.getSelectedItem()).getSessionId().toString();
            }*/

            switch (et.getSysCode()) {
                case "OTDR": //CF Fee Payment
                    if (isNeedDr) {
                        strSql = "update ot_his oh \n"
                                + "JOIN ot_details_his odh on oh.ot_inv_id = odh.vou_no \n"
                                + "JOIN ot_service os on odh.service_id = os.service_id \n"
                                + "join ot_doctor_fee odf on odh.ot_detail_id = odf.ot_detail_id \n"
                                + "set odf.pay_id = '?', odf.pay_amt = if(ifnull(odh.amount,0)=0,0,(ifnull(odh.qty,0)*ifnull(odf.dr_fee,0))) \n"
                                + "where oh.deleted = 0 and odf.doctor_id = '" + selectedDrId + "'\n"
                                + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and ifnull(odf.pay_id,'')='' and ifnull(odf.dr_fee,0) <> 0 ";
                    } else {
                        strSql = "update ot_his oh \n"
                                + "JOIN ot_details_his odh on oh.ot_inv_id = odh.vou_no \n"
                                + "JOIN ot_service os on odh.service_id = os.service_id \n"
                                + "set odh.pay_id1 = '?', odh.fee1_pay_amt = ifnull(odh.amount,0) \n"
                                + "where oh.deleted = 0 \n"
                                + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and ifnull(odh.pay_id1,'')='' ";
                    }
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and os.group_id in (select ot_cat_id from ot_cus_group_detail where cus_grp_id = "
                                + groupId + ")";
                    }

                    if (isNeedDr) {
                        if (drPaySetting.equals("IPD")) {
                            strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.dr_fee,0)) as amount, veac.exp_acc_id \n"
                                    + "from v_ot_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                    + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                    + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                    + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                    + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                    + " and group_id in (select ot_cat_id from ot_cus_group_detail a where cus_grp_id = veac.cus_group_id) "
                                    + "and pay_dr_id = '" + selectedDrId + "' and ifnull(pay_id,'')='' ";
                        } else {
                            strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.dr_fee,0)) as amount, veac.exp_acc_id\n"
                                    + "from v_ot_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                    + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                    + "where vdfp.payable_acc_opt = veac.use_for\n"
                                    + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                    + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                    + " and group_id in (select ot_cat_id from ot_cus_group_detail a where cus_grp_id = veac.cus_group_id) "
                                    + "and pay_dr_id = '" + selectedDrId + "' and ifnull(pay_id,'')='' ";
                        }
                    } else if (drPaySetting.equals("IPD")) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.amount,0)) as amount, veac.exp_acc_id\n"
                                + "from v_ot_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and group_id in (select ot_cat_id from ot_cus_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + " and ifnull(pay_id1,'')='' ";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.amount,0)) as amount, veac.exp_acc_id\n"
                                + "from v_ot_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and group_id in (select ot_cat_id from ot_cus_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + " and ifnull(pay_id1,'')='' ";
                    }
                    break;
                case "OTSTAFF": //OT Staff Payment
                    strSql = "update ot_his oh \n"
                            + "JOIN ot_details_his odh on oh.ot_inv_id = odh.vou_no \n"
                            + "JOIN ot_service os on odh.service_id = os.service_id \n"
                            + "set odh.pay_id2 = '?', odh.fee2_pay_amt = if(ifnull(odh.amount,0)=0,0,(ifnull(odh.qty,0)*ifnull(odh.srv_fee2,0))) \n"
                            + "where oh.deleted = 0 \n"
                            + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                            + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                            + " and ifnull(odh.pay_id2,'')='' and ifnull(odh.srv_fee2,0) <> 0 ";
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and os.group_id in (select ot_cat_id from ot_cus_group_detail where cus_grp_id = "
                                + groupId + ")";
                    }

                    if (drPaySetting.equals("IPD")) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.srv_fee2,0)) as amount, veac.exp_acc_id\n"
                                + "from v_ot_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and group_id in (select ot_cat_id from ot_cus_group_detail a where cus_grp_id = veac.cus_group_id) \n"
                                + "and ifnull(pay_id2,'')='' ";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.srv_fee2,0)) as amount, veac.exp_acc_id\n"
                                + "from v_ot_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and group_id in (select ot_cat_id from ot_cus_group_detail a where cus_grp_id = veac.cus_group_id) \n"
                                + "and ifnull(pay_id2,'')='' ";
                    }
                    break;
                case "OTNURSE": //OT Nurse Payment
                    strSql = "update ot_his oh \n"
                            + "JOIN ot_details_his odh on oh.ot_inv_id = odh.vou_no \n"
                            + "JOIN ot_service os on odh.service_id = os.service_id \n"
                            + "set odh.pay_id3 = '?', odh.fee3_pay_amt = if(ifnull(odh.amount,0)=0,0,(ifnull(odh.qty,0)*ifnull(odh.srv_fee3,0))) \n"
                            + "where oh.deleted = 0 \n"
                            + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                            + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                            + " and ifnull(odh.pay_id3,'')='' and ifnull(odh.srv_fee3,0) <> 0 ";
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and os.group_id in (select ot_cat_id from ot_cus_group_detail where cus_grp_id = "
                                + groupId + ")";
                    }

                    if (drPaySetting.equals("IPD")) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.srv_fee3,0)) as amount, veac.exp_acc_id\n"
                                + "from v_ot_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and group_id in (select ot_cat_id from ot_cus_group_detail a where cus_grp_id = veac.cus_group_id) \n"
                                + "and ifnull(pay_id3,'')='' ";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.srv_fee3,0)) as amount, veac.exp_acc_id\n"
                                + "from v_ot_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and group_id in (select ot_cat_id from ot_cus_group_detail a where cus_grp_id = veac.cus_group_id) \n"
                                + "and ifnull(pay_id3,'')='' ";
                    }
                    break;
                case "OTMO": //OT MO Payment
                    strSql = "update ot_his oh \n"
                            + "JOIN ot_details_his odh on oh.ot_inv_id = odh.vou_no \n"
                            + "JOIN ot_service os on odh.service_id = os.service_id \n"
                            + "set odh.pay_id4 = '?', odh.fee4_pay_amt = if(ifnull(odh.amount,0)=0,0,(ifnull(odh.qty,0)*ifnull(odh.srv_fee4,0))) \n"
                            + "where oh.deleted = 0 \n"
                            + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                            + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                            + " and ifnull(odh.pay_id4,'')='' and ifnull(odh.srv_fee4,0) <> 0 ";
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and os.group_id in (select ot_cat_id from ot_cus_group_detail where cus_grp_id = "
                                + groupId + ")";
                    }

                    if (drPaySetting.equals("IPD")) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.srv_fee4,0)) as amount, veac.exp_acc_id\n"
                                + "from v_ot_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt_adm = veac.use_for\n"
                                + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and group_id in (select ot_cat_id from ot_cus_group_detail a where cus_grp_id = veac.cus_group_id) \n"
                                + "and ifnull(pay_id4,'')='' ";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.srv_fee4,0)) as amount, veac.exp_acc_id\n"
                                + "from v_ot_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(ot_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and group_id in (select ot_cat_id from ot_cus_group_detail a where cus_grp_id = veac.cus_group_id) \n"
                                + "and ifnull(pay_id4,'')='' ";
                    }
                    break;
            }

            if (!sessionId.equals("0")) {
                strSql = strSql + " and oh.session_id = " + sessionId;
                strSqlExp = strSqlExp + " and session_id = " + sessionId;
            }

            strSqlExp = strSqlExp + " group by source_acc_id, acc_id, dept_code, use_for, veac.exp_acc_id";

            ResultSet rs = dao.execSQL(strSqlExp);
            if (rs != null) {
                //dao.open();
                //dao.beginTran();
                while (rs.next()) {
                    GenExpense rec = new GenExpense();
                    rec.setExpDate(DateUtil.toDate(tranDate));
                    rec.setExpType(et);
                    rec.setDesp(selectedDrId + " - " + "Unpaid Payment"
                            + " - (" + strTmpDate + " to " + strTmpDate + ")");
                    rec.setRemark(vouNo + " " + rs.getString("use_for") + " "
                            + Global.loginUser.getUserName());
                    rec.setCreatedBy(Global.loginUser.getUserId());
                    rec.setSession(Global.sessionId);
                    rec.setCreatedDate(new Date());
                    rec.setAmount(rs.getDouble("amount"));
                    rec.setAccId(rs.getString("acc_id"));
                    rec.setSourceAccId(rs.getString("exp_acc_id"));
                    rec.setDeptId(rs.getString("dept_code"));
                    rec.setPaidFor(rs.getString("use_for"));
                    rec.setVouNo(vouNo);
                    rec.setExpenseOpotion(et.getExpenseOption());
                    rec.setDoctorId(selectedDrId);
                    rec.setUpp(Boolean.TRUE);
                    rec.setDeleted(false);
                    dao.save(rec);
                    uploadToAccount(rec.getGeneId().toString());
                }
            }

            strSql = strSql.replace("?", vouNo);
            log.info("Save : " + strSql);
            dao.execSql(strSql);
            //dao.commit();
            vouEngine.updateVouNo();
        } catch (Exception ex) {
            //dao.rollBack();
            log.error("save : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void saveDC(OPDDrPayment record) {
        try {
            GenVouNoImpl vouEngine = new GenVouNoImpl(dao, "DCDRPAY",
                    DateUtil.getPeriod(tranDate));
            String vouNo = vouEngine.getVouNo1();

            ExpenseType et = (ExpenseType) dao.find(ExpenseType.class, record.getQty());
            String strSql = "-";
            String strSqlExp = "-";
            String groupId = et.getCusGroupoId().toString();
            String selectedDrId = record.getRegNo();
            String sessionId = "0";
            String strTmpDate = DateUtil.toDateStr(record.getTranDate());
            boolean isNeedDr = et.getNeedDr();

            if (selectedDrId.equals("-") && isNeedDr) {
                isNeedDr = false;
            }
            /*if (cboSession.getSelectedItem() instanceof Session) {
                sessionId = ((Session) cboSession.getSelectedItem()).getSessionId().toString();
            }*/

            switch (et.getSysCode()) {
                case "DCDR": //DC Doctor Fee Payment
                    if (isNeedDr) {
                        strSql = "update dc_his oh \n"
                                + "JOIN dc_details_his odh on oh.dc_inv_id = odh.vou_no \n"
                                + "JOIN inp_service os on odh.service_id = os.service_id \n"
                                + "join dc_doctor_fee odf on odh.dc_detail_id = odf.dc_detail_id \n"
                                + "set odf.pay_id = '?', odf.pay_amt = if(ifnull(odh.amount,0)=0,0,(ifnull(odh.qty,0)*ifnull(odf.dr_fee,0))) \n"
                                + "where oh.deleted = 0 and odf.doctor_id = '" + selectedDrId + "'\n"
                                + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and ifnull(odf.pay_id,'')='' ";
                    } else {
                        strSql = "update dc_his oh \n"
                                + "JOIN dc_details_his odh on oh.dc_inv_id = odh.vou_no \n"
                                + "JOIN inp_service os on odh.service_id = os.service_id \n"
                                + "set odh.pay_id1 = '?', odh.fee1_pay_amt = ifnull(odh.amount,0) \n"
                                + "where oh.deleted = 0 \n"
                                + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and ifnull(odh.pay_id1,'')='' ";
                    }
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and os.cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id ="
                                + groupId + ")";
                    }

                    if (isNeedDr) {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.dr_fee,0)) as amount, veac.exp_acc_id\n"
                                + "from v_dc_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                                + " and cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id = veac.cus_group_id) "
                                + "and pay_dr_id = '" + selectedDrId + "' and ifnull(pay_id,'')='' ";
                    } else {
                        strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.amount,0)) as amount, veac.exp_acc_id\n"
                                + "from v_dc_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                                + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                                + "where vdfp.payable_acc_opt = veac.use_for\n"
                                + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                                + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
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
                            + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                            + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                            + " and ifnull(odh.pay_id2,'')='' and ifnull(odh.srv_fee2,0) <> 0 ";
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and os.cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id ="
                                + groupId + ")";
                    }

                    strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.srv_fee2,0)) as amount, veac.exp_acc_id\n"
                            + "from v_dc_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                            + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                            + "where vdfp.payable_acc_opt = veac.use_for\n"
                            + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                            + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                            + " and cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id = veac.cus_group_id) "
                            + "and ifnull(pay_id2,'')='' ";

                    break;
                case "DCTECH":
                    strSql = "update dc_his oh \n"
                            + "JOIN dc_details_his odh on oh.dc_inv_id = odh.vou_no \n"
                            + "JOIN inp_service os on odh.service_id = os.service_id \n"
                            + "set odh.pay_id3 = '?', odh.fee3_pay_amt = if(ifnull(odh.amount,0)=0,0,(ifnull(odh.qty,0)*ifnull(odh.srv_fee3,0))) \n"
                            + "where oh.deleted = 0 \n"
                            + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                            + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                            + " and ifnull(odh.pay_id3,'')='' and ifnull(odh.srv_fee3,0) <> 0 ";
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and os.cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id ="
                                + groupId + ")";
                    }

                    strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.srv_fee3,0)) as amount, veac.exp_acc_id\n"
                            + "from v_dc_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                            + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                            + "where vdfp.payable_acc_opt = veac.use_for\n"
                            + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                            + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                            + " and cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id = veac.cus_group_id) "
                            + "and ifnull(pay_id3,'')='' ";

                    break;
                case "DCMO":
                    strSql = "update dc_his oh \n"
                            + "JOIN dc_details_his odh on oh.dc_inv_id = odh.vou_no \n"
                            + "JOIN inp_service os on odh.service_id = os.service_id \n"
                            + "set odh.pay_id4 = '?', odh.fee4_pay_amt = if(ifnull(odh.amount,0)=0,0,(ifnull(odh.qty,0)*ifnull(odh.srv_fee4,0))) \n"
                            + "where oh.deleted = 0 \n"
                            + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                            + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                            + " and ifnull(odh.pay_id4,'')='' and ifnull(odh.srv_fee4,0) <> 0 ";
                    if (!groupId.equals("-")) {
                        strSql = strSql + " and os.cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id ="
                                + groupId + ")";
                    }

                    strSqlExp = "select source_acc_id, acc_id, dept_code, use_for, sum(ifnull(vdfp.srv_fee4,0)) as amount, veac.exp_acc_id\n"
                            + "from v_dc_dr_fee_payment vdfp, (select cus_group_id, source_acc_id, acc_id, dept_code, use_for, exp_acc_id "
                            + "from v_expense_acc where expense_type_id = " + et.getExpenseId().toString() + ") veac\n"
                            + "where vdfp.payable_acc_opt = veac.use_for\n"
                            + " and date(dc_date) between '" + DateUtil.toDateStrMYSQL(strTmpDate)
                            + "' and '" + DateUtil.toDateStrMYSQL(strTmpDate) + "'\n"
                            + " and cat_id in (select dc_cat_id from dc_cus_group_detail a where cus_grp_id = veac.cus_group_id) "
                            + "and ifnull(pay_id4,'')='' ";

                    break;
            }

            if (!sessionId.equals("0")) {
                strSql = strSql + " and oh.session_id = " + sessionId;
                strSqlExp = strSqlExp + " and session_id = " + sessionId;
            }

            strSqlExp = strSqlExp + " group by source_acc_id, acc_id, dept_code, use_for, veac.exp_acc_id";

            ResultSet rs = dao.execSQL(strSqlExp);
            if (rs != null) {
                //dao.open();
                //dao.beginTran();
                while (rs.next()) {
                    GenExpense rec = new GenExpense();
                    rec.setExpDate(DateUtil.toDate(tranDate));
                    rec.setExpType(et);
                    rec.setDesp(selectedDrId + " - " + "Unpaid Payment"
                            + " - (" + strTmpDate + " to " + strTmpDate + ")");
                    rec.setRemark(vouNo + " " + rs.getString("use_for") + " "
                            + Global.loginUser.getUserName());
                    rec.setCreatedBy(Global.loginUser.getUserId());
                    rec.setSession(Global.sessionId);
                    rec.setCreatedDate(new Date());
                    rec.setAmount(rs.getDouble("amount"));
                    rec.setAccId(rs.getString("acc_id"));
                    rec.setSourceAccId(rs.getString("exp_acc_id"));
                    rec.setDeptId(rs.getString("dept_code"));
                    rec.setPaidFor(rs.getString("use_for"));
                    rec.setVouNo(vouNo);
                    rec.setExpenseOpotion(et.getExpenseOption());
                    rec.setDoctorId(selectedDrId);
                    rec.setUpp(Boolean.TRUE);
                    rec.setDeleted(false);
                    dao.save(rec);
                    uploadToAccount(rec.getGeneId().toString());
                }
            }

            strSql = strSql.replace("?", vouNo);
            log.info("Save : " + strSql);
            dao.execSql(strSql);
            //dao.commit();
            vouEngine.updateVouNo();
        } catch (Exception ex) {
            //dao.rollBack();
            log.error("save : " + ex.toString());
        } finally {
            dao.close();
        }
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
                    params.add(new BasicNameValuePair("expId", vouNo));
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

}
