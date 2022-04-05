/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="vou_id")
public class VouId implements java.io.Serializable{
    
    private Integer vouNo;
    private CompoundKey compoundKey;
    
    public VouId(){}
    
    public VouId(CompoundKey key, Integer vouNo){
        this.compoundKey = key;
        this.vouNo = vouNo;
    }
    
    @Column(name="vou_no")
    public Integer getVouNo() {
        return vouNo;
    }

    public void setVouNo(Integer vouNo) {
        this.vouNo = vouNo;
    }

    @EmbeddedId
    public CompoundKey getCompoundKey() {
        return compoundKey;
    }

    public void setCompoundKey(CompoundKey compoundKey) {
        this.compoundKey = compoundKey;
    }
}
