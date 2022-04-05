/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;
/**
 *
 * RelationGroup class is medicine packing relation.
 * Database table name is relation_group.
 */
@Entity
@Table(name = "item_rule")
public class ItemRule implements java.io.Serializable {

    private Integer ruleId;
    private String med_id;
    private Double chekcQtyPrice;
    private Float startQty;
    private Float endQty;
    private Double price;
    private Float qty;
    private Date startDate;
    private Date endDate;
    private Float proQty;
    private String description;
    private String sqItemUnit;
    private Float sqSmallestQty;
    private String eqItemUnit;
    private Float eqSmallestQty;
    private String itemUnit;
    private Float smallestQty;
    private String pqItemUnit;
    private Float pqSmallestQty;
    
    public ItemRule() {
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "item_rule_no", unique = true, nullable = false)
    public Integer getRuleId() {
        return ruleId;
    }

    public void setRuleId(Integer ruleId) {
        this.ruleId = ruleId;
    }

   
    
    @Column(name="med_id")
    public String getMed_id() {
        return med_id;
    }

    public void setMed_id(String med_id) {
        this.med_id = med_id;
    }

    @Column(name="check_qty_price")
    public Double getChekcQtyPrice() {
        return chekcQtyPrice;
    }

    public void setChekcQtyPrice(Double chekcQtyPrice) {
        this.chekcQtyPrice = chekcQtyPrice;
    }

    @Column(name="start_qty")
    public Float getStartQty() {
        return startQty;
    }

    public void setStartQty(Float startQty) {
        this.startQty = startQty;
    }

    @Column(name="end_qty")
    public Float getEndQty() {
        return endQty;
    }

    public void setEndQty(Float endQty) {
        this.endQty = endQty;
    }

    @Column(name="price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name="qty")
    public Float getQty() {
        return qty;
    }

    public void setQty(Float qty) {
        this.qty = qty;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="start_date")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="end_date")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name="promo_qty")
    public Float getProQty() {
        return proQty;
    }

    public void setProQty(Float proQty) {
        this.proQty = proQty;
    }

    @Column(name="description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="sqitem_unit")
    public String getSqItemUnit() {
        return sqItemUnit;
    }

    public void setSqItemUnit(String sqItemUnit) {
        this.sqItemUnit = sqItemUnit;
    }

    @Column(name="sqsmallest_qty")
    public Float getSqSmallestQty() {
        return sqSmallestQty;
    }

    public void setSqSmallestQty(Float sqSmallestQty) {
        this.sqSmallestQty = sqSmallestQty;
    }

    @Column(name="eqitem_unit")
    public String getEqItemUnit() {
        return eqItemUnit;
    }

    public void setEqItemUnit(String eqItemUnit) {
        this.eqItemUnit = eqItemUnit;
    }

    @Column(name="eqsmallest_qty")
    public Float getEqSmallestQty() {
        return eqSmallestQty;
    }

    public void setEqSmallestQty(Float eqSmallestQty) {
        this.eqSmallestQty = eqSmallestQty;
    }

    @Column(name="item_unit")
    public String getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(String ItemUnit) {
        this.itemUnit = ItemUnit;
    }

    @Column(name="smallest_qty")
    public Float getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Float SmallestQty) {
        this.smallestQty = SmallestQty;
    }

    @Column(name="pqitem_unit")
    public String getPqItemUnit() {
        return pqItemUnit;
    }

    public void setPqItemUnit(String pqItemUnit) {
        this.pqItemUnit = pqItemUnit;
    }

    @Column(name="pqsmallest_qty")
    public Float getPqSmallestQty() {
        return pqSmallestQty;
    }

    public void setPqSmallestQty(Float pqSmallestQty) {
        this.pqSmallestQty = pqSmallestQty;
    }

    
}
