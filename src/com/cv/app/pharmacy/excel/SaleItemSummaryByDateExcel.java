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
public class SaleItemSummaryByDateExcel extends GenExcel {
    static Logger log = Logger.getLogger(SaleItemSummaryByDateExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public SaleItemSummaryByDateExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select date(vs.sale_date) sale_date,rg.item_unit, vs.med_id,vs.med_name,med.rel_str,it.item_type_name,\n" +
"it.item_type_code,sum(ifnull(sale_smallest_qty,0)) sale_smallest,get_qty_in_str(sum(ifnull(sale_smallest_qty,0)), med.unit_smallest, med.unit_str) ttl_qty,\n" +
"sum(vs.sale_amount) amount,\n" +
"get_qty_in_str(sum(ifnull(vs.foc_smallest_qty,0)), med.unit_smallest, med.unit_str) foc_qty,\n" +
"get_qty_in_str(sum(ifnull(sale_smallest_qty,0)+ifnull(vs.foc_smallest_qty,0)), med.unit_smallest, med.unit_str) ttl,\n" +
"sum(vs.sale_amount)/sum(ifnull(sale_smallest_qty,1)) sale_price\n" +
"  from v_sale vs, currency cur, v_med_unit_smallest_rel med,medicine m,item_type it,payment_type pt,v_med_rel_smallest_unit vmrsu,relation_group rg\n" +
" where vs.currency_id = cur.cur_code\n" +
"   and vs.med_id = vmrsu.med_id and vmrsu.rel_group_id = rg.rel_group_id\n" +
"   and vs.med_id = med.med_id and vs.deleted = false and vs.med_id = m.med_id and m.med_type_id = it.item_type_code and\n" +
"   vs.payment_type_id = pt.payment_type_id\n" +
"   and date(vs.sale_date) between $P{prm_from} and $P{prm_to}\n" +
"   and (vs.location_id = $P{prm_location} or $P{prm_location} = 0)\n" +
"   and (vs.currency_id = $P{prm_currency} or $P{prm_currency} = 'All')\n" +
"   and (vs.session_id = $P{session} or $P{session} = '-')\n" +
"   and (vs.payment_type_id = $P{prm_payment} or $P{prm_payment} = 0)\n" +
"   and (vs.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}))\n" +
"group by date(vs.sale_date),it.item_type_name,\n" +
"vs.med_id,vs.med_name,med.rel_str,it.item_type_code,rg.item_unit\n" +
"order by date(vs.sale_date),it.item_type_code,vs.med_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                //.replace("$P{cus_group}", "'" + getCusGroup() + "'")
                .replace("$P{prm_from}", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getToDate() + "'")
                .replace("$P{prm_location}", getLocationId())
                .replace("$P{prm_currency}", "'" + getCurrencyId() + "'")
                //.replace("$P{reg_no}", "'" + getRegNo() + "'")
                .replace("$P{session}", "'" + getSession() + "'")
                .replace("$P{prm_payment}", getPaymentType());
        
        
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Date");
            listHeader.add("Code");
            listHeader.add("Item Name");
            listHeader.add("Pk. Size");
            listHeader.add("Price");
            listHeader.add("Sale Qty");
            listHeader.add("Small Qty");
            listHeader.add("FOC Qty");
            listHeader.add("Amount");
            
            List<String> listField = new ArrayList();
            listField.add("sale_date");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("rel_str");
            listField.add("sale_price");
            listField.add("ttl_qty");
            listField.add("sale_smallest");
            listField.add("foc_qty");
            listField.add("amount");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("sale_date", POIUtil.FormatType.DATE);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("rel_str", POIUtil.FormatType.TEXT);
            hmType.put("sale_price", POIUtil.FormatType.DOUBLE);
            hmType.put("ttl_qty", POIUtil.FormatType.TEXT);
            hmType.put("sale_smallest", POIUtil.FormatType.FLOAT);
            hmType.put("foc_qty", POIUtil.FormatType.TEXT);
            hmType.put("amount", POIUtil.FormatType.DOUBLE);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
