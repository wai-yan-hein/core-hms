/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
 * @author ACER
 */
@Entity
@Table(name="stock_check_detail")
public class StockCheckDetail implements java.io.Serializable {
    private Long tranId;
    private String batchNo;
    private String medId;
    private Float ttlQty;
    private Integer locationId;

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="tran_id", unique=true, nullable=false)
    public Long getTranId() {
        return tranId;
    }

    public void setTranId(Long tranId) {
        this.tranId = tranId;
    }

    @Column(name="batch_no", length=15)
    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    @Column(name="med_id", length=15)
    public String getMedId() {
        return medId;
    }
    
    public void setMedId(String medId) {
        this.medId = medId;
    }

    @Column(name="ttl_qty")
    public Float getTtlQty() {
        return ttlQty;
    }

    public void setTtlQty(Float ttlQty) {
        this.ttlQty = ttlQty;
    }

    @Column(name="location_id")
    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
}
