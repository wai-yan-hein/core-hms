package com.cv.app.pharmacy.excel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class StockMovementExcel extends GenExcel {

    static Logger log = Logger.getLogger(StockMovementExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public StockMovementExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select vsm.user_id, vsm.location_id, loc.location_name, vsm.med_id, med.med_name,\n"
                + "vsm.ttl_op ttl_op,\n"
                + "vsm.ttl_put ttl_pur,\n"
                + "vsm.ttl_ret_in ttl_ret_in,\n"
                + "vsm.ttl_trn_to ttl_trn_to,\n"
                + "vsm.ttl_rec ttl_rec,\n"
                + "vsm.ttl_adj_in ttl_adj_in,\n"
                + "vsm.ttl_stock_in ttl_stock_in,\n"
                + "vsm.ttl_sale ttl_sale,\n"
                + "vsm.ttl_ret_out ttl_ret_out,\n"
                + "vsm.ttl_trn_from ttl_trn_from,\n"
                + "vsm.ttl_iss ttl_iss,\n"
                + "vsm.ttl_adj_out ttl_adj_out,\n"
                + "vsm.ttl_damage ttl_damage,\n"
                + "vsm.ttl_stock_out ttl_stock_out,\n"
                + "vsm.ttl_stock_balance ttl_stock_balance, vsm.ttl_stock_balance as ttl_bal,\n"
                +"med.rel_str\n"
                + "from v_stock_movement vsm, v_med_unit_smallest_rel med, location loc\n"
                + "where vsm.med_id = med.med_id and vsm.location_id = loc.location_id\n"
                + "and vsm.user_id = $P{user_id}\n"
                + "order by vsm.location_id, vsm.med_id";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Location");
            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("UOM");
            listHeader.add("Opening");
            listHeader.add("Purchase");
            listHeader.add("Recieve");
            listHeader.add("Transfer");
            listHeader.add("Adjust");
            listHeader.add("Return In");
            listHeader.add("Total");
            listHeader.add("Sale");
            listHeader.add("Issue");
            listHeader.add("Transfer");
            listHeader.add("Return Out");
            listHeader.add("Damage");
            listHeader.add("Total");
            listHeader.add("Closing");

            List<String> listField = new ArrayList();
            listField.add("location_name");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("rel_str");
            listField.add("ttl_op");
            listField.add("ttl_pur");
            listField.add("ttl_rec");
            listField.add("ttl_trn_to");
            listField.add("ttl_adj_in");
            listField.add("ttl_ret_in");
            listField.add("ttl_stock_in");
            listField.add("ttl_sale");
            listField.add("ttl_iss");
            listField.add("ttl_trn_from");
            listField.add("ttl_ret_out");
            listField.add("ttl_damage");
            listField.add("ttl_stock_out");
            listField.add("ttl_bal");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("location_name", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("rel_str", POIUtil.FormatType.TEXT);
            hmType.put("ttl_op", POIUtil.FormatType.FLOAT);
            hmType.put("ttl_pur", POIUtil.FormatType.FLOAT);
            hmType.put("ttl_rec", POIUtil.FormatType.FLOAT);
            hmType.put("ttl_trn_to", POIUtil.FormatType.FLOAT);
            hmType.put("ttl_adj_in", POIUtil.FormatType.FLOAT);
            hmType.put("ttl_ret_in", POIUtil.FormatType.FLOAT);
            hmType.put("ttl_stock_in", POIUtil.FormatType.FLOAT);
            hmType.put("ttl_sale", POIUtil.FormatType.FLOAT);
            hmType.put("ttl_iss", POIUtil.FormatType.FLOAT);
            hmType.put("ttl_trn_from", POIUtil.FormatType.FLOAT);
            hmType.put("ttl_ret_out", POIUtil.FormatType.FLOAT);
            hmType.put("ttl_damage", POIUtil.FormatType.FLOAT);
            hmType.put("ttl_stock_out", POIUtil.FormatType.FLOAT);
            hmType.put("ttl_bal", POIUtil.FormatType.FLOAT);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
