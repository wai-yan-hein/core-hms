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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ACER
 */
@Entity
@Table(name="dc_room_transfer_his")
public class DCRoomTransferHis implements java.io.Serializable{
    private Long id;
    private String regNo;
    private String admissionNo;
    private Integer fromRoom;
    private Integer toRoom;
    private Date tranDate;
    private String userId;

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="reg_no", length=15)
    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    @Column(name="admission_no", length=15)
    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    @Column(name="from_room")
    public Integer getFromRoom() {
        return fromRoom;
    }

    public void setFromRoom(Integer fromRoom) {
        this.fromRoom = fromRoom;
    }

    @Column(name="to_room")
    public Integer getToRoom() {
        return toRoom;
    }

    public void setToRoom(Integer toRoom) {
        this.toRoom = toRoom;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "tran_date", nullable = false)
    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    @Column(name="user_id", length=15)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
}
