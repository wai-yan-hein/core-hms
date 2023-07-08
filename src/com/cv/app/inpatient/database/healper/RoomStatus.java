/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cv.app.inpatient.database.healper;

/**
 *
 * @author cv-svr
 */
public class RoomStatus {
    private Integer id;
    private String floor;
    private String code;
    private String room;
    private Double price;
    private String regNo;
    private String ptName;
    private boolean status;
    
    public RoomStatus(Integer id, String floor, String code, String room, 
            Double price, String regNo, String ptName, boolean status){
        this.id = id;
        this.floor = floor;
        this.code = code;
        this.room = room;
        this.price = price;
        this.regNo = regNo;
        this.ptName = ptName;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getPtName() {
        return ptName;
    }

    public void setPtName(String ptName) {
        this.ptName = ptName;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
