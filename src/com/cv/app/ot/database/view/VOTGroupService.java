/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.database.view;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author user
 */
@Entity
@Table(name = "v_ot_group_service")
public class VOTGroupService implements java.io.Serializable{
    private Integer srvId;
    private Integer groupId;
    private String groupName;
    private String srvName;
    private Boolean actStatus;

    @Id
    @Column(name="service_id")
    public Integer getSrvId() {
        return srvId;
    }

    public void setSrvId(Integer srvId) {
        this.srvId = srvId;
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

    @Column(name="service_name")
    public String getSrvName() {
        return srvName;
    }

    public void setSrvName(String srvName) {
        this.srvName = srvName;
    }

    @Column(name="act_status")
    public Boolean getActStatus() {
        return actStatus;
    }

    public void setActStatus(Boolean actStatus) {
        this.actStatus = actStatus;
    }
}
