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
public class ItemCodeListWithPurPriceExcel extends GenExcel {
    static Logger log = Logger.getLogger(ItemCodeListWithPurPriceExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public ItemCodeListWithPurPriceExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select med.med_id, med.med_name, rg.item_unit,rg.rel_group_id, itp.item_type_name,\n" +
            "med.med_type_id,med.med_rel_str,med.active,\n" +
            "med.pur_smallest_price pur_price,\n" +
            "(ifnull(rg.sale_price,0)/rg.smallest_qty) sale_price,\n" +
            "ifnull(rg.sale_pric_a,' ') sale_pric_a,\n" +
            "ifnull(rg.sale_pric_b,' ') sale_pric_b,\n" +
            "ifnull(rg.sale_pric_c,' ') sale_pric_c,\n" +
            "ifnull(rg.sale_pric_d,' ') sale_pric_d,\n" +
            "ifnull(ib.brand_name,'-') brand_name,\n" +
            "pcost.smallest_pur_cost\n" +
            "from (select distinct med.med_id, med.med_name,med.med_type_id,med.med_rel_str,med.active, med.pur_price,\n" +
            "             (med.pur_price/rg.smallest_qty) pur_smallest_price, med.brand_id\n" +
            "        from medicine med, med_rel mr, relation_group rg\n" +
            "       where med.med_id = mr.med_id and mr.rel_group_id = rg.rel_group_id\n" +
            "         and (med.pur_unit = rg.item_unit or med.pur_unit is null)) med\n" +
            "left join item_brand ib on med.brand_id =ib.brand_id\n" +
            "join med_rel mr on med.med_id = mr.med_id\n" +
            "join relation_group rg on mr.rel_group_id = rg.rel_group_id\n" +
            "join item_type itp on med.med_type_id = itp.item_type_code\n" +
            "join v_med_rel_smallest_unit vmrs on rg.rel_group_id = vmrs.rel_group_id and med.med_id = vmrs.med_id\n" +
            "join (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}) filt on med.med_id = filt.med_id\n" +
            "left join (select vp.med_id, vp.pur_unit_cost, vp.pur_unit, vmr.smallest_qty as unit_smallest_qty,\n" +
            "(vp.pur_unit_cost/vmr.smallest_qty) as smallest_pur_cost\n" +
            "from v_purchase vp\n" +
            "join (select med_id, max(pur_detail_id) as detail_id\n" +
            "        from pur_detail_his group by med_id) m on vp.med_id = m.med_id\n" +
            "         and vp.pur_detail_id = m.detail_id\n" +
            "join v_med_rel vmr on vp.med_id = vmr.med_id and vp.pur_unit = vmr.item_unit\n" +
            "where vp.deleted = false\n" +
            "order by vp.med_id) pcost on med.med_id = pcost.med_id\n" +
            "group by med.med_id, med.med_name,med.pur_price,med.med_rel_str,med.active, rg.item_unit,rg.sale_price,rg.sale_pric_a,rg.sale_pric_b\n" +
            ",rg.sale_pric_c,rg.sale_pric_d, itp.item_type_name, med.med_type_id,\n" +
            "ib.brand_name\n" +
            "order by med.active,med.med_type_id,med.med_id,rg.rel_group_id,ib.brand_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Item Type");
            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("Brand Name");
            listHeader.add("Packing");
            listHeader.add("UOM");
            listHeader.add("Pur Price");
            listHeader.add("Pur Cost");
            listHeader.add("Sale Price");
            listHeader.add("Status");
            
            List<String> listField = new ArrayList();
            listField.add("item_type_name");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("brand_name");
            listField.add("med_rel_str");
            listField.add("item_unit");
            listField.add("pur_price");
            listField.add("smallest_pur_cost");
            listField.add("sale_price");
            listField.add("active");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("item_type_name", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("brand_name", POIUtil.FormatType.TEXT);
            hmType.put("med_rel_str", POIUtil.FormatType.TEXT);
            hmType.put("item_unit", POIUtil.FormatType.TEXT);
            hmType.put("pur_price", POIUtil.FormatType.FLOAT);
            hmType.put("smallest_pur_cost", POIUtil.FormatType.FLOAT);
            hmType.put("sale_price", POIUtil.FormatType.FLOAT);
            hmType.put("active", POIUtil.FormatType.TEXT);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
