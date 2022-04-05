/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import static javax.persistence.GenerationType.IDENTITY;
/**
 *
 * @author WSwe
 */
@Entity
@Table(name="city")
public class City implements java.io.Serializable{
    private Integer cityId;
    private String cityName;
    private Integer migId;
    
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="city_id", unique=true, nullable=false)
    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    @Column(name="city_name", unique=true, nullable=false,
            length=70)
    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    
    @Override
    public String toString(){
        return cityName;
    }

    @Column(name="mig_id")
    public Integer getMigId() {
        return migId;
    }

    public void setMigId(Integer migId) {
        this.migId = migId;
    }
}
