/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.tempentity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author wswe
 */
@Entity
@Table(name="tmp_vou_srv_filter_clinic")
public class VouSrvFilter implements java.io.Serializable{
    private VouSrvFilterKey key;

    public VouSrvFilter(){}
    
    public VouSrvFilter(VouSrvFilterKey key){
        this.key = key;
    }
    
    @EmbeddedId
    public VouSrvFilterKey getKey() {
        return key;
    }

    public void setKey(VouSrvFilterKey key) {
        this.key = key;
    }
}
