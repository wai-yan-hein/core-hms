/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.view;

/**
 *
 * @author Cash
 */


import javax.persistence.*;


@Entity
@Table(name = "v_addservice")
public class VAddService implements java.io.Serializable{
    
    private String tranOption;
    private Integer tranServiceId;
    private Integer addServiceId;
    private Integer addQty;
    private Integer sortOrder;
    
    @Id
    @Column(name="tran_option")
    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }

    @Id
    @Column(name="tran_service_id")
    public Integer getTranServiceId() {
        return tranServiceId;
    }

    public void setTranServiceId(Integer tranServiceId) {
        this.tranServiceId = tranServiceId;
    }

    @Id
    @Column(name="add_service_id")
    public Integer getAddServiceId() {
        return addServiceId;
    }

    public void setAddServiceId(Integer addServiceId) {
        this.addServiceId = addServiceId;
    }

    @Column(name="add_qty")
    public Integer getAddQty() {
        return addQty;
    }

    public void setAddQty(Integer addQty) {
        this.addQty = addQty;
    }

    @Column(name="sort_order")
    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
