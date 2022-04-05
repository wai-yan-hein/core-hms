/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "stock_receive_detail_his")
public class StockReceiveDetailHis implements java.io.Serializable {

    private String tranId;
    private String recOption; //Borrow or Purchase outstand
    private String refVou;
    private Medicine orderMed;
    private Medicine recMed;
    private Date expDate;
    private Float unitQty;
    private ItemUnit unit;
    private Float smallestQty;
    private boolean fullReceive;
    private Integer uniqueId;
    private String traderId;
    private String strOutstanding;
    private Float outsBalance; //Outstanding balance
    private String balance;
    private String vouNo;
    private Double costPrice;
    private Currency currency;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    @Column(name = "full_receive")
    public boolean isFullReceive() {
        return fullReceive;
    }

    public void setFullReceive(boolean fullReceive) {
        this.fullReceive = fullReceive;
    }

    @ManyToOne
    @JoinColumn(name = "order_med_id")
    public Medicine getOrderMed() {
        return orderMed;
    }

    public void setOrderMed(Medicine orderMed) {
        this.orderMed = orderMed;
    }

    @ManyToOne
    @JoinColumn(name = "rec_med_id")
    public Medicine getRecMed() {
        return recMed;
    }

    public void setRecMed(Medicine recMed) {
        this.recMed = recMed;
    }

    @Column(name = "rec_option")
    public String getRecOption() {
        return recOption;
    }

    public void setRecOption(String recOption) {
        this.recOption = recOption;
    }

    @Column(name = "ref_vou", length = 15)
    public String getRefVou() {
        return refVou;
    }

    public void setRefVou(String refVou) {
        this.refVou = refVou;
    }

    @Column(name = "smallest_qty")
    public Float getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Float smallestQty) {
        this.smallestQty = smallestQty;
    }

    @Id
    @Column(name = "rec_detail_id", unique = true, nullable = false, length = 25)
    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
    }

    @ManyToOne
    @JoinColumn(name = "item_unit")
    public ItemUnit getUnit() {
        return unit;
    }

    public void setUnit(ItemUnit unit) {
        this.unit = unit;
    }

    @Column(name = "unit_qty")
    public Float getUnitQty() {
        return unitQty;
    }

    public void setUnitQty(Float unitQty) {
        this.unitQty = unitQty;
    }

    @Column(name = "unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name = "cus_id")
    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    @Transient
    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Transient
    public Float getOutsBalance() {
        return outsBalance;
    }

    public void setOutsBalance(Float outsBalance) {
        this.outsBalance = outsBalance;
    }

    @Transient
    public String getStrOutstanding() {
        return strOutstanding;
    }

    public void setStrOutstanding(String strOutstanding) {
        this.strOutstanding = strOutstanding;
    }

    @Column(name="vou_no", length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name="cost_price")
    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }
    
    @ManyToOne
    @JoinColumn(name = "currency_id")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
