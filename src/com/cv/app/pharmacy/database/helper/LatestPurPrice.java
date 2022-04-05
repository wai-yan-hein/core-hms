/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.helper;

/**
 *
 * @author WSwe
 */
public class LatestPurPrice {
    private String medId;
    private String unit;
    private Double purPrice;

    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getPurPrice() {
        return purPrice;
    }

    public void setPurPrice(Double purPrice) {
        this.purPrice = purPrice;
    }
}
