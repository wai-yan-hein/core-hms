/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author winswe
 */
@Embeddable
public class PurchaseIMEINoKey implements Serializable {
    
    private String purVouNo;
    private Long purDetailId;
    private String itemId;
    private String imei1;

    @Column(name="pur_inv_id", nullable=false, length=15)
    public String getPurVouNo() {
        return purVouNo;
    }

    public void setPurVouNo(String purVouNo) {
        this.purVouNo = purVouNo;
    }

    @Column(name="pur_detail_id", nullable=false)
    public Long getPurDetailId() {
        return purDetailId;
    }

    public void setPurDetailId(Long purDetailId) {
        this.purDetailId = purDetailId;
    }

    @Column(name="item_id", nullable=false)
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Column(name="imei1", nullable=false, length=50)
    public String getImei1() {
        return imei1;
    }

    public void setImei1(String imei1) {
        this.imei1 = imei1;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.purVouNo);
        hash = 37 * hash + Objects.hashCode(this.purDetailId);
        hash = 37 * hash + Objects.hashCode(this.itemId);
        hash = 37 * hash + Objects.hashCode(this.imei1);
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
        final PurchaseIMEINoKey other = (PurchaseIMEINoKey) obj;
        if (!Objects.equals(this.purVouNo, other.purVouNo)) {
            return false;
        }
        if (!Objects.equals(this.itemId, other.itemId)) {
            return false;
        }
        if (!Objects.equals(this.imei1, other.imei1)) {
            return false;
        }
        if (!Objects.equals(this.purDetailId, other.purDetailId)) {
            return false;
        }
        return true;
    }
}
