/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.view;

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
@Table(name="v_opd_service")
public class VService implements java.io.Serializable{
    private Integer serviceId;
    private String serviceName;
    private Double fees;
    private Integer catId;
    private Integer priceVersionId;
    private Integer updateId;
    private Double fees1;
    private boolean cfs;
    private Integer meduVersionId;
    private Integer updateMeduVId;
    private String serviceCode;
    private boolean status;
    private Integer groupId;
    private String groupName;
    private String catName;
    private Integer migId;
    private String labRemark;
    private Integer cusGroupId;
    private Double cost;
    
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="service_id", unique=true, nullable=false)
    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name="service_name", unique=true, length=50)
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Column(name="srv_fees")
    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }

    @Column(name="cat_id")
    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

  @Column(name="price_ver_id")
  public Integer getPriceVersionId() {
    return priceVersionId;
  }

  public void setPriceVersionId(Integer priceVersionId) {
    this.priceVersionId = priceVersionId;
  }

    @Column(name="ver_upd_id")
    public Integer getUpdateId() {
        return updateId;
    }

    public void setUpdateId(Integer updateId) {
        this.updateId = updateId;
    }

    @Column(name="srv_fees1")
    public Double getFees1() {
        return fees1;
    }

    public void setFees1(Double fees1) {
        this.fees1 = fees1;
    }

    @Column(name="cfs")
  public boolean isCfs() {
    return cfs;
  }

  public void setCfs(boolean cfs) {
    this.cfs = cfs;
  }

  @Column(name="medu_ver_id")
    public Integer getMeduVersionId() {
        return meduVersionId;
    }

    public void setMeduVersionId(Integer meduVersionId) {
        this.meduVersionId = meduVersionId;
    }

    @Column(name="medu_upd_id")
    public Integer getUpdateMeduVId() {
        return updateMeduVId;
    }

    public void setUpdateMeduVId(Integer updateMeduVId) {
        this.updateMeduVId = updateMeduVId;
    }

    @Column(name="service_code", length=15)
    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    @Column(name="act_status")
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Column(name="group_id")
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Column(name="group_name")
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Column(name="cat_name")
    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    @Column(name="mig_id")
    public Integer getMigId() {
        return migId;
    }

    public void setMigId(Integer migId) {
        this.migId = migId;
    }

    @Column(name="lab_remark")
    public String getLabRemark() {
        return labRemark;
    }

    public void setLabRemark(String labRemark) {
        this.labRemark = labRemark;
    }

    @Column(name="cus_group_id")
    public Integer getCusGroupId() {
        return cusGroupId;
    }

    public void setCusGroupId(Integer cusGroupId) {
        this.cusGroupId = cusGroupId;
    }

    @Column(name="service_cost")
    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
    
    
}
