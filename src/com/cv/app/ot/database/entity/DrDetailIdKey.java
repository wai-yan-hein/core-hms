/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.database.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author MyintMo
 */
@Embeddable
public class DrDetailIdKey implements Serializable{
    private String option;
    private Integer serviceId;

    @Column(name="option", length=15, nullable=false, unique=true)
    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    @Column(name="service_id", nullable=false, unique=true)
    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.option);
        hash = 83 * hash + Objects.hashCode(this.serviceId);
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
        final DrDetailIdKey other = (DrDetailIdKey) obj;
        if (!Objects.equals(this.option, other.option)) {
            return false;
        }
        if (!Objects.equals(this.serviceId, other.serviceId)) {
            return false;
        }
        return true;
    }
}
