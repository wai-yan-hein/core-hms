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
public class PurchaseItemSummaryExcel extends GenExcel {
    static Logger log = Logger.getLogger(PurchaseItemSummaryExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public PurchaseItemSummaryExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select vp.med_id,vp.med_name, med1.med_rel_str, med1.med_type_id, it.item_type_name,\n" +
"get_qty_in_str(sum(ifnull(vp.pur_smallest_qty,0)), med.unit_smallest, med.unit_str) ttl_pur_qty,\n" +
"get_qty_in_str(sum(ifnull(vp.pur_foc_smallest_qty,0)), med.unit_smallest, med.unit_str) ttl_foc_qty,\n" +
"sum(ifnull(vp.pur_smallest_qty,0)+ifnull(vp.pur_foc_smallest_qty,0)) ttl_pur_smallest,\n" +
"sum(ifnull(vp.pur_foc_smallest_qty,0)) ttl_foc_smallest,sum(vp.pur_amount) amount,\n" +
"(vp.pur_unit_cost/vrg.smallest_qty) as smallest_cost\n" +
"  from v_purchase vp, charge_type ct, currency cur, v_med_unit_smallest_rel med,\n" +
"       medicine med1, v_relatio_group vrg, item_type it\n" +
" where vp.currency = cur.cur_code and vp.charge_type = ct.charge_type_id\n" +
"   and vp.med_id = med1.med_id and vp.med_id = vrg.med_id\n" +
"   and vp.pur_unit = vrg.item_unit and med1.med_type_id = it.item_type_code\n" +
"   and vp.med_id = med.med_id and vp.deleted = false\n" +
"   and date(vp.pur_date) between $P{prm_from} and $P{prm_to}\n" +
"   and (vp.location = $P{prm_location} or $P{prm_location} = 0)\n" +
"   and (vp.currency = $P{prm_currency} or $P{prm_currency} = 'All')\n" +
"   and (vp.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}))\n" +
"and (vp.cus_id in (select distinct trader_id from tmp_trader_bal_filter where user_id = $P{user_id})\n" +
"		or (select count(*) from tmp_trader_bal_filter where user_id = $P{user_id}) = 0)\n" +
"group by vp.med_id,vp.med_name, med1.med_rel_str, med.unit_smallest, med.unit_str,it.item_type_name,\n" +
"vp.pur_unit_cost, vrg.smallest_qty\n" +
"order by med1.med_type_id,it.item_type_name,vp.med_id, vp.med_name";
        

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{prm_from}", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getToDate() + "'")
                .replace("$P{prm_location}", getLocationId())
                .replace("$P{prm_currency}", "'" + getCurrencyId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Item Type");
            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("Packing");
            listHeader.add("Pur Qty");
            listHeader.add("FOC Qty");
            listHeader.add("Pur Smallest Qty");
            listHeader.add("Pur Smallest Cost");
            listHeader.add("Amount");
            
            List<String> listField = new ArrayList();
            listField.add("item_type_name");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("med_rel_str");
            listField.add("ttl_pur_qty");
            listField.add("ttl_foc_qty");
            listField.add("ttl_pur_smallest");
            listField.add("smallest_cost");
            listField.add("amount");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("item_type_name", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("med_rel_str", POIUtil.FormatType.TEXT);
            hmType.put("ttl_pur_qty", POIUtil.FormatType.TEXT);
            hmType.put("ttl_foc_qty", POIUtil.FormatType.TEXT);
            hmType.put("ttl_pur_smallest", POIUtil.FormatType.FLOAT);
            hmType.put("smallest_cost", POIUtil.FormatType.FLOAT);
            hmType.put("amount", POIUtil.FormatType.DOUBLE);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
