/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.entity;

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
 * @author Cash
 */
@Entity
@Table(name = "booking_room")
public class RBooking implements java.io.Serializable {
    private Integer bookingId;
    private Date bookingDate;
    private String bookingName;
    private BuildingStructure buildingStructure;
    private String bookingRemark;
    private String bookingContact;
    private boolean checkStatus;

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="booking_id", unique=true, nullable=false)
    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }
    
    @Column(name="booking_name")
    public String getBookingName() {
        return bookingName;
    }

    public void setBookingName(String bookingName) {
        this.bookingName = bookingName;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "booking_date")
    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    @ManyToOne
    @JoinColumn(name = "room_id")
    public BuildingStructure getBuildingStructure() {
        return buildingStructure;
    }

    public void setBuildingStructure(BuildingStructure buildingStructure) {
        this.buildingStructure = buildingStructure;
    }

    @Column(name="remark")
    public String getBookingRemark() {
        return bookingRemark;
    }

    public void setBookingRemark(String bookingRemark) {
        this.bookingRemark = bookingRemark;
    }

    @Column(name="contact_no")
    public String getBookingContact() {
        return bookingContact;
    }

    public void setBookingContact(String bookingContact) {
        this.bookingContact = bookingContact;
    }

    @Column(name="check_status")
    public boolean isCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(boolean checkStatus) {
        this.checkStatus = checkStatus;
    }
    
     @Override
    public String toString(){
        return bookingName;
    }
    
}
