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
public class TmpStockBalOutsKey implements Serializable {
    private String tranOption;
    private String itemId;
    private String traderId;
    private String userId;

    @Column(name="tran_option", nullable=false, length=20)
    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }

    @Column(name="item_id", nullable=false, length=15)
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Column(name="trader_id", nullable=false, length=15)
    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    @Column(name="user_id", nullable=false, length=15)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.tranOption);
        hash = 79 * hash + Objects.hashCode(this.itemId);
        hash = 79 * hash + Objects.hashCode(this.traderId);
        hash = 79 * hash + Objects.hashCode(this.userId);
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
        final TmpStockBalOutsKey other = (TmpStockBalOutsKey) obj;
        if (!Objects.equals(this.tranOption, other.tranOption)) {
            return false;
        }
        if (!Objects.equals(this.itemId, other.itemId)) {
            return false;
        }
        if (!Objects.equals(this.traderId, other.traderId)) {
            return false;
        }
        if (!Objects.equals(this.userId, other.userId)) {
            return false;
        }
        return true;
    }
}
