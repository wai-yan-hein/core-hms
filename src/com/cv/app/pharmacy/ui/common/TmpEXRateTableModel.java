/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Currency;
import com.cv.app.pharmacy.database.tempentity.TmpEXRate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class TmpEXRateTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(TmpEXRateTableModel.class.getName());
    private List<TmpEXRate> list = new ArrayList();
    private final String[] columnNames = {"From", "To", "Ex-Rate"};
    private final AbstractDataAccess dao = Global.dao;
    private String fromCurr;
    private JTable parent;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 1;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //From
                return String.class;
            case 1: //To
                return String.class;
            case 2: //Ex-Rate
                return Float.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (list == null) {
            return null;
        }

        if (list.isEmpty()) {
            return null;
        }

        try {
            TmpEXRate record = list.get(row);

            switch (column) {
                case 0: //From
                    if (record.getKey() == null) {
                        return null;
                    } else {
                        return record.getKey().getFromCurr();
                    }
                case 1: //To
                    if (record.getKey() == null) {
                        return null;
                    } else {
                        return record.getKey().getToCurr();
                    }
                case 2: //Ex-Rate
                    return record.getExRate();
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
        if (list == null) {
            return;
        }

        if (list.isEmpty()) {
            return;
        }

        try {
            TmpEXRate record = list.get(row);
            switch (column) {
                case 0: //from
                    if (value != null) {
                        Currency curr = (Currency)value;
                        record.getKey().setFromCurr(curr.getCurrencyCode());
                        record.getKey().setToCurr(fromCurr);
                        record.getKey().setUserId(Global.machineId);
                    } else {
                        record.getKey().setFromCurr(null);
                        record.getKey().setToCurr(null);
                        record.setExRate(null);
                    }
                    break;
                case 1: //to
                    break;
                case 2: //rate
                    if (value != null) {
                        record.setExRate(Float.parseFloat(value.toString()));
                    } else {
                        record.setExRate(null);
                    }
                    break;
            }

            save(record);
            addEmptyRow();
            fireTableCellUpdated(row, column);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    private void save(TmpEXRate record) {
        if (record.getKey().getUserId() != null && record.getKey().getFromCurr() != null
                && record.getKey().getToCurr() != null) {
            try {
                dao.save(record);
            } catch (Exception ex) {
                log.error("save : " + ex.getMessage());
            } finally {
                dao.close();
            }
        }
    }

    @Override
    public int getRowCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<TmpEXRate> getList() {
        return list;
    }

    public void setList(List<TmpEXRate> list) {
        this.list = list;
        fireTableDataChanged();
    }

    public String getFromCurr() {
        return fromCurr;
    }

    public void setFromCurr(String fromCurr) {
        this.fromCurr = fromCurr;
    }

    public boolean hasEmptyRow() {
        if (list == null) {
            return false;
        }
        if (list.isEmpty()) {
            return false;
        }

        TmpEXRate record = list.get(list.size() - 1);
        if (record.getKey() != null) {
            return record.getKey().getFromCurr() == null;
        } else {
            return true;
        }
    }

    public void addEmptyRow() {
        if (list != null) {
            if (!hasEmptyRow()) {
                TmpEXRate record = new TmpEXRate();
                list.add(record);

                fireTableRowsInserted(list.size() - 1, list.size() - 1);
                parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
            }
        }
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }

    public void delete(int index) {
        TmpEXRate record = list.get(index);
        try {
            String strSql = "delete from tmp_ex_rate where user_id = '"
                    + record.getKey().getUserId() + "' and from_curr = '"
                    + record.getKey().getFromCurr() + "' and to_curr = '"
                    + record.getKey().getToCurr() + "'";
            dao.deleteSQL(strSql);
            list.remove(index);
            fireTableRowsDeleted(index, index);
            if (index - 1 >= 0) {
                parent.setRowSelectionInterval(index - 1, index - 1);
            }
        } catch (Exception ex) {
            log.error("delete : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }
    
    public void clear(){
        list = new ArrayList();
        fireTableDataChanged();
    }
}
