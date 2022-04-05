/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.helper;

import java.util.Date;

/**
 *
 * @author ACER
 */
public class OPDDrPayment {
    private Date tranDate;
    private String vouNo;
    private String regNo;
    private String ptName;
    private String admissionNo;
    private String serviceName;
    private Integer qty;
    private Double price;
    private String chargeType;
    private Double amount;
    private Double moFee;
    private Double staffFee;
    private Double techFee;
    private Double referFee;
    private Double readerFee;
    private Boolean paid;
    
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

    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getPrice() {
        return price;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
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

    public Double getMoFee() {
        return moFee;
    }

    public void setMoFee(Double moFee) {
        this.moFee = moFee;
    }

    public Double getStaffFee() {
        return staffFee;
    }

    public void setStaffFee(Double staffFee) {
        this.staffFee = staffFee;
    }

    public Double getTechFee() {
        return techFee;
    }

    public void setTechFee(Double techFee) {
        this.techFee = techFee;
    }

    public Double getReferFee() {
        return referFee;
    }

    public void setReferFee(Double referFee) {
        this.referFee = referFee;
    }

    public Double getReaderFee() {
        return readerFee;
    }

    public void setReaderFee(Double readerFee) {
        this.readerFee = readerFee;
    }

    public Boolean getPaid() {
        if(paid == null){
            return false;
        }
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
}
