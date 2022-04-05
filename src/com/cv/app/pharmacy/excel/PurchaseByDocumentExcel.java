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
 * @author lenovo
 */
public class PurchaseByDocumentExcel extends GenExcel {

    static Logger log = Logger.getLogger(PurchaseByDocumentExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public PurchaseByDocumentExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select distinct date(ph.pur_date) pur_date, ph.pur_inv_id, ph.currency, cr1.cur_name,\n"
                + "tr.trader_name, ph.vou_total, ph.discount, ph.tax_amt, ph.paid, ph.pur_exp_total, ph.balance,\n"
                + "pt.payment_type_name, ph.due_date, ph.ref_no, cr1.cur_name, loc.location_name, vs.status_desp,\n"
                + "ph.disc_p, ph.tax_p, ph.cus_id,"
                + "pdh.med_id, med.med_name, med.med_rel_str, pdh.expire_date, pdh.pur_qty, pdh.pur_unit,\n" 
                + "concat(pdh.pur_qty,pdh.pur_unit) qty, pdh.pur_price, pdh.pur_disc1_p, pdh.pur_disc2_p,\n"
                + "concat(pdh.pur_foc_qty, pdh.foc_unit) foc, pdh.item_expense, pdh.pur_unit_cost, pdh.pur_amount,\n"
                + "ct.charge_type_desc, pdh.pur_smallest_qty, (pdh.pur_unit_cost/vm.smallest_qty) smallest_cost "
                + "from pur_his ph, pur_detail_his pdh,trader tr, currency cr1, payment_type pt, location loc,\n"
                + "vou_status vs, medicine med, charge_type ct, v_medicine vm \n"
                + "where ph.pur_inv_id = pdh.vou_no and ph.cus_id = tr.trader_id\n"
                + "and ph.currency = cr1.cur_code and ph.payment_type = pt.payment_type_id and ph.deleted = false\n"
                + "and ph.location = loc.location_id and ph.vou_status = vs.vou_status_id \n"
                + "and pdh.med_id = med.med_id and pdh.charge_type = ct.charge_type_id "
                + "and pdh.med_id = vm.med_id and pdh.pur_unit = vm.item_unit "
                + "and date(ph.pur_date) between $P{prm_from} and $P{prm_to}\n"
                + "and (ph.location = $P{prm_location} or $P{prm_location} = 0)\n"
                + "and (ph.currency = $P{prm_currency} or $P{prm_currency} = 'All')\n"
                + "and (ph.payment_type = $P{prm_payment} or $P{prm_payment} = 0)\n"
                + "and (ph.vou_status = $P{prm_vou_type} or $P{prm_vou_type} = 0)\n"
                + "and (pdh.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}))\n"
                + "and (ph.cus_id in (select distinct trader_id from tmp_trader_bal_filter where user_id = $P{user_id})\n"
                + "		or (select count(*) from tmp_trader_bal_filter where user_id = $P{user_id}) = 0)\n"
                + "order by ph.pur_date, ph.pur_inv_id, tr.trader_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{prm_from}", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getToDate() + "'")
                .replace("$P{prm_location}", getLocationId())
                .replace("$P{prm_currency}", "'" + getCurrencyId() + "'")
                .replace("$P{prm_payment}", getPaymentType())
                .replace("$P{prm_vou_type}", "'" + getVouType() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Purchase Date");
            listHeader.add("Vou No");
            listHeader.add("Supplier Code");
            listHeader.add("Supplier Name");
            listHeader.add("Code");
            listHeader.add("Description");
            listHeader.add("Relation-Str");
            listHeader.add("Exp-Date");
            listHeader.add("Qty");
            listHeader.add("Pur Price");
            listHeader.add("1%");
            listHeader.add("2%");
            listHeader.add("FOC");
            listHeader.add("Expense");
            listHeader.add("Unit Cost");
            listHeader.add("Charge Type");
            listHeader.add("Amount");
            listHeader.add("Pur Qty Smallest");
            listHeader.add("Smallest Cost");
            
            List<String> listField = new ArrayList();
            listField.add("pur_date");
            listField.add("pur_inv_id");
            listField.add("cus_id");
            listField.add("trader_name");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("med_rel_str");

            listField.add("expire_date");
            listField.add("qty");
            listField.add("pur_price");
            listField.add("pur_disc1_p");
            listField.add("pur_disc2_p");
            // listField.add("ret_out_qty_str");
            listField.add("foc");
            listField.add("item_expense");
            listField.add("pur_unit_cost");
            listField.add("charge_type_desc");
            listField.add("pur_amount");
            listField.add("pur_smallest_qty");
            listField.add("smallest_cost");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("pur_date", POIUtil.FormatType.TEXT);
            hmType.put("pur_inv_id", POIUtil.FormatType.TEXT);
            hmType.put("cus_id", POIUtil.FormatType.TEXT);
            hmType.put("trader_name", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("med_rel_str", POIUtil.FormatType.TEXT);
            hmType.put("expire_date", POIUtil.FormatType.TEXT);
            hmType.put("qty", POIUtil.FormatType.TEXT);
            hmType.put("pur_price", POIUtil.FormatType.FLOAT);
            hmType.put("pur_disc1_p", POIUtil.FormatType.FLOAT);
            hmType.put("pur_disc2_p", POIUtil.FormatType.FLOAT);
            hmType.put("foc", POIUtil.FormatType.TEXT);
            hmType.put("item_expense", POIUtil.FormatType.FLOAT);
            hmType.put("pur_unit_cost", POIUtil.FormatType.FLOAT);
            hmType.put("charge_type_desc", POIUtil.FormatType.TEXT);
            hmType.put("pur_amount", POIUtil.FormatType.DOUBLE);
            hmType.put("pur_smallest_qty", POIUtil.FormatType.FLOAT);
            hmType.put("smallest_cost", POIUtil.FormatType.FLOAT);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }

}
