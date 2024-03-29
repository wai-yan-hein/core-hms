/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cv.app.opd.database.helper;

import java.util.Date;

/**
 *
 * @author cv-svr
 */
public class BillTransferDetail {

    private Date tranDate;
    private String regNo;
    private String name;
    private String status;
    private String admissionNo;
    private String strAge;
    private Double amount;
    private Integer payTypeId;
    private Double discount;
    private Double paid;
    private Double balance;
    private boolean pStatus;
    
    public BillTransferDetail(Date tranDate, String regNo, String name, String status,
            String admissionNo, String strAge, Double amount, Integer payTypeId) {
        this.tranDate = tranDate;
        this.regNo = regNo;
        this.name = name;
        this.status = status;
        this.admissionNo = admissionNo;
        this.strAge = strAge;
        this.amount = amount;
        this.payTypeId = payTypeId;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    public String getStrAge() {
        return strAge;
    }

    public void setStrAge(String strAge) {
        this.strAge = strAge;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getPayTypeId() {
        return payTypeId;
    }

    public void setPayTypeId(Integer payTypeId) {
        this.payTypeId = payTypeId;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public boolean ispStatus() {
        return pStatus;
    }

    public void setpStatus(boolean pStatus) {
        this.pStatus = pStatus;
    }
}
