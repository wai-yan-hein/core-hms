/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "adj_type")
public class AdjType implements java.io.Serializable{
    private String adjTypeId;
    private String adjDesp;

    @Column(name="adj_type_desp", unique=true, nullable=false, length=10)
    public String getAdjDesp() {
        return adjDesp;
    }

    public void setAdjDesp(String adjDesp) {
        this.adjDesp = adjDesp;
    }

    @Id
    @Column(name="adj_type_id", unique=true, nullable=false, length=2)
    public String getAdjTypeId() {
        return adjTypeId;
    }

    
    public void setAdjTypeId(String adjTypeId) {
        this.adjTypeId = adjTypeId;
    }
    
    @Override
    public String toString(){
        return adjDesp;
    }
}
