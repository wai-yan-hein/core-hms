/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.view;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "v_clinic_package_ttl")
public class VClinicPackageTotal implements Serializable {
    
    private Long pkgId;
    private String itemOption;
    private String typeName;
    private Double sysTotal;
    private Double usrTotal;

    @Column(name="pkg_id")
    public Long getPkgId() {
        return pkgId;
    }

    public void setPkgId(Long pkgId) {
        this.pkgId = pkgId;
    }

    @Column(name="item_option")
    public String getItemOption() {
        return itemOption;
    }

    public void setItemOption(String itemOption) {
        this.itemOption = itemOption;
    }

    @Id @Column(name="item_type_name")
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Column(name="ttl_sys")
    public Double getSysTotal() {
        return sysTotal;
    }

    public void setSysTotal(Double sysTotal) {
        this.sysTotal = sysTotal;
    }

    @Column(name="ttl_usr")
    public Double getUsrTotal() {
        return usrTotal;
    }

    public void setUsrTotal(Double usrTotal) {
        this.usrTotal = usrTotal;
    }
}
