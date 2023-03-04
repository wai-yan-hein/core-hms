/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.pharmacy.database.helper.VoucherSearch;
import com.cv.app.util.DateUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author WSwe
 */
public class OPDVouTableModel extends AbstractTableModel {

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
        if (listOPDHis == null) {
            return null;
        }

        if (listOPDHis.isEmpty()) {
            return null;
        }

        VoucherSearch record = listOPDHis.get(row);
        switch (column) {
            case 0: //Date
                return DateUtil.toDateStr(record.getTranDate());
            case 1: //Vou No
                if (record.getIsDeleted()) {
                    if (record.getDcStatus() == 0) {
                        return record.getInvNo() + " *";
                    } else {
                        return record.getInvNo() + " P*";
                    }
                } else {
                    if (record.getDcStatus() == 0) {
                        return record.getInvNo();
                    } else {
                        return record.getInvNo() + " P";
                    }
                }
            case 2: //Patient
                if (record.getCusNo() != null) {
                    if (record.getRefNo() != null) {
                        if (record.getRefNo().trim().length() > 0) {
                            return record.getCusNo() + " - " + record.getRefNo();
                        } else {
                            return record.getCusNo() + " - " + record.getCusName();
                        }
                    } else {
                        return record.getCusNo() + " - " + record.getCusName();
                    }
                } else {
                    return record.getCusName();
                }
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

    public void setListOPDHis(List<VoucherSearch> listOPDHis) {
        this.listOPDHis = listOPDHis;
        fireTableDataChanged();
    }

    public List<VoucherSearch> getListOPDHis() {
        return listOPDHis;
    }

    public VoucherSearch getOPDHis(int row) {
        return listOPDHis.get(row);
    }
}
