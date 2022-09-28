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
public class OPDServiceFeeListExcel extends GenExcel {
    static Logger log = Logger.getLogger(OPDServiceFeeListExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public OPDServiceFeeListExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select oc.cat_name, os.service_code, os.service_name, os.srv_fees,os.srv_fees1,os.srv_fees2,os.srv_fees3,os.srv_fees4,os.srv_fees5,os.srv_fees6\n" +
"from opd_service os, opd_category oc, tmp_opd_service_filter tosf\n" +
"where os.cat_id = oc.cat_id and os.service_id = tosf.service_id\n" +
"and tosf.user_id = $P{user_id}\n" +
"order by oc.cat_name, os.service_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Category");
            listHeader.add("Service Name");
            listHeader.add("Price");
            listHeader.add("Srv Fee");
            listHeader.add("MO Fee");
            listHeader.add("Staff Fee");
            listHeader.add("Tech Fee");
            listHeader.add("Refer Fee");
            listHeader.add("Reader Fee");
            
            List<String> listField = new ArrayList();
            listField.add("cat_name");
            listField.add("service_name");
            listField.add("srv_fees");
            listField.add("srv_fees1");
            listField.add("srv_fees2");
            listField.add("srv_fees3");
            listField.add("srv_fees4");
            listField.add("srv_fees5");
            listField.add("srv_fees6");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("cat_name", POIUtil.FormatType.TEXT);
            hmType.put("service_name", POIUtil.FormatType.TEXT);
            hmType.put("srv_fees", POIUtil.FormatType.DOUBLE);
            hmType.put("srv_fees1", POIUtil.FormatType.DOUBLE);
            hmType.put("srv_fees2", POIUtil.FormatType.DOUBLE);
            hmType.put("srv_fees3", POIUtil.FormatType.DOUBLE);
            hmType.put("srv_fees4", POIUtil.FormatType.DOUBLE);
            hmType.put("srv_fees5", POIUtil.FormatType.DOUBLE);
            hmType.put("srv_fees6", POIUtil.FormatType.DOUBLE);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
