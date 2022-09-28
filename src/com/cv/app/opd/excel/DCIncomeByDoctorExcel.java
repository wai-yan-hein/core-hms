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
public class DCIncomeByDoctorExcel extends GenExcel {
    static Logger log = Logger.getLogger(DCIncomeByDoctorExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;
    
    public DCIncomeByDoctorExcel(AbstractDataAccess dao, String fileName){
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }
    
    @Override
    public void genExcel(){
        String strSql = "select a.doctor_name,a.admission_no,a.patient_name,a.patient_id, sum(a.discount) as discount,\n" +
"sum(total) as total, sum(amt) as amt\n" +
"from\n" +
"(\n" +
"  select dr.doctor_name, dh.patient_id, dh.admission_no, pd.patient_name,\n" +
"         sum(dh.disc_a) as discount, sum(dh.vou_total) as total, sum(dh.vou_total-dh.disc_a) as amt\n" +
"    from dc_his dh\n" +
"    left join patient_detail pd on dh.patient_id = pd.reg_no\n" +
"    left join doctor dr on dh.doctor_id = dr.doctor_id\n" +
"   where date(dh.dc_date) between $P{from_date} and $P{to_date}\n" +
"     and dh.deleted = false\n" +
"     and (dh.patient_id = $P{reg_no} or $P{reg_no} = '-')\n" +
"     and (dh.doctor_id = $P{doctor} or $P{doctor} = '-')\n" +
"   group by dr.doctor_name, dh.patient_id, dh.admission_no, pd.patient_name\n" +
"   union all\n" +
"  select ifnull(dr.doctor_name,dr1.doctor_name) as doctor_name, sh.reg_no as patient_id, sh.admission_no,\n" +
"         pd.patient_name,\n" +
"         sum(sh.discount) as discount, sum(sh.vou_total) as total, sum(sh.vou_total-sh.discount) as amt\n" +
"    from sale_his sh\n" +
"    left join patient_detail pd on sh.reg_no = pd.reg_no\n" +
"    left join doctor dr on sh.doctor_id = dr.doctor_id\n" +
"    left join doctor dr1 on pd.doctor_id = dr1.doctor_id\n" +
"   where date(sh.sale_date) between $P{from_date} and $P{to_date}\n" +
"     and sh.deleted = false and sh.location_id = 1\n" +
"     and if(sh.admission_no='',null,sh.admission_no) is not null\n" +
"     and (sh.reg_no = $P{reg_no} or $P{reg_no} = '-')\n" +
"     and (sh.doctor_id = $P{doctor} or $P{doctor} = '-')\n" +
"   group by dr.doctor_name, sh.reg_no, sh.admission_no, pd.patient_name\n" +
"   union all\n" +
"  select '-' as doctor_name, rih.reg_no as patient_id, rih.admission_no, pd.patient_name,\n" +
"         0 as discount, sum(rih.vou_total*-1) as total, sum(rih.vou_total*-1) as amt\n" +
"    from ret_in_his rih\n" +
"    left join patient_detail pd on rih.reg_no = pd.reg_no\n" +
"   where date(rih.ret_in_date) between $P{from_date} and $P{to_date}\n" +
"     and rih.location = 1\n" +
"     and rih.deleted = false and if(rih.admission_no='',null,rih.admission_no) is not null\n" +
"     and (rih.reg_no = $P{reg_no} or $P{reg_no} = '-')\n" +
"   group by rih.reg_no, rih.admission_no, pd.patient_name\n" +
"   union all\n" +
"  select dr.doctor_name, oh.patient_id, oh.admission_no, pd.patient_name,\n" +
"         sum(ifnull(oh.disc_a,0)) as discount, sum(ifnull(oh.vou_total,0)) as total,\n" +
"         sum(ifnull(oh.vou_total,0)-ifnull(oh.disc_a,0)) as amt\n" +
"    from opd_his oh\n" +
"    left join patient_detail pd on oh.patient_id = pd.reg_no\n" +
"    left join doctor dr on oh.doctor_id = dr.doctor_id\n" +
"   where date(oh.opd_date) between $P{from_date} and $P{to_date}\n" +
"     and oh.deleted = false and if(oh.admission_no='',null,oh.admission_no) is not null\n" +
"     and (oh.patient_id = $P{reg_no} or $P{reg_no} = '-')\n" +
"   group by dr.doctor_name, oh.patient_id, oh.admission_no, pd.patient_name\n" +
") a\n" +
"group by a.doctor_name,a.admission_no,a.patient_name,a.patient_id\n" +
"order by a.doctor_name,a.admission_no";

        strSql = strSql.replace("$P{from_date}", "'" + getFromDate() + "'")
                .replace("$P{to_date}", "'" + getToDate() + "'")
                .replace("$P{reg_no}", "'" + getRegNo() + "'")
                .replace("$P{doctor}", "'" + getDoctorId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Doctor Name");
            listHeader.add("Reg No.");
            listHeader.add("Ams No.");
            listHeader.add("Patient Name");
            listHeader.add("Discount");
            listHeader.add("V-Total");
            listHeader.add("Amount");
            
            List<String> listField = new ArrayList();
            listField.add("doctor_name");
            listField.add("patient_id");
            listField.add("admission_no");
            listField.add("patient_name");
            listField.add("discount");
            listField.add("total");
            listField.add("amt");
            
            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("doctor_name", POIUtil.FormatType.TEXT);
            hmType.put("patient_id", POIUtil.FormatType.TEXT);
            hmType.put("admission_no", POIUtil.FormatType.TEXT);
            hmType.put("patient_name", POIUtil.FormatType.TEXT);
            hmType.put("discount", POIUtil.FormatType.DOUBLE);
            hmType.put("total", POIUtil.FormatType.DOUBLE);
            hmType.put("amt", POIUtil.FormatType.DOUBLE);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
