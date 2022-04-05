/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="tmp_doctor_filter")
public class DoctorFilter implements java.io.Serializable{
    private DoctorFilterKey key;

    public DoctorFilter(){}
    
    public DoctorFilter(DoctorFilterKey key){
        this.key = key;
    }
    
    @EmbeddedId
    public DoctorFilterKey getKey() {
        return key;
    }

    public void setKey(DoctorFilterKey key) {
        this.key = key;
    }
}
