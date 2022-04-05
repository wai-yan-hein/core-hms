/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.inpatient.database.entity.Diagnosis;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class DiagnosisTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(DiagnosisTableModel.class.getName());
    private List<Diagnosis> listDiag = new ArrayList();
    private final String[] columnNames = {"Local Name", "IDC Code", "IDC Name"};

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
        if (listDiag == null) {
            return null;
        }

        if (listDiag.isEmpty()) {
            return null;
        }

        try {
            Diagnosis record = listDiag.get(row);

            switch (column) {
                case 0: //Name
                    return record.getLocalName();
                case 1: //int code
                    return record.getIntCode();
                case 2: //int Name
                    return record.getIntName();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listDiag == null) {
            return 0;
        } else {
            return listDiag.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<Diagnosis> getListDiag() {
        return listDiag;
    }

    public void setListDiag(List<Diagnosis> listDiag) {
        this.listDiag = listDiag;
        fireTableDataChanged();
    }

    public Diagnosis getDiagnosis(int row) {
        if (listDiag != null) {
            if (!listDiag.isEmpty()) {
                return listDiag.get(row);
            }
        }
        return null;
    }

    public void setDiagnosis(int row, Diagnosis diagnosis) {
        if (listDiag != null) {
            if (!listDiag.isEmpty()) {
                listDiag.set(row, diagnosis);
                fireTableRowsUpdated(row, row);
            }
        }
    }

    public void addDiag(Diagnosis diagnosis) {
        if (listDiag != null) {
            listDiag.add(diagnosis);
            fireTableRowsInserted(listDiag.size() - 1, listDiag.size() - 1);
        }
    }

    public void deleteDiag(int row) {
        if (listDiag != null) {
            listDiag.remove(row);
            fireTableRowsDeleted(0, listDiag.size() - 1);
        }
    }
}
