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
public class StockInOutExcel extends GenExcel {

    static Logger log = Logger.getLogger(StockInOutExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public StockInOutExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select tsio.*, loc.location_name,med.med_rel_str\n"
                + "from tmp_stock_in_out tsio, location loc,medicine med\n"
                + "where tsio.location_id = loc.location_id and med.med_id=tsio.item_id\n"
                + "and tsio.user_id = $P{user_id}";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Location");
            listHeader.add("Date");
            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("UOM");
            listHeader.add("Opening");
            listHeader.add("Purchase");
            listHeader.add("Receive");
            listHeader.add("Transfer");
            listHeader.add("Adjust");
            listHeader.add("Ret In");
            listHeader.add("Total");
            listHeader.add("Sale");
            listHeader.add("Issue");
            listHeader.add("Transfer");
            listHeader.add("Adjust");
            listHeader.add("Ret Out");
            listHeader.add("Damage");
            listHeader.add("Total");
            listHeader.add("Closing");

            List<String> listField = new ArrayList();
            listField.add("location_name");
            listField.add("tran_date");
            listField.add("item_id");
            listField.add("item_name");
            listField.add("med_rel_str");
            listField.add("op_qty");

            listField.add("pur_qty");
            listField.add("rcv_qtyr");
            listField.add("tran_in_qty");
            listField.add("adj_in_qty");
            listField.add("ret_in_qty");
            // listField.add("ret_out_qty_str");
            listField.add("in_ttl_str");
            listField.add("sale_qty");
            listField.add("issue_qty");
            listField.add("tran_out_qty");
            listField.add("adj_out_qty");
            listField.add("ret_out_qty");
            listField.add("dmg_qty");
            listField.add("out_ttl");
            listField.add("closing");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("location_name", POIUtil.FormatType.TEXT);
            hmType.put("tran_date", POIUtil.FormatType.TEXT);
            hmType.put("item_id", POIUtil.FormatType.TEXT);
            hmType.put("item_name", POIUtil.FormatType.TEXT);
            hmType.put("op_qty", POIUtil.FormatType.FLOAT);
           hmType.put("med_rel_str", POIUtil.FormatType.TEXT);
            hmType.put("pur_qty", POIUtil.FormatType.FLOAT);
            hmType.put("rcv_qty", POIUtil.FormatType.FLOAT);
            hmType.put("tran_in_qty", POIUtil.FormatType.FLOAT);
            hmType.put("adj_in_qty", POIUtil.FormatType.FLOAT);
            hmType.put("ret_in_qty", POIUtil.FormatType.FLOAT);
            hmType.put("in_ttl", POIUtil.FormatType.FLOAT);
            hmType.put("sale_qty", POIUtil.FormatType.FLOAT);
            hmType.put("issue_qty", POIUtil.FormatType.FLOAT);
            hmType.put("tran_out_qty", POIUtil.FormatType.FLOAT);
            hmType.put("adj_out_qty", POIUtil.FormatType.FLOAT);
            hmType.put("ret_out_qty", POIUtil.FormatType.FLOAT);
            hmType.put("dmg_qty", POIUtil.FormatType.FLOAT);
            hmType.put("out_ttl", POIUtil.FormatType.FLOAT);
            hmType.put("closing", POIUtil.FormatType.FLOAT);
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
