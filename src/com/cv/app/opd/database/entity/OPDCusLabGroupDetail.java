/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author PHSH MDY
 */
@Entity
@Table(name = "opd_cus_lab_group_detail")
public class OPDCusLabGroupDetail implements java.io.Serializable {
    
    private OPDCusLabGroupDetailKey key;
    
    public OPDCusLabGroupDetail(){
        key = new OPDCusLabGroupDetailKey();
    }

    @EmbeddedId
    public OPDCusLabGroupDetailKey getKey() {
        return key;
    }

    public void setKey(OPDCusLabGroupDetailKey key) {
        this.key = key;
    }
}
