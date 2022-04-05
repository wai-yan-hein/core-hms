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
 * @author Eitar
 */
@Entity
@Table(name = "dc_status")
public class DCStatus implements java.io.Serializable{
    private Integer statusId;
    private String statusDesp;

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="status_id", unique=true, nullable=false)
    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    @Column(name="status_desp", length=25)
    public String getStatusDesp() {
        return statusDesp;
    }

    public void setStatusDesp(String statusDesp) {
        this.statusDesp = statusDesp;
    }
    
    @Override
    public String toString(){
        return statusDesp;
    }
}
