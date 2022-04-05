/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

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
@Table(name = "re_order_level")
public class ReOrderLevel implements java.io.Serializable {

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
}
