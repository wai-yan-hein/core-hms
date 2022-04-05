/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class AdjSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(AdjSearchTableModel.class.getName());
    private List<VoucherSearch> listAdjHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "Location", "Remark", "User"};

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
                return Date.class;
            case 1: //Vou No
                return String.class;
            case 2: //Location
                return String.class;
            case 3: //Remark
                return String.class;
            case 4: //User
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listAdjHis == null) {
            return null;
        }

        if (listAdjHis.isEmpty()) {
            return null;
        }

        try {
            VoucherSearch ajh = listAdjHis.get(row);

            switch (column) {
                case 0: //Date
                    return ajh.getTranDate();
                case 1: //Vou No
                    if (Util1.getNullTo(ajh.getIsDeleted())) {
                        return ajh.getInvNo() + "**";
                    } else {
                        return ajh.getInvNo();
                    }
                case 2: //Location
                    return ajh.getLocation();
                case 3: //Remark
                    return ajh.getRefNo();
                case 4: //User
                    return ajh.getUserName();
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
        return listAdjHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherSearch> getListAdjHis() {
        return listAdjHis;
    }

    public void setListAdjHis(List<VoucherSearch> listAdjHis) {
        this.listAdjHis = listAdjHis;
        fireTableDataChanged();
    }

    public VoucherSearch getSelectVou(int row) {
        if(listAdjHis != null){
            if(!listAdjHis.isEmpty()){
                return listAdjHis.get(row);
            }
        }
        return null;
    }
}
