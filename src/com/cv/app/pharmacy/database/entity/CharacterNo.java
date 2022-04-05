/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * Use for medicine code generation.
 */
@Entity
@Table(name = "char_seq")
public class CharacterNo implements java.io.Serializable{
    private String ch;
    private String strNumber;
    
    public CharacterNo() {}
    
    public CharacterNo(String ch, String strNumber){
        this.ch = ch;
        this.strNumber = strNumber;
    }
    
    @Id 
    @Column(name="ch", unique=true, nullable=false, length=2)
    public String getCh() {
        return ch;
    }

    public void setCh(String ch) {
        this.ch = ch;
    }

    @Column(name="char_no", nullable=false, length=3, unique=true)
    public String getStrNumber() {
        return strNumber;
    }

    public void setStrNumber(String strNumber) {
        this.strNumber = strNumber;
    }
}
