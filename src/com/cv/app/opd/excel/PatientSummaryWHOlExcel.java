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
public class PatientSummaryWHOlExcel extends GenExcel {

    static Logger log = Logger.getLogger(PatientSummaryWHOlExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public PatientSummaryWHOlExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select a.* from (select pt.payment_type_name,a.cus_id, a.trader_name, if(ifnull(admission_no,'')='','OPD','Admission') as pd_status,\n"
                + "GET_AGE(pd.dob) as age,sum(a.vou_total) as ttl_amt, DATE_FORMAT(min(tran_date),'%d/%m/%Y') as tran_date \n"
                + "from (\n"
                + "select source, date(tran_date) tran_date, inv_id, cus_id, trader_name,\n"
                + "if(source = 'Return In', vou_total*-1,vou_total) vou_total, paid, discount, tax_amt,\n"
                + "if(source = 'Return In', balance*-1, balance) balance, payment_type_name,\n"
                + "currency, payment_type\n"
                + "from v_session\n"
                + "where deleted = false and date(tran_date) between $P{from_date} and $P{to_date}\n"
                + "and (payment_type = $P{payment} or $P{payment} = -1)\n"
                + "and source in ('Sale', 'Return In')\n"
                + "and (cus_id = $P{reg_no} or $P{reg_no} = '-')\n"
                + "union all\n"
                + "select tran_option source, date(tran_date) tran_date, opd_inv_id inv_id, patient_id cus_id,\n"
                + "pt_name trader_name, vou_total, paid, disc_a discount, tax_a tax_amt, vou_balance balance,\n"
                + "payment_type_name, currency_id currency, payment_type_id payment_type\n"
                + "from v_session_clinic\n"
                + "where deleted = false and date(tran_date) between $P{from_date} and $P{to_date}\n"
                + "and (payment_type_id = $P{payment} or $P{payment} = -1)\n"
                + "and (patient_id = $P{reg_no} or $P{reg_no} = '-')\n"
                + ") a\n"
                + "left join patient_detail pd on a.cus_id = pd.reg_no\n"
                + "left join payment_type pt on a.payment_type = pt.payment_type_id\n"
                + "group by pt.payment_type_name,a.cus_id, a.trader_name\n"
                + "order by a.trader_name, a.source, date(a.tran_date)) a order by a.tran_date,a.trader_name";

        strSql = strSql.replace("$P{from_date}", "'" + getFromDate() + "'")
                .replace("$P{to_date}", "'" + getToDate() + "'")
                .replace("$P{payment}", getPaymentType())
                .replace("$P{reg_no}", "'" + getRegNo() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Bill Name");
            listHeader.add("Tran Date");
            listHeader.add("Reg No");
            listHeader.add("Patient Name");
            listHeader.add("Status");
            listHeader.add("Age");
            listHeader.add("Amount");

            List<String> listField = new ArrayList();
            listField.add("payment_type_name");
            listField.add("tran_date");
            listField.add("cus_id");
            listField.add("trader_name");
            listField.add("pd_status");
            listField.add("age");
            listField.add("ttl_amt");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("payment_type_name", POIUtil.FormatType.TEXT);
            hmType.put("tran_date", POIUtil.FormatType.TEXT);
            hmType.put("cus_id", POIUtil.FormatType.TEXT);
            hmType.put("trader_name", POIUtil.FormatType.TEXT);
            hmType.put("pd_status", POIUtil.FormatType.TEXT);
            hmType.put("service_name", POIUtil.FormatType.TEXT);
            hmType.put("age", POIUtil.FormatType.TEXT);
            hmType.put("ttl_amt", POIUtil.FormatType.DOUBLE);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
