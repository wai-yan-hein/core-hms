/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author MyintMo
 */
@Entity
@Table(name="tmp_item_code_filter_rpt")
public class ItemCodeFilterRpt implements Serializable{
    private Long id;
    private Medicine item;
    private ItemUnit unit;
    private String userId;
    private int noOfCopy = 1;
    
    @Id 
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="id", unique=true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name="item_code", nullable=false)
    public Medicine getItem() {
        return item;
    }

    public void setItem(Medicine item) {
        this.item = item;
    }

    @ManyToOne
    @JoinColumn(name="unit_short")
    public ItemUnit getUnit() {
        return unit;
    }

    public void setUnit(ItemUnit unit) {
        this.unit = unit;
    }

    @Column(name="user_id", length=15)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name="no_of_copy")
    public int getNoOfCopy() {
        return noOfCopy;
    }

    public void setNoOfCopy(int noOfCopy) {
        this.noOfCopy = noOfCopy;
    }
}
