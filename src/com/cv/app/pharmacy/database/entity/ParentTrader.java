/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.*;

/**
 *
 * Trader class is parent class of Customer, Patient and Supplier class. Sharing
 * "trader" table with Patient, Customer and Supplier class. Database table name
 * is trader.
 */
@Entity
@Table(name = "trader")
public class ParentTrader implements java.io.Serializable {

    private String traderId;
    private String traderName;
    
    @Id
    @Column(name = "trader_id", unique = true, nullable = false, length = 15)
    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    @Column(name = "trader_name", nullable = false, length = 50, unique = true)
    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }
}
