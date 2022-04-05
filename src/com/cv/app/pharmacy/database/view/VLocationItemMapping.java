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
@Table(name = "v_location_item_mapping")
public class VLocationItemMapping implements java.io.Serializable {

    private LocationItemMappingKey key;
    private String locationName;
    private Integer parentLocation;
    private Boolean calcStock;
    private Integer locationType;
    private String itemName;
    private Boolean mapStatus;
    
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

    @Override
    public String toString() {
        return locationName;
    }
}
