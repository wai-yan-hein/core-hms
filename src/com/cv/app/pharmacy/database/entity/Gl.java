/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="gl")
public class Gl implements java.io.Serializable{
    private Long glId;
    private Date glDate;
    private String description;
    private String sourceAcId;
    private String accountId;
    private String toCurId;
    private String fromCurId;
    private Double exRate;
    private Double drAmt;
    private Double crAmt;
    private String reference;
    private String deptId;
    private String vouNo;
    private Long traderId; //For account
    private String chequeNo;
    private Integer compId;
    private Double gst;
    private Date createdDate;
    private Date modifyDate;
    private String modifyBy;
    private String createdBy;
    private String tranSource;
    private String bankCode;
    private String glVouNo; //For general voucher system id
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "gl_id", unique = true, nullable = false)
    public Long getGlId() {
        return glId;
    }

    public void setGlId(Long glId) {
        this.glId = glId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "gl_date")
    public Date getGlDate() {
        return glDate;
    }

    public void setGlDate(Date glDate) {
        this.glDate = glDate;
    }

    @Column(name="description", length=255)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "source_ac_id")
    public String getSourceAcId() {
        return sourceAcId;
    }

    public void setSourceAcId(String sourceAcId) {
        this.sourceAcId = sourceAcId;
    }

    @Column(name = "account_id")
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Column(name = "to_cur_id")
    public String getToCurId() {
        return toCurId;
    }

    public void setToCurId(String toCurId) {
        this.toCurId = toCurId;
    }

    @Column(name = "from_cur_id")
    public String getFromCurId() {
        return fromCurId;
    }

    public void setFromCurId(String fromCurId) {
        this.fromCurId = fromCurId;
    }

    @Column(name="ex_rate")
    public Double getExRate() {
        return exRate;
    }

    public void setExRate(Double exRate) {
        this.exRate = exRate;
    }

    @Column(name="dr_amt")
    public Double getDrAmt() {
        return drAmt;
    }

    public void setDrAmt(Double drAmt) {
        this.drAmt = drAmt;
    }

    @Column(name="cr_amt")
    public Double getCrAmt() {
        return crAmt;
    }

    public void setCrAmt(Double crAmt) {
        this.crAmt = crAmt;
    }

    @Column(name="reference", length=50)
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Column(name = "dept_id")
    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    @Column(name="voucher_no", length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name = "cv_id")
    public Long getTraderId() {
        return traderId;
    }

    public void setTraderId(Long traderId) {
        this.traderId = traderId;
    }
    
    @Column(name="cheque_no", length=20)
    public String getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }

    @Column(name = "comp_id")
    public Integer getCompId() {
        return compId;
    }

    public void setCompId(Integer compId) {
        this.compId = compId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modify_date")
    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    @Column(name="modify_by", length=15)
    public String getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }

    @Column(name="user_id", length=15)
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name="gst")
    public Double getGst() {
        return gst;
    }

    public void setGst(Double gst) {
        this.gst = gst;
    }
    
    @Column(name="tran_source", length=25)
    public String getTranSource() {
        return tranSource;
    }

    public void setTranSource(String tranSource) {
        this.tranSource = tranSource;
    }

    @Column(name = "bank_code")
    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    @Column(name="gl_vou_no", length=25)
    public String getGlVouNo() {
        return glVouNo;
    }

    public void setGlVouNo(String glVouNo) {
        this.glVouNo = glVouNo;
    }
}
