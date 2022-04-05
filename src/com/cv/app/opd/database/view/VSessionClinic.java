/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.view;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "v_session_clinic")
public class VSessionClinic implements java.io.Serializable{
    private String key;
    private String tranOption;
    private Date tranDate;
    private String vouId;
    private String ptId;
    private String ptName;
    private String drName;
    private String drId;
    private String currency;
    private boolean deleted;
    private Double vouTotal;
    private Double discount;
    private Double tax;
    private Double paid;
    private Double vouBalance;
    private Integer session;
    private String user;
    private Integer payType;
    private String sessionName;
    private String payTypeDesp;
    private String userShort;
    
    @Id
    @Column(name="key_id")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Column(name="tran_option")
    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="tran_date")
    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    @Column(name="opd_inv_id")
    public String getVouId() {
        return vouId;
    }

    public void setVouId(String vouId) {
        this.vouId = vouId;
    }

    @Column(name="patient_id")
    public String getPtId() {
        return ptId;
    }

    public void setPtId(String ptId) {
        this.ptId = ptId;
    }

    @Column(name="pt_name")
    public String getPtName() {
        return ptName;
    }

    public void setPtName(String ptName) {
        this.ptName = ptName;
    }

    @Column(name="doctor_name")
    public String getDrName() {
        return drName;
    }

    public void setDrName(String drName) {
        this.drName = drName;
    }

    @Column(name="currency_id")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name="deleted")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name="vou_total")
    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    @Column(name="disc_a")
    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    @Column(name="tax_a")
    public Double getTax() {
        return tax;
    }

    public void setTax(Double tax) {
        this.tax = tax;
    }

    @Column(name="paid")
    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    @Column(name="vou_balance")
    public Double getVouBalance() {
        return vouBalance;
    }

    public void setVouBalance(Double vouBalance) {
        this.vouBalance = vouBalance;
    }

    @Column(name="session_id")
    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    @Column(name="user_id")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Column(name="doctor_id")
    public String getDrId() {
        return drId;
    }

    public void setDrId(String drId) {
        this.drId = drId;
    }

    @Column(name="payment_type_id")
    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    @Column(name="session_name")
    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    @Column(name="payment_type_name")
    public String getPayTypeDesp() {
        return payTypeDesp;
    }

    public void setPayTypeDesp(String payTypeDesp) {
        this.payTypeDesp = payTypeDesp;
    }

    @Column(name="user_short_name")
    public String getUserShort() {
        return userShort;
    }

    public void setUserShort(String userShort) {
        this.userShort = userShort;
    }
}
