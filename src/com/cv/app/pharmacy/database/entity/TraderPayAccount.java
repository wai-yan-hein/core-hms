/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

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
@Table(name = "trader_pay_account")
public class TraderPayAccount implements java.io.Serializable {

    private Integer payId;
    private String account;
    private String desp;
    private Boolean status;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "pay_id", unique = true, nullable = false)
    public Integer getPayId() {
        return payId;
    }

    public void setPayId(Integer payId) {
        this.payId = payId;
    }

    @Column(name = "acc_id", nullable = false, length = 15)
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Column(name = "desp", length = 100)
    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    @Column(name = "active")
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return desp;
    }
}
