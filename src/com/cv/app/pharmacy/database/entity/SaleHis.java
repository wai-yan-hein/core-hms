/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import com.cv.app.opd.database.entity.Doctor;
import com.cv.app.opd.database.entity.Patient;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "sale_his")
public class SaleHis implements java.io.Serializable {

    private Patient patientId;
    private String saleInvId;
    private Trader customerId;
    private Date saleDate;
    private Date dueDate;
    private PaymentType paymentTypeId;
    private Location locationId;
    private Boolean deleted;
    private Currency currencyId;
    private Double vouTotal;
    private Double paid;
    private Double discount;
    private Double balance;
    private Appuser createdBy;
    private Date createdDate;
    private Appuser updatedBy;
    private Date updatedDate;
    private String remark;
    private String remark1;
    private List<SaleDetailHis> saleDetailHis;
    private Integer session;
    private List<SaleExpense> expense;
    private VouStatus vouStatus;
    private Double expenseTotal;
    private Double discP;
    private Double taxAmt;
    private Double taxP;
    private Currency paidCurrency;
    private Double paidCurrencyAmt;
    private Double paidCurrencyExRate;
    private Double exRateP; //Exchange rate parent currency
    private List<SaleOutstand> listOuts;
    private Doctor doctor;
    private List<SaleWarranty> warrandy;
    private Double payAmt; //For refund calculation
    private Double refund; //For refund calculation
    private Double ttlExpenseIn; //For total expense in
    //For school
    private String regNo;
    private String stuNo;
    private String stuName;
    private String admissionNo;

    private String migId;
    private String ptType; //Patient Type
    private String otId;
    private Double paymentAmt; //For payment process
    private Boolean isOverdue;
    private Integer paymentId;
    private String visitId;
    private Boolean isPrinted;
    private Long exrId;
    private String btId; //Bill transfer id

    public SaleHis() {
    }

    @Column(name = "balance")
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @ManyToOne
    @JoinColumn(name = "currency_id")
    public Currency getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Currency currencyId) {
        this.currencyId = currencyId;
    }

    @ManyToOne
    @JoinColumn(name = "cus_id")
    public Trader getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Trader customerId) {
        this.customerId = customerId;
    }

    @Column(name = "deleted")
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "discount")
    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @ManyToOne
    @JoinColumn(name = "location_id")
    public Location getLocationId() {
        return locationId;
    }

    public void setLocationId(Location locationId) {
        this.locationId = locationId;
    }

    @Column(name = "paid_amount")
    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    @ManyToOne
    @JoinColumn(name = "payment_type_id")
    public PaymentType getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(PaymentType paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sale_date", nullable = false)
    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    @Id
    @Column(name = "sale_inv_id", unique = true, nullable = false, length = 15)
    public String getSaleInvId() {
        return saleInvId;
    }

    public void setSaleInvId(String saleInvId) {
        this.saleInvId = saleInvId;
    }

    @Column(name = "vou_total", nullable = false)
    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    @ManyToOne
    @JoinColumn(name = "created_by")
    public Appuser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Appuser createdBy) {
        this.createdBy = createdBy;
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

    @Column(name = "remark", length = 255)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "remark1", length = 225)
    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    /*@OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "sale_join", joinColumns = {
        @JoinColumn(name = "sale_inv_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "sale_detail_id")})
    @OrderBy("uniqueId")*/
    @Transient
    public List<SaleDetailHis> getSaleDetailHis() {
        return saleDetailHis;
    }

    public void setSaleDetailHis(List<SaleDetailHis> saleDetailHis) {
        this.saleDetailHis = saleDetailHis;
    }

    @Column(name = "session_id")
    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    /*@OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "sale_expense_join", joinColumns = {
        @JoinColumn(name = "sale_inv_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "sale_expense_id")})*/
    @Transient
    public List<SaleExpense> getExpense() {
        return expense;
    }

    public void setExpense(List<SaleExpense> expense) {
        this.expense = expense;
    }

    @ManyToOne
    @JoinColumn(name = "vou_status")
    public VouStatus getVouStatus() {
        return vouStatus;
    }

    public void setVouStatus(VouStatus vouStatus) {
        this.vouStatus = vouStatus;
    }

    @Column(name = "sale_exp_total")
    public Double getExpenseTotal() {
        return expenseTotal;
    }

    public void setExpenseTotal(Double expenseTotal) {
        this.expenseTotal = expenseTotal;
    }

    @Column(name = "disc_p")
    public Double getDiscP() {
        return discP;
    }

    public void setDiscP(Double discP) {
        this.discP = discP;
    }

    @Column(name = "tax_amt")
    public Double getTaxAmt() {
        return taxAmt;
    }

    public void setTaxAmt(Double taxAmt) {
        this.taxAmt = taxAmt;
    }

    @Column(name = "tax_p")
    public Double getTaxP() {
        return taxP;
    }

    public void setTaxP(Double taxP) {
        this.taxP = taxP;
    }

    @ManyToOne
    @JoinColumn(name = "paid_currency")
    public Currency getPaidCurrency() {
        return paidCurrency;
    }

    public void setPaidCurrency(Currency paidCurrency) {
        this.paidCurrency = paidCurrency;
    }

    @Column(name = "paid_currency_amt")
    public Double getPaidCurrencyAmt() {
        return paidCurrencyAmt;
    }

    public void setPaidCurrencyAmt(Double paidCurrencyAmt) {
        this.paidCurrencyAmt = paidCurrencyAmt;
    }

    @Column(name = "paid_currency_ex_rate")
    public Double getPaidCurrencyExRate() {
        return paidCurrencyExRate;
    }

    public void setPaidCurrencyExRate(Double paidCurrencyExRate) {
        this.paidCurrencyExRate = paidCurrencyExRate;
    }

    @Override
    public boolean equals(Object o) {
        boolean status = true;

        if (o instanceof SaleHis) {
            if (!saleInvId.equals(((SaleHis) o).getSaleInvId())) {
                status = false;
            }
        } else {
            status = false;
        }

        return status;
    }

    @Override
    public int hashCode() {
        return saleInvId.hashCode();
    }

    @Column(name = "exchange_rate_p")
    public Double getExRateP() {
        return exRateP;
    }

    public void setExRateP(Double exRateP) {
        this.exRateP = exRateP;
    }

    /*@OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "sale_outs_join", joinColumns = {
        @JoinColumn(name = "sale_inv_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "outstanding_id")})*/
    @Transient
    public List<SaleOutstand> getListOuts() {
        return listOuts;
    }

    public void setListOuts(List<SaleOutstand> listOuts) {
        this.listOuts = listOuts;
    }

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    /*@OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "sale_warr_join", joinColumns = {
        @JoinColumn(name = "sale_inv_id")},
            inverseJoinColumns = {
                @JoinColumn(name = "warranty_id")})*/
    @Transient
    public List<SaleWarranty> getWarrandy() {
        return warrandy;
    }

    public void setWarrandy(List<SaleWarranty> warrandy) {
        this.warrandy = warrandy;
    }

    @Column(name = "pay_amt")
    public Double getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(Double payAmt) {
        this.payAmt = payAmt;
    }

    @Column(name = "refund")
    public Double getRefund() {
        return refund;
    }

    public void setRefund(Double refund) {
        this.refund = refund;
    }

    @Column(name = "sale_exp_total_in")
    public Double getTtlExpenseIn() {
        return ttlExpenseIn;
    }

    public void setTtlExpenseIn(Double ttlExpenseIn) {
        this.ttlExpenseIn = ttlExpenseIn;
    }

    @Column(name = "stu_reg_no", length = 15)
    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    @Column(name = "stu_no", length = 25)
    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }

    @Column(name = "stu_name", length = 255)
    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    @ManyToOne
    @JoinColumn(name = "reg_no")
    public Patient getPatientId() {
        return patientId;
    }

    public void setPatientId(Patient patientId) {
        this.patientId = patientId;
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

    @Column(name = "patient_type", length = 15)
    public String getPtType() {
        return ptType;
    }

    public void setPtType(String ptType) {
        this.ptType = ptType;
    }

    @Column(name = "ot_id", length = 15)
    public String getOtId() {
        return otId;
    }

    public void setOtId(String otId) {
        this.otId = otId;
    }

    @Column(name = "payment_amt")
    public Double getPaymentAmt() {
        return paymentAmt;
    }

    public void setPaymentAmt(Double paymentAmt) {
        this.paymentAmt = paymentAmt;
    }

    @Column(name = "is_overdue")
    public Boolean getIsOverdue() {
        if (isOverdue == null) {
            return false;
        } else {
            return isOverdue;
        }
    }

    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }

    @Column(name = "payment_id")
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    @Column(name = "visit_id", length = 45)
    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    @Column(name = "is_printed")
    public Boolean getIsPrinted() {
        if (isPrinted == null) {
            return false;
        }
        return isPrinted;
    }

    public void setIsPrinted(Boolean isPrinted) {
        this.isPrinted = isPrinted;
    }

    @Column(name = "exr_id")
    public Long getExrId() {
        return exrId;
    }

    public void setExrId(Long exrId) {
        this.exrId = exrId;
    }

    @Column(name = "bt_id", length = 15)
    public String getBtId() {
        return btId;
    }

    public void setBtId(String btId) {
        this.btId = btId;
    }
}
