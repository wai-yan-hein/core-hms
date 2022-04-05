/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import com.cv.app.pharmacy.database.entity.*;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "v_re_order_level")
public class VReOrderLevel implements java.io.Serializable {

    private ReOrderLevelKey key;
    private Float maxUnitQty;
    private ItemUnit maxItemUnit;
    private Float maxSmallest;
    private Float minUnitQty;
    private ItemUnit minItemUnit;
    private Float minSmallest;
    private Float balance;
    private String strBalance;
    private Float balMax;
    private String strBalMax;
    private Float balMin;
    private String strBalMin;
    private Float balMain;
    private String strBalMain;
    private Float orderQty;
    private ItemUnit orderUnit;
    private Float orderQtySmallest;
    private String medName;
    private String relStr;
    private String locationName;
    private String shortName;
    private String itemTypeId;
    private Integer catId;
    private Integer brandId;
    private Boolean itemActive;
    
    @EmbeddedId
    public ReOrderLevelKey getKey() {
        return key;
    }

    public void setKey(ReOrderLevelKey key) {
        this.key = key;
    }

    @Column(name = "max_unit_qty")
    public Float getMaxUnitQty() {
        return maxUnitQty;
    }

    public void setMaxUnitQty(Float maxUnitQty) {
        this.maxUnitQty = maxUnitQty;
    }

    @ManyToOne
    @JoinColumn(name = "max_unit")
    public ItemUnit getMaxItemUnit() {
        return maxItemUnit;
    }

    public void setMaxItemUnit(ItemUnit maxItemUnit) {
        this.maxItemUnit = maxItemUnit;
    }

    @Column(name = "max_smallest")
    public Float getMaxSmallest() {
        return maxSmallest;
    }

    public void setMaxSmallest(Float maxSmallest) {
        this.maxSmallest = maxSmallest;
    }

    @Column(name = "min_unit_qty")
    public Float getMinUnitQty() {
        return minUnitQty;
    }

    public void setMinUnitQty(Float minUnitQty) {
        this.minUnitQty = minUnitQty;
    }

    @ManyToOne
    @JoinColumn(name = "min_unit")
    public ItemUnit getMinItemUnit() {
        return minItemUnit;
    }

    public void setMinItemUnit(ItemUnit minItemUnit) {
        this.minItemUnit = minItemUnit;
    }

    @Column(name = "min_smallest")
    public Float getMinSmallest() {
        return minSmallest;
    }

    public void setMinSmallest(Float minSmallest) {
        this.minSmallest = minSmallest;
    }

    @Column(name = "balance")
    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    @Column(name = "balance_str")
    public String getStrBalance() {
        return strBalance;
    }

    public void setStrBalance(String strBalance) {
        this.strBalance = strBalance;
    }

    @Column(name = "bal_max")
    public Float getBalMax() {
        return balMax;
    }

    public void setBalMax(Float balMax) {
        this.balMax = balMax;
    }

    @Column(name = "bal_min")
    public Float getBalMin() {
        return balMin;
    }

    public void setBalMin(Float balMin) {
        this.balMin = balMin;
    }

    @Column(name = "bal_max_str")
    public String getStrBalMax() {
        return strBalMax;
    }

    public void setStrBalMax(String strBalMax) {
        this.strBalMax = strBalMax;
    }

    @Column(name = "bal_min_str")
    public String getStrBalMin() {
        return strBalMin;
    }

    public void setStrBalMin(String strBalMin) {
        this.strBalMin = strBalMin;
    }

    @Column(name="main_bal")
    public Float getBalMain() {
        return balMain;
    }

    public void setBalMain(Float balMain) {
        this.balMain = balMain;
    }

    @Column(name="main_bal_str", length=45)
    public String getStrBalMain() {
        return strBalMain;
    }

    public void setStrBalMain(String strBalMain) {
        this.strBalMain = strBalMain;
    }

    @Column(name="order_qty")
    public Float getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(Float orderQty) {
        this.orderQty = orderQty;
    }

    @ManyToOne
    @JoinColumn(name = "order_unit")
    public ItemUnit getOrderUnit() {
        return orderUnit;
    }

    public void setOrderUnit(ItemUnit orderUnit) {
        this.orderUnit = orderUnit;
    }

    @Column(name="order_qty_smallest")
    public Float getOrderQtySmallest() {
        return orderQtySmallest;
    }

    public void setOrderQtySmallest(Float orderQtySmallest) {
        this.orderQtySmallest = orderQtySmallest;
    }

    @Column(name="med_name")
    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    @Column(name="med_rel_str")
    public String getRelStr() {
        return relStr;
    }

    public void setRelStr(String relStr) {
        this.relStr = relStr;
    }

    @Column(name="location_name")
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Column(name="short_name")
    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Column(name="med_type_id")
    public String getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(String itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    @Column(name="category_id")
    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    @Column(name="brand_id")
    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    @Column(name="active")
    public Boolean getItemActive() {
        return itemActive;
    }

    public void setItemActive(Boolean itemActive) {
        this.itemActive = itemActive;
    }
}
