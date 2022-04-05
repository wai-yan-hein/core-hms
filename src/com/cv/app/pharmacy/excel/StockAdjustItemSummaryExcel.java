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
public class StockAdjustItemSummaryExcel extends GenExcel {

    static Logger log = Logger.getLogger(StockAdjustItemSummaryExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public StockAdjustItemSummaryExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "SELECT loct.location_name,vd.med_id, med.med_name,\n"
                + "       if(get_qty_in_str(sum(if(adj_type='+',adj_smallest_qty,0)),med.unit_smallest,med.unit_str)='null','',\n"
                + "        get_qty_in_str(sum(if(adj_type='+',adj_smallest_qty,0)),med.unit_smallest,med.unit_str)) ttl_p_qty,\n"
                + "       if(get_qty_in_str(sum(if(adj_type='-',adj_smallest_qty,0)),med.unit_smallest,med.unit_str)='null','',\n"
                + "        get_qty_in_str(sum(if(adj_type='-',adj_smallest_qty,0)),med.unit_smallest,med.unit_str)) ttl_m_qty,\n"
                + "       sum(if(adj_type='+',adj_smallest_qty,0)) sqty_p,\n"
                + "       sum(if(adj_type='-',adj_smallest_qty,0)) sqty_m,\n"
                + "	   sum(if(adj_type='+',adj_smallest_qty * (vd.cost_price/rg.smallest_qty),0)) amt_p,\n"
                + "       sum(if(adj_type='-',adj_smallest_qty * (vd.cost_price/rg.smallest_qty),0)) amt_m,\n"
                + "       (vd.cost_price/rg.smallest_qty) smallest_cost,\n"
                + "       med1.med_rel_str, vd.remark\n"
                + "  FROM v_adj vd INNER JOIN v_med_unit_smallest_rel med ON vd.med_id = med.med_id\n"
                + " inner join location loct on vd.location = loct.location_id\n"
                + " inner join medicine med1 on vd.med_id = med1.med_id\n"
                + " inner join med_rel mr on med1.med_id = mr.med_id\n"
                + " inner join relation_group rg on mr.rel_group_id = rg.rel_group_id and vd.item_unit = rg.item_unit\n"
                + " where vd.med_id = med.med_id and vd.deleted = false\n"
                + "   and date(vd.adj_date) between $P{prm_from} and $P{prm_to}\n"
                + "   and (vd.location = $P{prm_location} or $P{prm_location} = 0)\n"
                + "   and (vd.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}))\n"
                + "group by loct.location_name, vd.med_id,med.med_name, med.unit_smallest,med.unit_str,\n"
                + "vd.cost_price,rg.smallest_qty,med1.med_rel_str, vd.remark\n"
                + "order by loct.location_name, vd.med_id, med.med_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{prm_from}", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getToDate() + "'")
                .replace("$P{prm_location}", getLocationId());
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Remark");
            listHeader.add("Location");
            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("Packing");
            listHeader.add("+ Qty");
            listHeader.add("+ Qty S");
            listHeader.add("- Qty");
            listHeader.add("- Qty S");
            listHeader.add("S Cost");
            listHeader.add("+ Amount");
            listHeader.add("- Amount");
            
            List<String> listField = new ArrayList();
            listField.add("remark");
            listField.add("location_name");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("med_rel_str");
            
            listField.add("ttl_p_qty");
            listField.add("sqty_p");
            listField.add("ttl_m_qty");
            listField.add("sqty_m");
            listField.add("smallest_cost");
            listField.add("amt_p");
            listField.add("amt_m");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("remark", POIUtil.FormatType.TEXT);
            hmType.put("location_name", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("med_rel_str", POIUtil.FormatType.TEXT);
            
            hmType.put("ttl_p_qty", POIUtil.FormatType.TEXT);
            hmType.put("sqty_p", POIUtil.FormatType.FLOAT);
            hmType.put("ttl_m_qty", POIUtil.FormatType.TEXT);
            hmType.put("sqty_m", POIUtil.FormatType.FLOAT);
            hmType.put("smallest_cost", POIUtil.FormatType.FLOAT);
            hmType.put("amt_p", POIUtil.FormatType.DOUBLE);
            hmType.put("amt_m", POIUtil.FormatType.DOUBLE);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
