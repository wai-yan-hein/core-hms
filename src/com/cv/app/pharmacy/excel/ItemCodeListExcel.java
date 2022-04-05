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
public class ItemCodeListExcel extends GenExcel {
    static Logger log = Logger.getLogger(ItemCodeListExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public ItemCodeListExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select med.med_id, med.med_name, med.med_rel_str, itp.item_type_name, med.med_type_id,\n"
                + "group_concat(concat(rg.sale_price, '/',rg.item_unit) separator ',') sale_price,\n"
                + "group_concat(concat(rg.sale_pric_a, '/',rg.item_unit) separator ',') sale_pric_a,\n"
                + "group_concat(concat(rg.sale_pric_b, '/',rg.item_unit) separator ',') sale_pric_b,\n"
                + "group_concat(concat(rg.sale_pric_c, '/',rg.item_unit) separator ',') sale_pric_c,\n"
                + "group_concat(concat(rg.sale_pric_d, '/',rg.item_unit) separator ',') sale_pric_d\n"
                + "from medicine med, med_rel mr, relation_group rg, item_type itp,\n"
                + "(select distinct med_id from tmp_stock_filter where user_id = $P{user_id}) filt\n"
                + "where med.med_id = mr.med_id and mr.rel_group_id = rg.rel_group_id\n"
                + "and med.med_id = filt.med_id and med.med_type_id = itp.item_type_code\n"
                + "group by med.med_id, med.med_name, med.med_rel_str, itp.item_type_name, med.med_type_id\n"
                + "order by itp.item_type_name, med.med_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Item Type");
            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("Packing");
            
            List<String> listField = new ArrayList();
            listField.add("item_type_name");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("med_rel_str");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("item_type_name", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("med_rel_str", POIUtil.FormatType.TEXT);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
