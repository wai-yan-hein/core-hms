/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.database.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author PHSH MDY
 */
@Entity
@Table(name = "ot_cus_group_detail")
public class OTCusGroupDetail implements java.io.Serializable {
    
    private OTCusGroupDetailKey key;
    
    public OTCusGroupDetail(){
        key = new OTCusGroupDetailKey();
    }

    @EmbeddedId
    public OTCusGroupDetailKey getKey() {
        return key;
    }

    public void setKey(OTCusGroupDetailKey key) {
        this.key = key;
    }
}
