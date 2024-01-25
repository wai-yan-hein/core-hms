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
    private Float lowValue;
    private Float highValue;
    private Gender sex;
    private String lowVOperator;
    private String highVOperator;
    private Integer ageFrom;
    private Integer ageTo;
    private String afOperator;
    private String atOperator;
    private LabMachine machine;
    private String desp;

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

    @Column(name="low_value")
    public Float getLowValue() {
        return lowValue;
    }

    public void setLowValue(Float lowValue) {
        this.lowValue = lowValue;
    }

    @Column(name="high_value")
    public Float getHighValue() {
        return highValue;
    }

    public void setHighValue(Float highValue) {
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

    @Column(name="low_v_oper", length=15)
    public String getLowVOperator() {
        return lowVOperator;
    }

    public void setLowVOperator(String lowVOperator) {
        this.lowVOperator = lowVOperator;
    }

    @Column(name="high_v_oper", length=15)
    public String getHighVOperator() {
        return highVOperator;
    }

    public void setHighVOperator(String highVOperator) {
        this.highVOperator = highVOperator;
    }

    @Column(name="age_from")
    public Integer getAgeFrom() {
        return ageFrom;
    }

    public void setAgeFrom(Integer ageFrom) {
        this.ageFrom = ageFrom;
    }

    @Column(name="age_to")
    public Integer getAgeTo() {
        return ageTo;
    }

    public void setAgeTo(Integer ageTo) {
        this.ageTo = ageTo;
    }

    @Column(name="af_oper", length=15)
    public String getAfOperator() {
        return afOperator;
    }

    public void setAfOperator(String afOperator) {
        this.afOperator = afOperator;
    }

    @Column(name="at_oper", length=15)
    public String getAtOperator() {
        return atOperator;
    }

    public void setAtOperator(String atOperator) {
        this.atOperator = atOperator;
    }

    @ManyToOne
    @JoinColumn(name = "machine_id")
    public LabMachine getMachine() {
        return machine;
    }

    public void setMachine(LabMachine machine) {
        this.machine = machine;
    }

    @Column(name="desp", length=500)
    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    
}
