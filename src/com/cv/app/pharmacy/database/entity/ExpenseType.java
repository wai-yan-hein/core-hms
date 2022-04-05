/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;
/**
 *
 * @author WSwe
 */
@Entity
@Table(name="expense_type")
public class ExpenseType implements java.io.Serializable{
    private Integer expenseId;
    private String expenseName;
    private String accountCode;
    private String expenseOption;
    private String deptCode;
    private String sysCode;
    private Integer cusGroupoId;
    private Boolean needDr;
    
    @Column(name="account_code", length=15)
    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    @Column(name="expense_name", nullable=false, length=45, unique=true)
    public String getExpenseName() {
        return expenseName;
    }

    public void setExpenseName(String expenseName) {
        this.expenseName = expenseName;
    }
    
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="expense_type_id", unique=true, nullable=false)
    public Integer getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Integer expenseId) {
        this.expenseId = expenseId;
    }

    @Column(name="expense_option", length=15)
    public String getExpenseOption() {
        return expenseOption;
    }

    public void setExpenseOption(String expenseOption) {
        this.expenseOption = expenseOption;
    }
    
    @Override
    public String toString(){
        return expenseName;
    }

    @Column(name="dept_code", length=15)
    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    @Column(name="sys_code", length=15)
    public String getSysCode() {
        return sysCode;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
    }

    @Column(name="cus_group_id")
    public Integer getCusGroupoId() {
        return cusGroupoId;
    }

    public void setCusGroupoId(Integer cusGroupoId) {
        this.cusGroupoId = cusGroupoId;
    }

    @Column(name="need_dr")
    public Boolean getNeedDr() {
        if(needDr == null){
            return false;
        }
        return needDr;
    }

    public void setNeedDr(Boolean needDr) {
        this.needDr = needDr;
    }
}
