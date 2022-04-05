/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author ACER
 */
@Entity
@Table(name="v_stock_op")
public class VStockOpening implements java.io.Serializable{
    private String opDetailId;
    private String opId;
    private Date opDate;
    private String remark;
    private Integer sessionId;
    private Integer locationId;
    private Date expDate;
    private Float opUnitQty;
    private Float opSmallestQty;
    private Integer uniqueId;
    private String medId;
    private String unitId;
    private Float costPrice;

    @Id @Column(name="op_detail_id")
    public String getOpDetailId() {
        return opDetailId;
    }

    public void setOpDetailId(String opDetailId) {
        this.opDetailId = opDetailId;
    }

    @Column(name="op_id")
    public String getOpId() {
        return opId;
    }

    public void setOpId(String opId) {
        this.opId = opId;
    }

    @Column(name="op_date")
    public Date getOpDate() {
        return opDate;
    }

    public void setOpDate(Date opDate) {
        this.opDate = opDate;
    }

    @Column(name="remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name="session_id")
    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    @Column(name="location")
    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @Column(name="expire_date")
    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    @Column(name="op_qty")
    public Float getOpUnitQty() {
        return opUnitQty;
    }

    public void setOpUnitQty(Float opUnitQty) {
        this.opUnitQty = opUnitQty;
    }

    @Column(name="op_smallest_qty")
    public Float getOpSmallestQty() {
        return opSmallestQty;
    }

    public void setOpSmallestQty(Float opSmallestQty) {
        this.opSmallestQty = opSmallestQty;
    }

    @Column(name="unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name="med_id")
    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    @Column(name="item_unit")
    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    @Column(name="cost_price")
    public Float getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Float costPrice) {
        this.costPrice = costPrice;
    }
}
