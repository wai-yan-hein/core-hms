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
public class PriceChangeMedHis implements java.io.Serializable{
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
    private List<PriceChangeUnitHis> listUnit;
    
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
    public List<PriceChangeUnitHis> getListUnit() {
        return listUnit;
    }

    public void setListUnit(List<PriceChangeUnitHis> listUnit) {
        this.listUnit = listUnit;
    }

    @Column(name="remark_med")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    
}
