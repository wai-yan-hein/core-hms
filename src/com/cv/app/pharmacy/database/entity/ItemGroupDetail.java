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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="item_group_detail")
public class ItemGroupDetail implements java.io.Serializable {
    private Integer groupDetailId;
    private Medicine item;
    private Integer groupId;

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="grp_detail_id", unique=true, nullable=false)
    public Integer getGroupDetailId() {
        return groupDetailId;
    }

    public void setGroupDetailId(Integer groupDetailId) {
        this.groupDetailId = groupDetailId;
    }

    @ManyToOne
    @JoinColumn(name="item_id")
    public Medicine getItem() {
        return item;
    }

    public void setItem(Medicine item) {
        this.item = item;
    }

    @Column(name="group_id")
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
