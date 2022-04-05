/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author WSwe
 */
@Embeddable
public class VSessionKey implements Serializable{
    private String invId;
    private String source;

    @Column(name="inv_id", nullable=false)
    public String getInvId() {
        return invId;
    }

    public void setInvId(String invId) {
        this.invId = invId;
    }

    @Column(name="source")
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.invId);
        hash = 11 * hash + Objects.hashCode(this.source);
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
        final VSessionKey other = (VSessionKey) obj;
        if (!Objects.equals(this.invId, other.invId)) {
            return false;
        }
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        return true;
    }
}
