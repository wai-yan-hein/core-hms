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
public class StockBalanceExcel extends GenExcel {
    static Logger log = Logger.getLogger(StockBalanceExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public StockBalanceExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select vsm.user_id, vsm.location_id, loc.location_name, "
                + "vsm.med_id, med.med_name, med.rel_str,\n" +
            "get_qty_in_str(vsm.ttl_stock_balance, med.unit_smallest, med.unit_str) "
                + "ttl_stock_balance, vsm.ttl_stock_balance as ttl_bal\n" +
            "from v_stock_movement vsm, v_med_unit_smallest_rel med, location loc\n" +
            "where vsm.med_id = med.med_id and vsm.location_id = loc.location_id\n" +
            "and vsm.user_id = $P{user_id}\n" +
            "order by vsm.location_id, vsm.med_id";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Location");
            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("Packing");
            listHeader.add("Balance");
            listHeader.add("Smallest Balance");
            
            List<String> listField = new ArrayList();
            listField.add("location_name");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("rel_str");
            listField.add("ttl_stock_balance");
            listField.add("ttl_bal");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("location_name", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("rel_str", POIUtil.FormatType.TEXT);
            hmType.put("ttl_stock_balance", POIUtil.FormatType.TEXT);
            hmType.put("ttl_bal", POIUtil.FormatType.FLOAT);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
