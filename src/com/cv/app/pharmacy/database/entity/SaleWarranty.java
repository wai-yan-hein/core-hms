/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "sale_warranty")
public class SaleWarranty implements java.io.Serializable{
    private Integer WarrantyId;
    private Medicine item;
    private String serialNo;
    private Date startDate;
    private Date endDate;
    private String warranty;
    private String vouNo;
    
    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="warranty_id", unique=true, nullable=false)
    public Integer getWarrantyId() {
        return WarrantyId;
    }

    public void setWarrantyId(Integer WarrantyId) {
        this.WarrantyId = WarrantyId;
    }

    @ManyToOne
    @JoinColumn(name = "item_id")
    public Medicine getItem() {
        return item;
    }

    public void setItem(Medicine item) {
        this.item = item;
    }

    @Column(name="serial_no", length=25)
    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="start_date")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="end_date")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name="warranty", length=15)
    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }
    
    @Column(name="vou_no", length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }
}
