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
public class LabTeachWithTechExcel extends GenExcel {

    static Logger log = Logger.getLogger(LabTeachWithTechExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public LabTeachWithTechExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select oc.cat_name, os.service_name, sum(ifnull(odh.qty, 0)) ttl_qty, sum(ifnull(odh.amount,0)) ttl_amount,os.srv_fees,\n"
                + "if(odh.is_percent = 0,if(odh.srv_fees1 > 0, sum(ifnull(odh.qty, 0)) * ifnull(odh.srv_fees1,0),0), if(odh.srv_fees1 > 0 ,sum(ifnull(odh.amount,0))* ifnull(odh.srv_fees1,0) / 100,0)) srv_fees1,\n"
                + "if(odh.is_percent = 0,if(odh.srv_fees2 > 0, sum(ifnull(odh.qty, 0)) * ifnull(odh.srv_fees2,0),0), if(odh.srv_fees2 > 0 ,sum(ifnull(odh.amount,0))* ifnull(odh.srv_fees2,0) / 100,0)) srv_fees2,\n"
                + "if(odh.is_percent = 0,if(odh.srv_fees3 > 0, sum(ifnull(odh.qty, 0)) * ifnull(odh.srv_fees3,0),0), if(odh.srv_fees3 > 0 ,sum(ifnull(odh.amount,0))* ifnull(odh.srv_fees3,0) / 100,0)) srv_fees3,\n"
                + "if(odh.is_percent = 0,if(odh.srv_fees4 > 0, sum(ifnull(odh.qty, 0)) * ifnull(odh.srv_fees4,0),0), if(odh.srv_fees4 > 0 ,sum(ifnull(odh.amount,0))* ifnull(odh.srv_fees4,0) / 100,0)) srv_fees4,\n"
                + "if(odh.is_percent = 0,if(odh.srv_fees5 > 0, sum(ifnull(odh.qty, 0)) * ifnull(odh.srv_fees5,0),0), if(odh.srv_fees5 > 0 ,sum(ifnull(odh.amount,0))* ifnull(odh.srv_fees5,0) / 100,0)) srv_fees5,\n"
                + "if(odh.is_percent = 0,if(odh.srv_fees6 > 0, sum(ifnull(odh.qty, 0)) * ifnull(odh.srv_fees6,0),0), if(odh.srv_fees6 > 0 ,sum(ifnull(odh.amount,0))* ifnull(odh.srv_fees6,0) / 100,0)) srv_fees6\n"
                + ",odh.is_percent,t.tech_name\n"
                + "from opd_his oh, opd_details_his odh, opd_service os, opd_category oc, tmp_opd_service_filter tosf,technician t\n"
                + "where oh.opd_inv_id = odh.vou_no and odh.tech_id = t.tech_id\n"
                + "and odh.service_id = os.service_id and os.cat_id = oc.cat_id and odh.service_id = tosf.service_id\n"
                + "and tosf.user_id = $P{user_id} and oh.deleted = false\n"
                + "and date(oh.opd_date) between $P{from_date} and $P{to_date}\n"
                + "and (oh.currency_id = $P{currency} or $P{currency} = '-')\n"
                + "and (oh.payment_id = $P{payment} or $P{payment} = -1)\n"
                + "and (odh.tech_id = $P{tech_id} or $P{tech_id} = '-')\n"
                + "and (oh.patient_id = $P{reg_no} or $P{reg_no} = '-')\n"
                + "group by oc.cat_name, os.service_name,os.srv_fees,odh.srv_fees1,odh.srv_fees2,odh.srv_fees3,odh.srv_fees4,odh.srv_fees5,odh.srv_fees6,odh.is_percent,t.tech_name\n"
                + "order by t.tech_name,oc.cat_name, os.service_name";

        strSql = strSql.replace("$P{from_date}", "'" + getFromDate() + "'")
                .replace("$P{to_date}", "'" + getToDate() + "'")
                .replace("$P{reg_no}", "'" + getRegNo() + "'")
                .replace("$P{doctor}", "'" + getDoctorId() + "'")
                .replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{currency}", "'" + getCurrencyId() + "'")
                .replace("$P{payment}", getPaymentType())
                .replace("$P{session}", getSession())
                .replace("$P{opd_category}", getOpdCat());
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Tech Name");
            listHeader.add("Category");
            listHeader.add("Service Name");
            listHeader.add("Qty");
            listHeader.add("Amount");
            listHeader.add("Tech Fee");

            List<String> listField = new ArrayList();
            listField.add("tech_name");
            listField.add("cat_name");
            listField.add("service_name");
            listField.add("ttl_qty");
            listField.add("ttl_amount");
            listField.add("srv_fees4");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("tech_name", POIUtil.FormatType.TEXT);
            hmType.put("cat_name", POIUtil.FormatType.TEXT);
            hmType.put("service_name", POIUtil.FormatType.TEXT);
            hmType.put("ttl_qty", POIUtil.FormatType.INTEGER);
            hmType.put("ttl_amount", POIUtil.FormatType.DOUBLE);
            hmType.put("srv_fees4", POIUtil.FormatType.DOUBLE);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
