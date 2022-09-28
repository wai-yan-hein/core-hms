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
public class CraftGroupRptExcel extends GenExcel {
    static Logger log = Logger.getLogger(CraftGroupRptExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public CraftGroupRptExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select sp.desp, dr.doctor_name, tmp.emergency_cnt, tmp.emergency_amt, tmp.emergency_amt/tmp.emergency_cnt as emergency_arpp,\n" +
"       tmp.ipd_cnt, tmp.ipd_amt, tmp.ipd_amt/tmp.ipd_cnt as ipd_arpp,\n" +
"       tmp.mcu_cnt, tmp.mcu_amt, tmp.mcu_amt/tmp.mcu_cnt as mcu_arpp,\n" +
"       tmp.dcare_cmt, tmp.dcare_amt, tmp.dcare_amt/tmp.dcare_cmt as dcare_arpp,\n" +
"       tmp.ext_cnt, tmp.ext_amt, tmp.ext_amt/tmp.ext_cnt as ext_arpp,\n" +
"       tmp.opd_cnt, tmp.opd_amt, tmp.opd_amt/tmp.opd_cnt as opd_arpp,\n" +
"       tmp.net_amt, tmp.rev_inv\n" +
"  from tmp_craft_group_rpt tmp, doctor dr, speciality sp\n" +
" where tmp.doctor_id = dr.doctor_id and dr.speciality_id = sp.spec_id\n" +
"   and tmp.user_id = $P{user_id}\n" +
" order by sp.desp, dr.doctor_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Craft Group");
            listHeader.add("Doctor");
            listHeader.add("EMERGENCY Throughput");
            listHeader.add("EMERGENCY GOR");
            listHeader.add("EMERGENCY ARPP");
            listHeader.add("IPD Throughput");
            listHeader.add("IPD GOR");
            listHeader.add("IPD ARPP");
            listHeader.add("MCU Throughput");
            listHeader.add("MCU GOR");
            listHeader.add("MCU ARPP");
            listHeader.add("Day Care Throughput");
            listHeader.add("Day Care GOR");
            listHeader.add("Day Care ARPP");
            listHeader.add("External Throughput");
            listHeader.add("External GOR");
            listHeader.add("External ARPP");
            listHeader.add("OPD Throughput");
            listHeader.add("OPD GOR");
            listHeader.add("OPD ARPP");
            listHeader.add("NET AMT");
            listHeader.add("Rev In(%)");
            
            List<String> listField = new ArrayList();
            listField.add("desp");
            listField.add("doctor_name");
            listField.add("emergency_cnt");
            listField.add("emergency_amt");
            listField.add("emergency_arpp");
            listField.add("ipd_cnt");
            listField.add("ipd_amt");
            listField.add("ipd_arpp");
            listField.add("mcu_cnt");
            listField.add("mcu_amt");
            listField.add("mcu_arpp");
            listField.add("dcare_cmt");
            listField.add("dcare_amt");
            listField.add("dcare_arpp");
            listField.add("ext_cnt");
            listField.add("ext_amt");
            listField.add("ext_arpp");
            listField.add("opd_cnt");
            listField.add("opd_amt");
            listField.add("opd_arpp");
            listField.add("net_amt");
            listField.add("rev_inv");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("desp", POIUtil.FormatType.TEXT);
            hmType.put("doctor_name", POIUtil.FormatType.TEXT);
            hmType.put("emergency_cnt", POIUtil.FormatType.INTEGER);
            hmType.put("emergency_amt", POIUtil.FormatType.DOUBLE);
            hmType.put("emergency_arpp", POIUtil.FormatType.DOUBLE);
            hmType.put("ipd_cnt", POIUtil.FormatType.INTEGER);
            hmType.put("ipd_amt", POIUtil.FormatType.DOUBLE);
            hmType.put("ipd_arpp", POIUtil.FormatType.DOUBLE);
            hmType.put("mcu_cnt", POIUtil.FormatType.INTEGER);
            hmType.put("mcu_amt", POIUtil.FormatType.DOUBLE);
            hmType.put("mcu_arpp", POIUtil.FormatType.DOUBLE);
            hmType.put("dcare_cmt", POIUtil.FormatType.INTEGER);
            hmType.put("dcare_amt", POIUtil.FormatType.DOUBLE);
            hmType.put("dcare_arpp", POIUtil.FormatType.DOUBLE);
            hmType.put("ext_cnt", POIUtil.FormatType.INTEGER);
            hmType.put("ext_amt", POIUtil.FormatType.DOUBLE);
            hmType.put("ext_arpp", POIUtil.FormatType.DOUBLE);
            hmType.put("opd_cnt", POIUtil.FormatType.INTEGER);
            hmType.put("opd_amt", POIUtil.FormatType.DOUBLE);
            hmType.put("opd_arpp", POIUtil.FormatType.DOUBLE);
            hmType.put("net_amt", POIUtil.FormatType.DOUBLE);
            hmType.put("rev_inv", POIUtil.FormatType.DOUBLE);
            
            ResultSet rs = dao.execSQL(strSql);
            //POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName);
            POIUtil.genExcelFileForMISReport(listHeader, listField, hmType, rs, 
                    fileName, getFromDate(), getToDate());
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
