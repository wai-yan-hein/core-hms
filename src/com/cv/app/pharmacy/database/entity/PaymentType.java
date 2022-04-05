/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;
/**
 *
 * @author WSwe
 */
@Entity
@Table(name="payment_type")
public class PaymentType implements java.io.Serializable{
    private Integer paymentTypeId;
    private String paymentTypeName;
    
    public PaymentType(){}

    @Id 
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="payment_type_id", unique=true, nullable=false)
    public Integer getPaymentTypeId() {
        return paymentTypeId;
    }

    public void setPaymentTypeId(Integer paymentTypeId) {
        this.paymentTypeId = paymentTypeId;
    }

    @Column(name="payment_type_name", nullable=false, length=40, unique=true)
    public String getPaymentTypeName() {
        return paymentTypeName;
    }

    public void setPaymentTypeName(String paymentTypeName) {
        this.paymentTypeName = paymentTypeName;
    }
    
    @Override
    public String toString(){
        return paymentTypeName;
    }
}
