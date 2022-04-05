/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.excel;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.POIUtil;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class SaleSummaryExcel extends GenExcel {

    static Logger log = Logger.getLogger(SaleSummaryExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public SaleSummaryExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select distinct sh.sale_inv_id, date(sh.sale_date) sale_date, ifnull(tr.trader_name, pd.patient_name) trader_name,\n"
                + "	   cr1.cur_name vou_currency, pt.payment_type_name, sh.vou_total,\n"
                + "	   sh.discount, sh.tax_amt, sh.paid_amount, sh.balance,ifnull(sh.reg_no, sh.cus_id) reg_no,sh.sale_exp_total,\n"
                + "	   cr2.cur_name paid_currency, sh.paid_currency_amt\n"
                + "  from sale_his sh \n"
                + "   join sale_detail_his sdh on sh.sale_inv_id = sdh.vou_no\n"
                + "   join currency cr1 on sh.currency_id = cr1.cur_code\n"
                + "   join currency cr2 on sh.paid_currency = cr2.cur_code\n"
                + "   join payment_type pt on sh.payment_type_id = pt.payment_type_id\n"
                + "   left join trader tr on sh.cus_id = tr.trader_id\n"
                + "   left join patient_detail pd on sh.reg_no = pd.reg_no\n"
                + " where sh.deleted = false and date(sh.sale_date) between $P{prm_from} and $P{prm_to} and (sh.session_id = $P{session} or $P{session} = '-')\n"
                + "   and (sh.location_id = $P{prm_location} or $P{prm_location} = 0)\n"
                + "   and (sh.currency_id = $P{prm_currency} or $P{prm_currency} = 'All')\n"
                + "   and (sh.payment_type_id = $P{prm_payment} or $P{prm_payment} = 0)\n"
                + "   and (vou_status = $P{prm_vou_type} or $P{prm_vou_type} = 0)\n"
                + "   #and (sdh.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}))\n"
                + "   and ((sh.cus_id in (select distinct trader_id from tmp_trader_bal_filter where user_id = $P{user_id})\n"
                + "		or (select count(*) from tmp_trader_bal_filter where user_id = $P{user_id}) = 0)\n"
                + "	or (select count(*) from sys_prop where sys_prop_desp = 'system.app.usage.type' and sys_prop_value = 'Hospital')>0)\n"
                + "order by sh.sale_date";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{prm_vou_type}", getVouType())
                .replace("$P{prm_from}", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getToDate() + "'")
                .replace("$P{prm_location}", getLocationId())
                .replace("$P{prm_currency}", "'" + getCurrencyId() + "'")
                .replace("$P{session}", "'" + getSession() + "'")
                .replace("$P{prm_payment}", getPaymentType());

        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Sale Date");
            listHeader.add("Vou No");
            listHeader.add("Customer");
            listHeader.add("Reg No");
            listHeader.add("Vou Total");
            listHeader.add("Discount");
            listHeader.add("Tax Amt");
            listHeader.add("Paid");
            listHeader.add("Sale-Exp");
            listHeader.add("Balance");
            listHeader.add("P-Curr");
            listHeader.add("P-Amount");

            List<String> listField = new ArrayList();
            listField.add("sale_date");
            listField.add("sale_inv_id");
            listField.add("trader_name");
            listField.add("reg_no");
            listField.add("vou_total");
            listField.add("discount");
            listField.add("tax_amt");
            listField.add("paid_amount");
            listField.add("sale_exp_total");
            listField.add("balance");
            listField.add("paid_currency");
            listField.add("paid_currency_amt");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("sale_date", POIUtil.FormatType.DATE);
            hmType.put("sale_inv_id", POIUtil.FormatType.TEXT);
            hmType.put("trader_name", POIUtil.FormatType.TEXT);
            hmType.put("reg_no", POIUtil.FormatType.TEXT);
            hmType.put("vou_total", POIUtil.FormatType.DOUBLE);
            hmType.put("discount", POIUtil.FormatType.DOUBLE);
            hmType.put("tax_amt", POIUtil.FormatType.DOUBLE);
            hmType.put("paid_amount", POIUtil.FormatType.DOUBLE);
            hmType.put("sale_exp_total", POIUtil.FormatType.DOUBLE);
            hmType.put("balance", POIUtil.FormatType.DOUBLE);
            hmType.put("paid_currency", POIUtil.FormatType.TEXT);
            hmType.put("paid_currency_amt", POIUtil.FormatType.DOUBLE);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
