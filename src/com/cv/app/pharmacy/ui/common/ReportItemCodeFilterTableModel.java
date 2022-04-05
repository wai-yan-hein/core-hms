/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cv.app.pharmacy.ui.common;

import com.cv.app.common.Global;
import com.cv.app.pharmacy.database.controller.AbstractDataAccess;
import com.cv.app.pharmacy.database.entity.ItemUnit;
import com.cv.app.pharmacy.database.entity.Medicine;
import com.cv.app.pharmacy.database.tempentity.ItemCodeFilter;
import com.cv.app.pharmacy.database.tempentity.ItemCodeFilterKey;
import com.cv.app.pharmacy.database.tempentity.ItemCodeFilterRpt;
import com.cv.app.pharmacy.util.MedicineUP;
import com.cv.app.util.JoSQLUtil;
import com.cv.app.util.NumberUtil;
import com.cv.app.util.Util1;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.apache.log4j.Logger;

/**
 *
 * @author WSwe
 */
public class ReportItemCodeFilterTableModel extends AbstractTableModel {

    static Logger log = Logger.getLogger(ReportItemCodeFilterTableModel.class.getName());
    private List<ItemCodeFilterRpt> listCodeFilter = new ArrayList();
    private final String[] columnNames = {"Code", "Item Name", "Copy", "Unit"};
    private final AbstractDataAccess dao;
    private boolean insStatus = true; //Insert to the table
    private JLabel lblTotal;
    private final MedicineUP medUp;
    private final String codeUsage = Util1.getPropValue("system.item.code.field");

    public ReportItemCodeFilterTableModel(AbstractDataAccess dao, boolean insStatus,
            MedicineUP medUp) {
        this.dao = dao;
        this.insStatus = insStatus;
        this.medUp = medUp;
    }

    public void setLabel(JLabel lblTotal) {
        this.lblTotal = lblTotal;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0 || column == 2 || column == 3;
    }

    @Override
    public Class getColumnClass(int column) {
        switch (column) {
            case 0:
            case 1:
                return String.class;
            case 2:
                return Integer.class;
            case 3:
                return String.class;
            default:
                return null;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (listCodeFilter == null) {
            return null;
        }

        if (listCodeFilter.isEmpty()) {
            return null;
        }

        try {
            ItemCodeFilterRpt record = listCodeFilter.get(row);

            switch (column) {
                case 0: //Code
                    if (codeUsage.equals("SHORTNAME")) {
                        if (record.getItem() != null) {
                            return record.getItem().getShortName();
                        } else {
                            return null;
                        }
                    } else {
                        if (record.getItem() != null) {
                            return record.getItem().getMedId();
                        } else {
                            return null;
                        }
                    }
                case 1: //Desp
                    if (record.getItem() != null) {
                        return record.getItem().getMedName();
                    } else {
                        return null;
                    }
                case 2: //Copy
                    return record.getNoOfCopy();
                case 3: //Unit
                    if (record.getUnit() != null) {
                        return record.getUnit().getItemUnitCode();
                    } else {
                        return null;
                    }
                default:
                    return new Object();
            }
        } catch (Exception ex) {
            log.error("getValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }

        return null;
    }

    private void calculateTotal() {
        if (lblTotal != null) {
            Long total = new Long(0);

            if (listCodeFilter != null) {
                for (ItemCodeFilterRpt icf : listCodeFilter) {
                    if (icf.getItem() != null) {
                        total += NumberUtil.NZeroInt(icf.getNoOfCopy());
                    }
                }
            }

            lblTotal.setText(total.toString());
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (listCodeFilter == null) {
            return;
        }

        if (listCodeFilter.isEmpty()) {
            return;
        }

        try {
            ItemCodeFilterRpt record = listCodeFilter.get(row);

            switch (column) {
                case 0: //Code
                    if (value != null) {
                        getMedInfo(value.toString(), record);
                    }
                    break;
                case 1: //Desp
                    //record.setMedName(value.toString());
                    break;
                case 2: //Copy
                    record.setNoOfCopy(NumberUtil.NZeroInt(value));
                    break;
                case 3: //Unit
                    if (value != null) {
                        record.setUnit((ItemUnit) value);
                    } else {
                        record.setUnit(null);
                    }
                    break;
                default:
                    System.out.println("invalid index");
            }
            dao.save(record);
        } catch (Exception ex) {
            log.error("setValueAt : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        } finally {
            dao.close();
        }
        fireTableCellUpdated(row, column);
        calculateTotal();
    }

    @Override
    public int getRowCount() {
        if (listCodeFilter == null) {
            return 0;
        }
        return listCodeFilter.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public boolean hasEmptyRow() {
        if (listCodeFilter == null) {
            return false;
        }
        if (listCodeFilter.isEmpty()) {
            return false;
        }

        ItemCodeFilterRpt record = listCodeFilter.get(listCodeFilter.size() - 1);
        return record.getItem() == null;
    }

    public void addEmptyRow() {
        if (listCodeFilter != null) {
            ItemCodeFilterRpt record = new ItemCodeFilterRpt();
            listCodeFilter.add(record);
            fireTableRowsInserted(listCodeFilter.size() - 1, listCodeFilter.size() - 1);
        }
    }

    public void delete(int row) {
        if (listCodeFilter == null) {
            return;
        }
        if (listCodeFilter.isEmpty()) {
            return;
        }

        ItemCodeFilterRpt record = listCodeFilter.get(row);

        if (record.getItem() != null) {
            try {
                dao.delete(record);
                listCodeFilter.remove(row);
                if (!hasEmptyRow()) {
                    addEmptyRow();
                }

                fireTableRowsDeleted(row, row);
            } catch (Exception ex) {
                log.error("delete : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
            }
        }
    }

    public void setItemCodeFilter(ItemCodeFilterRpt record, int pos) {
        if (listCodeFilter == null) {
            return;
        }
        if (listCodeFilter.isEmpty()) {
            return;
        }

        listCodeFilter.set(pos, record);
        //fireTableDataChanged();

        if (!hasEmptyRow()) {
            addEmptyRow();
        }
    }

    public void getMedInfo(String medCode, ItemCodeFilterRpt record) {
        String userId = Global.loginUser.getUserId();
        /*final String TABLE = "com.cv.app.pharmacy.database.tempentity.ItemCodeFilterRpt";
        String strSQL = "SELECT * FROM " + TABLE
                + " WHERE item.medId = '" + medCode + "'";*/

        try {
            //if (!JoSQLUtil.isAlreadyHave(strSQL, listCodeFilter)) {
            dao.open();
            Medicine medicine = (Medicine) dao.find("Medicine", "medId = '"
                    + medCode + "' and active = true");
            dao.close();

            if (medicine != null) {
                medUp.add(medicine);
                medicine.setRelationGroupId(medUp.getRelation(medicine.getMedId()));
                //ItemCodeFilterRpt icf = new ItemCodeFilterRpt();
                record.setItem(medicine);
                record.setUserId(userId);

                if (!hasEmptyRow()) {
                    addEmptyRow();
                }
                /*if (insStatus) {
                        dao.save(icf);
                    }*/

                //setItemCodeFilter(icf, row);
            } else {
                if (!medCode.isEmpty()) {
                    JOptionPane.showMessageDialog(Util1.getParent(), "Invalid medicine code.",
                            "Invalid.", JOptionPane.ERROR_MESSAGE);
                }
            }
            /*} else {
                JOptionPane.showMessageDialog(Util1.getParent(), "Duplicate medicine code.",
                        "Duplicate", JOptionPane.ERROR_MESSAGE);
            }*/
        } catch (Exception ex) {
            log.error("getMedInfo : " + ex.getStackTrace()[0].getLineNumber() + " - " + ex.getMessage());
        }
    }

    public List<ItemCodeFilterRpt> getListCodeFilter() {
        return listCodeFilter;
    }

    public void setListCodeFilter(List<ItemCodeFilterRpt> listCodeFilter) {
        this.listCodeFilter = listCodeFilter;
        fireTableDataChanged();
    }

    public String getFilterCodeStr() {
        String strTmp = null;

        if (listCodeFilter != null) {
            for (ItemCodeFilterRpt code : listCodeFilter) {
                if (code.getItem() != null) {
                    if (strTmp == null) {
                        strTmp = "'" + code.getItem().getMedId() + "'";
                    } else {
                        strTmp = strTmp + ",'" + code.getItem().getMedId() + "'";
                    }
                }
            }
        }

        return strTmp;
    }

    public ItemCodeFilterRpt getCodeFilter(int row) {
        ItemCodeFilterRpt icf = null;

        if (listCodeFilter != null) {
            if (!listCodeFilter.isEmpty()) {
                if (row < listCodeFilter.size()) {
                    icf = listCodeFilter.get(row);
                }
            }
        }
        return icf;
    }

    public void setCostList(String[] codes) {
        if (codes != null) {
            if (codes.length > 0) {
                String userId = Global.loginUser.getUserId();
                List<ItemCodeFilterRpt> listCode = new ArrayList();
                String prvCode = "-";
                try {
                    for (String code : codes) {
                        if (!prvCode.equals(code)) {
                            ItemCodeFilterRpt record = new ItemCodeFilterRpt();
                            Medicine medicine = (Medicine) dao.find("Medicine", "medId = '"
                                    + code + "'");
                            record.setItem(medicine);
                            record.setNoOfCopy(1);
                            record.setUserId(userId);
                            dao.save(record);
                            listCode.add(record);
                            prvCode = code;
                        }
                    }
                } catch (Exception ex) {
                    log.error("setCostList : " + ex.toString());
                } finally {
                    dao.close();
                }
                listCodeFilter = listCode;
                if (!hasEmptyRow()) {
                    addEmptyRow();
                }

                fireTableDataChanged();
            }
        }
    }
}
