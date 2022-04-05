/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import com.cv.app.pharmacy.database.entity.CompoundKeyTraderOp;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *  
 * @author Eitar
 */

@Entity
@Table(name="v_trader_op_toacc")
public class VTraderOpToAcc implements java.io.Serializable{
    private CompoundKeyTraderOp key;
    
    private Double amount;
    private String accountId;
    private String discriminator;
    private String sourceAccId;
    private String deptCode;
    
    @EmbeddedId
    public CompoundKeyTraderOp getKey() {
        return key;
    }

    public void setKey(CompoundKeyTraderOp key) {
        this.key = key;
    }
    
    @Column(name="op_amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Column(name="account_id")
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Column(name="discriminator")
    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    @Column(name="source_acc_id")
    public String getSourceAccId() {
        return sourceAccId;
    }

    public void setSourceAccId(String sourceAccId) {
        this.sourceAccId = sourceAccId;
    }

    @Column(name="dept_id")
    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }
}
