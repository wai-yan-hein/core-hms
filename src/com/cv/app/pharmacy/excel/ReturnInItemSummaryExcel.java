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
public class ReturnInItemSummaryExcel extends GenExcel {

    static Logger log = Logger.getLogger(ReturnInItemSummaryExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public ReturnInItemSummaryExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select a.med_id, a.med_name, a.med_rel_str, a.item_type_code, a.item_type_name,\n"
                + "   get_qty_in_str(a.opd_qty,a.unit_smallest, a.unit_str) as opd_ttl_qty_str,\n"
                + "   get_qty_in_str(a.imp_qty,a.unit_smallest, a.unit_str) as imp_ttl_qty_str,\n"
                + "   a.opd_qty, a.imp_qty, a.opd_amt, a.imp_amt, (a.opd_amt+a.imp_amt) amount,\n"
                + " a.ret_in_id, a.ret_in_date "
                + "from (\n"
                + "SELECT 	vri.currency,c.cur_name,vri.med_id,vri.med_name, m.med_rel_str, it.item_type_code,\n"
                + "       it.item_type_name, vmusr.unit_smallest, vmusr.unit_str,\n"
                + "	sum(if(ifnull(vri.admission_no,'') = '',vri.ret_in_smallest_qty,0)) as opd_qty,\n"
                + "    sum(if(ifnull(vri.admission_no,'') = '',0,vri.ret_in_smallest_qty)) as imp_qty,\n"
                + "    sum(vri.ret_in_smallest_qty) as ttl_retin,\n"
                + "	sum(if(ifnull(vri.admission_no,'') = '',vri.ret_in_amount, 0)) as opd_amt,\n"
                + "    sum(if(ifnull(vri.admission_no,'') = '', 0,vri.ret_in_amount)) as imp_amt,\n"
                + "    sum(vri.ret_in_amount) as ttl_retin_amt,\n"
                + " vri.ret_in_id, date(vri.ret_in_date) as ret_in_date "
                + "FROM 	v_return_in vri, currency c, v_med_unit_smallest_rel vmusr, medicine m, item_type it\n"
                + "WHERE 	vri.currency = c.cur_code\n"
                + "and     vri.med_id = m.med_id\n"
                + "and 	m.med_type_id = it.item_type_code\n"
                + "AND 	vri.med_id = vmusr.med_id\n"
                + "AND 	vri.deleted = FALSE\n"
                + "AND 	DATE(vri.ret_in_date) BETWEEN $P{prm_from} AND $P{prm_to}\n"
                + "AND 	(vri.location = $P{prm_location} OR $P{prm_location} = 0)\n"
                + "AND 	(vri.currency = $P{prm_currency} OR $P{prm_currency} = '-')\n"
                + "and (vri.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}))\n"
                + "GROUP BY vri.currency, c.cur_name, vri.med_id, vri.med_name, m.med_rel_str,\n"
                + "it.item_type_code, it.item_type_name, vmusr.unit_smallest, vmusr.unit_str,"
                + "vri.ret_in_id, date(vri.ret_in_date)) a\n"
                + "order by a.ret_in_date, a.ret_in_id, a.item_type_code,a.item_type_name,a.med_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{prm_from}", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getToDate() + "'")
                .replace("$P{prm_location}", getLocationId())
                .replace("$P{prm_currency}", "'" + getCurrencyId()+ "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Date");
            listHeader.add("Vou No");
            listHeader.add("Code");
            listHeader.add("Item Name");
            listHeader.add("Pk. Size");
            listHeader.add("OPD Qty");
            listHeader.add("OPD S-Qty");
            listHeader.add("Inp-Qty");
            listHeader.add("Inp S-Qty");
            listHeader.add("OPD Amount");
            listHeader.add("Inp-Amount");

            List<String> listField = new ArrayList();
            listField.add("ret_in_date");
            listField.add("ret_in_id");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("med_rel_str");
            listField.add("opd_ttl_qty_str");
            listField.add("opd_qty");
            listField.add("imp_ttl_qty_str");
            listField.add("imp_qty");
            listField.add("opd_amt");
            listField.add("imp_amt");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("ret_in_date", POIUtil.FormatType.TEXT);
            hmType.put("ret_in_id", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("med_rel_str", POIUtil.FormatType.TEXT);
            hmType.put("opd_ttl_qty_str", POIUtil.FormatType.TEXT);
            hmType.put("opd_qty", POIUtil.FormatType.FLOAT);
            hmType.put("imp_qty", POIUtil.FormatType.TEXT);
            hmType.put("imp_qty", POIUtil.FormatType.FLOAT);
            hmType.put("opd_amt", POIUtil.FormatType.DOUBLE);
            hmType.put("imp_amt", POIUtil.FormatType.DOUBLE);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
