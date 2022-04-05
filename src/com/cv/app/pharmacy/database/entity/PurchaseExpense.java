/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="purchase_expense")
public class PurchaseExpense implements java.io.Serializable{
    private String purchaseExpId;
    private ExpenseType expType;
    private Double expAmount;
    private Date createdDate;
    private Integer uniqueId;
    private Date expDate;
    private String vouNo;
    
    @Column(name = "created_date", insertable=false, updatable=false, 
            columnDefinition="timestamp default current_timestamp")
    @org.hibernate.annotations.Generated(value=GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name="expense_amt")
    public Double getExpAmount() {
        return expAmount;
    }

    public void setExpAmount(Double expAmount) {
        this.expAmount = expAmount;
    }

    @ManyToOne
    @JoinColumn(name="expense_type")
    public ExpenseType getExpType() {
        return expType;
    }

    public void setExpType(ExpenseType expType) {
        this.expType = expType;
    }

    @Id
    @Column(name="purchase_expense_id", unique=true, nullable=false, length=25)
    public String getPurchaseExpId() {
        return purchaseExpId;
    }

    public void setPurchaseExpId(String purchaseExpId) {
        this.purchaseExpId = purchaseExpId;
    }

    @Column(name="unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="exp_date")
    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }
    
    @Column(name="vou_no", length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }
}
