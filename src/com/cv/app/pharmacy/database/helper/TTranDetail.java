/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.helper;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "trader_tran_detail")
public class TTranDetail implements java.io.Serializable{
    private Integer tranId;
    private String tranOption;
    private Date tranDate;
    private String medName;
    private String qtyStr;
    private Double price;
    private Double amount;
    private String userId;
    private String machineId;
    
    public TTranDetail(){}
    
    public TTranDetail(String tranOption, Date tranDate, String medName,
            String qtyStr, Double price, Double amount){
        this.tranOption = tranOption;
        this.tranDate = tranDate;
        this.medName = medName;
        this.qtyStr = qtyStr;
        this.price = price;
        this.amount = amount;
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

    @Column(name="item_name")
    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    @Column(name="qty")
    public String getQtyStr() {
        return qtyStr;
    }

    public void setQtyStr(String qtyStr) {
        this.qtyStr = qtyStr;
    }

    @Column(name="price")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name="amount")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    @Transient
    public String getFilter(){
        return tranOption + tranDate.toString();
    }

    @Id
    @Column(name="tran_id", unique=true, nullable=false)
    public Integer getTranId() {
        return tranId;
    }

    public void setTranId(Integer tranId) {
        this.tranId = tranId;
    }

    @Column(name="user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name="machine_id", length=15)
    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }
}
