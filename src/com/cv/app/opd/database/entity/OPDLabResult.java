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
@Table(name="opd_lab_result")
public class OPDLabResult implements java.io.Serializable{
    
    private Integer resultId;
    private Integer serviceId;
    private String resultText;
    private String resultUnit;
    private String resultNormal;
    private OPDResultType resultType;
    private Integer migId;
    private Integer sortOrder;
    private String labResultRemark;
    
    @GeneratedValue(strategy=IDENTITY)
    @Id @Column(name="result_id", unique=true, nullable=false)
    public Integer getResultId() {
        return resultId;
    }

    public void setResultId(Integer resultId) {
        this.resultId = resultId;
    }

    @Column(name="service_id")
    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name="result_text", length=500)
    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    @Column(name="result_unit", length=50)
    public String getResultUnit() {
        return resultUnit;
    }

    public void setResultUnit(String resultUnit) {
        this.resultUnit = resultUnit;
    }

    @Column(name="result_normal", length=50)
    public String getResultNormal() {
        return resultNormal;
    }

    public void setResultNormal(String resultNormal) {
        this.resultNormal = resultNormal;
    }

    @ManyToOne
    @JoinColumn(name = "result_type")
    public OPDResultType getResultType() {
        return resultType;
    }

    public void setResultType(OPDResultType resultType) {
        this.resultType = resultType;
    }

    @Column(name="mig_id")
    public Integer getMigId() {
        return migId;
    }

    public void setMigId(Integer migId) {
        this.migId = migId;
    }

    @Column(name="sort_order")
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Column(name="lab_result_remark")
    public String getLabResultRemark() {
        return labResultRemark;
    }

    public void setLabResultRemark(String labResultRemark) {
        this.labResultRemark = labResultRemark;
    }
    
    
}
