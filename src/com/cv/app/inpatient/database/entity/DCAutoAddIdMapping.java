/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.entity;

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
public class DCAutoAddIdMapping implements java.io.Serializable{
    private DCAutoAddIdMappingKey key;
    private Integer sortOrder;
    private Integer qty;
    
    public DCAutoAddIdMapping(){
        key = new DCAutoAddIdMappingKey();
    }
    
    @EmbeddedId
    public DCAutoAddIdMappingKey getKey() {
        return key;
    }

    public void setKey(DCAutoAddIdMappingKey key) {
        this.key = key;
    }

    @Column(name="sort_order")
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    @Column(name="add_qty")
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
