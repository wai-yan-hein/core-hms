/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.helper;

import java.util.Date;

/**
 *
 * @author ACER
 */
public class SaleDetailHis {
    private String medId;
    private String medName;
    private String relStr;
    private Date expDate;
    private String saleQty;
    private Double costPrice;
    private Double salePrice;
    private Float itemDiscount;
    private String focQty;
    private Double saleAmount;

    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getRelStr() {
        return relStr;
    }

    public void setRelStr(String relStr) {
        this.relStr = relStr;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getSaleQty() {
        return saleQty;
    }

    public void setSaleQty(String saleQty) {
        this.saleQty = saleQty;
    }

    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Float getItemDiscount() {
        return itemDiscount;
    }

    public void setItemDiscount(Float itemDiscount) {
        this.itemDiscount = itemDiscount;
    }

    public String getFocQty() {
        return focQty;
    }

    public void setFocQty(String focQty) {
        this.focQty = focQty;
    }

    public Double getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(Double saleAmount) {
        this.saleAmount = saleAmount;
    }
    
    
}
