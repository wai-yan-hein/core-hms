/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
/**
 *
 * @author Thandar
 */
@Entity
@Table(name="doctor")
public class Doctor implements java.io.Serializable{
    private String doctorId;
    private String doctorName;
    private Gender genderId;
    private String nirc;
    private Speciality speciality;
    private Initial initialID;
    private String licenseNo;
    private boolean active;
    private String phoneNo;
    private Date updateDate;
    private List<DoctorFeesMapping> listFees;
    private String drType;
    private List<DoctorFeesMappingDC> listDFMDC;
    private List<DoctorFeesMappingOT> listDFMOT;
    
    @Id
    @Column(name = "doctor_id", unique = true, nullable = false, length = 15)
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @Column(name="doctor_name", unique=true, nullable=false,
            length=100)
    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    @Column(name="nirc",length=30)
    public String getNirc() {
        return nirc;
    }

    public void setNirc(String nirc) {
        this.nirc = nirc;
    }
   
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="speciality_id")
    public Speciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
    }
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="initial_id")    
    public Initial getInitialID() {
        return initialID;
    }   
    
    public void setInitialID(Initial initialID) {
        this.initialID = initialID;
    } 
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="gender_id")    
    public Gender getGenderId() {
        return genderId;
    }   
    
    public void setGenderId(Gender genderId) {
        this.genderId = genderId;
    } 
    
    @Override
    public String toString(){
        return doctorName;
    }

    @Column(name="license_no", length = 16)
    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    @Column(name="active")
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /*@OneToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "doctor_fee_join", joinColumns = { @JoinColumn(name = "doctor_id") }, 
            inverseJoinColumns = { @JoinColumn(name = "map_id") })*/
    @Transient
    public List<DoctorFeesMapping> getListFees() {
        return listFees;
    }

    public void setListFees(List<DoctorFeesMapping> listFees) {
        this.listFees = listFees;
    }

    @Column(name="phone", length=255)
    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_date")
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Column(name="dr_type", length=25)
    public String getDrType() {
        return drType;
    }

    public void setDrType(String drType) {
        this.drType = drType;
    }

    @Transient
    public List<DoctorFeesMappingDC> getListDFMDC() {
        return listDFMDC;
    }

    public void setListDFMDC(List<DoctorFeesMappingDC> listDFMDC) {
        this.listDFMDC = listDFMDC;
    }

    @Transient
    public List<DoctorFeesMappingOT> getListDFMOT() {
        return listDFMOT;
    }

    public void setListDFMOT(List<DoctorFeesMappingOT> listDFMOT) {
        this.listDFMOT = listDFMOT;
    }
}
