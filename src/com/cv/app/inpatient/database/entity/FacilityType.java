/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author MyintMo
 */
@Entity
@Table(name = "facility_type")
public class FacilityType implements java.io.Serializable {
    private Integer typeId;
    private String typeDesp;
    private Double price;

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="type_id", unique=true, nullable=false)
    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    @Column(name="type_desp", unique=true, nullable=false, length=255)
    public String getTypeDesp() {
        return typeDesp;
    }

    public void setTypeDesp(String typeDesp) {
        this.typeDesp = typeDesp;
    }

    @Column(name="price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
    
    @Override
    public String toString(){
        return typeDesp;
    }
}
