/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.healper;

import java.util.Date;

/**
 *
 * @author ACER
 */
public class CurrPTBalanceTran {
    private String tranOption;
    private Date tranDate;
    private String vouNo;
    private String itemName;
    private String qty;
    private Double price;
    private Double amount;

    public CurrPTBalanceTran(){}

    public CurrPTBalanceTran(String tranOption, Date tranDate, String vouNo, 
            String itemName, String qty, Double price, Double amount) {
        this.tranOption = tranOption;
        this.tranDate = tranDate;
        this.vouNo = vouNo;
        this.itemName = itemName;
        this.qty = qty;
        this.price = price;
        this.amount = amount;
    }
    
    
    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
}
