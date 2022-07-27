/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "ot_group")
public class OTProcedureGroup implements java.io.Serializable {

    private Integer groupId;
    private String groupName;
    private Integer migId;
    private Integer sortOrder;
    private String opdAccCode;
    private String ipdAccCode;
    private String depCode;
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
    @Column(name = "group_id", unique = true, nullable = false)
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Column(name = "group_name", length = 50)
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return groupName;
    }

    @Column(name = "mig_id")
    public Integer getMigId() {
        return migId;
    }

    public void setMigId(Integer migId) {
        this.migId = migId;
    }

    @Column(name = "sort_order")
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Column(name = "opd_acc_code", length = 15)
    public String getOpdAccCode() {
        return opdAccCode;
    }

    public void setOpdAccCode(String opdAccCode) {
        this.opdAccCode = opdAccCode;
    }

    @Column(name = "srvf1_acc_id", length = 15)
    public String getSrvF1AccId() {
        return srvF1AccId;
    }

    @Column(name = "ipd_acc_code", length = 15)
    public String getIpdAccCode() {
        return ipdAccCode;
    }

    public void setIpdAccCode(String ipdAccCode) {
        this.ipdAccCode = ipdAccCode;
    }

    @Column(name = "dep_code", length = 15)
    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
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
