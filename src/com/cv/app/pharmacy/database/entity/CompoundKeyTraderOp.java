/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author WSwe
 */
@Embeddable
public class CompoundKeyTraderOp implements Serializable{
    private Trader trader;
    private Date opDate;
    private Currency currency;
    private String desp;

    public CompoundKeyTraderOp(){
        
    }
    
    public CompoundKeyTraderOp(Trader trader){
        this.trader = trader;
    }
    
    @ManyToOne
    @JoinColumn(name="trader_id")
    public Trader getTrader() {
        return trader;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="op_date")
    public Date getOpDate() {
        return opDate;
    }

    public void setOpDate(Date opDate) {
        this.opDate = opDate;
    }

    @ManyToOne
    @JoinColumn(name="currency")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Column(name="desp", length=25)
    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }
}
