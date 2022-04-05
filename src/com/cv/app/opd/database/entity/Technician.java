/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author asus
 */
@Entity
@Table(name = "technician")
public class Technician implements java.io.Serializable{
    private Integer techId;
    private String techName;
    private Integer techType;
    private Boolean active;
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "tech_id", unique = true, nullable = false)
    public Integer getTechId() {
        return techId;
    }

    public void setTechId(Integer techId) {
        this.techId = techId;
    }

    @Column(name="tech_name", unique=true, length=500)
    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    @Column(name="tech_type")
    public Integer getTechType() {
        return techType;
    }

    public void setTechType(Integer techType) {
        this.techType = techType;
    }

    @Column(name="active")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    
    @Override
    public String toString(){
        return techName;
    }
}
