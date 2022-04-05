/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="ot_service")
public class OTProcedure implements java.io.Serializable{
    private Integer serviceId;
    private String serviceName;
    private String serviceCode;
    private Integer groupId;
    private Double srvFees;
    private Double srvFees1;
    private Double srvFees2;
    private Double srvFees3;
    private Double srvFees4;
    private Double srvFees5;
    private boolean status;
    private Integer migId;
    private Boolean cfs;
    
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="service_id", unique=true, nullable=false)
    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name="service_name", length=50)
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Column(name="service_code", length=15)
    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    @Column(name="group_id")
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Column(name="srv_fees")
    public Double getSrvFees() {
        return srvFees;
    }

    public void setSrvFees(Double srvFees) {
        this.srvFees = srvFees;
    }

    @Column(name="srv_fees1")
    public Double getSrvFees1() {
        return srvFees1;
    }

    public void setSrvFees1(Double srvFees1) {
        this.srvFees1 = srvFees1;
    }

    @Column(name="srv_fees2")
    public Double getSrvFees2() {
        return srvFees2;
    }

    public void setSrvFees2(Double srvFees2) {
        this.srvFees2 = srvFees2;
    }

    @Column(name="srv_fees3")
    public Double getSrvFees3() {
        return srvFees3;
    }

    public void setSrvFees3(Double srvFees3) {
        this.srvFees3 = srvFees3;
    }

    @Column(name="srv_fees4")
    public Double getSrvFees4() {
        return srvFees4;
    }

    public void setSrvFees4(Double srvFees4) {
        this.srvFees4 = srvFees4;
    }

    @Column(name="srv_fees5")
    public Double getSrvFees5() {
        return srvFees5;
    }

    public void setSrvFees5(Double srvFees5) {
        this.srvFees5 = srvFees5;
    }

    @Column(name="act_status")
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Column(name="mig_id")
    public Integer getMigId() {
        return migId;
    }

    public void setMigId(Integer migId) {
        this.migId = migId;
    }

    @Column(name="cfs")
    public Boolean isCfs() {
        return cfs;
    }

    public void setCfs(Boolean cfs) {
        this.cfs = cfs;
    }
    
    @Override
    public String toString(){
        return serviceName;
    }
}
