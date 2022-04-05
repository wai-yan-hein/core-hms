/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author winswe
 */
@Embeddable
public class VouFilterKey implements Serializable{
  private String tranOption;
  private String userId;

  @Column(name="tran_option", nullable=false, length=20)
  public String getTranOption() {
    return tranOption;
  }

  public void setTranOption(String tranOption) {
    this.tranOption = tranOption;
  }

  
  @Column(name="user_id", nullable=false, length=15)
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
