/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import com.cv.app.pharmacy.database.entity.Medicine;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author winswe
 */
@Entity
@Table(name = "tmp_stock_inout_detail")
public class StockInOutDetail implements java.io.Serializable {

  private Integer tranId;
  private Medicine item;
  private Integer location;
  private Date tranDate;
  private String opBalance;
  private String purQty;
  private String retInQty;
  private String saleQty;
  private String retOutQty;
  private String adjQty;
  private String clBalance;
  private String userId;

  @Column(name="adjust_qty")
  public String getAdjQty() {
    return adjQty;
  }

  public void setAdjQty(String adjQty) {
    this.adjQty = adjQty;
  }

  @Column(name="adjust_qty")
  public String getClBalance() {
    return clBalance;
  }

  public void setClBalance(String clBalance) {
    this.clBalance = clBalance;
  }

  @ManyToOne
  @JoinColumn(name="item_id", nullable=false)
  public Medicine getItem() {
    return item;
  }

  public void setItem(Medicine item) {
    this.item = item;
  }

  @Column(name="adjust_qty")
  public Integer getLocation() {
    return location;
  }

  public void setLocation(Integer location) {
    this.location = location;
  }

  @Column(name="adjust_qty")
  public String getOpBalance() {
    return opBalance;
  }

  public void setOpBalance(String opBalance) {
    this.opBalance = opBalance;
  }

  @Column(name="adjust_qty")
  public String getPurQty() {
    return purQty;
  }

  public void setPurQty(String purQty) {
    this.purQty = purQty;
  }

  @Column(name="adjust_qty")
  public String getRetInQty() {
    return retInQty;
  }

  public void setRetInQty(String retInQty) {
    this.retInQty = retInQty;
  }

  @Column(name="adjust_qty")
  public String getRetOutQty() {
    return retOutQty;
  }

  public void setRetOutQty(String retOutQty) {
    this.retOutQty = retOutQty;
  }

  @Column(name="adjust_qty")
  public String getSaleQty() {
    return saleQty;
  }

  public void setSaleQty(String saleQty) {
    this.saleQty = saleQty;
  }

  @Temporal(TemporalType.DATE)
  @Column(name="tran_date")
  public Date getTranDate() {
    return tranDate;
  }

  public void setTranDate(Date tranDate) {
    this.tranDate = tranDate;
  }

  @Id
  @Column(name="tran_id", unique=true, nullable=false)
  public Integer getTranId() {
    return tranId;
  }

  public void setTranId(Integer tranId) {
    this.tranId = tranId;
  }

  @Column(name="adjust_qty")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
  
}
