/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
@Table(name = "v_last_cost_price_unit")
public class VLastCostPriceUnit implements java.io.Serializable{
    private VLastCostPriceUnitKey key;
    private String itemName;
    private Double unitCostPrice;

    public VLastCostPriceUnit(){
        key = new VLastCostPriceUnitKey();
    }
    
    @EmbeddedId
    public VLastCostPriceUnitKey getKey() {
        return key;
    }

    public void setKey(VLastCostPriceUnitKey key) {
        this.key = key;
    }

    @Column(name="med_name")
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Column(name="unit_cost_price")
    public Double getUnitCostPrice() {
        return unitCostPrice;
    }

    public void setUnitCostPrice(Double unitCostPrice) {
        this.unitCostPrice = unitCostPrice;
    }
    
    
}
