/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.excel;

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
public class AuditLogExcel extends GenExcel {

    static Logger log = Logger.getLogger(AuditLogExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public AuditLogExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select tran_option, sale_date tran_date, sale_inv_id vou_no, admission_no, curr_deleted, curr_vou_total,\n"
                + "curr_discount, curr_paid_amount, curr_balance, deleted, vou_total, discount, paid_amount, balance, cusr.user_name as create_user,\n" +
"eusr.user_name as edit_user, bk_date as edit_date, vaev.edit_machine, mi.machine_name, vaev.new_vtotal, vaev.new_paid, vaev.new_disc, vaev.new_balance \n"
                + "from v_all_edit_voucher vaev "
                + " left join appuser cusr on vaev.create_user_id =cusr.user_id"
                + " left join appuser eusr on vaev.edit_user_id = eusr.user_id"
                + " left join machine_info mi on vaev.edit_machine = mi.machine_id "
                + "where date(sale_date) between  $P{prm_from} and $P{prm_to}\n"
                + "order by tran_option, sale_inv_id, sale_date, bk_date";

        strSql = strSql.replace("$P{prm_from} ", "'" + getFromDate() + "'")
                .replace("$P{prm_to}", "'" + getToDate() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Tran Option");
            listHeader.add("Tran Date");
            listHeader.add("Edit Date");
            listHeader.add("Vou No");
            listHeader.add("Adm No.");
            listHeader.add("CD");
            listHeader.add("C-Total");
            listHeader.add("C-Disc");
            listHeader.add("C-Paid");
            listHeader.add("C-VBal");
            listHeader.add("D");
            listHeader.add("VTotal");
            listHeader.add("VDisc");
            listHeader.add("VPaid");
            listHeader.add("VBal");
            listHeader.add("Create User");
            listHeader.add("Edit User");
            listHeader.add("Edit Machine");
            listHeader.add("Change To Vou Total");
            listHeader.add("Change To Vou Paid");
            listHeader.add("Change To Vou Discount");
            listHeader.add("Change To Vou Balance");
            
            List<String> listField = new ArrayList();
            listField.add("tran_option");
            listField.add("tran_date");
            listField.add("edit_date");
            listField.add("vou_no");
            listField.add("admission_no");
            listField.add("curr_deleted");
            listField.add("curr_vou_total}");
            listField.add("curr_discount");
            listField.add("curr_paid_amount");
            listField.add("curr_balance");
            listField.add("deleted");
            listField.add("vou_total");
            listField.add("discount");
            listField.add("paid_amount");
            listField.add("balance");
            listField.add("create_user");
            listField.add("edit_user");
            
            listField.add("machine_name");
            listField.add("new_vtotal");
            listField.add("new_paid");
            listField.add("new_disc");
            listField.add("new_balance");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("tran_option", POIUtil.FormatType.TEXT);
            hmType.put("tran_date", POIUtil.FormatType.TEXT);
            hmType.put("edit_date", POIUtil.FormatType.TEXT);
            hmType.put("vou_no", POIUtil.FormatType.TEXT);
            hmType.put("admission_no", POIUtil.FormatType.TEXT);
            hmType.put("curr_deleted", POIUtil.FormatType.INTEGER);
            hmType.put("curr_vou_total", POIUtil.FormatType.DOUBLE);
            hmType.put("curr_discount", POIUtil.FormatType.DOUBLE);
            hmType.put("curr_paid_amount", POIUtil.FormatType.DOUBLE);
            hmType.put("curr_balance", POIUtil.FormatType.DOUBLE);
            hmType.put("deleted", POIUtil.FormatType.INTEGER);
            hmType.put("vou_total", POIUtil.FormatType.DOUBLE);
            hmType.put("discount", POIUtil.FormatType.DOUBLE);
            hmType.put("paid_amount", POIUtil.FormatType.DOUBLE);
            hmType.put("balance", POIUtil.FormatType.DOUBLE);
            hmType.put("create_user", POIUtil.FormatType.TEXT);
            hmType.put("edit_user", POIUtil.FormatType.TEXT);
            hmType.put("machine_name", POIUtil.FormatType.TEXT);
            
            hmType.put("new_vtotal", POIUtil.FormatType.DOUBLE);
            hmType.put("new_paid", POIUtil.FormatType.DOUBLE);
            hmType.put("new_disc", POIUtil.FormatType.DOUBLE);
            hmType.put("new_balance", POIUtil.FormatType.DOUBLE);
            
            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, 
                    "Audit Log", getFromDate(), getToDate());
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
