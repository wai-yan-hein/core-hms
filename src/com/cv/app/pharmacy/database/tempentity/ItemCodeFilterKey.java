/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import com.cv.app.pharmacy.database.entity.Medicine;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author WSwe
 */
@Embeddable
public class ItemCodeFilterKey implements Serializable{
    private Medicine itemCode;
    private String userId;
    
    public ItemCodeFilterKey() {}
    
    public ItemCodeFilterKey(Medicine itemCode, String userId){
        this.itemCode = itemCode;
        this.userId = userId;
    }

    @ManyToOne
    @JoinColumn(name="item_code", nullable=false)
    public Medicine getItemCode() {
        return itemCode;
    }

    public void setItemCode(Medicine itemCode) {
        this.itemCode = itemCode;
    }

    @Column(name="user_id", nullable=false)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
