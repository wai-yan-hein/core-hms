/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author WSwe
 */
@Embeddable
public class CompoundKeyMedOpDate implements Serializable{
    private Integer locationId;
    private String medId;
    private Date opDate;

    public CompoundKeyMedOpDate(){}
    
    public CompoundKeyMedOpDate(Integer locationId, String medId, Date opDate){
        this.locationId = locationId;
        this.medId = medId;
        this.opDate = opDate;
    }
    
    @Column(name="location_id")
    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @Column(name="med_id")
    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="op_date")
    public Date getOpDate() {
        return opDate;
    }

    public void setOpDate(Date opDate) {
        this.opDate = opDate;
    }
}
