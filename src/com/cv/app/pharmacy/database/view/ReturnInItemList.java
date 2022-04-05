/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import com.cv.app.pharmacy.database.entity.ItemUnit;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "v_sale")
public class ReturnInItemList implements java.io.Serializable{
    private ReturnInItemListKey key;
    private String remark;
    private Date saleDate;
    private String currency;
    private Float saleQty;
    private Float saleQtySmallest;
    private ItemUnit saleUnit;
    private Double salePrice;
    private String traderId;
    private Date expDate;
    private boolean deleted;
    private Float qtyFoc;
    private String regNo;
    
    public ReturnInItemList(){
        key = new ReturnInItemListKey();
    }
    
    @EmbeddedId
    public ReturnInItemListKey getKey() {
        return key;
    }

    public void setKey(ReturnInItemListKey key) {
        this.key = key;
    }
    
    @Column(name="remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="sale_date", nullable=false)
    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    @Column(name="currency_id")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name="sale_qty")
    public Float getSaleQty() {
        return saleQty;
    }

    public void setSaleQty(Float saleQty) {
        this.saleQty = saleQty;
    }

    @Column(name="foc_qty")
    public Float getQtyFoc() {
        return qtyFoc;
    }

    public void setQtyFoc(Float qtyFoc) {
        this.qtyFoc = qtyFoc;
    }

    @Column(name="sale_smallest_qty")
    public Float getSaleQtySmallest() {
        return saleQtySmallest;
    }

    public void setSaleQtySmallest(Float saleQtySmallest) {
        this.saleQtySmallest = saleQtySmallest;
    }

    @ManyToOne
    @JoinColumn(name="item_unit")
    public ItemUnit getSaleUnit() {
        return saleUnit;
    }

    public void setSaleUnit(ItemUnit saleUnit) {
        this.saleUnit = saleUnit;
    }

    @Column(name="sale_price")
    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    @Column(name="cus_id")
    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="expire_date", nullable=false)
    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    @Column(name="deleted")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name="reg_no")
    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }
}
