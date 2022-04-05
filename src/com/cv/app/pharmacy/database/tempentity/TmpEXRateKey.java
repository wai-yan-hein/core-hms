/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author winswe
 */
@Embeddable
public class TmpEXRateKey implements Serializable{
    private String userId;
    private String fromCurr;
    private String toCurr;

    @Column(name="user_id", nullable=false, length=15)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        hash = 59 * hash + Objects.hashCode(this.userId);
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
        final TmpEXRateKey other = (TmpEXRateKey) obj;
        if (!Objects.equals(this.userId, other.userId)) {
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
