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
public class StockBalanceBrandWithSalePriceExcel extends GenExcel {
    static Logger log = Logger.getLogger(StockBalanceBrandWithSalePriceExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public StockBalanceBrandWithSalePriceExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select vsm.user_id, vsm.location_id, loc.location_name, vsm.med_id, med.med_name, med.rel_str, ps.brand_name system_desp,\n" +
"get_qty_in_str(vsm.ttl_stock_balance, med.unit_smallest, med.unit_str) ttl_stock_balance, vsm.ttl_stock_balance as ttl_bal,\n" +
"a.sale_price, (vsm.ttl_stock_balance*a.sale_price) as amount\n" +
"from v_stock_movement vsm\n" +
"join v_med_unit_smallest_rel med on vsm.med_id = med.med_id\n" +
"join location loc on vsm.location_id = loc.location_id\n" +
"join medicine med1 on vsm.med_id = med1.med_id\n" +
"left join item_brand ps on med1.brand_id = ps.brand_id\n" +
"join (select rel.med_id, rg.sale_price\n" +
"from relation_group rg, v_med_rel_smallest_unit rel\n" +
"where rg.rel_group_id = rel.rel_group_id) a on vsm.med_id = a.med_id\n" +
"where  vsm.user_id = $P{user_id}\n" +
"order by ps.brand_name,  med.med_name, vsm.location_id";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Location");
            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("Packing");
            listHeader.add("Balance");
            listHeader.add("Smallest Balance");
            listHeader.add("Price");
            listHeader.add("Amount");
            
            List<String> listField = new ArrayList();
            listField.add("location_name");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("rel_str");
            listField.add("ttl_stock_balance");
            listField.add("ttl_bal");
            listField.add("sale_price");
            listField.add("amount");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("location_name", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("rel_str", POIUtil.FormatType.TEXT);
            hmType.put("ttl_stock_balance", POIUtil.FormatType.TEXT);
            hmType.put("ttl_bal", POIUtil.FormatType.FLOAT);
            hmType.put("sale_price", POIUtil.FormatType.FLOAT);
            hmType.put("amount", POIUtil.FormatType.FLOAT);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
