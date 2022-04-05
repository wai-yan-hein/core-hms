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
@Table(name = "ret_out_detail_his")
public class RetOutDetailHis implements java.io.Serializable{
    private String retOutDetailId;
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
    private Double amountP;
    //============================
    private String vouNo;
    
    @Column(name="ret_out_amount")
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

    @Column(name="ret_out_price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name="ret_out_qty")
    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    @Id
    @Column(name="ret_out_detail_id", unique=true, nullable=false, length=25)
    public String getRetOutDetailId() {
        return retOutDetailId;
    }

    public void setRetOutDetailId(String retOutDetailId) {
        this.retOutDetailId = retOutDetailId;
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

    @Column(name="ret_out_smallest_qty")
    public Float getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Float smallestQty) {
        this.smallestQty = smallestQty;
    }

    @Column(name="ret_out_price_p")
    public Double getPriceP() {
        return priceP;
    }

    public void setPriceP(Double priceP) {
        this.priceP = priceP;
    }

    @Column(name="ret_out_amount_p")
    public Double getAmountP() {
        return amountP;
    }

    public void setAmountP(Double amountP) {
        this.amountP = amountP;
    }
    
    @Column(name="vou_no", length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }
}
