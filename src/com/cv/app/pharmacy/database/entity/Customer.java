/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.*;

/**
 *
 * Customer class. Sharing "trader" table with Supplier, Patient and Trader class.
 * Database table name is trader.
 */
@Entity
@Table(name="trader")
@DiscriminatorValue("C")
public class Customer extends Trader implements java.io.Serializable{
    private Integer creditLimit;
    private Integer creditDays;
    private String parent;
    
    public Customer(){
        super();
    }

    @Column(name = "credit_days", nullable = true)
    public Integer getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Integer creditDays) {
        this.creditDays = creditDays;
    }

    @Column(name = "credit_limit", nullable = true)
    public Integer getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Integer creditLimit) {
        this.creditLimit = creditLimit;
    }

    @Column(name = "parent", nullable = true, length=15)
    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
    
    @Transient
    public int getCusTypeId(){
      return super.getTypeId().getTypeId();
    }
    
    @Override
    public String toString(){
        return getTraderId();
    }
}
