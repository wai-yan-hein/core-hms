/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "payment_his")
public class TraderPayHis implements java.io.Serializable {

    private Integer paymentId;
    private Trader trader;
    private Date payDate;
    private Location location;
    private Currency currency; //Pay currency
    private Double exRate;
    private Double paidAmtC;
    private Double paidAmtP;
    private String remark;
    private boolean deleted;
    private Date createdDate;
    private Date updatedDate;
    private Appuser createdBy;
    private Appuser updateBy;
    private Integer machineId;
    private List<PaymentVou> listDetail;
    private String payOption;
    private String saleVou;
    private Integer sessionId;
    private Currency parentCurr;
    private Date payDt;
    private Double discount;
    private String intgUpdStatus;
    private TraderPayAccount payAccount;
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "payment_id", unique = true, nullable = false)
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    @ManyToOne
    @JoinColumn(name = "trader_id")
    public Trader getTrader() {
        return trader;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "pay_date")
    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    @ManyToOne
    @JoinColumn(name = "location_id")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @ManyToOne
    @JoinColumn(name = "currency_id")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Column(name = "exchange_rate")
    public Double getExRate() {
        return exRate;
    }

    public void setExRate(Double exRate) {
        this.exRate = exRate;
    }

    @Column(name = "paid_amtc")
    public Double getPaidAmtC() {
        return paidAmtC;
    }

    public void setPaidAmtC(Double paidAmtC) {
        this.paidAmtC = paidAmtC;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "deleted")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "created_date", insertable = false, updatable = false,
            columnDefinition = "timestamp default current_timestamp")
    @org.hibernate.annotations.Generated(value = GenerationTime.INSERT)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @ManyToOne
    @JoinColumn(name = "created_by")
    public Appuser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Appuser createdBy) {
        this.createdBy = createdBy;
    }

    @ManyToOne
    @JoinColumn(name = "updated_by")
    public Appuser getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Appuser updateBy) {
        this.updateBy = updateBy;
    }

    @Column(name = "machine_id")
    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "pay_his_join", joinColumns = {
        @JoinColumn(name = "payment_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "tran_id")})
    public List<PaymentVou> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<PaymentVou> listDetail) {
        this.listDetail = listDetail;
    }

    @Column(name = "pay_option", nullable = false)
    public String getPayOption() {
        return payOption;
    }

    public void setPayOption(String payOption) {
        this.payOption = payOption;
    }

    @Column(name = "paid_amtp")
    public Double getPaidAmtP() {
        return paidAmtP;
    }

    public void setPaidAmtP(Double paidAmtP) {
        this.paidAmtP = paidAmtP;
    }

    @Column(name = "sale_vou")
    public String getSaleVou() {
        return saleVou;
    }

    public void setSaleVou(String saleVou) {
        this.saleVou = saleVou;
    }

    @Column(name = "session_id")
    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    @ManyToOne
    @JoinColumn(name = "parent_curr_id", nullable = false)
    public Currency getParentCurr() {
        return parentCurr;
    }

    public void setParentCurr(Currency parentCurr) {
        this.parentCurr = parentCurr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "pay_dt")
    public Date getPayDt() {
        return payDt;
    }

    public void setPayDt(Date payDt) {
        this.payDt = payDt;
    }

    @Column(name = "discount")
    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    @Column(name = "intg_upd_status")
    public String getIntgUpdStatus() {
        return intgUpdStatus;
    }

    public void setIntgUpdStatus(String intgUpdStatus) {
        this.intgUpdStatus = intgUpdStatus;
    }

    @ManyToOne
    @JoinColumn(name = "pay_id")
    public TraderPayAccount getPayAccount() {
        return payAccount;
    }

    public void setPayAccount(TraderPayAccount payAccount) {
        this.payAccount = payAccount;
    }

}
