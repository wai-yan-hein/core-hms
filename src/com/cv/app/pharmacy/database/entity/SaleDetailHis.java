/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "sale_detail_his")
public class SaleDetailHis implements java.io.Serializable {

    private Medicine medId;
    private Date expireDate;
    private Double price;
    private Float quantity;
    private ItemUnit unitId;
    private ChargeType chargeId;
    private Double discount;
    private Double amount;
    private Float saleSmallestQty;
    private Integer uniqueId;
    private Double salePriceP; //For parent currency
    private Double itemDiscP; //For parent currency
    private Double saleAmtP; //For parent currency
    private Float focQty;
    private ItemUnit focUnit;
    private Float focSmallestQty;
    private Location location;
    
    /*private String promoDesp;
    private Date promoStartDate;
    private Date promoEndDate;
    private Float promoGivePercent;
    private Float promoGetPercent;
    private boolean promoGetComplete;*/
    
    private String imei1;
    private String imei2;
    private String sdNo;
    
    private Float balSmallestQty;
    private String balQtyInString;
    private Double costPrice;
    private Double difference;
    private Double smallestCost;
    private Double unitCost;
    private String costUnit;
    private String vouNo;
    private String strSaleDetailId;
    
    public SaleDetailHis() {
    }

    @Column(name = "sale_amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @ManyToOne
    @JoinColumn(name = "charge_type")
    public ChargeType getChargeId() {
        return chargeId;
    }

    public void setChargeId(ChargeType chargeId) {
        this.chargeId = chargeId;
    }

    @Column(name = "item_discount")
    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
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
    public Medicine getMedId() {
        return medId;
    }

    public void setMedId(Medicine medId) {
        this.medId = medId;
    }

    @Column(name = "sale_price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name = "sale_qty")
    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    @ManyToOne
    @JoinColumn(name = "item_unit")
    public ItemUnit getUnitId() {
        return unitId;
    }

    public void setUnitId(ItemUnit unitId) {
        this.unitId = unitId;
    }

    @Column(name = "sale_smallest_qty")
    public Float getSaleSmallestQty() {
        return saleSmallestQty;
    }

    public void setSaleSmallestQty(Float saleSmallestQty) {
        this.saleSmallestQty = saleSmallestQty;
    }

    @Column(name = "unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public boolean equals(Object o) {
        SaleDetailHis tmpObj = (SaleDetailHis) o;

        if(medId.getMedId() == null || tmpObj.medId.getMedId() == null){
            return true;
        }
        
        if (this.medId.getMedId().equals(tmpObj.getMedId().getMedId())
                && this.chargeId.getChargeTypeId().equals(tmpObj.getChargeId().getChargeTypeId())
                && this.saleSmallestQty.equals(tmpObj.getSaleSmallestQty())) {
            return true;
        } else {
            return false;
        }
    }

    @Column(name="sale_price_p")
    public Double getSalePriceP() {
        return salePriceP;
    }

    public void setSalePriceP(Double salePriceP) {
        this.salePriceP = salePriceP;
    }

    @Column(name="item_discount_p")
    public Double getItemDiscP() {
        return itemDiscP;
    }

    public void setItemDiscP(Double itemDiscP) {
        this.itemDiscP = itemDiscP;
    }

    @Column(name="sale_amount_p")
    public Double getSaleAmtP() {
        return saleAmtP;
    }

    public void setSaleAmtP(Double saleAmtP) {
        this.saleAmtP = saleAmtP;
    }

    @Column(name="foc_qty")
    public Float getFocQty() {
        return focQty;
    }

    public void setFocQty(Float focQty) {
        this.focQty = focQty;
    }

    @ManyToOne
    @JoinColumn(name="foc_unit")
    public ItemUnit getFocUnit() {
        return focUnit;
    }

    public void setFocUnit(ItemUnit focUnit) {
        this.focUnit = focUnit;
    }

    @Column(name="foc_smallest_qty")
    public Float getFocSmallestQty() {
        return focSmallestQty;
    }

    public void setFocSmallestQty(Float focSmallestQty) {
        this.focSmallestQty = focSmallestQty;
    }
    
    @ManyToOne
    @JoinColumn(name="location_id")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    
   /* @Column(name="promo_desp", length=500)
    public String getPromoDesp() {
        return promoDesp;
    }

    public void setPromoDesp(String promoDesp) {
        this.promoDesp = promoDesp;
    }

    @Column(name="promo_start_date")
    @Temporal(TemporalType.DATE)
    public Date getPromoStartDate() {
        return promoStartDate;
    }

    public void setPromoStartDate(Date promoStartDate) {
        this.promoStartDate = promoStartDate;
    }

    @Column(name="promo_end_date")
    @Temporal(TemporalType.DATE)
    public Date getPromoEndDate() {
        return promoEndDate;
    }

    public void setPromoEndDate(Date promoEndDate) {
        this.promoEndDate = promoEndDate;
    }

    @Column(name="promo_give_percent")
    public Float getPromoGivePercent() {
        return promoGivePercent;
    }

    public void setPromoGivePercent(Float promoGivePercent) {
        this.promoGivePercent = promoGivePercent;
    }

    @Column(name="promo_get_percent")
    public Float getPromoGetPercent() {
        return promoGetPercent;
    }

    public void setPromoGetPercent(Float promoGetPercent) {
        this.promoGetPercent = promoGetPercent;
    }

    @Column(name="promo_get_complete")
    public boolean isPromoGetComplete() {
        return promoGetComplete;
    }

    public void setPromoGetComplete(boolean promoGetComplete) {
        this.promoGetComplete = promoGetComplete;
    }*/

    @Column(name="imei1", length=50)
    public String getImei1() {
        return imei1;
    }

    public void setImei1(String imei1) {
        this.imei1 = imei1;
    }

    @Column(name="imei2", length=50)
    public String getImei2() {
        return imei2;
    }

    public void setImei2(String imei2) {
        this.imei2 = imei2;
    }

    @Column(name="sd_no", length=50)
    public String getSdNo() {
        return sdNo;
    }

    public void setSdNo(String sdNo) {
        this.sdNo = sdNo;
    }

    @Column(name="bal_qty_smallest")
    public Float getBalSmallestQty() {
        return balSmallestQty;
    }

    public void setBalSmallestQty(Float balSmallestQty) {
        this.balSmallestQty = balSmallestQty;
    }

    @Column(name="bal_qty_str", length=50)
    public String getBalQtyInString() {
        return balQtyInString;
    }

    public void setBalQtyInString(String balQtyInString) {
        this.balQtyInString = balQtyInString;
    }

    @Column(name="cost_price")
    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    @Column(name="difference")
    public Double getDifference() {
        return difference;
    }

    public void setDifference(Double difference) {
        this.difference = difference;
    }

    @Column(name="smallest_cost")
    public Double getSmallestCost() {
        return smallestCost;
    }

    public void setSmallestCost(Double smallestCost) {
        this.smallestCost = smallestCost;
    }

    @Column(name="unit_cost")
    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }

    @Column(name="cost_unit")
    public String getCostUnit() {
        return costUnit;
    }

    public void setCostUnit(String costUnit) {
        this.costUnit = costUnit;
    }

    @Column(name="vou_no", length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Id
    @Column(name = "sale_detail_id", unique = true, nullable = false, length=25)
    public String getSaleDetailId() {
        return strSaleDetailId;
    }

    public void setSaleDetailId(String strSaleDetailId) {
        this.strSaleDetailId = strSaleDetailId;
    }
    
}
