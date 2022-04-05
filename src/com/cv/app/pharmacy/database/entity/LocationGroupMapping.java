/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="loc_group_mapping")
public class LocationGroupMapping implements Serializable {
    private LocationGroupMappingKey key;

    public LocationGroupMapping(){
        key = new LocationGroupMappingKey();
    }
            
    @EmbeddedId
    public LocationGroupMappingKey getKey() {
        return key;
    }

    public void setKey(LocationGroupMappingKey key) {
        this.key = key;
    }
}
