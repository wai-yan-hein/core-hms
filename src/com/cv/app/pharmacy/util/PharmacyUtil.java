/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.util;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Sequence;
import com.cv.app.pharmacy.database.helper.Stock;
import com.cv.app.util.DateUtil;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PharmacyUtil {

    static Logger log = Logger.getLogger(PharmacyUtil.class.getName());

    public static Double getSeq(String option, AbstractDataAccess dao) {
        double seq = 0;
        try {
            Sequence objSeq = (Sequence) dao.find(Sequence.class, option);

            if (objSeq == null) {
                objSeq = new Sequence(option, 2d);
                seq = 1;
            } else {
                seq = objSeq.getSeq();
                objSeq.setSeq(objSeq.getSeq() + 1);
            }
            dao.save(objSeq);
        } catch (Exception ex) {
            log.error(ex.toString());
        }

        return seq;
    }

    public static Double getSeqR(String option, AbstractDataAccess dao) {
        double seq;
        try {
            Sequence objSeq = (Sequence) dao.find(Sequence.class, option);

            if (objSeq == null) {
                seq = 1;
            } else {
                seq = objSeq.getSeq();
            }
        } catch (Exception ex) {
            seq = 0;
            log.error("getSeqR : " + ex.getMessage());
        } finally {
            dao.close();
        }
        return seq;
    }

    public static void updateSeq(String option, AbstractDataAccess dao) {
        try {
            Sequence objSeq = (Sequence) dao.find(Sequence.class, option);

            if (objSeq == null) {
                objSeq = new Sequence(option, 0.0);
            }

            objSeq.setSeq(objSeq.getSeq() + 1);
            dao.save(objSeq);
        } catch (Exception ex) {
            dao.rollBack();
            log.error(ex.toString());
        } finally {
            dao.close();
        }
    }

    public static boolean isItemAlreadyUsaded(String medId, AbstractDataAccess dao) {
        boolean status = false;
        long count = dao.getRowCount("select count(*) item_count from sale_detail_his where med_id ='"
                + medId + "'");

        if (count > 0) {
            status = true;
        } else {
            count = dao.getRowCount("select count(*) item_count from pur_detail_his where med_id ='"
                    + medId + "'");

            if (count > 0) {
                status = true;
            } else {
                count = dao.getRowCount("select count(*) item_count from stock_op_detail_his where med_id ='"
                        + medId + "'");

                if (count > 0) {
                    status = true;
                }
            }
        }

        return status;
    }

    public static Date getLockDate(AbstractDataAccess dao) {
        Date lockDate = DateUtil.toDate("01/01/1900");
        try {
            ResultSet rs = dao.execSQL("select max(lock_date) as lock_date from data_lock_his");
            if (rs != null) {
                if (rs.next()) {
                    lockDate = rs.getDate("lock_date");
                }
                rs.close();
            }

            if (lockDate == null) {
                lockDate = DateUtil.toDate("01/01/1900");
            }
        } catch (Exception ex) {
            log.error("Login lock date : " + ex.toString());
        } finally {
            dao.close();
        }
        return lockDate;
    }

    public static List<Stock> getStockList(List<Stock> minusList, List<Stock> stockList) {
        if (minusList != null) {
            for (Stock stkm : minusList) {
                float minusQty = stkm.getBalance();
                for (Stock stk : stockList) {
                    if (minusQty == 0.0) {
                        break;
                    }

                    if (stk.getBalance() != 0f) {
                        if (Math.abs(minusQty) == stk.getBalance()) {
                            stkm.setBalance(0f);
                            stk.setBalance(0f);
                            minusQty = 0f;
                        } else if (Math.abs(minusQty) < stk.getBalance()) {
                            stk.setBalance(minusQty + stk.getBalance());
                            minusQty = 0f;
                        } else if (Math.abs(minusQty) > stk.getBalance()) {
                            minusQty = minusQty + stk.getBalance();
                            stk.setBalance(0f);
                        }
                    }
                }

                stkm.setBalance(minusQty);
            }

            List<Stock> tmpList = minusList.stream().filter(s -> s.getBalance() != 0f)
                    .collect(toList());
            if (!tmpList.isEmpty()) {
                stockList.addAll(tmpList);
            }

            stockList = stockList.stream().filter(s -> s.getBalance() != 0.f)
                    .map(stk -> {
                        String qtyStr = MedicineUtil.getQtyInStr(stk.getMed(), stk.getBalance());
                        stk.setQtyStr(qtyStr);
                        stk.setQtyStrBal(qtyStr);
                        return stk;
                    })
                    .collect(toList());
        }

        return stockList;
    }
}
