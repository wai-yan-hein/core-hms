/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import com.cv.app.pharmacy.database.entity.Medicine;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author winswe
 */
@Embeddable
public class GrossProfitKey implements Serializable{
  private Medicine item;
  private String userId;
  
  public GrossProfitKey(){}
  
  public GrossProfitKey(Medicine item, String userId){
    this.item = item;
    this.userId = userId;
  }

  @ManyToOne
  @JoinColumn(name="item_id", nullable=false)
  public Medicine getItem() {
    return item;
  }

  public void setItem(Medicine item) {
    this.item = item;
  }

  @Column(name="user_id")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }  
}
