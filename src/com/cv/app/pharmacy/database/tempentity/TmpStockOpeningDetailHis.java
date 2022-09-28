/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import java.util.Date;
import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "tmp_stock_op_detail_his")
public class TmpStockOpeningDetailHis implements java.io.Serializable{
    private Long opDetailId;
    private Medicine med;
    private Date expDate;
    private Float qty;
    private ItemUnit unit;
    private Float smallestQty;
    private Integer uniqueId;
    private Double costPrice;
    private Integer tranId;
    private String userId;
    
    @Temporal(TemporalType.DATE)
    @Column(name="expire_date")
    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    @ManyToOne
    @JoinColumn(name="med_id")
    public Medicine getMed() {
        return med;
    }

    public void setMed(Medicine med) {
        this.med = med;
    }
    
    @Column(name="op_detail_id")
    public Long getOpDetailId() {
        return opDetailId;
    }

    public void setOpDetailId(Long opDetailId) {
        this.opDetailId = opDetailId;
    }

    @Column(name="op_qty")
    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    @Column(name="op_smallest_qty")
    public Float getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Float smallestQty) {
        this.smallestQty = smallestQty;
    }

    @Column(name="unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @ManyToOne
    @JoinColumn(name="item_unit")
    public ItemUnit getUnit() {
        return unit;
    }

    public void setUnit(ItemUnit unit) {
        this.unit = unit;
    }
    
    @Column(name="cost_price")
    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="tran_id", unique=true, nullable=false)
    public Integer getTranId() {
        return tranId;
    }

    public void setTranId(Integer tranId) {
        this.tranId = tranId;
    }

    @Column(name="user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
