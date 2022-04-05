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
public class VMedRelKey implements Serializable{
    private String medId;
    private Integer uniqueId;

    @Column(name="med_id")
    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    @Column(name="rel_unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.medId);
        hash = 89 * hash + Objects.hashCode(this.uniqueId);
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
        final VMedRelKey other = (VMedRelKey) obj;
        if (!Objects.equals(this.medId, other.medId)) {
            return false;
        }
        if (!Objects.equals(this.uniqueId, other.uniqueId)) {
            return false;
        }
        return true;
    }
    
}
