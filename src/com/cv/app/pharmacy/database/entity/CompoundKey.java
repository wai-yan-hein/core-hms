/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author WSwe
 */
@Embeddable
public class CompoundKey implements Serializable {

    private String machineName;
    private String vouType;
    private String period;

    public CompoundKey(){}
    
    public CompoundKey(String machineName, String vouType, String period){
        this.machineName = machineName;
        this.vouType = vouType;
        this.period = period;
    }
    
    @Column(name = "machine_name", nullable = false, length = 50)
    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    @Column(name = "vou_period", nullable = false, length = 15)
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    @Column(name = "vou_type", nullable = false, length = 15)
    public String getVouType() {
        return vouType;
    }

    public void setVouType(String vouType) {
        this.vouType = vouType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof CompoundKey)) {
            return false;
        }

        final CompoundKey other = (CompoundKey) o;
        if (!(getMachineName().equals(other.getMachineName()))) {
            return false;
        }

        if (!(getPeriod().equals(other.getPeriod()))) {
            return false;
        }

        if (!(getVouType().equals(other.getVouType()))) {
            return false;
        }

        return true;
    }
/*
    @Override
    public int hashCode() {
        int code = 0;
        
        if (machineName != null) {
            code += 1;
        }
        
        if (vouType != null) {
            code += 1;
        }
        
        if (period != null) {
            code += 1;
        }
        
        return code;
    }
    * 
    */
}
