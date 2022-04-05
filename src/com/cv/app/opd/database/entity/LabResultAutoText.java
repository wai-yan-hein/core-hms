/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author admin
 */
@Entity
@Table(name="opd_lab_result_auto_text")
public class LabResultAutoText implements java.io.Serializable{
    private Integer id;
    private String autoText;

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="auto_text", length=500)
    public String getAutoText() {
        return autoText;
    }

    public void setAutoText(String autoText) {
        this.autoText = autoText;
    }
    
    @Override
    public String toString(){
        return autoText;
    }
}
