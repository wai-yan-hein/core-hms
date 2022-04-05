/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author winswe
 */
@Entity
@Table(name="gross_profits")
public class GrossProfit implements java.io.Serializable{
  private GrossProfitKey key;
  private Double opValue;
  private Double ttlPur;
  private Double clValue;
  private Double cogsValue;
  private Double ttlSale;
  private Double gpValue;

  @Column(name="cl_value")
  public Double getClValue() {
    return clValue;
  }

  public void setClValue(Double clValue) {
    this.clValue = clValue;
  }

  @Column(name="cogs_value")
  public Double getCogsValue() {
    return cogsValue;
  }

  public void setCogsValue(Double cogsValue) {
    this.cogsValue = cogsValue;
  }

  @Column(name="gp_value")
  public Double getGpValue() {
    return gpValue;
  }

  public void setGpValue(Double gpValue) {
    this.gpValue = gpValue;
  }

  @EmbeddedId
  public GrossProfitKey getKey() {
    return key;
  }

  public void setKey(GrossProfitKey key) {
    this.key = key;
  }

  @Column(name="op_value")
  public Double getOpValue() {
    return opValue;
  }

  public void setOpValue(Double opValue) {
    this.opValue = opValue;
  }

  @Column(name="ttl_pur")
  public Double getTtlPur() {
    return ttlPur;
  }

  public void setTtlPur(Double ttlPur) {
    this.ttlPur = ttlPur;
  }

  @Column(name="ttl_sale")
  public Double getTtlSale() {
    return ttlSale;
  }

  public void setTtlSale(Double ttlSale) {
    this.ttlSale = ttlSale;
  }  
}
