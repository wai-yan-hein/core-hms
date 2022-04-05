/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.helper;

import com.cv.app.pharmacy.database.entity.Medicine;
import java.util.Date;

/**
 *
 * @author winswe
 */
public class StockOutstanding {

    private String tranOption;
    private Date tranDate;
    private String cusId;
    private float balanceQty;
    private String invId;
    private String qtyStr;
    private Medicine med;

    public StockOutstanding() {
    }

    public StockOutstanding(String tranOption, Date tranDate, String cusId, float balanceQty,
            String invId, String qtyStr, Medicine med) {
        this.tranOption = tranOption;
        this.tranDate = tranDate;
        this.cusId = cusId;
        this.balanceQty = balanceQty;
        this.invId = invId;
        this.qtyStr = qtyStr;
        this.med = med;
    }

    public float getBalanceQty() {
        return balanceQty;
    }

    public void setBalanceQty(float balanceQty) {
        this.balanceQty = balanceQty;
    }

    public String getCusId() {
        return cusId;
    }

    public void setCusId(String cusId) {
        this.cusId = cusId;
    }

    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }

    public String getInvId() {
        return invId;
    }

    public void setInvId(String invId) {
        this.invId = invId;
    }

    public String getQtyStr() {
        return qtyStr;
    }

    public void setQtyStr(String qtyStr) {
        this.qtyStr = qtyStr;
    }

    public Medicine getMed() {
        return med;
    }

    public void setMed(Medicine med) {
        this.med = med;
    }

}
