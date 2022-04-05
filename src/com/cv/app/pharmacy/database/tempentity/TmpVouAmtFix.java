/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="tmp_vou_amt_fix")
public class TmpVouAmtFix implements java.io.Serializable{
    
    private TmpVouAmtFixKey key;
    private String vouType;
    private Date dueDate;
    private String refNo;
    private Double vouTotal;
    private Double balance;
    private Double paidAmount;
    private Double prvBalance;
    
    @EmbeddedId
    public TmpVouAmtFixKey getKey() {
        return key;
    }

    public void setKey(TmpVouAmtFixKey key) {
        this.key = key;
    }

    @Column(name="vou_type", length=25)
    public String getVouType() {
        return vouType;
    }

    public void setVouType(String vouType) {
        this.vouType = vouType;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="due_date")
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Column(name="ref_no", length=255)
    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    @Column(name="vou_total")
    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    @Column(name="balance")
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Column(name="paid_amt")
    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    @Transient
    public Double getPrvBalance() {
        return prvBalance;
    }

    public void setPrvBalance(Double prvBalance) {
        this.prvBalance = prvBalance;
    }
}
