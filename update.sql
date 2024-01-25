StockList
=========
String strSql = "select * from (\n"
                            + "select a.location_id, loc.location_name, a.med_id, a.exp_date, sum(ttl_qty) ttl_qty\n"
                            + "  from (\n"
                            + "    select soh.location location_id, sodh.med_id,\n"
                            + "            sodh.expire_date exp_date, sum(sodh.op_smallest_qty) ttl_qty\n"
                            + "      from stock_op_his soh inner join stock_op_detail_his sodh on soh.op_id = sodh.op_id\n"
                            + "     where sodh.med_id = @medid and soh.location = @loc and soh.op_date = @opdate\n"
                            + "     group by soh.location, sodh.med_id, sodh.expire_date\n"
                            + "    union all \n"
                            + "    select ifnull(sdh.location_id, sh.location_id) location_id, sdh.med_id, sdh.expire_date exp_date,\n"
                            + "           (sum(sdh.sale_smallest_qty+ifnull(sdh.foc_smallest_qty,0))*-1) ttl_qty\n"
                            + "      from sale_his sh, sale_detail_his sdh \n"
                            + "     where sh.sale_inv_id = sdh.vou_no and sh.deleted = false\n"
                            + "       and date(sh.sale_date) >= @opdate \n"
                            + "       and sdh.med_id = @medid\n"
                            + "       and ifnull(sdh.location_id,sh.location_id) = @loc\n"
                            + "	   and sh.vou_status = 1 \n"
                            + "     group by ifnull(sdh.location_id,sh.location_id), sdh.med_id, sdh.expire_date\n"
                            + "     union all \n"
                            + "    select ifnull(pdh.location_id,ph.location) location_id, pdh.med_id, pdh.expire_date exp_date,\n"
                            + "           sum(pdh.pur_smallest_qty + ifnull(pdh.pur_foc_smallest_qty,0)) ttl_qty\n"
                            + "      from pur_his ph, pur_detail_his pdh\n"
                            + "     where ph.pur_inv_id = pdh.vou_no and ph.deleted = false\n"
                            + "       and date(ph.pur_date) >= @opdate \n"
                            + "       and pdh.med_id = @medid\n"
                            + "       and ifnull(pdh.location_id, ph.location) = @loc\n"
                            + "	   and ph.vou_status = 1 \n"
                            + "     group by ifnull(pdh.location_id, ph.location), pdh.med_id, pdh.expire_date\n"
                            + "     union all \n"
                            + "    select rih.location location_id, ridh.med_id, ridh.expire_date exp_date,\n"
                            + "           sum(ridh.ret_in_smallest_qty) ttl_qty\n"
                            + "      from ret_in_his rih, ret_in_detail_his ridh\n"
                            + "     where rih.ret_in_id = ridh.vou_no and rih.deleted = false\n"
                            + "       and date(rih.ret_in_date) >= @opdate \n"
                            + "       and ridh.med_id = @medid\n"
                            + "       and rih.location = @loc\n"
                            + "     group by rih.location, ridh.med_id, ridh.expire_date\n"
                            + "     union all \n"
                            + "    select roh.location location_id, rodh.med_id, rodh.expire_date exp_date,\n"
                            + "           (sum(rodh.ret_out_smallest_qty)*-1) ttl_qty\n"
                            + "      from ret_out_his roh, ret_out_detail_his rodh\n"
                            + "     where roh.ret_out_id = rodh.vou_no and roh.deleted = false\n"
                            + "       and date(roh.ret_out_date) >= @opdate \n"
                            + "       and rodh.med_id = @medid\n"
                            + "       and roh.location = @loc\n"
                            + "     group by roh.location, rodh.med_id, rodh.expire_date\n"
                            + "     union all \n"
                            + "    select th.from_location location_id, tdh.med_id, tdh.expire_date exp_date,\n"
                            + "           (sum(tdh.tran_smallest_qty)*-1) ttl_qty\n"
                            + "      from transfer_his th, transfer_detail_his tdh\n"
                            + "     where th.transfer_id = tdh.vou_no and th.deleted = false\n"
                            + "       and date(th.tran_date) >= @opdate and tdh.med_id = @medid\n"
                            + "       and th.from_location = @loc\n"
                            + "     group by th.from_location, tdh.med_id, tdh.expire_date\n"
                            + "     union all \n"
                            + "    select th.to_location location_id, tdh.med_id med_id, tdh.expire_date exp_date,\n"
                            + "           sum(tdh.tran_smallest_qty) ttl_qty\n"
                            + "      from transfer_his th, transfer_detail_his tdh\n"
                            + "     where th.transfer_id = tdh.vou_no and th.deleted = false\n"
                            + "       and date(th.tran_date) >= @opdate and tdh.med_id = @medid\n"
                            + "       and th.to_location = @loc\n"
                            + "     group by th.to_location, tdh.med_id, tdh.expire_date\n"
                            + "     union all \n"
                            + "    select ah.location location_id, adh.med_id, adh.expire_date exp_date,\n"
                            + "           sum(if(adh.adj_type = '-',(adh.adj_smallest_qty*-1),adh.adj_smallest_qty)) ttl_qty\n"
                            + "      from adj_his ah, adj_detail_his adh\n"
                            + "     where ah.adj_id = adh.vou_no and ah.deleted = false\n"
                            + "       and date(ah.adj_date) >= @opdate and adh.med_id = @medid\n"
                            + "       and ah.location = @loc\n"
                            + "     group by ah.location, adh.med_id, adh.expire_date\n"
                            + "	 union all \n"
                            + "	select srh.location_id location_id, srdh.order_med_id med_id, srdh.expire_date,\n"
                            + "		   sum(srdh.smallest_qty) ttl_qty\n"
                            + "	  from stock_receive_his srh, stock_receive_detail_his srdh\n"
                            + "	 where srh.receive_id = srdh.vou_no\n"
                            + "	   and srh.deleted = false and date(srh.receive_date) >= @opdate and srdh.order_med_id = @medid\n"
                            + "       and srh.location_id = @loc\n"
                            + "     group by srh.location_id, srdh.order_med_id, srdh.expire_date\n"
                            + "	 union all \n"
                            + "	select sih.location_id, sidh.med_id, sidh.expire_date, (sum(sidh.smallest_qty)*-1) ttl_qty\n"
                            + "	  from stock_issue_his sih,stock_issue_detail_his sidh\n"
                            + "	 where sih.issue_id = sidh.issue_id\n"
                            + "	   and sih.deleted = false and date(sih.issue_date) >= @opdate\n"
                            + "	   and sidh.med_id = @medid and sih.location_id = @loc\n"
                            + "	 group by sih.location_id, sidh.med_id, sidh.expire_date\n"
                            + "	 union all\n"
                            + "	select vd.location location_id, vd.med_id, vd.expire_date, sum(ifnull(vd.dmg_smallest_qty,0)*-1) ttl_qty\n"
                            + "	  from v_damage vd\n"
                            + "	 where vd.deleted = false and date(vd.dmg_date) >= @opdate\n"
                            + "	   and vd.med_id = @medid and vd.location = @loc\n"
                            + "	 group by vd.location, vd.med_id, expire_date\n"
                            + "     union all \n"
                            + "	select vlmu.location_id, vlmu.med_id, null exp_date, sum(ifnull(vlmu.ttl_med_usage_qty,0)*-1) ttl_qty\n"
                            + "	  from v_lab_med_usage vlmu\n"
                            + "	 where vlmu.location_id = @loc and vlmu.med_id = @medid\n"
                            + "	   and date(vlmu.opd_date) >= @opdate\n"
                            + "	 group by vlmu.location_id, vlmu.med_id \n"
                            + "	 union all \n"
                            + "	select vlmu.location_id, vlmu.med_id, null exp_date, sum(ifnull(vlmu.ttl_med_usage_qty,0)*-1) ttl_qty\n"
                            + "	  from v_investigation_med_usage vlmu\n"
                            + "	 where vlmu.location_id = @loc and vlmu.med_id = @medid\n"
                            + "	   and date(vlmu.opd_date) >= @opdate\n"
                            + "	 group by vlmu.location_id, vlmu.med_id\n"
                            + "     ) a, location loc\n"
                            + "  where a.location_id = loc.location_id\n"
                            + "  group by a.location_id, loc.location_name, a.med_id, a.exp_date) a\n"
                            + "  where a.ttl_qty <> 0 \n"
                            + "  order by a.location_id, a.location_name, a.med_id, a.ttl_qty, a.exp_date";