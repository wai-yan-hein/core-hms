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
public class PurchaseSummaryExcel extends GenExcel {
    static Logger log = Logger.getLogger(PurchaseSummaryExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public PurchaseSummaryExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select distinct date(ph.pur_date) pur_date, ph.pur_inv_id, ph.currency, cr1.cur_name,ph.remark,\n" +
"tr.trader_name, ph.vou_total, ph.discount, ph.tax_amt, ph.paid, ph.pur_exp_total, ph.balance,\n" +
"pt.payment_type_name\n" +
"from pur_his ph, pur_detail_his pdh,trader tr, currency cr1, payment_type pt\n" +
"where ph.pur_inv_id = pdh.vou_no and ph.cus_id = tr.trader_id\n" +
"and ph.currency = cr1.cur_code and ph.payment_type = pt.payment_type_id and ph.deleted = false\n" +
"and date(ph.pur_date) between $P{prm_from} and $P{prm_to}\n" +
"and (ph.location = $P{prm_location} or $P{prm_location} = 0)\n" +
"and (ph.currency = $P{prm_currency} or $P{prm_currency} = 'All')\n" +
"and (ph.payment_type = $P{prm_payment} or $P{prm_payment} = 0)\n" +
"and (ph.vou_status = $P{prm_vou_type} or $P{prm_vou_type} = 0)\n" +
"and (pdh.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}))\n" +
"and (ph.cus_id in (select distinct trader_id from tmp_trader_bal_filter where user_id = $P{user_id})\n" +
"		or (select count(*) from tmp_trader_bal_filter where user_id = $P{user_id}) = 0)\n" +
"order by ph.pur_date";
        

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{prm_from}", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getToDate() + "'")
                .replace("$P{prm_location}", getLocationId())
                .replace("$P{prm_currency}", "'" + getCurrencyId() + "'")
                .replace("$P{prm_vou_type}", getVouType())
                .replace("$P{prm_payment}", getPaymentType());
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Date");
            listHeader.add("Invoice No.");
            listHeader.add("Supplier");
            listHeader.add("Remark");
            listHeader.add("Vou Total");
            listHeader.add("Discount");
            listHeader.add("Tax Amt");
            listHeader.add("Paid");
            listHeader.add("Pur-Exp");
            listHeader.add("Balance");
            
            List<String> listField = new ArrayList();
            listField.add("pur_date");
            listField.add("pur_inv_id");
            listField.add("trader_name");
            listField.add("remark");
            listField.add("vou_total");
            listField.add("discount");
            listField.add("tax_amt");
            listField.add("paid");
            listField.add("pur_exp_total");
            listField.add("balance");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("pur_date", POIUtil.FormatType.DATE);
            hmType.put("pur_inv_id", POIUtil.FormatType.TEXT);
            hmType.put("trader_name", POIUtil.FormatType.TEXT);
            hmType.put("remark", POIUtil.FormatType.TEXT);
            hmType.put("vou_total", POIUtil.FormatType.DOUBLE);
            hmType.put("discount", POIUtil.FormatType.DOUBLE);
            hmType.put("tax_amt", POIUtil.FormatType.FLOAT);
            hmType.put("paid", POIUtil.FormatType.FLOAT);
            hmType.put("pur_exp_total", POIUtil.FormatType.DOUBLE);
            hmType.put("balance", POIUtil.FormatType.DOUBLE);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
