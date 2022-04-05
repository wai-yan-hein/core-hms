/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.view;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author wai
 */
@Entity
@Table(name = "v_opd_booking")
public class VBooking implements Serializable {

    private Integer bookingId;
    private Date bkDate;
    private String regNo;
    private String patientName;
    private String doctorId;
    private String doctorName;
    private String bkPhone;
    private Integer bkSerialNo;
    private Boolean bkActive;

    @Id
    @Column(name = "bk_id", unique = true, nullable = false, length = 15)
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

    @Column(name = "doctor_id", length = 15)

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @Column(name = "doctor_name", length = 100)

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    @Column(name = "bk_ser_no", length = 10)
    public Integer getBkSerialNo() {
        return bkSerialNo;
    }

    public void setBkSerialNo(Integer bkSerialNo) {
        this.bkSerialNo = bkSerialNo;
    }

}
