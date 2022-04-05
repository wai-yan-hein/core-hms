/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author ACER
 */
@Entity
@Table(name="v_stock_costing")
public class VStockCosting implements java.io.Serializable{
    private String costKey;
    private String medId;
    private String userId;
    private Float balQty;
    private String balQtyStr;
    private Double ttlCost;
    private String tranOption;
    private Integer locationId;
    private Float locTtlSmallestQty;
    private Double locTtlCost;
    private String medTypeId;
    private Integer catId;
    private Integer brandId;
    private String shortName;
    private String relStr;
    private String medName;

    @Id @Column(name="cost_key")
    public String getCostKey() {
        return costKey;
    }

    public void setCostKey(String costKey) {
        this.costKey = costKey;
    }

    @Column(name="med_id")
    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    @Column(name="user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name="bal_qty")
    public Float getBalQty() {
        return balQty;
    }

    public void setBalQty(Float balQty) {
        this.balQty = balQty;
    }

    @Column(name="qty_str")
    public String getBalQtyStr() {
        return balQtyStr;
    }

    public void setBalQtyStr(String balQtyStr) {
        this.balQtyStr = balQtyStr;
    }

    @Column(name="total_cost")
    public Double getTtlCost() {
        return ttlCost;
    }

    public void setTtlCost(Double ttlCost) {
        this.ttlCost = ttlCost;
    }

    @Column(name="tran_option")
    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }

    @Column(name="location_id")
    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @Column(name="loc_ttl_small_qty")
    public Float getLocTtlSmallestQty() {
        return locTtlSmallestQty;
    }

    public void setLocTtlSmallestQty(Float locTtlSmallestQty) {
        this.locTtlSmallestQty = locTtlSmallestQty;
    }

    @Column(name="loc_ttl_cost")
    public Double getLocTtlCost() {
        return locTtlCost;
    }

    public void setLocTtlCost(Double locTtlCost) {
        this.locTtlCost = locTtlCost;
    }

    @Column(name="med_type_id")
    public String getMedTypeId() {
        return medTypeId;
    }

    public void setMedTypeId(String medTypeId) {
        this.medTypeId = medTypeId;
    }

    @Column(name="category_id")
    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    @Column(name="brand_id")
    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    @Column(name="short_name")
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Column(name="med_rel_str")
    public String getRelStr() {
        return relStr;
    }

    public void setRelStr(String relStr) {
        this.relStr = relStr;
    }

    @Column(name="med_name")
    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }
}
