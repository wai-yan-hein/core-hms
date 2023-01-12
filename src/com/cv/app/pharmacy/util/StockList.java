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
                DateUtil.setStartTime();
                ResultSet resultSet = dao.getPro("get_stock_balance_code",
                        strLocation, med.getMedId(), Global.machineId);
                log.info("Stock Calculation time taken : " + DateUtil.getDuration());
                if (resultSet != null) {
                    /*HashMap<Integer, List<Stock>> minusHM = new HashMap();
                    List<Stock> listStock = new ArrayList();
                    List<Integer> listLoc = new ArrayList();
                    while (resultSet.next()) {
                        float qty = NumberUtil.FloatZero(resultSet.getInt("TTL_QTY"));
                        Integer tmpLocId = resultSet.getInt("location_id");
                        List<Stock> minusList = minusHM.get(tmpLocId);
                        if (qty < 0) {
                            if (minusList == null) {
                                minusList = new ArrayList();
                                minusHM.put(tmpLocId, minusList);
                                listLoc.add(tmpLocId);
                            }

                            Stock stock = new Stock(med, resultSet.getDate("EXP_DATE"),
                                    null, qty, null,
                                    resultSet.getString("location_name"), tmpLocId);
                            minusList.add(stock);
                        } else if (qty > 0) {
                            if (minusList == null) {
                                String qtyStr = MedicineUtil.getQtyInStr(med,
                                        resultSet.getInt("TTL_QTY"));
                                Stock stock = new Stock(med, resultSet.getDate("EXP_DATE"),
                                        qtyStr, resultSet.getFloat("TTL_QTY"), qtyStr,
                                        resultSet.getString("location_name"), tmpLocId);

                                listStock.add(stock);
                                qty = qty - resultSet.getInt("TTL_QTY");
                            } else {
                                if (minusList.isEmpty()) {

                                } else {
                                    Stock tmpStk = minusList.get(0);
                                    float tmpBalance = tmpStk.getBalance();
                                    if ((tmpBalance * -1) > qty) {
                                        float tmpQty = tmpBalance + qty;
                                        tmpStk.setBalance(tmpQty);
                                        tmpStk.setQtySmallest(tmpQty);
                                        qty = 0;
                                    } else if ((tmpBalance * -1) == qty) {
                                        tmpStk.setBalance(0f);
                                        minusList.remove(0);
                                        qty = 0;
                                    } else {
                                        minusList.remove(0);
                                        qty = tmpBalance + qty;
                                        if (!minusList.isEmpty() && qty > 0) {
                                            List<Stock> listS = new ArrayList();
                                            listS.addAll(minusList);
                                            for (Stock stk : listS) {
                                                tmpBalance = stk.getBalance();
                                                if ((tmpBalance * -1) > qty) {
                                                    stk.setBalance(tmpBalance + qty);
                                                    break;
                                                } else if ((tmpBalance * -1) == qty) {
                                                    minusList.remove(0);
                                                    qty = 0;
                                                    break;
                                                } else {
                                                    minusList.remove(0);
                                                    qty = tmpBalance + qty;
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (qty != 0) {
                                String qtyStr = MedicineUtil.getQtyInStr(med, qty);
                                Stock stock = new Stock(med, resultSet.getDate("EXP_DATE"),
                                        qtyStr, qty, qtyStr,
                                        resultSet.getString("location_name"), tmpLocId);

                                listStock.add(stock);
                            }
                        }
                    }*/

 /*listLoc.stream().map(id -> minusHM.get(id)).filter(minusList -> (minusList != null))
                            .filter(minusList -> (!minusList.isEmpty())).forEachOrdered(minusList -> {
                        minusList.stream().map(stk -> {
                            String qtyStr = MedicineUtil.getQtyInStr(stk.getMed(), stk.getBalance());
                            stk.setQtyStr(qtyStr);
                            stk.setQtyStrBal(qtyStr);
                            return stk;
                        }).forEachOrdered(stk -> {
                            listStock.add(stk);
                        });
                    });

                    hasStock.put(med.getMedId(), listStock);*/
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
