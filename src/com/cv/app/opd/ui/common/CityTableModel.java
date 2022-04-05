/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.City;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class CityTableModel extends AbstractTableModel{
    private List<City> listCity = new ArrayList();
    private String[] columnNames = {"City"};
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        City record = listCity.get(row);

        switch (column) {
            case 0: //Name
                return record.getCityName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listCity.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<City> getListCity() {
        return listCity;
    }

    public void setListCity(List<City> listCity) {
        this.listCity = listCity;
        fireTableDataChanged();
    }
    
    public City getCity(int row){
        return listCity.get(row);
    }
    
    public void setCity(int row, City city){
        listCity.set(row, city);
        fireTableRowsUpdated(row, row);
    }
    
    public void addCity(City city){
        listCity.add(city);
        fireTableRowsInserted(listCity.size()-1, listCity.size()-1);
    }
    
    public void deleteCity(int row){
        listCity.remove(row);
        fireTableRowsDeleted(0, listCity.size()-1);
    }
}
