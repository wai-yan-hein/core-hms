/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "ret_in_detail_his")
public class RetInDetailHis implements java.io.Serializable{
    private String retInDetailId;
    private Medicine medicineId;
    private Date expireDate;
    private Float qty;
    private Double price;
    private ItemUnit unit;
    private Double amount;
    private Integer uniqueId;
    private Float smallestQty;
    //For parent currency
    private Double priceP;
    private Double amtP;
    //===============================
    private String saleIvId;
    private String vouNo;
    private Double costPrice;
    
    @Column(name="ret_in_amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="expire_date")
    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @ManyToOne
    @JoinColumn(name="med_id")
    public Medicine getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Medicine medicineId) {
        this.medicineId = medicineId;
    }

    @Column(name="ret_in_price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name="ret_in_qty")
    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    @Id
    @Column(name="ret_in_detail_id", unique=true, nullable=false, length=25)
    public String getRetInDetailId() {
        return retInDetailId;
    }

    public void setRetInDetailId(String retInDetailId) {
        this.retInDetailId = retInDetailId;
    }

    @ManyToOne
    @JoinColumn(name="item_unit")
    public ItemUnit getUnit() {
        return unit;
    }

    public void setUnit(ItemUnit unit) {
        this.unit = unit;
    }

    @Column(name="unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name="ret_in_smallest_qty")
    public Float getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Float smallestQty) {
        this.smallestQty = smallestQty;
    }

    @Column(name="ret_in_price_p")
    public Double getPriceP() {
        return priceP;
    }

    public void setPriceP(Double priceP) {
        this.priceP = priceP;
    }

    @Column(name="ret_in_amt_p")
    public Double getAmtP() {
        return amtP;
    }

    public void setAmtP(Double amtP) {
        this.amtP = amtP;
    }

    @Column(name="sale_inv_id")
    public String getSaleIvId() {
        return saleIvId;
    }

    public void setSaleIvId(String saleIvId) {
        this.saleIvId = saleIvId;
    }
    
    @Column(name="vou_no", length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name="cost_price")
    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }
}
