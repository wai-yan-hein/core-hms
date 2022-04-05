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
public class CustomerWithParent extends Trader implements java.io.Serializable{
    private Integer creditLimit;
    private Integer creditDays;
    private ParentTrader parent;
    
    public CustomerWithParent(){
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

    @ManyToOne
    @JoinColumn(name="parent")
    public ParentTrader getParent() {
        return parent;
    }

    public void setParent(ParentTrader parent) {
        this.parent = parent;
    }
    
    @Transient
    public int getCusTypeId(){
      return super.getTypeId().getTypeId();
    }
}
