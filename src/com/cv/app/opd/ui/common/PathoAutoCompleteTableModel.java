/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.Doctor;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class PathoAutoCompleteTableModel extends AbstractTableModel {

    private List<Doctor> listPatho;
    private final String[] columnNames = {"Name", "Rank", "Post"};

    public PathoAutoCompleteTableModel(List<Doctor> listPatho){
        this.listPatho = listPatho;
    }
    
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
        if (listPatho == null) {
            return null;
        }

        if (listPatho.isEmpty()) {
            return null;
        }

        Doctor record = listPatho.get(row);

        switch (column) {
            case 0: //Name
                return record.getDoctorName();
            case 1: //Rank
                return null;
            case 2: //Post
                return null;
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listPatho == null) {
            return 0;
        } else {
            return listPatho.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Doctor> getListPatho() {
        return listPatho;
    }

    public void setListPatho(List<Doctor> listPatho) {
        this.listPatho = listPatho;
        fireTableDataChanged();
    }

    public Doctor getPatho(int row) {
        Doctor patho = null;

        if (listPatho != null) {
            if (!listPatho.isEmpty()) {
                if (row < listPatho.size()) {
                    patho = listPatho.get(row);
                }
            }
        }
        return patho;
    }
}
