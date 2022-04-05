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
 * @author winswe
 */
@Entity
@Table(name="opd_result_type")
public class OPDResultType implements java.io.Serializable{
    
    private Integer typeId;
    private String typeDesp;

    @GeneratedValue(strategy=IDENTITY)
    @Id @Column(name="type_id", unique=true, nullable=false)
    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    @Column(name="type_desp", length=100)
    public String getTypeDesp() {
        return typeDesp;
    }

    public void setTypeDesp(String typeDesp) {
        this.typeDesp = typeDesp;
    }
    
    @Override
    public String toString(){
        return typeDesp;
    }
}
