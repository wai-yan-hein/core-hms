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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="opd_lab_result_ind")
public class OPDLabResultInd implements java.io.Serializable{
    
    private Integer indId;
    private Integer resultId;
    private String lowValue;
    private String highValue;
    private Gender sex;

    @GeneratedValue(strategy=IDENTITY)
    @Id @Column(name="ind_id", unique=true, nullable=false)
    public Integer getIndId() {
        return indId;
    }

    public void setIndId(Integer indId) {
        this.indId = indId;
    }

    @Column(name="result_id")
    public Integer getResultId() {
        return resultId;
    }

    public void setResultId(Integer resultId) {
        this.resultId = resultId;
    }

    @Column(name="low_value", length=50)
    public String getLowValue() {
        return lowValue;
    }

    public void setLowValue(String lowValue) {
        this.lowValue = lowValue;
    }

    @Column(name="high_value", length=50)
    public String getHighValue() {
        return highValue;
    }

    public void setHighValue(String highValue) {
        this.highValue = highValue;
    }

    @ManyToOne
    @JoinColumn(name = "sex")
    public Gender getSex() {
        return sex;
    }

    public void setSex(Gender sex) {
        this.sex = sex;
    }
    
    
}
