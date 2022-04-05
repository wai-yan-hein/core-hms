/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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

/**
 *
 * @author Hsu Pyae Sone
 */
@Entity
@Table(name = "exchange_rate_log")
public class ExchangeRateLog implements java.io.Serializable {
    private Long tranId;
    private Long exrId;
    private Double oldRate;
    private Double newRate;
    private Date logDate;
    private String userId;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tran_id", unique = true, nullable = false)
    public Long getTranId() {
        return tranId;
    }

    public void setTranId(Long tranId) {
        this.tranId = tranId;
    }

    @Column(name="exr_id")
    public Long getExrId() {
        return exrId;
    }

    public void setExrId(Long exrId) {
        this.exrId = exrId;
    }

    @Column(name="old_rate")
    public Double getOldRate() {
        return oldRate;
    }

    public void setOldRate(Double oldRate) {
        this.oldRate = oldRate;
    }

    @Column(name="new_rate")
    public Double getNewRate() {
        return newRate;
    }

    public void setNewRate(Double newRate) {
        this.newRate = newRate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="log_date")
    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    @Column(name="user_id", length=15)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
