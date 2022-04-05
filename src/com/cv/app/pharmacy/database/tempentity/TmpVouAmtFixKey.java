/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author winswe
 */
@Embeddable
public class TmpVouAmtFixKey implements Serializable {
    private Date saleDate;
    private String vouNo;
    private String cusId;
    private String userId;

    @Temporal(TemporalType.DATE)
    @Column(name="sale_date")
    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    @Column(name="vou_no")
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name="cus_id")
    public String getCusId() {
        return cusId;
    }

    public void setCusId(String cusId) {
        this.cusId = cusId;
    }

    @Column(name="user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.saleDate);
        hash = 37 * hash + Objects.hashCode(this.vouNo);
        hash = 37 * hash + Objects.hashCode(this.cusId);
        hash = 37 * hash + Objects.hashCode(this.userId);
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
        final TmpVouAmtFixKey other = (TmpVouAmtFixKey) obj;
        if (!Objects.equals(this.vouNo, other.vouNo)) {
            return false;
        }
        if (!Objects.equals(this.cusId, other.cusId)) {
            return false;
        }
        if (!Objects.equals(this.userId, other.userId)) {
            return false;
        }
        if (!Objects.equals(this.saleDate, other.saleDate)) {
            return false;
        }
        return true;
    }
    
    
}
