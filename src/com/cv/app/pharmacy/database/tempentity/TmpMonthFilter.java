/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.tempentity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author MyintMo
 */
@Entity
@Table(name="tmp_month_filter")
public class TmpMonthFilter implements Serializable{
    private TmpMonthFilterKey key;
    private Integer filterMonth;
    private Integer filterYear;

    public TmpMonthFilter(){}
    
    public TmpMonthFilter(String userId, String monthYear, int month, int year){
        key = new TmpMonthFilterKey();
        key.setUserId(userId);
        key.setMonthYear(monthYear);
        this.filterMonth = month;
        this.filterYear = year;
    }
    
    @EmbeddedId
    public TmpMonthFilterKey getKey() {
        return key;
    }

    public void setKey(TmpMonthFilterKey key) {
        this.key = key;
    }

    @Column(name="f_m")
    public Integer getFilterMonth() {
        return filterMonth;
    }

    public void setFilterMonth(Integer filterMonth) {
        this.filterMonth = filterMonth;
    }

    @Column(name="f_y")
    public Integer getFilterYear() {
        return filterYear;
    }

    public void setFilterYear(Integer filterYear) {
        this.filterYear = filterYear;
    }
}
