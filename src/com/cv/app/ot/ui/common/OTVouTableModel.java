/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.ot.ui.common;

import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class OTVouTableModel extends AbstractTableModel {

    private List<VoucherSearch> listOPDHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Patient", "Vou Total", "User"};

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
            case 0: //Date
                return String.class;
            case 1: //Vou No
                return String.class;
            case 2: //Patient
                return String.class;
            case 3: //Vou Total
                return Double.class;
            case 4: //User
                return String.class;
            default:
                return Object.class;
        }

    }

    @Override
    public Object getValueAt(int row, int column) {
        if(listOPDHis == null){
            return null;
        }
        
        if(listOPDHis.isEmpty()){
            return null;
        }
        VoucherSearch record = listOPDHis.get(row);

        switch (column) {
            case 0: //Date
                return DateUtil.toDateStr(record.getTranDate());
            case 1: //Vou No
                if (record.getIsDeleted()) {
                    return record.getInvNo() + "*";
                } else {
                    return record.getInvNo();
                }
            case 2: //Patient
                return record.getCusNo() + " - " + record.getCusName();
            case 3: //Vou Total
                return record.getVouTotal();
            case 4: //User
                return record.getUserName();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        return listOPDHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherSearch> getListOPDHis() {
        return listOPDHis;
    }
    
    public void setListOPDHis(List<VoucherSearch> listOPDHis) {
        this.listOPDHis = listOPDHis;
        fireTableDataChanged();
    }

    public VoucherSearch getOPDHis(int row) {
        return listOPDHis.get(row);
    }
}
