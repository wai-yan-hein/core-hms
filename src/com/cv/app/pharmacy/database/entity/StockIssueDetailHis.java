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
@Table(name = "stock_issue_detail_his")
public class StockIssueDetailHis implements java.io.Serializable {

    private String tranId;
    private Medicine issueMed;
    private Date expDate;
    private Float unitQty;
    private ItemUnit unit;
    private Float smallestQty;
    private Integer uniqueId;
    private String issueOpt; //Sale, FOC or Borrow
    private String refVou;
    private Trader trader;
    private String strOutstanding;
    private Float outsBalance; //Outstanding balance
    private String balance;
    private Double unitCost;
    private Double amount;
    private String issueId;
    private Currency currency;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "expire_date")
    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    @ManyToOne
    @JoinColumn(name = "med_id")
    public Medicine getIssueMed() {
        return issueMed;
    }

    public void setIssueMed(Medicine issueMed) {
        this.issueMed = issueMed;
    }

    @Column(name = "smallest_qty")
    public Float getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Float smallestQty) {
        this.smallestQty = smallestQty;
    }

    @Id @Column(name = "issue_detail_id", unique = true, nullable = false, length=25)
    public String getTranId() {
        return tranId;
    }

    public void setTranId(String tranId) {
        this.tranId = tranId;
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

    @Column(name = "unit_qty")
    public Float getUnitQty() {
        return unitQty;
    }

    public void setUnitQty(Float unitQty) {
        this.unitQty = unitQty;
    }

    @Column(name = "issue_opt")
    public String getIssueOpt() {
        return issueOpt;
    }

    public void setIssueOpt(String issueOpt) {
        this.issueOpt = issueOpt;
    }

    @Column(name = "ref_vou", length = 15)
    public String getRefVou() {
        return refVou;
    }

    public void setRefVou(String refVou) {
        this.refVou = refVou;
    }

    @ManyToOne
    @JoinColumn(name = "cus_id")
    public Trader getTrader() {
        return trader;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
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

    @Column(name="unit_cost")
    public Double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }

    @Column(name="amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Column(name="issue_id", length=15)
    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
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
