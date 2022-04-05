/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.database.entity;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="speciality")
public class Speciality implements java.io.Serializable{
  private Integer specId;
  private String desp;

  @Column(name="desp", unique=true, nullable=false, length=150)
  public String getDesp() {
    return desp;
  }

  public void setDesp(String desp) {
    this.desp = desp;
  }

  @Id @GeneratedValue(strategy=IDENTITY)
  @Column(name="spec_id", unique=true, nullable=false)
  public Integer getSpecId() {
    return specId;
  }

  public void setSpecId(Integer specId) {
    this.specId = specId;
  }
  
  @Override
  public String toString(){
    return desp;
  }
}
