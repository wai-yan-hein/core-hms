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
@Table(name="data_lock_his")
public class DataLock implements java.io.Serializable {
    private Integer tranId;
    private Date lockDate;
    private String lockUserId;
    private Integer lockMachineId;
    private Date tranDate;
    
    @Id 
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="tran_id", unique=true, nullable=false)
    public Integer getTranId() {
        return tranId;
    }

    public void setTranId(Integer tranId) {
        this.tranId = tranId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="lock_date")
    public Date getLockDate() {
        return lockDate;
    }

    public void setLockDate(Date lockDate) {
        this.lockDate = lockDate;
    }

    @Column(name="lock_user", length=15)
    public String getLockUserId() {
        return lockUserId;
    }

    public void setLockUserId(String lockUserId) {
        this.lockUserId = lockUserId;
    }

    @Column(name="lock_machine")
    public Integer getLockMachineId() {
        return lockMachineId;
    }

    public void setLockMachineId(Integer lockMachineId) {
        this.lockMachineId = lockMachineId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="tran_date")
    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }
}
