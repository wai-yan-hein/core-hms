/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.view;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author user
 */
@Entity
@Table(name = "v_dc_cat_service")
public class VDCGroupService implements java.io.Serializable {

    private Integer srvId;
    private Integer catId;
    private String catName;
    private String srvName;
    private Boolean actStatus;

    @Id
    @Column(name = "service_id")
    public Integer getSrvId() {
        return srvId;
    }

    public void setSrvId(Integer srvId) {
        this.srvId = srvId;
    }

    @Column(name = "cat_id")
    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    @Column(name = "cat_name")

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    @Column(name = "service_name")
    public String getSrvName() {
        return srvName;
    }

    public void setSrvName(String srvName) {
        this.srvName = srvName;
    }

    @Column(name = "act_status")
    public Boolean getActStatus() {
        return actStatus;
    }

    public void setActStatus(Boolean actStatus) {
        this.actStatus = actStatus;
    }
}
