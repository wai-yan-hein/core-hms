/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

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
@Table(name="clinic_package_detail")
public class ClinicPackageDetail implements java.io.Serializable {
    
    private Long id;
    private Long pkgId;
    private String itemKey;
    private Float unitQty;
    private String itemUnit;
    private Double qtySmallest;
    private Double sysPrice;
    private Double usrPrice;
    private Double sysAmount;
    private Double usrAmount;
    private Date updatedDate;
    private String updatedBy;
    
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="id", unique=true, nullable=false)
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

    @Column(name="item_key", length=25)
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

    @Column(name="item_unit", length=15)
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Column(name="updated_by", length=15)
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
