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
@Table(name = "transfer_detail_his")
public class TransferDetailHis implements java.io.Serializable {

    private String tranDetailId;
    private Medicine medicineId;
    private Date expireDate;
    private Float qty;
    private Double price;
    private ItemUnit unit;
    private Integer uniqueId;
    private Float smallestQty;
    private String inHandQtyStr;
    private Float inHandQtySmall;
    private String balQtyStr;
    private Double amount;
    private Float focQty;
    private ItemUnit focItemUnit;
    private Float focSmallestQty;
    private Float itemDiscP;
    private Float itemDiscA;
    private String vouNo;
    
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

    @Column(name = "tran_price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name = "tran_qty")
    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    @Id
    @Column(name = "tran_detail_id", unique = true, nullable = false, length=25)
    public String getTranDetailId() {
        return tranDetailId;
    }

    public void setTranDetailId(String tranDetailId) {
        this.tranDetailId = tranDetailId;
    }

    @ManyToOne
    @JoinColumn(name = "item_unit")
    public ItemUnit getUnit() {
        return unit;
    }

    public void setUnit(ItemUnit unit) {
        this.unit = unit;
    }

    @Column(name = "unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name = "tran_smallest_qty")
    public Float getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Float smallestQty) {
        this.smallestQty = smallestQty;
    }

    @Column(name="balance_qty_str", length=50)
    public String getBalQtyStr() {
        return balQtyStr;
    }

    public void setBalQtyStr(String balQtyStr) {
        this.balQtyStr = balQtyStr;
    }

    @Column(name="inhand_qty_str", length=50)
    public String getInHandQtyStr() {
        return inHandQtyStr;
    }

    public void setInHandQtyStr(String inHandQtyStr) {
        this.inHandQtyStr = inHandQtyStr;
    }

    @Column(name="inhand_qty_small")
    public Float getInHandQtySmall() {
        return inHandQtySmall;
    }

    public void setInHandQtySmall(Float inHandQtySmall) {
        this.inHandQtySmall = inHandQtySmall;
    }

    @Column(name = "amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Column(name="tran_foc_qty")
    public Float getFocQty() {
        return focQty;
    }

    public void setFocQty(Float focQty) {
        this.focQty = focQty;
    }

    @ManyToOne
    @JoinColumn(name = "foc_item_unit")
    public ItemUnit getFocItemUnit() {
        return focItemUnit;
    }

    public void setFocItemUnit(ItemUnit focItemUnit) {
        this.focItemUnit = focItemUnit;
    }

    @Column(name="foc_smallest_qty")
    public Float getFocSmallestQty() {
        return focSmallestQty;
    }

    public void setFocSmallestQty(Float focSmallestQty) {
        this.focSmallestQty = focSmallestQty;
    }

    @Column(name="item_disc_p")
    public Float getItemDiscP() {
        return itemDiscP;
    }

    public void setItemDiscP(Float itemDiscP) {
        this.itemDiscP = itemDiscP;
    }

    @Column(name="item_disc_a")
    public Float getItemDiscA() {
        return itemDiscA;
    }

    public void setItemDiscA(Float itemDiscA) {
        this.itemDiscA = itemDiscA;
    }

    @Column(name="vou_no", length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }
}
