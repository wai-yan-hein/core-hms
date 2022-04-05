/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ExpenseTypeAcc;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ExpenseTypeAccountSetupTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ExpenseTypeAccountSetupTableModel.class.getName());
    private List<ExpenseTypeAcc> listAS = new ArrayList();
    private final String[] columnNames = {"Source Acc", "Account", "Dept", "Payable Acc Opt", 
        "Remark", "Exp Acc"};
    private final AbstractDataAccess dao = Global.dao; // Data access object.  
    private Integer expTypeId = -1;
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0: //Source Acc
                return String.class;
            case 1: //Account
                return String.class;
            case 2: //Dept
                return String.class;
            case 3: //Use For
                return String.class;
            case 4: //Remark
                return String.class;
            case 5: //Exp Acc
                return String.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listAS == null) {
            return null;
        }

        if (listAS.isEmpty()) {
            return null;
        }

        try {
            ExpenseTypeAcc record = listAS.get(row);

            switch (column) {
                case 0: //Source Acc
                    return record.getSourceAccId();
                case 1: //Account
                    return record.getAccId();
                case 2: //Dept
                    return record.getDepdCode();
                case 3: //Use For
                    return record.getUseFor();
                case 4: //Remark
                    return record.getRemark();
                case 5: //Exp Acc
                    return record.getUnPaidAcc();
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
        if (listAS == null) {
            return;
        }

        if (listAS.isEmpty()) {
            return;
        }

        try {
            ExpenseTypeAcc record = listAS.get(row);
            switch (column) {
                case 0: //Source Acc
                    if (value == null) {
                        record.setSourceAccId(null);
                    } else {
                        record.setSourceAccId(value.toString());
                    }
                    break;
                case 1: //Account
                    if (value == null) {
                        record.setAccId(null);
                    } else {
                        record.setAccId(value.toString());
                    }
                    break;
                case 2: //Dept
                    if (value == null) {
                        record.setDepdCode(null);
                    } else {
                        record.setDepdCode(value.toString());
                    }
                    break;
                case 3: //Use For
                    if (value == null) {
                        record.setUseFor(null);
                    } else {
                        record.setUseFor(value.toString());
                    }
                    break;
                case 4: //Remark
                    if (value == null) {
                        record.setRemark(null);
                    } else {
                        record.setRemark(value.toString());
                    }
                    break;
                case 5: //Exp Acc
                    if(value == null){
                        record.setUnPaidAcc(null);
                    }else{
                        record.setUnPaidAcc(value.toString());
                    }
                    break;
            }
            save(record);
            fireTableCellUpdated(row, column);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
        
        addEmptyRow();
    }

    @Override
    public int getRowCount() {
        if (listAS == null) {
            return 0;
        }
        return listAS.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public List<ExpenseTypeAcc> getListAS() {
        return listAS;
    }

    public void setListAS(List<ExpenseTypeAcc> listAS) {
        this.listAS = listAS;
        fireTableDataChanged();
        addEmptyRow();
    }

    public void addEmptyRow() {
        if (listAS == null) {
            listAS = new ArrayList();
            listAS.add(new ExpenseTypeAcc());
        } else if (listAS.isEmpty()) {
            listAS.add(new ExpenseTypeAcc());
        } else {
            ExpenseTypeAcc eta = listAS.get(listAS.size() - 1);
            if (eta.getId() != null) {
                listAS.add(new ExpenseTypeAcc());
            }
        }

        try {
            fireTableRowsInserted(listAS.size() - 1, listAS.size() - 1);
        } catch (Exception ex) {

        }
    }

    public ExpenseTypeAcc getSelected(int row) {
        if (listAS != null) {
            if (!listAS.isEmpty()) {
                return listAS.get(row);
            }
        }
        return null;
    }

    public void delete(int row) {
        if (listAS != null) {
            if (!listAS.isEmpty()) {
                try {
                    ExpenseTypeAcc record = listAS.get(row);
                    if (record.getId() != null) {
                        dao.delete(record);
                    }
                    listAS.remove(row);
                    fireTableRowsDeleted(0, listAS.size());
                } catch (Exception ex) {
                    log.error("delete : " + ex.toString());
                } finally {
                    dao.close();
                }
            }
        }
    }

    private void save(ExpenseTypeAcc record) {
        try {
            if (record.getSourceAccId() != null || record.getAccId() != null
                    || record.getDepdCode() != null) {
                record.setExpTypeId(expTypeId);
                dao.save(record);
            }
        } catch (Exception ex) {
            log.error("save : " + ex.toString());
        } finally {
            dao.close();
        }
    }

    public Integer getExpTypeId() {
        return expTypeId;
    }

    public void setExpTypeId(Integer expTypeId) {
        this.expTypeId = expTypeId;
    }
    
    public void clear(){
        listAS = new ArrayList();
        fireTableDataChanged();
        System.gc();
    }
}
