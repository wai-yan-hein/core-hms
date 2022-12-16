/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.entry;

import com.cv.app.common.Global;
import com.cv.app.common.StartWithRowFilter;
import com.cv.app.inpatient.database.entity.Ams;
import com.cv.app.inpatient.database.healper.CurrPTBalance;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.inpatient.database.healper.CurrPTBalanceTran;
import com.cv.app.inpatient.ui.common.CurrPTBalanceTableModel;
import com.cv.app.inpatient.ui.common.CurrPTBalanceTranTableModel;
import com.cv.app.inpatient.ui.common.ErrorVouTableModel;
import com.cv.app.inpatient.ui.common.GroupTotalTableModel;
import com.cv.app.inpatient.ui.common.PaymentTableModel;
import com.cv.app.opd.database.entity.PatientBillPayment;
import com.cv.app.pharmacy.database.helper.SessionTtl;
import com.cv.app.pharmacy.database.tempentity.TmpBillPayment;
import com.cv.app.pharmacy.ui.common.PatientBillTableModel;
import com.cv.app.ui.common.TableDateFieldRenderer;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.ReportUtil;
import com.cv.app.util.Util1;
import java.awt.Cursor;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.apache.log4j.Logger;

/**
 *
 * @author ACER
 */
public class DCPatientBalance extends javax.swing.JPanel {

    static Logger log = Logger.getLogger(DCPatientBalance.class.getName());
    private final CurrPTBalanceTableModel ptBalTableModel = new CurrPTBalanceTableModel();
    private final CurrPTBalanceTranTableModel ptBalTranTableModel = new CurrPTBalanceTranTableModel();
    private final PaymentTableModel paymentTableModel = new PaymentTableModel();
    private final ErrorVouTableModel errVouTableModel = new ErrorVouTableModel();
    private final AbstractDataAccess dao = Global.dao;
    private final GroupTotalTableModel gtTablemodel = new GroupTotalTableModel();
    private final StartWithRowFilter swrfPatient;
    private final TableRowSorter<TableModel> sorterPatient;
    private final StartWithRowFilter swrfTran;
    private final TableRowSorter<TableModel> sorterTran;
    private final PatientBillTableModel tblPatientBillTableModel = new PatientBillTableModel();

    /**
     * Creates new form DCPatientBalance
     */
    public DCPatientBalance() {
        initComponents();
        initTable();

        swrfPatient = new StartWithRowFilter(txtPtFilter);
        sorterPatient = new TableRowSorter(tblPtList.getModel());
        tblPtList.setRowSorter(sorterPatient);

        swrfTran = new StartWithRowFilter(txtTranFilter);
        sorterTran = new TableRowSorter(tblTran.getModel());
        tblTran.setRowSorter(sorterTran);
        butPrintBill.setVisible(false);
    }

    private void initTable() {
        getBalance();

        tblPtList.getTableHeader().setFont(Global.lableFont);
        tblPtList.getColumnModel().getColumn(0).setPreferredWidth(20); //Reg No
        tblPtList.getColumnModel().getColumn(1).setPreferredWidth(20); //Adm No
        tblPtList.getColumnModel().getColumn(2).setPreferredWidth(150); //Patient
        tblPtList.getColumnModel().getColumn(3).setPreferredWidth(50); //Balance
        tblPtList.getColumnModel().getColumn(4).setPreferredWidth(5); //E
        tblPtList.getColumnModel().getColumn(5).setPreferredWidth(5); //P

        tblPtList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPtList.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                if (!chkError.isSelected()) {
                    if (e.getValueIsAdjusting()) {
                        txtDB.setValue(0);
                        if (tblPtList.getSelectedRow() >= 0) {
                            int selectRow = tblPtList.convertRowIndexToModel(tblPtList.getSelectedRow());
                            log.info("select : " + e.getValueIsAdjusting() + " - " + selectRow);
                            CurrPTBalance cpb = ptBalTableModel.getPatientBalance(selectRow);

                            if (cpb != null) {
                                txtAdmNo.setText(cpb.getAdmNo());
                                getBillInfo();
                            } else {
                                clear();
                            }
                        }
                    }
                }
            }
        });

        tblTran.getTableHeader().setFont(Global.lableFont);
        tblTran.getColumnModel().getColumn(0).setPreferredWidth(20); //Tran Type
        tblTran.getColumnModel().getColumn(1).setPreferredWidth(20); //Tran Date
        tblTran.getColumnModel().getColumn(2).setPreferredWidth(50); //Vou No
        tblTran.getColumnModel().getColumn(3).setPreferredWidth(250); //Item Name
        tblTran.getColumnModel().getColumn(4).setPreferredWidth(20); //Qty
        tblTran.getColumnModel().getColumn(5).setPreferredWidth(40); //Price
        tblTran.getColumnModel().getColumn(6).setPreferredWidth(50); //Amount
        tblTran.getColumnModel().getColumn(1).setCellRenderer(new TableDateFieldRenderer());

        tblErrorVoucher.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
        tblPayment.getColumnModel().getColumn(0).setCellRenderer(new TableDateFieldRenderer());
    }

    private void getBalance() {
        ptBalTableModel.getData1();
        txtTtlBal.setValue(ptBalTableModel.getTotal());
        txtTotalRecords.setValue(ptBalTableModel.getCount());
    }

    private void getBillInfo() {
        getBillDetailInfo();

        String regNo = txtRegNo.getText().trim();
        String admissionNo = txtAdmNo.getText().trim();
        String admDate = DateUtil.toDateStrMYSQL(txtAdmDate.getText());
        String tranDate = DateUtil.getTodayDateStrMYSQL();

        getPatientPayment(regNo, admissionNo, admDate, tranDate);
        getErrorVou(regNo, admissionNo, admDate, tranDate);
        getPatientBill(regNo);

        double tmpBal1 = NumberUtil.NZero(txtPBTotal.getText());
        double tmpTtlExp = NumberUtil.NZero(txtTtlExpense.getText());
        double tmpTtlPay = NumberUtil.NZero(txtTotalPayment.getText());
        double tmpDiff = tmpBal1 - (tmpTtlExp - tmpTtlPay);
        txtDB.setValue(tmpDiff);
    }

    private void getBillDetailInfo() {
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
                        txtAdmDate.setText(DateUtil.toDateTimeStr(ams.getAmsDate(), "dd/MM/yyyy"));
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
                                + "and date(sh.sale_date) between '" + admitDate + "' and '" + tranDate + "'\n"
                                + "union all \n"
                                + "select 'Return In' as tran_option, date(rih.ret_in_date) as tran_date, rih.ret_in_id as vou_no,\n"
                                + "concat(ridh.med_id, '-', med.med_name) as item_name, concat(ridh.ret_in_qty,ridh.item_unit) as qty,\n"
                                + "ridh.ret_in_price as price, (ridh.ret_in_amount*-1) as amount, ridh.unique_id, 'Return In' as group_name\n"
                                + "from ret_in_his rih, ret_in_detail_his ridh, medicine med\n"
                                + "where rih.deleted = false and rih.ret_in_id = ridh.vou_no\n"
                                + "and ridh.med_id = med.med_id and rih.reg_no = '" + regNo + "' and rih.admission_no = '" + admNo + "' \n"
                                + "and date(rih.ret_in_date) between '" + admitDate + "' and '" + tranDate + "' "
                                + ") a \n"
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
        txtAdmDate.setText("");
        txtTranFilter.setText(null);
        txtTtlExpense.setValue(0);
        txtTotalPayment.setValue(0);
        txtDB.setValue(0);
        txtPBTotal.setValue(0);
        ptBalTranTableModel.clear();
        gtTablemodel.clear();
        paymentTableModel.clear();
        tblPatientBillTableModel.clear();
        errVouTableModel.clear();
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
                + "'OPD Discount' as item_name, 1 as qty, oh.disc_a as price , ifnull(oh.disc_a,0) as amount, 1 as unique_id, 'OPD' as group_name \n"
                + "from opd_his oh\n"
                + "where oh.deleted = false and  oh.patient_id = '" + regNo + "' and oh.admission_no = '" + admNo + "' \n"
                + "and date(oh.opd_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                + "union all\n"
                + "select 'OPD' as tran_option,date(oh.opd_date) as tran_date, oh.opd_inv_id as vou_no,\n"
                + "'OPD Paid' as item_name, 1 as qty, oh.disc_a as price , ifnull(oh.disc_a,0) as amount, 1 as unique_id, 'OPD' as group_name \n"
                + "from opd_his oh\n"
                + "where oh.deleted = false and  oh.patient_id = '" + regNo + "' and oh.admission_no = '" + admNo + "' \n"
                + "and date(oh.opd_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                + "union all\n"
                + "select concat('Pharmacy-',loc.location_name) as tran_option,date(sh.sale_date) as tran_date, sh.sale_inv_id as vou_no, \n"
                + "'Pharmacy Paid' as item_name, 1 as qty, sh.paid_amount as price, \n"
                + "ifnull(sh.paid_amount,0) as amount, 1 as unique_id, 'Pharmacy' as group_name \n"
                + "from sale_his sh, location loc \n"
                + "where sh.location_id = loc.location_id \n"
                + "and sh.deleted = false and sh.reg_no = '" + regNo + "' and sh.admission_no = '" + admNo + "' \n"
                + "and date(sh.sale_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                + "union all\n"
                + "select concat('Pharmacy-',loc.location_name) as tran_option,date(sh.sale_date) as tran_date, sh.sale_inv_id as vou_no, \n"
                + "'Pharmacy Discount' as item_name, 1 as qty, sh.discount as price, \n"
                + "ifnull(sh.discount,0) as amount, 1 as unique_id, 'Pharmacy' as group_name \n"
                + "from sale_his sh, location loc \n"
                + "where sh.location_id = loc.location_id \n"
                + "and sh.deleted = false and sh.reg_no = '" + regNo + "' and sh.admission_no = '" + admNo + "' \n"
                + "and date(sh.sale_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                + "union all\n"
                + "select 'Bill Payment' as tran_option, pay_date as tran_date, remark as vou_no,\n"
                + "'Bill Payment' as item_name, 1 as qty, (ifnull(pay_amt,0)+ifnull(discount,0)) as price,\n"
                + "(ifnull(pay_amt,0)+ifnull(discount,0)) as amount, 1 as unique_id, 'Bill Payment' as group_name\n"
                + "from v_opd_patient_bill_payment\n"
                + "where deleted = false and reg_no = '" + regNo + "' and admission_no = '" + admNo + "' \n"
                + "and pay_date between '" + admitDate + "' and '" + tranDate + "' \n"
                + ") a \n"
                + " where a.amount <> 0 \n"
                + "order by a.group_name, a.tran_option, a.tran_date, a.vou_no, a.unique_id";
        //log.info("strSql : " + strSql);

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                List<CurrPTBalanceTran> list = new ArrayList();
                double ttlAmt = 0;
                while (rs.next()) {
                    double amt = NumberUtil.roundTo(rs.getDouble("amount"), 0);
                    CurrPTBalanceTran cptbt = new CurrPTBalanceTran(
                            rs.getString("tran_option"),
                            rs.getDate("tran_date"),
                            rs.getString("vou_no"),
                            rs.getString("item_name"),
                            rs.getString("qty"),
                            rs.getDouble("price"),
                            amt
                    );

                    list.add(cptbt);
                    ttlAmt += amt;
                }

                txtTotalPayment.setValue(ttlAmt);
                paymentTableModel.setList(list);
            }
        } catch (Exception ex) {
            log.error("getPatientPayment : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private void getErrorVou(String regNo, String admNo, String admitDate, String tranDate) {
        String strSql = "select * \n"
                + "  from (select 'Sale' as tran_option, 'Sale - Balance Error' as err_desp, date(sale_date) as tran_date, \n"
                + "			   sale_inv_id as vou_no, vou_total, sum(sale_amount) as detail_total, balance, sale_exp_total as ttl_exp,\n"
                + "			   paid_amount as paid, tax_amt, discount\n"
                + "		  from v_sale1 \n"
                + "		 where deleted = false and reg_no = '@regNo' and admission_no = '@admNo' and date(sale_date) between '@from' and '@to'\n"
                + "	     group by date(sale_date), sale_inv_id, vou_total, balance, sale_exp_total, paid_amount, tax_amt) a \n"
                + " where (a.vou_total <> a.detail_total or a.balance <> (a.detail_total+a.ttl_exp+a.tax_amt-a.paid-a.discount))\n"
                + " union all\n"
                + "select * \n"
                + "  from (select 'Return In' as tran_option, 'Ret In - Balance Error' as err_desp, date(ret_in_date) as tran_date,\n"
                + "			   ret_in_id as vou_no, vou_total, sum(ret_in_amount) as detail_total, balance, 0 as ttl_exp,\n"
                + "			   paid, 0 as tax_amt, 0 as discount\n"
                + "		  from v_return_in\n"
                + "		 where deleted = false and reg_no = '@regNo' and admission_no = '@admNo' and date(ret_in_date) between '@from' and '@to'\n"
                + "		 group by date(ret_in_date), ret_in_id, vou_total, balance) a\n"
                + " where (a.vou_total <> a.detail_total or a.balance <> (a.detail_total+a.ttl_exp+a.tax_amt-a.paid-a.discount))\n"
                + " union all\n"
                + "select * \n"
                + "  from (select 'OPD' as tran_option, 'OPD - Balance Error' as err_desp, date(opd_date) as tran_date,\n"
                + "			   opd_inv_id as vou_no, vou_total, sum(amount) as detail_total, vou_balance as balance, 0 as ttl_exp,\n"
                + "			   paid, tax_a as tax_amt, disc_a as discount\n"
                + "		  from v_opd\n"
                + "		 where deleted = false and patient_id = '@regNo' and admission_no = '@admNo' and date(opd_date) between '@from' and '@to'\n"
                + "		 group by date(opd_date), opd_inv_id, vou_total, vou_balance, paid, tax_a, disc_a) a\n"
                + " where (a.vou_total <> a.detail_total or a.balance <> (a.detail_total+a.ttl_exp+a.tax_amt-a.paid-a.discount))\n"
                + " union all\n"
                + "select * \n"
                + "  from (select 'OT' as tran_option, 'OT - Balance Error' as err_desp, date(ot_date) as tran_date,\n"
                + "			   ot_inv_id as vou_no, vou_total, sum(amount) as detail_total, vou_balance as balance, 0 as ttl_exp,\n"
                + "			   paid, tax_a as tax_amt, disc_a as discount\n"
                + "		  from v_ot\n"
                + "		 where deleted = false and service_id not in (select sys_prop_value from sys_prop \n"
                + "                where sys_prop_desp in ('system.ot.deposite.id','system.ot.disc.id','system.ot.paid.id','system.ot.refund.id'))\n"
                + "		   and patient_id = '@regNo' and admission_no = '@admNo' and date(ot_date) between '@from' and '@to'\n"
                + "		 group by date(ot_date), ot_inv_id, vou_total, vou_balance, paid, tax_a, disc_a) a\n"
                + " where (a.vou_total <> a.detail_total or a.balance <> (a.detail_total+a.ttl_exp+a.tax_amt-a.paid-a.discount))\n"
                + " union all\n"
                + "select * \n"
                + "  from (select 'DC' as tran_option, 'DC - Balance Error' as err_desp, date(dc_date) as tran_date,\n"
                + "			   dc_inv_id as vou_no, vou_total, sum(amount) as detail_total, vou_balance as balance, 0 as ttl_exp,\n"
                + "			   paid, tax_a as tax_amt, disc_a as discount\n"
                + "		  from v_dc\n"
                + "		 where deleted = false and service_id not in (select sys_prop_value from sys_prop \n"
                + "                where sys_prop_desp in ('system.dc.deposite.id','system.dc.disc.id','system.dc.paid.id','system.dc.refund.id'))\n"
                + "		   and patient_id = '@regNo' and admission_no = '@admNo' and date(dc_date) between '@from' and '@to'\n"
                + "		 group by date(dc_date), dc_inv_id, vou_total, vou_balance, paid, tax_a, disc_a) a\n"
                + " where (a.vou_total <> a.detail_total or a.balance <> (a.detail_total+a.ttl_exp+a.tax_amt-a.paid-a.discount))";
        strSql = strSql.replace("@regNo", regNo)
                .replace("@admNo", admNo)
                .replace("@from", admitDate)
                .replace("@to", tranDate);
        log.info("strSql : " + strSql);
        String strSql1 = "select 'Sale' as tran_option, 'Invalid Admission' as err_desp, date(sale_date) as tran_date,\n"
                + "       sale_inv_id as vou_no, vou_total, balance\n"
                + "  from sale_his\n"
                + " where deleted = false and reg_no = '@regNo' and if(admission_no='', null, admission_no) is null and ifnull(balance,0) <> 0\n"
                + " union all\n"
                + "select 'Return In' as tran_option, 'Invalid Admission' as err_desp, date(ret_in_date) as tran_date,\n"
                + "	   ret_in_id as vou_no, vou_total, balance\n"
                + "  from ret_in_his \n"
                + " where deleted = false and reg_no = '@regNo' and if(admission_no='', null, admission_no) is null and ifnull(balance,0) <> 0\n"
                + " union all\n"
                + "select 'OPD' as tran_option, 'Invalid Admission' as err_desp, date(opd_date) as tran_date,\n"
                + "	   opd_inv_id as vou_no, vou_total, vou_balance as balance\n"
                + "  from opd_his\n"
                + " where deleted = false and patient_id = '@regNo' and if(admission_no='', null, admission_no) is null and ifnull(vou_balance,0) <> 0\n"
                + " union all\n"
                + "select 'OT' as tran_option, 'Invalid Admission' as err_desp, date(ot_date) as tran_date,\n"
                + "	   ot_inv_id as vou_no, vou_total, vou_balance as balance\n"
                + "  from ot_his\n"
                + " where deleted = false and patient_id = '@regNo' and if(admission_no='', null, admission_no) is null and ifnull(vou_balance,0) <> 0";
        strSql1 = strSql1.replace("@regNo", regNo);

        try {
            List<CurrPTBalanceTran> list = new ArrayList();
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                while (rs.next()) {
                    CurrPTBalanceTran cptbt = new CurrPTBalanceTran(
                            rs.getString("tran_option"),
                            rs.getDate("tran_date"),
                            rs.getString("vou_no"),
                            rs.getString("err_desp"),
                            "",
                            rs.getDouble("vou_total"),
                            rs.getDouble("balance")
                    );
                    list.add(cptbt);
                }
            }

            ResultSet rs1 = dao.execSQL(strSql1);
            if (rs1 != null) {
                while (rs1.next()) {
                    CurrPTBalanceTran cptbt = new CurrPTBalanceTran(
                            rs.getString("tran_option"),
                            rs.getDate("tran_date"),
                            rs.getString("vou_no"),
                            rs.getString("err_desp"),
                            "",
                            rs.getDouble("vou_total"),
                            rs.getDouble("balance")
                    );
                    list.add(cptbt);
                }
            }

            errVouTableModel.setList(list);
        } catch (Exception ex) {
            log.error("getErrorVou : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public void getPatientBill(String regNo) {
        try {
            List<PatientBillPayment> listPBP = new ArrayList();
            Double totalBalance = 0.0;
            String currency = Util1.getPropValue("system.app.currency"); //"MMK"; //((Currency) cboCurrency.getSelectedItem()).getCurrencyCode();
            if(currency.isEmpty()){
                currency = "MMK";
            }
            //String date = DateUtil.toDateStrMYSQL(txtDate.getText());
            try (
                     ResultSet resultSet = dao.getPro("patient_bill_payment",
                            regNo, DateUtil.toDateStrMYSQL(DateUtil.getTodayDateStr()),
                            currency, Global.machineId)) {
                while (resultSet.next()) {
                    double bal = resultSet.getDouble("balance");
                    if (bal != 0) {
                        PatientBillPayment pbp = new PatientBillPayment();

                        pbp.setBillTypeDesp(resultSet.getString("payment_type_name"));
                        pbp.setBillTypeId(resultSet.getInt("bill_type"));
                        pbp.setCreatedBy(Global.loginUser.getUserId());
                        pbp.setCurrency(resultSet.getString("currency"));
                        //pbp.setPayDate(DateUtil.toDate(txtDate.getText()));
                        pbp.setRegNo(resultSet.getString("reg_no"));
                        pbp.setAmount(resultSet.getDouble("balance"));
                        pbp.setBalance(resultSet.getDouble("balance"));

                        totalBalance += pbp.getAmount();
                        listPBP.add(pbp);
                    }
                }
            }

            tblPatientBillTableModel.setListPBP(listPBP);
            txtPBTotal.setValue(totalBalance);
        } catch (Exception ex) {
            log.error("PatientSearch : Patient_bill_Payment :" + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        } finally {
            dao.close();
        }
    }

    private void assignChkBalance() {
        boolean status = false;
        try {
            List<CurrPTBalance> listBal = ptBalTableModel.getListBal();
            //String tranDate = DateUtil.getYesterdayateStrMYSQL();
            String tranDate = DateUtil.getTodayDateStrMYSQL();
            for (CurrPTBalance cptb : listBal) {
                String regNo = cptb.getRegNo();
                String amsNo = cptb.getAdmNo();
                List<Ams> listAms = dao.findAllHSQL(
                        "select o from Ams o where o.key.amsNo = '" + cptb.getAdmNo() + "'"
                );
                Ams ams = listAms.get(0);
                String admitDate = DateUtil.toDateTimeStr(ams.getAmsDate(), "yyyy-MM-dd");
                double ttlExpense = getTotalExpense(regNo, amsNo, admitDate, tranDate);
                double ttlPayment = getTotalPayment(regNo, amsNo, admitDate, tranDate);

                cptb.setCheckBalance(ttlExpense - ttlPayment);

                if (cptb.isError()) {
                    status = true;
                    log.error("Balance Error : " + regNo + " Balance : " + cptb.getBalance()
                            + " Check Balance : " + cptb.getCheckBalance());
                }
            }
        } catch (Exception ex) {
            log.error("assignChkBalance : " + ex.getMessage());
        } finally {
            dao.close();
        }

        if (status) {
            JOptionPane.showMessageDialog(Util1.getParent(), "Patient balance error have.\nPlease check in the list.",
                    "Balance Error", JOptionPane.ERROR_MESSAGE);
            butPrintBill.setVisible(false);
        } else {
            butPrintBill.setVisible(true);
        }
    }

    private double getTotalExpense(String regNo, String admNo, String admitDate, String tranDate) {
        double total = 0;
        String strSql = "select sum(ifnull(a.amount,0)) as ttl_expense from (\n"
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
                + "and date(sh.sale_date) between '" + admitDate + "' and '" + tranDate + "'\n"
                + "union all \n"
                + "select 'Return In' as tran_option, date(rih.ret_in_date) as tran_date, rih.ret_in_id as vou_no,\n"
                + "concat(ridh.med_id, '-', med.med_name) as item_name, concat(ridh.ret_in_qty,ridh.item_unit) as qty,\n"
                + "ridh.ret_in_price as price, (ridh.ret_in_amount*-1) as amount, ridh.unique_id, 'Return In' as group_name\n"
                + "from ret_in_his rih, ret_in_detail_his ridh, medicine med\n"
                + "where rih.deleted = false and rih.ret_in_id = ridh.vou_no\n"
                + "and ridh.med_id = med.med_id and rih.reg_no = '" + regNo + "' and rih.admission_no = '" + admNo + "' \n"
                + "and date(rih.ret_in_date) between '" + admitDate + "' and '" + tranDate + "' "
                + ") a ";

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                if (rs.next()) {
                    total = rs.getDouble("ttl_expense");
                }
            }
        } catch (Exception ex) {
            log.error("getTotalExpense : " + ex.getMessage());
        } finally {
            dao.close();
        }

        return total;
    }

    private double getTotalPayment(String regNo, String admNo, String admitDate, String tranDate) {
        double total = 0;
        String strSql = "select sum(ifnull(a.amount,0)) as ttl_payment from (\n"
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
                + "'OPD Discount' as item_name, 1 as qty, oh.disc_a as price , ifnull(oh.disc_a,0) as amount, 1 as unique_id, 'OPD' as group_name \n"
                + "from opd_his oh\n"
                + "where oh.deleted = false and  oh.patient_id = '" + regNo + "' and oh.admission_no = '" + admNo + "' \n"
                + "and date(oh.opd_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                + "union all\n"
                + "select 'OPD' as tran_option,date(oh.opd_date) as tran_date, oh.opd_inv_id as vou_no,\n"
                + "'OPD Paid' as item_name, 1 as qty, oh.disc_a as price , ifnull(oh.disc_a,0) as amount, 1 as unique_id, 'OPD' as group_name \n"
                + "from opd_his oh\n"
                + "where oh.deleted = false and  oh.patient_id = '" + regNo + "' and oh.admission_no = '" + admNo + "' \n"
                + "and date(oh.opd_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                + "union all\n"
                + "select concat('Pharmacy-',loc.location_name) as tran_option,date(sh.sale_date) as tran_date, sh.sale_inv_id as vou_no, \n"
                + "'Pharmacy Paid' as item_name, 1 as qty, sh.paid_amount as price, \n"
                + "ifnull(sh.paid_amount,0) as amount, 1 as unique_id, 'Pharmacy' as group_name \n"
                + "from sale_his sh, location loc \n"
                + "where sh.location_id = loc.location_id \n"
                + "and sh.deleted = false and sh.reg_no = '" + regNo + "' and sh.admission_no = '" + admNo + "' \n"
                + "and date(sh.sale_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                + "union all\n"
                + "select concat('Pharmacy-',loc.location_name) as tran_option,date(sh.sale_date) as tran_date, sh.sale_inv_id as vou_no, \n"
                + "'Pharmacy Discount' as item_name, 1 as qty, sh.discount as price, \n"
                + "ifnull(sh.discount,0) as amount, 1 as unique_id, 'Pharmacy' as group_name \n"
                + "from sale_his sh, location loc \n"
                + "where sh.location_id = loc.location_id \n"
                + "and sh.deleted = false and sh.reg_no = '" + regNo + "' and sh.admission_no = '" + admNo + "' \n"
                + "and date(sh.sale_date) between '" + admitDate + "' and '" + tranDate + "' \n"
                + "union all\n"
                + "select 'Bill Payment' as tran_option, pay_date as tran_date, remark as vou_no,\n"
                + "'Bill Payment' as item_name, 1 as qty, (ifnull(pay_amt,0)+ifnull(discount,0)) as price,\n"
                + "(ifnull(pay_amt,0)+ifnull(discount,0)) as amount, 1 as unique_id, 'Bill Payment' as group_name\n"
                + "from v_opd_patient_bill_payment\n"
                + "where deleted = false and reg_no = '" + regNo + "' and admission_no = '" + admNo + "' \n"
                + "and pay_date between '" + admitDate + "' and '" + tranDate + "' \n"
                + ") a ";

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                if (rs.next()) {
                    total = rs.getDouble("ttl_payment");
                }
            }
        } catch (Exception ex) {
            log.error("getTotalExpense : " + ex.getMessage());
        } finally {
            dao.close();
        }

        return total;
    }

    private void print() {
        insertPatientFilter();
        Map<String, Object> params = getParameter();
        String reportName = Util1.getPropValue("system.dc.pbalance.rpt");
        String reportPath = Util1.getAppWorkFolder()
                        + Util1.getPropValue("report.folder.path")
                        + "clinic/"
                        + reportName;
        try {
            dao.close();
            ReportUtil.viewReport(reportPath, params, dao.getConnection());
            dao.commit();
        }catch(Exception ex){
            log.error("print : " + ex.getMessage());
        }finally{
            dao.close();
        }
    }

    private void insertPatientFilter() {
        String machineId = Global.machineId;

        try {
            dao.execSql("delete from tmp_bill_payment where user_id = '" + machineId + "'");
            List<CurrPTBalance> listBal = ptBalTableModel.getListBal();
            for (CurrPTBalance cpb : listBal) {
                TmpBillPayment tbp = new TmpBillPayment();
                tbp.setRegNo(cpb.getRegNo());
                tbp.setAdmissionNo(cpb.getAdmNo());
                tbp.setPatientName(cpb.getPtName());
                tbp.setUserId(machineId);

                dao.save(tbp);
            }

        } catch (Exception ex) {
            log.error("insertPatientFilter : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    private Map<String, Object> getParameter() {
        String phoneNo = Util1.getPropValue("report.phone");
        String address = Util1.getPropValue("report.address");
        Map<String, Object> params = new HashMap();
        params.put("user_id", Global.machineId);
        params.put("compName", Util1.getPropValue("report.company.name"));
        params.put("adm_date", DateUtil.getYesterdayateStrMYSQL());
        params.put("tran_date", DateUtil.getYesterdayateStrMYSQL());
        //params.put("adm_date", "2022-11-05");
        //params.put("tran_date", "2022-11-05");
        params.put("IMAGE_PATH", Util1.getAppWorkFolder()
                + Util1.getPropValue("report.folder.path"));
        params.put("phoneNo", phoneNo);
        params.put("comAddress", address);
        
        return params;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtTranFilter = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTran = new javax.swing.JTable();
        txtTtlExpense = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        txtRegNo = new javax.swing.JTextField();
        txtAdmNo = new javax.swing.JTextField();
        lblPtName = new javax.swing.JLabel();
        butClear = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblPayment = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblGroupTotal = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblErrorVoucher = new javax.swing.JTable();
        txtAdmDate = new javax.swing.JFormattedTextField();
        jPanel1 = new javax.swing.JPanel();
        txtPtFilter = new javax.swing.JTextField();
        butRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPtList = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        txtTotalRecords = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        txtTtlBal = new javax.swing.JFormattedTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        txtPBTotal = new javax.swing.JFormattedTextField();
        jLabel4 = new javax.swing.JLabel();
        chkError = new javax.swing.JCheckBox();
        butPrintBill = new javax.swing.JButton();
        txtTotalPayment = new javax.swing.JFormattedTextField();
        jLabel5 = new javax.swing.JLabel();
        txtDB = new javax.swing.JFormattedTextField();
        jLabel6 = new javax.swing.JLabel();

        txtTranFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));
        txtTranFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtTranFilterKeyReleased(evt);
            }
        });

        tblTran.setFont(Global.textFont);
        tblTran.setModel(ptBalTranTableModel);
        tblTran.setRowHeight(23);
        jScrollPane2.setViewportView(tblTran);

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

        lblPtName.setFont(Global.textFont);
        lblPtName.setText(" ");
        lblPtName.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient Name"));

        butClear.setText("Clear");
        butClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butClearActionPerformed(evt);
            }
        });

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Payment"));

        tblPayment.setFont(Global.textFont);
        tblPayment.setModel(paymentTableModel);
        tblPayment.setRowHeight(23);
        jScrollPane3.setViewportView(tblPayment);

        jScrollPane4.setBorder(javax.swing.BorderFactory.createTitledBorder("Group Total"));

        tblGroupTotal.setFont(Global.textFont);
        tblGroupTotal.setModel(gtTablemodel);
        tblGroupTotal.setRowHeight(23);
        jScrollPane4.setViewportView(tblGroupTotal);

        jScrollPane5.setBorder(javax.swing.BorderFactory.createTitledBorder("Error Voucher"));

        tblErrorVoucher.setFont(Global.textFont);
        tblErrorVoucher.setModel(errVouTableModel);
        tblErrorVoucher.setRowHeight(23);
        jScrollPane5.setViewportView(tblErrorVoucher);

        txtAdmDate.setEditable(false);
        txtAdmDate.setBorder(javax.swing.BorderFactory.createTitledBorder("Admitted Date"));

        txtPtFilter.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));
        txtPtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPtFilterKeyReleased(evt);
            }
        });

        butRefresh.setText("Refresh");
        butRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butRefreshActionPerformed(evt);
            }
        });

        tblPtList.setFont(Global.textFont);
        tblPtList.setModel(ptBalTableModel);
        tblPtList.setRowHeight(23);
        jScrollPane1.setViewportView(tblPtList);

        jLabel3.setText("Total Records : ");

        txtTotalRecords.setEditable(false);
        txtTotalRecords.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Total : ");

        txtTtlBal.setEditable(false);
        txtTtlBal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jScrollPane6.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient Balance"));

        jTable2.setFont(Global.textFont);
        jTable2.setModel(tblPatientBillTableModel);
        jTable2.setRowHeight(23);
        jScrollPane6.setViewportView(jTable2);

        txtPBTotal.setEditable(false);
        txtPBTotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setText("Total : ");

        chkError.setText("Check Error");
        chkError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkErrorActionPerformed(evt);
            }
        });

        butPrintBill.setText("Print");
        butPrintBill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butPrintBillActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(txtPtFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butRefresh))
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(chkError)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(butPrintBill)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPBTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTotalRecords, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTtlBal, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE))
                .addGap(2, 2, 2))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtPtFilter, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(butRefresh, javax.swing.GroupLayout.Alignment.LEADING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtTotalRecords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTtlBal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPBTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(chkError)
                    .addComponent(butPrintBill))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {butRefresh, txtPtFilter});

        txtTotalPayment.setEditable(false);
        txtTotalPayment.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("Total Payment : ");

        txtDB.setEditable(false);
        txtDB.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Difference Balance : ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(butClear)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDB, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTotalPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTtlExpense, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1))
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAdmNo, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPtName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAdmDate, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTranFilter)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txtAdmNo, txtRegNo});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtRegNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtAdmNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblPtName)
                                .addComponent(txtTranFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtAdmDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTtlExpense, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(butClear)
                            .addComponent(txtTotalPayment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(txtDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))))
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

    private void txtPtFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPtFilterKeyReleased
        if (txtPtFilter.getText().isEmpty()) {
            sorterPatient.setRowFilter(null);
        } else if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
            sorterPatient.setRowFilter(swrfPatient);
        } else {
            sorterPatient.setRowFilter(RowFilter.regexFilter(txtPtFilter.getText()));
        }
    }//GEN-LAST:event_txtPtFilterKeyReleased

    private void butRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butRefreshActionPerformed
        getBalance();
    }//GEN-LAST:event_butRefreshActionPerformed

    private void txtTranFilterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTranFilterKeyReleased
        if (txtTranFilter.getText().isEmpty()) {
            sorterTran.setRowFilter(null);
        } else if (Util1.getPropValue("system.text.filter.method").equals("SW")) {
            sorterTran.setRowFilter(swrfTran);
        } else {
            sorterTran.setRowFilter(RowFilter.regexFilter(txtTranFilter.getText()));
        }
    }//GEN-LAST:event_txtTranFilterKeyReleased

    private void chkErrorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkErrorActionPerformed
        if (chkError.isSelected()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            assignChkBalance();
            butPrintBill.setVisible(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else {
            butPrintBill.setVisible(false);
        }
    }//GEN-LAST:event_chkErrorActionPerformed

    private void butPrintBillActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butPrintBillActionPerformed
        print();
    }//GEN-LAST:event_butPrintBillActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton butClear;
    private javax.swing.JButton butPrintBill;
    private javax.swing.JButton butRefresh;
    private javax.swing.JCheckBox chkError;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel lblPtName;
    private javax.swing.JTable tblErrorVoucher;
    private javax.swing.JTable tblGroupTotal;
    private javax.swing.JTable tblPayment;
    private javax.swing.JTable tblPtList;
    private javax.swing.JTable tblTran;
    private javax.swing.JFormattedTextField txtAdmDate;
    private javax.swing.JTextField txtAdmNo;
    private javax.swing.JFormattedTextField txtDB;
    private javax.swing.JFormattedTextField txtPBTotal;
    private javax.swing.JTextField txtPtFilter;
    private javax.swing.JTextField txtRegNo;
    private javax.swing.JFormattedTextField txtTotalPayment;
    private javax.swing.JFormattedTextField txtTotalRecords;
    private javax.swing.JTextField txtTranFilter;
    private javax.swing.JFormattedTextField txtTtlBal;
    private javax.swing.JFormattedTextField txtTtlExpense;
    // End of variables declaration//GEN-END:variables
}
