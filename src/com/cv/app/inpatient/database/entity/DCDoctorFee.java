/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.entity;

import com.cv.app.opd.database.entity.Doctor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author admin
 */
@Entity
@Table(name = "dc_doctor_fee")
public class DCDoctorFee implements java.io.Serializable{
    private String drFeeId;
    private Doctor doctor;
    private Double drFee;
    private String dcDetailId;
    private Integer uniqueId;
    private String payId;
    
    @Id
    @Column(name="dr_fee_id", unique=true, nullable=false, length=25)
    public String getDrFeeId() {
        return drFeeId;
    }

    public void setDrFeeId(String drFeeId) {
        this.drFeeId = drFeeId;
    }

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @Column(name="dr_fee")
    public Double getDrFee() {
        return drFee;
    }

    public void setDrFee(Double drFee) {
        this.drFee = drFee;
    }

    @Column(name="dc_detail_id", length=25)
    public String getDcDetailId() {
        return dcDetailId;
    }

    public void setDcDetailId(String dcDetailId) {
        this.dcDetailId = dcDetailId;
    }

    @Column(name="unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name="pay_id", length=15)
    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId;
    }
}
