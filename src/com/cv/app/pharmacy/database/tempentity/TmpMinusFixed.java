/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="tmp_minus_fixed")
public class TmpMinusFixed implements Serializable{
    private TmpMinusFixedKey key;
    private Integer balance;

    public TmpMinusFixed(){
        key = new TmpMinusFixedKey();
    }
    
    @EmbeddedId
    public TmpMinusFixedKey getKey() {
        return key;
    }

    public void setKey(TmpMinusFixedKey key) {
        this.key = key;
    }

    @Column(name="balance")
    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }
}
