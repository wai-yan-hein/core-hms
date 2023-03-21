/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.util;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.helper.MinusPlusList;
import com.cv.app.pharmacy.database.helper.Stock;
import com.cv.app.util.DateUtil;
import com.cv.app.util.NumberUtil;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import org.apache.log4j.Logger;
import org.josql.Query;
import org.josql.QueryExecutionException;
import org.josql.QueryParseException;
import org.josql.QueryResults;

/**
 *
 * @author WSwe
 */
public class StockList {

    static Logger log = Logger.getLogger(StockList.class.getName());
    private Hashtable hasStock;
    private AbstractDataAccess dao;
    private List<Medicine> listMed = new ArrayList();
    private Location location;
    private MedicineUP medUp;

    @SuppressWarnings("UseOfObsoleteCollectionType")
    public StockList(AbstractDataAccess dao, MedicineUP medUp) {
        hasStock = new Hashtable();
        this.dao = dao;
        this.medUp = medUp;
    }

    public void add(Medicine med, Float ttlQty, Date expDate) {
        List<Stock> listStock;
        if (hasStock.contains(med.getMedId())) {
            listStock = (List<Stock>) hasStock.get(med.getMedId());
        } else {
            listStock = new ArrayList();
        }

        String qtyStr = MedicineUtil.getQtyInStr(med, ttlQty);
        Stock stock = new Stock(med, expDate, qtyStr, ttlQty, qtyStr,
                location.getLocationName(), location.getLocationId());
        listStock.add(stock);
    }

    public void add(Medicine med, Location locationId) {
        setLocation(locationId);

        if (!hasStock.containsKey(med.getMedId())) {
            listMed.add(med);

            try {
                String strLocation = "-1";
                if (location != null) {
                    strLocation = Integer.toString(location.getLocationId());
                }

                String medId = med.getMedId();
                Object maxDate = dao.getMax("op_date", "med_op_date",
                        "location_id = " + strLocation
                        + " and med_id = '" + medId + "' and op_date <= CURDATE()");
                String strOpDate = "1900-01-01";
                if (maxDate != null) {
                    strOpDate = maxDate.toString();
                }

                DateUtil.setStartTime();
                ResultSet resultSet;
                if (strLocation.equals("-1")) {
                    resultSet = dao.getPro("get_stock_balance_code",
                            strLocation, med.getMedId(), Global.machineId);
                } else {
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
                    strSql = strSql.replace("@loc", strLocation)
                            .replace("@medid", "'" + medId + "'")
                            .replace("@opdate", "'" + strOpDate + "'");
                    resultSet = dao.execSQL(strSql);
                }
                log.info("Stock Calculation time taken : " + DateUtil.getDuration());
                if (resultSet != null) {
                    HashMap<Integer, MinusPlusList> hmMP = new HashMap();
                    List<Stock> listMinusStock;
                    List<Stock> listPlusStock;
                    List<Integer> listLocation = new ArrayList();
                    while (resultSet.next()) {
                        Integer tmpLocId = resultSet.getInt("location_id");
                        if (!hmMP.containsKey(tmpLocId)) {
                            listMinusStock = new ArrayList();
                            listPlusStock = new ArrayList();
                            MinusPlusList mpl = new MinusPlusList(listMinusStock, listPlusStock);
                            hmMP.put(tmpLocId, mpl);
                            listLocation.add(tmpLocId);
                        } else {
                            MinusPlusList mpl = hmMP.get(tmpLocId);
                            listMinusStock = mpl.getListMinusStock();
                            listPlusStock = mpl.getListPlusStock();
                        }

                        float qty = NumberUtil.FloatZero(resultSet.getInt("TTL_QTY"));

                        if (qty < 0) {
                            Stock stock = new Stock(med, resultSet.getDate("EXP_DATE"),
                                    null, qty, null,
                                    resultSet.getString("location_name"), tmpLocId);
                            listMinusStock.add(stock);
                        } else {
                            Stock stock = new Stock(med, resultSet.getDate("EXP_DATE"),
                                    null, qty, null,
                                    resultSet.getString("location_name"), tmpLocId);
                            listPlusStock.add(stock);
                        }
                    }

                    List<Stock> listS = new ArrayList();
                    for (Integer loc : listLocation) {
                        MinusPlusList mpl = hmMP.get(loc);
                        listMinusStock = mpl.getListMinusStock();
                        listPlusStock = mpl.getListPlusStock();
                        listS.addAll(PharmacyUtil.getStockList(listMinusStock, listPlusStock));
                    }

                    hasStock.put(med.getMedId(), listS);
                    resultSet.close();
                }
            } catch (Exception ex) {
                log.error("add : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            }
        }
    }

    public void setLocation(Location location) {
        if (this.location == null) {
            this.location = location;
        }

        if (this.location != null) {
            if (!Objects.equals(this.location.getLocationId(), location.getLocationId())) {
                this.location = location;
                hasStock.clear();
                listMed.removeAll(listMed);

                System.gc();
            }
        } else {

        }
    }

    public List<Medicine> getMedList() {
        return listMed;
    }

    public List<Stock> getStockList(String medId) {
        return (List<Stock>) hasStock.get(medId);
    }

    public void delete(String medId, Date expDate, String option) {
        List<Stock> listStock = (List<Stock>) hasStock.get(medId);

        if (listStock != null) {
            String strSQL = "SELECT * FROM com.cv.app.pharmacy.database.helper.Stock WHERE med.medId = '"
                    + medId + "'" + " and expDate = " + expDate;
            Query q = new Query();

            try {
                q.parse(strSQL);
                QueryResults qr = q.execute(listStock);
                List list = qr.getResults();

                if (option.equals("Normal")) {
                    for (int i = 0; i < list.size(); i++) {
                        Stock stock = (Stock) list.get(i);
                        stock.setQtyStrDeman(null);
                        stock.setUnit(null);
                        stock.setUnitQty(null);

                        //need to recalculate balance
                        if (stock.getFocUnit() != null) {
                            String key = stock.getMed().getMedId() + "-"
                                    + stock.getFocUnit().getItemUnitCode();
                            int balance = NumberUtil.NZeroInt(stock.getQtySmallest())
                                    - (NumberUtil.NZeroInt(stock.getFocUnitQty())
                                    * NumberUtil.NZeroInt(medUp.getQtyInSmallest(key)));
                            stock.setQtyStrBal(MedicineUtil.getQtyInStr(stock.getMed(),
                                    balance));
                        } else {
                            stock.setQtyStrBal(stock.getQtyStr());
                        }
                    }
                } else if (option.equals("FOC")) {
                    for (int i = 0; i < list.size(); i++) {
                        Stock stock = (Stock) list.get(i);
                        stock.setFocQtyStr(null);
                        stock.setFocUnit(null);
                        stock.setFocUnitQty(null);

                        //need to recalculate balance
                        if (stock.getUnit() != null) {
                            String key = stock.getMed().getMedId() + "-"
                                    + stock.getUnit().getItemUnitCode();
                            int balance = NumberUtil.NZeroInt(stock.getQtySmallest())
                                    - (NumberUtil.NZeroInt(stock.getUnitQty())
                                    * NumberUtil.NZeroInt(medUp.getQtyInSmallest(key)));
                            stock.setQtyStrBal(MedicineUtil.getQtyInStr(stock.getMed(),
                                    balance));
                        } else {
                            stock.setQtyStrBal(stock.getQtyStr());
                        }
                    }
                }
            } catch (QueryParseException | QueryExecutionException ex) {
                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            }
        }
    }

    public String getLocation() {
        if (location != null) {
            return location.getLocationName();
        } else {
            return null;
        }
    }

    public void clear() {
        hasStock.clear();
        listMed.removeAll(listMed);
    }
}
