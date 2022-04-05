/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.util;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.CompoundKey;
import com.cv.app.pharmacy.database.entity.VouId;
import com.cv.app.util.Util1;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class GenVouNoImpl implements GenVouNo {
    static Logger log = Logger.getLogger(GenVouNoImpl.class.getName());
    private AbstractDataAccess dao;
    private String vouType;
    private String period;
    private String vouNo;
    private int lastVouNo;
    private String machineNo;
    private int vouTotalDigit = 5;
    private int machineTtlDigit = 2;
    private boolean isError = false;
    
    public GenVouNoImpl(AbstractDataAccess dao, String vouType,
            String period) {
        this.dao = dao;
        this.vouType = vouType;
        this.period = period;
        machineNo = Global.machineId;

        getLastVouNo();
    }

    @Override
    public String getVouNo() {
        generateVouNo();

        return vouNo;
    }

    @Override
    public String getVouNo1(){
        generateVouNo1();
        return vouNo;
    }
    
    public void setPeriod(String period) {
        this.period = period;

        getLastVouNo();
    }

    private void generateVouNo() {
        //machineNo+lastVouNo+period
        if(isError){
            vouNo = "-";
            return;
        }
        if (machineNo.length() < machineTtlDigit) {
            int needToAdd = machineTtlDigit - machineNo.length();

            for (int i = 0; i < needToAdd; i++) {
                machineNo = "0" + machineNo;
            }
        }

        String strVouNo = Integer.toString(lastVouNo);
        if (strVouNo.length() < vouTotalDigit) {
            int needToAdd = vouTotalDigit - strVouNo.length();

            for (int i = 0; i < needToAdd; i++) {
                strVouNo = "0" + strVouNo;
            }
        }

        vouNo = machineNo + strVouNo + period;
    }

    private void generateVouNo1() {
        //machineNo+lastVouNo+period
        if (machineNo.length() < machineTtlDigit) {
            int needToAdd = machineTtlDigit - machineNo.length();

            for (int i = 0; i < needToAdd; i++) {
                machineNo = "0" + machineNo;
            }
        }

        String strVouNo = Integer.toString(lastVouNo);
        if (strVouNo.length() < vouTotalDigit) {
            int needToAdd = vouTotalDigit - strVouNo.length();

            for (int i = 0; i < needToAdd; i++) {
                strVouNo = "0" + strVouNo;
            }
        }

        vouNo = machineNo + strVouNo + "-" + period.substring(0, 2) + 
                "-" + period.substring(2, 6);
    }
    
    @Override
    public void updateVouNo() {
        lastVouNo += 1;

        try {
            dao.open();
            VouId vouId = (VouId) dao.find(VouId.class,
                    new CompoundKey(Global.machineName, vouType, period));
            vouId.setVouNo(lastVouNo);
            dao.save(vouId);
            dao.close();
        } catch (Exception ex) {
            log.error("updateVouNo : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            JOptionPane.showMessageDialog(Util1.getParent(),
                        "Error in id generation. Please exit the program and try again", "Update",
                        JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
            isError = true;
        }
    }

    private void getLastVouNo() {
        try {
            dao.open();
            Object objVouNo = dao.getMax("vou_no", "vou_id",
                    "machine_name = '" + Global.machineName + "' and vou_type = '"
                    + vouType + "' and vou_period = '" + period + "'");

            if (objVouNo == null) {
                //Need to insert new
                lastVouNo = 1;
                VouId vouId = new VouId(new CompoundKey(Global.machineName, vouType, period), lastVouNo);
                dao.save(vouId);
            } else {
                lastVouNo = Integer.parseInt(objVouNo.toString());
            }
            dao.close();
        } catch (Exception ex) {
            log.error("getLastVouNo : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
            JOptionPane.showMessageDialog(Util1.getParent(),
                        "Error in id generation. Please exit the program and try again", "ID Generation",
                        JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
            isError = true;
        }
    }

    public boolean isIsError() {
        return isError;
    }

    public void setIsError(boolean isError) {
        this.isError = isError;
    }
    
    public String getVouInfo(){
        return "Vou Type : " + vouType + " Period : " + period + " Vou No : "
                + vouNo + " Last Vou No : " + lastVouNo + " Machine No : "
                + machineNo;
    }
}
