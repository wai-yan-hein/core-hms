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
public class PurchaseVoucher {
    private Date purDate;
    private String vouoNo;
    private String supId;
    private String supName;
    private Double vouTotal;
    private boolean check;
    
    public Date getPurDate() {
        return purDate;
    }

    public void setPurDate(Date purDate) {
        this.purDate = purDate;
    }

    public String getVouoNo() {
        return vouoNo;
    }

    public void setVouoNo(String vouoNo) {
        this.vouoNo = vouoNo;
    }

    public String getSupId() {
        return supId;
    }

    public void setSupId(String supId) {
        this.supId = supId;
    }

    public String getSupName() {
        return supName;
    }

    public void setSupName(String supName) {
        this.supName = supName;
    }

    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
