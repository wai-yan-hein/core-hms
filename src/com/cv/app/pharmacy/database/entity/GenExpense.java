/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "gen_expense")
public class GenExpense implements java.io.Serializable {

    private Long geneId;
    private Date expDate;
    private String desp;
    private Double amount;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
    private String remark;
    private Integer session;
    private ExpenseType expType;
    private Double drAmt;
    private Location location;
    private String vouNo;
    private String sourceAccId;
    private String accId;
    private String deptId;
    private String paidFor;
    private String expenseOpotion;
    private String doctorId;
    private Boolean deleted;
    private String intgUpdStatus;
    private Boolean upp;
    private String currency;
    private Boolean recLock;
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "gene_id", unique = true, nullable = false)
    public Long getGeneId() {
        return geneId;
    }

    public void setGeneId(Long geneId) {
        this.geneId = geneId;
    }

    @Column(name = "exp_amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Column(name = "desp", length = 200)
    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    @Column(name = "exp_date", nullable = true)
    @Temporal(TemporalType.DATE)
    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    @Column(name = "created_by")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
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

    @Column(name = "updated_by")
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Column(name = "remark", length = 100)
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

    @ManyToOne
    @JoinColumn(name = "exp_type")
    public ExpenseType getExpType() {
        return expType;
    }

    public void setExpType(ExpenseType expType) {
        this.expType = expType;
    }

    @Column(name = "dr_amt")
    public Double getDrAmt() {
        return drAmt;
    }

    public void setDrAmt(Double drAmt) {
        this.drAmt = drAmt;
    }

    @ManyToOne
    @JoinColumn(name = "location_id")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Column(name = "vou_no", length = 15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name="source_acc_id", length=15)
    public String getSourceAccId() {
        return sourceAccId;
    }

    public void setSourceAccId(String sourceAccId) {
        this.sourceAccId = sourceAccId;
    }

    @Column(name="acc_id", length=15)
    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    @Column(name="dept_id", length=15)
    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    @Column(name="paid_for", length=15)
    public String getPaidFor() {
        return paidFor;
    }

    public void setPaidFor(String paidFor) {
        this.paidFor = paidFor;
    }

    @Column(name="expense_option", length=15)
    public String getExpenseOpotion() {
        return expenseOpotion;
    }

    public void setExpenseOpotion(String expenseOpotion) {
        this.expenseOpotion = expenseOpotion;
    }

    @Column(name="doctor_id", length=15)
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @Column(name="deleted")
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name="intg_upd_status", length=5)
    public String getIntgUpdStatus() {
        return intgUpdStatus;
    }

    public void setIntgUpdStatus(String intgUpdStatus) {
        this.intgUpdStatus = intgUpdStatus;
    }

    @Column(name="un_paid_status")
    public Boolean getUpp() {
        return upp;
    }

    public void setUpp(Boolean upp) {
        this.upp = upp;
    }

    @Column(name="currency_id", length=15)
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name="rec_lock")
    public Boolean getRecLock() {
        return recLock;
    }

    public void setRecLock(Boolean recLock) {
        this.recLock = recLock;
    }
}
