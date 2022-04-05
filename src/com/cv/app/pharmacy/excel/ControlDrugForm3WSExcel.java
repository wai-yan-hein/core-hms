package com.cv.app.pharmacy.excel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class ControlDrugForm3WSExcel extends GenExcel {

    static Logger log = Logger.getLogger(ControlDrugForm3WSExcel.class.getName());
    private final AbstractDataAccess dao;
    private final String fileName;

    public ControlDrugForm3WSExcel(AbstractDataAccess dao, String fileName) {
        super(dao);
        this.dao = dao;
        this.fileName = fileName;
    }

    @Override
    public void genExcel() {
        String strSql = "select tran_date, item_id, item_name, \n"
                + "op_qty, sale_qty, in_ttl, out_ttl, closing,\n"
                + "name1 as vou_no, reg_no, name2 as pt_name, name3 as nric, concat(dr.doctor_name,'(',license_no, ')') as doctor_name,\n"
                + "if(ifnull(tsio.in_ttl,0)<>0,concat('Total In : ',in_ttl),concat(name2,'(',reg_no,')')) as pt_name1\n"
                + "from tmp_stock_in_out tsio\n"
                + "left join doctor dr on tsio.name4 = dr.doctor_id\n"
                + "where user_id = $P{user_id} and (in_ttl <> 0 or out_ttl <> 0)\n"
                + "order by tran_id, item_id, tran_date ";

        strSql = strSql.replace("$P{user_id}", "'" + getUserId() + "'");
        try {
            List<String> listHeader = new ArrayList();
            listHeader.add("ေဆးဆိုုင္က ၀ယ္ယူသည့္ ေရာင္းခ်သည့္ ရက္စြဲ");
            listHeader.add("ေဆးဆိုုင္က ၀ယ္ယူသည့္ ကုမၸဏီ၊ပုုဂိုုလ္၊ ေဆးဆိုုင္အမည္");
            listHeader.add("ထုုတ္လုုပ္သည့္ အပတ္စဥ္အမွတ္၊ သက္တမ္း ကုုန္ဆံုုးရက္");
            listHeader.add("လက္ခံရ ရွိသည့္ ပမာဏ");
            listHeader.add("ေရာင္းခ် သည့္ ပမာဏ");
            listHeader.add("ေရာင္းခ်သည့္ ေျပစာ အမွတ္");
            listHeader.add("၀ယ္ယူသအမည္ ႏိုုင္ငံသားစီစစ္ေရး ကတ္ျပားအမွတ္၊ ေနရပ္လိုုပ္စာ");
            listHeader.add("ေရာဂါရွင္အမည္၊ ေရာဂါ၊ လူနညမွတ္ပံုုတင္ အမွတ္");
            listHeader.add("ေဆးစညြန္းစာေရးသည့္ ဆရာ၀န္အမည္၊ ေဆးကုုသခြင့္လိုုင္စင္၊ လက္မွတ္အမွတ္");
            listHeader.add("လက္ က်န္ ပမာက");

            List<String> listField = new ArrayList();
            listField.add("tran_date");
            listField.add("");
            listField.add("");
            listField.add("op_qty");
            listField.add("out_ttl");
            listField.add("vou_no");
            listField.add("nric");
            listField.add("pt_name1");
            listField.add("doctor_name");
            listField.add("closing");

            HashMap<String, POIUtil.FormatType> hmType = new HashMap();
            hmType.put("tran_date", POIUtil.FormatType.TEXT);
            hmType.put("op_qty", POIUtil.FormatType.INTEGER);
            hmType.put("out_ttl", POIUtil.FormatType.INTEGER);
            hmType.put("vou_no", POIUtil.FormatType.TEXT);
            hmType.put("nric", POIUtil.FormatType.TEXT);
            hmType.put("pt_name1", POIUtil.FormatType.TEXT);
            hmType.put("doctor_name", POIUtil.FormatType.TEXT);
            hmType.put("closing", POIUtil.FormatType.INTEGER);

            ResultSet rs = dao.execSQL(strSql);
            POIUtil.genExcelFileForControlDrugForm3WS(listHeader, listField, hmType, rs, fileName);
        } catch (Exception ex) {
            log.error("butExcelActionPerformed : " + ex.getMessage());
        }
    }
}
