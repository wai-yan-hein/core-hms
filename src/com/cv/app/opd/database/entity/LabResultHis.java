/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="lab_result_his")
public class LabResultHis implements java.io.Serializable{
    private LabResultHisKey key;
    private String resultText;
    private String result;
    private String refRange;
    private String resultUnit;
    private String remark;
    private LabResultMethod method;
    private Boolean print;
    private Integer resultType;
    private Integer sortOrder;
    private String labRemark;
    
    @EmbeddedId
    public LabResultHisKey getKey() {
        return key;
    }

    public void setKey(LabResultHisKey key) {
        this.key = key;
    }

    @Column(name="result_text", length=500)
    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    @Column(name="result", length=500)
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Column(name="ref_range", length=50)
    public String getRefRange() {
        return refRange;
    }

    public void setRefRange(String refRange) {
        this.refRange = refRange;
    }

    @Column(name="result_unit", length=50)
    public String getResultUnit() {
        return resultUnit;
    }

    public void setResultUnit(String resultUnit) {
        this.resultUnit = resultUnit;
    }

    @Column(name="remark", length=150)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @ManyToOne
    @JoinColumn(name="method")
    public LabResultMethod getMethod() {
        return method;
    }

    public void setMethod(LabResultMethod method) {
        this.method = method;
    }
   
    @Transient
    public Boolean getPrint() {
        return print;
    }

    public void setPrint(Boolean print) {
        this.print = print;
    }

    @Column(name="result_type")
    public Integer getResultType() {
        return resultType;
    }

    public void setResultType(Integer resultType) {
        this.resultType = resultType;
    }

    @Column(name="sort_order")
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Column(name="lab_remark", length=2500)
    public String getLabRemark() {
        return labRemark;
    }

    public void setLabRemark(String labRemark) {
        this.labRemark = labRemark;
    }
}
