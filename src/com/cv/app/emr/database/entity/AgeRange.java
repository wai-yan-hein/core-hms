/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.emr.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author admin
 */
@Entity
@Table(name = "emr_age_range")
public class AgeRange implements java.io.Serializable{
    private Integer id;
    private String agerDesp;
    private Integer sortOrder;

    @Id @Column(name="id", nullable=false, unique=true)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="ager_desp", length=50)
    public String getAgerDesp() {
        return agerDesp;
    }

    public void setAgerDesp(String agerDesp) {
        this.agerDesp = agerDesp;
    }

    @Column(name="sort_order")
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    @Override
    public String toString(){
        return agerDesp;
    }
}
