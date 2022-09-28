/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "payment_vou")
public class PaymentVou implements java.io.Serializable{
    private Integer tranId;
    private String vouNo;
    private Double vouBal;
    private Double vouPaid;
    private Double balance;
    private String vouType;
    private Date vouDate;
    private String refNo;
    private Double vouTotal; //Sale voucher total
    private Double paidAmount; //Sale voucher paid amount
    private Double discount; //Sale vourcher discount
    private Double vouBalance; //Sale voucher balance
    
    public PaymentVou() {}
    
    public PaymentVou(String vouNo, Double vouBal, String vouType,
            Date vouDate, String refNo, Double balance){
        this.vouNo = vouNo;
        this.vouBal = vouBal;
        this.vouType = vouType;
        this.vouDate = vouDate;
        this.refNo = refNo;
        this.balance = balance;
    }
    
    public PaymentVou(String vouNo, Double vouBal, String vouType,
            Date vouDate, String refNo, Double balance, Double vouTotal,
            Double vouPaid, Double vouDisc, Double vouBalance){
        this.vouNo = vouNo;
        this.vouBal = vouBal;
        this.vouType = vouType;
        this.vouDate = vouDate;
        this.refNo = refNo;
        this.balance = balance;
        
        this.vouTotal = vouTotal;
        this.paidAmount = vouPaid;
        this.discount = vouDisc;
        this.vouBalance = vouBalance;
    }
    
    @Id
    @GeneratedValue(strategy= IDENTITY)
    @Column(name="tran_id")
    public Integer getTranId() {
        return tranId;
    }

    public void setTranId(Integer tranId) {
        this.tranId = tranId;
    }

    @Column(name="vou_no")
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name="vou_bal")
    public Double getVouBal() {
        return vouBal;
    }

    public void setVouBal(Double vouBal) {
        this.vouBal = vouBal;
    }

    @Column(name="vou_paid")
    public Double getVouPaid() {
        return vouPaid;
    }

    public void setVouPaid(Double vouPaid) {
        this.vouPaid = vouPaid;
    }

    @Column(name="balance")
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Column(name="vou_type", nullable=false, length=15)
    public String getVouType() {
        return vouType;
    }

    public void setVouType(String vouType) {
        this.vouType = vouType;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="vou_date")
    public Date getVouDate() {
        return vouDate;
    }

    public void setVouDate(Date vouDate) {
        this.vouDate = vouDate;
    }

    @Column(name="ref_no")
    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    @Transient
    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    @Transient
    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    @Column(name="discount")
    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    @Transient
    public Double getVouBalance() {
        return vouBalance;
    }

    public void setVouBalance(Double vouBalance) {
        this.vouBalance = vouBalance;
    }
}
