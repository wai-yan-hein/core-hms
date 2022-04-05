/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "packing_item")
public class PackingItem implements java.io.Serializable{
    private PackingItemKey key;
    private Float unitQty;
    private ItemUnit unit;
    private Double qtyInSmallest;
    private Integer uniqueId;
    
    public PackingItem(){
        key = new PackingItemKey();
        unit = new ItemUnit();
    }
    
    @EmbeddedId
    public PackingItemKey getKey() {
        return key;
    }

    public void setKey(PackingItemKey key) {
        this.key = key;
    }

    @Column(name="unit_qty")
    public Float getUnitQty() {
        return unitQty;
    }

    public void setUnitQty(Float unitQty) {
        this.unitQty = unitQty;
    }

    @ManyToOne
    @JoinColumn(name = "unit_id")
    public ItemUnit getUnit() {
        return unit;
    }

    public void setUnit(ItemUnit unit) {
        this.unit = unit;
    }

    @Column(name="qty_in_smallest")
    public Double getQtyInSmallest() {
        return qtyInSmallest;
    }

    public void setQtyInSmallest(Double qtyInSmallest) {
        this.qtyInSmallest = qtyInSmallest;
    }

    @Column(name="unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }
}
