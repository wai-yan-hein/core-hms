/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import com.cv.app.emr.database.entity.AgeRange;
import com.cv.app.pharmacy.database.entity.Appuser;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.PaymentType;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "opd_his")
public class OPDHis implements java.io.Serializable {

    private String opdInvId;
    private Date invDate;
    private Patient patient;
    private Doctor doctor;
    private Currency currency;
    private PaymentType paymentType;
    private boolean deleted;
    private Double vouTotal;
    private Double discountP;
    private Double discountA;
    private Double taxP;
    private Double taxA;
    private Double paid;
    private Double vouBalance;
    private Appuser createdBy;
    private Date createdDate;
    private Appuser updatedBy;
    private Date updatedDate;
    private List<OPDDetailHis> listOPDDetailHis;
    private String remark;
    private Integer session;
    private String patientName;

    private Currency paidCurrnecy;
    private Double paidCurrAmount;
    private Double paidCurrExRate;
    private Double exRateToP; //Exchange rate to parent currency
    private String donorName;
    private String admissionNo;
    private String migId;
    private AgeRange ageRange;
    private String nos; //nature_of_specimen
    private String examRequired;
    private Integer age;
    private String otId;
    private String visitId;
    private Long pkgId;
    private String pkgName;
    private Double pkgPrice;
    private String btId; //Bill transfer id
    private Float emgPercent;
    private Integer ageM;
    private Integer ageD;

    @Id
    @Column(name = "opd_inv_id", unique = true, nullable = false, length = 15)
    public String getOpdInvId() {
        return opdInvId;
    }

    public void setOpdInvId(String opdInvId) {
        this.opdInvId = opdInvId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "opd_date", nullable = false)
    public Date getInvDate() {
        return invDate;
    }

    public void setInvDate(Date invDate) {
        this.invDate = invDate;
    }

    @ManyToOne
    @JoinColumn(name = "patient_id")
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @ManyToOne
    @JoinColumn(name = "currency_id")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @ManyToOne
    @JoinColumn(name = "payment_id")
    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @Column(name = "deleted")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "vou_total")
    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    @Column(name = "disc_p")
    public Double getDiscountP() {
        return discountP;
    }

    public void setDiscountP(Double discountP) {
        this.discountP = discountP;
    }

    @Column(name = "disc_a")
    public Double getDiscountA() {
        return discountA;
    }

    public void setDiscountA(Double discountA) {
        this.discountA = discountA;
    }

    @Column(name = "tax_p")
    public Double getTaxP() {
        return taxP;
    }

    public void setTaxP(Double taxP) {
        this.taxP = taxP;
    }

    @Column(name = "tax_a")
    public Double getTaxA() {
        return taxA;
    }

    public void setTaxA(Double taxA) {
        this.taxA = taxA;
    }

    @Column(name = "paid")
    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    @Column(name = "vou_balance")
    public Double getVouBalance() {
        return vouBalance;
    }

    public void setVouBalance(Double vouBalance) {
        this.vouBalance = vouBalance;
    }

    @ManyToOne
    @JoinColumn(name = "created_by")
    public Appuser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Appuser createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @ManyToOne
    @JoinColumn(name = "updated_by")
    public Appuser getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Appuser updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "updated_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @ManyToOne
    @JoinColumn(name = "paid_currency_id")
    public Currency getPaidCurrnecy() {
        return paidCurrnecy;
    }

    public void setPaidCurrnecy(Currency paidCurrnecy) {
        this.paidCurrnecy = paidCurrnecy;
    }

    @Column(name = "paid_curr_amt")
    public Double getPaidCurrAmount() {
        return paidCurrAmount;
    }

    public void setPaidCurrAmount(Double paidCurrAmount) {
        this.paidCurrAmount = paidCurrAmount;
    }

    @Column(name = "paid_curr_ex_rate")
    public Double getPaidCurrExRate() {
        return paidCurrExRate;
    }

    public void setPaidCurrExRate(Double paidCurrExRate) {
        this.paidCurrExRate = paidCurrExRate;
    }

    @Column(name = "ex_rate_to_pcurr")
    public Double getExRateToP() {
        return exRateToP;
    }

    public void setExRateToP(Double exRateToP) {
        this.exRateToP = exRateToP;
    }

    /*@OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "opd_join", joinColumns = {
        @JoinColumn(name = "opd_inv_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "opd_detail_id")})*/
    @Transient
    public List<OPDDetailHis> getListOPDDetailHis() {
        return listOPDDetailHis;
    }

    public void setListOPDDetailHis(List<OPDDetailHis> listOPDDetailHis) {
        this.listOPDDetailHis = listOPDDetailHis;
    }

    @Column(name = "remark", length = 50)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "session_id")
    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    @Column(name = "patient_name", length = 200)
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Column(name = "donor_name", length = 500)
    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    @Column(name = "admission_no", length = 15)
    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    @Column(name = "mig_id", length = 25)
    public String getMigId() {
        return migId;
    }

    public void setMigId(String migId) {
        this.migId = migId;
    }

    @ManyToOne
    @JoinColumn(name = "age_range")
    public AgeRange getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(AgeRange ageRange) {
        this.ageRange = ageRange;
    }

    @Column(name = "nature_of_specimen", length = 50)
    public String getNos() {
        return nos;
    }

    public void setNos(String nos) {
        this.nos = nos;
    }

    @Column(name = "examination_required", length = 50)
    public String getExamRequired() {
        return examRequired;
    }

    public void setExamRequired(String examRequired) {
        this.examRequired = examRequired;
    }

    @Column(name = "age")
    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Column(name = "ot_id", length = 15)
    public String getOtId() {
        return otId;
    }

    public void setOtId(String otId) {
        this.otId = otId;
    }

    @Column(name = "visit_id", length = 45)
    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    @Column(name = "package_id")
    public Long getPkgId() {
        return pkgId;
    }

    public void setPkgId(Long pkgId) {
        this.pkgId = pkgId;
    }

    @Column(name = "package_name", length = 500)
    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    @Column(name = "package_price")
    public Double getPkgPrice() {
        return pkgPrice;
    }

    public void setPkgPrice(Double pkgPrice) {
        this.pkgPrice = pkgPrice;
    }

    @Column(name = "bt_id", length = 15)
    public String getBtId() {
        return btId;
    }

    public void setBtId(String btId) {
        this.btId = btId;
    }

    @Column(name="emg_percent")
    public Float getEmgPercent() {
        return emgPercent;
    }

    public void setEmgPercent(Float emgPercent) {
        this.emgPercent = emgPercent;
    }

    @Column(name="agem")
    public Integer getAgeM() {
        return ageM;
    }

    public void setAgeM(Integer ageM) {
        this.ageM = ageM;
    }

    @Column(name="aged")
    public Integer getAgeD() {
        return ageD;
    }

    public void setAgeD(Integer ageD) {
        this.ageD = ageD;
    }
}
