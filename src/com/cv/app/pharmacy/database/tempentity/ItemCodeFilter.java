/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="tmp_item_code_filter")
public class ItemCodeFilter implements java.io.Serializable{
    private ItemCodeFilterKey key;
    private int noOfCopy = 1;
    
    public ItemCodeFilter(){}
    
    public ItemCodeFilter(ItemCodeFilterKey key){
        this.key = key;
    }
    
    @EmbeddedId
    public ItemCodeFilterKey getKey() {
        return key;
    }

    public void setKey(ItemCodeFilterKey key) {
        this.key = key;
    }

    @Transient
    public int getNoOfCopy() {
        return noOfCopy;
    }

    public void setNoOfCopy(int noOfCopy) {
        this.noOfCopy = noOfCopy;
    }
}
