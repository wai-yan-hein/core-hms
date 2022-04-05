/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "sys_prop")
public class SysProperty implements java.io.Serializable {

    private String propDesp;
    private String propValue;
    private String propRemark;

    @Id
    @Column(name = "sys_prop_desp", unique = true, nullable = false, length = 255)
    public String getPropDesp() {
        return propDesp;
    }

    public void setPropDesp(String propDesp) {
        this.propDesp = propDesp;
    }

    @Column(name = "sys_prop_value", length = 255)
    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    @Column(name = "sys_prop_remark", length = 255)
    public String getPropRemark() {
        return propRemark;
    }

    public void setPropRemark(String propRemark) {
        this.propRemark = propRemark;
    }
}
