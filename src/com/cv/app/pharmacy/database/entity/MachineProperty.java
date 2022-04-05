/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "machine_prop")
public class MachineProperty implements java.io.Serializable {

    private MachinePropertyKey key;
    private String propValue;
    private String propRemark;

    public MachineProperty(){
        key = new MachinePropertyKey();
    }
    
    @EmbeddedId
    public MachinePropertyKey getKey() {
        return key;
    }

    public void setKey(MachinePropertyKey key) {
        this.key = key;
    }

    @Column(name = "mac_prop_value", length = 255)
    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    @Column(name = "mac_prop_remark", length = 255)
    public String getPropRemark() {
        return propRemark;
    }

    public void setPropRemark(String propRemark) {
        this.propRemark = propRemark;
    }
}
