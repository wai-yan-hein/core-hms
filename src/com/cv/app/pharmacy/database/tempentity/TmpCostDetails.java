/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="tmp_cost_details")
public class TmpCostDetails implements Serializable{
    private Integer tranId;
    private String tranOption;
    private String itemId;
    private Date tranDate;
    private Integer ttlQty;
    private Double costPrice;
    private Double smallestCost;
    private String itemUnit;
    private String userId;

    @Id
    @Column(name="tran_id")
    public Integer getTranId() {
        return tranId;
    }

    public void setTranId(Integer tranId) {
        this.tranId = tranId;
    }

    @Column(name="tran_option", length=45)
    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }

    @Column(name="item_id", length=15)
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="tran_date")
    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    @Column(name="ttl_qty")
    public Integer getTtlQty() {
        return ttlQty;
    }

    public void setTtlQty(Integer ttlQty) {
        this.ttlQty = ttlQty;
    }

    @Column(name="cost_price")
    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    @Column(name="smallest_cost")
    public Double getSmallestCost() {
        return smallestCost;
    }

    public void setSmallestCost(Double smallestCost) {
        this.smallestCost = smallestCost;
    }

    @Column(name="item_unit", length=15)
    public String getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(String itemUnit) {
        this.itemUnit = itemUnit;
    }

    @Column(name="user_id", length=15)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
