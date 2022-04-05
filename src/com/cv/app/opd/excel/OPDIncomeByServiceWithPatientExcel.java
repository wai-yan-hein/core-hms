/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.excel;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.excel.GenExcel;
import com.cv.app.util.POIUtil;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author USER
 */
public class OPDIncomeByServiceWithPatientExcel extends GenExcel {

    static Logger log = Logger.getLogger(DCIncomeByDoctorExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public OPDIncomeByServiceWithPatientExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select oc.cat_name, os.service_name, sum(ifnull(odh.qty, 0)) ttl_qty, sum(ifnull(odh.amount,0)) ttl_amount,oh.patient_name\n"
                + "from opd_his oh, opd_details_his odh, opd_service os, opd_category oc, tmp_opd_service_filter tosf\n"
                + "where oh.opd_inv_id = odh.vou_no\n"
                + "and odh.service_id = os.service_id and os.cat_id = oc.cat_id and odh.service_id = tosf.service_id\n"
                + "and tosf.user_id = $P{user_id} and oh.deleted = false\n"
                + "and (odh.charge_type = $P{charge_type} or  $P{charge_type} = -1)\n"
                + "and date(oh.opd_date) between $P{from_date} and $P{to_date}\n"
                + "and (oh.currency_id = $P{currency} or $P{currency} = '-')\n"
                + "and (oh.payment_id = $P{payment} or $P{payment} = -1)\n"
                + "and (oh.doctor_id = $P{doctor} or $P{doctor} = '-')\n"
                + "and (oh.patient_id = $P{reg_no} or $P{reg_no} = '-')\n"
                + "group by oc.cat_name, os.service_name,oh.patient_name\n"
                + "order by oh.patient_name,oc.cat_name, os.service_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{from_date}", "'" + getFromDate() + "'")
                .replace("$P{to_date}", "'" + getFromDate() + "'")
                .replace("$P{charge_type}", getChargeType())
                .replace("$P{currency}", "'" + getCurrencyId() + "'")
                .replace("$P{payment}", getPaymentType())
                .replace("$P{doctor}", "'" + getDoctorId() + "'")
                .replace("$P{reg_no}", "'" + getRegNo() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Group Name");
            listHeader.add("Service Name");
            listHeader.add("Patient Name");
            listHeader.add("Qty");
            listHeader.add("Amount");

            List<String> listField = new ArrayList();
            listField.add("cat_name");
            listField.add("service_name");
            listField.add("patient_name");
            listField.add("ttl_qty");
            listField.add("ttl_amount");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("cat_name", POIUtil.FormatType.TEXT);
            hmType.put("service_name", POIUtil.FormatType.TEXT);
            hmType.put("patient_name", POIUtil.FormatType.TEXT);
            hmType.put("ttl_qty", POIUtil.FormatType.INTEGER);
            hmType.put("ttl_amount", POIUtil.FormatType.DOUBLE);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
