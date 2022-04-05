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
@Table(name = "dmg_detail_his")
public class DamageDetailHis implements java.io.Serializable {

    private String dmgDetailId;
    private Medicine medicineId;
    private Date expireDate;
    private Float qty;
    private ItemUnit unit;
    private Integer uniqueId;
    private Float smallestQty;
    private Double costPrice;
    private Double amount;
    private String vouNo;

    @Id
    @Column(name = "dmg_detail_id", unique = true, nullable = false, length=25)
    public String getDmgDetailId() {
        return dmgDetailId;
    }

    public void setDmgDetailId(String dmgDetailId) {
        this.dmgDetailId = dmgDetailId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @ManyToOne
    @JoinColumn(name = "med_id")
    public Medicine getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(Medicine medicineId) {
        this.medicineId = medicineId;
    }

    @Column(name = "dmg_qty")
    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    @Column(name = "dmg_smallest_qty")
    public Float getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Float smallestQty) {
        this.smallestQty = smallestQty;
    }

    @Column(name = "unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @ManyToOne
    @JoinColumn(name = "item_unit")
    public ItemUnit getUnit() {
        return unit;
    }

    public void setUnit(ItemUnit unit) {
        this.unit = unit;
    }

    @Column(name = "cost_price")
    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    @Column(name = "amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    @Column(name="vou_no", length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

}
