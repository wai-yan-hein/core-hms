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
public class LabReferDoctorSummarySeinExcel extends GenExcel {

    static Logger log = Logger.getLogger(LabReferDoctorSummarySeinExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public LabReferDoctorSummarySeinExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select a.doctor_name, sum(a.lab_amt) as lab_amt, sum(a.xu_amt) as xu_amt, sum(a.lab_amt+a.xu_amt) as total\n"
                + "from (\n"
                + "select d.doctor_name,\n"
                + "if(oc.group_id = 1,\n"
                + "if(odh.is_percent = 0,if(odh.srv_fees5 > 0, ifnull(odh.qty, 0) * ifnull(odh.srv_fees5,0),0),\n"
                + "if(odh.srv_fees5 > 0 ,ifnull(odh.amount,0)* ifnull(odh.srv_fees5,0) / 100,0)), 0) lab_amt,\n"
                + "if(oc.group_id = 2,\n"
                + "if(odh.is_percent = 0,if(odh.srv_fees5 > 0, ifnull(odh.qty, 0) * ifnull(odh.srv_fees5,0),0),\n"
                + "if(odh.srv_fees5 > 0 ,ifnull(odh.amount,0)* ifnull(odh.srv_fees5,0) / 100,0)), 0) xu_amt\n"
                + "from opd_his oh\n"
                + "join opd_details_his odh on oh.opd_inv_id = odh.vou_no\n"
                + "join opd_service os on odh.service_id = os.service_id\n"
                + "join opd_category oc on os.cat_id = oc.cat_id\n"
                + "left join doctor d on refer_doctor_id = d.doctor_id\n"
                + "left join patient_detail pd on oh.patient_id = pd.reg_no\n"
                + "where oh.deleted = false\n"
                + "and date(oh.opd_date) between $P{from_date} and $P{to_date}\n"
                + "and oc.group_id in (1,2)\n"
                + "and (odh.refer_doctor_id = $P{doctor} or $P{doctor} = '-')\n"
                + ") a\n"
                + "group by a.doctor_name";

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
            listHeader.add("Doctor Name");
            listHeader.add("Lab Refer Fee");
            listHeader.add("X-Ray & USG Refer");
            listHeader.add("Total Amount");

            List<String> listField = new ArrayList();
            listField.add("doctor_name");
            listField.add("lab_amt");
            listField.add("xu_amt");
            listField.add("total");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("doctor_name", POIUtil.FormatType.TEXT);
            hmType.put("lab_amt", POIUtil.FormatType.DOUBLE);
            hmType.put("xu_amt", POIUtil.FormatType.DOUBLE);
            hmType.put("total", POIUtil.FormatType.DOUBLE);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
