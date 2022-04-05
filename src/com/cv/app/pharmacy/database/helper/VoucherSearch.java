/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.helper;

import java.util.Date;

/**
 *
 * @author ACER
 */
public class VoucherSearch {
    private Date tranDate;
    private String invNo;
    private String refNo;
    private String cusNo;
    private String cusName;
    private String userName;
    private Double vouTotal;
    private Double discount;
    private Double balance;
    private Double paid;
    private String location;
    private Boolean isDeleted;
    private Integer dcStatus;
    private String saleMan;
    private String voucherChecker;
    private Boolean isPrinted;
    private Date tranDT;
    
    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public String getInvNo() {
        return invNo;
    }

    public void setInvNo(String invNo) {
        this.invNo = invNo;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public String getCusNo() {
        return cusNo;
    }

    public void setCusNo(String cusNo) {
        this.cusNo = cusNo;
    }

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getDcStatus() {
        return dcStatus;
    }

    public void setDcStatus(Integer dcStatus) {
        this.dcStatus = dcStatus;
    }

    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    public String getSaleMan() {
        return saleMan;
    }

    public void setSaleMan(String saleMan) {
        this.saleMan = saleMan;
    }

    public String getVoucherChecker() {
        return voucherChecker;
    }

    public void setVoucherChecker(String voucherChecker) {
        this.voucherChecker = voucherChecker;
    }

    public Boolean getIsPrinted() {
        return isPrinted;
    }

    public void setIsPrinted(Boolean isPrinted) {
        this.isPrinted = isPrinted;
    }

    public Date getTranDT() {
        return tranDT;
    }

    public void setTranDT(Date tranDT) {
        this.tranDT = tranDT;
    }
}
