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
public class SaleIncomeByDoctorExcel extends GenExcel {
    static Logger log = Logger.getLogger(SaleIncomeByDoctorExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public SaleIncomeByDoctorExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select doctor_name, reg_no, admission_no, patient_name, sale_inv_id, sum(opd_amt) as opd_amt, sum(adm_amt) as adm_amt\n" +
"from v_sale_for_hp\n" +
"where deleted = false and date(sale_date) between $P{prm_from} and $P{prm_to}\n" +
"and (reg_no = $P{reg_no} or $P{reg_no} = '-')\n" +
"and (location_id = $P{prm_location} or $P{prm_location} = 0)\n" +
"and ($P{prm_location_group} = 0 or location_id in (select location_id from loc_group_mapping where group_id = $P{prm_location_group}))\n" +
"and ((select count(*) from tmp_doctor_filter where user_id = $P{user_id}) >0\n" +
"and doctor_id in (select distinct doctor_id from tmp_doctor_filter) or\n" +
"(select count(*) from tmp_doctor_filter where user_id = $P{user_id}) = 0)\n" +
"group by doctor_name, reg_no, admission_no, patient_name, sale_inv_id\n" +
"order by doctor_name, patient_name, sale_inv_id";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{prm_from}", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getToDate() + "'")
                .replace("$P{reg_no}", "'" + getRegNo() + "'")
                .replace("$P{prm_currency}", "'" + getCurrencyId() + "'")
                .replace("$P{prm_location}", getLocationId())
                .replace("$P{prm_location_group}", getLocationGroup());
        
        
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Vou No");
            listHeader.add("Doctor Name");
            listHeader.add("Reg No.");
            listHeader.add("Ams No.");
            listHeader.add("Patient Name");
            listHeader.add("OPD Amount");
            listHeader.add("ADM Amount");
            
            List<String> listField = new ArrayList();
            listField.add("sale_inv_id");
            listField.add("doctor_name");
            listField.add("reg_no");
            listField.add("admission_no");
            listField.add("patient_name");
            listField.add("opd_amt");
            listField.add("adm_amt");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("sale_inv_id", POIUtil.FormatType.TEXT);
            hmType.put("doctor_name", POIUtil.FormatType.TEXT);
            hmType.put("reg_no", POIUtil.FormatType.TEXT);
            hmType.put("admission_no", POIUtil.FormatType.TEXT);
            hmType.put("patient_name", POIUtil.FormatType.TEXT);
            hmType.put("opd_amt", POIUtil.FormatType.DOUBLE);
            hmType.put("adm_amt", POIUtil.FormatType.DOUBLE);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
