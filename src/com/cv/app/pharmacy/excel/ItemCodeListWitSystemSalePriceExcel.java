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
public class ItemCodeListWitSystemSalePriceExcel extends GenExcel {

    static Logger log = Logger.getLogger(ItemCodeListWitSystemSalePriceExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public ItemCodeListWitSystemSalePriceExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select ps.system_desp, med.med_id, med.med_name, med.med_rel_str,\n"
                + "ifnull(group_concat(concat(rg.sale_price, '/',rg.item_unit) separator ','),' ') sale_price,\n"
                + "ifnull(group_concat(concat(rg.sale_pric_a, '/',rg.item_unit) separator ','), ' ') sale_pric_a,\n"
                + "ifnull(group_concat(concat(rg.sale_pric_b, '/',rg.item_unit) separator ','), ' ') sale_pric_b,\n"
                + "ifnull(group_concat(concat(rg.sale_pric_c, '/',rg.item_unit) separator ','), ' ') sale_pric_c,\n"
                + "ifnull(group_concat(concat(rg.sale_pric_d, '/',rg.item_unit) separator ','), ' ') sale_pric_d,\n"
                + "ifnull(group_concat(concat(rg.std_cost, '/',rg.item_unit) separator ','), ' ') sale_pric_e\n"
                + "from medicine med\n"
                + "join med_rel mr on med.med_id = mr.med_id\n"
                + "join relation_group rg on mr.rel_group_id = rg.rel_group_id\n"
                + "join item_type itp on med.med_type_id = itp.item_type_code\n"
                + "join (select distinct med_id from tmp_stock_filter where user_id = $P!{user_id}) filt on med.med_id = filt.med_id\n"
                + "left join phar_system ps on med.phar_sys_id = ps.id\n"
                + "where med.med_type_id in ('01','02','03','04','05','06','07','08','09','10','11','12')\n"
                + "group by med.med_id, med.med_name, med.med_rel_str, ps.system_desp\n"
                + "order by  ps.system_desp,med.med_name";

        strSql = strSql.replace("$P!{user_id}", "'" + getUserId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("System");
            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("Packing");
            listHeader.add("Sale Price");
            listHeader.add("Price A");
            listHeader.add("Price B");
            listHeader.add("Price C");
            listHeader.add("Price D");
            listHeader.add("Price E");
            
            List<String> listField = new ArrayList();
            listField.add("system_desp");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("med_rel_str");
            listField.add("sale_price");
            listField.add("sale_pric_a");
            listField.add("sale_pric_b");
            listField.add("sale_pric_c");
            listField.add("sale_pric_d");
            listField.add("sale_pric_e");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("system_desp", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("med_rel_str", POIUtil.FormatType.TEXT);
            hmType.put("sale_price", POIUtil.FormatType.TEXT);
            hmType.put("sale_pric_a", POIUtil.FormatType.TEXT);
            hmType.put("sale_pric_b", POIUtil.FormatType.TEXT);
            hmType.put("sale_pric_c", POIUtil.FormatType.TEXT);
            hmType.put("sale_pric_d", POIUtil.FormatType.TEXT);
            hmType.put("sale_pric_e", POIUtil.FormatType.TEXT);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
