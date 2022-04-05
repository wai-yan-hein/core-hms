/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import com.cv.app.pharmacy.database.entity.Location;
import com.cv.app.pharmacy.database.entity.Medicine;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author WSwe
 */
@Embeddable
public class StockBalanceKey implements Serializable{
    private Medicine med;
    private Location location;
    private String userId;
    private Date expDate;
    private String tranOption;

    @ManyToOne
    @JoinColumn(name="med_id", nullable=false)
    public Medicine getMed() {
        return med;
    }

    public void setMed(Medicine med) {
        this.med = med;
    }

    @ManyToOne
    @JoinColumn(name="location_id", nullable=false)
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

     @Column(name="user_id", nullable=false)
    public String getUserId() {
        return userId;
    }
   
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="exp_date", nullable=false)
    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    @Column(name="tran_option", nullable=false)
    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }
}
