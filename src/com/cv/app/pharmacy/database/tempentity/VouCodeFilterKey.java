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
public class VouCodeFilterKey implements Serializable{
    private String tranOption;
    private String userId;
    private Medicine itemCode;

    public VouCodeFilterKey(){}
    
    public VouCodeFilterKey(String tranOption, String userId, Medicine itemCode){
      this.tranOption = tranOption;
      this.userId = userId;
      this.itemCode = itemCode;
    }
    
    @Column(name="tran_option", nullable=false, length=20)
    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }

    @Column(name="user_id", nullable=false, length=20)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @ManyToOne
    @JoinColumn(name="item_id", nullable=false)
    public Medicine getItemCode() {
        return itemCode;
    }

    public void setItemCode(Medicine itemCode) {
        this.itemCode = itemCode;
    }
}
