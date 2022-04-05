/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.helper;

/**
 *
 * @author ACER
 */
public class CheckStockBalanceEntity {
    private String medId;
    private String medName;
    private String relStr;
    private Float balQty;
    private String balQtyStr;
    private Double ttlCost;
    private Float smallestCost;
    private Float usrBalQty;
    private String usrBalStr;
    private Double usrTtlCost;
    private Float diffQty;
    private String diffQtyStr;
    private Double diffCost;

    public CheckStockBalanceEntity(String medId, String medName, String relStr, 
            Float balQty, String balQtyStr, Double ttlCost, Float smallestCost, 
            Float usrBalQty, String usrBalStr, Double usrTtlCost, Float diffQty, 
            String diffQtyStr, Double diffCost) {
        this.medId = medId;
        this.medName = medName;
        this.relStr = relStr;
        this.balQty = balQty;
        this.balQtyStr = balQtyStr;
        this.ttlCost = ttlCost;
        this.smallestCost = smallestCost;
        this.usrBalQty = usrBalQty;
        this.usrBalStr = usrBalStr;
        this.usrTtlCost = usrTtlCost;
        this.diffQty = diffQty;
        this.diffQtyStr = diffQtyStr;
        this.diffCost = diffCost;
    }

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

    public Float getBalQty() {
        return balQty;
    }

    public void setBalQty(Float balQty) {
        this.balQty = balQty;
    }

    public String getBalQtyStr() {
        return balQtyStr;
    }

    public void setBalQtyStr(String balQtyStr) {
        this.balQtyStr = balQtyStr;
    }

    public Double getTtlCost() {
        return ttlCost;
    }

    public void setTtlCost(Double ttlCost) {
        this.ttlCost = ttlCost;
    }

    public Float getSmallestCost() {
        return smallestCost;
    }

    public void setSmallestCost(Float smallestCost) {
        this.smallestCost = smallestCost;
    }

    public Float getUsrBalQty() {
        return usrBalQty;
    }

    public void setUsrBalQty(Float usrBalQty) {
        this.usrBalQty = usrBalQty;
    }

    public String getUsrBalStr() {
        return usrBalStr;
    }

    public void setUsrBalStr(String usrBalStr) {
        this.usrBalStr = usrBalStr;
    }

    public Double getUsrTtlCost() {
        return usrTtlCost;
    }

    public void setUsrTtlCost(Double usrTtlCost) {
        this.usrTtlCost = usrTtlCost;
    }

    public Float getDiffQty() {
        return diffQty;
    }

    public void setDiffQty(Float diffQty) {
        this.diffQty = diffQty;
    }

    public String getDiffQtyStr() {
        return diffQtyStr;
    }

    public void setDiffQtyStr(String diffQtyStr) {
        this.diffQtyStr = diffQtyStr;
    }

    public Double getDiffCost() {
        return diffCost;
    }

    public void setDiffCost(Double diffCost) {
        this.diffCost = diffCost;
    }
}
