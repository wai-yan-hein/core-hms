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
public class SupplierListExcel extends GenExcel {

    static Logger log = Logger.getLogger(SupplierListExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public SupplierListExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select tr.trader_id, tr.trader_name, tr.phone, tr.email, tr.credit_days, tr.credit_limit, tr.address, \n"
                + "ts.township_name, tt.description as price_type, cg.group_name, b.description as business_type, active\n"
                + "from trader tr\n"
                + "left join township ts on tr.township = ts.township_id\n"
                + "left join trader_type tt on tr.type_id = tt.trader_type_id\n"
                + "left join customer_group cg on tr.group_id = cg.group_id\n"
                + "left join business b on tr.business_id = b.business_id\n"
                + "join (select distinct trader_id from tmp_trader_bal_filter where user_id = $P{user_id}) f on tr.trader_id = f.trader_id \n"
                + "where tr.discriminator = 'S'\n"
                + "order by tr.trader_name;";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Customer Code");
            listHeader.add("Customer Name");
            listHeader.add("Phone");
            listHeader.add("Email");
            listHeader.add("Credit Days");
            listHeader.add("Credit Limit");
            listHeader.add("Address");
            listHeader.add("Township");
            listHeader.add("Price Type");
            listHeader.add("Group Name");
            listHeader.add("Business Type");
            listHeader.add("Active Status");

            List<String> listField = new ArrayList();
            listField.add("trader_id");
            listField.add("trader_name");
            listField.add("phone");
            listField.add("email");
            listField.add("credit_days");
            listField.add("credit_limit");
            listField.add("address");
            listField.add("township_name");
            listField.add("price_type");
            listField.add("group_name");
            listField.add("business_type");
            listField.add("active");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("trader_id", POIUtil.FormatType.TEXT);
            hmType.put("trader_name", POIUtil.FormatType.TEXT);
            hmType.put("phone", POIUtil.FormatType.TEXT);
            hmType.put("email", POIUtil.FormatType.TEXT);
            hmType.put("credit_days", POIUtil.FormatType.INTEGER);
            hmType.put("credit_limit", POIUtil.FormatType.DOUBLE);
            hmType.put("address", POIUtil.FormatType.TEXT);
            hmType.put("township_name", POIUtil.FormatType.TEXT);
            hmType.put("price_type", POIUtil.FormatType.TEXT);
            hmType.put("group_name", POIUtil.FormatType.TEXT);
            hmType.put("business_type", POIUtil.FormatType.TEXT);
            hmType.put("active", POIUtil.FormatType.TEXT);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
