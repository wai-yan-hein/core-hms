/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.helper;

import java.util.Date;

/**
 *
 * @author lenovo
 */
public class VoucherPayment {

    private Date tranDate;
    private String vouNo;
    private String traderId;
    private String traderName;
    private String vouType;
    private Date dueDate;
    private Double vouTotal;
    private Double discount;
    private Double ttlPaid;
    private Double vouBalance;
    private int ttlOverdue;
    private boolean isFullPaid = false;
    private Double currentPaid;
    private Double currentDiscount;
    private Long payId;
    private String remark;
    private Date payDate;
    private String userId;
    private int listIndex;
    private Integer tranId;
    private String currency;
    private String traderCode;
    
    public VoucherPayment() {
    }

    public VoucherPayment(Date tranDate, String vouNo, String traderId,
            String traderName, String vouType, Date dueDate, Double vouTotal,
            Double discount, Double ttlPaid, Double vouBalance, int ttlOverdue,
            String currency,Date payDate, String traderCode) {
        this.tranDate = tranDate;
        this.vouNo = vouNo;
        this.traderId = traderId;
        this.traderName = traderName;
        this.vouType = vouType;
        this.dueDate = dueDate;
        this.vouTotal = vouTotal;
        this.discount = discount;
        this.ttlPaid = ttlPaid;
        this.vouBalance = vouBalance;
        this.ttlOverdue = ttlOverdue;
        this.currency = currency;
        this.payDate = payDate;
        this.traderCode = traderCode;
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

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    public String getVouType() {
        return vouType;
    }

    public void setVouType(String vouType) {
        this.vouType = vouType;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
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

    public Double getTtlPaid() {
        return ttlPaid;
    }

    public void setTtlPaid(Double ttlPaid) {
        this.ttlPaid = ttlPaid;
    }

    public Double getVouBalance() {
        return vouBalance;
    }

    public void setVouBalance(Double vouBalance) {
        this.vouBalance = vouBalance;
    }

    public int getTtlOverdue() {
        return ttlOverdue;
    }

    public void setTtlOverdue(int ttlOverdue) {
        this.ttlOverdue = ttlOverdue;
    }

    public boolean isIsFullPaid() {
        return isFullPaid;
    }

    public void setIsFullPaid(boolean isFullPaid) {
        this.isFullPaid = isFullPaid;
    }

    public Double getCurrentPaid() {
        return currentPaid;
    }

    public void setCurrentPaid(Double currentPaid) {
        this.currentPaid = currentPaid;
    }

    public Double getCurrentDiscount() {
        return currentDiscount;
    }

    public void setCurrentDiscount(Double currentDiscount) {
        this.currentDiscount = currentDiscount;
    }

    public Long getPayId() {
        return payId;
    }

    public void setPayId(Long payId) {
        this.payId = payId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getListIndex() {
        return listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }

    public Integer getTranId() {
        return tranId;
    }

    public void setTranId(Integer tranId) {
        this.tranId = tranId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTraderCode() {
        return traderCode;
    }

    public void setTraderCode(String traderCode) {
        this.traderCode = traderCode;
    }
}
