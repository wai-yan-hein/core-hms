/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
@Table(name="tmp_stock_bal_outs")
public class TmpStockBalOuts implements java.io.Serializable{
    private TmpStockBalOutsKey key;
    private Float qtySmallese;
    private String qtyStr;

    @EmbeddedId
    public TmpStockBalOutsKey getKey() {
        return key;
    }

    public void setKey(TmpStockBalOutsKey key) {
        this.key = key;
    }

    @Column(name="qty_smallest")
    public Float getQtySmallese() {
        return qtySmallese;
    }

    public void setQtySmallese(Float qtySmallese) {
        this.qtySmallese = qtySmallese;
    }

    @Column(name="qty_str", length=100)
    public String getQtyStr() {
        return qtyStr;
    }

    public void setQtyStr(String qtyStr) {
        this.qtyStr = qtyStr;
    }
}
