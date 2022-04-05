/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.opd.database.entity.Pathologiest;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class PathoTableModel extends AbstractTableModel {

    private List<Pathologiest> listPatho = new ArrayList();
    private final String[] columnNames = {"Name", "Rank", "Post", "Active"};

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
        if (column == 3) {
            return Boolean.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listPatho == null) {
            return null;
        }

        if (listPatho.isEmpty()) {
            return null;
        }

        Pathologiest record = listPatho.get(row);

        switch (column) {
            case 0: //Name
                return record.getPathologyName();
            case 1: //Rank
                return record.getRank();
            case 2: //Post
                return record.getPost();
            case 3: //Active
                return record.isActive();
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

    public List<Pathologiest> getListPatho() {
        return listPatho;
    }

    public void setListPatho(List<Pathologiest> listPatho) {
        this.listPatho = listPatho;
        fireTableDataChanged();
    }

    public Pathologiest getPatho(int row) {
        Pathologiest patho = null;

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
