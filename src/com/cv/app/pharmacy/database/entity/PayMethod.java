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
 * @author winswe
 */
@Entity
@Table(name = "pay_method")
public class PayMethod implements java.io.Serializable {

    private Integer methodId;
    private String methodDesp;
    private Date updatedDate;
    private String groupCode;
    private Integer factor;
    private Double allowAmt;

    @Column(name = "method_desc", unique = true, length = 50)
    public String getMethodDesp() {
        return methodDesp;
    }

    public void setMethodDesp(String methodDesp) {
        this.methodDesp = methodDesp;
    }

    @Id
    @Column(name = "method_id", unique = true, nullable = false)
    public Integer getMethodId() {
        return methodId;
    }

    public void setMethodId(Integer methodId) {
        this.methodId = methodId;
    }

    @Override
    public String toString() {
        return methodDesp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Column(name="group_code")
    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    @Column(name="factor")
    public Integer getFactor() {
        return factor;
    }

    public void setFactor(Integer factor) {
        this.factor = factor;
    }

    @Column(name="allow_amt")
    public Double getAllowAmt() {
        return allowAmt;
    }

    public void setAllowAmt(Double allowAmt) {
        this.allowAmt = allowAmt;
    }
}
