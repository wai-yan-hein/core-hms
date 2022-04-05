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
public class GPExcel extends GenExcel {
    static Logger log = Logger.getLogger(GPExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public GPExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select gp.item_id, med.med_name, round(gp.op_value,2) op_value, round(gp.ttl_pur,2) ttl_pur,\n" +
"round(gp.cl_value,2) cl_value, round(gp.cogs_value,2) cogs_value, round(gp.ttl_sale,2) ttl_sale,\n" +
"round(gp.gp_value,2) gp_value\n" +
"from gross_profits gp, medicine med\n" +
"where gp.item_id = med.med_id and user_id = $P{user_id}\n" +
"and (med.med_type_id = $P{item_type} or $P{item_type} = 0)\n" +
"and (med.category_id = $P{cate_id} or $P{cate_id} = 0)\n" +
"and (med.brand_id = $P{brand_id} or $P{brand_id} = 0)\n" +
"and (gp.item_id in (select distinct item_code from tmp_item_code_filter where user_id = $P{user_id})\n" +
"     or (select count(*) from tmp_item_code_filter where user_id = $P{user_id}) = 0)";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{cg_id}", getItemGroup())
                .replace("$P{item_type}", getItemType())
                .replace("$P{cate_id}", getCategoryId())
                .replace("$P{brand_id}", getBrandId());
        
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("Adj-Value");
            listHeader.add("Return In Value");
            listHeader.add("Damage Value");
            listHeader.add("Cost of Sale");
            listHeader.add("Total Sale");
            listHeader.add("GP");
            
            List<String> listField = new ArrayList();
            listField.add("item_id");
            listField.add("med_name");
            listField.add("op_value");
            listField.add("ttl_pur");
            listField.add("cl_value");
            listField.add("cogs_value");
            listField.add("ttl_sale");
            listField.add("gp_value");
            
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("item_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("op_value", POIUtil.FormatType.DOUBLE);
            hmType.put("ttl_pur", POIUtil.FormatType.DOUBLE);
            hmType.put("cl_value", POIUtil.FormatType.DOUBLE);
            hmType.put("cogs_value", POIUtil.FormatType.DOUBLE);
            hmType.put("ttl_sale", POIUtil.FormatType.INTEGER);
            hmType.put("gp_value", POIUtil.FormatType.DOUBLE);
            
            strSql = strSql.replace("-", "0");
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
