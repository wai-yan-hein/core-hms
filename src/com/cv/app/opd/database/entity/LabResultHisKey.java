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
 * @author winswe
 */
@Embeddable
public class LabResultHisKey implements Serializable{
    private String opdInvId;
    private Integer labTestId; //service_id
    private Integer labResultId;
    private Integer uniqueId;

    @Column(name="opd_inv_id", nullable=false)
    public String getOpdInvId() {
        return opdInvId;
    }

    public void setOpdInvId(String opdInvId) {
        this.opdInvId = opdInvId;
    }

    @Column(name="lab_test_id", nullable=false)
    public Integer getLabTestId() {
        return labTestId;
    }

    public void setLabTestId(Integer labTestId) {
        this.labTestId = labTestId;
    }

    @Column(name="lab_result_id", nullable=false)
    public Integer getLabResultId() {
        return labResultId;
    }

    public void setLabResultId(Integer labResultId) {
        this.labResultId = labResultId;
    }

    @Column(name="unique_id", nullable=false)
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.opdInvId);
        hash = 89 * hash + Objects.hashCode(this.labTestId);
        hash = 89 * hash + Objects.hashCode(this.labResultId);
        hash = 89 * hash + Objects.hashCode(this.uniqueId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LabResultHisKey other = (LabResultHisKey) obj;
        if (!Objects.equals(this.opdInvId, other.opdInvId)) {
            return false;
        }
        if (!Objects.equals(this.labTestId, other.labTestId)) {
            return false;
        }
        if (!Objects.equals(this.labResultId, other.labResultId)) {
            return false;
        }
        if (!Objects.equals(this.uniqueId, other.uniqueId)) {
            return false;
        }
        return true;
    }
}
