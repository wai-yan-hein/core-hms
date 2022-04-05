/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.auditlog;

import java.util.Date;

/**
 *
 * @author winswe
 */
public class SaleAuditLog {
    private Long bkId;
    private Date vouDate;
    private String vouNo;
    private Double vouTotal;
    private Date tranDate;
    private String tranUser;

    public Long getBkId() {
        return bkId;
    }

    public void setBkId(Long bkId) {
        this.bkId = bkId;
    }

    public Date getVouDate() {
        return vouDate;
    }

    public void setVouDate(Date vouDate) {
        this.vouDate = vouDate;
    }

    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public String getTranUser() {
        return tranUser;
    }

    public void setTranUser(String tranUser) {
        this.tranUser = tranUser;
    }
}
