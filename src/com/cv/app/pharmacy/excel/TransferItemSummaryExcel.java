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
public class TransferItemSummaryExcel extends GenExcel {
    static Logger log = Logger.getLogger(TransferItemSummaryExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public TransferItemSummaryExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "SELECT\n" +
"    get_qty_in_str(sum(tran_smallest_qty),med.unit_smallest,med.unit_str)ttl_qty,\n" +
"     sum(vt.amount)amount, vt.med_id,sum(tran_smallest_qty) sqty, vt.med_name,concat(vt.from_location,'-',vt.to_location) loc_group,\n" +
"locf.location_name from_loc, loct.location_name to_loc, med.rel_str, vt.transfer_id, vt.tran_date \n" +
"  FROM\n" +
"     v_transfer vt INNER JOIN v_med_unit_smallest_rel med ON vt.med_id = med.med_id\n" +
"inner join location locf on vt.from_location = locf.location_id inner join location loct on\n" +
"vt.to_location = loct.location_id\n" +
" where\n" +
"vt.med_id = med.med_id and vt.deleted = false\n" +
"   and date(vt.tran_date) between $P{prm_from} and $P{prm_to}\n" +
"   and (vt.from_location = $P{prm_flocation} or $P{prm_flocation} = 0)\n" +
"   and (vt.to_location = $P{prm_tlocation} or $P{prm_tlocation} = 0)\n" +
"   and (vt.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}))\n" +
"group by vt.med_id,vt.med_name, vt.transfer_id, vt.tran_date\n" +
"order by vt.tran_date,vt.transfer_id,locf.location_name";
        

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{prm_from}", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getToDate() + "'")
                .replace("$P{prm_flocation}", getLocationId())
                .replace("$P{prm_tlocation}", "'" + getToLocationId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Date");
            listHeader.add("Vou No");
            listHeader.add("From");
            listHeader.add("To");
            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("Packing");
            listHeader.add("Qty");
            listHeader.add("Qty Smallest");
            listHeader.add("Amount");
            
            List<String> listField = new ArrayList();
            listField.add("tran_date");
            listField.add("transfer_id");
            listField.add("from_loc");
            listField.add("to_loc");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("rel_str");
            listField.add("ttl_qty");
            listField.add("sqty");
            listField.add("amount");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("tran_date", POIUtil.FormatType.TEXT);
            hmType.put("transfer_id", POIUtil.FormatType.TEXT);
            hmType.put("from_loc", POIUtil.FormatType.TEXT);
            hmType.put("to_loc", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("rel_str", POIUtil.FormatType.TEXT);
            hmType.put("ttl_qty", POIUtil.FormatType.TEXT);
            hmType.put("sqty", POIUtil.FormatType.FLOAT);
            hmType.put("amount", POIUtil.FormatType.DOUBLE);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
