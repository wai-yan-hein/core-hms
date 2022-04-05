/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.tempentity;

import com.cv.app.opd.database.entity.Service;
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
public class VouSrvFilterKey implements Serializable{
    private String tranOption;
    private String userId;
    private Service service;

    public VouSrvFilterKey(){}
    
    public VouSrvFilterKey(String tranOption, String userId, Service service){
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
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
