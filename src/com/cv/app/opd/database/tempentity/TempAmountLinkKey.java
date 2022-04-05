/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.tempentity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author MyintMo
 */
@Embeddable
public class TempAmountLinkKey implements Serializable{
    private String userId;
    private String tranOption;
    private String invId;

    @Column(name="user_id", length=15, nullable=false)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name="tran_option", length=15, nullable=false)
    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }

    @Column(name="inv_id", length=25, nullable=false)
    public String getInvId() {
        return invId;
    }

    public void setInvId(String invId) {
        this.invId = invId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.userId);
        hash = 11 * hash + Objects.hashCode(this.tranOption);
        hash = 11 * hash + Objects.hashCode(this.invId);
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
        final TempAmountLinkKey other = (TempAmountLinkKey) obj;
        if (!Objects.equals(this.userId, other.userId)) {
            return false;
        }
        if (!Objects.equals(this.tranOption, other.tranOption)) {
            return false;
        }
        if (!Objects.equals(this.invId, other.invId)) {
            return false;
        }
        return true;
    }
}
