/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.checkbalance;

/**
 *
 * @author winswe
 */
public class PatientBalance {
    private String regNo;
    private String ptName;
    private Double rptBalance;
    private Double chkBalance;

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getPtName() {
        return ptName;
    }

    public void setPtName(String ptName) {
        this.ptName = ptName;
    }

    public Double getRptBalance() {
        return rptBalance;
    }

    public void setRptBalance(Double rptBalance) {
        this.rptBalance = rptBalance;
    }

    public Double getChkBalance() {
        return chkBalance;
    }

    public void setChkBalance(Double chkBalance) {
        this.chkBalance = chkBalance;
    }
}
