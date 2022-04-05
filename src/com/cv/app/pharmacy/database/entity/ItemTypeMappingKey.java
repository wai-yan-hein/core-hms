/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author winswe
 */
@Embeddable
public class ItemTypeMappingKey implements Serializable{
    private Integer groupId;
    private ItemType itemType;

    @Column(name="group_id")
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @ManyToOne
    @JoinColumn(name="item_type_code")
    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.groupId);
        hash = 23 * hash + Objects.hashCode(this.itemType);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemTypeMappingKey other = (ItemTypeMappingKey) obj;
        if (!Objects.equals(this.groupId, other.groupId)) {
            return false;
        }
        if (!Objects.equals(this.itemType, other.itemType)) {
            return false;
        }
        return true;
    }
}
