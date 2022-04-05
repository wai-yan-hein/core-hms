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
public class StockCostingKey implements Serializable {

    private Medicine medicine;
    private String userId;
    private String tranOption;

    public StockCostingKey() {
    }

    public StockCostingKey(Medicine medicine, String userId, String tranOption) {
        this.medicine = medicine;
        this.userId = userId;
        this.tranOption = tranOption;
    }

    @ManyToOne
    @JoinColumn(name = "med_id", nullable = false)
    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    @Column(name = "tran_option")
    public String getTranOption() {
        return tranOption;
    }

    public void setTranOption(String tranOption) {
        this.tranOption = tranOption;
    }

    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
