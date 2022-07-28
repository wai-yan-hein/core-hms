/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "v_session")
public class VSession implements java.io.Serializable {

    private VSessionKey key;
    private Date tranDate;
    private String traderId;
    private String traderName;
    private Double vouTotal;
    private Double paid;
    private Double discount;
    private Double balance;
    private Double expense;
    private String userId;
    private String userShortName;
    private String machineId;
    private Integer locationId;
    private boolean deleted;
    private String currency;
    private Integer sessionId;
    private String paidCurrency;
    private Double paidCurrAmt;
    private String discriminator;
    private Integer paymentType;
    private Double taxP;
    private Double taxAmt;
    private String refNo;
    private Integer expType;
    private String expTypeDesp;
    private Double expIn;
    private String intgUpdStatus;
    private String sessionName;
    private String payType;
    private String groupId;
    private String admissionNo;
    private String accountId;
    private String stuNo;
    private String doctorId;
    
    @Column(name = "balance")
    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    @Column(name = "currency")
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(name = "deleted")
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Column(name = "discount")
    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    @Column(name = "expense")
    public Double getExpense() {
        return expense;
    }

    public void setExpense(Double expense) {
        this.expense = expense;
    }

    @Column(name = "location_id")
    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @Column(name = "machine")
    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    @Column(name = "paid")
    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    @Column(name = "cus_id")
    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    @Column(name = "trader_name")
    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "tran_date")
    public Date getTranDate() {
        return tranDate;
    }

    public void setTranDate(Date tranDate) {
        this.tranDate = tranDate;
    }

    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "user_short_name")
    public String getUserShortName() {
        return userShortName;
    }

    public void setUserShortName(String userShortName) {
        this.userShortName = userShortName;
    }

    @Column(name = "vou_total")
    public Double getVouTotal() {
        return vouTotal;
    }

    public void setVouTotal(Double vouTotal) {
        this.vouTotal = vouTotal;
    }

    @Column(name = "session_id")
    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    @EmbeddedId
    public VSessionKey getKey() {
        return key;
    }

    public void setKey(VSessionKey key) {
        this.key = key;
    }

    @Column(name = "paid_currency_amt")
    public Double getPaidCurrAmt() {
        return paidCurrAmt;
    }

    public void setPaidCurrAmt(Double paidCurrAmt) {
        this.paidCurrAmt = paidCurrAmt;
    }

    @Column(name = "paid_currency")
    public String getPaidCurrency() {
        return paidCurrency;
    }

    public void setPaidCurrency(String paidCurrency) {
        this.paidCurrency = paidCurrency;
    }

    @Column(name = "discriminator")
    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    @Column(name = "payment_type")
    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    @Column(name = "tax_p")
    public Double getTaxP() {
        return taxP;
    }

    public void setTaxP(Double taxP) {
        this.taxP = taxP;
    }

    @Column(name = "tax_amt")
    public Double getTaxAmt() {
        return taxAmt;
    }

    public void setTaxAmt(Double taxAmt) {
        this.taxAmt = taxAmt;
    }

    @Column(name = "ref_no")
    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    @Column(name = "exp_type")
    public Integer getExpType() {
        return expType;
    }

    public void setExpType(Integer expType) {
        this.expType = expType;
    }

    @Column(name = "expense_name")
    public String getExpTypeDesp() {
        return expTypeDesp;
    }

    public void setExpTypeDesp(String expTypeDesp) {
        this.expTypeDesp = expTypeDesp;
    }

    @Column(name = "exp_in")
    public Double getExpIn() {
        return expIn;
    }

    public void setExpIn(Double expIn) {
        this.expIn = expIn;
    }

    @Column(name = "intg_upd_status")
    public String getIntgUpdStatus() {
        return intgUpdStatus;
    }

    public void setIntgUpdStatus(String intgUpdStatus) {
        this.intgUpdStatus = intgUpdStatus;
    }

    @Column(name="session_name")
    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    @Column(name="payment_type_name")
    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    @Column(name = "group_id")
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Column(name="admission_no")
    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    @Column(name="account_id")
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @Column(name="stu_no")
    public String getStuNo() {
        return stuNo;
    }

    public void setStuNo(String stuNo) {
        this.stuNo = stuNo;
    }

    @Column(name="doctor_id")
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }
}
