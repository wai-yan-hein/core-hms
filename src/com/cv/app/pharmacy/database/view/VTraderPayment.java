/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "v_trader_payment")
public class VTraderPayment implements java.io.Serializable{
    private Integer payId;
    private boolean deleted;
    private Double exRate;
    private Integer machineId;
    private Double paidAmtC;
    private Double paidAmtP;
    private Date payDate;
    private String payOpt;
    private String remark;
    private String saleInv;
    private String currency;
    private Integer location;
    private String traderId;
    private Integer session;
    private String currP;
    private String discm;
    private String traderName;
    private String createdBy;
    private String cusGroupId;
    private String stuNo;
    
    @Id
    @Column(name="payment_id")
    public Integer getPayId() {
        return payId;
    }

    public void setPayId(Integer payId) {
        this.payId = payId;
    }

    @Column(name="deleted")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name="exchange_rate")
    public Double getExRate() {
        return exRate;
    }

    public void setExRate(Double exRate) {
        this.exRate = exRate;
    }

    @Column(name="machine_id")
    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    @Column(name="paid_amtc")
    public Double getPaidAmtC() {
        return paidAmtC;
    }

    public void setPaidAmtC(Double paidAmtC) {
        this.paidAmtC = paidAmtC;
    }

    @Column(name="paid_amtp")
    public Double getPaidAmtP() {
        return paidAmtP;
    }

    public void setPaidAmtP(Double paidAmtP) {
        this.paidAmtP = paidAmtP;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="pay_date")
    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    @Column(name="pay_option")
    public String getPayOpt() {
        return payOpt;
    }

    public void setPayOpt(String payOpt) {
        this.payOpt = payOpt;
    }

    @Column(name="remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name="sale_vou")
    public String getSaleInv() {
        return saleInv;
    }

    public void setSaleInv(String saleInv) {
        this.saleInv = saleInv;
    }

    @Column(name="currency_id")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name="location_id")
    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    @Column(name="trader_id")
    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    @Column(name="session_id")
    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    @Column(name="parent_curr_id")
    public String getCurrP() {
        return currP;
    }

    public void setCurrP(String currP) {
        this.currP = currP;
    }

    @Column(name="discriminator")
    public String getDiscm() {
        return discm;
    }

    public void setDiscm(String discm) {
        this.discm = discm;
    }

    @Column(name="trader_name")
    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    @Column(name="created_by")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name="group_id")
    public String getCusGroupId() {
        return cusGroupId;
    }

    public void setCusGroupId(String cusGroupId) {
        this.cusGroupId = cusGroupId;
    }

    @Column(name="stu_no")
    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }
}
