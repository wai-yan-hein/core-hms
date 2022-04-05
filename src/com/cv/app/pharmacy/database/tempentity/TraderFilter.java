/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="tmp_trader_filter")
public class TraderFilter implements java.io.Serializable{
    private TraderFilterKey key;

    public TraderFilter(){}
    
    public TraderFilter(TraderFilterKey key){
        this.key = key;
    }
    
    @EmbeddedId
    public TraderFilterKey getKey() {
        return key;
    }

    public void setKey(TraderFilterKey key) {
        this.key = key;
    }
}
