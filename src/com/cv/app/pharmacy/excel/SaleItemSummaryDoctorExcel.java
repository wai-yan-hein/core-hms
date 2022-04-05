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
public class SaleItemSummaryDoctorExcel extends GenExcel {
    static Logger log = Logger.getLogger(SaleItemSummaryDoctorExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public SaleItemSummaryDoctorExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select vs.description,vs.currency_id,cur.cur_name,vs.med_id,vs.med_name,vs.patient_name,p.doctor_name,\n" +
            "get_qty_in_str(sum(ifnull(sale_smallest_qty,0)), med.unit_smallest, med.unit_str) ttl_qty,vs.sale_date,\n" +
            "sum(vs.sale_amount) amount,\n" +
            "get_qty_in_str(sum(ifnull(vs.foc_smallest_qty,0)), med.unit_smallest, med.unit_str) foc_qty,\n" +
            "get_qty_in_str(sum(ifnull(sale_smallest_qty,0)+ifnull(vs.foc_smallest_qty,0)), med.unit_smallest, med.unit_str) ttl, vs.sale_price,\n" +
            "(sum(ifnull(sale_smallest_qty,0)+ifnull(vs.foc_smallest_qty,0))) ttl_qty_int,\n" +
            "vs.reg_no, vs.sale_inv_id, vs.doctor_id, sum(ifnull(sale_smallest_qty,0)) as ttl_sale,\n" +
            "sum(ifnull(vs.foc_smallest_qty,0)) as ttl_foc " +
            "  from (select s.*,bd.description\n" +
            " from v_sale s left join\n" +
            "(select b.description,a.reg_no from admission a,building_structure b where a.building_structure_id = b.id) bd\n" +
            "on s.reg_no = bd.reg_no) vs\n" +
            ", currency cur, v_med_unit_smallest_rel med,(select pd.*,d.doctor_name from  patient_detail pd left join doctor d on pd.doctor_id = d.doctor_id) p\n" +
            " where vs.currency_id = cur.cur_code and vs.reg_no = p.reg_no and\n" +
            "   (p.pt_type =$P{cus_group} or $P{cus_group} = 'All')\n" +
            "   and vs.med_id = med.med_id and vs.deleted = false\n" +
            "   and date(vs.sale_date) between $P{prm_from} and $P{prm_to}\n" +
            "   and (vs.location_id = $P{prm_location} or $P{prm_location} = 0)\n" +
            "   and (vs.currency_id = $P{prm_currency} or $P{prm_currency} = 'All')\n" +
            "   and (vs.reg_no = $P{reg_no} or $P{reg_no} = '-')\n" +
            "   and (vs.session_id = $P{session} or $P{session} = '-')\n" +
            "   and (vs.payment_type_id = $P{prm_payment} or $P{prm_payment} = 0)\n" +
            "   and (vs.med_id in (select distinct med_id from tmp_stock_filter where user_id = $P{user_id}))\n" +
            "group by vs.currency_id,cur.cur_name,vs.patient_name,p.doctor_name,vs.med_id,vs.med_name,vs.sale_price,vs.sale_date,vs.reg_no\n" +
            ",vs.description, vs.sale_inv_id, vs.doctor_id\n" +
            "order by p.doctor_name, vs.sale_date, vs.med_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{cus_group}", "'" + getCusGroup() + "'")
                .replace("$P{prm_from}", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getFromDate() + "'")
                .replace("$P{prm_location}", getLocationId())
                .replace("$P{prm_currency}", "'" + getCurrencyId() + "'")
                .replace("$P{reg_no}", "'" + getRegNo() + "'")
                .replace("$P{session}", "'" + getSession() + "'")
                .replace("$P{prm_payment}", getPaymentType());
        
        
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Sale Date");
            listHeader.add("Vou No");
            listHeader.add("Doctor Id");
            listHeader.add("Doctor Name");
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
            listField.add("doctor_id");
            listField.add("doctor_name");
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
            hmType.put("doctor_id", POIUtil.FormatType.TEXT);
            hmType.put("doctor_name", POIUtil.FormatType.TEXT);
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
