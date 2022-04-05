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
public class LabReferWithDoctorSeinExcel extends GenExcel {

    static Logger log = Logger.getLogger(LabReferWithDoctorSeinExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public LabReferWithDoctorSeinExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select a.*, if(ifnull(a.admission_no,'')='',a.qty,0) as opd_qty,\n"
                + "if(ifnull(a.admission_no,'')='',0,a.qty) as ipd_qty,\n"
                + "if(ifnull(a.admission_no,'')='',a.refer_amt,0) as opd_amt,\n"
                + "if(ifnull(a.admission_no,'')='',0,a.refer_amt) as ipd_amt,\n"
                + "(a.refer_amt) as ttl_amt\n"
                + "from (\n"
                + "select d.doctor_name,pd.patient_name,oc.cat_name, os.service_name,ifnull(odh.qty, 0) qty,\n"
                + "if(odh.is_percent = 0,if(odh.srv_fees5 > 0, ifnull(odh.qty, 0) * ifnull(odh.srv_fees5,0),0),\n"
                + "if(odh.srv_fees5 > 0 ,ifnull(odh.amount,0)* ifnull(odh.srv_fees5,0) / 100,0)) refer_amt,\n"
                + "oh.admission_no\n"
                + "from opd_his oh\n"
                + "join opd_details_his odh on oh.opd_inv_id = odh.vou_no\n"
                + "join opd_service os on odh.service_id = os.service_id\n"
                + "join opd_category oc on os.cat_id = oc.cat_id\n"
                + "join tmp_opd_service_filter tosf on odh.service_id = tosf.service_id\n"
                + "left join doctor d on refer_doctor_id = d.doctor_id\n"
                + "left join patient_detail pd on oh.patient_id = pd.reg_no\n"
                + "where tosf.user_id = $P{user_id} and oh.deleted = false\n"
                + "and date(oh.opd_date) between $P{from_date} and $P{to_date}\n"
                + "and (oh.currency_id = $P{currency} or $P{currency} = '-')\n"
                + "and (oh.payment_id = $P{payment} or $P{payment} = -1)\n"
                + "and (odh.refer_doctor_id = $P{doctor} or $P{doctor} = '-')\n"
                + "and (oh.patient_id = $P{reg_no} or $P{reg_no} = '-')\n"
                + "and (oh.session_id = $P{session} or $P{session} = '-')\n"
                + "and (os.cat_id = $P{opd_category} or $P{opd_category} = -1)) a\n"
                + "order by a.doctor_name,a.cat_name, a.service_name";

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
            listHeader.add("Patient Name");
            listHeader.add("Category");
            listHeader.add("Service Name");
            listHeader.add("OPD Qty");
            listHeader.add("IPD Qty");
            listHeader.add("OPD Refer Fee");
            listHeader.add("IPD Refer Fee");
            listHeader.add("Total Amount");

            List<String> listField = new ArrayList();
            listField.add("doctor_name");
            listField.add("patient_name");
            listField.add("cat_name");
            listField.add("service_name");
            listField.add("opd_qty");
            listField.add("ipd_qty");
            listField.add("opd_amt");
            listField.add("ipd_amt");
            listField.add("ttl_amt");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("doctor_name", POIUtil.FormatType.TEXT);
            hmType.put("patient_name", POIUtil.FormatType.TEXT);
            hmType.put("cat_name", POIUtil.FormatType.TEXT);
            hmType.put("service_name", POIUtil.FormatType.TEXT);
            hmType.put("opd_qty", POIUtil.FormatType.INTEGER);
            hmType.put("ipd_qty", POIUtil.FormatType.INTEGER);
            hmType.put("opd_amt", POIUtil.FormatType.DOUBLE);
            hmType.put("ipd_amt", POIUtil.FormatType.DOUBLE);
            hmType.put("ttl_amt", POIUtil.FormatType.DOUBLE);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
