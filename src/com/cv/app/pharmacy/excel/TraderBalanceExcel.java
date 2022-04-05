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
public class TraderBalanceExcel extends GenExcel {

    static Logger log = Logger.getLogger(TraderBalanceExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public TraderBalanceExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select ttbd.trader_id, v_cs.trader_name, ttbd.amount, ttbd.curr_id, curr.cur_name\n"
                + "from tmp_trader_bal_date ttbd, v_cs, currency curr\n"
                + "where ttbd.trader_id = v_cs.trader_id and ttbd.curr_id = curr.cur_code\n"
                + "and user_id = $P{user_id} and v_cs.discriminator = 'S'";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("Code");
            listHeader.add("Supplier Name");
            listHeader.add("Balance");

            List<String> listField = new ArrayList();
            listField.add("trader_id");
            listField.add("trader_name");
            listField.add("amount");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("trader_id", POIUtil.FormatType.TEXT);
            hmType.put("trader_name", POIUtil.FormatType.TEXT);
            hmType.put("amount", POIUtil.FormatType.FLOAT);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFile(listHeader, listField, hmType, rs, fileName, "-", "-", "-");
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
