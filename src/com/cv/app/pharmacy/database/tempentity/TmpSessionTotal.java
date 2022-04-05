/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "tmp_session_total")
public class TmpSessionTotal implements java.io.Serializable {
    
    private Long id;
    private String desp;
    private String currency;
    private Double amount;
    private String userId;

    public TmpSessionTotal(){}
    
    public TmpSessionTotal(String desp, String currency, Double amount, String userId){
        this.desp = desp;
        this.currency = currency;
        this.amount = amount;
        this.userId = userId;
    }
    
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name="desp", length=100)
    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    @Column(name="curr_code", length=5)
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name="amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Column(name="user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
}
