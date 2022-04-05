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
public class ReturnInBySaleVoucherExcel extends GenExcel {

    static Logger log = Logger.getLogger(ReturnInBySaleVoucherExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public ReturnInBySaleVoucherExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select reti.reg_no, pd.patient_name, reti.ret_in_date, reti.ret_in_id, reti.admission_no,\n"
                + "reti.vou_total ret_in_vou_total, reti.paid ret_in_vou_paid, reti.med_id, reti.med_name,\n"
                + "reti.ret_in_qty, reti.item_unit, reti.ret_in_price, reti.ret_in_amount, reti.sale_qty,\n"
                + "reti.sale_amount, reti.sale_inv_id, reti.sale_date,\n"
                + "concat(TRIM(TRAILING '.0' FROM reti.ret_in_qty), reti.item_unit) ret_in_qty1,\n"
                + "reti.sale_qty sale_qty1\n"
                + "from v_return_in_with_sale_ref reti\n"
                + "left join patient_detail pd on reti.reg_no = pd.reg_no\n"
                + "where deleted = false and date(reti.ret_in_date) between $P{prm_from} and $P{prm_to} \n"
                + "and (reti.reg_no = $P{reg_no} or $P{reg_no} = '-')\n"
                + "order by pd.patient_name, reti.ret_in_date, reti.ret_in_id";

        strSql = strSql
                .replace("$P{prm_from}", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getToDate() + "'")
                .replace("$P{reg_no}", "'" + getRegNo() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Reg No");
            listHeader.add("Patient Name");
            listHeader.add("Ret-In Date");
            listHeader.add("Vou No");
            listHeader.add("Adm No.");
            listHeader.add("Med Id");
            listHeader.add("Med Name");
            listHeader.add("Ret-In Qty");
            listHeader.add("Ret-In Qty Smalest");
            listHeader.add("Ret-In Price");
            listHeader.add("Ret-In Amount");
            listHeader.add("Sale Vou No");
            listHeader.add("Sale Qty");
            listHeader.add("Sale Qty Smallest");
            
            List<String> listField = new ArrayList();
            listField.add("reg_no");
            listField.add("patient_name");
            listField.add("ret_in_date");
            listField.add("ret_in_id");
            listField.add("admission_no");
            listField.add("med_id");
            listField.add("med_name");
            listField.add("ret_in_qty1");
            listField.add("ret_in_qty");
            listField.add("ret_in_price");
            listField.add("ret_in_amount");
            listField.add("sale_inv_id");
            listField.add("sale_qty1");
            listField.add("sale_qty");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("reg_no", POIUtil.FormatType.TEXT);
            hmType.put("patient_name", POIUtil.FormatType.TEXT);
            hmType.put("ret_in_date", POIUtil.FormatType.DATE);
            hmType.put("ret_in_id", POIUtil.FormatType.TEXT);
            hmType.put("admission_no", POIUtil.FormatType.TEXT);
            hmType.put("med_id", POIUtil.FormatType.TEXT);
            hmType.put("med_name", POIUtil.FormatType.TEXT);
            hmType.put("ret_in_qty1", POIUtil.FormatType.TEXT);
            hmType.put("ret_in_qty", POIUtil.FormatType.INTEGER);
            hmType.put("ret_in_price", POIUtil.FormatType.DOUBLE);
            hmType.put("ret_in_amount", POIUtil.FormatType.DOUBLE);
            hmType.put("sale_inv_id", POIUtil.FormatType.TEXT);
            hmType.put("sale_qty1", POIUtil.FormatType.TEXT);
            hmType.put("sale_qty", POIUtil.FormatType.INTEGER);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
