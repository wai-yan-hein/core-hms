/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "v_gross_profits")
public class VGrossProfit implements java.io.Serializable {

    private VGrossProfitKey key;
    private Double opValue;
    private Double ttlPur;
    private Double clValue;
    private Double cogsValue;
    private Double ttlSale;
    private Double gpValue;
    private String itemName;
    private String relStr;
    private Integer brandId;
    private Integer categoryId;
    private String medTypeId;
    
    @Column(name = "cl_value")
    public Double getClValue() {
        return clValue;
    }

    public void setClValue(Double clValue) {
        this.clValue = clValue;
    }

    @Column(name = "cogs_value")
    public Double getCogsValue() {
        return cogsValue;
    }

    public void setCogsValue(Double cogsValue) {
        this.cogsValue = cogsValue;
    }

    @Column(name = "gp_value")
    public Double getGpValue() {
        return gpValue;
    }

    public void setGpValue(Double gpValue) {
        this.gpValue = gpValue;
    }

    @EmbeddedId
    public VGrossProfitKey getKey() {
        return key;
    }

    public void setKey(VGrossProfitKey key) {
        this.key = key;
    }

    @Column(name = "op_value")
    public Double getOpValue() {
        return opValue;
    }

    public void setOpValue(Double opValue) {
        this.opValue = opValue;
    }

    @Column(name = "ttl_pur")
    public Double getTtlPur() {
        return ttlPur;
    }

    public void setTtlPur(Double ttlPur) {
        this.ttlPur = ttlPur;
    }

    @Column(name = "ttl_sale")
    public Double getTtlSale() {
        return ttlSale;
    }

    public void setTtlSale(Double ttlSale) {
        this.ttlSale = ttlSale;
    }

    @Column(name="med_name")
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Column(name="med_rel_str")
    public String getRelStr() {
        return relStr;
    }

    public void setRelStr(String relStr) {
        this.relStr = relStr;
    }

    @Column(name="brand_id")
    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    @Column(name="category_id")
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name="med_type_id")
    public String getMedTypeId() {
        return medTypeId;
    }

    public void setMedTypeId(String medTypeId) {
        this.medTypeId = medTypeId;
    }
}
