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
public class SaleItemSummaryPatientExcel extends GenExcel {
    static Logger log = Logger.getLogger(SaleItemSummaryPatientExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public SaleItemSummaryPatientExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select vs.description,vs.currency_id,cur.cur_name,vs.med_id,vs.med_name,vs.patient_name,d.doctor_name,\n" +
            "       get_qty_in_str(sum(ifnull(sale_smallest_qty,0)), med.unit_smallest, med.unit_str) ttl_qty,vs.sale_date,\n" +
            "       sum(vs.sale_amount) amount, sum(ifnull(sale_smallest_qty,0)) ttl_sale, sum(ifnull(vs.foc_smallest_qty,0)) ttl_foc,\n" +
            "       get_qty_in_str(sum(ifnull(vs.foc_smallest_qty,0)), med.unit_smallest, med.unit_str) foc_qty,\n" +
            "       get_qty_in_str(sum(ifnull(sale_smallest_qty,0)+ifnull(vs.foc_smallest_qty,0)), med.unit_smallest, med.unit_str) ttl, \n" +
            "       vs.sale_price, (sum(ifnull(sale_smallest_qty,0)+ifnull(vs.foc_smallest_qty,0))) ttl_qty_int,vs.reg_no, vs.sale_inv_id, vs.ams_no, \n" +
                "vs.item_type_name " +
            "  from (select s.*,bd.description, bd.ams_no \n" +
            "          from v_sale s left join\n" +
            "               (select b.description,a.reg_no, a.ams_no \n" +
            "                  from admission a,building_structure b \n" +
            "                 where a.building_structure_id = b.id) bd on s.reg_no = bd.reg_no and s.admission_no = bd.ams_no) vs\n" +
            "       join currency cur on vs.currency_id = cur.cur_code\n" +
            "       join v_med_unit_smallest_rel med on vs.med_id = med.med_id\n" +
            "       left join patient_detail pd on vs.reg_no = pd.reg_no \n" +
            "       left join doctor d on vs.doctor_id = d.doctor_id\n" +
            " where (pd.pt_type =$P{cus_group} or $P{cus_group} = 'All')\n" +
            "   and vs.deleted = false\n" +
            "   and date(vs.sale_date) between $P{prm_from} and $P{prm_to}\n" +
            "   and (vs.location_id = $P{prm_location} or $P{prm_location} = 0)\n" +
            "   and (vs.currency_id = $P{prm_currency} or $P{prm_currency} = 'All')\n" +
            "   and (vs.reg_no = $P{reg_no} or $P{reg_no} = '-')\n" +
            "   and (vs.session_id = $P{session} or $P{session} = '-')\n" +
            "   and (vs.payment_type_id = $P{prm_payment} or $P{prm_payment} = 0)\n" +
            "   and (vs.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}))\n" +
            "   and ((vs.doctor_id in (select doctor_id from tmp_doctor_filter where user_id = $P{user_id}\n" +
            "         and vs.doctor_id is not null))\n" +
            "        or (select count(*) from tmp_doctor_filter where user_id = $P{user_id}) = 0)\n" +
            "group by vs.currency_id,cur.cur_name,vs.patient_name,d.doctor_name,vs.med_id,vs.med_name,vs.sale_price,\n" +
            "vs.sale_date,vs.reg_no,vs.description, vs.sale_inv_id, vs.ams_no, vs.item_type_name \n" +
            "order by d.doctor_name,vs.patient_name, vs.sale_date, vs.med_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{cus_group}", "'" + getCusGroup() + "'")
                .replace("$P{prm_from}", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getToDate() + "'")
                .replace("$P{prm_location}", getLocationId())
                .replace("$P{prm_currency}", "'" + getCurrencyId() + "'")
                .replace("$P{reg_no}", "'" + getRegNo() + "'")
                .replace("$P{session}", "'" + getSession() + "'")
                .replace("$P{prm_payment}", getPaymentType());
        
        
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Sale Date");
            listHeader.add("Vou No");
            listHeader.add("Adm No.");
            listHeader.add("Doctor Name");
            listHeader.add("Reg No");
            listHeader.add("Patient Name");
            listHeader.add("Item Type");
            listHeader.add("Code");
            listHeader.add("Item Name");
            listHeader.add("Sale Qty");
            listHeader.add("Sale Qty Smallest");
            listHeader.add("Foc Qty");
            listHeader.add("Foc Qty Smallest");
            listHeader.add("Tot Qty");
            listHeader.add("Tot Qty Smallest");
            listHeader.add("Price");
            listHeader.add("Amount");
            
            List<String> listField = new ArrayList();
            listField.add("sale_date");
            listField.add("sale_inv_id");
            listField.add("ams_no");
            listField.add("doctor_name");
            listField.add("reg_no");
            listField.add("patient_name");
            listField.add("item_type_name");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("ttl_qty");
            listField.add("ttl_sale");
            listField.add("foc_qty");
            listField.add("ttl_foc");
            listField.add("ttl");
            listField.add("ttl_qty_int");
            listField.add("sale_price");
            listField.add("amount");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("sale_date", POIUtil.FormatType.DATE);
            hmType.put("sale_inv_id", POIUtil.FormatType.TEXT);
            hmType.put("ams_no", POIUtil.FormatType.TEXT);
            hmType.put("doctor_name", POIUtil.FormatType.TEXT);
            hmType.put("reg_no", POIUtil.FormatType.TEXT);
            hmType.put("patient_name", POIUtil.FormatType.TEXT);
            hmType.put("item_type_name", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("ttl_qty", POIUtil.FormatType.TEXT);
            hmType.put("ttl_sale", POIUtil.FormatType.FLOAT);
            hmType.put("foc_qty", POIUtil.FormatType.TEXT);
            hmType.put("ttl_foc", POIUtil.FormatType.FLOAT);
            hmType.put("ttl", POIUtil.FormatType.TEXT);
            hmType.put("ttl_qty_int", POIUtil.FormatType.FLOAT);
            hmType.put("sale_price", POIUtil.FormatType.DOUBLE);
            hmType.put("amount", POIUtil.FormatType.DOUBLE);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
