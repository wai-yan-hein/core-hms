/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author winswe
 */
@Embeddable
public class SessionFilterKey implements Serializable{
    private String progId;
    private String tranSource;

    @Column(name="program_id", nullable=false, length=15)
    public String getProgId() {
        return progId;
    }

    public void setProgId(String progId) {
        this.progId = progId;
    }

    @Column(name="tran_source", nullable=false, length=15)
    public String getTranSource() {
        return tranSource;
    }

    public void setTranSource(String tranSource) {
        this.tranSource = tranSource;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Objects.hashCode(this.progId);
        hash = 19 * hash + Objects.hashCode(this.tranSource);
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
        final SessionFilterKey other = (SessionFilterKey) obj;
        if (!Objects.equals(this.progId, other.progId)) {
            return false;
        }
        if (!Objects.equals(this.tranSource, other.tranSource)) {
            return false;
        }
        return true;
    }
}
