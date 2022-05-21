/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.database.helper;

/**
 *
 * @author WSwe
 */
public class SessionTtl extends CurrencyTtl {

    private String desc;

    

    public SessionTtl(String desc, String strCurrency, Double total) {
        super(strCurrency, total);
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
