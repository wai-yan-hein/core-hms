/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.opd.ui.common;

import com.cv.app.common.Global;
import com.cv.app.opd.database.tempentity.TempAmountLink;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.util.NumberUtil;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class AmountLinkTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(AmountLinkTableModel.class.getName());
    private final AbstractDataAccess dao = Global.dao;
    private List<TempAmountLink> listTAL = new ArrayList();
    private final String[] columnNames = {"Tran Option", "Vou No.", "Amount", "Print"};

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 3;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Tran Option
                return String.class;
            case 1: //Vou No.
                return String.class;
            case 2: //Amount
                return Double.class;
            case 3: //Print
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        TempAmountLink record = listTAL.get(row);

        switch (column) {
            case 0: //Tran Option
                if (record.getKey() != null) {
                    return record.getKey().getTranOption();
                } else {
                    return null;
                }
            case 1: //Vou No.
                if (record.getKey() != null) {
                    return record.getKey().getInvId();
                } else {
                    return null;
                }
            case 2: //Amount
                return record.getAmount();
            case 3: //Print
                return record.isPrintStatus();
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (column == 3) {
            TempAmountLink record = listTAL.get(row);
            try {
                if (value != null) {
                    record.setPrintStatus((boolean) value);
                } else {
                    record.setPrintStatus(false);
                }
                dao.save(record);
            } catch (Exception ex) {
                log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex);
            } finally {
                dao.close();
            }
        }
        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        return listTAL.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<TempAmountLink> getListTAL() {
        return listTAL;
    }

    public void setListTAL(List<TempAmountLink> listTAL) {
        this.listTAL = listTAL;
        fireTableDataChanged();
    }

    public double getTotalAmount() {
        double ttlAmt = 0;
        if (listTAL != null) {
            for (TempAmountLink tal : listTAL) {
                if (tal.isPrintStatus()) {
                    ttlAmt += NumberUtil.NZero(tal.getAmount());
                }
            }
        }
        return ttlAmt;
    }

    public double getTotalDiscount() {
        double ttlAmt = 0;
        if (listTAL != null) {
            for (TempAmountLink tal : listTAL) {
                if (tal.isPrintStatus()) {
                    ttlAmt += NumberUtil.NZero(tal.getDiscount());
                }
            }
        }
        return ttlAmt;
    }

    public void clear() {
        listTAL = new ArrayList();
        fireTableDataChanged();
        System.gc();
    }
}
