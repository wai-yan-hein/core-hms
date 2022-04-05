/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.view;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author winswe
 */
@Embeddable
public class VOpdKey implements Serializable{
    private String vouNo;
    private Integer serviceId;
    private Integer uniqueId;
    private String opdDetailId;
    
    @Column(name="opd_inv_id")
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

    @Column(name="unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Column(name="opd_detail_id")
    public String getOpdDetailId() {
        return opdDetailId;
    }

    public void setOpdDetailId(String opdDetailId) {
        this.opdDetailId = opdDetailId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.vouNo);
        hash = 79 * hash + Objects.hashCode(this.serviceId);
        hash = 79 * hash + Objects.hashCode(this.uniqueId);
        hash = 79 * hash + Objects.hashCode(this.opdDetailId);
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
        final VOpdKey other = (VOpdKey) obj;
        if (!Objects.equals(this.vouNo, other.vouNo)) {
            return false;
        }
        if (!Objects.equals(this.serviceId, other.serviceId)) {
            return false;
        }
        if (!Objects.equals(this.uniqueId, other.uniqueId)) {
            return false;
        }
        if (!Objects.equals(this.opdDetailId, other.opdDetailId)) {
            return false;
        }
        return true;
    }
    
}
