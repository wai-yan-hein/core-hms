/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;

/**
 *
 * @author winswe
 */
public class StockIssueExRateHisKey implements Serializable{
    private String vouNo;
    private String fromCurr;
    private String toCurr;

    @Column(name="vou_no", nullable=false, length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name="from_curr", nullable=false, length=15)
    public String getFromCurr() {
        return fromCurr;
    }

    public void setFromCurr(String fromCurr) {
        this.fromCurr = fromCurr;
    }

    @Column(name="to_curr", nullable=false, length=15)
    public String getToCurr() {
        return toCurr;
    }

    public void setToCurr(String toCurr) {
        this.toCurr = toCurr;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.vouNo);
        hash = 59 * hash + Objects.hashCode(this.fromCurr);
        hash = 59 * hash + Objects.hashCode(this.toCurr);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StockIssueExRateHisKey other = (StockIssueExRateHisKey) obj;
        if (!Objects.equals(this.vouNo, other.vouNo)) {
            return false;
        }
        if (!Objects.equals(this.fromCurr, other.fromCurr)) {
            return false;
        }
        if (!Objects.equals(this.toCurr, other.toCurr)) {
            return false;
        }
        return true;
    }
    
    
}
