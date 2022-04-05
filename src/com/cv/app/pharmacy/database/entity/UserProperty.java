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
@Table(name = "user_prop")
public class UserProperty implements java.io.Serializable {

    private String propDesp;
    private String propValue;
    private String propRemark;
    private String userId;
    
    @Id
    @Column(name = "user_proc_desp", unique = true, nullable = false, length = 255)
    public String getPropDesp() {
        return propDesp;
    }

    public void setPropDesp(String propDesp) {
        this.propDesp = propDesp;
    }

    @Column(name = "user_proc_value", length = 255)
    public String getPropValue() {
        return propValue;
    }

    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }

    @Column(name = "user_proc_remark", length = 255)
    public String getPropRemark() {
        return propRemark;
    }

    public void setPropRemark(String propRemark) {
        this.propRemark = propRemark;
    }

    @Column(name="user_id", length = 15)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
