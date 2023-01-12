/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cv.app.pharmacy.database.helper;

import java.util.List;

/**
 *
 * @author cv-svr
 */
public class MinusPlusList {
    private List<Stock> listMinusStock;
    private List<Stock> listPlusStock;
    
    public MinusPlusList(List<Stock> listMinusStock, List<Stock> listPlusStock){
        this.listMinusStock = listMinusStock;
        this.listPlusStock = listPlusStock;
    }

    public List<Stock> getListMinusStock() {
        return listMinusStock;
    }

    public void setListMinusStock(List<Stock> listMinusStock) {
        this.listMinusStock = listMinusStock;
    }

    public List<Stock> getListPlusStock() {
        return listPlusStock;
    }

    public void setListPlusStock(List<Stock> listPlusStock) {
        this.listPlusStock = listPlusStock;
    }
}
