/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * Medicine class. 
 * Database table name is medicine.
 */
@Entity
@Table(name = "medicine")
public class Medicine implements java.io.Serializable {
    private String medId;
    private ItemType medTypeId;
    private String medName;
    private Category catId;
    private ItemBrand brand;
    private String chemicalName;
    private String relStr;
    private boolean active;
    private List<RelationGroup> relationGroupId;
    private Appuser createdBy;
    private Date createdDate;
    private Appuser updatedBy;
    private Date updatedDate;
    private String barcode;
    private String shortName;
    private Double purPrice;
    private ItemUnit purUnit;
    private Date liceneExpDate;
    private String typeOption;
    private String unitSmallest;
    private String unitStr;
    private PharmacySystem ps;
    
    public Medicine(){}

    @Column(name="active")
    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @ManyToOne
    @JoinColumn(name="brand_id")
    public ItemBrand getBrand() {
        return brand;
    }

    public void setBrand(ItemBrand brand) {
        this.brand = brand;
    }
    
    @ManyToOne
    @JoinColumn(name="category_id")
    public Category getCatId() {
        return catId;
    }

    public void setCatId(Category catId) {
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

    @Column(name="med_name", nullable=true, length=255, unique=true)
    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    @ManyToOne
    @JoinColumn(name="med_type_id")
    public ItemType getMedTypeId() {
        return medTypeId;
    }

    public void setMedTypeId(ItemType medTypeId) {
        this.medTypeId = medTypeId;
    }

    @Column(name="med_rel_str", nullable=true)
    public String getRelStr() {
        return relStr;
    }

    public void setRelStr(String relStr) {
        this.relStr = relStr;
    }

    @OneToMany(cascade= CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinTable(name = "med_rel", joinColumns = { @JoinColumn(name = "med_id") }, 
            inverseJoinColumns = { @JoinColumn(name = "rel_group_id") })
    @OrderBy("relUniqueId")
    public List<RelationGroup> getRelationGroupId() {
        return relationGroupId;
    }

    public void setRelationGroupId(List<RelationGroup> relationGroupId) {
        if(relationGroupId != null){
            if(!relationGroupId.isEmpty()){
                this.relationGroupId = relationGroupId;
            }
        }
    }
    
    @Override
    public String toString(){
        return medName;
    }

    @ManyToOne
    @JoinColumn(name="created_by")
    public Appuser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Appuser createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "created_date", insertable=false, updatable=false, 
            columnDefinition="timestamp default current_timestamp")
    @org.hibernate.annotations.Generated(value=GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @ManyToOne
    @JoinColumn(name="updated_by")
    public Appuser getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Appuser updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
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

    @Column(name="pur_price")
    public Double getPurPrice() {
        return purPrice;
    }

    public void setPurPrice(Double purPrice) {
        this.purPrice = purPrice;
    }

    @ManyToOne
    @JoinColumn(name="pur_unit")
    public ItemUnit getPurUnit() {
        return purUnit;
    }

    public void setPurUnit(ItemUnit purUnit) {
        this.purUnit = purUnit;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="licene_exp_date")
    public Date getLiceneExpDate() {
        return liceneExpDate;
    }

    public void setLiceneExpDate(Date liceneExpDate) {
        this.liceneExpDate = liceneExpDate;
    }

    @Column(name="type_option", length=15)
    public String getTypeOption() {
        return typeOption;
    }

    public void setTypeOption(String typeOption) {
        this.typeOption = typeOption;
    }

    @Column(name="unit_smallest", length=500)
    public String getUnitSmallest() {
        return unitSmallest;
    }

    public void setUnitSmallest(String unitSmallest) {
        this.unitSmallest = unitSmallest;
    }

    @Column(name="unit_str", length=500)
    public String getUnitStr() {
        return unitStr;
    }

    public void setUnitStr(String unitStr) {
        this.unitStr = unitStr;
    }

    @ManyToOne
    @JoinColumn(name="phar_sys_id")
    public PharmacySystem getPs() {
        return ps;
    }

    public void setPs(PharmacySystem ps) {
        this.ps = ps;
    }
}
