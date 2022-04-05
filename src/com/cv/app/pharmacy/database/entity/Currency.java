/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="currency")
public class Currency implements java.io.Serializable{
    private String currencyCode;
    private String currencyName;
    private String currencySymbol;
    private String currencyAccId;
    
    public Currency(){}
    
    public Currency(String currencyCode, String currencyName){
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
    }
    
    @Id
    @Column(name="cur_code", unique=true, nullable=false)
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Column(name="cur_name", unique=true, nullable=false)
    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    @Column(name="cur_symbol", nullable=true)
    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }
    
    @Override
    public String toString(){
        return currencyName;
    }

    @Column(name="acc_id")
    public String getCurrencyAccId() {
        return currencyAccId;
    }

    public void setCurrencyAccId(String currencyAccId) {
        this.currencyAccId = currencyAccId;
    }
    

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.currencyCode);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Currency other = (Currency) obj;
        if (!Objects.equals(this.currencyCode, other.currencyCode)) {
            return false;
        }
        return true;
    }
}
