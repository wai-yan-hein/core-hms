/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cv.app.opd.database.helper;

/**
 *
 * @author macbook
 */
public class OPDServiceView {
    private Integer groupId;
    private String groupName;
    private Integer catId;
    private String catName;
    private Integer serviceId;
    private String serviceName;

    public OPDServiceView(Integer groupId, String groupName, Integer catId,
            String catName, Integer serviceId, String serviceName){
        this.groupId = groupId;
        this.groupName = groupName;
        this.catId = catId;
        this.catName = catName;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }
    
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getCatId() {
        return catId;
    }

    public void setCatId(Integer catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
}
