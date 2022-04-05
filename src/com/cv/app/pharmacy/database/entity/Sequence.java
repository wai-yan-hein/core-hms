/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="sequence")
public class Sequence implements java.io.Serializable{
    private String obtion;
    private Double seq;

    public Sequence(){
        
    }
    
    public Sequence(String option, Double seq){
        this.obtion = option;
        this.seq = seq;
    }
    
    @Id
    @Column(name="seq_option", unique=true, nullable=false, length=15)
    public String getObtion() {
        return obtion;
    }

    public void setObtion(String obtion) {
        this.obtion = obtion;
    }

    @Column(name="seq_no")
    public Double getSeq() {
        return seq;
    }

    public void setSeq(Double seq) {
        this.seq = seq;
    }
    
    
}
