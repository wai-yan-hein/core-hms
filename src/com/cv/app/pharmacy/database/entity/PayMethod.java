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
 * @author winswe
 */
@Entity
@Table(name = "pay_method")
public class PayMethod implements java.io.Serializable {

  private Integer methodId;
  private String methodDesp;

  @Column(name="method_desc", unique=true, length=50)
  public String getMethodDesp() {
    return methodDesp;
  }

  public void setMethodDesp(String methodDesp) {
    this.methodDesp = methodDesp;
  }

  @Id
  @Column(name = "method_id", unique = true, nullable = false)
  public Integer getMethodId() {
    return methodId;
  }

  public void setMethodId(Integer methodId) {
    this.methodId = methodId;
  }
  
  @Override
  public String toString(){
    return methodDesp;
  }
}
