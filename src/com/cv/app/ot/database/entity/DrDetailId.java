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
 * @author MyintMo
 */
@Entity
@Table(name = "dr_detail_id")
public class DrDetailId implements java.io.Serializable{
    private DrDetailIdKey key;

    public DrDetailId(){
        key = new DrDetailIdKey();
    }
    
    @EmbeddedId
    public DrDetailIdKey getKey() {
        return key;
    }

    public void setKey(DrDetailIdKey key) {
        this.key = key;
    }
}
