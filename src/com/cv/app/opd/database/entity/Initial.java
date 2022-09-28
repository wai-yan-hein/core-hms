/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;
/**
 *
 * @author WSwe
 */
@Entity
@Table(name="initial")
public class Initial implements java.io.Serializable{
    private Integer initialId;
    private String initialName;

    @Id @GeneratedValue(strategy=IDENTITY)
    @Column(name="initial_id", unique=true, nullable=false)
    public Integer getInitialId() {
        return initialId;
    }

    public void setInitialId(Integer initialId) {
        this.initialId = initialId;
    }

    @Column(name="initial_name", unique=true, nullable=false,
            length=5)
    public String getInitialName() {
        return initialName;
    }

    public void setInitialName(String initialName) {
        this.initialName = initialName;
    }
    
    @Override
    public String toString(){
        return initialName;
    }
}
