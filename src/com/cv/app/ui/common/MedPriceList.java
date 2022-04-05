/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ui.common;

/**
 *
 * @author WSwe
 */
public class MedPriceList {
    private String type;
    private Double price;
    
    public MedPriceList(String type, Double price){
        this.type = type;
        this.price = price;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
