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
public class OPDAutoAddIdMapping implements java.io.Serializable{
    private OPDAutoAddIdMappingKey key;
    private Integer sortOrder;
    private Integer qty;
    
    public OPDAutoAddIdMapping(){
        key = new OPDAutoAddIdMappingKey();
    }
    
    @EmbeddedId
    public OPDAutoAddIdMappingKey getKey() {
        return key;
    }

    public void setKey(OPDAutoAddIdMappingKey key) {
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
