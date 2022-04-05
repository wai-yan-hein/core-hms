/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import com.cv.app.pharmacy.database.entity.Trader;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author WSwe
 */
@Embeddable
public class TraderFilterKey implements Serializable{
    private Trader traderId;
    private String userId;

    public TraderFilterKey(){}
    
    public TraderFilterKey(Trader traderId, String userId){
        this.traderId = traderId;
        this.userId = userId;
    }
    
    @ManyToOne
    @JoinColumn(name="trader_id", nullable=false)
    public Trader getTraderId() {
        return traderId;
    }

    public void setTraderId(Trader traderId) {
        this.traderId = traderId;
    }

    @Column(name="user_id", nullable=false)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
