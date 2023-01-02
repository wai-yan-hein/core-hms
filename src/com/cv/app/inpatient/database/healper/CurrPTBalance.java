/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.healper;

import java.util.Objects;

/**
 *
 * @author ACER
 */
public class CurrPTBalance {
    private String regNo;
    private String admNo;
    private String ptName;
    private Double balance;
    private Double checkBalance;
    private boolean status = false;
    
    public CurrPTBalance(){}
    
    public CurrPTBalance(String regNo, String admNo, String ptName, Double balance) {
        this.regNo = regNo;
        this.admNo = admNo;
        this.ptName = ptName;
        this.balance = balance;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getAdmNo() {
        return admNo;
    }

    public void setAdmNo(String admNo) {
        this.admNo = admNo;
    }

    public String getPtName() {
        return ptName;
    }

    public void setPtName(String ptName) {
        this.ptName = ptName;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getCheckBalance() {
        return checkBalance;
    }

    public void setCheckBalance(Double checkBalance) {
        this.checkBalance = checkBalance;
    }
    
    public boolean isError(){
        return !Objects.equals(balance, checkBalance);
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
