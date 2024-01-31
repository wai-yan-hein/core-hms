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
 * @author winswe
 */
@Embeddable
public class MUKey implements Serializable{
    private String vouType;
    private String vouNo;
    private Integer serviceId;
    private String medId;

    @Column(name="vou_type")
    public String getVouType() {
        return vouType;
    }

    public void setVouType(String vouType) {
        this.vouType = vouType;
    }

    @Column(name="vou_no")
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }

    @Column(name="service_id")
    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    @Column(name="med_id")
    public String getMedId() {
        return medId;
    }

    public void setMedId(String medId) {
        this.medId = medId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.vouType);
        hash = 29 * hash + Objects.hashCode(this.vouNo);
        hash = 29 * hash + Objects.hashCode(this.serviceId);
        hash = 29 * hash + Objects.hashCode(this.medId);
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
        final MUKey other = (MUKey) obj;
        if (!Objects.equals(this.vouType, other.vouType)) {
            return false;
        }
        if (!Objects.equals(this.vouNo, other.vouNo)) {
            return false;
        }
        if (!Objects.equals(this.medId, other.medId)) {
            return false;
        }
        return Objects.equals(this.serviceId, other.serviceId);
    }
}
