/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cv.app.opd.database.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author cv-svr
 */
@Embeddable
public class BTDKey implements Serializable{
    private String bthId;
    private String regNo;
    private Integer uniqueId;

    @Column(name="bth_id", length=15)
    public String getBthId() {
        return bthId;
    }

    public void setBthId(String bthId) {
        this.bthId = bthId;
    }

    @Column(name="reg_no", length=15)
    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    @Column(name="unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.bthId);
        hash = 53 * hash + Objects.hashCode(this.regNo);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BTDKey other = (BTDKey) obj;
        if (!Objects.equals(this.bthId, other.bthId)) {
            return false;
        }
        return Objects.equals(this.regNo, other.regNo);
    }
}
