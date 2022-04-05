/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.tempentity.StockBalance;
import com.cv.app.util.DateUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class StockTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(StockTableModel.class.getName());
    private List<StockBalance> listDetail = new ArrayList();
    private final String[] columnNames = {"Code", "Description", "Relation-Str",
        "Exp-Date", "Qty-Str"};
    private final String codeUsage = Util1.getPropValue("system.item.code.field");

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
            case 1: //Medicine Name
            case 2: //Relation-Str
                return String.class;
            case 3: //Exp-Date
                return String.class;
            case 4: //Qty-Str
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listDetail == null) {
            return null;
        }

        if (listDetail.isEmpty()) {
            return null;
        }

        try {
            StockBalance record = listDetail.get(row);

            switch (column) {
                case 0: //Code
                    if (codeUsage.equals("SHORTNAME")) {
                        if (record.getKey() == null) {
                            return null;
                        } else {
                            return record.getKey().getMed().getShortName();
                        }
                    } else {
                        if (record.getKey() == null) {
                            return null;
                        } else {
                            return record.getKey().getMed().getMedId();
                        }
                    }
                case 1: //Medicine Name
                    if (record.getKey() == null) {
                        return null;
                    } else {
                        return record.getKey().getMed().getMedName();
                    }
                case 2: //Relation-Str
                    if (record.getKey() == null) {
                        return null;
                    } else {
                        return record.getKey().getMed().getRelStr();
                    }
                case 3: //Exp-Date
                    if (record.getKey().getExpDate().toString().equals("1900-01-01")) {
                        return null;
                    } else {
                        return DateUtil.toDateStr(record.getKey().getExpDate());
                    }
                case 4: //Qty-Str
                    return record.getQtyStr();
                default:
                    return new Object();
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
        if (listDetail == null) {
            return 0;
        }
        return listDetail.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public void setListDetail(List<StockBalance> listDetail) {
        this.listDetail = listDetail;
        fireTableDataChanged();
    }
}
