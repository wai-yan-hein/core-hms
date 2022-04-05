/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cv.app.pharmacy.database.view;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "v_med_rel")
public class VMedRel implements java.io.Serializable{
    private VMedRelKey key;
    private Integer smallestQty;
    private String unitId;

    public VMedRel(){
        key = new VMedRelKey();
    }
    
    @EmbeddedId
    public VMedRelKey getKey() {
        return key;
    }

    public void setKey(VMedRelKey key) {
        this.key = key;
    }

    @Column(name="smallest_qty")
    public Integer getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Integer smallestQty) {
        this.smallestQty = smallestQty;
    }

    @Column(name="item_unit")
    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }
}
