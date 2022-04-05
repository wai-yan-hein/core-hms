/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.inpatient.ui.common;

import com.cv.app.inpatient.database.entity.InpService;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class DCAutoCompleteTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(DCAutoCompleteTableModel.class.getName());
    private List<InpService> listService;
    private final String[] columnNames = {"Code", "Description", "Fees"};

    public DCAutoCompleteTableModel(List<InpService> listService) {
        this.listService = listService;
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
        switch (column) {
            case 0: //Code
                return String.class;
            case 1: //Description
                return String.class;
            case 2: //Fees
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listService != null) {
            if (!listService.isEmpty()) {
                try {
                    InpService record = listService.get(row);

                    switch (column) {
                        case 0: //Code
                            return record.getServiceCode();
                        case 1: //Description
                            return record.getServiceName();
                        case 2: //Fees
                            return record.getFees();
                        default:
                            return null;
                    }
                } catch (Exception ex) {
                    log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
                }
            }
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listService == null) {
            return 0;
        } else {
            return listService.size();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public InpService getService(int row) {
        if (listService != null) {
            if (!listService.isEmpty()) {
                return listService.get(row);
            }
        }
        return null;
    }

    public int getSize() {
        if (listService == null) {
            return 0;
        } else {
            return listService.size();
        }
    }
}
