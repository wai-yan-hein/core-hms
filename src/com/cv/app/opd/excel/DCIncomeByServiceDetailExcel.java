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
public class DCIncomeByServiceDetailExcel extends GenExcel {

    static Logger log = Logger.getLogger(DCIncomeByServiceDetailExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public DCIncomeByServiceDetailExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select tmp.cat_name,tmp.service_name,tmp.patient_name,sum(tmp.ttl_qty1) ttl_qty,tmp.admission_no,\n"
                + "sum(tmp.ttl_amount1) ttl_amount,tmp.doctor_name,sum(tmp.srv_fee_amt1) srv_fee1_amt1,\n"
                + "sum(tmp.srv_fee_amt2) srv_fee1_amt2,sum(tmp.srv_fee_amt3) srv_fee1_amt3,sum(tmp.srv_fee_amt4) srv_fee1_amt4,\n"
                + "sum(tmp.srv_fee_amt5) srv_fee1_amt5 from\n"
                + "((select oc.cat_name, os.service_name,oh.patient_name,sum(ifnull(odh.qty,0)) ttl_qty1,oh.admission_no, sum(ifnull(odh.amount,0)) ttl_amount1,d.doctor_name,\n"
                + "sum(ifnull(odh.qty, 0)*ifnull(odh.srv_fee1,0)) srv_fee_amt1,\n"
                + "sum(ifnull(odh.qty, 0)*ifnull(odh.srv_fee2,0)) srv_fee_amt2,\n"
                + "sum(ifnull(odh.qty, 0)*ifnull(odh.srv_fee3,0)) srv_fee_amt3,\n"
                + "sum(ifnull(odh.qty, 0)*ifnull(odh.srv_fee4,0)) srv_fee_amt4,\n"
                + "sum(ifnull(odh.qty, 0)*ifnull(odh.srv_fee5,0)) srv_fee_amt5\n"
                + "from dc_his oh, dc_details_his odh , inp_service os, inp_category oc, tmp_dc_service_filter tosf,doctor d\n"
                + "where oh.dc_inv_id = odh.vou_no and d.doctor_id = oh.doctor_id and odh.dc_detail_id not in (select dc_detail_id from dc_dr_fee_join)\n"
                + "and odh.service_id = os.service_id and os.cat_id = oc.cat_id and odh.service_id = tosf.service_id\n"
                + "and tosf.user_id = $P{user_id} and oh.deleted = false\n"
                + "and date(oh.dc_date) between $P{from_date} and $P{to_date}\n"
                + "and (oh.currency_id = $P{currency} or $P{currency} = '-')\n"
                + "and (oh.payment_id = $P{payment} or $P{payment} = -1)\n"
                + "and (oh.doctor_id = $P{doctor} or $P{doctor} = '-')\n"
                + "and (oh.patient_id = $P{reg_no} or $P{reg_no} = '-')\n"
                + "group by oc.cat_name, os.service_name,d.doctor_name,oh.patient_name,oh.admission_no\n"
                + "order by d.doctor_name,oc.cat_name, os.service_name,oh.patient_name)\n"
                + "union\n"
                + "(select oc.cat_name, os.service_name,oh.patient_name,sum(ifnull(odh.qty,0)) ttl_qty1,oh.admission_no,\n"
                + "if(ddf.doctor_id = null, sum(odh.amount), sum(ddf.dr_fee)) amount1,d.doctor_name,\n"
                + "sum(ifnull(odh.qty, 0)*ifnull(odh.srv_fee1,0)) srv_fee_amt1,\n"
                + "sum(ifnull(odh.qty, 0)*ifnull(odh.srv_fee2,0)) srv_fee_amt2,\n"
                + "sum(ifnull(odh.qty, 0)*ifnull(odh.srv_fee3,0)) srv_fee_amt3,\n"
                + "sum(ifnull(odh.qty, 0)*ifnull(odh.srv_fee4,0)) srv_fee_amt4,\n"
                + "sum(ifnull(odh.qty, 0)*ifnull(odh.srv_fee5,0)) srv_fee_amt5\n"
                + "from dc_his oh, dc_details_his odh ,dc_doctor_fee ddf,dc_dr_fee_join ddfj,inp_service os, inp_category oc, tmp_dc_service_filter tosf,\n"
                + "doctor d\n"
                + "where oh.dc_inv_id = odh.vou_no and ddfj.dc_detail_id = odh.dc_detail_id  and odh.service_id = tosf.service_id and tosf.user_id = $P{user_id}\n"
                + "and ddfj.dr_fee_id = ddf.dr_fee_id and odh.service_id = os.service_id and os.cat_id = oc.cat_id  and d.doctor_id = ddf.doctor_id\n"
                + "and oh.deleted = false\n"
                + "and date(oh.dc_date) between $P{from_date} and $P{to_date}\n"
                + "and (oh.currency_id = $P{currency} or $P{currency} = '-')\n"
                + "and (oh.payment_id = $P{payment} or $P{payment} = -1)\n"
                + "and (ddf.doctor_id = $P{doctor} or $P{doctor} = '-')\n"
                + "and (oh.patient_id = $P{reg_no} or $P{reg_no} = '-')\n"
                + "group by oc.cat_name, os.service_name,d.doctor_name,oh.patient_name,oh.admission_no\n"
                + "order by d.doctor_name,oc.cat_name, os.service_name,oh.patient_name)) tmp\n"
                + "group by tmp.cat_name, tmp.service_name,tmp.doctor_name,tmp.patient_name,tmp.admission_no\n"
                + "order by tmp.doctor_name,tmp.cat_name, tmp.service_name,tmp.patient_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{from_date}", "'" + getFromDate() + "'")
                .replace("$P{to_date}", "'" + getToDate() + "'")
                .replace("$P{currency}", "'" + getCurrencyId() + "'")
                .replace("$P{payment}", getPaymentType())
                .replace("$P{doctor}", "'" + getDoctorId() + "'")
                .replace("$P{reg_no}", "'" + getRegNo() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Doctor Name :");
            listHeader.add("Group Name");
            listHeader.add("Adm No");
            listHeader.add("Patient Name");
            listHeader.add("DC Service Name");
            listHeader.add("Qty");
            listHeader.add("Amount");
            listHeader.add("To Doc");
            listHeader.add("To Nurse");
            listHeader.add("To Tech");

            List<String> listField = new ArrayList();
            listField.add("doctor_name");
            listField.add("cat_name");
            listField.add("admission_no");
            listField.add("patient_name");
            listField.add("service_name");
            listField.add("ttl_qty");
            listField.add("ttl_amount");
            listField.add("srv_fee1_amt1");
            listField.add("srv_fee1_amt2");
            listField.add("srv_fee1_amt3");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("doctor_name", POIUtil.FormatType.TEXT);
            hmType.put("cat_name", POIUtil.FormatType.TEXT);
            hmType.put("admission_no", POIUtil.FormatType.TEXT);
            hmType.put("patient_name", POIUtil.FormatType.TEXT);
            hmType.put("service_name", POIUtil.FormatType.TEXT);
            hmType.put("ttl_qty", POIUtil.FormatType.DOUBLE);
            hmType.put("ttl_amount", POIUtil.FormatType.DOUBLE);
            hmType.put("srv_fee1_amt1", POIUtil.FormatType.DOUBLE);
            hmType.put("srv_fee1_amt2", POIUtil.FormatType.DOUBLE);
            hmType.put("srv_fee1_amt3", POIUtil.FormatType.DOUBLE);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
