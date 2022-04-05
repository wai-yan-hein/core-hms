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
@Table(name="service_fees")
public class ServiceFees implements java.io.Serializable{
  private Integer id;
  private String feesDesp;
  private Integer serviceId;
  private Double fee;
  
  @Column(name="fees_desp", length=15)
  public String getFeesDesp() {
    return feesDesp;
  }

  public void setFeesDesp(String feesDesp) {
    this.feesDesp = feesDesp;
  }

  @Id @GeneratedValue(strategy=IDENTITY)
  @Column(name="fees_id", unique=true, nullable=false)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Column(name="service_id")
  public Integer getServiceId() {
    return serviceId;
  }

  public void setServiceId(Integer serviceId) {
    this.serviceId = serviceId;
  }

  @Column(name="srv_fees")
  public Double getFee() {
    return fee;
  }

  public void setFee(Double fee) {
    this.fee = fee;
  }
  
  
}
