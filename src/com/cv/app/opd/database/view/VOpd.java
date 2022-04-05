/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.view;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "v_opd")
public class VOpd implements Serializable{
    private VOpdKey key;
    private Date opdDate;
    private String patientId;
    private String doctorId;
    private String currency;
    private Integer paymentId;
    private boolean deleted;
    private Double vouTotal;
    private Double discP;
    private Double discA;
    private Double taxP;
    private Double taxA;
    private Double paid;
    private Double vouBalance;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updateDate;
    private String paidCurrancy;
    private Double paidCurrAmt;
    private Double paidCurrExRate;
    private Double exRateToPCurr;
    private String remark;
    private Integer sessionId;
    private String patientName;
    private String donorName;
    private Integer qty;
    private Double price;
    private Integer feesVersionId;
    private Integer chargeType;
    private Double amount;
    private Integer readerStatus;
    private String doctorName;
    private String serviceCode;
    private String serviceName;
    private String catName;
    private String groupId;
    private String readerDrId;
    private String readerDrName;
    private Integer pathoId;
    private String pathologyName;
    private Integer techId;
    private String techName;
    private Integer techType;
    private Boolean print;
    private String admissionNo;
    private String referDrId;
    private String referDrName;
    private Boolean completeStatus;
    private Integer catId;
    private String xrayNo;
    private Integer xrayFlimCnt;
    private Integer xrayFlimCnt1;
    private Integer labMachineId;
    private String labMachineName;
    private Integer resultOrder;
    
    @EmbeddedId
    public VOpdKey getKey() {
        return key;
    }

    public void setKey(VOpdKey key) {
        this.key = key;
    }
    
    @Temporal(TemporalType.DATE)
    @Column(name="opd_date")
    public Date getOpdDate() {
        return opdDate;
    }

    public void setOpdDate(Date opdDate) {
        this.opdDate = opdDate;
    }

    @Column(name="patient_id")
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Column(name="doctor_id")
    public String getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @Column(name="currency_id")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name="payment_id")
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    @Column(name="deleted")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name="vou_total")
    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    @Column(name="disc_p")
    public Double getDiscP() {
        return discP;
    }

    public void setDiscP(Double discP) {
        this.discP = discP;
    }

    @Column(name="disc_a")
    public Double getDiscA() {
        return discA;
    }

    public void setDiscA(Double discA) {
        this.discA = discA;
    }

    @Column(name="tax_p")
    public Double getTaxP() {
        return taxP;
    }

    public void setTaxP(Double taxP) {
        this.taxP = taxP;
    }

    @Column(name="tax_a")
    public Double getTaxA() {
        return taxA;
    }

    public void setTaxA(Double taxA) {
        this.taxA = taxA;
    }

    @Column(name="paid")
    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    @Column(name="vou_balance")
    public Double getVouBalance() {
        return vouBalance;
    }

    public void setVouBalance(Double vouBalance) {
        this.vouBalance = vouBalance;
    }

    @Column(name="created_by")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="created_date")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name="updated_by")
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="updated_date")
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Column(name="paid_currency_id")
    public String getPaidCurrancy() {
        return paidCurrancy;
    }

    public void setPaidCurrancy(String paidCurrancy) {
        this.paidCurrancy = paidCurrancy;
    }

    @Column(name="paid_curr_amt")
    public Double getPaidCurrAmt() {
        return paidCurrAmt;
    }

    public void setPaidCurrAmt(Double paidCurrAmt) {
        this.paidCurrAmt = paidCurrAmt;
    }

    @Column(name="paid_curr_ex_rate")
    public Double getPaidCurrExRate() {
        return paidCurrExRate;
    }

    public void setPaidCurrExRate(Double paidCurrExRate) {
        this.paidCurrExRate = paidCurrExRate;
    }

    @Column(name="ex_rate_to_pcurr")
    public Double getExRateToPCurr() {
        return exRateToPCurr;
    }

    public void setExRateToPCurr(Double exRateToPCurr) {
        this.exRateToPCurr = exRateToPCurr;
    }

    @Column(name="remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name="session_id")
    public Integer getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    @Column(name="patient_name")
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Column(name="donor_name")
    public String getDonorName() {
        return donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    @Column(name="qty")
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    @Column(name="price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name="fees_version_id")
    public Integer getFeesVersionId() {
        return feesVersionId;
    }

    public void setFeesVersionId(Integer feesVersionId) {
        this.feesVersionId = feesVersionId;
    }

    @Column(name="charge_type")
    public Integer getChargeType() {
        return chargeType;
    }

    public void setChargeType(Integer chargeType) {
        this.chargeType = chargeType;
    }

    @Column(name="amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Column(name="reader_status")
    public Integer getReaderStatus() {
        return readerStatus;
    }

    public void setReaderStatus(Integer readerStatus) {
        this.readerStatus = readerStatus;
    }

    @Column(name="doctor_name")
    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    @Column(name="service_code")
    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    @Column(name="service_name")
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Column(name="cat_name")
    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    @Column(name="group_id")
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Column(name="reader_doctor_id")
    public String getReaderDrId() {
        return readerDrId;
    }

    public void setReaderDrId(String readerDrId) {
        this.readerDrId = readerDrId;
    }

    @Column(name="reader_doctor_name")
    public String getReaderDrName() {
        return readerDrName;
    }

    public void setReaderDrName(String readerDrName) {
        this.readerDrName = readerDrName;
    }

    @Column(name="patho_id")
    public Integer getPathoId() {
        return pathoId;
    }

    public void setPathoId(Integer pathoId) {
        this.pathoId = pathoId;
    }

    @Column(name="pathology_name")
    public String getPathologyName() {
        return pathologyName;
    }

    public void setPathologyName(String pathologyName) {
        this.pathologyName = pathologyName;
    }

    @Column(name="tech_id")
    public Integer getTechId() {
        return techId;
    }

    public void setTechId(Integer techId) {
        this.techId = techId;
    }

    @Column(name="tech_name")
    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    @Column(name="tech_type")
    public Integer getTechType() {
        return techType;
    }

    public void setTechType(Integer techType) {
        this.techType = techType;
    }

    @Transient
    public Boolean getPrint() {
        return print;
    }

    public void setPrint(Boolean print) {
        this.print = print;
    }

    @Column(name="admission_no")
    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    @Column(name="refer_doctor_id")
    public String getReferDrId() {
        return referDrId;
    }

    public void setReferDrId(String referDrId) {
        this.referDrId = referDrId;
    }

    @Column(name="refer_doctor_name")
    public String getReferDrName() {
        return referDrName;
    }

    public void setReferDrName(String referDrName) {
        this.referDrName = referDrName;
    }

    @Column(name="complete_status")
    public Boolean isCompleteStatus() {
        return completeStatus;
    }

    public void setCompleteStatus(Boolean completeStatus) {
        this.completeStatus = completeStatus;
    }

    @Column(name="cat_id")
    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    @Column(name="xray_no", length=50)
    public String getXrayNo() {
        return xrayNo;
    }

    public void setXrayNo(String xrayNo) {
        this.xrayNo = xrayNo;
    }

    @Column(name="xray_flim_cnt")
    public Integer getXrayFlimCnt() {
        return xrayFlimCnt;
    }

    public void setXrayFlimCnt(Integer xrayFlimCnt) {
        this.xrayFlimCnt = xrayFlimCnt;
    }

    @Column(name="xray_flim_cnt1")
    public Integer getXrayFlimCnt1() {
        return xrayFlimCnt1;
    }

    public void setXrayFlimCnt1(Integer xrayFlimCnt1) {
        this.xrayFlimCnt1 = xrayFlimCnt1;
    }

    @Column(name="lab_machine_id")
    public Integer getLabMachineId() {
        return labMachineId;
    }

    public void setLabMachineId(Integer labMachineId) {
        this.labMachineId = labMachineId;
    }

    @Column(name="lab_machine_name")
    public String getLabMachineName() {
        return labMachineName;
    }

    public void setLabMachineName(String labMachineName) {
        this.labMachineName = labMachineName;
    }

    @Column(name="result_order")
    public Integer getResultOrder() {
        return resultOrder;
    }

    public void setResultOrder(Integer resultOrder) {
        this.resultOrder = resultOrder;
    }
}
