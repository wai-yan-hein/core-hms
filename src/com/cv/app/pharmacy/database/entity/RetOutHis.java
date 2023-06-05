/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "ret_out_his")
public class RetOutHis implements java.io.Serializable {

    private String retOutId;
    private Date retOutDate;
    private Trader customer;
    private PaymentType paymentType;
    private Location location;
    private String remark;
    private Appuser createdBy;
    private Date createdDate;
    private Appuser updatedBy;
    private Date updatedDate;
    private Boolean deleted;
    private Integer session;
    private Double vouTotal;
    private Double paid;
    private Double balance;
    private List<RetOutDetailHis> listDetail;
    private Currency currency;
    //For parent currency
    private Double exRateP;
    //=================================
    private String migId;
    private Long exrId;

    @Column(name = "balance")
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @ManyToOne
    @JoinColumn(name = "created_by")
    public Appuser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Appuser createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "created_date", insertable = false, updatable = false,
            columnDefinition = "timestamp default current_timestamp")
    @org.hibernate.annotations.Generated(value = GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @ManyToOne
    @JoinColumn(name = "cus_id")
    public Trader getCustomer() {
        return customer;
    }

    public void setCustomer(Trader customer) {
        this.customer = customer;
    }

    @Column(name = "deleted")
    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @ManyToOne
    @JoinColumn(name = "location")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Column(name = "paid")
    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    @ManyToOne
    @JoinColumn(name = "payment_type")
    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @Column(name = "remark", length = 25)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ret_out_date")
    public Date getRetOutDate() {
        return retOutDate;
    }

    public void setRetOutDate(Date retOutDate) {
        this.retOutDate = retOutDate;
    }

    @Id
    @Column(name = "ret_out_id", unique = true, nullable = false, length = 15)
    public String getRetOutId() {
        return retOutId;
    }

    public void setRetOutId(String retOutId) {
        this.retOutId = retOutId;
    }

    @Column(name = "session_id")
    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    @ManyToOne
    @JoinColumn(name = "updated_by")
    public Appuser getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Appuser updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Column(name = "vou_total")
    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    /*@OneToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "ret_out_join", joinColumns = { @JoinColumn(name = "ret_out_id") }, 
            inverseJoinColumns = { @JoinColumn(name = "ret_out_detail_id") })
    @OrderBy("uniqueId")*/
    @Transient
    public List<RetOutDetailHis> getListDetail() {
        return listDetail;
    }

    public void setListDetail(List<RetOutDetailHis> listDetail) {
        this.listDetail = listDetail;
    }

    @ManyToOne
    @JoinColumn(name = "currency")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Column(name = "exchange_rate_p")
    public Double getExRateP() {
        return exRateP;
    }

    public void setExRateP(Double exRateP) {
        this.exRateP = exRateP;
    }

    @Column(name = "mig_id", length = 25)
    public String getMigId() {
        return migId;
    }

    public void setMigId(String migId) {
        this.migId = migId;
    }

    @Column(name = "exr_id")
    public Long getExrId() {
        return exrId;
    }

    public void setExrId(Long exrId) {
        this.exrId = exrId;
    }
}
