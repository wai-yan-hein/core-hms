/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

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
@Table(name="item_group")
public class ItemGroup implements java.io.Serializable {
    private Integer groupId;
    private String groupName;
    private Integer parentGroupId;

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="group_id", unique=true, nullable=false)
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

    @Column(name="parent_group_id")
    public Integer getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(Integer parentGroupId) {
        this.parentGroupId = parentGroupId;
    }
    
    @Override
    public String toString(){
        return groupName;
    }
}
