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
public class ItemCodeListWitSalePriceExcel extends GenExcel {

    static Logger log = Logger.getLogger(ItemCodeListWitSalePriceExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public ItemCodeListWitSalePriceExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select it.item_type_name, cat.cat_name, ib.brand_name, vmr.med_id, vmr.med_name, vmr.med_rel_str, \n"
                + "group_concat(concat(vmr.sale_price, '/',vmr.item_unit) separator ',') sale_price,\n"
                + "group_concat(concat(vmr.sale_pric_a, '/',vmr.item_unit) separator ',') sale_pric_a,\n"
                + "group_concat(concat(vmr.sale_pric_b, '/',vmr.item_unit) separator ',') sale_pric_b,\n"
                + "group_concat(concat(vmr.sale_pric_c, '/',vmr.item_unit) separator ',') sale_pric_c,\n"
                + "group_concat(concat(vmr.sale_pric_d, '/',vmr.item_unit) separator ',') sale_pric_d\n"
                + "from v_med_rel vmr\n"
                + "left join item_type it on vmr.med_type_id = it.item_type_code\n"
                + "left join category cat on vmr.category_id = cat.cat_id\n"
                + "left join item_brand ib on vmr.brand_id = ib.brand_id\n"
                + "join (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}) filt on vmr.med_id = filt.med_id\n"
                + "group by it.item_type_name, cat.cat_name, ib.brand_name, vmr.med_id, vmr.med_name, vmr.med_rel_str\n"
                + "order by it.item_type_name, cat.cat_name, ib.brand_name, vmr.med_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Item Type");
            listHeader.add("Category");
            listHeader.add("Brand Name");
            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("UOM");
            listHeader.add("Sale Price");

            List<String> listField = new ArrayList();
            listField.add("item_type_name");
            listField.add("cat_name");
            listField.add("brand_name");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("med_rel_str");
            listField.add("sale_price");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("item_type_name", POIUtil.FormatType.TEXT);
            hmType.put("cat_name", POIUtil.FormatType.TEXT);
            hmType.put("brand_name", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("med_rel_str", POIUtil.FormatType.TEXT);
            hmType.put("sale_price", POIUtil.FormatType.TEXT);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
