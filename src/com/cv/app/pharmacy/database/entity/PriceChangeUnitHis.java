/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "price_change_unit_his")
public class PriceChangeUnitHis implements java.io.Serializable{
    private Long pcUnitHisId;
    private ItemUnit pcUnit;
    private Double nPriceOld;
    private Double nPriceNew;
    private Double aPriceOld;
    private Double aPriceNew;
    private Double bPriceOld;
    private Double bPriceNew;
    private Double cPriceOld;
    private Double cPriceNew;
    private Double dPriceOld;
    private Double dPriceNew;
    private Integer uniqueId;
    private Double costPrice;
    
    public PriceChangeUnitHis(){}
    
    public PriceChangeUnitHis(ItemUnit pcUnit, Double nPriceOld, Double aPriceOld,
            Double bPriceOld, Double cPriceOld, Double dPriceOld){
        this.pcUnit = pcUnit;
        this.nPriceOld = nPriceOld;
        this.aPriceOld = aPriceOld;
        this.bPriceOld = bPriceOld;
        this.cPriceOld = cPriceOld;
        this.dPriceOld = dPriceOld;
    }
    
    @Column(name="a_price_new")
    public Double getaPriceNew() {
        return aPriceNew;
    }

    public void setaPriceNew(Double aPriceNew) {
        this.aPriceNew = aPriceNew;
    }

    @Column(name="a_price_old")
    public Double getaPriceOld() {
        return aPriceOld;
    }

    public void setaPriceOld(Double aPriceOld) {
        this.aPriceOld = aPriceOld;
    }

    @Column(name="b_price_new")
    public Double getbPriceNew() {
        return bPriceNew;
    }

    public void setbPriceNew(Double bPriceNew) {
        this.bPriceNew = bPriceNew;
    }

    @Column(name="b_price_old")
    public Double getbPriceOld() {
        return bPriceOld;
    }

    public void setbPriceOld(Double bPriceOld) {
        this.bPriceOld = bPriceOld;
    }

    @Column(name="c_price_new")
    public Double getcPriceNew() {
        return cPriceNew;
    }

    public void setcPriceNew(Double cPriceNew) {
        this.cPriceNew = cPriceNew;
    }

    @Column(name="c_price_old")
    public Double getcPriceOld() {
        return cPriceOld;
    }

    public void setcPriceOld(Double cPriceOld) {
        this.cPriceOld = cPriceOld;
    }

    @Column(name="d_price_new")
    public Double getdPriceNew() {
        return dPriceNew;
    }

    public void setdPriceNew(Double dPriceNew) {
        this.dPriceNew = dPriceNew;
    }

    @Column(name="d_price_old")
    public Double getdPriceOld() {
        return dPriceOld;
    }

    public void setdPriceOld(Double dPriceOld) {
        this.dPriceOld = dPriceOld;
    }

    @Column(name="n_price_new")
    public Double getnPriceNew() {
        return nPriceNew;
    }

    public void setnPriceNew(Double nPriceNew) {
        this.nPriceNew = nPriceNew;
    }

    @Column(name="n_price_old")
    public Double getnPriceOld() {
        return nPriceOld;
    }

    public void setnPriceOld(Double nPriceOld) {
        this.nPriceOld = nPriceOld;
    }

    @ManyToOne
    @JoinColumn(name="pc_unit")
    public ItemUnit getPcUnit() {
        return pcUnit;
    }

    public void setPcUnit(ItemUnit pcUnit) {
        this.pcUnit = pcUnit;
    }

    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="pc_unit_his_id", unique=true, nullable=false)
    public Long getPcUnitHisId() {
        return pcUnitHisId;
    }

    public void setPcUnitHisId(Long pcUnitHisId) {
        this.pcUnitHisId = pcUnitHisId;
    }

    @Column(name="unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name="cost_price")
    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }
}
