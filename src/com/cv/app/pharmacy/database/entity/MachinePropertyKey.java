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

/**
 *
 * @author winswe
 */
@Embeddable
public class MachinePropertyKey implements Serializable{
    
    private String propDesp;
    private Integer machineId;

    @Column(name = "mac_prop_desp", nullable = false, length = 255)
    public String getPropDesp() {
        return propDesp;
    }

    public void setPropDesp(String propDesp) {
        this.propDesp = propDesp;
    }

    @Column(name="machine_id")
    public Integer getMachineId() {
        return machineId;
    }

    public void setMachineId(Integer machineId) {
        this.machineId = machineId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.propDesp);
        hash = 97 * hash + Objects.hashCode(this.machineId);
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
        final MachinePropertyKey other = (MachinePropertyKey) obj;
        if (!Objects.equals(this.propDesp, other.propDesp)) {
            return false;
        }
        if (!Objects.equals(this.machineId, other.machineId)) {
            return false;
        }
        return true;
    }
}
