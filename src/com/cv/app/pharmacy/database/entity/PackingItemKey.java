/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author winswe
 */
@Embeddable
public class PackingItemKey implements Serializable{
    private String packingItemCode;
    private Medicine item;

    public PackingItemKey(){
        item = new Medicine();
    }
    
    @Column(name="packing_item_code", length=15)
    public String getPackingItemCode() {
        return packingItemCode;
    }

    public void setPackingItemCode(String packingItemCode) {
        this.packingItemCode = packingItemCode;
    }

    @ManyToOne
    @JoinColumn(name = "med_id")
    public Medicine getItem() {
        return item;
    }

    public void setItem(Medicine item) {
        this.item = item;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.packingItemCode);
        hash = 89 * hash + Objects.hashCode(this.item);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PackingItemKey other = (PackingItemKey) obj;
        if (!Objects.equals(this.packingItemCode, other.packingItemCode)) {
            return false;
        }
        if (!Objects.equals(this.item, other.item)) {
            return false;
        }
        return true;
    }
}
