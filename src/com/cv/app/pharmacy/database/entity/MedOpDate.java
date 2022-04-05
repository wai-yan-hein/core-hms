/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="med_op_date")
public class MedOpDate implements java.io.Serializable {
    private CompoundKeyMedOpDate key;

    public MedOpDate(){
        key = new CompoundKeyMedOpDate();
    }
    
    public MedOpDate(CompoundKeyMedOpDate key){
        this.key = key;
    }
    
    @EmbeddedId
    public CompoundKeyMedOpDate getKey() {
        return key;
    }

    public void setKey(CompoundKeyMedOpDate key) {
        this.key = key;
    }
}
