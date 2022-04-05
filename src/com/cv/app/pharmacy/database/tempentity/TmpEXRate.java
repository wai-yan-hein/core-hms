/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="tmp_ex_rate")
public class TmpEXRate implements java.io.Serializable {
    private TmpEXRateKey key;
    private Float exRate;

    public TmpEXRate(){
        key = new TmpEXRateKey();
    }
    
    @EmbeddedId
    public TmpEXRateKey getKey() {
        return key;
    }

    public void setKey(TmpEXRateKey key) {
        this.key = key;
    }

    @Column(name="ex_rate")
    public Float getExRate() {
        return exRate;
    }

    public void setExRate(Float exRate) {
        this.exRate = exRate;
    }
}
