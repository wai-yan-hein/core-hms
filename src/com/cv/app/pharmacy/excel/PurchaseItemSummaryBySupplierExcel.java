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
public class PurchaseItemSummaryBySupplierExcel extends GenExcel {
    static Logger log = Logger.getLogger(PurchaseItemSummaryBySupplierExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public PurchaseItemSummaryBySupplierExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select vp.currency,cur.cur_name,vp.med_id,vp.med_name,get_qty_in_str(sum(pur_smallest_qty), med.unit_smallest, med.unit_str) ttl_qty,sum(vp.pur_amount) amount,vp.charge_type, ct.charge_type_desc,sum(pur_smallest_qty) sqty,it.item_type_name,med.rel_str,vp.trader_name,m.med_type_id,ifnull(vp.expire_date,'-') expire_date\n" +
"  from v_purchase vp, charge_type ct, currency cur, v_med_unit_smallest_rel med,medicine m,item_type it\n" +
" where vp.currency = cur.cur_code and vp.charge_type = ct.charge_type_id\n" +
"   and vp.med_id = med.med_id and vp.deleted = false\n" +
" and med.med_id = m.med_id and m.med_type_id = it.item_type_code\n" +
"   and date(vp.pur_date) between $P{prm_from} and $P{prm_to}\n" +
"   and (vp.location = $P{prm_location} or $P{prm_location} = 0)\n" +
"   and (vp.currency = $P{prm_currency} or $P{prm_currency} = 'All')\n" +
"   and (vp.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}))\n" +
"and (vp.cus_id in (select distinct trader_id from tmp_trader_bal_filter where user_id = $P{user_id})\n" +
"		or (select count(*) from tmp_trader_bal_filter where user_id = $P{user_id}) = 0)\n" +
"group by it.item_type_name,vp.trader_name,vp.currency,cur.cur_name,\n" +
"vp.med_id,vp.med_name,vp.charge_type, ct.charge_type_desc,med.rel_str,vp.expire_date,m.med_type_id\n" +
"order by vp.trader_name,m.med_type_id";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{prm_from}", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getToDate() + "'")
                .replace("$P{prm_location}", getLocationId())
                .replace("$P{prm_currency}", "'" + getCurrencyId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Supplier name");
            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("Packing Size    ");
            listHeader.add("Exp Date");
            listHeader.add("Qty");
            listHeader.add("Smallest Qty");
            listHeader.add("Amount");
            
            List<String> listField = new ArrayList();
            listField.add("trader_name");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("rel_str");
            listField.add("expire_date");
            listField.add("ttl_qty");
            listField.add("sqty");
            listField.add("amount");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("trader_name", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("rel_str", POIUtil.FormatType.TEXT);
            hmType.put("expire_date", POIUtil.FormatType.TEXT);
            hmType.put("ttl_qty", POIUtil.FormatType.TEXT);
            hmType.put("sqty", POIUtil.FormatType.FLOAT);
            hmType.put("amount", POIUtil.FormatType.DOUBLE);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
