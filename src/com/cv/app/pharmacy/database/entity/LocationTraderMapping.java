/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="location_trader_mapping")
public class LocationTraderMapping implements java.io.Serializable {
    
    private LocationTraderMappingKey key;

    @EmbeddedId
    public LocationTraderMappingKey getKey() {
        return key;
    }

    public void setKey(LocationTraderMappingKey key) {
        this.key = key;
    }
    
}
