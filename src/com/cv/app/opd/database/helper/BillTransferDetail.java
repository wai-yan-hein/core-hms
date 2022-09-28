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
    
    public BillTransferDetail(Date tranDate, String regNo, String name, String status,
            String admissionNo, String strAge, Double amount) {
        this.tranDate = tranDate;
        this.regNo = regNo;
        this.name = name;
        this.status = status;
        this.admissionNo = admissionNo;
        this.strAge = strAge;
        this.amount = amount;
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

}
