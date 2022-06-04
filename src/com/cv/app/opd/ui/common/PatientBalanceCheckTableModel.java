/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.inpatient.database.healper.CurrPTBalance;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.DateUtil;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PatientBalanceCheckTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PatientBalanceCheckTableModel.class.getName());
    private List<CurrPTBalance> list;
    private final String[] columnNames = {"Reg No.", "Adm No", "Patient",
        "S", "Balance", "C-Balance"};
    private final AbstractDataAccess dao = Global.dao;
    private String tranDate;
    private String currId;
    private double checkTotal = 0;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Reg No.
                return String.class;
            case 1: //Adm No.
                return String.class;
            case 2: //Patient
                return String.class;
            case 3: //Status
                return Boolean.class;
            case 4: //Balance
                return Double.class;
            case 5: //Check Balance
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        CurrPTBalance record = list.get(row);

        switch (column) {
            case 0: //Reg No.
                return record.getRegNo();
            case 1: //Adm No.
                return record.getAdmNo();
            case 2: //Patient
                return record.getPtName();
            case 3: //S
                return record.isError();
            case 4: //Balance
                return record.getBalance();
            case 5: //Check Balance
                return record.getCheckBalance();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {

    }

    @Override
    public int getRowCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<CurrPTBalance> getList() {
        return list;
    }

    public void setList(List<CurrPTBalance> list) {
        this.list = list;
        fireTableDataChanged();
    }

    public void setTranDate(String tranDate) {
        this.tranDate = tranDate;
    }

    public void setCurrency(String currency) {
        this.currId = currency;
    }

    public void checkBalance() {
        list.forEach(ptb -> {
            String regNo = ptb.getRegNo();
            String strOPDate = DateUtil.toDateStr(getPatientOPDate(regNo, currId), "YYYY-MM-DD");
            if (strOPDate != null) {
                Double opAmt = getOpAmt(regNo, currId, strOPDate);
                String strSql = "select round(sum(a.balance),0) as balance from (\n"
                        + "select sum(a.vou_total-a.discount+a.sale_exp_total-a.paid_amount-a.item_discount) as balance\n"
                        + "from (\n"
                        + "select sh.sale_inv_id, sh.discount, sh.sale_exp_total, sh.paid_amount, \n"
                        + "sum(ifnull(sdh.sale_qty,0)*ifnull(sdh.sale_price,0)) as vou_total,\n"
                        + "sum(if(a.sys_prop_value='Percent',\n"
                        + "  ifnull(sdh.sale_qty,0)*ifnull(sdh.sale_price,0)*(ifnull(sdh.item_discount,0)/100),\n"
                        + "  if(a.sys_prop_value='Each',ifnull(sdh.item_discount,0)*ifnull(sdh.sale_qty,0),ifnull(sdh.item_discount,0)))) as item_discount \n"
                        + "from sale_his sh\n"
                        + "join sale_detail_his sdh on sh.sale_inv_id = sdh.vou_no\n"
                        + "join (select sys_prop_value from sys_prop where sys_prop_desp = 'system.app.sale.discount.calculation') a \n"
                        + "where sh.deleted = false and sh.reg_no = '" + regNo
                        + "' and date(sh.sale_date) between '" + strOPDate
                        + "' and '" + DateUtil.toDateStrMYSQL(tranDate) + "'\n"
                        + "and sh.currency_id = '" + currId + "'\n"
                        + "group by sh.sale_inv_id, sh.discount, sh.sale_exp_total, sh.paid_amount) a \n"
                        + "union all \n"
                        + "select ifnull(sum(a.vou_total-a.paid)*-1,0) as balance\n"
                        + "from (\n"
                        + "select rih.ret_in_id, rih.paid, sum(ifnull(ridh.ret_in_qty,0)*ifnull(ridh.ret_in_price,0)) as vou_total\n"
                        + "from ret_in_his rih\n"
                        + "join ret_in_detail_his ridh on rih.ret_in_id = ridh.vou_no\n"
                        + "where rih.deleted = false and rih.reg_no = '" + regNo
                        + "' and date(rih.ret_in_date) between '" + strOPDate
                        + "' and '" + DateUtil.toDateStrMYSQL(tranDate) + "'\n"
                        + "and rih.currency = '" + currId + "'\n"
                        + "group by rih.ret_in_id, rih.paid) a \n"
                        + "union all \n"
                        + "select sum(a.vou_total-a.disc_a+a.tax_a-a.paid) as balance\n"
                        + "from (\n"
                        + "select oh.opd_inv_id, oh.disc_a, oh.tax_a, oh.paid,\n"
                        + "sum(if(ct.is_amount,(ifnull(odh.qty,0)*ifnull(odh.price,0))+ifnull(ct.factor,0),\n"
                        + "(ifnull(odh.qty,0)*ifnull(odh.price,0))*ifnull(ct.factor,0))) as vou_total\n"
                        + "from opd_his oh\n"
                        + "join opd_details_his odh on oh.opd_inv_id = odh.vou_no\n"
                        + "join charge_type ct on odh.charge_type = ct.charge_type_id\n"
                        + "where oh.deleted = false and oh.patient_id = '" + regNo
                        + "' and date(opd_date) between '" + strOPDate
                        + "' and '" + DateUtil.toDateStrMYSQL(tranDate) + "'\n"
                        + "and oh.currency_id = '" + currId + "'\n"
                        + "group by oh.opd_inv_id, oh.disc_a, oh.tax_a, oh.paid) a \n"
                        + "union all \n"
                        + "select ifnull(sum(a.vou_total-a.disc_a+a.tax_a-a.paid),0) as balance\n"
                        + "from (\n"
                        + "select oh.ot_inv_id, oh.disc_a, oh.tax_a, oh.paid, ifnull(s.vou_total,0) as vou_total \n"
                        + "from ot_his oh\n"
                        + "left join (\n"
                        + "select odh.vou_no, sum(if(ct.is_amount,(ifnull(odh.qty,0)*ifnull(odh.price,0))+ifnull(ct.factor,0),\n"
                        + "(ifnull(odh.qty,0)*ifnull(odh.price,0))*ifnull(ct.factor,0))) as vou_total\n"
                        + "from ot_details_his odh\n"
                        + "join charge_type ct on odh.charge_type = ct.charge_type_id\n"
                        + "where odh.service_id not in (select sys_prop_value from sys_prop \n"
                        + "where sys_prop_desp in ('system.ot.deposite.id','system.ot.disc.id','system.ot.paid.id','system.ot.refund.id'))\n"
                        + "group by odh.vou_no) s on oh.ot_inv_id = s.vou_no\n"
                        + "where oh.deleted = false and oh.patient_id = '" + regNo
                        + "' and date(ot_date) between '" + strOPDate
                        + "' and '" + DateUtil.toDateStrMYSQL(tranDate) + "'\n"
                        + "and oh.currency_id = '" + currId + "'\n"
                        + "group by oh.ot_inv_id, oh.disc_a, oh.tax_a, oh.paid) a \n"
                        + "union all \n"
                        + "select sum(a.vou_total-a.disc_a+a.tax_a-a.paid) as balance\n"
                        + "from (\n"
                        + "select oh.dc_inv_id, oh.disc_a, oh.tax_a, oh.paid,ifnull(s.vou_total,0) as vou_total\n"
                        + "from dc_his oh\n"
                        + "left join(\n"
                        + "select odh.vou_no, sum(if(ct.is_amount,(ifnull(odh.qty,0)*ifnull(odh.price,0))+ifnull(ct.factor,0),\n"
                        + "(ifnull(odh.qty,0)*ifnull(odh.price,0))*ifnull(ct.factor,0))) as vou_total\n"
                        + "from dc_details_his odh \n"
                        + "join charge_type ct on odh.charge_type = ct.charge_type_id\n"
                        + "where odh.service_id not in (select sys_prop_value from sys_prop \n"
                        + " where sys_prop_desp in ('system.dc.deposite.id','system.dc.disc.id','system.dc.paid.id', 'system.dc.refund.id'))\n"
                        + " group by odh.vou_no \n"
                        + " ) s on oh.dc_inv_id = s.vou_no\n"
                        + "where oh.deleted = false and oh.patient_id = '" + regNo + "' and date(dc_date) between '" + strOPDate
                        + "' and '" + DateUtil.toDateStrMYSQL(tranDate) + "'\n"
                        + "and oh.currency_id = '" + currId + "' \n"
                        + "group by oh.dc_inv_id, oh.disc_a, oh.tax_a, oh.paid) a \n"
                        + "union all \n"
                        + "select (sum(ifnull(pay_amt,0))*-1) as balance\n"
                        + "from opd_patient_bill_payment \n"
                        + "where reg_no = '" + regNo
                        + "' and currency_id = '" + currId + "' and date(pay_date) between '"
                        + strOPDate + "' and '" + DateUtil.toDateStrMYSQL(tranDate) + "' \n"
                        + ") a";
                try {
                    log.info("sql c : " + strSql);
                    ResultSet rs = dao.execSQL(strSql);
                    if (rs != null) {
                        if (rs.next()) {
                            Double balance = rs.getDouble("balance");
                            ptb.setCheckBalance(balance + opAmt);
                            checkTotal += balance + opAmt;
                        }
                        rs.close();
                    }
                } catch (Exception ex) {
                    log.error("checkBalance : " + regNo + " : " + ex.getMessage());
                } finally {
                    dao.close();
                }
            }
        });

        fireTableDataChanged();
    }

    private Date getPatientOPDate(String regNo, String currency) {
        String strSql = "select ifnull(max(op_date),'1900-01-01') as op_date\n"
                + "from patient_op\n"
                + "where reg_no = '" + regNo + "' and currency = '" + currency + "' "
                + "and op_date <= '" + DateUtil.toDateStrMYSQLEnd(tranDate) + "'";
        Date tmpDate = null;

        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                if (rs.next()) {
                    tmpDate = rs.getDate("op_date");
                }
            }
        } catch (Exception ex) {
            log.error("getPatientOPDate : " + ex.getMessage());
        } finally {
            dao.close();
        }

        return tmpDate;
    }

    private Double getOpAmt(String regNo, String currency, String strOPDate) {
        String strSql = "select sum(op_amount) as op_amt\n"
                + "from patient_op\n"
                + "where reg_no = '" + regNo + "' and currency = '" + currency
                + "' and op_date = '" + strOPDate + "'";
        Double tmpAmt = 0.0;
        try {
            ResultSet rs = dao.execSQL(strSql);
            if (rs != null) {
                if (rs.next()) {
                    tmpAmt = rs.getDouble("op_amt");
                }
            }
        } catch (Exception ex) {
            log.error("getOpAmt : " + ex.getMessage());
        } finally {
            dao.close();
        }

        return tmpAmt;
    }

    public double getCheckTotal() {
        return checkTotal;
    }
}
