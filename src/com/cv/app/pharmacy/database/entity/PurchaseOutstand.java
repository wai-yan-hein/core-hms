/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author WSwe
 */
@Entity
@Table(name = "purchase_outstanding")
public class PurchaseOutstand implements java.io.Serializable{
    private String outsId;
    private Medicine itemId;
    private String qtyStr;
    private Float outsQtySmall;
    private Date expDate;
    private String outsOption;
    private String vouNo;
    
    @Id
    @Column(name="outstanding_id", unique=true, nullable=false, length=25)
    public String getOutsId() {
        return outsId;
    }

    public void setOutsId(String outsId) {
        this.outsId = outsId;
    }

    @ManyToOne
    @JoinColumn(name="item_id")
    public Medicine getItemId() {
        return itemId;
    }

    public void setItemId(Medicine itemId) {
        this.itemId = itemId;
    }

    @Column(name="qty_str", length=45)
    public String getQtyStr() {
        return qtyStr;
    }

    public void setQtyStr(String qtyStr) {
        this.qtyStr = qtyStr;
    }

    @Column(name="outs_qty_small")
    public Float getOutsQtySmall() {
        return outsQtySmall;
    }

    public void setOutsQtySmall(Float outsQtySmall) {
        this.outsQtySmall = outsQtySmall;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="exp_date")
    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    @Column(name="outs_option", length=20)
    public String getOutsOption() {
        return outsOption;
    }

    public void setOutsOption(String outsOption) {
        this.outsOption = outsOption;
    }
    
    @Column(name="vou_no", length=15)
    public String getVouNo() {
        return vouNo;
    }

    public void setVouNo(String vouNo) {
        this.vouNo = vouNo;
    }
}
