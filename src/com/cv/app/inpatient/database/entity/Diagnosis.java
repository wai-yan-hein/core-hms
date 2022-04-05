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
 * @author admin
 */
@Entity
@Table(name = "diagnosis")
public class Diagnosis implements java.io.Serializable{
    private Integer id;
    private String localName;
    private String intCode; //International code
    private String intName; //International name
    private Integer migId;

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="local_name", length=150)
    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    @Column(name="int_code", length=25, unique=true)
    public String getIntCode() {
        return intCode;
    }

    public void setIntCode(String intCode) {
        this.intCode = intCode;
    }

    @Column(name="int_name", length=150)
    public String getIntName() {
        return intName;
    }

    public void setIntName(String intName) {
        this.intName = intName;
    }

    @Column(name="mig_id")
    public Integer getMigId() {
        return migId;
    }

    public void setMigId(Integer migId) {
        this.migId = migId;
    }
    
    @Override
    public String toString(){
        return localName;
    }
}
