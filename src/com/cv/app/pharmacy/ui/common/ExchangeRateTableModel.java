/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ExchangeRate;
import com.cv.app.pharmacy.database.entity.ExchangeRateLog;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ExchangeRateTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ExchangeRateTableModel.class.getName());
    private List<ExchangeRate> listER = new ArrayList();
    private final String[] columnNames = {"Date", "From", "To", "Exchange Rate"};
    private String homeCurrency = Util1.getPropValue("system.app.currency");
    private final AbstractDataAccess dao = Global.dao;
    private JTable parent;

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        switch (column) {
            case 0:
                return false;
            case 1:
                return false;
            case 2:
                return true;
            case 3:
                return true;
            default:
                return false;
        }
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Date
                return Date.class;
            case 1: //From Currency
                return String.class;
            case 2: //To Currency
                return String.class;
            case 3: //Exchange Rate
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listER == null) {
            return null;
        }

        if (listER.isEmpty()) {
            return null;
        }

        try {
            ExchangeRate record = listER.get(row);

            switch (column) {
                case 0: //Date
                    return record.getCreatedDate();
                case 1: //From Currency
                    return record.getFromCurr();
                case 2: //To Currency
                    return record.getToCurr();
                case 3: //Exchange Rate;
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
        if (listER == null) {
            return;
        }

        if (listER.isEmpty()) {
            return;
        }

        try {
            ExchangeRate record = listER.get(row);

            switch (column) {
                case 0: //Date
                    break;
                case 1: //From Currency
                    break;
                case 2: //To Currency
                    if (value == null) {
                        record.setToCurr(null);
                    } else {
                        record.setToCurr(value.toString());
                    }
                    break;
                case 3: //Exchange Rate;
                    if (value == null) {

                    } else {
                        Double oldExRate = record.getExRate();
                        record.setExRate(NumberUtil.NZero(value));
                        logExchangeRate(record, oldExRate);
                    }
                    break;
            }
            
            fireTableCellUpdated(row, 0);
            fireTableCellUpdated(row, column);
            addNewRecord();
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    @Override
    public int getRowCount() {
        if (listER == null) {
            return 0;
        }
        return listER.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    private void logExchangeRate(ExchangeRate er, Double oldRate) {
        try {
            if (er.getExrId() != null) {
                ExchangeRateLog erl = new ExchangeRateLog();
                erl.setExrId(er.getExrId());
                erl.setLogDate(new Date());
                erl.setNewRate(er.getExRate());
                erl.setOldRate(oldRate);
                erl.setUserId(Global.loginUser.getUserId());
                dao.save(erl);
                
                er.setUpdatedBy(Global.loginUser.getUserId());
                er.setUpdatedDate(new Date());
            }else{
                er.setCreatedBy(Global.loginUser.getUserId());
                er.setCreatedDate(new Date());
            }
            
            dao.save(er);
        } catch (Exception ex) {
            log.error("logExchangeRate : " + ex.getMessage());
        } finally {
            dao.close();
        }
    }

    public List<ExchangeRate> getListER() {
        return listER;
    }

    public void setListER(List<ExchangeRate> listER) {
        this.listER = listER;
        fireTableDataChanged();
    }

    public void addNewRecord() {
        if (!hasEmptyRecord()) {
            ExchangeRate record = new ExchangeRate();
            record.setFromCurr(homeCurrency);
            listER.add(record);
            fireTableRowsInserted(listER.size() - 1, listER.size() - 1);
            parent.scrollRectToVisible(parent.getCellRect(parent.getRowCount() - 1, 0, true));
        }
    }

    private boolean hasEmptyRecord() {
        if (listER == null) {
            return false;
        }
        if (listER.isEmpty()) {
            return false;
        }

        ExchangeRate record = listER.get(listER.size() - 1);
        return record.getExrId() == null;
    }

    public String getHomeCurrency() {
        return homeCurrency;
    }

    public void setHomeCurrency(String homeCurrency) {
        this.homeCurrency = homeCurrency;
    }

    public JTable getParent() {
        return parent;
    }

    public void setParent(JTable parent) {
        this.parent = parent;
    }
}
