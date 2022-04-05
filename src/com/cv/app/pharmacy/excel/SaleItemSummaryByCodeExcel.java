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
public class SaleItemSummaryByCodeExcel extends GenExcel {
    static Logger log = Logger.getLogger(SaleItemSummaryByCodeExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public SaleItemSummaryByCodeExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select it.item_type_code, vs.med_id,vs.med_name,med.rel_str,rg.item_unit,it.item_type_name,vs.sale_price,\n" +
"       get_qty_in_str(sum(ifnull(sale_smallest_qty,0)), med.unit_smallest, med.unit_str) ttl_qty,\n" +
"       sum(vs.sale_amount) amount,sum(ifnull(sale_smallest_qty,0)) sale_small_qty,\n" +
"       get_qty_in_str(sum(ifnull(vs.foc_smallest_qty,0)), med.unit_smallest, med.unit_str) foc_qty,\n" +
"       get_qty_in_str(sum(ifnull(sale_smallest_qty,0)+ifnull(vs.foc_smallest_qty,0)), med.unit_smallest, med.unit_str) ttl,\n" +
"       sum(if(ifnull(admission_no,'')='',vs.sale_amount,0)) opd_sale,\n" +
"       sum(if(ifnull(admission_no,'')='',0,vs.sale_amount)) adm_sale\n" +
"  from v_sale vs, currency cur, v_med_unit_smallest_rel med,medicine m,\n" +
"       item_type it,v_med_rel_smallest_unit vmrsu,relation_group rg\n" +
" where vs.currency_id = cur.cur_code\n" +
"   and vs.med_id = vmrsu.med_id and vmrsu.rel_group_id = rg.rel_group_id\n" +
"   and vs.med_id = med.med_id and vs.deleted = false\n" +
"   and med.med_id = m.med_id and m.med_type_id = it.item_type_code\n" +
"   and date(vs.sale_date) between $P{prm_from} and $P{prm_to}\n" +
"   and (vs.location_id = $P{prm_location} or $P{prm_location} = 0)\n" +
"   and (vs.currency_id = $P{prm_currency} or $P{prm_currency} = 'All')\n" +
"   and (vs.session_id = $P{session} or $P{session} = '-')\n" +
"   and (vs.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id})\n" +
"	)\n" +
"group by it.item_type_name,it.item_type_code,vs.med_id,\n" +
"vs.med_name,med.rel_str,rg.item_unit\n" +
"order by it.item_type_code,it.item_type_name,vs.med_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                //.replace("$P{cus_group}", "'" + getCusGroup() + "'")
                .replace("$P{prm_from}", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getToDate() + "'")
                .replace("$P{prm_location}", getLocationId())
                .replace("$P{prm_currency}", "'" + getCurrencyId() + "'")
                //.replace("$P{reg_no}", "'" + getRegNo() + "'")
                .replace("$P{session}", "'" + getSession() + "'");
                //.replace("$P{prm_payment}", getPaymentType());
        
        
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Code");
            listHeader.add("Item Name");
            listHeader.add("UOM");
            listHeader.add("Sale Qty");
            listHeader.add("Small Qty");
            listHeader.add("FOC");
            listHeader.add("OPD Amount");
            listHeader.add("Inp-Amount");
            
            List<String> listField = new ArrayList();
            listField.add("med_id");
            listField.add("med_name");
            listField.add("rel_str");
            listField.add("ttl_qty");
            listField.add("sale_small_qty");
            listField.add("foc_qty");
            listField.add("opd_sale");
            listField.add("adm_sale");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("rel_str", POIUtil.FormatType.TEXT);
            hmType.put("ttl_qty", POIUtil.FormatType.TEXT);
            hmType.put("sale_small_qty", POIUtil.FormatType.FLOAT);
            hmType.put("opd_sale", POIUtil.FormatType.DOUBLE);
            hmType.put("adm_sale", POIUtil.FormatType.DOUBLE);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
