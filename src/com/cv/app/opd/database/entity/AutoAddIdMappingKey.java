/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author MyintMo
 */
@Embeddable
public class AutoAddIdMappingKey implements Serializable{
    private String tranOption;
    private Integer tranServiceId;
    private Integer addServiceId;
    
    @Column(name="tran_option", length=15, nullable=false, unique=true)
    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }

    @Column(name="tran_service_id", nullable=false)
    public Integer getTranServiceId() {
        return tranServiceId;
    }

    public void setTranServiceId(Integer tranServiceId) {
        this.tranServiceId = tranServiceId;
    }

    @Column(name="add_service_id", nullable=false)
    public Integer getAddServiceId() {
        return addServiceId;
    }

    public void setAddServiceId(Integer addServiceId) {
        this.addServiceId = addServiceId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.tranOption);
        hash = 23 * hash + Objects.hashCode(this.tranServiceId);
        hash = 23 * hash + Objects.hashCode(this.addServiceId);
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
        final AutoAddIdMappingKey other = (AutoAddIdMappingKey) obj;
        if (!Objects.equals(this.tranOption, other.tranOption)) {
            return false;
        }
        if (!Objects.equals(this.tranServiceId, other.tranServiceId)) {
            return false;
        }
        if (!Objects.equals(this.addServiceId, other.addServiceId)) {
            return false;
        }
        return true;
    }
}
