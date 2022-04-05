/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.view;

import com.cv.app.pharmacy.database.entity.Medicine;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author WSwe
 */
@Embeddable
public class ReturnInItemListKey implements Serializable{
    private String saleInvId;
    private Medicine item;
    private Integer uniqueId;

    @Column(name="sale_inv_id")
    public String getSaleInvId() {
        return saleInvId;
    }

    public void setSaleInvId(String saleInvId) {
        this.saleInvId = saleInvId;
    }

    @ManyToOne
    @JoinColumn(name="med_id")
    public Medicine getItem() {
        return item;
    }

    public void setItem(Medicine item) {
        this.item = item;
    }

    @Column(name="unique_id")
    public Integer getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(Integer uniqueId) {
        this.uniqueId = uniqueId;
    }
}
