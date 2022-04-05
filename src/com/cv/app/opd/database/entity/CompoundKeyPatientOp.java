/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.entity.PaymentType;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author WSwe
 */
@Embeddable
public class CompoundKeyPatientOp implements Serializable{
    private Patient patient;
    private Date opDate;
    private Currency currency;
    private PaymentType billType;
    
    public CompoundKeyPatientOp(){
        
    }
    
    public CompoundKeyPatientOp(Patient patient){
        this.patient = patient;
    }
    
    @ManyToOne
    @JoinColumn(name="reg_no")
    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="op_date")
    public Date getOpDate() {
        return opDate;
    }

    public void setOpDate(Date opDate) {
        this.opDate = opDate;
    }

    @ManyToOne
    @JoinColumn(name="currency")
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @ManyToOne
    @JoinColumn(name="bill_type")
    public PaymentType getBillType() {
        return billType;
    }

    public void setBillType(PaymentType billType) {
        this.billType = billType;
    }
}
