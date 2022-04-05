/*
 * To change this template, choose Tools | Templates
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
@Table(name = "charge_type")
public class ChargeType implements java.io.Serializable{
    private Integer chargeTypeId;
    private String chargeTypeDesc;
    private Float factor;
    private Boolean isAmount;
    
    public ChargeType(){}

    @Column(name="charge_type_desc", nullable=false, length=15, unique=true)
    public String getChargeTypeDesc() {
        return chargeTypeDesc;
    }

    public void setChargeTypeDesc(String chargeTypeDesc) {
        this.chargeTypeDesc = chargeTypeDesc;
    }

    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="charge_type_id", unique=true, nullable=false)
    public Integer getChargeTypeId() {
        return chargeTypeId;
    }

    public void setChargeTypeId(Integer chargeTypeId) {
        this.chargeTypeId = chargeTypeId;
    }
    
    @Override
    public String toString(){
        return chargeTypeDesc;
    }

    @Column(name="factor")
    public Float getFactor() {
        return factor;
    }
    
    public void setFactor(Float factor) {
        this.factor = factor;
    }

    @Column(name="is_amount")
    public Boolean getIsAmount() {
        return isAmount;
    }

    public void setIsAmount(Boolean isAmount) {
        this.isAmount = isAmount;
    }
}
