/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.database.entity;

import com.cv.app.pharmacy.database.entity.ItemUnit;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="ot_med_usage")
public class OTMedUsage implements java.io.Serializable{
    private OTMedUsageKey key;
    private Float unitQty;
    private ItemUnit unit;
    private Float qtySmallest;
    private Date createdDate;
    private Date updatedDate;
    
    public OTMedUsage(){
        key = new OTMedUsageKey();
    }
    
    @EmbeddedId
    public OTMedUsageKey getKey() {
        return key;
    }

    public void setKey(OTMedUsageKey key) {
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
    @JoinColumn(name="unit_id")
    public ItemUnit getUnit() {
        return unit;
    }

    public void setUnit(ItemUnit unit) {
        this.unit = unit;
    }

    @Column(name="qty_smallest")
    public Float getQtySmallest() {
        return qtySmallest;
    }

    public void setQtySmallest(Float qtySmallest) {
        this.qtySmallest = qtySmallest;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}
