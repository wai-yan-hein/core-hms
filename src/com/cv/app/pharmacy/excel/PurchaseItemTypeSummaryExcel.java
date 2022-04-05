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
 * @author MyoGyi
 */
public class PurchaseItemTypeSummaryExcel extends GenExcel {

    static Logger log = Logger.getLogger(PurchaseItemTypeSummaryExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public PurchaseItemTypeSummaryExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select tmp.* from\n"
                + "(select vs.currency,cur.cur_name,it.item_type_code,\n"
                + "sum(vs.pur_amount) amount,it.item_type_name,vs.payment_type,pt.payment_type_name\n"
                + "  from v_purchase vs, currency cur, medicine med,item_type it,\n"
                + "payment_type pt\n"
                + " where vs.currency = cur.cur_code and vs.med_id=med.med_id and med.med_type_id=it.item_type_code and vs.payment_type=pt.payment_type_id\n"
                + "   and vs.med_id = med.med_id and vs.deleted = false\n"
                + " and (vs.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}))\n"
                + "   and date(vs.pur_date) between $P{prm_from} and $P{prm_to}\n"
                + "   and (vs.location = $P{prm_location} or $P{prm_location} = 0)\n"
                + "   and (vs.currency = $P{prm_currency} or $P{prm_currency} = 'All')\n"
                + "   and (vs.session_id = $P{session} or $P{session} = '-')\n"
                + "group by vs.currency,cur.cur_name,it.item_type_name,vs.payment_type,pt.payment_type_name,\n"
                + "it.item_type_code\n"
                + "union\n"
                + "select vs.currency,cur.cur_name,it.item_type_code,\n"
                + "sum(vs.pur_amount) amount,it.item_type_name,200 payment_type,'Total' payment_type_name\n"
                + "  from v_purchase vs, currency cur, medicine med,item_type it,\n"
                + "payment_type pt\n"
                + " where vs.currency = cur.cur_code and vs.med_id=med.med_id and med.med_type_id=it.item_type_code and vs.payment_type=pt.payment_type_id\n"
                + "   and vs.med_id = med.med_id and vs.deleted = false\n"
                + "and (vs.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}))\n"
                + "   and date(vs.pur_date) between $P{prm_from} and $P{prm_to}\n"
                + "   and (vs.location = $P{prm_location} or $P{prm_location} = 0)\n"
                + "   and (vs.currency = $P{prm_currency} or $P{prm_currency} = 'All')\n"
                + "   and (vs.session_id = $P{session} or $P{session} = '-')\n"
                + "group by vs.currency,cur.cur_name,it.item_type_name,it.item_type_code) tmp\n"
                + "order by tmp.payment_type,tmp.item_type_code";
        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'");
        strSql = strSql.replace("$P{prm_from}", "'" + getFromDate() + "'");
        strSql = strSql.replace("$P{prm_to}", "'" + getToDate() + "'");
        strSql = strSql.replace("$P{prm_location}", "'" + getLocationId() + "'");
        strSql = strSql.replace("$P{prm_currency}", "'" + getCurrencyId()+ "'");
        strSql = strSql.replace("$P{session}", "'" + getSession() + "'");

        try {
            List<String> listHeader = new ArrayList();
            
            listHeader.add("Item Type Name");
            listHeader.add("Amount");
            listHeader.add("Currency");
            listHeader.add("Payment Type");

            List<String> listField = new ArrayList();
            listField.add("item_type_name");
            listField.add("amount");
            listField.add("cur_name");
            listField.add("payment_type_name");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("item_type_name", POIUtil.FormatType.TEXT);
            hmType.put("amount", POIUtil.FormatType.TEXT);
            hmType.put("cur_name", POIUtil.FormatType.TEXT);
            hmType.put("payment_type_name", POIUtil.FormatType.TEXT);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }

    }

}
