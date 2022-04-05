/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import static javax.persistence.GenerationType.IDENTITY;
/**
 *
 * PackingTemplatedetail is detail information of PackingTemplate class.
 * Database table name is packing_template_detail.
 */
@Entity
@Table(name="packing_template_detail")
public class PackingTemplateDetail implements java.io.Serializable{
    private Integer ptdId;
    private Integer uniqueId;
    private Float unitQty;
    private ItemUnit itemUnit;
    private Float smallestQty;
    
    public PackingTemplateDetail(){}

    @ManyToOne
    @JoinColumn(name="item_unit")
    public ItemUnit getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(ItemUnit itemUnit) {
        this.itemUnit = itemUnit;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "template_detail_id", unique = true, nullable = false)
    public Integer getPtdId() {
        return ptdId;
    }

    public void setPtdId(Integer ptdId) {
        this.ptdId = ptdId;
    }

    @Column(name = "smallest_qty")
    public Float getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Float smallestQty) {
        this.smallestQty = smallestQty;
    }

    @Column(name = "unique_id", nullable = false)
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name = "unit_qty", nullable = false)
    public Float getUnitQty() {
        return unitQty;
    }

    public void setUnitQty(Float unitQty) {
        this.unitQty = unitQty;
    }
    
    
}
