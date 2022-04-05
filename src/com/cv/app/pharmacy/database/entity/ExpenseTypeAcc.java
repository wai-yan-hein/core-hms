/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author ACER
 */
@Entity
@Table(name = "expense_type_acc")
public class ExpenseTypeAcc implements java.io.Serializable{
    private Integer id;
    private String sourceAccId;
    private String accId;
    private String useFor;
    private String remark;
    private String depdCode;
    private Integer expTypeId;
    private String unPaidAcc;
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="source_acc_id", length=15)
    public String getSourceAccId() {
        return sourceAccId;
    }

    public void setSourceAccId(String sourceAccId) {
        this.sourceAccId = sourceAccId;
    }

    @Column(name="acc_id", length=15)
    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    @Column(name="use_for", length=25)
    public String getUseFor() {
        return useFor;
    }

    public void setUseFor(String useFor) {
        this.useFor = useFor;
    }

    @Column(name="remark", length=250)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name="dept_code", length=15)
    public String getDepdCode() {
        return depdCode;
    }

    public void setDepdCode(String depdCode) {
        this.depdCode = depdCode;
    }

    @Column(name="exp_type_id")
    public Integer getExpTypeId() {
        return expTypeId;
    }

    public void setExpTypeId(Integer expTypeId) {
        this.expTypeId = expTypeId;
    }

    @Column(name="exp_acc_id", length=15)
    public String getUnPaidAcc() {
        return unPaidAcc;
    }

    public void setUnPaidAcc(String unPaidAcc) {
        this.unPaidAcc = unPaidAcc;
    }
}
