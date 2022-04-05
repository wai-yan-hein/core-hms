/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author MyintMo
 */
@Entity
@Table(name="auto_add_id_mapping")
public class AutoAddIdMapping implements java.io.Serializable{
    private AutoAddIdMappingKey key;
    private Integer sortOrder;
    
    public AutoAddIdMapping(){
        key = new AutoAddIdMappingKey();
    }
    
    @EmbeddedId
    public AutoAddIdMappingKey getKey() {
        return key;
    }

    public void setKey(AutoAddIdMappingKey key) {
        this.key = key;
    }

    @Column(name="sort_order")
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
