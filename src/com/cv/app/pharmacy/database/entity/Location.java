/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;


import java.util.Date;
import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;
/**
 *
 * @author WSwe
 */
@Entity
@Table(name="location")
public class Location implements java.io.Serializable{
    private Integer locationId;
    private String locationName;
    private Integer parent;
    private boolean calcStock;
    private Integer locationType;
    private String accDeptCode; //For sale
    private String accountCode; //For sale
    private String purAccDeptCode;
    private String purAccountCode;
    private String retInAccDeptCode;
    private String retInAccountCode;
    private String retOutAccDeptCode;
    private String retOutAccountCode;
    private String dmgAccDeptCode;
    private String dmgAccountCode;
    private String adjAccDeptCode;
    private String adjAccountCode;
    private String issuAccDeptCode;
    private String issuAccountCode;
    private String saleDiscAcc;
    private String salePaidAcc;
    private String saleTaxAcc;
    private String saleBalAcc;
    private String saleIPDAcc;
    private String saleIPDDept;
    private String retinPaidAcc;
    private String retinBalAcc;
    private String retinIPDAcc;
    private String retinIPDDept;
    private String purDiscAcc;
    private String purPaidAcc;
    private String purTaxAcc;
    private String purBalAcc;
    private String retoutPaidAcc;
    private String retoutBalAcc;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
    private String machineId;
    
    public Location(){}
    
    public Location(int locationId, String locationName){
        this.locationId = locationId;
        this.locationName = locationName;
    }
    
    @Id 
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="location_id", unique=true, nullable=false)
    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @Column(name="location_name", nullable=false, length=50, unique=true)
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Column(name="parent")
    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }
    
    @Override
    public String toString(){
        return locationName;
    }
    
    @Override
    public boolean equals(Object o){
        boolean status = false;
        
        if(o instanceof Location){
            if(locationId == ((Location)o).locationId){
                status = true;
            }
        }
        
        return status;
    }

    @Column(name="calc_stock")
    public boolean isCalcStock() {
        return calcStock;
    }

    public void setCalcStock(boolean calcStock) {
        this.calcStock = calcStock;
    }

    @Column(name="location_type")
    public Integer getLocationType() {
        return locationType;
    }

    public void setLocationType(Integer locationType) {
        this.locationType = locationType;
    }

    @Column(name="acc_dept_code", length=15)
    public String getAccDeptCode() {
        return accDeptCode;
    }

    public void setAccDeptCode(String accDeptCode) {
        this.accDeptCode = accDeptCode;
    }

    @Column(name="account_code", length=15)
    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    @Column(name="pur_dept_code", length=15)
    public String getPurAccDeptCode() {
        return purAccDeptCode;
    }

    public void setPurAccDeptCode(String purAccDeptCode) {
        this.purAccDeptCode = purAccDeptCode;
    }

    @Column(name="pur_account_code", length=15)
    public String getPurAccountCode() {
        return purAccountCode;
    }

    public void setPurAccountCode(String purAccountCode) {
        this.purAccountCode = purAccountCode;
    }

    @Column(name="retin_dept_code", length=15)
    public String getRetInAccDeptCode() {
        return retInAccDeptCode;
    }

    public void setRetInAccDeptCode(String retInAccDeptCode) {
        this.retInAccDeptCode = retInAccDeptCode;
    }

    @Column(name="retin_account_code", length=15)
    public String getRetInAccountCode() {
        return retInAccountCode;
    }

    public void setRetInAccountCode(String retInAccountCode) {
        this.retInAccountCode = retInAccountCode;
    }

    @Column(name="retout_dept_code", length=15)
    public String getRetOutAccDeptCode() {
        return retOutAccDeptCode;
    }

    public void setRetOutAccDeptCode(String retOutAccDeptCode) {
        this.retOutAccDeptCode = retOutAccDeptCode;
    }

    @Column(name="retout_account_code", length=15)
    public String getRetOutAccountCode() {
        return retOutAccountCode;
    }

    public void setRetOutAccountCode(String retOutAccountCode) {
        this.retOutAccountCode = retOutAccountCode;
    }

    @Column(name="damage_dept_code", length=15)
    public String getDmgAccDeptCode() {
        return dmgAccDeptCode;
    }

    public void setDmgAccDeptCode(String dmgAccDeptCode) {
        this.dmgAccDeptCode = dmgAccDeptCode;
    }

    @Column(name="damage_account_code", length=15)
    public String getDmgAccountCode() {
        return dmgAccountCode;
    }

    public void setDmgAccountCode(String dmgAccountCode) {
        this.dmgAccountCode = dmgAccountCode;
    }

    @Column(name="adj_dept_code", length=15)
    public String getAdjAccDeptCode() {
        return adjAccDeptCode;
    }

    public void setAdjAccDeptCode(String adjAccDeptCode) {
        this.adjAccDeptCode = adjAccDeptCode;
    }

    @Column(name="adj_account_code", length=15)
    public String getAdjAccountCode() {
        return adjAccountCode;
    }

    public void setAdjAccountCode(String adjAccountCode) {
        this.adjAccountCode = adjAccountCode;
    }

    @Column(name="issu_dept_code", length=15)
    public String getIssuAccDeptCode() {
        return issuAccDeptCode;
    }

    public void setIssuAccDeptCode(String issuAccDeptCode) {
        this.issuAccDeptCode = issuAccDeptCode;
    }

    @Column(name="issu_account_code", length=15)
    public String getIssuAccountCode() {
        return issuAccountCode;
    }

    public void setIssuAccountCode(String issuAccountCode) {
        this.issuAccountCode = issuAccountCode;
    }

    @Column(name="sale_disc_acc", length=15)
    public String getSaleDiscAcc() {
        return saleDiscAcc;
    }

    public void setSaleDiscAcc(String saleDiscAcc) {
        this.saleDiscAcc = saleDiscAcc;
    }

    @Column(name="sale_paid_acc", length=15)
    public String getSalePaidAcc() {
        return salePaidAcc;
    }

    public void setSalePaidAcc(String salePaidAcc) {
        this.salePaidAcc = salePaidAcc;
    }

    @Column(name="sale_tax_acc", length=15)
    public String getSaleTaxAcc() {
        return saleTaxAcc;
    }

    public void setSaleTaxAcc(String saleTaxAcc) {
        this.saleTaxAcc = saleTaxAcc;
    }

    @Column(name="sale_bal_acc", length=15)
    public String getSaleBalAcc() {
        return saleBalAcc;
    }

    public void setSaleBalAcc(String saleBalAcc) {
        this.saleBalAcc = saleBalAcc;
    }

    @Column(name="sale_ipd_acc", length=15)
    public String getSaleIPDAcc() {
        return saleIPDAcc;
    }

    public void setSaleIPDAcc(String saleIPDAcc) {
        this.saleIPDAcc = saleIPDAcc;
    }

    @Column(name="sale_ipd_dept", length=15)
    public String getSaleIPDDept() {
        return saleIPDDept;
    }

    public void setSaleIPDDept(String saleIPDDept) {
        this.saleIPDDept = saleIPDDept;
    }

    @Column(name="retin_paid_acc", length=15)
    public String getRetinPaidAcc() {
        return retinPaidAcc;
    }

    public void setRetinPaidAcc(String retinPaidAcc) {
        this.retinPaidAcc = retinPaidAcc;
    }

    @Column(name="retin_bal_acc", length=15)
    public String getRetinBalAcc() {
        return retinBalAcc;
    }

    public void setRetinBalAcc(String retinBalAcc) {
        this.retinBalAcc = retinBalAcc;
    }

    @Column(name="retin_ipd_acc", length=15)
    public String getRetinIPDAcc() {
        return retinIPDAcc;
    }

    public void setRetinIPDAcc(String retinIPDAcc) {
        this.retinIPDAcc = retinIPDAcc;
    }

    @Column(name="retin_ipd_dept", length=15)
    public String getRetinIPDDept() {
        return retinIPDDept;
    }

    public void setRetinIPDDept(String retinIPDDept) {
        this.retinIPDDept = retinIPDDept;
    }

    @Column(name="pur_disc_acc", length=15)
    public String getPurDiscAcc() {
        return purDiscAcc;
    }

    public void setPurDiscAcc(String purDiscAcc) {
        this.purDiscAcc = purDiscAcc;
    }

    @Column(name="pur_paid_acc", length=15)
    public String getPurPaidAcc() {
        return purPaidAcc;
    }

    public void setPurPaidAcc(String purPaidAcc) {
        this.purPaidAcc = purPaidAcc;
    }

    @Column(name="pur_tax_acc", length=15)
    public String getPurTaxAcc() {
        return purTaxAcc;
    }

    public void setPurTaxAcc(String purTaxAcc) {
        this.purTaxAcc = purTaxAcc;
    }

    @Column(name="pur_bal_acc", length=15)
    public String getPurBalAcc() {
        return purBalAcc;
    }

    public void setPurBalAcc(String purBalAcc) {
        this.purBalAcc = purBalAcc;
    }

    @Column(name="retout_paid_acc", length=15)
    public String getRetoutPaidAcc() {
        return retoutPaidAcc;
    }

    public void setRetoutPaidAcc(String retoutPaidAcc) {
        this.retoutPaidAcc = retoutPaidAcc;
    }

    @Column(name="retout_bal_acc", length=15)
    public String getRetoutBalAcc() {
        return retoutBalAcc;
    }

    public void setRetoutBalAcc(String retoutBalAcc) {
        this.retoutBalAcc = retoutBalAcc;
    }

    @Column(name="created_by", length=15)
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_date")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name="updated_by", length=15)
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
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

    @Column(name="machine_id", length=15)
    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
}
