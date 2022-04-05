/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.List;
import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "price_change_med_his")
public class PriceChangeMedHis1 implements java.io.Serializable{
    private Long pcMedHisId;
    private Medicine med;
    private Double purchasePrice;
    private ItemUnit purchaseUnit;
    private Double costPrice;
    private ItemUnit costUnit;
    private Double marketPrice;
    private ItemUnit marketUnit;
    private Integer uniqueId;
    private String remark;
    private List<PriceChangeUnitHis1> listUnit;
    private String ttlSaleStr;
    private Double ttlSaleSmallest;
    private String ttlPurStr;
    private Double ttlPurSmallest;
    private String stkBalanceStr;
    private Double stkBalanceSmallest;
    private String saleFocStr;
    private Double saleFocSmallest;
    private Double saleAmt;
    private String purFocStr;
    private Double purFocSmallest;
    private Double purAmt;
    
    @Column(name="cost_price")
    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    @ManyToOne
    @JoinColumn(name="cost_unit")
    public ItemUnit getCostUnit() {
        return costUnit;
    }

    public void setCostUnit(ItemUnit costUnit) {
        this.costUnit = costUnit;
    }

    @Column(name="market_price")
    public Double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }

    @ManyToOne
    @JoinColumn(name="market_unit")
    public ItemUnit getMarketUnit() {
        return marketUnit;
    }

    public void setMarketUnit(ItemUnit marketUnit) {
        this.marketUnit = marketUnit;
    }

    @ManyToOne
    @JoinColumn(name="med_id")
    public Medicine getMed() {
        return med;
    }

    public void setMed(Medicine med) {
        this.med = med;
    }

    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="pc_med_his_id", unique=true, nullable=false)
    public Long getPcMedHisId() {
        return pcMedHisId;
    }

    public void setPcMedHisId(Long pcMedHisId) {
        this.pcMedHisId = pcMedHisId;
    }

    @Column(name="purchase_price")
    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    @ManyToOne
    @JoinColumn(name="pur_unit")
    public ItemUnit getPurchaseUnit() {
        return purchaseUnit;
    }

    public void setPurchaseUnit(ItemUnit purchaseUnit) {
        this.purchaseUnit = purchaseUnit;
    }

    @Column(name="unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @OneToMany(cascade=CascadeType.ALL)
    @JoinTable(name = "pcm_his_join", joinColumns = { @JoinColumn(name = "pc_med_his_id") }, 
            inverseJoinColumns = { @JoinColumn(name = "pc_unit_his_id") })
    public List<PriceChangeUnitHis1> getListUnit() {
        return listUnit;
    }

    public void setListUnit(List<PriceChangeUnitHis1> listUnit) {
        this.listUnit = listUnit;
    }

    @Column(name="remark_med")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    @Column(name="ttl_sale_str")
    public String getTtlSaleStr() {
        return ttlSaleStr;
    }
    
    public void setTtlSaleStr(String ttlSaleStr) {
        this.ttlSaleStr = ttlSaleStr;
    }

    @Column(name="ttl_sale_smallest")
    public Double getTtlSaleSmallest() {
        return ttlSaleSmallest;
    }
    
    public void setTtlSaleSmallest(Double ttlSaleSmallest) {
        this.ttlSaleSmallest = ttlSaleSmallest;
    }

    @Column(name="ttl_pur_str")
    public String getTtlPurStr() {
        return ttlPurStr;
    }

    public void setTtlPurStr(String ttlPurStr) {
        this.ttlPurStr = ttlPurStr;
    }

    @Column(name="ttl_pur_smallest")
    public Double getTtlPurSmallest() {
        return ttlPurSmallest;
    }

    public void setTtlPurSmallest(Double ttlPurSmallest) {
        this.ttlPurSmallest = ttlPurSmallest;
    }

    @Column(name="balance_str")
    public String getStkBalanceStr() {
        return stkBalanceStr;
    }

    public void setStkBalanceStr(String stkBalanceStr) {
        this.stkBalanceStr = stkBalanceStr;
    }

    @Column(name="balance_smallest")
    public Double getStkBalanceSmallest() {
        return stkBalanceSmallest;
    }

    public void setStkBalanceSmallest(Double stkBalanceSmallest) {
        this.stkBalanceSmallest = stkBalanceSmallest;
    }

    @Column(name="sale_foc_str")
    public String getSaleFocStr() {
        return saleFocStr;
    }

    public void setSaleFocStr(String saleFocStr) {
        this.saleFocStr = saleFocStr;
    }

    @Column(name="sale_foc_smallest")
    public Double getSaleFocSmallest() {
        return saleFocSmallest;
    }

    public void setSaleFocSmallest(Double saleFocSmallest) {
        this.saleFocSmallest = saleFocSmallest;
    }

    @Column(name="sale_amout")
    public Double getSaleAmt() {
        return saleAmt;
    }

    public void setSaleAmt(Double saleAmt) {
        this.saleAmt = saleAmt;
    }

    @Column(name="pur_foc_str")
    public String getPurFocStr() {
        return purFocStr;
    }

    public void setPurFocStr(String purFocStr) {
        this.purFocStr = purFocStr;
    }

    @Column(name="pur_foc_smallest")
    public Double getPurFocSmallest() {
        return purFocSmallest;
    }

    public void setPurFocSmallest(Double purFocSmallest) {
        this.purFocSmallest = purFocSmallest;
    }

    @Column(name="pur_amount")
    public Double getPurAmt() {
        return purAmt;
    }

    public void setPurAmt(Double purAmt) {
        this.purAmt = purAmt;
    }
}
