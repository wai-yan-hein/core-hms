/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "acc_setting")
public class AccSetting implements java.io.Serializable{
    private String accType;
    private String discAcc;
    private String payAcc;
    private String taxAcc;
    private String deptCode;
    private String sourceAcc;
    private String balanceAcc;
    private String sourceAccIPD;
    private String deptCodeIPD;
    
    @Id @Column(name="type", nullable=false, unique=true, length=15)
    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    @Column(name="dis_acc", length=15)
    public String getDiscAcc() {
        return discAcc;
    }

    public void setDiscAcc(String discAcc) {
        this.discAcc = discAcc;
    }

    @Column(name="pay_acc", length=15)
    public String getPayAcc() {
        return payAcc;
    }

    public void setPayAcc(String payAcc) {
        this.payAcc = payAcc;
    }

    @Column(name="tax_acc", length=15)
    public String getTaxAcc() {
        return taxAcc;
    }

    public void setTaxAcc(String taxAcc) {
        this.taxAcc = taxAcc;
    }

    @Column(name="dep_code", length=15)
    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    @Column(name="source_acc", length=15)
    public String getSourceAcc() {
        return sourceAcc;
    }

    public void setSourceAcc(String sourceAcc) {
        this.sourceAcc = sourceAcc;
    }

    @Column(name="bal_acc", length=15)
    public String getBalanceAcc() {
        return balanceAcc;
    }

    public void setBalanceAcc(String balanceAcc) {
        this.balanceAcc = balanceAcc;
    }

    @Column(name="source_acc_1", length=15)
    public String getSourceAccIPD() {
        return sourceAccIPD;
    }

    public void setSourceAccIPD(String sourceAccIPD) {
        this.sourceAccIPD = sourceAccIPD;
    }

    @Column(name="dep_code_ipd", length=15)
    public String getDeptCodeIPD() {
        return deptCodeIPD;
    }

    public void setDeptCodeIPD(String deptCodeIPD) {
        this.deptCodeIPD = deptCodeIPD;
    }
}
