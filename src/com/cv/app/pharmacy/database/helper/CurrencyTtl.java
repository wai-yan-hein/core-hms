/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.helper;

/**
 *
 * @author WSwe
 */
public class CurrencyTtl {
    private String currency;
    private Double ttlPaid;
    
    public CurrencyTtl(String currency, Double ttlPaid){
        this.currency = currency;
        this.ttlPaid = ttlPaid;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getTtlPaid() {
        return ttlPaid;
    }

    public void setTtlPaid(Double ttlPaid) {
        this.ttlPaid = ttlPaid;
    }
}
