/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.database.entity;

import com.cv.app.pharmacy.database.entity.Medicine;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author winswe
 */
@Embeddable
public class OTMedUsageKey implements Serializable{
    private Integer serviceId;
    private Medicine med;

    public OTMedUsageKey(){}
    
    public OTMedUsageKey(Integer serviceId, Medicine med){
        this.serviceId = serviceId;
        this.med = med;
    }
    
    @Column(name="service_id")
    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    @ManyToOne
    @JoinColumn(name="med_id", nullable=false)
    public Medicine getMed() {
        return med;
    }

    public void setMed(Medicine med) {
        this.med = med;
    }
}
