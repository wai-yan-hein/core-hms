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
    
    @Override
    public boolean equals(Object o){
        if (o == null) {
            return false;
        }

        if (!(o instanceof CompoundKeyTraderOp)) {
            return false;
        }

        final CompoundKeyTraderOp other = (CompoundKeyTraderOp) o;
        if (!(trader.getTraderId().equals(other.getTrader().getTraderId()))) {
            return false;
        }

        if (opDate != other.getOpDate()) {
            return false;
        }

        if (!(currency.getCurrencyCode().equals(other.getCurrency().getCurrencyCode()))) {
            return false;
        }

        return true;
    }
}
