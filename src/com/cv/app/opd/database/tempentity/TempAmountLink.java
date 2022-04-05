/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.tempentity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author MyintMo
 */
@Entity
@Table(name="tmp_amount_link")
public class TempAmountLink implements java.io.Serializable{
    private TempAmountLinkKey key;
    private Double amount;
    private boolean printStatus;

    public TempAmountLink(){
        key = new TempAmountLinkKey();
    }
    
    @EmbeddedId
    public TempAmountLinkKey getKey() {
        return key;
    }

    public void setKey(TempAmountLinkKey key) {
        this.key = key;
    }

    @Column(name="amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Column(name="print_status")
    public boolean isPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(boolean printStatus) {
        this.printStatus = printStatus;
    }
}
