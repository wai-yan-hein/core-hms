/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "inp_service")
public class InpService implements java.io.Serializable {

    private Integer serviceId;
    private String serviceName;
    private Double fees;
    private Integer catId;
    private Integer priceVersionId;
    private Integer updateId;
    private Double fees1;//UHC_FEES
    private Boolean cfs;
    private Integer meduVersionId;
    private Integer updateMeduVId;
    private Double fees2;//MO_FEES
    private Double fees3;//STAFF_FEES
    private Double fees4;//NURSE_FEES
    private Double fees5;
    private boolean status;
    private String serviceCode;
    private Integer migId;
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "service_id", unique = true, nullable = false)
    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name = "service_name", unique = true, length = 50)
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Column(name = "srv_fees")
    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }

    @Column(name = "cat_id")
    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    @Column(name = "price_ver_id")
    public Integer getPriceVersionId() {
        return priceVersionId;
    }

    public void setPriceVersionId(Integer priceVersionId) {
        this.priceVersionId = priceVersionId;
    }

    @Column(name = "ver_upd_id")
    public Integer getUpdateId() {
        return updateId;
    }

    public void setUpdateId(Integer updateId) {
        this.updateId = updateId;
    }

    @Column(name = "srv_fees1")
    public Double getFees1() {
        return fees1;
    }

    public void setFees1(Double fees1) {
        this.fees1 = fees1;
    }

    @Column(name = "cfs")
    public Boolean isCfs() {
        return cfs;
    }

    public void setCfs(Boolean cfs) {
        this.cfs = cfs;
    }

    @Column(name = "medu_ver_id")
    public Integer getMeduVersionId() {
        return meduVersionId;
    }

    public void setMeduVersionId(Integer meduVersionId) {
        this.meduVersionId = meduVersionId;
    }

    @Column(name = "medu_upd_id")
    public Integer getUpdateMeduVId() {
        return updateMeduVId;
    }

    public void setUpdateMeduVId(Integer updateMeduVId) {
        this.updateMeduVId = updateMeduVId;
    }

    @Column(name = "srv_fees2")
    public Double getFees2() {
        return fees2;
    }

    public void setFees2(Double fees2) {
        this.fees2 = fees2;
    }

    @Column(name = "srv_fees3")
    public Double getFees3() {
        return fees3;
    }

    public void setFees3(Double fees3) {
        this.fees3 = fees3;
    }

    @Column(name = "srv_fees4")
    public Double getFees4() {
        return fees4;
    }

    public void setFees4(Double fees4) {
        this.fees4 = fees4;
    }

    @Column(name = "srv_fees5")
    public Double getFees5() {
        return fees5;
    }

    public void setFees5(Double fees5) {
        this.fees5 = fees5;
    }

    @Column(name = "act_status")
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Column(name="service_code", length=15)
    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    @Column(name="mig_id")
    public Integer getMigId() {
        return migId;
    }

    public void setMigId(Integer migId) {
        this.migId = migId;
    }
    
    @Override
    public String toString(){
        return serviceName;
    }
}
