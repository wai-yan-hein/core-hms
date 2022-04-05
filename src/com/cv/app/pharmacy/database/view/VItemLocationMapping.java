/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import com.cv.app.pharmacy.database.entity.LocationItemMappingKey;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "v_item_location_mapping")
public class VItemLocationMapping implements java.io.Serializable {

    private LocationItemMappingKey key;
    private String locationName;
    private Integer parentLocation;
    private Boolean calcStock;
    private Integer locationType;
    private String itemName;
    private Boolean mapStatus;
    private String itemTypeId;
    private String itemTypeName;
    private Integer categoryId;
    private String categoryName;
    private Integer brandId;
    private String brandName;
    private String shortName;
    
    @EmbeddedId
    public LocationItemMappingKey getKey() {
        return key;
    }

    public void setKey(LocationItemMappingKey key) {
        this.key = key;
    }

    @Column(name = "location_name")
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Column(name = "parent")
    public Integer getParentLocation() {
        return parentLocation;
    }

    public void setParentLocation(Integer parentLocation) {
        this.parentLocation = parentLocation;
    }

    @Column(name = "calc_stock")
    public Boolean getCalcStock() {
        return calcStock;
    }

    public void setCalcStock(Boolean calcStock) {
        this.calcStock = calcStock;
    }

    @Column(name = "location_type")
    public Integer getLocationType() {
        return locationType;
    }

    public void setLocationType(Integer locationType) {
        this.locationType = locationType;
    }

    @Column(name = "med_name")
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Column(name = "map_status")
    public Boolean getMapStatus() {
        return mapStatus;
    }

    public void setMapStatus(Boolean mapStatus) {
        this.mapStatus = mapStatus;
    }

    @Column(name = "item_type_id")
    public String getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(String itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    @Column(name = "item_type_name")
    public String getItemTypeName() {
        return itemTypeName;
    }

    public void setItemTypeName(String itemTypeName) {
        this.itemTypeName = itemTypeName;
    }

    @Column(name = "category_id")
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Column(name = "cat_name")
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Column(name="brand_id")
    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    @Column(name="brand_name")
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
    
    @Column(name="short_name")
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

}
