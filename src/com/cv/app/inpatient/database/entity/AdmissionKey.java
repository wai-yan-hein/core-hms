/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.entity;

import com.cv.app.opd.database.entity.Patient;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author Eitar
 */
@Embeddable

public class AdmissionKey implements Serializable {

    private Patient register;
    private String amsNo;

    public AdmissionKey() {
    }

    public AdmissionKey(Patient pd, String amsNo) {
        this.register = pd;
        this.amsNo = amsNo;
    }

    @ManyToOne
    @JoinColumn(name = "reg_no", nullable = false)
    public Patient getRegister() {
        return register;
    }

    public void setRegister(Patient register) {
        this.register = register;
    }

    @Column(name="ams_no")
    public String getAmsNo() {
        return amsNo;
    }

    public void setAmsNo(String amsNo) {
        this.amsNo = amsNo;
    }
    
    

}
