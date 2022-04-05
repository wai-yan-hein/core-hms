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
public class TransferSearchTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(TransferSearchTableModel.class.getName());
    private List<VoucherSearch> listTransferHis = new ArrayList();
    private final String[] columnNames = {"Date", "Vou No", "From", "To", "User"};

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
            case 2: //From
                return String.class;
            case 3: //To
                return String.class;
            case 4: //User
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listTransferHis == null) {
            return null;
        }

        if (listTransferHis.isEmpty()) {
            return null;
        }

        try {
            VoucherSearch th = listTransferHis.get(row);

            switch (column) {
                case 0: //Date
                    return th.getTranDate();
                case 1: //Vou No
                    if (Util1.getNullTo(th.getIsDeleted())) {
                        return th.getInvNo() + "**";
                    } else {
                        return th.getInvNo();
                    }
                case 2: //From
                    return th.getLocation();
                case 3: //To
                    return th.getCusName();
                case 4: //User
                    return th.getUserName();
                default:
                    return null;
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getMessage());
        }

        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
    }

    @Override
    public int getRowCount() {
        if (listTransferHis == null) {
            return 0;
        }
        return listTransferHis.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<VoucherSearch> getListTransferHis() {
        return listTransferHis;
    }

    public void setListTransferHis(List<VoucherSearch> listTransferHis) {
        this.listTransferHis = listTransferHis;
        fireTableDataChanged();
    }

    public VoucherSearch getSelectVou(int row) {
        if (listTransferHis != null) {
            if (!listTransferHis.isEmpty()) {
                return listTransferHis.get(row);
            }
        }
        return null;
    }
}
