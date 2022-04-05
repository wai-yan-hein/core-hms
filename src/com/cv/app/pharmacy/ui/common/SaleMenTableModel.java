/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.opd.database.entity.Doctor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author winswe
 */
public class SaleMenTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(SaleMenTableModel.class.getName());
    private List<Doctor> listSM = new ArrayList();
    private final String[] columnNames = {"Code", "Name"};

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
        if (listSM == null) {
            return null;
        }

        if (listSM.isEmpty()) {
            return null;
        }

        try {
            Doctor sm = listSM.get(row);

            switch (column) {
                case 0: //Code
                    return sm.getDoctorId();
                case 1: //Name
                    return sm.getDoctorName();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listSM == null) {
            return 0;
        }
        return listSM.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Doctor> getListSM() {
        return listSM;
    }

    public void setListSM(List<Doctor> listSM) {
        this.listSM = listSM;
        fireTableDataChanged();
    }

    public Doctor getSaleMen(int row) {
        if (listSM != null) {
            if (!listSM.isEmpty()) {
                return listSM.get(row);
            }
        }

        return null;
    }

    public void setSaleMen(int row, Doctor saleMan) {
        if (listSM != null) {
            if (!listSM.isEmpty()) {
                listSM.set(row, saleMan);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addSaleMen(Doctor saleMen) {
        if (listSM != null) {
            listSM.add(saleMen);
            fireTableRowsInserted(listSM.size() - 1, listSM.size() - 1);
        }
    }

    public void deleteSaleMen(int row) {
        if (listSM != null) {
            if (!listSM.isEmpty()) {
                listSM.remove(row);
                fireTableRowsDeleted(0, listSM.size());
            }
        }
    }
}
