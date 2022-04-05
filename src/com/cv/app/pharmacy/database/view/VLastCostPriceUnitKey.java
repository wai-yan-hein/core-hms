/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author winswe
 */
@Embeddable
public class VLastCostPriceUnitKey implements Serializable{
    private String medId;
    private String unitId;

    @Column(name="med_id")
    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    @Column(name="item_unit")
    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.medId);
        hash = 11 * hash + Objects.hashCode(this.unitId);
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
        final VLastCostPriceUnitKey other = (VLastCostPriceUnitKey) obj;
        if (!Objects.equals(this.medId, other.medId)) {
            return false;
        }
        if (!Objects.equals(this.unitId, other.unitId)) {
            return false;
        }
        return true;
    }
}
