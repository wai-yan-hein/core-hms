/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import javax.persistence.*;
import org.hibernate.annotations.GenerationTime;

/**
 *
 * Trader class is parent class of Customer, Patient and Supplier class. Sharing
 * "trader" table with Patient, Customer and Supplier class. Database table name
 * is trader.
 */
@Entity
@Table(name = "trader")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "T")
public class Trader implements java.io.Serializable {

    private String traderId;
    private String traderName;
    private String address;
    private Township township;
    private String phone;
    private String email;
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
    private String accountCode;
    private boolean active;
    private TraderType typeId;
    private PayMethod payMethod;
    private String remark;
    private String accountId;
    private String intgUpdStatus; //For integration update status
    private CustomerGroup traderGroup;
    private Integer migId;
    private BusinessType businessType;
    private Float expensePercent;
    private String stuCode;
    private String discrimator;
    
    public Trader() {
    }

    @Column(name = "address", nullable = true, length = 1000)
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "created_id", nullable = false, length = 15)
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "created_date", insertable = false, updatable = false,
            columnDefinition = "timestamp default current_timestamp")
    @org.hibernate.annotations.Generated(value = GenerationTime.INSERT)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Column(name = "email", nullable = true, length = 25)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "phone", nullable = true, length = 255)
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @ManyToOne
    @JoinColumn(name = "township")
    public Township getTownship() {
        return township;
    }

    public void setTownship(Township township) {
        this.township = township;
    }

    @Id
    @Column(name = "trader_id", unique = true, nullable = false, length = 15)
    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    @Column(name = "trader_name", nullable = false, length = 50, unique = true)
    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    @Column(name = "updated_id", nullable = true, length = 15)
    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "updated_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Column(name = "account_code", nullable = true, length = 15)
    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    @Column(name = "active")
    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @ManyToOne
    @JoinColumn(name = "type_id")
    public TraderType getTypeId() {
        return typeId;
    }

    public void setTypeId(TraderType typeId) {
        this.typeId = typeId;
    }

    @ManyToOne
    @JoinColumn(name = "pay_method_id")
    public PayMethod getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(PayMethod payMethod) {
        this.payMethod = payMethod;
    }

    @Column(name="remark", length=255)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(name="account_id")
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
    @Column(name="intg_upd_status", length=5)
    public String getIntgUpdStatus() {
        return intgUpdStatus;
    }

    public void setIntgUpdStatus(String intgUpdStatus) {
        this.intgUpdStatus = intgUpdStatus;
    }
    
    @ManyToOne
    @JoinColumn(name="group_id")
    public CustomerGroup getTraderGroup() {
        return traderGroup;
    }

    public void setTraderGroup(CustomerGroup traderGroup) {
        this.traderGroup = traderGroup;
    }

    @Column(name="mig_id")
    public Integer getMigId() {
        return migId;
    }

    public void setMigId(Integer migId) {
        this.migId = migId;
    }

    @ManyToOne
    @JoinColumn(name="business_id")
    public BusinessType getBusinessType() {
        return businessType;
    }

    public void setBusinessType(BusinessType businessType) {
        this.businessType = businessType;
    }

    @Column(name="expense_p")
    public Float getExpensePercent() {
        return expensePercent;
    }

    public void setExpensePercent(Float expensePercent) {
        this.expensePercent = expensePercent;
    }

    @Column(name="stu_no", length=25)
    public String getStuCode() {
        return stuCode;
    }

    public void setStuCode(String stuCode) {
        this.stuCode = stuCode;
    }

    @Column(name="discriminator", insertable=false, updatable=false)
    public String getDiscrimator() {
        return discrimator;
    }

    public void setDiscrimator(String discrimator) {
        this.discrimator = discrimator;
    }
}
