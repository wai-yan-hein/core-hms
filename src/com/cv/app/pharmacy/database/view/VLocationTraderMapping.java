/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import com.cv.app.pharmacy.database.entity.LocationTraderMappingKey;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "v_location_trader_mapping")
public class VLocationTraderMapping implements java.io.Serializable {
    
    private LocationTraderMappingKey key;
    private String locationName;
    private String locationParent;
    private Boolean calcStock;
    private Integer locationType;
    private String traderType;
    private Boolean tActive;
    private Integer townshipId;
    private String traderName;
    private Integer creditDays;
    private Double creditLimit;
    private String traderParent;
    private Integer priceTypeId;
    private String groupId;
    private String accountId;
    private Integer businessTypeId;
    private String groupName;
    private String townshipName;
    private Boolean mapStatus;
    private String stuNo;
    //private String sysP;
    
    @EmbeddedId
    public LocationTraderMappingKey getKey() {
        return key;
    }

    public void setKey(LocationTraderMappingKey key) {
        this.key = key;
    }

    @Column(name="location_name")
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Column(name="location_parent")
    public String getLocationParent() {
        return locationParent;
    }

    public void setLocationParent(String locationParent) {
        this.locationParent = locationParent;
    }

    @Column(name="calc_stock")
    public Boolean getCalcStock() {
        return calcStock;
    }

    public void setCalcStock(Boolean calcStock) {
        this.calcStock = calcStock;
    }

    @Column(name="location_type")
    public Integer getLocationType() {
        return locationType;
    }

    public void setLocationType(Integer locationType) {
        this.locationType = locationType;
    }

    @Column(name="discriminator")
    public String getTraderType() {
        return traderType;
    }

    public void setTraderType(String traderType) {
        this.traderType = traderType;
    }

    @Column(name="active")
    public Boolean gettActive() {
        return tActive;
    }

    public void settActive(Boolean tActive) {
        this.tActive = tActive;
    }

    @Column(name="township")
    public Integer getTownshipId() {
        return townshipId;
    }

    public void setTownshipId(Integer townshipId) {
        this.townshipId = townshipId;
    }

    @Column(name="trader_name")
    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    @Column(name="credit_days")
    public Integer getCreditDays() {
        return creditDays;
    }

    public void setCreditDays(Integer creditDays) {
        this.creditDays = creditDays;
    }

    @Column(name="credit_limit")
    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    @Column(name="trader_parent")
    public String getTraderParent() {
        return traderParent;
    }

    public void setTraderParent(String traderParent) {
        this.traderParent = traderParent;
    }

    @Column(name="type_id")
    public Integer getPriceTypeId() {
        return priceTypeId;
    }

    public void setPriceTypeId(Integer priceTypeId) {
        this.priceTypeId = priceTypeId;
    }

    @Column(name="group_id")
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Column(name="account_id")
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Column(name="business_id")
    public Integer getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(Integer businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    @Column(name="group_name")
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Column(name="township_name")
    public String getTownshipName() {
        return townshipName;
    }

    public void setTownshipName(String townshipName) {
        this.townshipName = townshipName;
    }

    @Column(name="map_status")
    public Boolean getMapStatus() {
        return mapStatus;
    }

    public void setMapStatus(Boolean mapStatus) {
        this.mapStatus = mapStatus;
    }

    @Column(name="stu_no")
    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }

    /*@Column(name="sys_p")
    public String getSysP() {
        return sysP;
    }

    public void setSysP(String sysP) {
        this.sysP = sysP;
    }*/
    
}
