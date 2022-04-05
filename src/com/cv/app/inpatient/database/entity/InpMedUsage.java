/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.entity;

import com.cv.app.pharmacy.database.entity.ItemUnit;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="inp_med_usage")
public class InpMedUsage implements java.io.Serializable{
    private MedUsageKey key;
    private Float unitQty;
    private ItemUnit unit;
    private Float qtySmall;
    
    public InpMedUsage(){}
    
    public InpMedUsage(MedUsageKey key){
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
    public Float getQtySmall() {
        return qtySmall;
    }

    public void setQtySmall(Float qtySmall) {
        this.qtySmall = qtySmall;
    }

    @EmbeddedId
    public MedUsageKey getKey() {
        return key;
    }

    public void setKey(MedUsageKey key) {
        this.key = key;
    }
}
