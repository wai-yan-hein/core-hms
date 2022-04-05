/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.util;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.DateUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class PharmacyReport {
    static Logger log = Logger.getLogger(PharmacyReport.class.getName());
    
    public static void StockBalanceExp(AbstractDataAccess dao, String stockDate, 
            String location, String userId){
        String strSql = "insert into tmp_stock_balance_exp(user_id, tran_option, location_id, med_id, exp_date,\n" +
"                bal_qty, qty_str)\n" +
"    select prm_user_id, A.tran_option, A.location_id, A.med_id,\n" +
"           ifnull(A.exp_date, '1900-01-01'), sum(A.ttl_qty),\n" +
"           get_qty_in_str(ifnull(sum(A.ttl_qty),0), B.unit_smallest, B.unit_str)\n" +
"      from (\n" +
"            select if(prm_tran_opt = 'Balance', 'Opening', prm_tran_opt) tran_option,\n" +
"                   vso.location location_id, vso.med_id, vso.expire_date exp_date,\n" +
"                   sum(ifnull(vso.op_smallest_qty,0)) ttl_qty\n" +
"              from v_stock_op vso, tmp_stock_filter tsf\n" +
"             where vso.location = tsf.location_id and vso.med_id = tsf.med_id\n" +
"               and vso.op_date = tsf.op_date and tsf.user_id = prm_user_id\n" +
"               and (vso.location = prm_location or prm_location = 0)\n" +
"             group by vso.location, vso.med_id,vso.expire_date\n" +
"             /*union all\n" +
"            select if(prm_tran_opt = 'Balance', 'Sale', prm_tran_opt) tran_option,\n" +
"                   vs.location_id, vs.med_id, vs.expire_date exp_date,\n" +
"                   sum((ifnull(vs.sale_smallest_qty, 0)+ifnull(vs.foc_smallest_qty,0))*-1) ttl_qty\n" +
"              from v_sale vs, tmp_stock_filter tsf\n" +
"             where vs.location_id = tsf.location_id and vs.med_id = tsf.med_id\n" +
"               and date(vs.sale_date) >= tsf.op_date and date(vs.sale_date) <= prm_stock_date\n" +
"               and vs.deleted = false and vs.vou_status = 1 and tsf.user_id = prm_user_id\n" +
"               and (vs.location_id = prm_location or prm_location = 0)\n" +
"             group by vs.location_id, vs.med_id, vs.expire_date\n" +
"             union all\n" +
"            select if(prm_tran_opt = 'Balance', 'Purchase', prm_tran_opt) tran_option,\n" +
"                   vp.location location_id, vp.med_id, vp.expire_date exp_date,\n" +
"                   sum(ifnull(vp.pur_smallest_qty,0)+ifnull(vp.pur_foc_smallest_qty,0)) ttl_qty\n" +
"              from v_purchase vp, tmp_stock_filter tsf\n" +
"             where vp.location = tsf.location_id and vp.med_id = tsf.med_id\n" +
"               and date(vp.pur_date) >= tsf.op_date and date(vp.pur_date) <= prm_stock_date\n" +
"               and vp.deleted = false and vp.vou_status = 1 and tsf.user_id = prm_user_id\n" +
"               and (vp.location = prm_location or prm_location = 0)\n" +
"             group by vp.location, vp.med_id, vp.expire_date\n" +
"             union all\n" +
"            select if(prm_tran_opt = 'Balance', 'Return In', prm_tran_opt) tran_option,\n" +
"                   vri.location location_id, vri.med_id, vri.expire_date exp_date,\n" +
"                   sum(ifnull(vri.ret_in_smallest_qty,0)) ttl_qty\n" +
"              from v_return_in vri, tmp_stock_filter tsf\n" +
"             where vri.location = tsf.location_id and vri.med_id = tsf.med_id\n" +
"               and date(vri.ret_in_date) >= tsf.op_date and tsf.user_id = prm_user_id\n" +
"               and date(vri.ret_in_date) <= prm_stock_date and vri.deleted = false\n" +
"               and (vri.location = prm_location or prm_location = 0)\n" +
"             group by vri.location, vri.med_id, vri.expire_date\n" +
"             union all\n" +
"            select if(prm_tran_opt = 'Balance', 'Return Out', prm_tran_opt) tran_option,\n" +
"                   vro.location location_id, vro.med_id, vro.expire_date exp_date,\n" +
"                   sum(ifnull(ret_out_smallest_qty,0)*-1) ttl_qty\n" +
"              from v_return_out vro, tmp_stock_filter tsf\n" +
"             where vro.location = tsf.location_id and vro.med_id = tsf.med_id\n" +
"               and date(vro.ret_out_date) >= tsf.op_date and tsf.user_id = prm_user_id\n" +
"               and date(vro.ret_out_date) <= prm_stock_date and vro.deleted = false\n" +
"               and (vro.location = prm_location or prm_location = 0)\n" +
"             group by vro.location , vro.med_id, vro.expire_date\n" +
"             union all\n" +
"            select if(prm_tran_opt = 'Balance', 'Adjust', prm_tran_opt) tran_option,\n" +
"                   va.location location_id, va.med_id, va.expire_date exp_date,\n" +
"                   sum(if(va.adj_type = '-',(ifnull(va.adj_smallest_qty,0)*-1),\n" +
"                        ifnull(va.adj_smallest_qty,0))) ttl_qty\n" +
"              from v_adj va, tmp_stock_filter tsf\n" +
"             where va.location = tsf.location_id and va.med_id = tsf.med_id\n" +
"               and date(va.adj_date) >= tsf.op_date and tsf.user_id = prm_user_id\n" +
"               and date(va.adj_date) <= prm_stock_date and va.deleted = false\n" +
"               and (va.location = prm_location or prm_location = 0)\n" +
"             group by va.location, va.med_id, va.expire_date\n" +
"             union all\n" +
"            select if(prm_tran_opt = 'Balance', 'Transfer From', prm_tran_opt) tran_option,\n" +
"                   vt.from_location location_id, vt.med_id, vt.expire_date exp_date,\n" +
"                   sum(ifnull(tran_smallest_qty,0)*-1) ttl_qty\n" +
"              from v_transfer vt, tmp_stock_filter tsf\n" +
"             where vt.from_location = tsf.location_id and vt.med_id = tsf.med_id\n" +
"               and date(vt.tran_date) >= tsf.op_date and tsf.user_id = prm_user_id\n" +
"               and date(vt.tran_date) <= prm_stock_date and vt.deleted = false\n" +
"               and (vt.from_location = prm_location or prm_location = 0)\n" +
"             group by vt.from_location, vt.med_id, vt.expire_date\n" +
"             union all\n" +
"            select if(prm_tran_opt = 'Balance', 'Transfer To', prm_tran_opt) tran_option,\n" +
"                   vt.to_location location_id, vt.med_id, vt.expire_date exp_date,\n" +
"                   sum(ifnull(tran_smallest_qty,0)) ttl_qty\n" +
"              from v_transfer vt, tmp_stock_filter tsf\n" +
"             where vt.to_location = tsf.location_id and vt.med_id = tsf.med_id\n" +
"               and date(vt.tran_date) >= tsf.op_date and tsf.user_id = prm_user_id\n" +
"               and date(vt.tran_date) <= prm_stock_date and vt.deleted = false\n" +
"               and (vt.to_location = prm_location or prm_location = 0)\n" +
"             group by vt.to_location, vt.med_id, vt.expire_date\n" +
"             union all\n" +
"            select if(prm_tran_opt = 'Balance', 'Issue', prm_tran_opt) tran_option,\n" +
"                   vsi.location_id, vsi.med_id, vsi.expire_date exp_date,\n" +
"                   sum(ifnull(smallest_qty,0)*-1) ttl_qty\n" +
"              from v_stock_issue vsi, tmp_stock_filter tsf\n" +
"             where vsi.location_id = tsf.location_id and vsi.med_id = tsf.med_id\n" +
"               and date(vsi.issue_date) >= tsf.op_date and tsf.user_id = prm_user_id\n" +
"               and date(vsi.issue_date) <= prm_stock_date and vsi.deleted = false\n" +
"               and (vsi.location_id = prm_location or prm_location = 0)\n" +
"             group by vsi.location_id, vsi.med_id, vsi.expire_date\n" +
"             union all\n" +
"            select if(prm_tran_opt = 'Balance', 'Receive', prm_tran_opt) tran_option,\n" +
"                   vsr.location_id, vsr.rec_med_id med_id, vsr.expire_date exp_date,\n" +
"                   sum(ifnull(smallest_qty,0)) ttl_qty\n" +
"              from v_stock_receive vsr, tmp_stock_filter tsf\n" +
"             where vsr.location_id = tsf.location_id and vsr.rec_med_id = tsf.med_id\n" +
"               and date(vsr.receive_date) >= tsf.op_date and tsf.user_id = prm_user_id\n" +
"               and date(vsr.receive_date) <= prm_stock_date and vsr.deleted = false\n" +
"               and (vsr.location_id = prm_location or prm_location = 0)\n" +
"             group by vsr.location_id, vsr.rec_med_id, vsr.expire_date\n" +
"             union all\n" +
"            select if(prm_tran_opt = 'Balance', 'Damage', prm_tran_opt) tran_option,\n" +
"                   vd.location location_id, vd.med_id, vd.expire_date exp_date,\n" +
"                   sum(ifnull(dmg_smallest_qty,0)*-1) ttl_qty\n" +
"              from v_damage vd, tmp_stock_filter tsf\n" +
"             where vd.location = tsf.location_id and vd.med_id = tsf.med_id\n" +
"               and date(vd.dmg_date) >= tsf.op_date and tsf.user_id = prm_user_id\n" +
"               and date(vd.dmg_date) <= prm_stock_date and vd.deleted = false\n" +
"               and (vd.location = prm_location or prm_location  = 0)\n" +
"             group by vd.location, vd.med_id, vd.expire_date*/) A,\n" +
"            v_med_unit_smallest_rel B\n" +
"     where A.med_id = B.med_id\n" +
"     group by A.tran_option, A.location_id, A.med_id, A.exp_date";
        
        try {
            strSql = strSql.replace("prm_tran_opt", "'Opening'")
                    .replace("prm_stock_date", "'" + DateUtil.toDateStrMYSQL(stockDate) + "'")
                    .replace("prm_location", location)
                    .replace("prm_user_id", "'" + userId + "'");
            dao.execSql("delete from tmp_stock_balance_exp where user_id = '"
                    + userId + "'");
            dao.commit();
            dao.close();
            dao.execSql(strSql);
        } catch (Exception ex) {
            log.error("execStockBalanceExp : " + ex.toString());
        } finally {
            dao.close();
        }
    }
}
