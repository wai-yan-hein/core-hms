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
public class ReturnOutItemSummaryExcel extends GenExcel {

    static Logger log = Logger.getLogger(ReturnOutItemSummaryExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public ReturnOutItemSummaryExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {

        String strSql = "select vro.currency, cur.cur_name, vro.med_id, vro.med_name,\n"
                + "get_qty_in_str(sum(vro.ret_out_smallest_qty), med.unit_smallest, med.unit_str) ttl_qty,\n"
                + "sum(vro.ret_out_amount) amount\n"
                + "from v_return_out vro, currency cur, v_med_unit_smallest_rel med\n"
                + "where vro.currency = cur.cur_code and vro.med_id = med.med_id\n"
                + "and vro.deleted = false\n"
                + " and date(vro.ret_out_date) between $P{prm_from} and $P{prm_to}\n"
                + "   and (vro.location = $P{prm_location} or $P{prm_location} = 0)\n"
                + "   and (vro.currency = $P{prm_currency} or $P{prm_currency} = 'All')\n"
                + "   and (vro.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}))\n"
                + "AND (vro.cus_id IN\n"
                + "		(select distinct trader_id from tmp_trader_bal_filter where user_id = $P{user_id})\n"
                + "		OR (select count(*) from tmp_trader_bal_filter where user_id = $P{user_id}) = 0)\n"
                + "group by vro.currency,cur.cur_name,vro.med_id,vro.med_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'");
        strSql = strSql.replace("$P{prm_from}", "'" + getFromDate() + "'");
        strSql = strSql.replace("$P{prm_to}", "'" + getToDate() + "'");
        strSql = strSql.replace("$P{prm_location}", "'" + getLocationId() + "'");
        strSql = strSql.replace("$P{prm_currency}", "'" + getCurrencyId() + "'");

        try {
            List<String> listHeader = new ArrayList();

            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("Qty");
            listHeader.add("Amount");
            listHeader.add("Currency");

            List<String> listField = new ArrayList();
            listField.add("med_id");
            listField.add("med_name");
            listField.add("ttl_qty");
            listField.add("amount");
            listField.add("cur_name");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();

            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("ttl_qty", POIUtil.FormatType.TEXT);
            hmType.put("amount", POIUtil.FormatType.TEXT);
            hmType.put("cur_name", POIUtil.FormatType.TEXT);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }

    }

}
