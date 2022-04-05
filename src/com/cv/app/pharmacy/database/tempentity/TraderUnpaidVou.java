/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name="tmp_trader_unpaid_vou")
public class TraderUnpaidVou implements Serializable{
    private Long tranId;
    private Date vouDate;
    private Date dueDate;
    private String vouNo;
    private String refNo;
    private String vouType;
    private Double vouTtl;
    private Double paid;
    private Double vouBalance;
    private String traderId;
    private String userId;
    private String currency;

    @Id
    @Column(name="tran_id")
    public Long getTranId() {
        return tranId;
    }

    public void setTranId(Long tranId) {
        this.tranId = tranId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="vou_date")
    public Date getVouDate() {
        return vouDate;
    }

    public void setVouDate(Date vouDate) {
        this.vouDate = vouDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="due_date")
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @Column(name="vou_no")
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name="ref_no")
    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    @Column(name="vou_type")
    public String getVouType() {
        return vouType;
    }

    public void setVouType(String vouType) {
        this.vouType = vouType;
    }

    @Column(name="vou_ttl")
    public Double getVouTtl() {
        return vouTtl;
    }

    public void setVouTtl(Double vouTtl) {
        this.vouTtl = vouTtl;
    }

    @Column(name="paid")
    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    @Column(name="vou_balance")
    public Double getVouBalance() {
        return vouBalance;
    }

    public void setVouBalance(Double vouBalance) {
        this.vouBalance = vouBalance;
    }

    @Column(name="trader_id")
    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    @Column(name="user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name="currency_id")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
