/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.PayBy;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class PayByTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PayByTableModel.class.getName());
    private List<PayBy> listPB = new ArrayList();
    private final String[] columnNames = {"Location Type"};

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
        if (listPB == null) {
            return null;
        }

        if (listPB.isEmpty()) {
            return null;
        }

        try {
            PayBy record = listPB.get(row);

            switch (column) {
                case 0: //Name
                    return record.getDesp();
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
        if (listPB == null) {
            return 0;
        }
        return listPB.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PayBy> getListPB() {
        return listPB;
    }

    public void setListPB(List<PayBy> listPB) {
        this.listPB = listPB;
    }

    public PayBy getPayBy(int row){
        if(listPB != null){
            if(!listPB.isEmpty()){
                return listPB.get(row);
            }
        }
        return null;
    }

    public void setPayBy(int row, PayBy pb){
        if(listPB != null){
            if(!listPB.isEmpty()){
                listPB.set(row, pb);
                fireTableRowsUpdated(row, row);
            }
        }
    }
    
    public void addPayBy(PayBy pb){
        if(listPB != null){
            listPB.add(pb);
            fireTableRowsInserted(listPB.size() - 1, listPB.size() - 1);
        }
    }

    public void delete(int row) {
        if (listPB != null) {
            if (!listPB.isEmpty()) {
                listPB.remove(row);
                fireTableRowsDeleted(0, listPB.size());
            }
        }
    }
}
