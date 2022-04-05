/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author WSwe
 */
@Embeddable
public class TraderPayHisFixKey implements Serializable{
    private String fixNo;
    private Integer paymentId;

    @Column(name="fix_no")
    public String getFixNo() {
        return fixNo;
    }

    public void setFixNo(String fixNo) {
        this.fixNo = fixNo;
    }

    @Column(name="payment_id")
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.fixNo);
        hash = 67 * hash + Objects.hashCode(this.paymentId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TraderPayHisFixKey other = (TraderPayHisFixKey) obj;
        if (!Objects.equals(this.fixNo, other.fixNo)) {
            return false;
        }
        if (!Objects.equals(this.paymentId, other.paymentId)) {
            return false;
        }
        return true;
    }
}
