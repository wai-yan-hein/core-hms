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
 * @author ACER
 */
@Entity
@Table(name="gen_expense_lock_his")
public class GenExpenseLockHis implements java.io.Serializable{
    private Long id;
    private Date lockTime;
    private String lockUser;
    private Double inAmt;
    private Double outAmt;
    private Integer lockMachine;
    private Date dataDate;
    private Integer ttlRecs;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "lock_time")
    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    @Column(name="lock_user", length=15)
    public String getLockUser() {
        return lockUser;
    }

    public void setLockUser(String lockUser) {
        this.lockUser = lockUser;
    }

    @Column(name="in_amt")
    public Double getInAmt() {
        return inAmt;
    }

    public void setInAmt(Double inAmt) {
        this.inAmt = inAmt;
    }

    @Column(name="out_amt")
    public Double getOutAmt() {
        return outAmt;
    }

    public void setOutAmt(Double outAmt) {
        this.outAmt = outAmt;
    }

    @Column(name="lock_machine")
    public Integer getLockMachine() {
        return lockMachine;
    }

    public void setLockMachine(Integer lockMachine) {
        this.lockMachine = lockMachine;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "data_date")
    public Date getDataDate() {
        return dataDate;
    }

    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }

    @Column(name="ttl_record")
    public Integer getTtlRecs() {
        return ttlRecs;
    }

    public void setTtlRecs(Integer ttlRecs) {
        this.ttlRecs = ttlRecs;
    }
    
}
