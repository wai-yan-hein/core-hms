/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * Patient class is patient information. Sharing "trader" table with Patient, 
 * Customer and Trader class.
 * Database table name is trader.
 */
@Entity
@Table(name="trader")
@DiscriminatorValue("S")
public class Supplier extends Trader implements java.io.Serializable{
    private String parent;
    private Integer creditDays;
    
    public Supplier(){}
    
     @Column(name = "credit_days", nullable = true)
    public Integer getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Integer creditDays) {
        this.creditDays = creditDays;
    }
    
    @Column(name = "parent", nullable = true, length=15)
    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
