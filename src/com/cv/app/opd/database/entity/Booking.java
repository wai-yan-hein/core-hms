/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author wai
 */
@Entity
@Table(name = "opd_booking")
public class Booking implements Serializable {

    private Integer bookingId;
    private Date bkDate;
    private String regNo;
    private String patientName;
    private String bkPhone;
    private Boolean bkActive;
    private Integer bkSerialNo;
    private Date createdDate;
    private String createdBy;
    private Doctor doctor;
    private String bkType;
    private Integer session;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "bk_id", unique = true, nullable = false)
    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    @Column(name = "bk_date")
    @Temporal(javax.persistence.TemporalType.DATE)
    public Date getBkDate() {
        return bkDate;
    }

    public void setBkDate(Date bkDate) {
        this.bkDate = bkDate;
    }

    @Column(name = "reg_no", length = 15)
    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    @Column(name = "patient_name", length = 100)

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Column(name = "bk_phone", length = 30)

    public String getBkPhone() {
        return bkPhone;
    }

    public void setBkPhone(String bkPhone) {
        this.bkPhone = bkPhone;
    }

    @Column(name = "bk_active", length = 2)

    public Boolean getBkActive() {
        return bkActive;
    }

    public void setBkActive(Boolean bkActive) {
        this.bkActive = bkActive;
    }

    @Column(name = "bk_ser_no", length = 10)

    public Integer getBkSerialNo() {
        return bkSerialNo;
    }

    public void setBkSerialNo(Integer bkSerialNo) {
        this.bkSerialNo = bkSerialNo;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "doctor_id")

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
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

    @Column(name = "created_by", length = 15)
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name="bk_type", length=15)
    public String getBkType() {
        return bkType;
    }

    public void setBkType(String bkType) {
        this.bkType = bkType;
    }

    @Column(name="session_id")
    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

}
