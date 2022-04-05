/*
 * To change this template, choose Tools | Templates
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
 * @author WSwe
 */
@Entity
@Table(name="opd_group")
public class OPDGroup implements java.io.Serializable{
    private Integer groupId;
    private String groupName;
    
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="group_id", unique=true, nullable=false)
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Column(name="group_name", unique=true, length=15)
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    @Override
    public String toString(){
        return groupName;
    }
}
