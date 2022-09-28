/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="patient_op")
public class PatientOpening implements java.io.Serializable{
    private CompoundKeyPatientOp key;
    private Double amount;
    private String admissionNo;
    
    public PatientOpening(){}
    
    public PatientOpening(CompoundKeyPatientOp key){
        this.key = key;
    }
    
    @EmbeddedId
    public CompoundKeyPatientOp getKey() {
        return key;
    }

    public void setKey(CompoundKeyPatientOp key) {
        this.key = key;
    }

    @Column(name="op_amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Column(name="admission_no", length=15)
    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }
}
