/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import com.cv.app.pharmacy.database.entity.ExpenseType;
import com.cv.app.pharmacy.database.entity.Location;
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
@Table(name = "v_gen_expense")
public class VGenExpense implements java.io.Serializable {

    private Long expId;
    private Double expAmt;
    private String desp;
    private Date expnDate;
    private String remark;
    private Integer sessionId;
    private ExpenseType expType;
    private String tranType;
    private Double drAmt;
    private Location location;
    private String expenseOption;
    private String vouNo;
    private Boolean deleted;
    private String drId;
    private Boolean recLock;
    private Date lockDT;
    private Date updatedDate;
    private Boolean upp;

    @Id
    @Column(name = "expense_id")
    public Long getExpId() {
        return expId;
    }

    public void setExpId(Long expId) {
        this.expId = expId;
    }

    @Column(name = "exp_amount")
    public Double getExpAmt() {
        return expAmt;
    }

    public void setExpAmt(Double expAmt) {
        this.expAmt = expAmt;
    }

    @Column(name = "desp")
    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    @Column(name = "exp_date")
    @Temporal(TemporalType.DATE)
    public Date getExpnDate() {
        return expnDate;
    }

    public void setExpnDate(Date expnDate) {
        this.expnDate = expnDate;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name = "session_id")
    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    @ManyToOne
    @JoinColumn(name = "exp_type")
    public ExpenseType getExpType() {
        return expType;
    }

    public void setExpType(ExpenseType expType) {
        this.expType = expType;
    }

    @Column(name = "tran_type")
    public String getTranType() {
        return tranType;
    }

    public void setTranType(String tranType) {
        this.tranType = tranType;
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

    @Column(name = "expense_option")
    public String getExpenseOption() {
        return expenseOption;
    }

    public void setExpenseOption(String expenseOption) {
        this.expenseOption = expenseOption;
    }

    @Column(name = "vou_no")
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name = "deleted")
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "doctor_id")
    public String getDrId() {
        return drId;
    }

    public void setDrId(String drId) {
        this.drId = drId;
    }

    @Column(name = "rec_lock")
    public Boolean getRecLock() {
        if (recLock == null) {
            return false;
        } else {
            return recLock;
        }
    }

    public void setRecLock(Boolean recLock) {
        this.recLock = recLock;
    }

    @Column(name = "lock_dt")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getLockDT() {
        return lockDT;
    }

    public void setLockDT(Date lockDT) {
        this.lockDT = lockDT;
    }

    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Column(name = "upp")
    public Boolean getUpp() {
        return upp;
    }

    public void setUpp(Boolean upp) {
        this.upp = upp;
    }
}
