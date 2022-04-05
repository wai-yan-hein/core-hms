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
public class CostingExcel extends GenExcel {
    static Logger log = Logger.getLogger(CostingExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public CostingExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select tsc.med_id, med.med_name, med.med_rel_str, round(tsc.total_cost,2) total_cost,tsc.bal_qty,\n" +
"round(tsc.total_cost/tsc.bal_qty,2) as smallest_cost, opc.total_qty as qty_op,\n" +
"opc.ttl_amount as total_cost_op, opc.smallest_cost_op, (round(tsc.total_cost/tsc.bal_qty,2) - opc.smallest_cost_op) as cs_op\n" +
"from tmp_stock_costing tsc"
                + " join medicine med on tsc.med_id = med.med_id \n" +
" left join (select a.*, (a.ttl_amount/a.total_qty) smallest_cost_op from (\n" +
"select med_id, sum(op_qty) total_qty, sum(op_qty*cost_price) ttl_amount\n" +
"from v_stock_op vso\n" +
"group by med_id) a where a.total_qty <> 0) opc on tsc.med_id = opc.med_id \n" +
"where tsc.tran_option = 'Opening'\n" +
"and tsc.user_id = $P{user_id} \n" +
"and (tsc.med_id in (select distinct item_code from tmp_item_code_filter where user_id = $P{user_id})\n" +
"     or (select count(*) from tmp_item_code_filter where user_id = $P{user_id}) = 0)\n" +
" and (tsc.med_id in (select item_id from item_group_detail where group_id = $P{cg_id}) " +
"     or $P{cg_id}=0) " +
" order by tsc.med_id";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{cg_id}", getItemGroup());
        
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Item Code");
            listHeader.add("Item Name");
            listHeader.add("Packing");
            listHeader.add("Total Qty");
            listHeader.add("Total Cost");
            listHeader.add("Smallest Cost");
            listHeader.add("OP Qty");
            listHeader.add("OP Ttl Cost");
            listHeader.add("OP Smallest Cost");
            listHeader.add("Difference (C-OP)");
            
            List<String> listField = new ArrayList();
            listField.add("med_id");
            listField.add("med_name");
            listField.add("med_rel_str");
            
            listField.add("bal_qty");
            listField.add("total_cost");
            listField.add("smallest_cost");
            listField.add("qty_op");
            listField.add("total_cost_op");
            listField.add("smallest_cost_op");
            listField.add("cs_op");
            
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("med_rel_str", POIUtil.FormatType.TEXT);
            
            hmType.put("bal_qty", POIUtil.FormatType.INTEGER);
            hmType.put("total_cost", POIUtil.FormatType.DOUBLE);
            hmType.put("smallest_cost", POIUtil.FormatType.DOUBLE);
            hmType.put("qty_op", POIUtil.FormatType.INTEGER);
            hmType.put("total_cost_op", POIUtil.FormatType.DOUBLE);
            hmType.put("smallest_cost_op", POIUtil.FormatType.DOUBLE);
            hmType.put("cs_op", POIUtil.FormatType.FLOAT);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
