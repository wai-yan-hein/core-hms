/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="tmp_stock_balance_exp")
public class StockBalance implements java.io.Serializable{
    private StockBalanceKey key;
    private Integer balance;
    private String qtyStr;
    
    @EmbeddedId
    public StockBalanceKey getKey() {
        return key;
    }

    public void setKey(StockBalanceKey key) {
        this.key = key;
    }

    @Column(name="bal_qty")
    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    @Column(name="qty_str")
    public String getQtyStr() {
        return qtyStr;
    }

    public void setQtyStr(String qtyStr) {
        this.qtyStr = qtyStr;
    }
}
