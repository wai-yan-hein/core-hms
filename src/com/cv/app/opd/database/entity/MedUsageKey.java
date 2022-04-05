/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import com.cv.app.pharmacy.database.entity.Medicine;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author WSwe
 */
@Embeddable
public class MedUsageKey implements Serializable{
    private Integer service;
    private Medicine med;
    
    public MedUsageKey(){}
    
    public MedUsageKey(Integer service, Medicine med){
        this.service = service;
        this.med = med;
    }
    
    @Column(name="service_id")
    public Integer getService() {
        return service;
    }

    public void setService(Integer service) {
        this.service = service;
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
