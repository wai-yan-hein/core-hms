/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.tempentity.VouCodeFilter;
import com.cv.app.pharmacy.database.tempentity.VouCodeFilterKey;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class CodeTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(CodeTableModel.class.getName());
    private List<VouCodeFilter> listVouCodeFilter = new ArrayList();
    private final String[] columnNames = {"Code", "Item Name"};
    private final AbstractDataAccess dao;
    private boolean insStatus = true; //Insert to the table
    private final String modelOption;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");
    
    public CodeTableModel(AbstractDataAccess dao, boolean insStatus, String modelOption) {
        this.dao = dao;
        this.insStatus = insStatus;
        this.modelOption = modelOption;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0;
    }

    @Override
    public Class getColumnClass(int column) {
        return String.class;
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listVouCodeFilter == null) {
            return null;
        }

        if (listVouCodeFilter.isEmpty()) {
            return null;
        }

        try {
            VouCodeFilter record = listVouCodeFilter.get(row);

            switch (column) {
                case 0: //Code
                    if (codeUsage.equals("SHORTNAME")) {
                        if (record.getKey() == null) {
                            return null;
                        } else {
                            return record.getKey().getItemCode().getShortName();
                        }
                    } else {
                        if (record.getKey() == null) {
                            return null;
                        } else {
                            return record.getKey().getItemCode().getMedId();
                        }
                    }
                case 1: //Desp
                    if (record.getKey() == null) {
                        return null;
                    } else {
                        return record.getKey().getItemCode().getMedName();
                    }
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

        switch (column) {
            case 0: //Code
                if (value != null) {
                    getMedInfo(value.toString(), row);
                }
                break;
            case 1: //Desp
                //record.setMedName(value.toString());
                break;
            default:
                System.out.println("invalid index");
        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public int getRowCount() {
        /*if (listVouCodeFilter == null) {
            return 0;
        }*/
        return listVouCodeFilter.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public boolean hasEmptyRow() {
        if (listVouCodeFilter == null) {
            return false;
        }
        if (listVouCodeFilter.isEmpty()) {
            return false;
        }

        VouCodeFilter record = listVouCodeFilter.get(listVouCodeFilter.size() - 1);

        return record.getKey() == null;
    }

    public void addEmptyRow() {
        if (listVouCodeFilter != null) {
            VouCodeFilter record = new VouCodeFilter();
            listVouCodeFilter.add(record);
            fireTableRowsInserted(listVouCodeFilter.size() - 1, listVouCodeFilter.size() - 1);
        }
    }

    public void delete(int row) {
        if (listVouCodeFilter != null) {
            if (!listVouCodeFilter.isEmpty()) {
                VouCodeFilter record = listVouCodeFilter.get(row);

                if (record.getKey().getItemCode() != null) {
                    try {
                        dao.delete(record);
                        listVouCodeFilter.remove(row);
                        if (!hasEmptyRow()) {
                            addEmptyRow();
                        }

                        fireTableRowsDeleted(row, row);
                    } catch (Exception ex) {
                        log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
                    } finally {
                        dao.close();
                    }
                }
            }
        }
    }

    public void setItemCodeFilter(VouCodeFilter record, int pos) {
        if (listVouCodeFilter != null) {
            if (!listVouCodeFilter.isEmpty()) {
                listVouCodeFilter.set(pos, record);
                fireTableDataChanged();

                if (!hasEmptyRow()) {
                    addEmptyRow();
                }
            }
        }
    }

    public void getMedInfo(String medCode, int row) {
        String userId = Global.machineId;
        final String TABLE = "com.cv.app.pharmacy.database.tempentity.VouCodeFilter";
        String strSQL = "SELECT * FROM " + TABLE
                + " WHERE key.itemCode.medId = '" + medCode + "'";

        try {
            if (!JoSQLUtil.isAlreadyHave(strSQL, listVouCodeFilter)) {
                dao.open();
                Medicine medicine = (Medicine) dao.find("Medicine", "medId = '"
                        + medCode + "' and active = true");
                dao.close();

                if (medicine != null) {
                    VouCodeFilter icf = new VouCodeFilter(
                            new VouCodeFilterKey(modelOption, userId, medicine));

                    if (insStatus) {
                        dao.save(icf);
                    }

                    setItemCodeFilter(icf, row);
                } else {
                    if (!medCode.isEmpty()) {
                        JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                                "Invalid.", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate medicine code.",
                        "Duplicate", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            log.error("getMedInfo : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.toString());
        }
    }

    public List<VouCodeFilter> getListCodeFilter() {
        return listVouCodeFilter;
    }

    public void setListCodeFilter(List<VouCodeFilter> listVouCodeFilter) {
        this.listVouCodeFilter = listVouCodeFilter;
        fireTableDataChanged();
    }

    public String getFilterCodeStr() {
        String strTmp = null;

        if (listVouCodeFilter != null) {
            for (VouCodeFilter code : listVouCodeFilter) {
                if (code.getKey() != null) {
                    if (code.getKey().getItemCode() != null) {
                        if (strTmp == null) {
                            strTmp = "'" + code.getKey().getItemCode().getMedId() + "'";
                        } else {
                            strTmp = strTmp + ",'" + code.getKey().getItemCode().getMedId() + "'";
                        }
                    }
                }
            }
        }

        return strTmp;
    }
}
