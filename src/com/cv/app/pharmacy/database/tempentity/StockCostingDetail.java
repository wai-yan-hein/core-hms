/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author wswe
 */
@Entity
@Table(name = "tmp_costing_detail")
public class StockCostingDetail implements java.io.Serializable {

    private Integer tranId;
    private String itemId;
    private Date tranDate;
    private String tranOption;
    private Integer tranQty; //for transaction qty
    private Double packingCost;
    private Double smallestCost;
    private Integer costQty;
    private String userId;
    private String costFor;
    private String unit;
    private Integer location;
    private Integer saleQty;
    private String currencyId;
    private String homeCurr;
    private String exrDesp;
    private Double exrRate;
    private Double exrSmallCost;
    private Double exrTtlCost;
    
    @Column(name = "location")
    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name = "tran_id", unique = true, nullable = false)
    public Integer getTranId() {
        return tranId;
    }

    public void setTranId(Integer tranId) {
        this.tranId = tranId;
    }

    @Column(name = "item_id")
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "tran_date")
    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    @Column(name = "tran_option")
    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }

    @Column(name = "ttl_qty")
    public Integer getTranQty() {
        return tranQty;
    }

    public void setTranQty(Integer tranQty) {
        this.tranQty = tranQty;
    }

    @Column(name = "cost_price")
    public Double getPackingCost() {
        return packingCost;
    }

    public void setPackingCost(Double packingCost) {
        this.packingCost = packingCost;
    }

    @Column(name = "smallest_cost")
    public Double getSmallestCost() {
        return smallestCost;
    }

    public void setSmallestCost(Double smallestCost) {
        this.smallestCost = smallestCost;
    }

    @Column(name = "cost_qty")
    public Integer getCostQty() {
        return costQty;
    }

    public void setCostQty(Integer costQty) {
        this.costQty = costQty;
    }

    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "cost_for")
    public String getCostFor() {
        return costFor;
    }

    public void setCostFor(String costFor) {
        this.costFor = costFor;
    }

    @Column(name = "item_unit", length = 15)
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Column(name="sale_cost_qty")
    public Integer getSaleQty() {
        return saleQty;
    }

    public void setSaleQty(Integer saleQty) {
        this.saleQty = saleQty;
    }

    @Column(name="currency_id", length=15)
    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    @Column(name="home_currency", length=15)
    public String getHomeCurr() {
        return homeCurr;
    }

    public void setHomeCurr(String homeCurr) {
        this.homeCurr = homeCurr;
    }

    @Column(name="exr_desp", length=25)
    public String getExrDesp() {
        return exrDesp;
    }

    public void setExrDesp(String exrDesp) {
        this.exrDesp = exrDesp;
    }
    
    @Column(name="exr_rate")
    public Double getExrRate() {
        return exrRate;
    }

    public void setExrRate(Double exrRate) {
        this.exrRate = exrRate;
    }

    @Column(name="exr_smallest_cost")
    public Double getExrSmallCost() {
        return exrSmallCost;
    }

    public void setExrSmallCost(Double exrSmallCost) {
        this.exrSmallCost = exrSmallCost;
    }

    @Column(name="exr_ttl_cost")
    public Double getExrTtlCost() {
        return exrTtlCost;
    }

    public void setExrTtlCost(Double exrTtlCost) {
        this.exrTtlCost = exrTtlCost;
    }
}
