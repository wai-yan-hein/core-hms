/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.database.tempentity;

import com.cv.app.ot.database.entity.OTProcedure;
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
public class OTVouSrvFilterKey implements Serializable{
    private String tranOption;
    private String userId;
    private OTProcedure service;

    public OTVouSrvFilterKey(){}
    
    public OTVouSrvFilterKey(String tranOption, String userId, OTProcedure service){
      this.tranOption = tranOption;
      this.userId = userId;
      this.service = service;
    }
    
    @Column(name="tran_option", nullable=false, length=50)
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
    @JoinColumn(name="srv_id", nullable=false)
    public OTProcedure getService() {
        return service;
    }

    public void setService(OTProcedure service) {
        this.service = service;
    }
}
