/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.database.tempentity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author wswe
 */
@Entity
@Table(name="tmp_vou_srv_filter_clinic")
public class OTVouSrvFilter implements java.io.Serializable{
    private OTVouSrvFilterKey key;

    public OTVouSrvFilter(){}
    
    public OTVouSrvFilter(OTVouSrvFilterKey key){
        this.key = key;
    }
    
    @EmbeddedId
    public OTVouSrvFilterKey getKey() {
        return key;
    }

    public void setKey(OTVouSrvFilterKey key) {
        this.key = key;
    }
}
