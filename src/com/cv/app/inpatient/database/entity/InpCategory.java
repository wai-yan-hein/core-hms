/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "inp_category")
public class InpCategory implements java.io.Serializable {

    private Integer catId;
    private String catName;
    private Integer sortOrder;
    private Integer imgId;
    private String accountId;
    private String deptId;
    private String srvF1AccId;
    private String srvF2AccId;
    private String srvF3AccId;
    private String srvF4AccId;
    private String srvF5AccId;
    private String payableAccId;
    private String payableAccOpt;
    private String userCode;
    private boolean expense;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "cat_id", unique = true, nullable = false)
    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    @Column(name = "cat_name", unique = true, length = 50)
    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    @Column(name = "sort_order")
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return catName;
    }

    @Column(name = "mig_id")
    public Integer getImgId() {
        return imgId;
    }

    public void setImgId(Integer imgId) {
        this.imgId = imgId;
    }

    @Column(name = "account_id", length = 15)
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Column(name = "dep_code", length = 15)
    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    @Column(name = "srvf1_acc_id", length = 15)
    public String getSrvF1AccId() {
        return srvF1AccId;
    }

    public void setSrvF1AccId(String srvF1AccId) {
        this.srvF1AccId = srvF1AccId;
    }

    @Column(name = "srvf2_acc_id", length = 15)
    public String getSrvF2AccId() {
        return srvF2AccId;
    }

    public void setSrvF2AccId(String srvF2AccId) {
        this.srvF2AccId = srvF2AccId;
    }

    @Column(name = "srvf3_acc_id", length = 15)
    public String getSrvF3AccId() {
        return srvF3AccId;
    }

    public void setSrvF3AccId(String srvF3AccId) {
        this.srvF3AccId = srvF3AccId;
    }

    @Column(name = "srvf4_acc_id", length = 15)
    public String getSrvF4AccId() {
        return srvF4AccId;
    }

    public void setSrvF4AccId(String srvF4AccId) {
        this.srvF4AccId = srvF4AccId;
    }

    @Column(name = "srvf5_acc_id", length = 15)
    public String getSrvF5AccId() {
        return srvF5AccId;
    }

    public void setSrvF5AccId(String srvF5AccId) {
        this.srvF5AccId = srvF5AccId;
    }

    @Column(name = "payable_acc_id", length = 25)
    public String getPayableAccId() {
        return payableAccId;
    }

    public void setPayableAccId(String payableAccId) {
        this.payableAccId = payableAccId;
    }

    @Column(name = "payable_acc_opt", length = 20)
    public String getPayableAccOpt() {
        return payableAccOpt;
    }

    public void setPayableAccOpt(String payableAccOpt) {
        this.payableAccOpt = payableAccOpt;
    }

    @Column(name = "user_code", length = 15)
    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Column(name = "expense")
    public boolean isExpense() {
        return expense;
    }

    public void setExpense(boolean expense) {
        this.expense = expense;
    }

}
