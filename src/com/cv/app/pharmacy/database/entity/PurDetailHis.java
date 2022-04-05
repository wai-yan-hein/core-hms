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
@Table(name = "pur_detail_his")
public class PurDetailHis implements java.io.Serializable{
    private String purDetailId;
    private Medicine medId;
    private Date expireDate;
    private Double price;
    private Double unitCost;
    private Float quantity;
    private ItemUnit unitId;
    private Double discount1;
    private Double discount2;
    private Double amount;
    private Float purSmallestQty;
    private Float focQty;
    private ItemUnit focUnitId;
    private Float focSmallestQty;
    private Integer uniqueId;
    private ChargeType chargeId;
    //For parent currency
    private Double purPriceP;
    private Double purDisc1P;
    private Double purDisc2P;
    private Double purUnitCostP;
    private Double purAmtP;
    //==========================
    private Double itemExpense;
    private Location location;
    
    private String promoDesp;
    private Date promoStartDate;
    private Date promoEndDate;
    private Float promoGivePercent;
    private Float promoGetPercent;
    private Boolean promoGetComplete;
    private Float itemExpenseP;
    private String vouNo;
    
    @Column(name="pur_amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Column(name="pur_discount1")
    public Double getDiscount1() {
        return discount1;
    }

    public void setDiscount1(Double discount1) {
        this.discount1 = discount1;
    }

    @Column(name="pur_discount2")
    public Double getDiscount2() {
        return discount2;
    }

    public void setDiscount2(Double discount2) {
        this.discount2 = discount2;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="expire_date")
    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    @Column(name="pur_foc_qty")
    public Float getFocQty() {
        return focQty;
    }

    public void setFocQty(Float focQty) {
        this.focQty = focQty;
    }

    @ManyToOne
    @JoinColumn(name="med_id")
    public Medicine getMedId() {
        return medId;
    }

    public void setMedId(Medicine medId) {
        this.medId = medId;
    }

    @Column(name="pur_price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Id
    @Column(name="pur_detail_id", unique=true, nullable=false, length=25)
    public String getPurDetailId() {
        return purDetailId;
    }

    public void setPurDetailId(String purDetailId) {
        this.purDetailId = purDetailId;
    }

    @Column(name="pur_smallest_qty")
    public Float getPurSmallestQty() {
        return purSmallestQty;
    }

    public void setPurSmallestQty(Float purSmallestQty) {
        this.purSmallestQty = purSmallestQty;
    }

    @Column(name="pur_qty")
    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    @Column(name="pur_unit_cost")
    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }

    @ManyToOne
    @JoinColumn(name="pur_unit")
    public ItemUnit getUnitId() {
        return unitId;
    }

    public void setUnitId(ItemUnit unitId) {
        this.unitId = unitId;
    }

    @ManyToOne
    @JoinColumn(name="foc_unit")
    public ItemUnit getFocUnitId() {
        return focUnitId;
    }

    public void setFocUnitId(ItemUnit focUnitId) {
        this.focUnitId = focUnitId;
    }

    @Column(name="pur_foc_smallest_qty")
    public Float getFocSmallestQty() {
        return focSmallestQty;
    }

    public void setFocSmallestQty(Float focSmallestQty) {
        this.focSmallestQty = focSmallestQty;
    }

    @Column(name="unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @ManyToOne
    @JoinColumn(name = "charge_type")
    public ChargeType getChargeId() {
        return chargeId;
    }

    public void setChargeId(ChargeType chargeId) {
        this.chargeId = chargeId;
    }

    @Column(name="pur_price_p")
    public Double getPurPriceP() {
        return purPriceP;
    }

    public void setPurPriceP(Double purPriceP) {
        this.purPriceP = purPriceP;
    }

    @Column(name="pur_disc1_p")
    public Double getPurDisc1P() {
        return purDisc1P;
    }

    public void setPurDisc1P(Double purDisc1P) {
        this.purDisc1P = purDisc1P;
    }

    @Column(name="pur_disc2_p")
    public Double getPurDisc2P() {
        return purDisc2P;
    }

    public void setPurDisc2P(Double purDisc2P) {
        this.purDisc2P = purDisc2P;
    }

    @Column(name="pur_unit_cost_p")
    public Double getPurUnitCostP() {
        return purUnitCostP;
    }

    public void setPurUnitCostP(Double purUnitCostP) {
        this.purUnitCostP = purUnitCostP;
    }

    @Column(name="pur_amt_p")
    public Double getPurAmtP() {
        return purAmtP;
    }

    public void setPurAmtP(Double purAmtP) {
        this.purAmtP = purAmtP;
    }

    @Column(name="item_expense")
    public Double getItemExpense() {
        return itemExpense;
    }

    public void setItemExpense(Double itemExpense) {
        this.itemExpense = itemExpense;
    }

    @ManyToOne
    @JoinColumn(name="location_id")
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    
    @Column(name="promo_desp", length=500)
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
    public Boolean isPromoGetComplete() {
        return promoGetComplete;
    }

    public void setPromoGetComplete(Boolean promoGetComplete) {
        this.promoGetComplete = promoGetComplete;
    }

    @Column(name="item_expense_p")
    public Float getItemExpenseP() {
        return itemExpenseP;
    }

    public void setItemExpenseP(Float itemExpenseP) {
        this.itemExpenseP = itemExpenseP;
    }
    
    @Column(name="vou_no", length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }
}
