/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

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
@Table(name="user_location_mapping")
public class UserLocationMapping implements java.io.Serializable {
    
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
    
}
