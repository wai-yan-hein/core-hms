/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.database.entity;

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
@Table(name = "anaesthesist_type")
public class AnesthesistType implements java.io.Serializable{
    private Integer id;
    private String typeName;
    private boolean actStatus;

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="anaesthesist_name", length=50)
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Column(name="act_status")
    public boolean isActStatus() {
        return actStatus;
    }

    public void setActStatus(boolean actStatus) {
        this.actStatus = actStatus;
    }
}
