/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.excel;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.excel.*;
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
public class AdmissionByDoctorExcel extends GenExcel {
    static Logger log = Logger.getLogger(AdmissionByDoctorExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public AdmissionByDoctorExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select pd.reg_no, pd.patient_name, g.description sex, date(pd.ams_date) ams_date, c.city_name , d.doctor_name,\n" +
"pd.ams_no\n" +
"from admission pd left join gender g on pd.sex = g.gender_id\n" +
"left join city c on pd.city_id = c.city_id left join doctor d on pd.doctor_id = d.doctor_id\n" +
"where date(pd.ams_date) between $P{from_date} and $P{to_date}\n" +
"and (pd.sex = $P{gender} or $P{gender} = '-')\n" +
"and (pd.city_id = $P{city} or $P{city} = -1)\n" +
"order by d.doctor_name,date(ams_date),pd.patient_name";

        strSql = strSql.replace("$P{from_date}", "'" + getFromDate() + "'")
                .replace("$P{to_date}", "'" + getToDate() + "'")
                .replace("$P{gender}", "'" + getGender() + "'")
                .replace("$P{city}", getCity());
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Date");
            listHeader.add("Doctor Name");
            listHeader.add("Reg No.");
            listHeader.add("Ams No.");
            listHeader.add("Patient Name");
            listHeader.add("Gender");
            listHeader.add("City");
            
            List<String> listField = new ArrayList();
            listField.add("ams_date");
            listField.add("doctor_name");
            listField.add("reg_no");
            listField.add("ams_no");
            listField.add("patient_name");
            listField.add("sex");
            listField.add("city_name");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("ams_date", POIUtil.FormatType.DATE);
            hmType.put("doctor_name", POIUtil.FormatType.TEXT);
            hmType.put("reg_no", POIUtil.FormatType.TEXT);
            hmType.put("ams_no", POIUtil.FormatType.TEXT);
            hmType.put("patient_name", POIUtil.FormatType.TEXT);
            hmType.put("sex", POIUtil.FormatType.TEXT);
            hmType.put("city_name", POIUtil.FormatType.TEXT);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
