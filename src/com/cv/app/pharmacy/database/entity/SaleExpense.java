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
@Table(name = "sale_expense")
public class SaleExpense implements java.io.Serializable {

    private String saleExpenseId;
    private ExpenseType expType;
    private Double expAmount;
    private Date createdDate;
    private Integer uniqueId;
    private Date expenseDate;
    private Double expenseIn;
    private String vouNo;
    
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

    @Column(name = "expense_amt")
    public Double getExpAmount() {
        return expAmount;
    }

    public void setExpAmount(Double expAmount) {
        this.expAmount = expAmount;
    }

    @ManyToOne
    @JoinColumn(name = "expense_type")
    public ExpenseType getExpType() {
        return expType;
    }

    public void setExpType(ExpenseType expType) {
        this.expType = expType;
    }

    @Id
    @Column(name = "sale_expense_id", unique = true, nullable = false)
    public String getSaleExpenseId() {
        return saleExpenseId;
    }

    public void setSaleExpenseId(String saleExpenseId) {
        this.saleExpenseId = saleExpenseId;
    }

    @Column(name = "unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name = "expense_date")
    @Temporal(TemporalType.TIMESTAMP)
    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    @Column(name="expense_amt_in")
    public Double getExpenseIn() {
        return expenseIn;
    }

    public void setExpenseIn(Double expenseIn) {
        this.expenseIn = expenseIn;
    }
    
    @Column(name="vou_no", length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }
}
