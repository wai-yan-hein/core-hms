/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cv.app.opd.database.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="med_usaged")
public class MUsage implements java.io.Serializable {
    private MUKey key;
    private Float unitQty;
    private String unitId;
    private Float qtySmallest;
    private Date createdDate;
    private Date updatedDate;
    private Integer location;
    private Double smallestCost;
    private Double ttlCost;

    @EmbeddedId
    public MUKey getKey() {
        return key;
    }

    public void setKey(MUKey key) {
        this.key = key;
    }

    @Column(name="unit_qty")
    public Float getUnitQty() {
        return unitQty;
    }

    public void setUnitQty(Float unitQty) {
        this.unitQty = unitQty;
    }

    @Column(name="unit_id")
    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
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

    @Column(name="location_id")
    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    @Column(name="smallest_cost")
    public Double getSmallestCost() {
        return smallestCost;
    }

    public void setSmallestCost(Double smallestCost) {
        this.smallestCost = smallestCost;
    }

    @Column(name="total_cost")
    public Double getTtlCost() {
        return ttlCost;
    }

    public void setTtlCost(Double ttlCost) {
        this.ttlCost = ttlCost;
    }
}
