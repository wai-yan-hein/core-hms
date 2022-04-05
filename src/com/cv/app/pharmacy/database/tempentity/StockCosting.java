/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "tmp_stock_costing")
public class StockCosting implements java.io.Serializable {

    private StockCostingKey key;
    private Integer blaQty;
    private String balQtyStr;
    private Double ttlCost;
    private Integer location;
    private Float locTtlSmallQty;
    private Double locTtlCost;
    
    @Column(name = "qty_str")
    public String getBalQtyStr() {
        return balQtyStr;
    }

    public void setBalQtyStr(String balQtyStr) {
        this.balQtyStr = balQtyStr;
    }

    @Column(name = "bal_qty")
    public Integer getBlaQty() {
        return blaQty;
    }

    public void setBlaQty(Integer blaQty) {
        this.blaQty = blaQty;
    }

    @EmbeddedId
    public StockCostingKey getKey() {
        return key;
    }

    public void setKey(StockCostingKey key) {
        this.key = key;
    }

    @Column(name = "total_cost")
    public Double getTtlCost() {
        return ttlCost;
    }

    public void setTtlCost(Double ttlCost) {
        this.ttlCost = ttlCost;
    }

    @Column(name="location_id")
    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    @Column(name="loc_ttl_small_qty")
    public Float getLocTtlSmallQty() {
        return locTtlSmallQty;
    }

    public void setLocTtlSmallQty(Float locTtlSmallQty) {
        this.locTtlSmallQty = locTtlSmallQty;
    }

    @Column(name="loc_ttl_cost")
    public Double getLocTtlCost() {
        return locTtlCost;
    }

    public void setLocTtlCost(Double locTtlCost) {
        this.locTtlCost = locTtlCost;
    }
}
