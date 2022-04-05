/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="customer_group")
public class CustomerGroup implements java.io.Serializable{
    private String groupId;
    private String groupName;
    private String accountId;
    private String intgUpdStatus; //For integration update status
    private String reportName;
    private Date updatedDate;
    private String deptId;
    private Integer overDueVouCnt;
    private String useFor;
    
    public CustomerGroup(){}

    @Id
    @Column(name="group_id", unique=true, nullable=false, length=5)
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Column(name="group_name", nullable=false, unique=true)
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    @Override
    public String toString(){
        return groupName;
    }

    @Column(name="account_id", length=15)
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
    @Column(name="intg_upd_status", length=5)
    public String getIntgUpdStatus() {
        return intgUpdStatus;
    }

    public void setIntgUpdStatus(String intgUpdStatus) {
        this.intgUpdStatus = intgUpdStatus;
    }

    @Column(name="report_name", length=500)
    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="update_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Column(name="dept_id", length=15)
    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    @Column(name="over_due_vou_cnt")
    public Integer getOverDueVouCnt() {
        return overDueVouCnt;
    }

    public void setOverDueVouCnt(Integer overDueVouCnt) {
        this.overDueVouCnt = overDueVouCnt;
    }

    @Column(name="use_for", length=5)
    public String getUseFor() {
        return useFor;
    }

    public void setUseFor(String useFor) {
        this.useFor = useFor;
    }
}
