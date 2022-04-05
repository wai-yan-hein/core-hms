/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author WSwe
 */
@Embeddable
public class ReOrderLevelKey implements Serializable{
    private Integer locationId;
    private String med;

    //@ManyToOne
    //@JoinColumn(name="location_id", nullable=false)
    @Column(name="location_id", nullable=false)
    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @Column(name="item_id", nullable=false)
    public String getMed() {
        return med;
    }

    public void setMed(String med) {
        this.med = med;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.locationId);
        hash = 17 * hash + Objects.hashCode(this.med);
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
        final ReOrderLevelKey other = (ReOrderLevelKey) obj;
        if (!Objects.equals(this.locationId, other.locationId)) {
            return false;
        }
        if (!Objects.equals(this.med, other.med)) {
            return false;
        }
        return true;
    }
}
