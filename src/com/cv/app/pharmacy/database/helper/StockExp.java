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
public class StockExp {
    private String medId;
    private Date expDate;
    private Integer locationId;
    private float balance;

    public StockExp(String medId, Date expDate, Integer locationId, float balance) {
        this.medId = medId;
        this.expDate = expDate;
        this.locationId = locationId;
        this.balance = balance;
    }

    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
}
