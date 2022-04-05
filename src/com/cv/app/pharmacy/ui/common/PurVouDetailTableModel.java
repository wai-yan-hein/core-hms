/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.pharmacy.database.entity.PurDetailHis;
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
public class PurVouDetailTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(PurVouDetailTableModel.class.getName());
    private List<PurDetailHis> listD = new ArrayList();
    private final String[] columnNames = {"Code", "Description", "Relation-Str",
        "Exp-Date", "Qty", "Pur Price", "%1", "FOC",
        "Expense %", "Expense", "Unit Cost", "Amount"};
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
                return String.class;
            case 1: //Description
                return String.class;
            case 2: //Relation-Str
                return String.class;
            case 3: //Exp-Date
                return String.class;
            case 4: //Qty
                return String.class;
            case 5: //Pur Price
                return Double.class;
            case 6: //%1
                return Double.class;
            case 7: //FOC
                return String.class;
            case 8: //Expense %
                return Double.class;
            case 9: //Expense
                return Double.class;
            case 10: //Unit cost
                return Double.class;
            case 11: //Amount
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listD == null) {
            return null;
        }

        if (listD.isEmpty()) {
            return null;
        }

        try {
            PurDetailHis record = listD.get(row);
            switch (column) {
                case 0: //Code
                    if (codeUsage.equals("SHORTNAME")) {
                        if (record.getMedId() == null) {
                            return null;
                        } else {
                            return record.getMedId().getShortName();
                        }
                    } else {
                        if (record.getMedId() == null) {
                            return null;
                        } else {
                            return record.getMedId().getMedId();
                        }
                    }
                case 1: //Medicine Name
                    if (record.getMedId() == null) {
                        return null;
                    } else {
                        return record.getMedId().getMedName();
                    }
                case 2: //Relation-Str
                    if (record.getMedId() == null) {
                        return null;
                    } else {
                        return record.getMedId().getRelStr();
                    }
                case 3: //Exp-Date
                    return DateUtil.toDateStr(record.getExpireDate());
                case 4: //Qty
                    if (record.getQuantity() == null) {
                        return null;
                    } else {
                        return record.getQuantity() + " " + record.getUnitId();
                    }
                case 5: //Pur Price
                    return record.getPrice();
                case 6: //%1
                    return record.getDiscount1();
                case 7: //FOC
                    if (record.getFocQty() == null) {
                        return null;
                    } else {
                        return record.getFocQty() + " " + record.getFocUnitId();
                    }
                case 8: //Expense %
                    return record.getItemExpenseP();
                case 9: //Expense
                    return record.getItemExpense();
                case 10: //Unit Cost
                    return record.getUnitCost();
                case 11: //Amount
                    return record.getAmount();
                default:
                    return new Object();
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
        if (listD == null) {
            return 0;
        }
        return listD.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<PurDetailHis> getListD() {
        return listD;
    }

    public void setListD(List<PurDetailHis> listD) {
        this.listD = listD;
        System.gc();
        fireTableDataChanged();
    }
}
