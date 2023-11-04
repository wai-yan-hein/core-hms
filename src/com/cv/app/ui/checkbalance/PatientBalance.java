/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.checkbalance;

import java.util.List;

/**
 *
 * @author winswe
 */
public class PatientBalance {
    private String regNo;
    private String ptName;
    private Double rptBalance;
    private Double chkBalance;
    private Double adjAmount;
    private List<SubBalance> listSB;
    
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

    public Double getAdjAmount() {
        return adjAmount;
    }

    public void setAdjAmount(Double adjAmount) {
        this.adjAmount = adjAmount;
    }

    public List<SubBalance> getListSB() {
        return listSB;
    }

    public void setListSB(List<SubBalance> listSB) {
        this.listSB = listSB;
    }
}
