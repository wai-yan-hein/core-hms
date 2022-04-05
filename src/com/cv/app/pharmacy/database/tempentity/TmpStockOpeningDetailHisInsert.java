/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import java.util.Date;
import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "tmp_stock_op_detail_his")
public class TmpStockOpeningDetailHisInsert implements java.io.Serializable{
    private Long opDetailId;
    private Integer tranId;
    private String medId;
    private Date expDate;
    private Integer qty;
    private String unit;
    private Integer smallestQty;
    private Integer uniqueId;
    private Double costPrice;
    private String userId;
    private Integer locationId;
            
    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="tran_id", unique=true, nullable=false)
    public Integer getTranId() {
        return tranId;
    }

    public void setTranId(Integer tranId) {
        this.tranId = tranId;
    }
    
    @Column(name="med_id")
    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }
    
    @Temporal(TemporalType.DATE)
    @Column(name="expire_date")
    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }
    
    @Column(name="op_detail_id")
    public Long getOpDetailId() {
        return opDetailId;
    }

    public void setOpDetailId(Long opDetailId) {
        this.opDetailId = opDetailId;
    }

    @Column(name="op_qty")
    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    @Column(name="op_smallest_qty")
    public Integer getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Integer smallestQty) {
        this.smallestQty = smallestQty;
    }

    @Column(name="unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name="item_unit")
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    @Column(name="cost_price")
    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }
    
    @Column(name="user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name="location_id")
    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
}
