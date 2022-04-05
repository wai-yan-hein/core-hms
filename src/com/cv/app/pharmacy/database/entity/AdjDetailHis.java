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
@Table(name = "adj_detail_his")
public class AdjDetailHis implements java.io.Serializable {

    private String adjDetailId;
    private Medicine medicineId;
    private Date expireDate;
    private Float qty;
    private ItemUnit unit;
    private Integer uniqueId;
    private Float smallestQty;
    private AdjType adjType;
    private Double costPrice;
    private Double amount;
    private String strBalance;
    private Float balance;
    private String vouNo;
    private Float usrBalQty;
    private ItemUnit usrBalUnit;
    private Float usrBalsmallest;
    private Float sysBalance;
    private String strSysBalance;
    private String currencyId;
    
    @Id
    @Column(name = "adj_detail_id", unique = true, nullable = false, length=25)
    public String getAdjDetailId() {
        return adjDetailId;
    }

    public void setAdjDetailId(String adjDetailId) {
        this.adjDetailId = adjDetailId;
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

    @Column(name = "adj_qty")
    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    @Column(name = "adj_smallest_qty")
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

    @ManyToOne
    @JoinColumn(name = "adj_type")
    public AdjType getAdjType() {
        return adjType;
    }

    public void setAdjType(AdjType adjType) {
        this.adjType = adjType;
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

    @Column(name="balance_str", length=50)
    public String getStrBalance() {
        return strBalance;
    }

    public void setStrBalance(String strBalance) {
        this.strBalance = strBalance;
    }

    @Column(name="balance")
    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    @Column(name="vou_no", length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name="usr_balance")
    public Float getUsrBalQty() {
        return usrBalQty;
    }

    public void setUsrBalQty(Float usrBalQty) {
        this.usrBalQty = usrBalQty;
    }

    @ManyToOne
    @JoinColumn(name = "usr_item_unit")
    public ItemUnit getUsrBalUnit() {
        return usrBalUnit;
    }

    public void setUsrBalUnit(ItemUnit usrBalUnit) {
        this.usrBalUnit = usrBalUnit;
    }

    @Column(name="usr_balance_smallest")
    public Float getUsrBalsmallest() {
        return usrBalsmallest;
    }

    public void setUsrBalsmallest(Float usrBalsmallest) {
        this.usrBalsmallest = usrBalsmallest;
    }

    @Column(name="sys_balance")
    public Float getSysBalance() {
        return sysBalance;
    }

    public void setSysBalance(Float sysBalance) {
        this.sysBalance = sysBalance;
    }

    @Column(name="sys_balance_str", length=50)
    public String getStrSysBalance() {
        return strSysBalance;
    }

    public void setStrSysBalance(String strSysBalance) {
        this.strSysBalance = strSysBalance;
    }

    @Column(name="currency_id", length=15)
    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }
}
