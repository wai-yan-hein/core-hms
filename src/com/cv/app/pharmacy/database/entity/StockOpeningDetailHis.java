/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import com.cv.app.pharmacy.database.tempentity.TmpStockOpeningDetailHis;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "stock_op_detail_his")
public class StockOpeningDetailHis implements java.io.Serializable{
    private String opDetailId;
    private Medicine med;
    private Date expDate;
    private Float qty;
    private ItemUnit unit;
    private Float smallestQty;
    private Integer uniqueId;
    private Double costPrice;
    private String opId;
    
    public StockOpeningDetailHis(){}
    
    public StockOpeningDetailHis(TmpStockOpeningDetailHis stockOp){
        //opDetailId = stockOp.getOpDetailId();
        med = stockOp.getMed();
        expDate = stockOp.getExpDate();
        qty = stockOp.getQty();
        unit = stockOp.getUnit();
        smallestQty = stockOp.getSmallestQty();
        uniqueId = stockOp.getUniqueId();
        costPrice = stockOp.getCostPrice();
    }
    
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

    @Id
    @Column(name="op_detail_id", unique=true, nullable=false, length=25)
    public String getOpDetailId() {
        return opDetailId;
    }

    public void setOpDetailId(String opDetailId) {
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

    @Column(name="op_id", length=15)
    public String getOpId() {
        return opId;
    }

    public void setOpId(String opId) {
        this.opId = opId;
    }
}
