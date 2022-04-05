/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "pur_imei_no")
public class PurchaseIMEINo implements Serializable{
    
    private PurchaseIMEINoKey key;
    private String imei2;
    private String sdNo;
    private String saleVouNo;
    private Long saleDetailId;
    
    public PurchaseIMEINo(){
        key = new PurchaseIMEINoKey();
    }
    
    @EmbeddedId
    public PurchaseIMEINoKey getKey() {
        return key;
    }

    public void setKey(PurchaseIMEINoKey key) {
        this.key = key;
    }

    @Column(name="imei2", length=50)
    public String getImei2() {
        return imei2;
    }

    public void setImei2(String imei2) {
        this.imei2 = imei2;
    }

    @Column(name="sd_no", length=50)
    public String getSdNo() {
        return sdNo;
    }

    public void setSdNo(String sdNo) {
        this.sdNo = sdNo;
    }

    @Column(name="sale_inv_id", length=15)
    public String getSaleVouNo() {
        return saleVouNo;
    }

    public void setSaleVouNo(String saleVouNo) {
        this.saleVouNo = saleVouNo;
    }

    @Column(name="sale_detail_id")
    public Long getSaleDetailId() {
        return saleDetailId;
    }

    public void setSaleDetailId(Long saleDetailId) {
        this.saleDetailId = saleDetailId;
    }
}
