/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 *
 * @author Eitar
 */
@Entity
@Table(name="v_pur_promo")
public class VPurPromo implements java.io.Serializable{
    private String purInvId;
    private Long purDetailId;
    private Date purDate;
    private String medId;
    private String medName;
    private String promoDesp;
    private Date promoStartDate;
    private Date promoEndDate;
    private Float promoGivePercent;
    private Float promoGetPercent;
    private boolean promoGetComplete;
    private String cusId;
    private String cusName;

    @Id
    @Column(name="pur_inv_id")
    public String getPurInvId() {
        return purInvId;
    }

    public void setPurInvId(String purInvId) {
        this.purInvId = purInvId;
    }

    @Column(name="pur_detail_id")
    public Long getPurDetailId() {
        return purDetailId;
    }

    public void setPurDetailId(Long purDetailId) {
        this.purDetailId = purDetailId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="pur_date", nullable=false)
    public Date getPurDate() {
        return purDate;
    }

    public void setPurDate(Date purDate) {
        this.purDate = purDate;
    }

    @Column(name="code")
    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    @Column(name="name")
    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    @Column(name="promo_desp", length=500)
    public String getPromoDesp() {
        return promoDesp;
    }

    public void setPromoDesp(String promoDesp) {
        this.promoDesp = promoDesp;
    }

    @Column(name="promo_start_date")
    @Temporal(TemporalType.DATE)
    public Date getPromoStartDate() {
        return promoStartDate;
    }

    public void setPromoStartDate(Date promoStartDate) {
        this.promoStartDate = promoStartDate;
    }

    @Column(name="promo_end_date")
    @Temporal(TemporalType.DATE)
    public Date getPromoEndDate() {
        return promoEndDate;
    }

    public void setPromoEndDate(Date promoEndDate) {
        this.promoEndDate = promoEndDate;
    }

    @Column(name="promo_give_percent")
    public Float getPromoGivePercent() {
        return promoGivePercent;
    }

    public void setPromoGivePercent(Float promoGivePercent) {
        this.promoGivePercent = promoGivePercent;
    }

    @Column(name="promo_get_percent")
    public Float getPromoGetPercent() {
        return promoGetPercent;
    }

    public void setPromoGetPercent(Float promoGetPercent) {
        this.promoGetPercent = promoGetPercent;
    }

    @Column(name="promo_get_complete")
    public boolean isPromoGetComplete() {
        return promoGetComplete;
    }

    public void setPromoGetComplete(boolean promoGetComplete) {
        this.promoGetComplete = promoGetComplete;
    }

    @Column(name="cus_id")
    public String getCusId() {
        return cusId;
    }

    public void setCusId(String cusId) {
        this.cusId = cusId;
    }

    @Column(name="trader_name")
    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }
    
    
}
