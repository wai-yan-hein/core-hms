/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="tmp_vou_code_filter")
public class VouCodeFilter implements java.io.Serializable{
    private VouCodeFilterKey key;

    public VouCodeFilter(){ /*key = new VouCodeFilterKey();*/ }
    
    public VouCodeFilter(VouCodeFilterKey key){
      this.key = key;
    }
    
    @EmbeddedId
    public VouCodeFilterKey getKey() {
        return key;
    }

    public void setKey(VouCodeFilterKey key) {
        this.key = key;
    }
}
