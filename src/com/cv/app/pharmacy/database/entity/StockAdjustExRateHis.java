/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="stock_adjust_ex_rate_his")
public class StockAdjustExRateHis implements java.io.Serializable {
    private StockIssueExRateHisKey key;
    private Float exRate;

    @EmbeddedId
    public StockIssueExRateHisKey getKey() {
        return key;
    }

    public void setKey(StockIssueExRateHisKey key) {
        this.key = key;
    }

    @Column(name="ex_rate")
    public Float getExRate() {
        return exRate;
    }

    public void setExRate(Float exRate) {
        this.exRate = exRate;
    }
}
