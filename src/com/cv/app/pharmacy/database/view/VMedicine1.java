/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * Medicine class. 
 * Database table name is medicine.
 */
@Entity
@Table(name = "v_medicine1")
public class VMedicine1 implements java.io.Serializable {
    private String medId;
    private String medTypeId;
    private String medTypeName;
    private String medName;
    private Integer catId;
    private String catName;
    private Integer brandId;
    private String brandName;
    private String chemicalName;
    private String relStr;
    private boolean active;
    private String shortName;
    private String barcode;
    private String cusGroup;
    private String typeOption;
    private String system;
    
    public VMedicine1(){}

    @Column(name="active")
    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(name="brand_id")
    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }
    
    @Column(name="category_id")
    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    @Column(name="chemical_name", length=45)
    public String getChemicalName() {
        return chemicalName;
    }

    public void setChemicalName(String chemicalName) {
        this.chemicalName = chemicalName;
    }

    @Id 
    @Column(name="med_id", unique=true, nullable=false, length=10)
    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    @Column(name="med_name", nullable=true, length=100, unique=true)
    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    @Column(name="med_type_id")
    public String getMedTypeId() {
        return medTypeId;
    }

    public void setMedTypeId(String medTypeId) {
        this.medTypeId = medTypeId;
    }

    @Column(name="med_rel_str", nullable=true)
    public String getRelStr() {
        return relStr;
    }

    public void setRelStr(String relStr) {
        this.relStr = relStr;
    }
    
    @Override
    public String toString(){
        return medName;
    }

    @Column(name="barcode")
    public String getBarcode() {
        return barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Column(name="short_name", length=45)
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Column(name="item_type_name")
    public String getMedTypeName() {
        return medTypeName;
    }

    public void setMedTypeName(String medTypeName) {
        this.medTypeName = medTypeName;
    }

    @Column(name="cat_name")
    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    @Column(name="brand_name")
    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    @Column(name="cus_grp")
    public String getCusGroup() {
        return cusGroup;
    }

    public void setCusGroup(String cusGroup) {
        this.cusGroup = cusGroup;
    }
    
    @Column(name="type_option", length=15)
    public String getTypeOption() {
        return typeOption;
    }

    public void setTypeOption(String typeOption) {
        this.typeOption = typeOption;
    }

    @Column(name="system_desp")
    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}
