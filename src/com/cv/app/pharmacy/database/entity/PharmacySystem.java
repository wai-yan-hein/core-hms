/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="phar_system")
public class PharmacySystem implements java.io.Serializable{
    private Long id;
    private String systemDesp;
    private Date updatedDate;
    private String mmDesp;
    private String sysType;
    private Long parentId;
    
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="system_desp", length=200)
    public String getSystemDesp() {
        return systemDesp;
    }

    public void setSystemDesp(String systemDesp) {
        this.systemDesp = systemDesp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    @Override
    public String toString(){
        return systemDesp;
    }

    @Column(name="mm_desp", length=255)
    public String getMmDesp() {
        return mmDesp;
    }

    public void setMmDesp(String mmDesp) {
        this.mmDesp = mmDesp;
    }

    @Column(name="sys_type", length=25)
    public String getSysType() {
        return sysType;
    }

    public void setSysType(String sysType) {
        this.sysType = sysType;
    }

    @Column(name="parent_id")
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
