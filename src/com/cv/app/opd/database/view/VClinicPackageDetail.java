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
@Table(name = "v_clinic_package_detail")
public class VClinicPackageDetail implements Serializable {
    
    private Long id;
    private Long pkgId;
    private String itemKey;
    private Float unitQty;
    private String itemUnit;
    private Double qtySmallest;
    private Double sysPrice;
    private Double usrPrice;
    private String itemOption;
    private String itemTypeName;
    private String medId;
    private String medName;
    private String relStr;
    private Double sysAmount;
    private Double usrAmount;
    
    @Id @Column(name="id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="pkg_id")
    public Long getPkgId() {
        return pkgId;
    }

    public void setPkgId(Long pkgId) {
        this.pkgId = pkgId;
    }

    @Column(name="item_key")
    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    @Column(name="unit_qty")
    public Float getUnitQty() {
        return unitQty;
    }

    public void setUnitQty(Float unitQty) {
        this.unitQty = unitQty;
    }

    @Column(name="item_unit")
    public String getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(String itemUnit) {
        this.itemUnit = itemUnit;
    }

    @Column(name="qty_smallest")
    public Double getQtySmallest() {
        return qtySmallest;
    }

    public void setQtySmallest(Double qtySmallest) {
        this.qtySmallest = qtySmallest;
    }

    @Column(name="sys_price")
    public Double getSysPrice() {
        return sysPrice;
    }

    public void setSysPrice(Double sysPrice) {
        this.sysPrice = sysPrice;
    }

    @Column(name="usr_price")
    public Double getUsrPrice() {
        return usrPrice;
    }

    public void setUsrPrice(Double usrPrice) {
        this.usrPrice = usrPrice;
    }

    @Column(name="item_option")
    public String getItemOption() {
        return itemOption;
    }

    public void setItemOption(String itemOption) {
        this.itemOption = itemOption;
    }

    @Column(name="item_type_name")
    public String getItemTypeName() {
        return itemTypeName;
    }

    public void setItemTypeName(String itemTypeName) {
        this.itemTypeName = itemTypeName;
    }

    @Column(name="med_id")
    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    @Column(name="med_name")
    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    @Column(name="med_rel_str")
    public String getRelStr() {
        return relStr;
    }

    public void setRelStr(String relStr) {
        this.relStr = relStr;
    }
    
    @Column(name="sys_amt")
    public Double getSysAmount() {
        return sysAmount;
    }

    public void setSysAmount(Double sysAmount) {
        this.sysAmount = sysAmount;
    }

    @Column(name="usr_amt")
    public Double getUsrAmount() {
        return usrAmount;
    }

    public void setUsrAmount(Double usrAmount) {
        this.usrAmount = usrAmount;
    }
}
