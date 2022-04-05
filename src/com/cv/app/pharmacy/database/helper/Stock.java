/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.helper;

import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import java.util.Date;

/**
 *
 * @author WSwe
 */
public class Stock {
    private Medicine med;
    private Date expDate;
    private String qtyStr;
    private Float qtySmallest;
    private String qtyStrBal;
    private Float unitQty;
    private ItemUnit unit;
    private String qtyStrDeman;
    private String focQtyStr;
    private ItemUnit focUnit;
    private Float focUnitQty;
    private String locationName;
    private Float balance;
    private Integer locationId;
    
    public Stock(Medicine med, Date expDate, String qtyStr, Float qtySmallest,
            String qtyStrBal) {
        this.med = med;
        this.expDate = expDate;
        this.qtyStr = qtyStr;
        this.qtySmallest = qtySmallest;
        this.balance = qtySmallest;
        this.qtyStrBal = qtyStrBal;
    }

    public Stock(Medicine med, Date expDate, String qtyStr, Float qtySmallest,
            String qtyStrBal, String locationName, Integer locationId) {
        this.med = med;
        this.expDate = expDate;
        this.qtyStr = qtyStr;
        this.qtySmallest = qtySmallest;
        this.balance = qtySmallest;
        this.qtyStrBal = qtyStrBal;
        this.locationName = locationName;
        this.locationId = locationId;
    }
    
    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public Medicine getMed() {
        return med;
    }

    public void setMed(Medicine med) {
        this.med = med;
    }

    public Float getQtySmallest() {
        return qtySmallest;
    }

    public void setQtySmallest(Float qtySmallest) {
        this.qtySmallest = qtySmallest;
    }

    public String getQtyStr() {
        return qtyStr;
    }

    public void setQtyStr(String qtyStr) {
        this.qtyStr = qtyStr;
    }
    
    public String getQtyStrBal() {
        return qtyStrBal;
    }

    public void setQtyStrBal(String qtyStrBal) {
        this.qtyStrBal = qtyStrBal;
    }

    public ItemUnit getUnit() {
        return unit;
    }

    public void setUnit(ItemUnit unit) {
        this.unit = unit;
    }

    public Float getUnitQty() {
        return unitQty;
    }

    public void setUnitQty(Float unitQty) {
        this.unitQty = unitQty;
    }

    public String getQtyStrDeman() {
        return qtyStrDeman;
    }

    public void setQtyStrDeman(String qtyStrDeman) {
        this.qtyStrDeman = qtyStrDeman;
    }

    public String getFocQtyStr() {
        return focQtyStr;
    }

    public void setFocQtyStr(String focQtyStr) {
        this.focQtyStr = focQtyStr;
    }

    public ItemUnit getFocUnit() {
        return focUnit;
    }

    public void setFocUnit(ItemUnit focUnit) {
        this.focUnit = focUnit;
    }

    public Float getFocUnitQty() {
        return focUnitQty;
    }

    public void setFocUnitQty(Float focUnitQty) {
        this.focUnitQty = focUnitQty;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
}
