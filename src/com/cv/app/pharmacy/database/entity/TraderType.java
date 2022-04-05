/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.io.Serializable;
import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name="trader_type")
public class TraderType implements Serializable{
    private Integer typeId;
    private String description;

    @Column(name="description", unique=true, nullable=false, length=10)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="trader_type_id", unique=true, nullable=false)
    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
    
    @Override
    public String toString(){
        return description;
    }
}
