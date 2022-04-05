/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "vou_status")
public class VouStatus implements java.io.Serializable{
    private Integer statusId;
    private String statusDesp;

    @Column(name="status_desp", unique=true, nullable=false, length=15)
    public String getStatusDesp() {
        return statusDesp;
    }

    public void setStatusDesp(String statusDesp) {
        this.statusDesp = statusDesp;
    }

    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="vou_status_id", unique=true, nullable=false)
    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
    
    @Override
    public String toString(){
        return statusDesp;
    }
}
