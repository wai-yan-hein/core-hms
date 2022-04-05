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
 * @author PHSH MDY
 */
@Entity
@Table(name = "opd_cus_lab_group")
public class OPDCusLabGroup implements java.io.Serializable{
    
    private Integer id;
    private String groupName;

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="id", nullable=false, unique=true)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="group_name", length=50)
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    @Override
    public String toString(){
        return groupName;
    }
}
