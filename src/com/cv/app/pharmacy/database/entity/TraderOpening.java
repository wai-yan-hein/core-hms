/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="trader_op")
public class TraderOpening implements java.io.Serializable{
    private CompoundKeyTraderOp key;
    private Double amount;

    public TraderOpening(){}
    
    public TraderOpening(CompoundKeyTraderOp key){
        this.key = key;
    }
    
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
}
