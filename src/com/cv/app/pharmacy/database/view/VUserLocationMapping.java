/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import com.cv.app.pharmacy.database.entity.UserLocationMappingKey;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="v_user_location_mapping")
public class VUserLocationMapping implements java.io.Serializable {
    private UserLocationMappingKey key;
    private Boolean isAllowSale;
    private Boolean isAllowPur;
    private Boolean isAllowRetIn;
    private Boolean isAllowRetOut;
    private Boolean isAllowTranIn;
    private Boolean isAllowTranOut;
    private Boolean isAllowDmg;
    private Boolean isAllowPriceChange;
    private Boolean isAllowAdj;
    private Boolean isAllowStkOp; //Stock Opening
    private Boolean isAllowCusPayVou;
    private Boolean isAllowSessCheck; //Session Check
    private Boolean isAllowStkIssue; //Stock Issue
    private Boolean isAllowStkReceive; //Stock Receive
    private Boolean isAllowReOrder; //Re-Order Level
    private Date updatedDate;
    
    private Boolean userStatus;
    private String userName;
    private Integer roleId;
    private String locationName;
    private Integer parentLocation;
    private Boolean isCalcStock;
    private Integer locationType;
    
    @EmbeddedId
    public UserLocationMappingKey getKey() {
        return key;
    }

    public void setKey(UserLocationMappingKey key) {
        this.key = key;
    }

    @Column(name="allow_sale")
    public Boolean getIsAllowSale() {
        return isAllowSale;
    }

    public void setIsAllowSale(Boolean isAllowSale) {
        this.isAllowSale = isAllowSale;
    }

    @Column(name="allow_pur")
    public Boolean getIsAllowPur() {
        return isAllowPur;
    }

    public void setIsAllowPur(Boolean isAllowPur) {
        this.isAllowPur = isAllowPur;
    }

    @Column(name="allow_retin")
    public Boolean getIsAllowRetIn() {
        return isAllowRetIn;
    }

    public void setIsAllowRetIn(Boolean isAllowRetIn) {
        this.isAllowRetIn = isAllowRetIn;
    }

    @Column(name="allow_retout")
    public Boolean getIsAllowRetOut() {
        return isAllowRetOut;
    }

    public void setIsAllowRetOut(Boolean isAllowRetOut) {
        this.isAllowRetOut = isAllowRetOut;
    }

    @Column(name="allow_tranin")
    public Boolean getIsAllowTranIn() {
        return isAllowTranIn;
    }

    public void setIsAllowTranIn(Boolean isAllowTranIn) {
        this.isAllowTranIn = isAllowTranIn;
    }

    @Column(name="allow_tranout")
    public Boolean getIsAllowTranOut() {
        return isAllowTranOut;
    }

    public void setIsAllowTranOut(Boolean isAllowTranOut) {
        this.isAllowTranOut = isAllowTranOut;
    }

    @Column(name="allow_dmg")
    public Boolean getIsAllowDmg() {
        return isAllowDmg;
    }

    public void setIsAllowDmg(Boolean isAllowDmg) {
        this.isAllowDmg = isAllowDmg;
    }

    @Column(name="allow_price_change")
    public Boolean getIsAllowPriceChange() {
        return isAllowPriceChange;
    }

    public void setIsAllowPriceChange(Boolean isAllowPriceChange) {
        this.isAllowPriceChange = isAllowPriceChange;
    }

    @Column(name="allow_adj")
    public Boolean getIsAllowAdj() {
        return isAllowAdj;
    }

    public void setIsAllowAdj(Boolean isAllowAdj) {
        this.isAllowAdj = isAllowAdj;
    }

    @Column(name="allow_stk_op")
    public Boolean getIsAllowStkOp() {
        return isAllowStkOp;
    }

    public void setIsAllowStkOp(Boolean isAllowStkOp) {
        this.isAllowStkOp = isAllowStkOp;
    }

    @Column(name="allow_cus_vou_pay")
    public Boolean getIsAllowCusPayVou() {
        return isAllowCusPayVou;
    }

    public void setIsAllowCusPayVou(Boolean isAllowCusPayVou) {
        this.isAllowCusPayVou = isAllowCusPayVou;
    }

    @Column(name="allow_sess_chk")
    public Boolean getIsAllowSessCheck() {
        return isAllowSessCheck;
    }

    public void setIsAllowSessCheck(Boolean isAllowSessCheck) {
        this.isAllowSessCheck = isAllowSessCheck;
    }

    @Column(name="allow_stk_issue")
    public Boolean getIsAllowStkIssue() {
        return isAllowStkIssue;
    }

    public void setIsAllowStkIssue(Boolean isAllowStkIssue) {
        this.isAllowStkIssue = isAllowStkIssue;
    }

    @Column(name="allow_stk_receve")
    public Boolean getIsAllowStkReceive() {
        return isAllowStkReceive;
    }

    public void setIsAllowStkReceive(Boolean isAllowStkReceive) {
        this.isAllowStkReceive = isAllowStkReceive;
    }

    @Column(name="allow_re_order")
    public Boolean getIsAllowReOrder() {
        return isAllowReOrder;
    }

    public void setIsAllowReOrder(Boolean isAllowReOrder) {
        this.isAllowReOrder = isAllowReOrder;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="updated_date")
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Column(name="active")
    public Boolean getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Boolean userStatus) {
        this.userStatus = userStatus;
    }

    @Column(name="user_name")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name="role_id")
    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Column(name="location_name")
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Column(name="parent")
    public Integer getParentLocation() {
        return parentLocation;
    }

    public void setParentLocation(Integer parentLocation) {
        this.parentLocation = parentLocation;
    }

    @Column(name="calc_stock")
    public Boolean getIsCalcStock() {
        return isCalcStock;
    }

    public void setIsCalcStock(Boolean isCalcStock) {
        this.isCalcStock = isCalcStock;
    }

    @Column(name="location_type")
    public Integer getLocationType() {
        return locationType;
    }

    public void setLocationType(Integer locationType) {
        this.locationType = locationType;
    }
}
