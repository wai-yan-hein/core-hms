/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.excel;

import com.cv.app.pharmacy.excel.*;
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
public class DcPatientByDoctorExcel extends GenExcel {
    static Logger log = Logger.getLogger(DcPatientByDoctorExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public DcPatientByDoctorExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select dc.doctor_id,d.doctor_name,count(dc.patient_id) as qty,date(dc.dc_date) dc_date,ad.reg_no,\n" +
"dc.admission_no,ad.patient_name\n" +
"from dc_his  dc, admission ad,doctor d\n" +
"where  dc.dc_status is not null and\n" +
"dc.admission_no=ad.ams_no and ad.doctor_id=d.doctor_id\n" +
"and date(dc.dc_date) between $P{from_date} and $P{to_date}\n" +
"group by dc.doctor_id,d.doctor_name,date(dc.dc_date),ad.reg_no,\n" +
"dc.admission_no,dc.patient_name\n" +
"order by d.doctor_name, date(dc.dc_date)";

        strSql = strSql.replace("$P{from_date}", "'" + getFromDate() + "'")
                .replace("$P{to_date}", "'" + getToDate() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Doctor ID");
            listHeader.add("Qty");
            listHeader.add("DC Date");
            listHeader.add("Reg No");
            listHeader.add("Doctor Name");
            listHeader.add("Ams No.");
            listHeader.add("Patient Name");
            
            List<String> listField = new ArrayList();
            listField.add("doctor_id");
            listField.add("qty");
            listField.add("dc_date");
            listField.add("reg_no");
            listField.add("doctor_name");
            listField.add("admission_no");
            listField.add("patient_name");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("doctor_id", POIUtil.FormatType.TEXT);
            hmType.put("qty", POIUtil.FormatType.TEXT);
            hmType.put("dc_date", POIUtil.FormatType.TEXT);
            hmType.put("reg_no", POIUtil.FormatType.TEXT);
            hmType.put("doctor_name", POIUtil.FormatType.TEXT);
            hmType.put("admission_no", POIUtil.FormatType.TEXT);
            hmType.put("patient_name", POIUtil.FormatType.TEXT);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
