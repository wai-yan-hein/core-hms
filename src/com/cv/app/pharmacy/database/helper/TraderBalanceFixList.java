/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.helper;

import com.cv.app.util.NumberUtil;

/**
 *
 * @author winswe
 */
public class TraderBalanceFixList {
    private String traderId;
    private String traderName;
    private Double traderBalance;
    private Double vouBalance;

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    public Double getTraderBalance() {
        return traderBalance;
    }

    public void setTraderBalance(Double traderBalance) {
        this.traderBalance = traderBalance;
    }

    public Double getVouBalance() {
        return vouBalance;
    }

    public void setVouBalance(Double vouBalance) {
        this.vouBalance = vouBalance;
    }
    
    public Double getDifference(){
        return NumberUtil.NZero(vouBalance)-NumberUtil.NZero(traderBalance);
    }
}
