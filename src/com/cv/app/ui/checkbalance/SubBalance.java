/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cv.app.ui.checkbalance;

/**
 *
 * @author cv-svr
 */
public class SubBalance {
    private String currency;
    private Integer billType;
    private String billName;
    private Double balance;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getBillType() {
        return billType;
    }

    public void setBillType(Integer billType) {
        this.billType = billType;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
    
}
