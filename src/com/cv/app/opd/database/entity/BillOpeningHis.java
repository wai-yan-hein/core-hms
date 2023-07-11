/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cv.app.opd.database.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 *
 * @author cv-svr
 */
@Entity
@Table(name = "bill_opening_his")
public class BillOpeningHis implements Serializable{
    private String billId;
    private String regNo;
    private String admNo;
    private Boolean status;
    private Date billOPDate;
    private String openBy;
    private Date billCLDate;
    private String closeBy;
    private String ptName;
   
    @Id @Column(name = "bill_id", unique = true, nullable = false, length=15)
    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    @Column(name="reg_no", length=15)
    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    @Column(name="adm_no", length=15)
    public String getAdmNo() {
        return admNo;
    }

    public void setAdmNo(String admNo) {
        this.admNo = admNo;
    }

    @Column(name="op_cl_status")
    public Boolean getStatus() {
        if(status == null){
            return false;
        }
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Column(name="bill_op_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getBillOPDate() {
        return billOPDate;
    }

    public void setBillOPDate(Date billOPDate) {
        this.billOPDate = billOPDate;
    }

    @Column(name="open_by", length=15)
    public String getOpenBy() {
        return openBy;
    }

    public void setOpenBy(String openBy) {
        this.openBy = openBy;
    }

    @Column(name="bill_cl_date")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getBillCLDate() {
        return billCLDate;
    }

    public void setBillCLDate(Date billCLDate) {
        this.billCLDate = billCLDate;
    }

    @Column(name="close_by", length=15)
    public String getCloseBy() {
        return closeBy;
    }

    public void setCloseBy(String closeBy) {
        this.closeBy = closeBy;
    }

    @Transient
    public String getPtName() {
        return ptName;
    }

    public void setPtName(String ptName) {
        this.ptName = ptName;
    }
    
}
