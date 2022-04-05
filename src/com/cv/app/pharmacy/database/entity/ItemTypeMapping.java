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
@Table(name="item_type_mapping")
public class ItemTypeMapping implements Serializable {
    private ItemTypeMappingKey key;

    public ItemTypeMapping(){
        key = new ItemTypeMappingKey();
    }
            
    @EmbeddedId
    public ItemTypeMappingKey getKey() {
        return key;
    }

    public void setKey(ItemTypeMappingKey key) {
        this.key = key;
    }
}
