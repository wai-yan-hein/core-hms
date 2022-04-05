/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import com.cv.app.opd.database.entity.Doctor;
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
public class DoctorFilterKey implements Serializable{
    private Doctor doctorId;
    private String userId;

    public DoctorFilterKey(){}
    
    public DoctorFilterKey(Doctor doctorId, String userId){
        this.doctorId = doctorId;
        this.userId = userId;
    }
    
    @ManyToOne
    @JoinColumn(name="doctor_id", nullable=false)
    public Doctor getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Doctor doctorId) {
        this.doctorId = doctorId;
    }

    @Column(name="user_id", nullable=false)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
