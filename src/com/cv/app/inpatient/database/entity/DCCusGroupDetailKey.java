/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.database.entity;

import com.cv.app.opd.database.entity.*;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author PHSH MDY
 */
@Embeddable
public class DCCusGroupDetailKey implements Serializable {
    private Integer cusGrpId;
    private InpCategory opdCatId;

    @Column(name="cus_grp_id", nullable=false)
    public Integer getCusGrpId() {
        return cusGrpId;
    }

    public void setCusGrpId(Integer cusGrpId) {
        this.cusGrpId = cusGrpId;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="dc_cat_id")
    public InpCategory getOpdCatId() {
        return opdCatId;
    }

    public void setOpdCatId(InpCategory opdCatId) {
        this.opdCatId = opdCatId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.cusGrpId);
        hash = 23 * hash + Objects.hashCode(this.opdCatId);
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
        final DCCusGroupDetailKey other = (DCCusGroupDetailKey) obj;
        if (!Objects.equals(this.cusGrpId, other.cusGrpId)) {
            return false;
        }
        if (!Objects.equals(this.opdCatId, other.opdCatId)) {
            return false;
        }
        return true;
    }
}
