/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author MyoGyi
 */
@Entity
@Table(name="business")
public class BusinessType implements Serializable {
    
    private Integer businessId;
    private String description;

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="business_id", unique=true, nullable=false)
    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    @Column(name="description", unique=true, nullable=false,
            length=225)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
    

    
}
