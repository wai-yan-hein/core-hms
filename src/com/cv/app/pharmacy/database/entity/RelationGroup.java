/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;
/**
 *
 * RelationGroup class is medicine packing relation.
 * Database table name is relation_group.
 */
@Entity
@Table(name = "relation_group")
public class RelationGroup implements java.io.Serializable {

    private Integer relGId;
    private Float unitQty;
    private ItemUnit unitId;
    private Double salePrice;
    private Double salePriceA;
    private Double salePriceB;
    private Double salePriceC;
    private Double salePriceD;
    private Integer relUniqueId;
    private Float smallestQty;
    private String unitBarcode;
    private Double stdCost;
    
    public RelationGroup() {
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "rel_group_id", unique = true, nullable = false)
    public Integer getRelGId() {
        return relGId;
    }

    public void setRelGId(Integer relGId) {
        this.relGId = relGId;
    }

    @Column(name = "rel_unique_id")
    public Integer getRelUniqueId() {
        return relUniqueId;
    }

    public void setRelUniqueId(Integer relUnqueId) {
        this.relUniqueId = relUnqueId;
    }

    @Column(name = "sale_price")
    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }
    
    @Column(name = "sale_pric_a")
    public Double getSalePriceA() {
        return salePriceA;
    }

    public void setSalePriceA(Double salePriceA) {
        this.salePriceA = salePriceA;
    }

    @Column(name = "sale_pric_b")
    public Double getSalePriceB() {
        return salePriceB;
    }

    public void setSalePriceB(Double salePriceB) {
        this.salePriceB = salePriceB;
    }

    @Column(name = "sale_pric_c")
    public Double getSalePriceC() {
        return salePriceC;
    }

    public void setSalePriceC(Double salePriceC) {
        this.salePriceC = salePriceC;
    }

    @Column(name = "sale_pric_d")
    public Double getSalePriceD() {
        return salePriceD;
    }

    public void setSalePriceD(Double salePriceD) {
        this.salePriceD = salePriceD;
    }

    @ManyToOne
    @JoinColumn(name="item_unit")
    public ItemUnit getUnitId() {
        return unitId;
    }

    public void setUnitId(ItemUnit unitId) {
        this.unitId = unitId;
    }
    
    @Column(name = "unit_qty", nullable = false)
    public Float getUnitQty() {
        return unitQty;
    }

    public void setUnitQty(Float unitQty) {
        this.unitQty = unitQty;
    }

    @Column(name = "smallest_qty", nullable = false)
    public Float getSmallestQty() {
        return smallestQty;
    }

    public void setSmallestQty(Float smallestQty) {
        this.smallestQty = smallestQty;
    }

    @Column(name="unit_barcode", length=50)
    public String getUnitBarcode() {
        return unitBarcode;
    }

    public void setUnitBarcode(String unitBarcode) {
        this.unitBarcode = unitBarcode;
    }

    @Column(name="std_cost")
    public Double getStdCost() {
        return stdCost;
    }

    public void setStdCost(Double stdCost) {
        this.stdCost = stdCost;
    }
}
