/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.helper;

/**
 *
 * @author ACER
 */
public class OrderScratchData {
    private String medId;
    private String medName;
    private Boolean active;
    private Integer catId;
    private String catName;
    private Integer sysId;
    private String sysName;
    private Integer brandId;
    private String brandName;
    private String supId;
    private String supName;
    private String phoneNo;

    public OrderScratchData(String medId, String medName, Boolean active, 
            Integer catId, String catName, Integer sysId, String sysName, 
            String brandName, String supId, String supName, String phoneNo) {
        this.medId = medId;
        this.medName = medName;
        this.active = active;
        this.catId = catId;
        this.catName = catName;
        this.sysId = sysId;
        this.sysName = sysName;
        this.brandName = brandName;
        this.supId = supId;
        this.supName = supName;
        this.phoneNo = phoneNo;
    }

    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public Integer getSysId() {
        return sysId;
    }

    public void setSysId(Integer sysId) {
        this.sysId = sysId;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getSupId() {
        return supId;
    }

    public void setSupId(String supId) {
        this.supId = supId;
    }

    public String getSupName() {
        return supName;
    }

    public void setSupName(String supName) {
        this.supName = supName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
