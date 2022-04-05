/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "session_filter")
public class SessionFilter implements java.io.Serializable{
    private SessionFilterKey key;
    private String rptParameter;
    private boolean apply;
    
    @EmbeddedId
    public SessionFilterKey getKey() {
        return key;
    }

    public void setKey(SessionFilterKey key) {
        this.key = key;
    }

    @Column(name="rpt_parameter", length=50)
    public String getRptParameter() {
        return rptParameter;
    }

    public void setRptParameter(String rptParameter) {
        this.rptParameter = rptParameter;
    }

    @Column(name="apply")
    public boolean isApply() {
        return apply;
    }

    public void setApply(boolean apply) {
        this.apply = apply;
    }
}
