/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.view;

import com.cv.app.ot.database.entity.DrDetailIdKey;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author ACER
 */
@Entity
@Table(name = "v_dc_dr_detail_id")
public class VDCDrDetailId implements java.io.Serializable{
    private DrDetailIdKey key;
    private String serviceName;

    @EmbeddedId
    public DrDetailIdKey getKey() {
        return key;
    }

    public void setKey(DrDetailIdKey key) {
        this.key = key;
    }

    @Column(name="service_name")
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
