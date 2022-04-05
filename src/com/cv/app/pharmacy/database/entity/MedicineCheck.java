/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author admin
 */
@Entity
@Table(name = "medicine_chk")
public class MedicineCheck implements java.io.Serializable {
    private String medId;
    private String medDesp;

    @Id @Column(name="med_id", length=15)
    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    @Column(name="description", length=255)
    public String getMedDesp() {
        return medDesp;
    }

    public void setMedDesp(String medDesp) {
        this.medDesp = medDesp;
    }
    
}
