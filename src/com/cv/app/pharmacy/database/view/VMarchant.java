/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="v_marchant")
public class VMarchant implements java.io.Serializable{
    private String personId;
    private String personNumber;
    private String personName;
    private String strAge;
    private String phone;
    private String personType;

    @Id
    @Column(name="person_id")
    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    @Column(name="person_no")
    public String getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(String personNumber) {
        this.personNumber = personNumber;
    }

    @Column(name="person_name")
    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    @Column(name="age_str")
    public String getStrAge() {
        return strAge;
    }

    public void setStrAge(String strAge) {
        this.strAge = strAge;
    }

    @Column(name="phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name="person_type")
    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }
}
