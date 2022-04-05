/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "patient_detail")
public class PatientDetail implements java.io.Serializable {

    private String regNo;
    private Date regDate;
    private Date dob;
    private Gender sex;
    private String fatherName;
    private String nirc;
    private City city;
    private String nationality;
    private String religion;
    private Doctor doctor;

    @Id
    @Column(name = "reg_no", unique = true, nullable = false, length = 15)
    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    @Column(name = "reg_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getRegDate() {
        return regDate;
    }

    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

    @Column(name = "dob")
    @Temporal(TemporalType.DATE)
    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    @ManyToOne
    @JoinColumn(name = "sex")
    public Gender getSex() {
        return sex;
    }

    public void setSex(Gender sex) {
        this.sex = sex;
    }

    @Column(name = "father_name", length = 100)
    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    @Column(name = "nirc", length = 100)
    public String getNirc() {
        return nirc;
    }

    public void setNirc(String nirc) {
        this.nirc = nirc;
    }

    @ManyToOne
    @JoinColumn(name = "city_id")
    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Column(name = "nationality", length = 45)
    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    @Column(name = "religion", length = 45)
    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

}
