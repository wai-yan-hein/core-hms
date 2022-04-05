/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author PHSH MDY
 */
@Entity
@Table(name = "dc_cus_group_detail")
public class DCCusGroupDetail implements java.io.Serializable {
    
    private DCCusGroupDetailKey key;
    
    public DCCusGroupDetail(){
        key = new DCCusGroupDetailKey();
    }

    @EmbeddedId
    public DCCusGroupDetailKey getKey() {
        return key;
    }

    public void setKey(DCCusGroupDetailKey key) {
        this.key = key;
    }
}
