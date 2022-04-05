/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.Township;
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
 * Patient class is patient information. Sharing "trader" table with Supplier,
 * Customer and Trader class. Database table name is trader.
 */
@Entity
@Table(name = "patient_detail")
public class Patient implements java.io.Serializable {

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
    private String patientName;
    private String address;
    private String contactNo;
    private String createdBy;
    private String admissionNo;
    private Integer age;
    private Integer month;
    private Integer day;
    private Township township;
    private CustomerGroup ptType;
    private String otId;

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

    @Column(name = "patient_name", length = 100)
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Column(name = "address", length = 250)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "contactno", length = 50)
    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    @Column(name = "created_by")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "admission_no", length = 15)
    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    @Column(name = "age")
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Column(name = "month")

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @Column(name = "day")

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    @ManyToOne
    @JoinColumn(name = "township_id")
    public Township getTownship() {
        return township;
    }

    public void setTownship(Township township) {
        this.township = township;
    }

    @ManyToOne
    @JoinColumn(name = "pt_type")
    public CustomerGroup getPtType() {
        return ptType;
    }

    public void setPtType(CustomerGroup ptType) {
        this.ptType = ptType;
    }

    @Column(name = "ot_id", length = 15)
    public String getOtId() {
        return otId;
    }

    public void setOtId(String otId) {
        this.otId = otId;
    }
}
