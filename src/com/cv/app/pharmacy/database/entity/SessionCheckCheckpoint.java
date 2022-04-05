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
 * @author winswe
 */
@Entity
@Table(name = "session_check_checkpoint")
public class SessionCheckCheckpoint implements java.io.Serializable {
    
    private Long tranId;
    private String tranOption;
    private Date tranDate;
    private String tranInvId;
    private String regNo;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tran_id", unique = true, nullable = false)
    public Long getTranId() {
        return tranId;
    }

    public void setTranId(Long tranId) {
        this.tranId = tranId;
    }

    @Column(name="tran_option", length=25)
    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "tran_date")
    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    @Column(name="tran_inv_id", length=15)
    public String getTranInvId() {
        return tranInvId;
    }

    public void setTranInvId(String tranInvId) {
        this.tranInvId = tranInvId;
    }

    @Column(name="patient_id", length=15)
    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }
}
