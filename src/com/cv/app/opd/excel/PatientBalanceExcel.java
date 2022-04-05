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
public class PatientBalanceExcel extends GenExcel {
    static Logger log = Logger.getLogger(PatientBalanceExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public PatientBalanceExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select a.reg_no, a.currency, sum(amt) balance,bs.description,a.patient_name, a.admission_no ams_no, a.dc_date\n" +
"    from (\n" +
"	  select po.reg_no, po1.patient_name, po1.admission_no, po.currency, sum(ifnull(op_amount,0)) amt,\n" +
"	         po1.dc_date\n" +
"	    from patient_op po, tmp_bill_payment po1\n" +
"	   where date(po.op_date) <= $P{to_date} and po1.user_id = $P{user_id}\n" +
"	     and (po.currency = $P{currency} or '-' = $P{currency})\n" +
"	     and po.reg_no = po1.reg_no and po.bill_type = po1.bill_type and po.currency = po1.currency\n" +
"	     and po.op_date = po1.op_date\n" +
"	   group by po.reg_no, po1.patient_name, po1.admission_no, po.currency, po1.dc_date\n" +
"	   union all\n" +
"	  select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, sum(a.amt), tbp.dc_date\n" +
"              from tmp_bill_payment tbp join (\n" +
"                   select sh.reg_no, sh.currency_id,date(sh.sale_date) sale_date, sum(ifnull(sh.balance,0)) amt\n" +
"                     from sale_his sh\n" +
"                    where deleted = false and sh.reg_no is not null \n" +
"                    group by sh.reg_no,sh.currency_id,date(sh.sale_date)) a on tbp.reg_no = a.reg_no \n" +
"               and tbp.currency = a.currency_id and a.sale_date between tbp.op_date and $P{to_date}\n" +
"             where tbp.user_id = $P{user_id} and (tbp.currency = $P{currency} or '-' = $P{currency})\n" +
"             group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n" +
"	   union all\n" +
"	  select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, sum(ifnull(oh.amt,0)) amt, tbp.dc_date\n" +
"	    from tmp_bill_payment tbp join (\n" +
"                    select patient_id, currency_id, date(opd_date) opd_date, sum(ifnull(vou_balance,0)) amt\n" +
"                      from opd_his\n" +
"                     where deleted = false and patient_id is not null\n" +
"                     group by patient_id, currency_id, date(opd_date)) oh on tbp.reg_no = oh.patient_id \n" +
"                and tbp.currency = oh.currency_id and date(oh.opd_date) between tbp.op_date and $P{to_date}\n" +
"              where tbp.user_id = $P{user_id} and (tbp.currency = $P{currency} or '-' = $P{currency})\n" +
"              group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n" +
"	    union all\n" +
"	   select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, sum(ifnull(oh.amt,0)) amt, tbp.dc_date\n" +
"	     from tmp_bill_payment tbp join (\n" +
"	          select patient_id, currency_id, date(ot_date) ot_date, sum(ifnull(vou_balance,0)) amt\n" +
"	            from ot_his\n" +
"	           where deleted = false and patient_id is not null\n" +
"		 group by patient_id, currency_id, date(ot_date)) oh on tbp.reg_no = oh.patient_id \n" +
"                and tbp.currency = oh.currency_id\n" +
"	      and date(oh.ot_date) between tbp.op_date and $P{to_date}\n" +
"	    where (tbp.currency = $P{currency} or '-' = $P{currency}) and tbp.user_id = $P{user_id}\n" +
"	    group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n" +
"	    union all\n" +
"	   select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency,sum(ifnull(oh.amt,0)) amt, tbp.dc_date\n" +
"	     from tmp_bill_payment tbp join (\n" +
"                    select patient_id, currency_id, date(dc_date) dc_date, sum(ifnull(vou_balance,0)) amt\n" +
"		 from dc_his\n" +
"		where deleted = false and patient_id is not null\n" +
"		group by patient_id, currency_id, date(dc_date)) oh on tbp.reg_no = oh.patient_id \n" +
"                and tbp.currency = oh.currency_id and date(oh.dc_date) between tbp.op_date and $P{to_date}\n" +
"	    where (tbp.currency = $P{currency} or '-' = $P{currency}) and tbp.user_id = $P{user_id}\n" +
"	    group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n" +
"	    union all\n" +
"	   select tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, (sum(ifnull(rih.amt,0))*-1) amt,\n" +
"                     tbp.dc_date\n" +
"	     from tmp_bill_payment tbp join (\n" +
"                    select reg_no, currency, date(ret_in_date) ret_in_date, sum(ifnull(balance,0)) amt\n" +
"		 from ret_in_his\n" +
"		where deleted = false and reg_no is not null\n" +
"	          group by reg_no, currency, date(ret_in_date)) rih on tbp.reg_no = rih.reg_no \n" +
"                and tbp.currency = rih.currency and date(rih.ret_in_date) between tbp.op_date and $P{to_date}\n" +
"	    where tbp.user_id = $P{user_id} and (tbp.currency = $P{currency} or '-' = $P{currency})\n" +
"	    group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date\n" +
"	    union all\n" +
"	   select tbp.reg_no, tbp.patient_name,tbp.admission_no, tbp.currency, \n" +
"                    (sum(ifnull(opbp.amt,0))*-1) amt, tbp.dc_date\n" +
"	     from tmp_bill_payment tbp join (\n" +
"                     select reg_no, currency_id, date(pay_date) pay_date, (sum(ifnull(pay_amt,0))) amt\n" +
"		  from opd_patient_bill_payment\n" +
"		 where reg_no is not null\n" +
"		 group by reg_no, currency_id, date(pay_date)) opbp on tbp.reg_no = opbp.reg_no \n" +
"                 and tbp.currency = opbp.currency_id and date(opbp.pay_date) between tbp.op_date and $P{to_date}\n" +
"	     where tbp.user_id = $P{user_id} and (tbp.currency = $P{currency} or '-' = $P{currency})\n" +
"	     group by tbp.reg_no, tbp.patient_name, tbp.admission_no, tbp.currency, tbp.dc_date) a\n" +
"    left join admission adm on a.admission_no = adm.ams_no\n" +
"    left join building_structure bs on adm.building_structure_id = bs.id\n" +
"   where (a.reg_no = $P{reg_no} or '-' = $P{reg_no})\n" +
"   group by a.reg_no, a.currency, bs.description,a.patient_name, a.dc_date\n" +
"   having sum(a.amt) <> 0\n" +
"order by a.patient_name";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'")
                .replace("$P{currency}", "'" + getCurrencyId() + "'")
                .replace("$P{to_date}", "'" + getToDate() + "'")
                .replace("$P{reg_no}", "'" + getRegNo() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Reg No");
            listHeader.add("Admission No");
            listHeader.add("DC Date");
            listHeader.add("Patient Name");
            listHeader.add("Room");
            listHeader.add("Balance");
            
            List<String> listField = new ArrayList();
            listField.add("reg_no");
            listField.add("ams_no");
            listField.add("dc_date");
            listField.add("patient_name");
            listField.add("description");
            listField.add("balance");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("reg_no", POIUtil.FormatType.TEXT);
            hmType.put("ams_no", POIUtil.FormatType.TEXT);
            hmType.put("dc_date", POIUtil.FormatType.TEXT);
            hmType.put("patient_name", POIUtil.FormatType.TEXT);
            hmType.put("description", POIUtil.FormatType.TEXT);
            hmType.put("balance", POIUtil.FormatType.DOUBLE);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
