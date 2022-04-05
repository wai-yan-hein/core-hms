/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.entity;

import com.cv.app.opd.database.entity.City;
import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Gender;
import com.cv.app.pharmacy.database.entity.CustomerGroup;
import com.cv.app.pharmacy.database.entity.Township;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Eitar
 */
@Entity
@Table(name = "admission")
public class Ams implements java.io.Serializable {

    private AdmissionKey key;
    private Date amsDate;
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
    private DCStatus dcStatus;
    private Date dcDateTime;
    private Township township;
    private CustomerGroup ptType;
    private BuildingStructure buildingStructure;
    private Integer diagnosis;
    private Integer ageRangeId;
    private Integer age;
    private Date createdDate;
    
    public Ams() {
        key = new AdmissionKey();
    }

    @EmbeddedId
    public AdmissionKey getKey() {
        return key;
    }

    public void setKey(AdmissionKey key) {
        this.key = key;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ams_date")
    public Date getAmsDate() {
        return amsDate;
    }

    public void setAmsDate(Date amsDate) {
        this.amsDate = amsDate;
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

    @ManyToOne
    @JoinColumn(name = "dc_status")
    public DCStatus getDcStatus() {
        return dcStatus;
    }

    public void setDcStatus(DCStatus dcStatus) {
        this.dcStatus = dcStatus;
    }

    @Column(name = "dc_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDcDateTime() {
        return dcDateTime;
    }

    public void setDcDateTime(Date dcDateTime) {
        this.dcDateTime = dcDateTime;
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

    @ManyToOne
    @JoinColumn(name = "building_structure_id")
    public BuildingStructure getBuildingStructure() {
        return buildingStructure;
    }

    public void setBuildingStructure(BuildingStructure buildingStructure) {
        this.buildingStructure = buildingStructure;
    }

    @Column(name="diagnosis_id")
    public Integer getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(Integer diagnosis) {
        this.diagnosis = diagnosis;
    }

    @Column(name="age_range_id")
    public Integer getAgeRangeId() {
        return ageRangeId;
    }

    public void setAgeRangeId(Integer ageRangeId) {
        this.ageRangeId = ageRangeId;
    }

    @Column(name="age")
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
