/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.common;

import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.CompoundKey;
import com.cv.app.pharmacy.database.entity.VouId;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class RegNo {

    static Logger log = Logger.getLogger(RegNo.class.getName());
    private AbstractDataAccess dao;
    private String period;
    private int lastNo;
    private int vouTotalDigit = 5;
    private String regNo;
    private String vouType;

    public RegNo(AbstractDataAccess dao, String vouType) {
        this.dao = dao;
        this.vouType = vouType;
        this.period = DateUtil.getPeriodY(DateUtil.getTodayDateStr());
    }

    public String getRegNo() {
        getLastRegNo();
        generateRegNo();
        return regNo;
    }

    public String getRegNo(Date date) {
        period = DateUtil.getPeriodY(DateUtil.toDateStr(date, Global.dateFormat));
        getLastRegNo();
        generateRegNo();
        return regNo;
    }

    public void updateRegNo() {
        lastNo += 1;

        try {
            dao.open();
            VouId vouId = (VouId) dao.find(VouId.class,
                    new CompoundKey(vouType, vouType, period));
            vouId.setVouNo(lastNo);
            dao.save(vouId);
            dao.close();
        } catch (Exception ex) {
            log.error("updateRegNo : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Error in id generation. Please exit the program and try again", "Duplicate",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getLastRegNo() {
        try {
            dao.open();
            Object objVouNo = null;
            /*Object objVouNo = dao.getMax("vou_no", "vou_id",
                    "machine_name = '" + vouType + "' and vou_type = '"
                    + vouType + "' and vou_period = '" + period + "'");*/
            List<VouId> listVI = dao.findAllHSQL("select o from VouId o where o.compoundKey.machineName = '" 
                    + vouType + "' and o.compoundKey.vouType = '" + vouType + "' and o.compoundKey.period = '"
                    + period + "'"
            );
            if(listVI != null){
                if(!listVI.isEmpty()){
                    VouId vid = listVI.get(0);
                    objVouNo = vid.getVouNo();
                }
            }
            if (objVouNo == null) {
                //Need to insert new
                lastNo = 1;
                VouId vouId = new VouId(new CompoundKey(vouType,
                        vouType, period), lastNo);
                dao.save(vouId);
            } else {
                lastNo = Integer.parseInt(objVouNo.toString());
                if (lastNo == 0) {
                    log.error("getLastRegNo : ID Generate to " + lastNo);
                    JOptionPane.showMessageDialog(Util1.getParent(),
                            "Error in id generation. Please exit the program and try again", "ID Generation",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(-1);
                }
            }
            dao.close();
        } catch (Exception ex) {
            log.error("getLastRegNo : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Error in id generation. Please exit the program and try again", "Duplicate",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }

    private void generateRegNo() {
        String strVouNo = Integer.toString(lastNo);
        /*if (strVouNo.length() < vouTotalDigit) {
            int needToAdd = vouTotalDigit - strVouNo.length();

            for (int i = 0; i < needToAdd; i++) {
                strVouNo = "0" + strVouNo;
            }
        }*/

        //regNo = strVouNo + "/" + period;
        if (lastNo == 0) {
            log.error("generateRegNo : " + "Registeration number generate to zero.");
            JOptionPane.showMessageDialog(Util1.getParent(),
                    "Error in id generation. Please exit the program and try again", "Duplicate",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        } else {
            regNo = strVouNo + period;
        }
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
